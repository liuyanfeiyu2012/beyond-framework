package org.lyfy.beyond.encrypt.plugin.interceptor;

import org.lyfy.beyond.encrypt.plugin.core.AggregatedSchemaEncryptRepo;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Properties;

@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = Statement.class)
})
public class EncryptResultSetInterceptor implements Interceptor {

    private ApplicationContext applicationContext;

    public EncryptResultSetInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object object = invocation.getTarget();
        Object[] args = invocation.getArgs();
        DefaultResultSetHandler resultSetHandler = (DefaultResultSetHandler) object;
        MappedStatement mappedStatement = (MappedStatement) getField(resultSetHandler, "mappedStatement");
        Executor executor = (Executor) getField(resultSetHandler, "executor");
        ParameterHandler parameterHandler = (ParameterHandler) getField(resultSetHandler, "parameterHandler");
        ResultHandler<?> resultHandler = (ResultHandler<?>) getField(resultSetHandler, "resultHandler");
        BoundSql boundSql = (BoundSql) getField(resultSetHandler, "boundSql");
        RowBounds rowBounds = (RowBounds) getField(resultSetHandler, "rowBounds");
        AggregatedSchemaEncryptRepo repo = new AggregatedSchemaEncryptRepo();
        try {
            repo = applicationContext.getBean(AggregatedSchemaEncryptRepo.class);
        } catch (Exception e) {
            //ignore
        }
        EncryptResultSetHandler encryptResultSetHandler = new EncryptResultSetHandler(repo, executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        return encryptResultSetHandler.handleResultSets((Statement) args[0]);
    }

    private Object getField(DefaultResultSetHandler resultSetHandler, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = resultSetHandler.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(resultSetHandler);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
