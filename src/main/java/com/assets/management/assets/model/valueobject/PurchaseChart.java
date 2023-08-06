package com.assets.management.assets.model.valueobject;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@RegisterForReflection
public class PurchaseChart {

    @Min(1)
    @NotNull
    public final Long quantity;

    @NotNull
    public final LocalDate purchaseDate;

    public PurchaseChart(Long quantity, LocalDate purchaseDate) {
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
    }
}
