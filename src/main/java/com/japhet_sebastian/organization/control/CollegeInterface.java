package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.PageRequest;
import com.japhet_sebastian.organization.entity.College;
import com.japhet_sebastian.organization.entity.CollegeAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface CollegeInterface {

    List<College> listColleges(PageRequest pageRequest);

    Optional<CollegeAddress> getCollege(@NotNull String collegeId);

    Long totalColleges();

    void addCollege(@Valid CollegeAddress collegeAddress);

    void updateCollege(@Valid College college);

    Boolean deleteCollege(@NotNull String collegeId);

}
