package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.AddressEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class AddressRepository implements PanacheRepositoryBase<AddressEntity, UUID> {
}
