package com.assets.management.electronic.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.electronic.model.SmartPhone;
import com.assets.management.electronic.model.Vendor;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class VendorService {

	@Inject
	Logger LOG;

	public Vendor persistVendor(@Valid Vendor vendor) {
		Vendor.persist(vendor);
		LOG.info("Vendor Persist: " + vendor.id);

		return vendor;
	}

	public Vendor updateVendor(@Valid Vendor vendor, @NotNull Long id) {
		Panache.getEntityManager().getReference(Vendor.class, id);

		LOG.info("Check Vendor: " + vendor.service);
		return Panache.getEntityManager().merge(vendor);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Vendor> allVendor(
	        Long vendorId,
	        Integer pageIndex,
	        Integer pageSize
	) {
		return Vendor.find("from Vendor vp").page(pageIndex, pageSize).list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Vendor findById(@NotNull Long id) {
		Optional<Vendor> phone = Vendor.findByIdOptional(id);
		return phone.orElseThrow(() -> new NotFoundException());
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countVendors() {
		return Vendor.count();
	}

	public void deleteVendor(@NotNull Long id) {
		Panache.getEntityManager().getReference(Vendor.class, id).delete();
	}

	public Long deleteAll() {
		return Vendor.deleteAll();
	}
}
