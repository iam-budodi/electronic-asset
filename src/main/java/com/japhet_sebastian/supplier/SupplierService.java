package com.japhet_sebastian.supplier;


import com.japhet_sebastian.vo.PageRequest;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@ApplicationScoped
public class SupplierService {

    @Inject
    SupplierRepository supplierRepository;

    public List<SupplierEntity> listSuppliers(PageRequest pageRequest) {
        return supplierRepository.allSuppliers(pageRequest);
    }
//
//    public Supplier createSupplier(@Valid Supplier supplier) {
//        supplier.address.supplier = supplier;
//        supplier.address.id = supplier.id;
//        Supplier.persist(supplier);
//        return supplier;
//    }
//
//    public void updateSupplier(@Valid Supplier supplier, @NotNull Long supplierId) {
//        supplier.address = null;
//        Supplier
//                .findByIdOptional(supplierId)
//                .map(found -> Panache.getEntityManager().merge(supplier))
//                .orElseThrow(() -> new NotFoundException("Supplier dont exists"));
//    }
//
//    public void deleteSupplier(@NotNull Long supplierId) {
//        Panache
//                .getEntityManager()
//                .getReference(Supplier.class, supplierId)
//                .delete();
//    }
//
//
//    public Optional<Supplier> findSupplier(@NotNull Long supplierId) {
//        return Supplier.find("FROM Supplier s "
//                                + "LEFT JOIN FETCH s.address "
//                                + "WHERE s.id = :supplierId",
//                        Parameters.with("supplierId", supplierId))
//                .firstResultOptional();
//    }
}
