package com.assets.management.assets.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "head_of_departments", uniqueConstraints = {
        @UniqueConstraint(name = "unique_employee", columnNames = {"employee_fk"})
})
@NamedQueries({
        @NamedQuery(
                name = "HoD.workId",
                query = "FROM HeadOfDepartment WHERE LOWER(employee.workId) = :workId")})
public class HeadOfDepartment extends PanacheEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_fk", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "hod_department_fk_constraint"))
    public Employee employee;

//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "department_fk", referencedColumnName = "id",
//            foreignKey = @ForeignKey(name = "hod_department_fk_constraint"))
//    public Department department;
}
