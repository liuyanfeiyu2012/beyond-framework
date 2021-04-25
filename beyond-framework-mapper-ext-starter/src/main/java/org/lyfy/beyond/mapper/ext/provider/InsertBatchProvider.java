package org.lyfy.beyond.mapper.ext.provider;

import com.google.common.collect.Sets;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

import static tk.mybatis.mapper.mapperhelper.SqlHelper.getDynamicTableName;
import static tk.mybatis.mapper.mapperhelper.SqlHelper.insertIntoTable;

public class InsertBatchProvider extends MapperTemplate {

    private static final Set<String> EXCLUDED_FIELD_SET = Sets.newHashSet("id", "created", "creator", "modify_time");

    public InsertBatchProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String insertListIgnore(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(insertIgnoreIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, true, false, false));
        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isInsertable()) {
                sql.append(column.getColumnHolder("record") + ",");
            }
        }
        sql.append("</trim>");
        sql.append("</foreach>");
        return sql.toString();
    }

    public String insertOrUpdate(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, true, false, false));
        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isInsertable()) {
                sql.append(column.getColumnHolder("record") + ",");
            }
        }
        sql.append("</trim>");
        sql.append("</foreach>");
        sql.append("ON DUPLICATE KEY UPDATE");
        sql.append("<trim suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isInsertable() && !EXCLUDED_FIELD_SET.contains(column.getColumn())) {
                sql.append(column.getColumn()).append("=").append("VALUES(").append(column.getColumn()).append(")" + ",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    private static String insertIgnoreIntoTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT IGNORE INTO ");
        sql.append(getDynamicTableName(entityClass, defaultTableName));
        sql.append(" ");
        return sql.toString();
    }
}
