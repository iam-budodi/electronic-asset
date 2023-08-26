package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.vo.PageRequest;
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


    // TODO :::: Rewrite everything since address can be obtained directly from the college entity then delete all address implementations
    public List<CollegeDetail> listColleges(PageRequest pageRequest) {
        return this.collegeRepository.allColleges(pageRequest);
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


// return this.collegeRepository.allColleges(pageRequest)
//         .stream()
//         .map(college -> {
//         String collegeId = college.getCollegeId();
//         Address address = this.addressRepository.findAddress(collegeId)
//         .orElseThrow(() -> new ServiceException("No address found for collegeId[%s]", collegeId));
//         return this.collegeMapper.toCollegeDetail(college, address);
//         }).collect(Collectors.toList());


//return this.collegeRepository.findByIdOptional(UUID.fromString(collegeId))
//        .stream()
//        .map(collegeEntity -> {
//        String collegeStrId = collegeEntity.getCollegeId().toString();
//        Address address = this.addressRepository.findAddress(collegeStrId)
//        .orElseThrow(() -> new ServiceException("No address found for collegeId[%s]", collegeStrId));
//        return this.collegeMapper.toCollegeDetail(this.collegeMapper.toCollege(collegeEntity), address);
//        }).findFirst();