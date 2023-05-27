package com.assets.management.assets.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(
        name = "contact_persons",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uniqueEmailAndOfficeExtension",
                        columnNames = {"email_address", "office_extension"})
        })
public class ContactPerson extends Person {

    @NotNull
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
    @Column(length = 64, nullable = false)
    public String position;

    @Column(name = "office_extension")
    public String extension;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "supplier_fk",
            foreignKey = @ForeignKey(
                    name = "supplier_contact_person_fk_constraint"))
    public Supplier supplier;
}
