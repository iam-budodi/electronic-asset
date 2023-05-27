package com.assets.management.assets.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "categories")
@Schema(description = "Category representation")
public class Category extends PanacheEntity {

    @NotNull
    @Schema(required = true)
    @Column(name = "category_name", length = 64, nullable = false)
    public String name;

    @Column(length = 1000)
    public String description;

    public static PanacheQuery<Category> listCategories(String searchValue, String column, String direction) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";

        String sortVariable = String.format("c.%s", column);

        Sort.Direction sortDirection = Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;

        String queryString = "SELECT c FROM Category c " +
                "WHERE (:searchValue IS NULL OR LOWER(c.name) LIKE :searchValue)";

        return find(
                queryString,
                Sort.by(sortVariable, sortDirection),
                Parameters.with("searchValue", searchValue)
        );
    }
}
