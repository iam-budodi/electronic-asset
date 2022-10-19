package com.assets.management.assets.service;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.assets.management.assets.client.QrProxy;
import com.assets.management.assets.model.Phone;
import com.assets.management.assets.model.QrContent;
import com.assets.management.assets.model.Vendor;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class PhoneService {

	@Inject
	Logger LOG;

	@Inject
	@RestClient
	QrProxy qrProxy;

	// delete
	public Phone persistPhone(
	        @Valid Phone phone,
	        @NotNull Long vendorId
	) { 
		Optional<Vendor> optional = Vendor.findByIdOptional(vendorId);
		LOG.info("Is Vendor Present " + optional.get());
		Vendor vendor = optional.orElseThrow(() -> new BadRequestException());

		phone.vendor = vendor;
		phone.stockedAt = Instant.now();

		Phone.persist(phone);
		phone.qrString = retrieveQrString(phone);
		return Panache.getEntityManager().merge(phone);
	}

	public Phone updatePhone(@Valid Phone phone, @NotNull Long id) {
		Phone sPhone = Panache.getEntityManager()
		        .getReference(Phone.class, id);
		phone.qrString = retrieveQrString(sPhone);
		phone.updatedAt = Instant.now();

		return Panache.getEntityManager().merge(phone);
	}

	// delete
	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Phone> allPhonesByVendor(
	        Long vendorId,
	        Integer pageIndex,
	        Integer pageSize
	) { 
		return Phone.find(
		        "select sp " 
		        		+ "from Phone sp "
		                + "where sp.vendor.id = ?1",
		                vendorId
		).page(pageIndex, pageSize).list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Phone findPhoneById(@NotNull Long id) {
		Optional<Phone> phone = Phone.findByIdOptional(id);
		return phone.orElseThrow(() -> new NotFoundException());
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countAllPhones() {
		return Phone.count();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<PanacheEntityBase> countPhonesPerStatus() {
		return Phone.find(
		        "select status, count(sp.status) as total "
		                + "from Phone sp " + "group by status"
		).list();

	}

	public void deletePhone(@NotNull Long id) {
		Panache.getEntityManager().getReference(Phone.class, id).delete();
	}
	
	// delete
	public Long deleteAllPhone(@NotNull Long vendorId) {
		Optional<Vendor> optional = Vendor.findByIdOptional(vendorId);
		// Vendor vendor = 
		optional.orElseThrow(() -> new BadRequestException());
		return Phone.deleteAll();
	}
// delete
	private String retrieveQrString(Phone phone) {
		PanacheQuery<QrContent> query = Phone.find("id", phone.id)
		        .project(QrContent.class);

		QrContent qrContent = query.singleResult();
		byte[]    code      = qrProxy.CreateQrString(qrContent);
		return Base64.getEncoder().encodeToString(code);
	}
}
