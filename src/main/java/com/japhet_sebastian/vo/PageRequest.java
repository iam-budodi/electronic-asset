package com.japhet_sebastian.vo;

import jakarta.ws.rs.DefaultValue;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.Objects;

public class PageRequest {

    @RestQuery("page")
    @DefaultValue("0")
    @Parameter(description = "Page index", required = false)
    private Integer pageNum;

    @RestQuery("size")
    @DefaultValue("10")
    @Parameter(description = "Page size", required = false)
    private Integer pageSize;

    @RestQuery("search")
    @Parameter(description = "Search query parameter", required = false)
    private String search;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageRequest that)) return false;
        return Objects.equals(getPageNum(), that.getPageNum())
                && Objects.equals(getPageSize(), that.getPageSize())
                && Objects.equals(getSearch(), that.getSearch());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPageNum(), getPageSize(), getSearch());
    }
}
