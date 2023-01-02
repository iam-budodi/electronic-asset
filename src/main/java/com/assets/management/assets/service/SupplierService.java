package com.assets.management.assets.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import com.assets.management.assets.model.Supplier;

import io.quarkus.hibernate.orm.panache.Panache;

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
	public List<Supplier> listSuppliers(Integer index, Integer size) {
//		return Supplier.find("FROM Supplier s " + "ORDER BY title").page(index, size).list();
		return Supplier
				.find("ORDER BY name")
				.page(index, size)
				.list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Optional<Supplier> findSupplier(@NotNull Long supplierId) {
		return Supplier.findByIdOptional(supplierId);
//		return Item
//				.find("supplier.id = ?1", supplierId)
//				.page(index, size)
//				.list();
	}
}
