package com.assets.management.assets.util;

import io.quarkus.panache.common.Sort;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Locale;
import java.util.Objects;

@ApplicationScoped
public class PanacheUtils {

    //    public PanacheQuery<T> pageQuery(Integer pageIndex, Integer pageSize) {
//        PanacheQuery<T> query = null;
//        Page currentPage = Page.of(pageIndex, pageSize);
//        return query.page(currentPage);
//    }
    public static Sort.Direction panacheSort(String direction) {
        return Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;
    }

    public static String searchString(String value) {
        if (value != null)
            return "%" + value.toLowerCase(Locale.ROOT) + "%";

        return null;
    }
}
