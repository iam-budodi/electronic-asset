package com.assets.management.assets.service;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.Employee;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class EndUserService {

	@Inject
	Logger LOG;

	public URI createCandidate(@Valid Employee endUser, @Context UriInfo uri) {
		Employee.persist(endUser);
		return uri.getAbsolutePathBuilder().path(Long.toString(endUser.id))
		        .build();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Employee> getAllCandidates(Integer page, Integer size) {
		return Employee.find("from EndUser eu").page(page, size).list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countCandidates() {
		return Employee.count();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Employee findById(@NotNull Long id) {
		Optional<Employee> candidate = Employee.findByIdOptional(id);
		return candidate.orElseThrow(() -> new NotFoundException());
	}

	public Employee updateById(@Valid Employee candidate, @NotNull Long id) {
		Panache.getEntityManager().getReference(Employee.class, id);
		return Panache.getEntityManager().merge(candidate);
	}

	public void deleteById(@NotNull Long id) {
		Panache.getEntityManager().getReference(Employee.class, id).delete();
	}

	public Long deleteAll() {
		return Employee.deleteAll();
	}

	public void assignAsset(@Valid Item asset, @NotNull Long candidateId) {
		Optional<Employee> optional = Employee.findByIdOptional(candidateId);
		LOG.info("Is EndUser Present " + optional.get());
		Employee endUser = optional.orElseThrow(
		        () -> new BadRequestException("Candidate dont exist")
		);
//
//		asset.endUser = endUser;
//		asset.employDate = Instant.now();
		Panache.getEntityManager().merge(asset);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Item> getAllAssets(Long candidateId) {
		return Item.find("endUser.id = ?1", candidateId).list();
	}

	public void unAssignAsset(@NotNull Long candidateId, String serialNumber) {
		Item asset = Item.find(
		        "endUser.id = ?1 and serialNumber = ?2", candidateId,
		        serialNumber
		).firstResult();
		if (asset == null)
			throw new NotFoundException("Record not found!");
//		asset.endUser = null;

		Panache.getEntityManager().merge(asset);
	}
}
