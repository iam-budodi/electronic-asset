package com.japhet_sebastian.organization.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "College")
@Table(name = "colleges", uniqueConstraints = {
        @UniqueConstraint(name = "unique_college_name_code", columnNames = {"college_name", "college_code"})
})
@NamedQueries({@NamedQuery(name = "College.nameOrCode", query = "FROM College WHERE LOWER(collegeName) = :name OR LOWER(collegeCode) = :code")})
@Schema(description = "College Representation")
public class CollegeEntity {

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "college_uuid", foreignKey = @ForeignKey(name = "college_address_fk_constraint"))
    AddressEntity address;

    @Id
    private UUID collegeId;

    @Schema(required = true)
    @NotEmpty(message = "{College.name.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    @Column(name = "college_name", length = 64, nullable = false)
    private String collegeName;

    @Size(min = 2, max = 10, message = "{Alphanumeric.character.length}")
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "{Alphanumeric.character}")
    @Column(name = "college_code")
    private String collegeCode;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CollegeEntity that = (CollegeEntity) o;
        return getCollegeId() != null && Objects.equals(getCollegeId(), that.getCollegeId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

