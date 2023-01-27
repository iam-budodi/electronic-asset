package com.assets.management.assets.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.entity.Category;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class CategoryService {

	@Inject
	Logger LOG;

	public Category createCategory(@Valid Category category) {
		Category.persist(category);
		return category;
	}

	public void updateCategory(
	        @Valid Category cat, @NotNull Long catId) {
		Category.findByIdOptional(catId).map(
		        found -> Panache.getEntityManager().merge(cat)
		).orElseThrow(
				() -> new NotFoundException("Category dont exists")
				);
	}

	public void deleteCategory(@NotNull Long catId) {
		Panache
			.getEntityManager()
			.getReference(Category.class, catId)
			.delete();
	}
  
}
