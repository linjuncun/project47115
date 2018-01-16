package com.hyperfit.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 分页实体对象</p>
 */
public class PageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private int pageIndex = 1;                        //当前页
    private int pageSize = 20;                //当前页大小,默认查询20条
    private long totalPage;                            //总页数
    private long recordsTotal;                        //总数据条数
    private int draw = 1;                            //datatable传参所需
    private long recordsFiltered;                    //过滤总数据
    private List<?> results;                        //返回结果集
    private Map<String, Object> map = new HashMap<>();//查询条件

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public List<?> getResults() {
        return results;
    }

    public void setResults(List<?> results) {
        this.results = results;
    }
}
