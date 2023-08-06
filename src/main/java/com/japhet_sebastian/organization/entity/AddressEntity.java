package com.japhet_sebastian.organization.entity;

import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.Supplier;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "Address")
@Table(name = "addresses")
public class AddressEntity {

    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    @Column(name = "street_name", length = 32, nullable = false)
    public String street;
    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    @Column(name = "ward_name", length = 32, nullable = false)
    public String ward;
    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    @Column(name = "district_name", length = 32, nullable = false)
    public String district;
    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    @Column(length = 32, nullable = false)
    public String city;
    @Size(min = 5, max = 5, message = "{Postal.code.length}")
    @Pattern(regexp = "^\\d{5}$", message = "{Postal.code.length}")
    @Column(name = "postal_code", length = 5, nullable = false)
    public String postalCode;
    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    @Column(length = 32, nullable = false)
    public String country;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_fk", foreignKey = @ForeignKey(name = "employee_address_fk_constraint"))
    public Employee employee;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_fk", foreignKey = @ForeignKey(name = "supplier_address_fk_constraint"))
    public Supplier supplier;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_uuid")
    private UUID addressId;

    public AddressEntity() {
    }

    public UUID getAddressId() {
        return addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressEntity that)) return false;
        return Objects.equals(getAddressId(), that.getAddressId()) && Objects.equals(getStreet(), that.getStreet()) && Objects.equals(getWard(), that.getWard()) && Objects.equals(getDistrict(), that.getDistrict()) && Objects.equals(getCity(), that.getCity()) && Objects.equals(getPostalCode(), that.getPostalCode()) && Objects.equals(getCountry(), that.getCountry()) && Objects.equals(getEmployee(), that.getEmployee()) && Objects.equals(getSupplier(), that.getSupplier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddressId(), getStreet(), getWard(), getDistrict(), getCity(), getPostalCode(), getCountry(), getEmployee(), getSupplier());
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "addressId=" + addressId +
                ", street='" + street + '\'' +
                ", ward='" + ward + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", employee=" + employee +
                ", supplier=" + supplier +
                '}';
    }
}