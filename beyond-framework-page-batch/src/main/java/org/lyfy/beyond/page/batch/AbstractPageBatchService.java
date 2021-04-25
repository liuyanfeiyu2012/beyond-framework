package org.lyfy.beyond.page.batch;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : JOSE 2018/9/16 下午1:36
 */
@Slf4j
public abstract class AbstractPageBatchService<T> implements PageBatchService<T> {

    @Override
    public PageInfo<T> selectPageData(int pageNum, int pageSize, BasePage basePage) {
        PageHelper.startPage(pageNum, pageSize);
        return selectAll(basePage);
    }

    @Override
    public Boolean doBatchService(int pageStart, int pageSize, int totalCount, BasePage basePage) {
        int pageCount = (totalCount % pageSize) != 0 ? (totalCount / pageSize) + 1 : (totalCount / pageSize);

        PageInfo<T> page = selectPageData(pageStart, pageSize, basePage);
        log.info("[PAGE-BATCH-{}]:pageNum:{}, pageSize:{}, totalData:{}", getJobType(), pageStart, pageSize, page.getTotal());

        Boolean hasNextPage = page.isHasNextPage();
        doService(page.getList());

        // 处理后面数据
        for (int i = pageStart + 1; i <= pageCount; i++) {
            PageInfo<T> tmpPage = selectPageData(i, pageSize, basePage);
            hasNextPage = tmpPage.isHasNextPage();
            log.info("[PAGE-BATCH-{}]:pageNum:{}, pageSize:{}, totalData:{}", getJobType(), i, pageSize, tmpPage.getTotal());
            doService(tmpPage.getList());
        }

        return hasNextPage;
    }

    protected abstract String getJobType();
}
