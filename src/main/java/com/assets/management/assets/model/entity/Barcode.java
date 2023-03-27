package com.assets.management.assets.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

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
