package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CollegeService implements CollegeInterface {

    @Inject
    CollegeRepository collegeRepository;

    public List<CollegeDetail> listColleges(OrgPage orgPage) {
        return this.collegeRepository.allColleges(orgPage);
    }

    public Optional<CollegeDetail> getCollege(@NotNull String collegeId) {
        return this.collegeRepository.singleCollege(collegeId);
    }

    public Long totalColleges() {
        return collegeRepository.count();
    }

    public List<SelectOptions> selected() {
        return collegeRepository.selectProjection();
    }

    public void addCollege(@Valid CollegeDetail collegeDetail) {
        this.collegeRepository.saveCollege(collegeDetail);
    }

    public void updateCollege(@Valid CollegeDetail collegeDetail) {
        this.collegeRepository.updateCollege(collegeDetail);
    }

    public void deleteCollege(@NotNull String collegeId) {
        this.collegeRepository.deleteCollege(collegeId);
    }
}