package com.assets.management.assets.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Asset;
import com.assets.management.assets.model.Asset;
import com.assets.management.assets.model.Vendor;
import com.assets.management.assets.util.QrCodeString;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class AssetService {

    @Inject
    Logger LOG;

    @Inject
    QrCodeString qrCodeString;

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Asset> getAllAssets(Integer page, Integer size) {
        return Asset.find("from Asset a").page(page, size).list();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Asset findById(@NotNull Long id) {
        Optional<Asset> asset = Asset.findByIdOptional(id);
        return asset.orElseThrow(NotFoundException::new);
    }

    public void updateAsset(@Valid Asset asset, @NotNull Long assetId) {
        Asset assetFound = Panache.getEntityManager()
                .getReference(Asset.class, assetId);

        if (!assetFound.serialNumber.equals(asset.serialNumber)
                || !assetFound.modelName.equals(asset.modelName))
            asset.qrString = qrCodeString.formatCodeImgToStr(asset);

        asset.updatedAt = Instant.now();
        Panache.getEntityManager().merge(asset);
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public Long countAllAssets() {
        return Asset.count();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PanacheEntityBase> countAssetPerVendor() {
        return Asset.find(
                "select a.vendor.companyName, count(a.vendor) as total from Asset a group by a.vendor.companyName"
        ).list();

    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PanacheEntityBase> countAssetPerStatus() {
        return Asset.find(
                "select a.status, count(a.status) as total from Asset a group by a.status"
        ).list();

    }

    public void deleteById(@NotNull Long id) {
		Panache.getEntityManager().getReference(Asset.class, id).delete();
	}
 
}
