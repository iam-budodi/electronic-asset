package com.japhet_sebastian.employee;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Objects;

public class ReportPage {

    @RestQuery("start")
    @Parameter(description = "Start Date", required = false)
    private LocalDate startDate;

    @RestQuery("end")
    @Parameter(description = "End Date", required = false)
    private LocalDate endDate;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportPage that)) return false;
        return Objects.equals(getStartDate(), that.getStartDate()) && Objects.equals(getEndDate(), that.getEndDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartDate(), getEndDate());
    }
}
