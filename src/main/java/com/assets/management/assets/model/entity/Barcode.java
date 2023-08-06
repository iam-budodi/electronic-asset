package com.assets.management.assets.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "barcodes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_tag",
                        columnNames = {"asset_tag"})})
public class Barcode extends PanacheEntity {

    @NotNull
    @Size(max = 64)
    @Column(name = "asset_tag", length = 64, unique = true)
    public String barcodeStrings;
}
