package org.lyfy.beyond.page.batch;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author : Yuan.Pan 2020/4/8 3:28 PM
 */
public interface PageBatchService<T> {

    PageInfo<T> selectAll(BasePage basePage);

    PageInfo<T> selectPageData(int pageNum, int pageSize, BasePage basePage);

    Boolean doBatchService(int pageStart, int pageSize, int totalCount, BasePage basePage);

    void doService(List<T> list);
}
