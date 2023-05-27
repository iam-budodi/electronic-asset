package com.assets.management.assets.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(
        name = "qr_codes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_label",
                        columnNames = {"qr_label"})})
@Schema(description = "QRCode representation")
public class QRCode extends PanacheEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Schema(required = true)
    @Size(max = 4000)
    @Column(name = "qr_label", length = 4000, nullable = false)
    public byte[] qrByteString;

}
