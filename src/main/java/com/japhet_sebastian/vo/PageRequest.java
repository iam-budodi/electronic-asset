package com.japhet_sebastian.vo;

import jakarta.ws.rs.DefaultValue;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Objects;

public class PageRequest {

    @RestQuery("page")
    @DefaultValue("0")
    @Parameter(description = "Page index")
    private Integer pageNum;

    @RestQuery("size")
    @DefaultValue("10")
    @Parameter(description = "Page size")
    private Integer pageSize;

    @RestQuery("search")
    @Parameter(description = "Search query parameter")
    private String search;

    @RestQuery("date")
    @Parameter(description = "Date query parameter")
    private LocalDate date;

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageRequest that)) return false;
        return Objects.equals(getPageNum(), that.getPageNum()) && Objects.equals(getPageSize(), that.getPageSize()) && Objects.equals(getSearch(), that.getSearch()) && Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPageNum(), getPageSize(), getSearch(), getDate());
    }
}
