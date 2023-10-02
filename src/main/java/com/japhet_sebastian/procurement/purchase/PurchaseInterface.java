package com.japhet_sebastian.procurement.purchase;

import java.util.List;

public interface PurchaseInterface {

    List<PurchaseDto> allPurchases(PurchasePage purchasePage);

    Long collegesCount();
}
