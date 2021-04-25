package org.lyfy.beyond.mapper.ext;

import org.lyfy.beyond.mapper.ext.provider.InsertBatchProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@tk.mybatis.mapper.annotation.RegisterMapper
public interface InsertBatchMapper<T> {

    /**
     * 批量插入，支持批量插入的数据库可以使用，例如MySQL,H2等，另外该接口限制实体包含`id`属性并且必须为自增列
     * 部分失败将会忽略
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @InsertProvider(type = InsertBatchProvider.class, method = "dynamicSQL")
    int insertListIgnore(List<T> recordList);

    /**
     * 批量插入，支持批量插入的数据库可以使用，例如MySQL,H2等，另外该接口限制实体包含`id`属性并且必须为自增列
     * 存在则更新
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @InsertProvider(type = InsertBatchProvider.class, method = "dynamicSQL")
    int insertOrUpdate(List<T> recordList);
}
