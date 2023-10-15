package com.japhet_sebastian.inventory;

import com.japhet_sebastian.vo.QrContent;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class AssetRepository implements PanacheRepositoryBase<AssetEntity, UUID> {

    public boolean checkSerialNumber(String serialNumber) {
        return find("#Asset.getSN", Parameters.with("serialNumber", serialNumber).map())
                .firstResultOptional().isPresent();
    }

    public PanacheQuery<AssetEntity> getAll(String searchValue, LocalDate date) {
        Map<String, Object> params = new HashMap<>();
        params.put("search", searchValue);
        params.put("date", date);


        if (Objects.nonNull(searchValue))
            searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";


        String query = queryString(date);
        Sort sort = Sort.by("a.brand", Sort.Direction.Descending)
                .and("a.model", Sort.Direction.Descending)
                .and("c.categoryName", Sort.Direction.Descending);

        return find(query, sort, params).page(Page.of(0, 10));
    }


    public PanacheQuery<AssetEntity> getById(Long assetId) {
        return find(selectString() + "WHERE a.assetId = :assetId", Parameters.with("assetId", assetId));
    }

    public PanacheQuery<AssetEntity> getAssetByInvoice(String invoiceNumber) {
        return find(selectString() + "WHERE a.purchase.invoiceNumber = :invoiceNumber",
                Parameters.with("invoiceNumber", invoiceNumber));
    }

    public QrContent projectQrContents(String sn) {
        return find("serialNumber", sn).project(QrContent.class).singleResult();
    }

    private String selectString() {
        return "SELECT DISTINCT a FROM Asset a LEFT JOIN FETCH a.category LEFT JOIN FETCH a.label " +
                "LEFT JOIN FETCH a.purchase p LEFT JOIN FETCH p.supplier s LEFT JOIN FETCH s.address ";
    }

    private String queryString(LocalDate date) {
        String query = "SELECT DISTINCT a FROM Asset a LEFT JOIN FETCH a.category c LEFT JOIN FETCH a.label " +
                "LEFT JOIN FETCH a.peripherals LEFT JOIN FETCH a.purchase p LEFT JOIN FETCH p.supplier s LEFT JOIN FETCH s.address " +
                "WHERE :search IS NULL OR LOWER(a.brand) LIKE :search " +
                "OR LOWER(a.model) LIKE :search " +
                "OR LOWER(a.modelNumber) LIKE :search " +
                "OR LOWER(a.serialNumber) LIKE :search " +
                "OR LOWER(c.categoryName) LIKE :search ";

        if (Objects.nonNull(date)) query += "AND p.purchaseDate = :date";
        else query += "AND (:date IS NULL OR p.purchaseDate = :date)";
        return query;
    }
}
