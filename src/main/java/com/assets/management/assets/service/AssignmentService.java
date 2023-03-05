package com.assets.management.assets.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.Item;
import com.assets.management.assets.model.entity.ItemAssignment;
import com.assets.management.assets.model.entity.QRCode;
import com.assets.management.assets.model.valueobject.Status;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class AssignmentService {

	@Inject
	Logger LOG;
	
	@Transactional(Transactional.TxType.SUPPORTS)
	public List<ItemAssignment> getAllAssignments(Integer page, Integer size) {
		return ItemAssignment.find("ORDER BY dateAssigned, qtyAssigned")
				.page(page, size)
				.list();
	}
	
	public ItemAssignment assignItem(
			@Valid ItemAssignment assignment, @NotNull Long empId, @NotNull Long itemId) {
		Employee.findByIdOptional(empId).map(
				employee -> assignment.employee = (Employee) employee)
		.orElseThrow(() -> new NotFoundException());
		
		Item.findByIdOptional(itemId).map(
				item -> assignment.item = (Item) item)
		.orElseThrow(() -> new NotFoundException());
		
		QRCode label = new QRCode();
//		label.qrByteString = "dummy".getBytes();
//		assignment.label = label;
		assignment.item.status = Status.InUse;
		assignment.item.transferCount = 0;
//		
//		assignment.label.itemAssignment = assignment;
//		assignment.label.id = assignment.id;
		
		ItemAssignment.persist(assignment);
//		assignment.label.itemQrString = qrCodeClient.formatQrImgToString(assignment.itemSerialNumber);
		Panache.getEntityManager().merge(assignment);
		
		return assignment;
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Item> getAssignedItems(Long empId) {
		return ItemAssignment.find(
				"SELECT i.item "
				+ "FROM ItemAssignment i "
				+ "WHERE i.employee.id = ?1 "
				+ "AND i.item.status <> ?2", empId, Status.Transfered)
				.list();
	}

	public void unassignItem(@NotNull Long empId, @NotNull String sNumber) {
		ItemAssignment found = ItemAssignment.find(
				"employee.id = ?1 AND itemSerialNumber = ?2", 
				empId, sNumber)
				.firstResult();
		
		if (found == null)
			throw new NotFoundException("Record not found!");
		
		found.delete();
	}
	
	public void updateAssignment(@Valid ItemAssignment assignment, @NotNull Long assignmentId) {
		ItemAssignment.findByIdOptional(assignmentId).map(
		        itemFound -> Panache.getEntityManager().merge(assignment)
		).orElseThrow(
				() -> new NotFoundException("Not assigned"));
	} 
}
