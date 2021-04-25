package org.lyfy.beyond.mapper.ext.example;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.SqlsCriteria;
import tk.mybatis.mapper.util.Sqls;
import tk.mybatis.mapper.weekend.Fn;
import tk.mybatis.mapper.weekend.reflection.Reflections;

/**
 * @author: 谢星星
 * @Date: 2019/8/1 20:01
 * @Description: 扩展Example
 */
public class XExample extends Example {

    public XExample(Class<?> entityClass) {
        super(entityClass);
    }

    public XExample(Class<?> entityClass, boolean exists) {
        super(entityClass, exists);
    }

    public XExample(Class<?> entityClass, boolean exists, boolean notNull) {
        super(entityClass, exists, notNull);
    }

    public static <T> XBuilder<T> xBuilder(Class<T> clazz) {
        return new XBuilder<>(clazz);
    }

    public static class XBuilder<T extends Object> extends Builder {

        public XBuilder(Class<T> clazz) {
            super(clazz);
        }

        public XBuilder(Class<T> entityClass, boolean exists) {
            super(entityClass, exists);
        }

        public XBuilder(Class<T> entityClass, boolean exists, boolean notNull) {
            super(entityClass, exists, notNull);
        }

        @SafeVarargs
        public final XBuilder<T> select(Fn<T, Object>... fns) {
            super.select(buildProperties(fns));
            return this;
        }

        @SafeVarargs
        public final XBuilder<T> selectDistinct(Fn<T, Object>... fns) {
            super.selectDistinct(buildProperties(fns));
            return this;
        }

        @SafeVarargs
        public final XBuilder<T> notSelect(Fn<T, Object>... fns) {
            super.notSelect(buildProperties(fns));
            return this;
        }

        @SafeVarargs
        public final XBuilder<T> orderBy(Fn<T, Object>... fns) {
            super.orderBy(buildProperties(fns));
            return this;
        }

        @SafeVarargs
        public final XBuilder<T> orderByAsc(Fn<T, Object>... fns) {
            super.orderByAsc(buildProperties(fns));
            return this;
        }

        @SafeVarargs
        public final XBuilder<T> orderByDesc(Fn<T, Object>... fns) {
            super.orderByDesc(buildProperties(fns));
            return this;
        }

        @Override
        public XBuilder<T> distinct() {
            return setDistinct(true);
        }

        @Override
        public XBuilder<T> from(String tableName) {
            super.from(tableName);
            return this;
        }

        @Override
        public XBuilder<T> where(Sqls sqls) {
            super.where(sqls);
            return this;
        }

        @Override
        public XBuilder<T> where(SqlsCriteria sqls) {
            if(null == sqls || null == sqls.getCriteria()){
                return this;
            }
            super.where(sqls);
            return this;
        }

        @Override
        public XBuilder<T> andWhere(Sqls sqls) {
            super.andWhere(sqls);
            return this;
        }

        @Override
        public XBuilder<T> andWhere(SqlsCriteria sqls) {
            if(null == sqls || null == sqls.getCriteria()){
                return this;
            }
            super.andWhere(sqls);
            return this;
        }

        @Override
        public XBuilder<T> orWhere(Sqls sqls) {
            super.orWhere(sqls);
            return this;
        }

        @Override
        public XBuilder<T> orWhere(SqlsCriteria sqls) {
            if(null == sqls || null == sqls.getCriteria()){
                return this;
            }
            super.orWhere(sqls);
            return this;
        }

        @Override
        public XBuilder<T> setDistinct(boolean distinct) {
            super.setDistinct(distinct);
            return this;
        }

        @Override
        public XBuilder<T> setForUpdate(boolean forUpdate) {
            super.setForUpdate(forUpdate);
            return this;
        }

        @Override
        public XBuilder<T> setTableName(String tableName) {
            super.setTableName(tableName);
            return this;
        }

        private String[] buildProperties(Fn<T, Object>[] fns) {
            String[] properties = new String[fns.length];
            for (int i = 0; i < properties.length; i++) {
                properties[i] = Reflections.fnToFieldName(fns[i]);
            }
            return properties;
        }
    }

}
