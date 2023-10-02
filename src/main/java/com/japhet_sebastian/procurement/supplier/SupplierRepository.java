package com.japhet_sebastian.procurement.supplier;

import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SupplierRepository implements PanacheRepositoryBase<SupplierEntity, UUID> {

    public PanacheQuery<SupplierEntity> allSuppliers(SupplierPage supplierPage) {
        String queryString = getQueryString();
        if (supplierPage.getSearch() != null)
            supplierPage.setSearch("%" + supplierPage.getSearch().toLowerCase(Locale.ROOT) + "%");

        return find(queryString, Sort.by("s.companyName", Sort.Direction.Descending),
                Parameters.with("search", supplierPage.getSearch()))
                .page(Page.of(supplierPage.getPageNumber(), supplierPage.getPageSize()));
    }

    public Optional<SupplierEntity> findSupplier(@NotNull String supplierId) {
        return find("FROM Supplier s LEFT JOIN FETCH s.address WHERE s.id = :supplierId",
                Parameters.with("supplierId", UUID.fromString(supplierId))).firstResultOptional();
    }

    public List<SelectOptions> selectProjection() {
        return find("SELECT s.id, s.companyName FROM Supplier s")
                .project(SelectOptions.class).list();
    }

    public Optional<SupplierEntity> searchByEmailOrPhone(String email, String phone) {
        return find("#SupplierEntity.getEmailOrPhone", Parameters.with("email", email).and("phone", phone).map())
                .firstResultOptional();
    }

    private String getQueryString() {
        return "FROM Supplier s LEFT JOIN FETCH s.address a " +
                "WHERE :search IS NULL OR LOWER(s.companyName) LIKE :search " +
                "OR LOWER(s.companyPhone) LIKE :search " +
                "OR LOWER(s.website) LIKE :search " +
                "OR LOWER(s.companyEmail) LIKE :search ";
    }
}
