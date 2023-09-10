package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.exception.ServiceException;
import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.AddressEntity;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.organization.entity.CollegeEntity;
import com.japhet_sebastian.vo.SelectOptions;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CollegeRepository implements PanacheRepositoryBase<CollegeEntity, UUID> {

    @Inject
    CollegeMapper collegeMapper;

    @Inject
    AddressRepository addressRepository;

    public List<CollegeDetail> allColleges(OrgPage orgPage) {
        if (orgPage.getSearch() != null)
            orgPage.setSearch("%" + orgPage.getSearch().toLowerCase(Locale.ROOT) + "%");
        String query = "FROM College c LEFT JOIN FETCH c.address " +
                "WHERE :search IS NULL OR LOWER(c.collegeName) LIKE :search " +
                "OR LOWER(c.collegeCode) LIKE :search";

        return find(query, Sort.by("c.collegeName"), Parameters.with("search", orgPage.getSearch()))
                .page(Page.of(orgPage.getPageNumber(), orgPage.getPageSize()))
                .stream().map(this.collegeMapper::toCollegeDetail)
                .collect(Collectors.toList());
    }

    public Optional<CollegeDetail> singleCollege(@NotNull String collegeId) {
        return find(collegeQuery(), UUID.fromString(collegeId)).firstResultOptional()
                .map(this.collegeMapper::toCollegeDetail);
    }

    public List<SelectOptions> selectProjection() {
        return find("SELECT ce.collegeId, ce.collegeName FROM College ce")
                .project(SelectOptions.class)
                .list();
    }

    public void saveCollege(@Valid CollegeDetail collegeDetail) {
        AddressEntity addressEntity = this.collegeMapper.toAddressEntity(collegeDetail);
        this.addressRepository.persist(addressEntity);

        CollegeEntity collegeEntity = this.collegeMapper.toCollegeEntity(collegeDetail);
        collegeEntity.setAddress(addressEntity);
        persist(collegeEntity);

        this.collegeMapper.updateCollegeDetailFromCollegeEntity(collegeEntity, collegeDetail);
    }

    public void updateCollege(@Valid CollegeDetail collegeDetail) {
        String collegeId = collegeDetail.getCollegeId();
        CollegeEntity collegeEntity = find(collegeQuery(), UUID.fromString(collegeId)).firstResultOptional()
                .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", collegeId));

        this.collegeMapper.updateCollegeEntityFromCollegeDetail(collegeDetail, collegeEntity);
        persist(collegeEntity);

        this.addressRepository.findByIdOptional(collegeEntity.getCollegeId()).map(addressEntity -> {
            this.collegeMapper.updateAddressEntityFromCollegeDetail(collegeDetail, addressEntity);
            addressEntity.setAddressId(collegeEntity.getCollegeId());
            this.addressRepository.persist(addressEntity);
            return addressEntity;
        });

        collegeMapper.updateCollegeDetailFromCollegeEntity(collegeEntity, collegeDetail);
    }

    public void deleteCollege(@NotNull String collegeId) {
        CollegeEntity collegeEntity = find(collegeQuery(), UUID.fromString(collegeId)).firstResultOptional()
                .orElseThrow(() -> new ServiceException("No college found for collegeId[%s]", collegeId));

        AddressEntity addressEntity = collegeEntity.getAddress();
        delete(collegeEntity);
        this.addressRepository.delete(addressEntity);
    }

    private String collegeQuery() {
        return "FROM College c LEFT JOIN FETCH c.address WHERE collegeId = ?1";
    }
}
