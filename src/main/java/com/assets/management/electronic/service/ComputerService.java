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
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.assets.management.electronic.client.QrProxy;
import com.assets.management.electronic.model.Computer;
import com.assets.management.electronic.model.QrContent;
import com.assets.management.electronic.model.SmartPhone;
import com.assets.management.electronic.model.Vendor;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class ComputerService {

	@Inject
	Logger LOG;

	@Inject
	@RestClient
	QrProxy qrProxy;

	public Computer persistComputer(
	        @Valid Computer computer,
	        @NotNull Long vendorId
	) {
		Optional<Vendor> optional = Vendor.findByIdOptional(vendorId);
		LOG.info("Is Vendor Present " + optional.get());
		Vendor vendor = optional.orElseThrow(() -> new BadRequestException());

		computer.vendor = vendor;
		computer.generatedAt = Instant.now();

		SmartPhone.persist(computer);
		computer.qrString = retrieveQrString(computer);
		return Panache.getEntityManager().merge(computer);
	}

	public Computer updateComputer(
	        @Valid Computer computer,
	        @NotNull Long id
	) {
		Computer pc = Panache.getEntityManager()
		        .getReference(Computer.class, id);
		computer.qrString = retrieveQrString(pc);
		computer.updatedAt = Instant.now();

		return Panache.getEntityManager().merge(computer);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Computer> allComputersByVendor(
	        Long vendorId,
	        Integer pageIndex,
	        Integer pageSize
	) {
		return SmartPhone
		        .find(
		                "select pc " + "from Computer pc "
		                        + "where pc.vendor.id = ?1",
		                vendorId
		        ).page(pageIndex, pageSize).list();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Computer findComputerById(@NotNull Long id) {
		Optional<Computer> phone = Computer.findByIdOptional(id);
		return phone.orElseThrow(() -> new NotFoundException());
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public Long countAllComputers() {
		return Computer.count();
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<PanacheEntityBase> countComputerPerStatus() {
		return Computer.find(
		        "select pc.status, count(pc.status) as total "
		                + "from Computer pc group by pc.status"
		).list();

	}

	public void deleteComputer(@NotNull Long id) {
		Panache.getEntityManager().getReference(Computer.class, id).delete();
	}

	public Long deleteAllComputer(@NotNull Long vendorId) {
		Optional<Vendor> optional = Vendor.findByIdOptional(vendorId);
		Vendor           vendor   = optional
		        .orElseThrow(() -> new BadRequestException());
		return Computer.deleteAll();
	}

	private String retrieveQrString(@Valid Computer computer) {
		PanacheQuery<QrContent> query = Computer.find("id", computer.id)
		        .project(QrContent.class);

		QrContent qrContent = query.singleResult();
		byte[]    code      = qrProxy.CreateQrString(qrContent);
		return Base64.getEncoder().encodeToString(code);
	}
}
