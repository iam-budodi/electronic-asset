package com.japhet_sebastian.organization.entity;

import java.util.Objects;

public class CollegeAddress {
    Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollegeAddress that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getAddress(), that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAddress());
    }

    @Override
    public String toString() {
        return "CollegeAddress{" +
                "address=" + address +
                '}';
    }
}
