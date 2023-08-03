package com.assets.management.assets.util.metadata;

import io.quarkus.panache.common.Page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class LinkHeaderPagination {

    public String linkStream(UriInfo uriInfo, Page currentPage, Integer size, int lastPage) {
        Stream<Link> linkStream = Stream.of(
                createLinkHeader(uriInfo, currentPage.previous(), "prev"),
                createLinkHeader(uriInfo, currentPage.next(), "next"),
                createLinkHeader(uriInfo, Page.ofSize(size), "first"),
                createLinkHeader(uriInfo, Page.of(lastPage, size), "last")
        ).filter(Objects::nonNull);

        return linkStream.map(Link::toString).collect(Collectors.joining(","));
    }

    private Link createLinkHeader(UriInfo uriInfo, Page page, String rel) {
        if (page == null) return null;

        return Link.fromUriBuilder(
                        uriInfo.getRequestUriBuilder()
                                .replaceQueryParam("page", page.index)
                                .replaceQueryParam("size", page.size))
                .rel(rel)
                .build();
    }
}
