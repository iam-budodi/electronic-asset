package com.assets.management.assets.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import com.assets.management.assets.model.AssignmentStatus;
import com.assets.management.assets.model.Employee;
import com.assets.management.assets.model.ItemAssignment;
import com.assets.management.assets.model.TransferHistory;
import com.assets.management.assets.util.QrCodeClient;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class TransferService {

	@Inject
	QrCodeClient qrCodeClient;
	 
	public TransferHistory transferItem(
			@Valid TransferHistory history,
			@NotNull Long itemId, 
			@NotNull Long transToId) {
		 
		Optional<Employee> transferTo = Employee.findByIdOptional(transToId); 
		Employee newCustodian = transferTo
				.orElseThrow(() -> new NotFoundException("Employee doesn't exists"));
		Optional<ItemAssignment> found = ItemAssignment.hasItem(itemId).firstResultOptional();
		ItemAssignment assignment = found
				.orElseThrow(() -> new NotFoundException("Item was not assigned to any user"));
		history.transferedFromEmployee = assignment.employee;
		assignment.employee = newCustodian;
		assignment.status = AssignmentStatus.Transfered;
		assignment.item.transferCount += 1;
		assignment.label.itemQrString = qrCodeClient.formatQrImgToString(assignment.itemSerialNumber);
		
		Panache.getEntityManager().merge(assignment);
		
		history.transferedToEmployee = newCustodian;
		history.item = assignment.item;
		TransferHistory.persist(history);
		
		return history;
	}
}
