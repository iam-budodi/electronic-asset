package com.assets.management.electronic.service;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.assets.management.electronic.client.QrProxy;
import com.assets.management.electronic.model.QrContent;
import com.assets.management.electronic.model.SmartPhone;
import com.assets.management.electronic.model.Vendor;

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

	public SmartPhone persistPhone(
	        @Valid SmartPhone phone,
	        @NotNull Long vendorId
	) {
//		Vendor vendor = Vendor.findById(vendorId);

		phone.vendor = Vendor.findById(vendorId);
		phone.generatedAt = Instant.now();

		SmartPhone.persist(phone);
		phone.qrString = retrieveQrString(phone);
		return Panache.getEntityManager().merge(phone);
	}

	public SmartPhone updatePhone(@Valid SmartPhone phone, @NotNull Long id) {
		SmartPhone sPhone = Panache.getEntityManager()
		        .getReference(SmartPhone.class, id);
		phone.qrString = retrieveQrString(sPhone);
		phone.updatedAt = Instant.now();

		return Panache.getEntityManager().merge(phone);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<SmartPhone> allPhonesByVendor(
	        Long vendorId,
	        Integer pageIndex,
	        Integer pageSize
	) {

//		Vendor vendor = Vendor.findById(vendorId);
		return SmartPhone.find(
		        "select sp " 
		        		+ "from SmartPhone sp "
		                + "where sp.vendor.id = ?1",
		                vendorId
		).page(pageIndex, pageSize).list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public SmartPhone findPhoneById(@NotNull Long id) {
		Optional<SmartPhone> phone = SmartPhone.findByIdOptional(id);
		return phone.orElseThrow(() -> new NotFoundException());
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countAllPhones() {
		return SmartPhone.count();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<PanacheEntityBase> countPhonesPerStatus() {
		return SmartPhone.find(
		        "select status, count(sp.status) as total "
		                + "from SmartPhone sp " + "group by status"
		).list();

	}

	public void deletePhone(@NotNull Long id) {
		Panache.getEntityManager().getReference(SmartPhone.class, id).delete();
	}

	private String retrieveQrString(SmartPhone phone) {
		PanacheQuery<QrContent> query = SmartPhone.find("id", phone.id)
		        .project(QrContent.class);

		QrContent qrContent = query.singleResult();
		byte[]    code      = qrProxy.CreateQrString(qrContent);
		return Base64.getEncoder().encodeToString(code);
	}
}
