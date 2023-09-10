package com.japhet_sebastian.employee;

import com.japhet_sebastian.supplier.SupplierDto;
import com.japhet_sebastian.supplier.SupplierPage;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface EmployeeInterface {
    List<SupplierDto> listSuppliers(SupplierPage supplierPage);

    Optional<SupplierDto> findSupplier(@NotNull String supplierId);

    Long supplierCount();

    List<SelectOptions> selectOptions();

    void saveSupplier(@Valid SupplierDto supplierDto);

    void updateSupplier(@Valid SupplierDto supplierDto);

    void deleteSupplier(@NotNull String supplierId);

}
