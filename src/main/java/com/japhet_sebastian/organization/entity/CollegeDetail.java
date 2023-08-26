package com.japhet_sebastian.organization.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class CollegeDetail {

    private String collegeId;

    @NotEmpty(message = "{College.name.required}")
    @Size(min = 2, max = 64, message = "{Sixty-four.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String collegeName;

    @Size(min = 2, max = 10, message = "{Alphanumeric.character.length}")
    @Pattern(regexp = "^[\\p{L}\\p{Nd} _]+$", message = "{Alphanumeric.character}")
    private String collegeCode;
    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String street;

    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String district;

    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String city;

    @Size(min = 5, max = 5, message = "{Postal.code.length}")
    @Pattern(regexp = "^\\d{5}$", message = "{Postal.code.length}")
    private String postalCode;

    @NotEmpty(message = "{Address.field.required}")
    @Size(min = 2, max = 32, message = "{Thirty-two.string.length}")
    @Pattern(regexp = "^[\\p{L} .'-/]+$", message = "{String.special.character}")
    private String country;

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollegeDetail that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getCollegeId(), that.getCollegeId()) && Objects.equals(getCollegeName(), that.getCollegeName()) && Objects.equals(getCollegeCode(), that.getCollegeCode()) && Objects.equals(getStreet(), that.getStreet()) && Objects.equals(getDistrict(), that.getDistrict()) && Objects.equals(getCity(), that.getCity()) && Objects.equals(getPostalCode(), that.getPostalCode()) && Objects.equals(getCountry(), that.getCountry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCollegeId(), getCollegeName(), getCollegeCode(), getStreet(), getDistrict(), getCity(), getPostalCode(), getCountry());
    }

    @Override
    public String toString() {
        return "CollegeDetail{" +
                "collegeId='" + collegeId + '\'' +
                ", collegeName='" + collegeName + '\'' +
                ", collegeCode='" + collegeCode + '\'' +
                ", street='" + street + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
