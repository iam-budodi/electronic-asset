package com.assets.management.assets.service;

import java.net.URI;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.assets.management.assets.client.QrProxy;
import com.assets.management.assets.model.Asset;
import com.assets.management.assets.model.QrContent;
import com.assets.management.assets.model.Vendor;
import com.assets.management.assets.util.QrCodeString;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class VendorService {

	@Inject
	Logger LOG;
	
	@Inject
	QrCodeString qrCodeString;
//
//	@Inject
//	@RestClient
//	QrProxy qrProxy;

	public URI createVendor(@Valid Vendor vendor, @Context UriInfo uriInfo) {
		Vendor.persist(vendor);
		return uriInfo.getAbsolutePathBuilder().path(Long.toString(vendor.id))
		        .build();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Vendor> getAllVendors(Integer pageIndex, Integer pageSize) {
		return Vendor.find("from Vendor vp").page(pageIndex, pageSize).list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countVendors() {
		return Vendor.count();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Vendor findById(@NotNull Long id) {
		Optional<Vendor> vendor = Vendor.findByIdOptional(id);
		return vendor.orElseThrow(() -> new NotFoundException());
	}

	public Vendor updateVendor(@Valid Vendor vendor, @NotNull Long id) {
		Panache.getEntityManager().getReference(Vendor.class, id);

		LOG.info("Check Vendor: " + vendor.service);
		return Panache.getEntityManager().merge(vendor);
	}

	public void deleteVendorById(@NotNull Long id) {
		Panache.getEntityManager().getReference(Vendor.class, id).delete();
	}

	public Long deleteAllVendors() {
		return Vendor.deleteAll();
	}

	public URI addAsset(
	        @Valid Asset asset,
	        @NotNull Long vendorId,
	        @Context UriInfo uriInfo
	) {
		Optional<Vendor> optional = Vendor.findByIdOptional(vendorId);
		LOG.info("Is Vendor Present " + optional.get());
		Vendor vendor = optional.orElseThrow(() -> new BadRequestException());

		asset.vendor = vendor;
		asset.stockedAt = Instant.now();

		Asset.persist(asset);
		LOG.info("Check retrned asset:  " + asset.id);
		asset.qrString = qrCodeString.formatCodeImgToStr(asset);
		Panache.getEntityManager().merge(asset);
		return uriInfo.getAbsolutePathBuilder().path(Long.toString(asset.id))
		        .build();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Asset> getAllAssets(
	        Long vendorId,
	        Integer index,
	        Integer size
	) { // "select a from Asset a where a.vendor.id = ?1",
		return Asset.find("vendor.id = ?1", vendorId).page(index, size).list();
	}

	public Long deleteAllAssets(@NotNull Long vendorId) {
		Optional<Vendor> optional = Vendor.findByIdOptional(vendorId);
		optional.orElseThrow(() -> new NotFoundException());
		return Asset.delete("vendor.id = ?1", vendorId);
	}
//
//	private String retrieveQrString(Asset asset) {
//		PanacheQuery<QrContent> query = Asset.find("id", asset.id)
//		        .project(QrContent.class);
//
//		QrContent qrContent = query.singleResult();
//		byte[]    code      = qrProxy.createQrString(qrContent);
//		return Base64.getEncoder().encodeToString(code);
//	}
}
