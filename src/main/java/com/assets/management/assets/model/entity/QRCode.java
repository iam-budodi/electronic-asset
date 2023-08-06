package com.assets.management.assets.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

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

    @Override
    public String toString() {
        return "QRCode{" +
                "qrByteString=" + Arrays.toString(qrByteString) +
                ", id=" + id +
                '}';
    }
}
