package com.assets.management.assets.service;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.assets.management.assets.client.QrProxy;
import com.assets.management.assets.model.Asset;
import com.assets.management.assets.model.Phone;
import com.assets.management.assets.model.QrContent;
import com.assets.management.assets.model.Vendor;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class AssetService {

	@Inject
	Logger LOG;

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Asset> getAllAssets(Integer page, Integer size) {
		return Asset.find("from Asset a").page(page, size).list();
	}
//
//	public Phone updateAsset(@Valid Asset asset, @NotNull Long assetId) {
//		Asset assetFound = Panache.getEntityManager()
//		        .getReference(Asset.class, assetId);
//		
//		if (assetFound.serialNumber != asset.serialNumber || assetFound.modelName != asset.modelName)
//			asset.qrString = retrieveQrString(asset);
//		phone.updatedAt = Instant.now();
//
//		return Panache.getEntityManager().merge(phone);
//	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Phone findPhoneById(@NotNull Long id) {
		Optional<Phone> phone = Phone.findByIdOptional(id);
		return phone.orElseThrow(() -> new NotFoundException());
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countAllPhones() {
		return Phone.count();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<PanacheEntityBase> countPhonesPerStatus() {
		return Phone.find(
		        "select status, count(sp.status) as total " + "from Phone sp "
		                + "group by status"
		).list();

	}

	public void deletePhone(@NotNull Long id) {
		Panache.getEntityManager().getReference(Phone.class, id).delete();
	}

	// delete
	public Long deleteAllPhone(@NotNull Long vendorId) {
		Optional<Vendor> optional = Vendor.findByIdOptional(vendorId);
		// Vendor vendor =
		optional.orElseThrow(() -> new BadRequestException());
		return Phone.deleteAll();
	}

}
