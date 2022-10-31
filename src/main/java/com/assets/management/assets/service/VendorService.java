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

import com.assets.management.assets.model.Asset;
import com.assets.management.assets.model.Vendor;
import com.assets.management.assets.util.QrCodeString;

import io.quarkus.hibernate.orm.panache.Panache;

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
        return vendor.orElseThrow(NotFoundException::new);
    }

    public void updateVendor(@Valid Vendor vendor, @NotNull Long id) {
        Panache.getEntityManager().getReference(Vendor.class, id);
        Panache.getEntityManager().merge(vendor);
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

        asset.vendor = optional.orElseThrow(NotFoundException::new);
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
        optional.orElseThrow(NotFoundException::new);
        return Asset.delete("vendor.id = ?1", vendorId);
    }
 
//	private String retrieveQrString(Asset asset) {
//		PanacheQuery<QrContent> query = Asset.find("id", asset.id)
//		        .project(QrContent.class);
//
//		QrContent qrContent = query.singleResult();
//		byte[]    code      = qrProxy.createQrString(qrContent);
//		return Base64.getEncoder().encodeToString(code);
//	}
}
