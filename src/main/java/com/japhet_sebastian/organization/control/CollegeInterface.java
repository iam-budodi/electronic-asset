package com.japhet_sebastian.organization.control;

import com.japhet_sebastian.organization.boundary.OrgPage;
import com.japhet_sebastian.organization.entity.CollegeDto;
import com.japhet_sebastian.vo.SelectOptions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface CollegeInterface {

    List<CollegeDto> listColleges(OrgPage orgPage);

    Optional<CollegeDto> getCollege(@NotNull String collegeId);

    Long totalColleges();

    List<SelectOptions> selected();

    void saveCollege(@Valid CollegeDto collegeDto);

    void updateCollege(@Valid CollegeDto collegeDto);

    void deleteCollege(@NotNull String collegeId);
}
