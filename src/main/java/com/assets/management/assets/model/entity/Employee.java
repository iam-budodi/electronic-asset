package com.assets.management.assets.model.entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.assets.management.assets.model.valueobject.EmploymentStatus;

import io.quarkus.panache.common.Parameters;

@Entity
@Table(
		name = "employees", 
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_email_phone", 
						columnNames = { "email_address", "phone_number" }),
				@UniqueConstraint(
						name = "unique_workid", 
						columnNames = { "work_id" }) 
		})
@NamedQueries({
	@NamedQuery(
			name = "Employee.getEmailOrPhone", 
			query = "FROM Employee WHERE email = :email OR mobile = :mobile")
})
@Schema(description = "Employee representation")
public class Employee extends Person {
 
	@NotNull
	@Schema(required = true)
	@Column(name = "work_id")
	public String workId;

	@NotNull
	@Schema(required = true)
	@Column(name = "birthdate")
	public LocalDate dateOfBirth;

	@NotNull
	@Schema(required = true)
	@Column(name = "hire_date", nullable = false)
	public LocalDate hireDate;

	@NotNull
	@Schema(required = true)
	@ElementCollection
	@Enumerated(EnumType.STRING)
	@Column(name = "employment_status", nullable = false)
	public Set<EmploymentStatus> status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "department_fk", 
			foreignKey = @ForeignKey(
					name = "employee_department_fk_constraint", 
					foreignKeyDefinition = ""))
	public Department department;
	
	@OneToOne(
			mappedBy = "employee", 
			orphanRemoval = true,
			cascade = CascadeType.ALL, 
			fetch = FetchType.LAZY)
	public Address address;

	@Transient
	public Integer age;
	
	@Transient
	public Integer timeOfService;

	@Transient
	public LocalDate retireAt;

	@PostLoad
	@PostPersist
	@PostUpdate
	protected void calculateAgeAndRetireDate() {
		if (hireDate == null) {
			retireAt = null;
			return;
		} else if (dateOfBirth == null) {
			age = null;
			return;
		}

		timeOfService = Period.between(hireDate, LocalDate.now()).getYears();
		age = Period.between(dateOfBirth, LocalDate.now()).getYears();
		retireAt = LocalDate.now().plusYears(60 - (LocalDate.now().getYear() - dateOfBirth.getYear()));
	}

	public static boolean checkByEmailAndPhone(String email, String mobile) {
		return find(
				"#Employee.getEmailOrPhone", 
				Parameters.with("email", email).and("mobile", mobile).map())
				.firstResultOptional()
				.isPresent();
	}
}
