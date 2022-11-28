package com.assets.management.assets.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.Supplier;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class SupplierService {
	
	public Supplier createVendor(@Valid Supplier supplier) {
		Supplier.persist(supplier);
		return supplier;
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Supplier> getAll(Integer index, Integer size) {
		return Supplier
				.find("FROM Supplier s "
						+ "ORDER BY title")
				.page(index, size)
				.list();
	}

	public void updateById(
			@Valid Supplier supplier, @NotNull Long supplierId) {
		Supplier.findByIdOptional(supplierId).map(
		        found -> Panache.getEntityManager().merge(supplier)
		).orElseThrow(
				() -> new NotFoundException("Supplier dont exists")
				);
	}

	public void deleteById(@NotNull Long supplierId) {
		Panache
			.getEntityManager()
			.getReference(Supplier.class, supplierId)
			.delete();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Item> getItems(
			@NotNull Long supplierId, Integer index, Integer size) {
		return Item
				.find("supplier.id = ?1", supplierId)
				.page(index, size)
				.list();
	}
//
//	public Long deleteAllAssets(@NotNull Long vendorId) {
//		Optional<Supplier> optional = Supplier.findByIdOptional(vendorId);
//		optional.orElseThrow(NotFoundException::new);
//		return Item.delete("supplier.id = ?1", vendorId);
//	}
}
