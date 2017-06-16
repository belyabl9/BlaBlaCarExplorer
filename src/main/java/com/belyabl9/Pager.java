package com.belyabl9;

public class Pager {
    private String page;
    private String pages;
    private String total;
    private String limit;

    public Pager(String page, String pages, String total, String limit) {
        this.page = page;
        this.pages = pages;
        this.total = total;
        this.limit = limit;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }
}
