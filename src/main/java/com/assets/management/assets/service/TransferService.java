package com.assets.management.assets.service;

import javax.validation.constraints.NotNull;

import com.assets.management.assets.model.ItemAssignment;

public class TransferService {

	// only query params pass here 
	public ItemAssignment transferItem(@NotNull Long transFromId, @NotNull Long transToId) {
		// check transferTo if exista
		// check and retrieve Item to be transfered 
		// transfer item
		// flag item as transfered in the ItemAssignment table
		
		
		
		
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
		
		return null;
	}
}
