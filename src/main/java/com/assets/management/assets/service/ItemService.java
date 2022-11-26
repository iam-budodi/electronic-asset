package com.assets.management.assets.service;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.Supplier;
import com.assets.management.assets.util.QrCodeString;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class ItemService {

	@Inject
	Logger LOG;

	@Inject
	QrCodeString qrCodeString;

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Item> getAllItems(Integer page, Integer size) {
		LOG.info("Returning all items...");
		return Item.find("ORDER BY itemName, qty").page(page, size).list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Item findById(@NotNull Long itemId) {
//    	return Item.findByIdOptional(itemId).map(item -> item).orElseThrow(NotFoundException::new);
//        Optional<Item> asset = Item.findByIdOptional(id);
//        return asset.orElseThrow(NotFoundException::new);
		return null;
	}

	public Item addItem(@Valid Item item) {
		return Supplier.findByIdOptional(item.supplier.id).map(supplier -> {
			Item.persist(item);
			LOG.info("Check retrned asset:  " + item.id);
			return item;
		}).orElseThrow(
				() -> new NotFoundException("Supplier not found"));
	}

	public void updateAsset(@Valid Item asset, @NotNull Long assetId) {
		Item assetFound = Panache.getEntityManager().getReference(Item.class, assetId);

//        if (!assetFound.serialNumber.equals(asset.serialNumber)
//                || !assetFound.modelName.equals(asset.modelName))
//            asset.qrString = qrCodeString.formatCodeImgToStr(asset);
//
//        asset.updatedAt = Instant.now();
		Panache.getEntityManager().merge(asset);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countAllAssets() {
		return Item.count();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<PanacheEntityBase> countAssetPerVendor() {
		return Item.find(
				"select a.vendor.companyName, count(a.vendor) as total from Asset a group by a.vendor.companyName")
				.list();

	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<PanacheEntityBase> countAssetPerStatus() {
		return Item.find("select a.status, count(a.status) as total from Asset a group by a.status").list();

	}

	public void deleteById(@NotNull Long id) {
		Panache.getEntityManager().getReference(Item.class, id).delete();
	}

}
