package com.hyperfit.util;

import com.github.pagehelper.Page;

import java.util.List;

/**
 * <p> 分页工具类</p>
 */
public class PageUtil {
    /**
     * 将返回值集合和page对象封装到PageEntity
     */
    public static void objectToPage(PageEntity pageEntity, List<?> list) {
        pageEntity.setResults(list);
        Page<?> page = (Page<?>) pageEntity.getResults();
        pageEntity.setRecordsTotal(page.getTotal());
        pageEntity.setRecordsFiltered(page.getTotal());
        long pageSize = pageEntity.getPageSize();
        long totalPage = page.getTotal() / pageSize;
        if (page.getTotal() % pageSize != 0) {
            totalPage += 1;
        }
        pageEntity.setTotalPage(totalPage);
        pageEntity.setMap(null);
    }

}
