package com.japhet_sebastian.organization.entity;

public class DeptDTO {
    DepartmentEntity department;
    AddressEntity address;

    public DeptDTO(DepartmentEntity department, AddressEntity address) {
        this.department = department;
        this.address = address;
    }

    public DepartmentEntity getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentEntity department) {
        this.department = department;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "DeptDTO{" +
                ", department=" + department +
                ", address=" + address +
                '}';
    }
}
