package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.Peripheral;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ComputerTest {
    // COMPUTERS
    private static final String BRAND = "Lenovo";
    private static final String MODEL_NAME = "ThinkBook 14 G2 ITL";
    private static final String MODEL_NUMBER = "MWNE2LL/A";
    private static final String SERIAL_NUMBER = "C8QCFJ6FN72J";
    private static final String PROCESSOR = "11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz (8 CPUs), ~2.4GHz";
    private static final String MANUFACTURER = "Lenovo";
    private static final Integer MEMORY = 8192;
    private static final Integer STORAGE = 1048576;
    private static final Integer GRAPHIC_CARD = 1500;
    private static final Double DISPLAY_SIZE = 14d;
    private static final Set<Peripheral> PERIPHERALS = Stream.of(Peripheral.MOUSE, Peripheral.KEYBOARD)
            .collect(Collectors.toCollection(HashSet::new));

    // UPDATED
    private static final String UPDATED_MODEL_NAME = "ThinkBook 14 G2 ITL - UPDATED";
    private static final String UPDATED_MODEL_NUMBER = "MPNXB192709Z";
    private static final String UPDATED_SERIAL_NUMBER = "C8QCFJ6FN72J - UPDATED";

    // PURCHASES
    private static final LocalDate PURCHASE_DATE = LocalDate.of(2022, Month.JANUARY, 19);
    private static final Integer PURCHASE_QUANTITY = 4;
    private static final BigDecimal PURCHASE_PRICE = BigDecimal.valueOf(1850000).setScale(2, RoundingMode.HALF_UP);
    private static final String INVOICE_NUMBER = "TA-123456PLC";

    private static Long computerId;
    private static Long purchaseId;

    @Test
    @Order(1)
    void shouldAddComputer() {
        Purchase purchase = new Purchase();
        purchase.purchaseDate = PURCHASE_DATE;
        purchase.purchaseQty = PURCHASE_QUANTITY;
        purchase.purchasePrice = PURCHASE_PRICE;
        purchase.invoiceNumber = INVOICE_NUMBER;

        Computer computer = new Computer();
        computer.brand = BRAND;
        computer.model = MODEL_NAME;
        computer.modelNumber = MODEL_NUMBER;
        computer.serialNumber = SERIAL_NUMBER;
        computer.manufacturer = MANUFACTURER;
        computer.processor = PROCESSOR;
        computer.memory = MEMORY;
        computer.storage = STORAGE;
        computer.graphicsCard = GRAPHIC_CARD;
        computer.displaySize = DISPLAY_SIZE;
        computer.peripherals = PERIPHERALS;
        computer.purchase = purchase;

        Computer.persist(purchase, computer);

        assertThat(computer)
                .satisfies(c -> {
                            assertThat(computer.isPersistent()).isTrue();
                            assertThat(computer.id).isNotNull().isPositive();
                            assertThat(c.purchase).isNotNull();
                        }
                );

        computerId = computer.id;
        purchaseId = computer.purchase.id;
    }

    @Test
    @Order(2)
    void shouldlistAll() {
        List<Computer> computers = Computer.listAll();
        assertThat(computers)
                .hasSizeGreaterThanOrEqualTo(1)
                .filteredOn(computer -> computer.serialNumber.contains(SERIAL_NUMBER))
                .hasSize(1);
    }

    @Test
    @Order(3)
    void shouldFindPurchaseById() {
        Computer computer = Computer.findById(computerId);
        assertThat(computer.purchase.purchaseDate).hasYear(PURCHASE_DATE.getYear())
                .hasMonth(PURCHASE_DATE.getMonth())
                .hasDayOfMonth(PURCHASE_DATE.getDayOfMonth());

    }

    @Test
    @Order(4)
    void shouldFindComputerById() {
        Optional<Computer> optionalComputer = Computer.findByIdOptional(computerId);
        assertThat(optionalComputer)
                .isNotEmpty()
                .containsInstanceOf(Computer.class)
                .hasValueSatisfying(
                        computer -> {
                            assertThat(computer.serialNumber).isEqualTo(SERIAL_NUMBER);
                            assertThat(computer.brand).isEqualTo(BRAND);
                            assertThat(computer.purchase).isInstanceOf(Purchase.class).isNotNull();
                        }
                );
    }

    @Test
    @Order(5)
    void shouldCheckIfExists() {
        assertThat(Computer.checkSerialNumber(SERIAL_NUMBER)).isTrue();
    }

    @Test
    @Order(6)
    void shouldCountAll() {
        assertThat(Computer.count())
                .isNotNegative()
                .isNotZero()
                .isGreaterThanOrEqualTo(1L);
    }

    @Test
    @Order(7)
    void shouldUpdatePurchase() {
        Purchase purchase = new Purchase();
        purchase.id = purchaseId;
        purchase.purchaseDate = PURCHASE_DATE;
        purchase.purchaseQty = PURCHASE_QUANTITY;
        purchase.purchasePrice = PURCHASE_PRICE;
        purchase.invoiceNumber = INVOICE_NUMBER;

        Computer computer = new Computer();
        computer.id = computerId;
        computer.brand = BRAND;
        computer.model = UPDATED_MODEL_NAME;
        computer.modelNumber = UPDATED_MODEL_NUMBER;
        computer.serialNumber = UPDATED_SERIAL_NUMBER;
        computer.manufacturer = MANUFACTURER;
        computer.processor = PROCESSOR;
        computer.memory = MEMORY;
        computer.storage = STORAGE;
        computer.graphicsCard = GRAPHIC_CARD;
        computer.displaySize = DISPLAY_SIZE;
        computer.peripherals = PERIPHERALS;
        computer.purchase = purchase;

        Panache.getEntityManager().merge(computer);

        assertThat(computer)
                .satisfies(c -> {
                            assertThat(c.brand).isEqualTo(BRAND);
                            assertThat(c.model).isEqualTo(UPDATED_MODEL_NAME).contains("UPDATED");
                            assertThat(c.serialNumber).isEqualTo(UPDATED_SERIAL_NUMBER);
                            assertThat(c.modelNumber).isEqualTo(UPDATED_MODEL_NUMBER).doesNotContain("UPDATED");
                            assertThat(c.purchase).isNotNull();
                        }
                );
    }

    @Test
    @Order(8)
    void shouldDeleteComputer() {
        assertThat(Computer.deleteById(computerId)).isTrue();
    }

    @Test
    @Order(9)
    void shouldDeletePurchase() {
        assertThat(Purchase.deleteById(purchaseId)).isTrue();
    }

    @Test
    @Order(10)
    void shouldNotRetrieveDeletedRecord() {
        List<Computer> computers = Computer.listAll();
        assertThat(computers).extracting("id").doesNotContain(computerId);
    }

}
