package com.japhet_sebastian.procurement.purchase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.japhet_sebastian.procurement.supplier.SupplierDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for {@link PurchaseEntity}
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseDto implements Serializable {

    String purchaseId;

    @NotNull(message = "{Purchase.date.required}")
    @PastOrPresent(message = "{Purchase.future-date.required}")
    LocalDate purchaseDate;

    @NotNull(message = "{Purchase.quantity.required}")
    @PositiveOrZero(message = "{Purchase.quantity-number.required}")
    Integer purchaseQty;

    @NotNull(message = "{Purchase.price.required}")
    @Min(message = "{Purchase.min-price.required}", value = 1)
    BigDecimal purchasePrice;

    @NotNull(message = "Purchase.invoice-number.required")
    String invoiceNumber;

    String supplierName;
    String supplierPhone;
    String supplierEmail;
    String supplierAddress;

    SupplierDto supplier;
}