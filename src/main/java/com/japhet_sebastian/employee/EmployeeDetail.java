package com.japhet_sebastian.employee;

import java.util.Objects;

public class EmployeeDetail extends EmployeeBase {

    private String timeOfService;

    public String getTimeOfService() {
        return timeOfService;
    }

    public void setTimeOfService(String timeOfService) {
        this.timeOfService = timeOfService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeDetail that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getTimeOfService(), that.getTimeOfService());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTimeOfService());
    }

    @Override
    public String toString() {
        return "EmployeeDetail{" +
                "timeOfService='" + timeOfService + '\'' +
                '}';
    }
}
