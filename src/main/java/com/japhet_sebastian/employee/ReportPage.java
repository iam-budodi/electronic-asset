package com.japhet_sebastian.employee;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class ReportPage {

    @RestQuery("start")
    @Parameter(description = "startDate", required = true)
    private LocalDate startDate;

    @RestQuery("end")
    @Parameter(description = "endDate", required = true)
    private LocalDate endDate;

}
