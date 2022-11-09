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

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class SupplierService {

    @Inject
    Logger LOG;

    @Inject
    QrCodeString qrCodeString;
//
//	@Inject
//	@RestClient
//	QrProxy qrProxy;

    public URI createVendor(@Valid Supplier vendor, @Context UriInfo uriInfo) {
        Supplier.persist(vendor);
        return uriInfo.getAbsolutePathBuilder().path(Long.toString(vendor.id))
                .build();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Supplier> getAllVendors(Integer pageIndex, Integer pageSize) {
        return Supplier.find("from Vendor vp").page(pageIndex, pageSize).list();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Long countVendors() {
        return Supplier.count();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Supplier findById(@NotNull Long id) {
        Optional<Supplier> vendor = Supplier.findByIdOptional(id);
        return vendor.orElseThrow(NotFoundException::new);
    }

    public void updateVendor(@Valid Supplier vendor, @NotNull Long id) {
        Panache.getEntityManager().getReference(Supplier.class, id);
        Panache.getEntityManager().merge(vendor);
    }

    public void deleteVendorById(@NotNull Long id) {
        Panache.getEntityManager().getReference(Supplier.class, id).delete();
    }

    public Long deleteAllVendors() {
        return Supplier.deleteAll();
    }

    public URI addAsset(
            @Valid Item asset,
            @NotNull Long vendorId,
            @Context UriInfo uriInfo
    ) {
        Optional<Supplier> optional = Supplier.findByIdOptional(vendorId);
//
//        asset.vendor = optional.orElseThrow(NotFoundException::new);
//        asset.stockedAt = Instant.now();

        Item.persist(asset);
        LOG.info("Check retrned asset:  " + asset.id);
//        asset.qrString = qrCodeString.formatCodeImgToStr(asset);
        Panache.getEntityManager().merge(asset);
        return uriInfo.getAbsolutePathBuilder().path(Long.toString(asset.id))
                .build();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Item> getAllAssets(
            Long vendorId,
            Integer index,
            Integer size
    ) { // "select a from Asset a where a.vendor.id = ?1",
        return Item.find("vendor.id = ?1", vendorId).page(index, size).list();
    }

    public Long deleteAllAssets(@NotNull Long vendorId) {
        Optional<Supplier> optional = Supplier.findByIdOptional(vendorId);
        optional.orElseThrow(NotFoundException::new);
        return Item.delete("vendor.id = ?1", vendorId);
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
