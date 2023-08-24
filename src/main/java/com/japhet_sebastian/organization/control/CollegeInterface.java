package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.entity.CollegeDetail;
import com.japhet_sebastian.vo.PageRequest;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface CollegeInterface {

    List<CollegeDetail> listColleges(PageRequest pageRequest);

    Optional<CollegeDetail> getCollege(@NotNull String collegeId);

    Long totalColleges();

    List<SelectOptions> selected();

    void addCollege(@Valid CollegeDetail collegeDetail);

    void updateCollege(@Valid CollegeDetail collegeDetail);

    void deleteCollege(@NotNull String collegeId);
}
