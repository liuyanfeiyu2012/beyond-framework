package org.lyfy.beyond.mybatis.generator.plugins;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * @author: 谢星星
 * @Date: 2019/7/22 17:07
 * @Description: 将数据库中的tinyint类型映射为Java中的Integer类型
 */
public class TinyInt2IntegerJavaTypeResolverImpl extends JavaTypeResolverDefaultImpl {

    public TinyInt2IntegerJavaTypeResolverImpl() {
        super();
        super.typeMap.put(Types.TINYINT, new JdbcTypeInformation("TINYINT", new FullyQualifiedJavaType(Integer.class.getName())));
        super.typeMap.put(Types.BIT, new JdbcTypeInformation("BIT", new FullyQualifiedJavaType(Integer.class.getName())));
        super.typeMap.put(Types.SMALLINT, new JdbcTypeInformation("SMALLINT", new FullyQualifiedJavaType(Integer.class.getName())));
    }
}
