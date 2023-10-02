package com.japhet_sebastian.employee;

import com.japhet_sebastian.vo.PageRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class EmployeePage extends PageRequest {

    @RestQuery("date")
    @Parameter(description = "Date parameter")
    private LocalDate date;
}
