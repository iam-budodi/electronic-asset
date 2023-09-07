package com.japhet_sebastian.supplier;

import com.japhet_sebastian.vo.PageRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

@ApplicationScoped
public class SupplierRepository implements PanacheRepositoryBase<SupplierEntity, UUID> {

    public List<SupplierEntity> allSuppliers(PageRequest pageRequest) {
        String queryString = getQueryString();
        if (pageRequest.getSearch() != null)
            pageRequest.setSearch("%" + pageRequest.getSearch().toLowerCase(Locale.ROOT) + "%");

        return find(
                queryString,
                Sort.by("s.companyName", Sort.Direction.Descending),
                Parameters.with("search", pageRequest.getSearch()))
                .page(Page.of(pageRequest.getPageNum(), pageRequest.getPageSize()))
                .list();
    }

    private String getQueryString() {
        return "FROM Supplier s LEFT JOIN FETCH s.address a " +
                "WHERE :search IS NULL OR LOWER(s.companyName) LIKE :search " +
                "OR LOWER(s.companyPhone) LIKE :search " +
                "OR LOWER(s.website) LIKE :search " +
                "OR LOWER(s.companyEmail) LIKE :search ";
    }

//    public static Optional<SupplierEntity> findByEmailAndPhone(String email, String phone) {
//        return find("#Supplier.getEmailOrPhone", Parameters.with("email", email).and("phone", phone).map())
//                .firstResultOptional();
//    }
}
