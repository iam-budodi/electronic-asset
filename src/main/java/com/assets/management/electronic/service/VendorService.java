package com.assets.management.electronic.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.jboss.logging.Logger;

import com.assets.management.electronic.model.Vendor;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class VendorService {

	@Inject
	Logger LOG;
	
	public Vendor persistVendor(@Valid Vendor vendor) {
//		Vendor vendor = new Vendor();
//		vendor.companyName = "jeff";
//		vendor.contactPerson = "jeff";
//		vendor.escalationPerson = "jeff"; 
		
//		phone.generatedAt = Instant.now();
		
//		vendor.addPhone(phone);
		Vendor.persist(vendor);
		LOG.info("Vendor Persist: " + vendor);
		
		return vendor;
//		SmartPhone.persist(phone);
//		phone.qrString = retrieveQrString(phone);
//		return Panache.getEntityManager().merge(phone);
	}
}
