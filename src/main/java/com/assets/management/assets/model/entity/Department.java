package com.assets.management.assets.model.entity;

import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@Entity
@Table(
		name = "departments", 
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_department_name", 
						columnNames = { "department_name" }) 
				}
		)
@NamedQueries({
	@NamedQuery(
			name = "Department.getName", 
			query = "FROM Department WHERE LOWER(name) = :name")
})
public class Department extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(name = "department_name", length = 64, nullable = false)
	public String name;

	@Size(min = 1, max = 400)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(length = 400)
	public String description;

	public static List<Department> findAllOrderByName() {
		return listAll(Sort.by("name"));
	}

	public static Optional<Department> findByName(String name) {
//		return find("LOWER(name)", name.toLowerCase())
//				.firstResultOptional();
//	}
		return find("#Department.getName", Parameters.with("name", name.toLowerCase()))
				.firstResultOptional();
	}
}
