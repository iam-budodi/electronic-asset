package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.PageRequest;
import com.japhet_sebastian.organization.entity.Address;
import com.japhet_sebastian.organization.entity.AddressEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
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
        return find("SELECT a FROM Address a LEFT JOIN FETCH a.college c " +
                        "WHERE :value IS NULL OR LOWER(c.collegeName) LIKE :value OR LOWER(c.collegeCode) LIKE :value",
                Sort.by("c.collegeName", Sort.Direction.Descending),
                Parameters.with("value", pageRequest.getSearch()))
                .page(Page.of(pageRequest.getPageNum(), pageRequest.getPageSize()))
                .list();
    }

    public Optional<Address> findAddress(String collegeId) {
        return find("FROM Address a LEFT JOIN FETCH a.college c " +
                "WHERE c.collegeId = ?1", UUID.fromString(collegeId))
                .firstResultOptional()
                .map(this.addressMapper::toAddress);
    }
}
