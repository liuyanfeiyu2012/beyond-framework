package org.lyfy.beyond.mapper.ext.example;

import tk.mybatis.mapper.util.Sqls;
import tk.mybatis.mapper.util.StringUtil;
import tk.mybatis.mapper.weekend.Fn;
import tk.mybatis.mapper.weekend.reflection.Reflections;

import java.util.Collection;
import java.util.Map;

/**
 * @author: 谢星星
 * @Date: 2019/8/1 20:01
 * @Description: 扩展WeekendSqls，如果传入的value是空或者null，则直接不加入过滤条件
 */
public class XWeekendSqls<T> implements tk.mybatis.mapper.entity.SqlsCriteria {

    private Sqls.Criteria criteria;

    private static final String LIKE_NULL = "null";

    private XWeekendSqls() {
        this.criteria = new Sqls.Criteria();
    }

    public static <T> XWeekendSqls<T> custom() {
        return new XWeekendSqls<T>();
    }

    public XWeekendSqls<T> andIsNull(String property) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, "is null", "and"));
        return this;
    }

    public XWeekendSqls<T> andIsNull(Fn<T, Object> fn) {
        return this.andIsNull(Reflections.fnToFieldName(fn));
    }

    public XWeekendSqls<T> andIsNotNull(String property) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, "is not null", "and"));
        return this;
    }

    public XWeekendSqls<T> andIsNotNull(Fn<T, Object> fn) {
        return this.andIsNotNull(Reflections.fnToFieldName(fn));
    }

    public XWeekendSqls<T> andEqualTo(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "=", "and"));
        return this;
    }

    public XWeekendSqls<T> andEqualTo(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.andEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> andNotEqualTo(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<>", "and"));
        return this;
    }

    public XWeekendSqls<T> andNotEqualTo(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.andNotEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> andGreaterThan(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, ">", "and"));
        return this;
    }

    public XWeekendSqls<T> andGreaterThan(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.andGreaterThan(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> andGreaterThanOrEqualTo(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, ">=", "and"));
        return this;
    }

    public XWeekendSqls<T> andGreaterThanOrEqualTo(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.andGreaterThanOrEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> andLessThan(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<", "and"));
        return this;
    }

    public XWeekendSqls<T> andLessThan(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.andLessThan(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> andLessThanOrEqualTo(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<=", "and"));
        return this;
    }

    public XWeekendSqls<T> andLessThanOrEqualTo(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.andLessThanOrEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> andIn(String property, Iterable values) {
        if (isEmpty(values)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, values, "in", "and"));
        return this;
    }

    public XWeekendSqls<T> andIn(Fn<T, Object> fn, Iterable values) {
        if (isEmpty(values)) {
            return this;
        }
        return this.andIn(Reflections.fnToFieldName(fn), values);
    }

    public XWeekendSqls<T> andNotIn(String property, Iterable values) {
        if (isEmpty(values)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, values, "not in", "and"));
        return this;
    }

    public XWeekendSqls<T> andNotIn(Fn<T, Object> fn, Iterable values) {
        if (isEmpty(values)) {
            return this;
        }
        return this.andNotIn(Reflections.fnToFieldName(fn), values);
    }

    public XWeekendSqls<T> andBetween(String property, Object value1, Object value2) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value1, value2, "between", "and"));
        return this;
    }

    public XWeekendSqls<T> andBetween(Fn<T, Object> fn, Object value1, Object value2) {
        return this.andBetween(Reflections.fnToFieldName(fn), value1, value2);
    }

    public XWeekendSqls<T> andNotBetween(String property, Object value1, Object value2) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value1, value2, "not between", "and"));
        return this;
    }

    public XWeekendSqls<T> andNotBetween(Fn<T, Object> fn, Object value1, Object value2) {
        return this.andNotBetween(Reflections.fnToFieldName(fn), value1, value2);
    }

    public XWeekendSqls<T> andLike(String property, String value) {
        String tmpValue = value.replace("%", "");
        if (LIKE_NULL.equals(tmpValue) || isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "like", "and"));
        return this;
    }

    public XWeekendSqls<T> andLike(Fn<T, Object> fn, String value) {
        String tmpValue = value.replace("%", "");
        if (LIKE_NULL.equals(tmpValue) || isEmpty(value)) {
            return this;
        }
        return this.andLike(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> andNotLike(String property, String value) {
        String tmpValue = value.replace("%", "");
        if (LIKE_NULL.equals(tmpValue) || isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "not like", "and"));
        return this;
    }

    public XWeekendSqls<T> andNotLike(Fn<T, Object> fn, String value) {
        String tmpValue = value.replace("%", "");
        if (LIKE_NULL.equals(tmpValue) || isEmpty(value)) {
            return this;
        }
        return this.andNotLike(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> orIsNull(String property) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, "is null", "or"));
        return this;
    }

    public XWeekendSqls<T> orIsNull(Fn<T, Object> fn) {
        return this.orIsNull(Reflections.fnToFieldName(fn));
    }

    public XWeekendSqls<T> orIsNotNull(String property) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, "is not null", "or"));
        return this;
    }

    public XWeekendSqls<T> orIsNotNull(Fn<T, Object> fn) {
        return this.orIsNotNull(Reflections.fnToFieldName(fn));
    }

    public XWeekendSqls<T> orEqualTo(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "=", "or"));
        return this;
    }

    public XWeekendSqls<T> orEqualTo(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.orEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> orNotEqualTo(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<>", "or"));
        return this;
    }

    public XWeekendSqls<T> orNotEqualTo(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.orNotEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> orGreaterThan(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, ">", "or"));
        return this;
    }

    public XWeekendSqls<T> orGreaterThan(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.orGreaterThan(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> orGreaterThanOrEqualTo(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, ">=", "or"));
        return this;
    }

    public XWeekendSqls<T> orGreaterThanOrEqualTo(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.orGreaterThanOrEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> orLessThan(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<", "or"));
        return this;
    }

    public XWeekendSqls<T> orLessThan(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.orLessThan(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> orLessThanOrEqualTo(String property, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<=", "or"));
        return this;
    }

    public XWeekendSqls<T> orLessThanOrEqualTo(Fn<T, Object> fn, Object value) {
        if (isEmpty(value)) {
            return this;
        }
        return this.orLessThanOrEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> orIn(String property, Iterable values) {
        if (isEmpty(values)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, values, "in", "or"));
        return this;
    }

    public XWeekendSqls<T> orIn(Fn<T, Object> fn, Iterable values) {
        if (isEmpty(values)) {
            return this;
        }
        return this.orIn(Reflections.fnToFieldName(fn), values);
    }

    public XWeekendSqls<T> orNotIn(String property, Iterable values) {
        if (isEmpty(values)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, values, "not in", "or"));
        return this;
    }

    public XWeekendSqls<T> orNotIn(Fn<T, Object> fn, Iterable values) {
        if (isEmpty(values)) {
            return this;
        }
        return this.orNotIn(Reflections.fnToFieldName(fn), values);
    }

    public XWeekendSqls<T> orBetween(String property, Object value1, Object value2) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value1, value2, "between", "or"));
        return this;
    }

    public XWeekendSqls<T> orBetween(Fn<T, Object> fn, Object value1, Object value2) {
        return this.orBetween(Reflections.fnToFieldName(fn), value1, value2);
    }

    public XWeekendSqls<T> orNotBetween(String property, Object value1, Object value2) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value1, value2, "not between", "or"));
        return this;
    }

    public XWeekendSqls<T> orNotBetween(Fn<T, Object> fn, Object value1, Object value2) {
        return this.orNotBetween(Reflections.fnToFieldName(fn), value1, value2);
    }

    public XWeekendSqls<T> orLike(String property, String value) {
        String tmpValue = value.replace("%", "");
        if (LIKE_NULL.equals(tmpValue) || isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "like", "or"));
        return this;
    }

    public XWeekendSqls<T> orLike(Fn<T, Object> fn, String value) {
        String tmpValue = value.replace("%", "");
        if (LIKE_NULL.equals(tmpValue) || isEmpty(value)) {
            return this;
        }
        return this.orLike(Reflections.fnToFieldName(fn), value);
    }

    public XWeekendSqls<T> orNotLike(String property, String value) {
        String tmpValue = value.replace("%", "");
        if (LIKE_NULL.equals(tmpValue) || isEmpty(value)) {
            return this;
        }
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "not like", "or"));
        return this;
    }

    public XWeekendSqls<T> orNotLike(Fn<T, Object> fn, String value) {
        String tmpValue = value.replace("%", "");
        if (LIKE_NULL.equals(tmpValue) || isEmpty(value)) {
            return this;
        }
        return this.orNotLike(Reflections.fnToFieldName(fn), value);
    }

    @Override
    public Sqls.Criteria getCriteria() {
        return criteria;
    }

    /**
     * 判断是否为null对象，以及如果是字符串类型则继续判断是否为空字符串
     */
    private static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;

        } else if (obj instanceof String && StringUtil.isEmpty((String) obj)) {
            return true;

        } else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;

        } else if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;

        } else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
            return true;
        }
        return false;
    }
}
