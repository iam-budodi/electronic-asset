package com.assets.management.assets.model.entity;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PurchaseTest {
    private static final LocalDate PURCHASE_DATE = LocalDate.of(2023, Month.JANUARY, 19);
    private static final Integer PURCHASE_QUANTITY = 5;
    private static final BigDecimal PURCHASE_PRICE = BigDecimal.valueOf(1850000).setScale(2, RoundingMode.HALF_UP);
    private static final String INVOICE_NUMBER = "UBX-123456PLC";

    private static final LocalDate UPDATED_PURCHASE_DATE = LocalDate.of(2023, Month.JANUARY, 20);
    private static final Integer UPDATED_PURCHASE_QUANTITY = 6;
    private static final BigDecimal UPDATED_PURCHASE_PRICE = PURCHASE_PRICE;
    private static final String UPDATED_INVOICE_NUMBER = "UBX-123456PLC - Updated";

    private static Long purchaseId;

    @Test
    @Order(1)
    void shouldAddPurchase() {
//		Purchase purchase = new Purchase();
        Purchase purchase = new Purchase();
        purchase.purchaseDate = PURCHASE_DATE;
        purchase.purchaseQty = PURCHASE_QUANTITY;
        purchase.purchasePrice = PURCHASE_PRICE;
        purchase.invoiceNumber = INVOICE_NUMBER;
        Purchase.persist(purchase);

        assertTrue(purchase.isPersistent());
        assertNotNull(purchase.id);

        purchaseId = purchase.id;
    }

    @Test
    @Order(2)
    void shouldlistAll() {
        List<Purchase> purchases = Purchase.listAll();
        assertThat(purchases)
                .hasSizeGreaterThanOrEqualTo(1)
                .filteredOn(purchase -> purchase.invoiceNumber.contains(INVOICE_NUMBER))
                .hasSize(1);
    }

    @Test
    @Order(3)
    void shouldFindPurchaseById() {
        Purchase purchase = Purchase.findById(purchaseId);
        assertThat(purchase.purchaseDate).hasYear(PURCHASE_DATE.getYear())
                .hasMonth(PURCHASE_DATE.getMonth())
                .hasDayOfMonth(PURCHASE_DATE.getDayOfMonth());

    }

    @Test
    @Order(4)
    void shouldCheckPurchaseByInvoice() {
        Optional<Purchase> optionalPurchase = Purchase.findByInvoice(INVOICE_NUMBER);
        assertThat(optionalPurchase)
                .isNotEmpty()
                .containsInstanceOf(Purchase.class)
                .hasValueSatisfying(
                        purchase -> {
                            assertThat(purchase.purchasePrice).isEqualTo(PURCHASE_PRICE);
                            assertThat(purchase.purchaseQty).isEqualTo(PURCHASE_QUANTITY);
                        }
                );
    }


    @Test
    @Order(5)
    void shouldCountAll() {
        assertThat(Purchase.count())
                .isNotNegative()
                .isNotZero()
                .isGreaterThanOrEqualTo(1L);
    }

    @Test
    @Order(6)
    void shouldUpdatePurchase() {
        Purchase purchase = new Purchase();
        purchase.id = purchaseId;
        purchase.purchaseDate = UPDATED_PURCHASE_DATE;
        purchase.purchaseQty = UPDATED_PURCHASE_QUANTITY;
        purchase.purchasePrice = UPDATED_PURCHASE_PRICE;
        purchase.invoiceNumber = UPDATED_INVOICE_NUMBER;
        Panache.getEntityManager().merge(purchase);

        assertThat(purchase)
                .satisfies(p -> {
                            assertThat(p.purchaseDate).isEqualTo(UPDATED_PURCHASE_DATE);
                            assertThat(p.purchaseQty).isEqualTo(UPDATED_PURCHASE_QUANTITY);
                            assertThat(p.purchasePrice).isEqualTo(UPDATED_PURCHASE_PRICE);
                            assertThat(p.invoiceNumber).isEqualTo(UPDATED_INVOICE_NUMBER);
                        }
                );
    }

    @Test
    @Order(7)
    void shouldDelete() {
        assertThat(Purchase.deleteById(purchaseId)).isTrue();
    }

    @Test
    @Order(8)
    void shouldNotRetrieveDeletedRecord() {
        List<Purchase> purchases = Purchase.listAll();
        assertThat(purchases).extracting("id").doesNotContain(purchaseId);
    }
}
