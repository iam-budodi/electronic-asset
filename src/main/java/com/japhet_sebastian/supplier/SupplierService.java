package com.japhet_sebastian.supplier;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.control.AddressRepository;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SupplierService implements SupplierInterface {

    @Inject
    SupplierRepository supplierRepository;

    @Inject
    AddressRepository addressRepository;

    @Inject
    SupplierMapper supplierMapper;

    public List<SupplierDto> listSuppliers(SupplierPage supplierPage) {
        return supplierMapper.toListDto(supplierRepository.allSuppliers(supplierPage).list());
    }

    public Optional<SupplierDto> findSupplier(@NotNull String supplierId) {
        return supplierRepository.findSupplier(supplierId).map(supplierMapper::toDto);
    }

    public Long supplierCount() {
        return supplierRepository.count();
    }

    public List<SelectOptions> selectOptions() {
        return supplierRepository.selectProjection();
    }

    public void saveSupplier(@Valid SupplierDto supplierDto) {
        supplierRepository.searchByEmailOrPhone(supplierDto.companyEmail, supplierDto.companyPhone)
                .ifPresent(employeeEntity -> {
                    throw new ServiceException("Email/phone number is taken");
                });

        SupplierEntity supplierEntity = supplierMapper.toSupplierEntity(supplierDto);
        addressRepository.persist(supplierEntity.getAddress());
        supplierRepository.persist(supplierEntity);
        supplierMapper.partialDtoUpdate(supplierEntity, supplierDto);
    }

    public void updateSupplier(@Valid SupplierDto supplierDto) {
        SupplierEntity supplierEntity = checkSupplier(supplierDto.supplierId);
        supplierEntity = supplierMapper.partialEntityUpdate(supplierDto, supplierEntity);
        supplierEntity.getAddress().setAddressId(supplierEntity.getSupplierId());
        addressRepository.persist(supplierEntity.getAddress());
        supplierRepository.persist(supplierEntity);
        supplierMapper.partialDtoUpdate(supplierEntity, supplierDto);
    }

    public void deleteSupplier(@NotNull String supplierId) {
        SupplierEntity supplierEntity = checkSupplier(supplierId);
        AddressEntity addressEntity = supplierEntity.getAddress();
        supplierRepository.delete(supplierEntity);
        addressRepository.delete(addressEntity);
    }

    private SupplierEntity checkSupplier(String supplierId) {
        return supplierRepository.findSupplier(supplierId)
                .orElseThrow(() -> new ServiceException("No supplier found for supplierId[%s]", supplierId));
    }
}
