package com.assets.management.assets.service;

import com.assets.management.assets.model.entity.Supplier;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class SupplierService {

    public Supplier createSupplier(@Valid Supplier supplier) {
        supplier.address.supplier = supplier;
        supplier.address.id = supplier.id;
        Supplier.persist(supplier);
        return supplier;
    }

    public void updateSupplier(@Valid Supplier supplier, @NotNull Long supplierId) {
        supplier.address = null;
        Supplier
                .findByIdOptional(supplierId)
                .map(found -> Panache.getEntityManager().merge(supplier))
                .orElseThrow(() -> new NotFoundException("Supplier dont exists"));
    }

    public void deleteSupplier(@NotNull Long supplierId) {
        Panache
                .getEntityManager()
                .getReference(Supplier.class, supplierId)
                .delete();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<Supplier> listSuppliers(String searchValue, String column, String direction) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";

        String sortVariable = String.format("s.%s", column);
        Sort.Direction sortDirection = Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;

        String queryString = "SELECT s FROM Supplier s LEFT JOIN s.address a " +
                "WHERE (:searchValue IS NULL OR LOWER(s.name) LIKE :searchValue " +
                "OR :searchValue IS NULL OR LOWER(s.phone) LIKE :searchValue " +
                "OR :searchValue IS NULL OR LOWER(s.website) LIKE :searchValue " +
                "OR LOWER(s.email) LIKE :searchValue) ";

        return Supplier.find(queryString,
                Sort.by(sortVariable, sortDirection),
                Parameters.with("searchValue", searchValue)
        );
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Supplier> findSupplier(@NotNull Long supplierId) {
        return Supplier.find("FROM Supplier s "
                                + "LEFT JOIN FETCH s.address "
                                + "WHERE s.id = :supplierId",
                        Parameters.with("supplierId", supplierId))
                .firstResultOptional();
    }
}
