package com.japhet_sebastian.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity(name = "QRCode")
@Table(name = "qr_codes", uniqueConstraints = {@UniqueConstraint(name = "unique_qr_label", columnNames = {"qr_label"})})
@Schema(description = "QRCode representation")
public class QRCodeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "qr_uuid", nullable = false)
    private UUID qrCodeId;

    @NotNull(message = "{Asset.qr-string.required}")
    @Size(max = 4000)
    @Schema(required = true)
    @Column(name = "qr_label", length = 4000, nullable = false)
    private byte[] qrByteString;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        QRCodeEntity that = (QRCodeEntity) o;
        return getQrCodeId() != null && Objects.equals(getQrCodeId(), that.getQrCodeId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
