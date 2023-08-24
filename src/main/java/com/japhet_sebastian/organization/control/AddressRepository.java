package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.Address;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.vo.PageRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AddressRepository implements PanacheRepositoryBase<AddressEntity, UUID> {

    @Inject
    AddressMapper addressMapper;

    public List<AddressEntity> pageOrListAll(PageRequest pageRequest) {
        return findAll()
                .page(Page.of(pageRequest.getPageNum(), pageRequest.getPageSize()))
                .list();
    }

    public Optional<Address> findAddress(String collegeId) {
        return find("addressId = ?1", UUID.fromString(collegeId))
                .firstResultOptional()
                .map(this.addressMapper::toAddress);
    }
}
