package com.assets.management.assets.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Employee;
import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.ItemAssignment;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class AssignmentService {

	@Inject
	Logger LOG;

	// FIX you dont assign same item more than once
	public ItemAssignment assignItem(
			@Valid ItemAssignment assignment, @NotNull Long empId, @NotNull Long itemId) {
		Employee.findByIdOptional(empId).map(
				employee -> assignment.employee = (Employee) employee)
		.orElseThrow(() -> new NotFoundException());
		
		Item.findByIdOptional(itemId).map(
				item -> assignment.item = (Item) item)
		.orElseThrow(() -> new NotFoundException());
//
//		asset.endUser = endUser;
//		asset.employDate = Instant.now();
//		Panache.getEntityManager().merge(item);
		ItemAssignment.persist(assignment);
		return assignment;
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Item> getAssignedItems(Long empId) {
		return ItemAssignment.find("employee.id = ?1", empId).list();
	}
//
//	public void unAssignAsset(@NotNull Long candidateId, String serialNumber) {
//		Item asset = Item.find("endUser.id = ?1 and serialNumber = ?2", candidateId, serialNumber).firstResult();
//		if (asset == null)
//			throw new NotFoundException("Record not found!");
////		asset.endUser = null;
//
//		Panache.getEntityManager().merge(asset);
//	}
}
