package com.belyabl9;

import com.google.gson.annotations.SerializedName;

public class Link {

    @SerializedName("_front")
    private String url;

    public Link(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        return url != null ? url.equals(link.url) : link.url == null;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Link{" +
                "url='" + url + '\'' +
                '}';
    }
}
