package com.assets.management.assets.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assets.management.assets.model.entity.Address;
import com.assets.management.assets.model.entity.Supplier;
import com.assets.management.assets.model.valueobject.SupplierType;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SupplierServiceTest {
    private static final String DEFAULT_NAME = "Technology Associate";
    private static final String DEFAULT_EMAIL = "technology.associate@technologyassociate.co.tz";
    private static final String DEFAULT_PHONE = "+(255) 744 111 789";
    private static final String DEFAULT_REGISTERED_BY = "Japhet";
    private static final String DEFAULT_DESCRIPTION = "Technology associates";
    private static final String UPDATED_NAME = "Technology Associate -  updated";
    private static final String UPDATED_EMAIL = "technology.associate@technologyassociate-inc.com";
    private static final String UPDATED_PHONE = "(255) 744 111 789";
    private static final String UPDATED_REGISTERED_BY = "Japhet - updated";
    private static final String UPDATED_DESCRIPTION = "Technology associates (updated)";

    private static final String DEFAULT_STREET = "Mikocheni";
    private static final String DEFAULT_WARD = "Msasani";
    private static final String DEFAULT_DISTRICT = "Kinondoni";
    private static final String DEFAULT_CITY = "Dar es Salaam";
    private static final String DEFAULT_POSTAL_CODE = "14110";
    private static final String DEFAULT_COUNTRY = "Tanzania";

    private static final Integer DEFAULT_PAGE_INDEX = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 15;
    private static Long supplierId;

    @Inject
    SupplierService supplierService;

    @Test
    @Order(1)
    void shouldFetchOrReturnEmptySupplierList() {
        Long size = Supplier.count();
        List<Supplier> suppliers = supplierService.listSuppliers(DEFAULT_PAGE_INDEX, DEFAULT_PAGE_SIZE);
//		assertEquals(size, Long.valueOf(suppliers.size()));
        assertThat(String.valueOf(size), is(String.valueOf(suppliers.size())));
    }

    @Test
    @Order(2)
    void shouldNotGetUnknownSupplier() {
        Long randomId = new Random().nextLong();
        Optional<Supplier> supplier = supplierService.findSupplier(randomId);
        assertFalse(supplier.isPresent());
    }

    @Test
    @Order(3)
    void shouldCreateSupplier() {
        Address address = new Address();
        address.street = DEFAULT_STREET;
        address.ward = DEFAULT_WARD;
        address.district = DEFAULT_DISTRICT;
        address.city = DEFAULT_CITY;
        address.postalCode = DEFAULT_POSTAL_CODE;
        address.country = DEFAULT_COUNTRY;

        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;
        supplier.address = address;

        assertFalse(supplier.isPersistent());
        supplier = supplierService.createSupplier(supplier);
        supplierId = supplier.id;

        assertNotNull(supplierId);
        assertEquals(DEFAULT_NAME, supplier.name);
        assertEquals(DEFAULT_EMAIL, supplier.email);
        assertEquals(DEFAULT_PHONE, supplier.phone);
        assertEquals(DEFAULT_REGISTERED_BY, supplier.registeredBy);
        assertEquals(SupplierType.RETAILER, supplier.supplierType);
        assertEquals(DEFAULT_DESCRIPTION, supplier.description);
        assertEquals(DEFAULT_DESCRIPTION, supplier.description);
    }

    @Test
    @Order(4)
    void shouldFetchSupplierList() {
        final int size = Supplier.listAll().size();
        List<Supplier> suppliers = supplierService.listSuppliers(DEFAULT_PAGE_INDEX, DEFAULT_PAGE_SIZE);
        assertEquals(size, suppliers.size());
    }

    @Test
    @Order(5)
    void shouldGetSupplier() {
        Optional<Supplier> supplier = supplierService.findSupplier(supplierId);
        assertTrue(supplier.isPresent());
        assertEquals(DEFAULT_NAME, supplier.get().name);
        assertEquals(DEFAULT_EMAIL, supplier.get().email);
        assertEquals(DEFAULT_PHONE, supplier.get().phone);
        assertEquals(DEFAULT_REGISTERED_BY, supplier.get().registeredBy);
        assertEquals(SupplierType.RETAILER, supplier.get().supplierType);
        assertEquals(DEFAULT_DESCRIPTION, supplier.get().description);
    }

    @Test
    @Order(6)
    void shouldUpdateSupplier() {
        Address address = new Address();
        address.street = DEFAULT_STREET;
        address.ward = DEFAULT_WARD;
        address.district = DEFAULT_DISTRICT;
        address.city = DEFAULT_CITY;
        address.postalCode = DEFAULT_POSTAL_CODE;
        address.country = DEFAULT_COUNTRY;

        Supplier supplier = new Supplier();
        supplier.id = supplierId;
        supplier.name = UPDATED_NAME;
        supplier.email = UPDATED_EMAIL;
        supplier.phone = UPDATED_PHONE;
        supplier.registeredBy = UPDATED_REGISTERED_BY;
        supplier.supplierType = SupplierType.WHOLESELLER;
        supplier.description = UPDATED_DESCRIPTION;
        supplier.address = address;

        supplierService.updateSupplier(supplier, supplierId);

        supplier = supplierService.findSupplier(supplierId).get();
        assertTrue(supplier.isPersistent());
        assertEquals(supplierId, supplier.id);
        assertFalse(DEFAULT_NAME.equals(supplier.name));
        assertTrue(UPDATED_NAME.equals(supplier.name));
        assertEquals(UPDATED_EMAIL, supplier.email);
        assertEquals(UPDATED_PHONE, supplier.phone);
        assertEquals(UPDATED_REGISTERED_BY, supplier.registeredBy);
        assertEquals(SupplierType.WHOLESELLER, supplier.supplierType);
        assertEquals(UPDATED_DESCRIPTION, supplier.description);
    }

    @Test
    @Order(7)
    void shouldDeleteSupplier() {
        supplierService.deleteSupplier(supplierId);
        Optional<Supplier> supplier = supplierService.findSupplier(supplierId);
        assertFalse(supplier.isPresent());
    }

    @Test
    @Order(8)
    void shouldThrowExceptionOnInsertingNullSupplierObject() {
        Supplier supplier = new Supplier();
        ConstraintViolationException thrown = assertThrows(
                ConstraintViolationException.class,
                () -> supplierService.createSupplier(supplier));
        assertEquals(null, thrown.getCause());

    }

    @Test
    @Order(9)
    void shouldThrowExceptionOnSupplyingNullValuesForRequiredFields() {
        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = null;
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;

        ConstraintViolationException thrown = assertThrows(
                ConstraintViolationException.class,
                () -> supplierService.createSupplier(supplier));
        assertEquals(null, thrown.getCause());

    }

    @Test
    @Order(10)
    void shouldThrowExceptionFetchingSupplierByNullId() {
        assertThrows(ConstraintViolationException.class,
                () -> supplierService.findSupplier(null));
    }

    @Test
    @Order(11)
    void shouldThrowNotFoundExceptionUponUpdate() {
        Supplier supplier = new Supplier();
        supplier.id = supplierId;
        supplier.name = UPDATED_NAME;
        supplier.email = UPDATED_EMAIL;
        supplier.phone = UPDATED_PHONE;
        supplier.registeredBy = UPDATED_REGISTERED_BY;
        supplier.supplierType = SupplierType.WHOLESELLER;
        supplier.description = UPDATED_DESCRIPTION;

        Long randomId = new Random().nextLong();
        assertThrows(NotFoundException.class, () -> {
            supplierService.updateSupplier(supplier, randomId);
        });
    }

    @Test
    @Order(12)
    void shouldThrowConstraintsViolationExceptionWhenSupplyingNullValuesForRequiredFields() {
        Supplier supplier = new Supplier();
        supplier.id = supplierId;
        supplier.name = UPDATED_NAME;
        supplier.email = UPDATED_EMAIL;
        supplier.phone = null;
        supplier.registeredBy = UPDATED_REGISTERED_BY;
        supplier.supplierType = SupplierType.WHOLESELLER;
        supplier.description = UPDATED_DESCRIPTION;

        assertThrows(ConstraintViolationException.class,
                () -> supplierService.updateSupplier(supplier, supplierId));
    }

    @Test
    @Order(13)
    void shouldThrowNotFoundExceptionForInvalidId() {
        Long randomId = new Random().nextLong();
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> supplierService.deleteSupplier(randomId));
        assertEquals(
                "Unable to find com.assets.management.assets.model.entity.Supplier with id "
                        + randomId,
                thrown.getMessage());
    }

    @Test
    @Order(14)
    void shouldThrowConstraintViolationExceptionForNullId() {
        assertThrows(ConstraintViolationException.class,
                () -> supplierService.deleteSupplier(null));
    }

    @Test
    @Order(15)
    void shouldInvalidCharaterOnNamesWhenCreateSupplier() {
        Supplier supplier = new Supplier();
        supplier.name = "Japhet$";
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;

        assertThrows(ConstraintViolationException.class,
                () -> supplierService.createSupplier(supplier));
    }

    @Test
    @Order(16)
    void shouldInvalidCharaterOnEmailWhenCreateSupplier() {
        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = "technology$associate@technologyassociate-inc.com";
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;

        assertThrows(ConstraintViolationException.class,
                () -> supplierService.createSupplier(supplier));
    }

    @Test
    @Order(17)
    void shouldInvalidCharaterOnMobileNumberWhenCreateSupplier() {
        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = "(255)744.111.789";
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;

        assertThrows(ConstraintViolationException.class,
                () -> supplierService.createSupplier(supplier));
    }

    @Test
    @Order(18)
    void shouldInvalidCharaterOnDescriptionWhenCreateSupplier() {
        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = "Japhet gave &500";

        assertThrows(ConstraintViolationException.class,
                () -> supplierService.createSupplier(supplier));
    }
}
