package com.japhet_sebastian.vo;

import jakarta.ws.rs.DefaultValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestQuery;

@Getter
@Setter
@EqualsAndHashCode
public class PageRequest {

    @RestQuery("page")
    @DefaultValue("0")
    @Parameter(description = "Page index")
    private Integer pageNumber;

    @RestQuery("size")
    @DefaultValue("10")
    @Parameter(description = "Page size")
    private Integer pageSize;

    @RestQuery("search")
    @Parameter(description = "Search parameter")
    private String search;


}
