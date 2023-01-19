package com.assets.management.assets.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PurchaseTest {
	private static final LocalDate PURCHASE_DATE = LocalDate.of(2022, Month.JANUARY, 19);
	private static final Integer PURCHASE_QUANTITY = 5;
	private static final BigDecimal PERCHASE_PRICE = BigDecimal.valueOf(1850000);
	private static final String INVOICE_NUMBER = "UBX-123456PLC";
	
	private static final LocalDate UPDATED_PURCHASE_DATE = LocalDate.of(2023, Month.JANUARY, 25);
	private static final Integer UPDATED_PURCHASE_QUANTITY = 6;
	private static final BigDecimal UPDATED_PERCHASE_PRICE = BigDecimal.valueOf(1800000);
	private static final String UPDATED_INVOICE_NUMBER = "UBX-123456PLC - Updated";
	
	private static Long purchaseId;


	@Test
	@Order(1)
	void shouldPersistSupplier() {
		Purchase purchase = new Purchase();
		purchase.purchaseDate = PURCHASE_DATE;
		purchase.purchaseQty = PURCHASE_QUANTITY;
		purchase.purchasePrice = PERCHASE_PRICE;
		purchase.invoiceNumber = INVOICE_NUMBER;
		Purchase.persist(purchase);

		assertTrue(purchase.isPersistent());
		assertNotNull(purchase.id);

		purchaseId = purchase.id;
	}

//	@Test
//	@Order(2)
//	void shouldFindAll() {
//		List<Purchase> purchase = Purchase.listAll();
//		assertEquals(1, suppliers.size());
//		assertEquals(DEFAULT_NAME, suppliers.get(0).name);
//	}

//	@Test
//	@Order(3)
//	void shouldFindSupplier() {
//		PanacheQuery<Supplier> supplierQuery = Supplier.find("from Supplier sp");
//		List<Supplier> suppliers = supplierQuery.list();
//		Long nbSuppliers = supplierQuery.count();
//		Supplier firstSupplier = supplierQuery.firstResult();
//		Optional<Supplier> dept = supplierQuery.firstResultOptional();
//
//		assertEquals(1, suppliers.size());
//		assertEquals(1, nbSuppliers);
//		assertEquals(supplierId, firstSupplier.id);
//		assertEquals(DEFAULT_NAME, dept.get().name);
//
//	}
//
//	@Test
//	@Order(4)
//	void shouldUpdate() {
//		Supplier supplier = Supplier.findById(supplierId);
//		supplier.name = UPDATED_NAME;
//		supplier.email = UPDATED_EMAIL;
//		supplier.phone = UPDATED_PHONE;
//		supplier.registeredBy = UPDATED_REGISTERED_BY;
//		supplier.supplierType = SupplierType.WHOLESELLER;
//		supplier.description = UPDATED_DESCRIPTION;
//		
//		Panache.getEntityManager().merge(supplier); // this is not necessary can be commented out
//		assertFalse(DEFAULT_NAME.equals(supplier.name));
//		assertTrue(UPDATED_NAME.equals(supplier.name));
//		assertEquals(UPDATED_EMAIL, supplier.email);
//		assertEquals(UPDATED_PHONE, supplier.phone);
//		assertEquals(UPDATED_REGISTERED_BY, supplier.registeredBy);
//		assertEquals(SupplierType.WHOLESELLER, supplier.supplierType);
//		assertEquals(UPDATED_DESCRIPTION, supplier.description);
//	}
//	
//	@Test
//	@Order(5)
//	void shouldDelete() {
//		Supplier.findById(supplierId).delete();
//		assertEquals(0, Supplier.listAll().size());
//	}
}
