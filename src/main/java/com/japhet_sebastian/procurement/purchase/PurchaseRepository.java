package com.japhet_sebastian.procurement.purchase;

import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class PurchaseRepository implements PanacheRepositoryBase<PurchaseEntity, UUID> {

    public PanacheQuery<PurchaseEntity> allPurchases(PurchasePage purchasePage) {
        Map<String, Object> purchaseParams;
        purchaseParams = new HashMap<>();
        purchaseParams.put("search", purchasePage.getSearch());
        purchaseParams.put("date", purchasePage.getDate());

        if (purchasePage.getSearch() != null)
            purchasePage.setSearch("%" + purchasePage.getSearch().toLowerCase(Locale.ROOT) + "%");

        String queryString = queryString(purchasePage);
        Sort sort = Sort.by("p.purchaseDate", Sort.Direction.Descending)
                .and("p.invoiceNumber", Sort.Direction.Descending)
                .and("p.supplier.companyName", Sort.Direction.Descending);

        return find(queryString, sort, purchaseParams)
                .page(Page.of(purchasePage.getPageNumber(), purchasePage.getPageSize()));
    }

    public Optional<PurchaseEntity> findPurchase(String purchaseId) {
        return find(purchaseQuery(), Parameters.with("purchaseId", UUID.fromString(purchaseId)))
                .firstResultOptional();
    }

    public PanacheQuery<SelectOptions> purchaseProjection() {
        return find("SELECT p.purchaseId, p.invoiceNumber FROM Purchase p").project(SelectOptions.class);
    }

    public Boolean isPurchase(String invoiceNumber) {
        final PanacheQuery<PurchaseEntity> query = find("SELECT p FROM Purchase p WHERE LOWER(invoiceNumber) = ?1", invoiceNumber.toLowerCase(Locale.ROOT));
        return query.count() > 0;
    }


//    public Optional<PurchaseEntity> findByInvoice(String invoiceNumber) {
//        return find("LOWER(invoiceNumber)", invoiceNumber.toLowerCase())
//                .firstResultOptional();
//    }

//    public static List<PurchasePerSupplier> purchasePerSupplier() {
//        return find("SELECT p.supplier.name AS supplier, COUNT(p.supplier) AS purchases "
//                + "FROM Purchase p GROUP BY p.supplier.name").project(PurchasePerSupplier.class).list();
//    }
//


    private String queryString(PurchasePage purchasePage) {
        String queryString = "SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.supplier s LEFT JOIN FETCH s.address " +
                "WHERE :search IS NULL OR LOWER(p.invoiceNumber) LIKE :search " +
                "OR LOWER(s.companyName) LIKE :search ";

        if (purchasePage.getDate() != null) queryString += "AND p.purchaseDate = :date";
        else queryString += "AND (:date IS NULL OR p.purchaseDate = :date)";
        return queryString;
    }

    private String purchaseQuery() {
        return "FROM Purchase p LEFT JOIN FETCH p.supplier s LEFT JOIN FETCH s.address "
                + "WHERE p.purchaseId = :purchaseId ";
    }
}
