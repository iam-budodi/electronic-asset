package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.SupplierType;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SupplierTest {
    private static final String DEFAULT_NAME = "Technology Associate";
    private static final String DEFAULT_EMAIL = "technology.associate@technologyassociate.co.tz";
    private static final String DEFAULT_PHONE = "0744 111 789";
    private static final String DEFAULT_REGISTERED_BY = "Japhet";
    private static final String DEFAULT_DESCRIPTION = "Technology associates";
    private static final String UPDATED_NAME = "Technology Associate -  updated";
    private static final String UPDATED_EMAIL = "technology.associate@technologyassociate-inc.com";
    private static final String UPDATED_PHONE = "0744-111-789";
    private static final String UPDATED_REGISTERED_BY = "Japhet - updated";
    private static final String UPDATED_DESCRIPTION = "Technology associates (updated)";
    private static Long supplierId;

    @Test
    @Order(1)
    void shouldPersistSupplier() {
        Supplier supplier = new Supplier();
        supplier.name = DEFAULT_NAME;
        supplier.email = DEFAULT_EMAIL;
        supplier.phone = DEFAULT_PHONE;
        supplier.registeredBy = DEFAULT_REGISTERED_BY;
        supplier.supplierType = SupplierType.RETAILER;
        supplier.description = DEFAULT_DESCRIPTION;
        Supplier.persist(supplier);

        assertTrue(supplier.isPersistent());
        assertNotNull(supplier.id);

        supplierId = supplier.id;
    }

    @Test
    @Order(2)
    void shouldFindAll() {
        List<Supplier> suppliers = Supplier.listAll();
        assertTrue(suppliers.size() >= 1);
    }

    @Test
    @Order(3)
    void shouldFindSupplier() {
        PanacheQuery<Supplier> supplierQuery = Supplier.find("from Supplier sp");
        List<Supplier> suppliers = supplierQuery.list();
        Long nbSuppliers = supplierQuery.count();
        Supplier firstSupplier = supplierQuery.firstResult();
        Optional<Supplier> optSupplier = supplierQuery.firstResultOptional();

        assertTrue(suppliers.size() >= 1);
        assertEquals(suppliers.size(), nbSuppliers);
        assertEquals(supplierId, firstSupplier.id);
        assertThat(optSupplier)
                .isNotEmpty()
                .map(supplier -> supplier.email)
                .contains(DEFAULT_EMAIL);
    }

    @Test
    @Order(4)
    void shouldUpdate() {
        Supplier supplier = Supplier.findById(supplierId);
        supplier.name = UPDATED_NAME;
        supplier.email = UPDATED_EMAIL;
        supplier.phone = UPDATED_PHONE;
        supplier.registeredBy = UPDATED_REGISTERED_BY;
        supplier.supplierType = SupplierType.WHOLESALER;
        supplier.description = UPDATED_DESCRIPTION;

        Panache.getEntityManager().merge(supplier); // this is not necessary can be commented out
        assertFalse(DEFAULT_NAME.equals(supplier.name));
        assertTrue(UPDATED_NAME.equals(supplier.name));
        assertEquals(UPDATED_EMAIL, supplier.email);
        assertEquals(UPDATED_PHONE, supplier.phone);
        assertEquals(UPDATED_REGISTERED_BY, supplier.registeredBy);
        assertEquals(SupplierType.WHOLESALER, supplier.supplierType);
        assertEquals(UPDATED_DESCRIPTION, supplier.description);
    }

    @Test
    @Order(5)
    void shouldDelete() {
        boolean deleted = Supplier.deleteById(supplierId);
        assertThat(deleted).isTrue();
    }

}
