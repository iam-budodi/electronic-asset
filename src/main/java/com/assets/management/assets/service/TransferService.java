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
import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.ItemAssignment;
import com.assets.management.assets.model.Label;
import com.assets.management.assets.model.Status;
import com.assets.management.assets.model.TransferHistory;
import com.assets.management.assets.util.QrCodeClient;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class TransferService {

	@Inject
	QrCodeClient qrCodeClient;
	
	// only query params pass here 
	public TransferHistory transferItem(
			@Valid TransferHistory history, 
			// @NotNull Long transFromId, 
			@NotNull Long itemId, 
			@NotNull Long transToId) {
		
		// check transferTo if exista
		Optional<Employee> transferTo = Employee.findByIdOptional(transToId); 
		Employee newCustodian = transferTo.orElseThrow(() -> new NotFoundException("User dont exists"));
		// check and retrieve Item to be transfered 
		Optional<ItemAssignment> found = ItemAssignment.hasItem(itemId).firstResultOptional();
		ItemAssignment assignment = found.orElseThrow(() -> new NotFoundException("Item was not assigned to any user"));
		assignment.employee = newCustodian;
		assignment.status = AssignmentStatus.Transfered;
		assignment.item.transferCount += 1;
		assignment.label.itemQrString = qrCodeClient.formatQrImgToString(assignment.itemSerialNumber);
		
//		assignment.label.itemAssignment = assignment;
//		assignment.label.id = assignment.id;
		Panache.getEntityManager().merge(assignment);
//		if (ItemAssignment.isItemAssigned(itemId)) 
		// transfer item
		// flag item as transfered in the ItemAssignment table and increment count
		// pass item id instead of transfromid then identify transfrom from the itemid
		
		history.item = assignment.item;
		TransferHistory.persist(history);
		
//		Employee.findByIdOptional(empId).map(
//				employee -> assignment.employee = (Employee) employee)
//		.orElseThrow(() -> new NotFoundException());
//		
//		Item.findByIdOptional(itemId).map(
//				item -> assignment.item = (Item) item)
//		.orElseThrow(() -> new NotFoundException());
//		
//		Label label = new Label();
//		label.itemQrString = "dummy".getBytes();
//		assignment.label = label;
//		assignment.item.status = Status.InUse;
//		
//		assignment.label.itemAssignment = assignment;
//		assignment.label.id = assignment.id;
//		
//		ItemAssignment.persist(assignment);
////		assignment.label.itemQrString = qrCodeClient.formatQrImgToString(assignment.itemSerialNumber);
//		Panache.getEntityManager().merge(assignment);
//		
//		return assignment;
		
		return history;
	}
}
