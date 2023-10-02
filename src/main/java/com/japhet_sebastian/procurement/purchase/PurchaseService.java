package com.japhet_sebastian.procurement.purchase;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class PurchaseService implements PurchaseInterface {

    @Inject
    PurchaseRepository purchaseRepository;

    @Inject
    PurchaseMapper purchaseMapper;

    public List<PurchaseDto> allPurchases(PurchasePage purchasePage) {
        List<PurchaseEntity> purchaseEntities = purchaseRepository.allPurchases(purchasePage).list();
        return purchaseMapper.toListDto(purchaseEntities);
    }

    public Long collegesCount() {
        return purchaseRepository.count();
    }

    public Optional<PurchaseDto> getPurchase(@NotNull String employeeId) {
        return this.purchaseRepository.findPurchase(employeeId).map(purchaseMapper::toDto);
    }

    public List<SelectOptions> projection() {
        return this.purchaseRepository.purchaseProjection().list();
    }

    public void savePurchase(@Valid PurchaseDto purchase) {
        if (purchaseRepository.isPurchase(purchase.getInvoiceNumber()))
            throw new ServiceException("Purchase record already exists!");

        PurchaseEntity purchaseEntity = purchaseMapper.toEntity(purchase);
        purchaseRepository.persist(purchaseEntity);
        purchaseMapper.partialDtoUpdate(purchaseEntity, purchase);
    }

    public void updatePurchase(@Valid PurchaseDto purchase) {
        PurchaseEntity purchaseEntity = getPurchaseEntity(purchase.purchaseId);
        purchaseEntity = purchaseMapper.partialEntityUpdate(purchase, purchaseEntity);
        purchaseRepository.persist(purchaseEntity);
        purchaseMapper.partialDtoUpdate(purchaseEntity, purchase);
    }

    public void deletePurchase(@NotNull String purchaseId) {
        purchaseRepository.delete(getPurchaseEntity(purchaseId));
    }

    private PurchaseEntity getPurchaseEntity(String purchaseId) {
        return purchaseRepository.findPurchase(purchaseId)
                .orElseThrow(() -> new ServiceException("No purchase found for purchaseId[%s]", purchaseId));
    }

}
