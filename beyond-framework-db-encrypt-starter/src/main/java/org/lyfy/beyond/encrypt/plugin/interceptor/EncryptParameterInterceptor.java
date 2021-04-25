package org.lyfy.beyond.encrypt.plugin.interceptor;

import org.lyfy.beyond.encrypt.plugin.core.AggregatedSchemaEncryptRepo;
import org.lyfy.beyond.encrypt.plugin.core.FieldWrapperService;
import org.lyfy.beyond.encrypt.plugin.util.MSUtil;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({
        @Signature(type = ParameterHandler.class, method = "setParameters", args = PreparedStatement.class)})
public class EncryptParameterInterceptor implements Interceptor {

    private ApplicationContext applicationContext;

    public EncryptParameterInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof DefaultParameterHandler) {
            DefaultParameterHandler parameterHandler = (DefaultParameterHandler) invocation.getTarget();
            Field field = parameterHandler.getClass().getDeclaredField("mappedStatement");
            field.setAccessible(true);
            MappedStatement mappedStatement = (MappedStatement) field.get(parameterHandler);
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
                return invokeSetParameter(parameterHandler, invocation);
            }
        }
        return invocation.proceed();
    }

    private Object invokeSetParameter(DefaultParameterHandler parameterHandler, Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        PreparedStatement ps = (PreparedStatement) args[0];
        Field field = parameterHandler.getClass().getDeclaredField("mappedStatement");
        field.setAccessible(true);
        MappedStatement mappedStatement = (MappedStatement) field.get(parameterHandler);
        field = parameterHandler.getClass().getDeclaredField("parameterObject");
        field.setAccessible(true);
        Object parameterObject = field.get(parameterHandler);
        Class<?> clazz = getEntityClass(mappedStatement);
        String entityName = clazz.getSimpleName();
        field = parameterHandler.getClass().getDeclaredField("boundSql");
        field.setAccessible(true);
        BoundSql boundSql = (BoundSql) field.get(parameterHandler);
        Configuration configuration = mappedStatement.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        AggregatedSchemaEncryptRepo repo = new AggregatedSchemaEncryptRepo();
        try {
            repo = applicationContext.getBean(AggregatedSchemaEncryptRepo.class);
        } catch (Exception e) {
            //ignore
        }

        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) {
                        jdbcType = configuration.getJdbcTypeForNull();
                    }
                    if (value instanceof String && !StringUtils.isEmpty(value)) {
                        String[] finalPropertyNames = propertyName.split("\\.");
                        String finalPropertyName = finalPropertyNames[finalPropertyNames.length - 1];
                        String fullName = buildExcludedFiledName(entityName, finalPropertyName);
                        Map<String, FieldWrapperService> fieldWrapperServiceMap = repo.getFieldWrapperServiceMap();
                        if (fieldWrapperServiceMap.containsKey(fullName)) {
                            FieldWrapperService fieldWrapperService = fieldWrapperServiceMap.get(fullName);
                            value = fieldWrapperService.filedWrap((String) value);
                        }
                    }
                    try {
                        typeHandler.setParameter(ps, i + 1, value, jdbcType);
                    } catch (TypeException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    } catch (SQLException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }
                }
            }
        }
        return null;
    }

    private String buildExcludedFiledName(String entityName, String finalPropertyName) {
        return (entityName + "." + finalPropertyName).toUpperCase();
    }


    public Class<?> getEntityClass(MappedStatement ms) {
        String msId = ms.getId();
        Class<?> mapperClass = MSUtil.getMapperClass(msId);
        Type[] types = mapperClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                if (t.getRawType() == tk.mybatis.mapper.common.Mapper.class || tk.mybatis.mapper.common.Mapper.class.isAssignableFrom((Class<?>) t.getRawType())) {
                    Class<?> returnType = (Class<?>) t.getActualTypeArguments()[0];
                    return returnType;
                }
            }
        }
        throw new RuntimeException("无法获取 " + msId + " 方法的泛型信息!");
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
