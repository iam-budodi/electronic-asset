package com.assets.management.electronic.service;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.assets.management.electronic.client.QrProxy;
import com.assets.management.electronic.model.Computer;
import com.assets.management.electronic.model.QrContent;
import com.assets.management.electronic.model.SmartPhone;
import com.assets.management.electronic.model.Status;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class DeviceService {

	@Inject
	Logger LOG; 
	
	@Inject
	@RestClient
	QrProxy qrProxy;

	public SmartPhone persistPhone(@Valid SmartPhone phone)  {
		LOG.info("Received Phone: " + phone);
		
		phone.generatedAt = Instant.now();  
		SmartPhone.persist(phone); 
		PanacheQuery<QrContent> query = SmartPhone.find("id", phone.id).project(QrContent.class);
		 
		QrContent qrContent = query.singleResult(); 
		byte[] qrCode = qrProxy.CreateQrString(qrContent); 
		String qrString = Base64.getEncoder().encodeToString(qrCode);
		phone.qrString = qrString;  
		 
		return phone;
	}

	public byte[] persistComputer(@Valid Computer computer)  {
//		computer.qrString = genQrString(computer);
		Computer.persist(computer);
		return Base64.getDecoder().decode(computer.qrString);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<SmartPhone> findAllPhones() {
		return SmartPhone.listAll();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public SmartPhone findPhoneById(Long id) {
		Optional<SmartPhone> phone = SmartPhone.findByIdOptional(id);
		return phone.orElseThrow(() -> new NotFoundException());
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<SmartPhone> listAvailablePhones() {
		return SmartPhone.list("status", Status.Available);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<SmartPhone> listTakenPhones() {
		return SmartPhone.list("status", Status.Taken);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<SmartPhone> listFaultyPhones() {
		return SmartPhone.list("status", Status.Faulty);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<SmartPhone> listEOLPhones() {
		return SmartPhone.list("status", Status.EOL);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countAllPhones() {
		return SmartPhone.count();
	}

	public SmartPhone updatePhone(@Valid SmartPhone phone, Long id) {
		SmartPhone sPhone = Panache.getEntityManager().getReference(
				SmartPhone.class, id
		);

		Panache.getEntityManager().merge(phone);
		return sPhone;
	}

	public void deletePhone(Long id) {
		SmartPhone phone = Panache.getEntityManager().getReference(
				SmartPhone.class, id
		);

		phone.delete();
	} 
}
