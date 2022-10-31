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

import com.assets.management.assets.model.Asset;
import com.assets.management.assets.model.EndUser;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class EndUserService {

	@Inject
	Logger LOG;

	public URI createCandidate(@Valid EndUser endUser, @Context UriInfo uri) {
		EndUser.persist(endUser);
		return uri.getAbsolutePathBuilder().path(Long.toString(endUser.id))
		        .build();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<EndUser> getAllCandidates(Integer page, Integer size) {
		return EndUser.find("from EndUser eu").page(page, size).list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countCandidates() {
		return EndUser.count();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public EndUser findById(@NotNull Long id) {
		Optional<EndUser> candidate = EndUser.findByIdOptional(id);
		return candidate.orElseThrow(() -> new NotFoundException());
	}

	public EndUser updateById(@Valid EndUser candidate, @NotNull Long id) {
		Panache.getEntityManager().getReference(EndUser.class, id);
		return Panache.getEntityManager().merge(candidate);
	}

	public void deleteById(@NotNull Long id) {
		Panache.getEntityManager().getReference(EndUser.class, id).delete();
	}

	public Long deleteAll() {
		return EndUser.deleteAll();
	}

	public void assignAsset(@Valid Asset asset, @NotNull Long candidateId) {
		Optional<EndUser> optional = EndUser.findByIdOptional(candidateId);
		LOG.info("Is EndUser Present " + optional.get());
		EndUser endUser = optional.orElseThrow(
		        () -> new BadRequestException("Candidate dont exist")
		);

		asset.endUser = endUser;
		asset.employDate = Instant.now();
		Panache.getEntityManager().merge(asset);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Asset> getAllAssets(Long candidateId) {
		return Asset.find("endUser.id = ?1", candidateId).list();
	}

	public void unAssignAsset(@NotNull Long candidateId, String serialNumber) {
		Asset asset = Asset.find(
		        "endUser.id = ?1 and serialNumber = ?2", candidateId,
		        serialNumber
		).firstResult();
		if (asset == null)
			throw new NotFoundException("Record not found!");
		asset.endUser = null;

		Panache.getEntityManager().merge(asset);
	}
}
