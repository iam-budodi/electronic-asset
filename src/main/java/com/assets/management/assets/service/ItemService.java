package com.assets.management.assets.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.Supplier;
import com.assets.management.assets.util.QrCodeClient;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class ItemService {

	@Inject
	Logger LOG;


	// Think about remove this property to assignment and transfer
	@Inject
	QrCodeClient qrCodeString;

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Item> getAllItems(Integer page, Integer size) {
		return Item.find("ORDER BY itemName, qty")
				.page(page, size)
				.list();
	}

	public Item addItem(@Valid Item item) {
		LOG.info("It got here...");
		return Supplier.findByIdOptional(item.supplier.id)
				.map(supplier -> {
					// should be generated and mapped on the 
					// assignment and transfer table
					// item.label.item = item;
					// item.label.id = item.id;
					Item.persist(item);
					return item;
					}
				).orElseThrow(() -> 
				new NotFoundException("Supplier dont exist"));
	}

	public void updateItem(@Valid Item item, @NotNull Long itemId) {
		Item.findByIdOptional(itemId).map(
		        itemFound -> Panache.getEntityManager().merge(item)
		).orElseThrow(
				() -> new NotFoundException("Item dont exist")
				);
	} 

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<PanacheEntityBase> countItemPerSupplier() {
		return Item
				.find(
				"SELECT i.supplier.title, "
				+ "COUNT(i.supplier) AS total "
				+ "FROM Item i "
				+ "GROUP BY i.supplier.title"
				)
				.list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<PanacheEntityBase> countItemPerStatus() {
		return Item
				.find(
				"SELECT i.status, COUNT(i.status) AS total "
				+ "FROM Item i "
				+ "GROUP BY i.status"
				)
				.list();
	}

	public void deleteById(@NotNull Long itemId) {
		Panache.getEntityManager()
			.getReference(Item.class, itemId)
			.delete();
	}

}
