package com.japhet_sebastian.employee;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity {

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @UpdateTimestamp(source = SourceType.DB)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotEmpty(message = "{LoggedIn.username.required}")
    @Column(name = "registered_by", length = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "{String.special.character}")
    private String registeredBy;

    @Column(name = "updated_by")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "{String.special.character}")
    private String updatedBy;

}
