package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Department;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.Transfer;
import com.assets.management.assets.model.valueobject.SelectOptions;
import com.assets.management.assets.service.EmployeeService;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Path("/employees")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Employee Endpoint", description = "This API allows to keep track of all the employees assigned the assets")
public class EmployeeResource {

    @Context
    UriInfo uriInfo;
    @Inject
    Logger LOG;

    @Inject
    EmployeeService employeeService;

    @Inject
    LinkHeaderPagination headerPagination;

    @GET
    @Operation(summary = "Retrieves all available employees from the database")
    @APIResponses(
            {@APIResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Employee.class, type = SchemaType.ARRAY)),
                    description = "Lists all the employees"),
                    @APIResponse(responseCode = "204", description = "No employee to display"),
            })
    public Response listEmployees(
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer pageIndex,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("15") Integer pageSize,
            @Parameter(description = "Search string", required = false) @QueryParam("search") String search,
            @Parameter(description = "Search date", required = false) @QueryParam("date") LocalDate date,
            @Parameter(description = "Order property", required = false) @QueryParam("prop") @DefaultValue("firstName") String column,
            @Parameter(description = "Order direction", required = false) @QueryParam("order") @DefaultValue("asc") String direction
    ) {
        PanacheQuery<Employee> query = employeeService.listEmployees(search, date, column, direction);
        Page currentPage = Page.of(pageIndex, pageSize);
        query.page(currentPage);

        Long totalCount = query.count();
        List<Employee> employeesForCurrentPage = query.list();
        //      int lastPage = (int) ((totalCount + size - 1)); // uncomment to test
        int lastPage = query.pageCount();
        if (employeesForCurrentPage.size() == 0) return Response.status(Status.NO_CONTENT).build();

        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, pageSize, lastPage);

        return Response.ok(employeesForCurrentPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    @GET
    @Path("/{id}")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Returns the employee for a given identifier")
    @APIResponses({@APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class)), description = "Returns a found employee"), @APIResponse(responseCode = "400", description = "Invalid input"), @APIResponse(responseCode = "404", description = "Employee is not found for a given identifier")})
    public Response findEmployee(@Parameter(description = "Employee Identifier", required = true) @PathParam("id") @NotNull Long empId) {
        return employeeService.findById(empId).map(employee -> Response.ok(employee).build()).orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("/select")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieve only employee ID and first name from all employees in the database")
    @APIResponses({
            @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)),
                    description = "ID and first name for all employees available"),
            @APIResponse(responseCode = "204", description = "No employee available in the database")
    })
    public Response employeesSelectOptions() {
        List<SelectOptions> employees = Employee.find("SELECT e.id, e.firstName FROM Employee e LEFT JOIN e.department d LEFT JOIN e.address LEFT JOIN d.location")
                .project(SelectOptions.class).list();
        if (employees.size() == 0) return Response.status(Status.NO_CONTENT).build();

        return Response.ok(employees).build();
    }

    @POST
    @Operation(summary = "Creates a valid employee and stores it into the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
                    description = "URI of the created employee"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)),
                    description = "Employee duplications is not allowed"),
            @APIResponse(
                    responseCode = "404",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)),
                    description = "Specified department does not exist in the database")})
    public Response createEmployee(@RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class))) @Valid Employee employee, @Context UriInfo uriInfo) {
        if (employee.address == null || employee.department == null) return Response.status(Status.BAD_REQUEST).build();
        else if (Employee.checkByEmailAndPhone(employee.email, employee.mobile))
            if (Employee.checkByEmailAndPhone(employee.email, employee.mobile))
                return Response.status(Status.CONFLICT).entity("Email or Phone number is already taken").build();
            else if (!Department.findByIdOptional(employee.department.id).isPresent())
                return Response.status(Status.NOT_FOUND).entity("Department dont exists").build();

        employee = employeeService.addEmployee(employee);
        URI employeeUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(employee.id)).build();
        return Response.created(employeeUri).build();
    }

//    @POST
//    @Path("/{id}/allocates")
//    @Operation(summary = "Allocates an asset to employee")
//    @APIResponses({@APIResponse(responseCode = "201", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)), description = "URI of the allocation record"), @APIResponse(responseCode = "400", description = "Invalid input"), @APIResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)), description = "Duplicates is not allowed"), @APIResponse(responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)), description = "Employee to be assigned an asset or the asset does not exist")})
//    public Response allocateAsset(@Parameter(description = "Employee Identifier", required = true) @PathParam("id") @NotNull Long employeeId, @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Allocation.class))) @Valid Allocation allocation, @Context UriInfo uriInfo) {
//        if (allocation.asset == null || allocation.asset.id == null) return Response.status(Status.BAD_REQUEST).build();
//        else if (allocation.employee != null && !employeeId.equals(allocation.employee.id))
//            return Response.status(Response.Status.CONFLICT).entity(allocation.employee).build();
//
//        URI allocationURI;
//        try {
//            employeeService.allocateAsset(allocation, employeeId);
//            allocationURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(allocation.id)).build();
//
//            LOG.info("ALLOCATED ASSET ID: " + allocation.asset.id);
//            employeeService.updateAssetWithlabel(allocation.asset, allocationURI);
//        } catch (NotFoundException nf) {
//            return Response.status(Response.Status.NOT_FOUND).entity("Employee/Asset don't exist").build();
//        } catch (ClientErrorException ce) {
//            return Response.status(Status.CONFLICT).entity("Asset is already taken!").build();
//        }
//
//        return Response.created(allocationURI).build();
//    }

//    @POST
//    @Path("/{id}/transfers")
//    @Operation(summary = "Transfers an asset to another employee")
//    @APIResponses({@APIResponse(responseCode = "201", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)), description = "URI of the transfer record"), @APIResponse(responseCode = "400", description = "Invalid input"), @APIResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)), description = "Duplicates is not allowed"), @APIResponse(responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)), description = "Employee or asset does not exist")})
//    public Response transferAsset(
//            @Parameter(description = "Employee Identifier", required = true) @PathParam("id") @NotNull Long fromEmployeeId,
//            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
//                    schema = @Schema(implementation = Transfer.class))) @Valid Transfer transfer,
//            @Context UriInfo uriInfo) {
//        if (transfer.asset == null || transfer.asset.id == null) return Response.status(Status.BAD_REQUEST).build();
//        else if (transfer.prevCustodian == null || !fromEmployeeId.equals(transfer.prevCustodian.id))
//            return Response.status(Response.Status.CONFLICT).entity(transfer.prevCustodian).build();
//        else if (transfer.currentCustodian == null || transfer.currentCustodian.id == null)
//            return Response.status(Response.Status.BAD_REQUEST).entity("Include employee to transfer the asset").build();
//
//        URI transferURI = null;
//        String uriSubPath = "employees" + "/" + Long.toString(transfer.currentCustodian.id) + "/" + "allocates" + "/";
//
//        try {
//            Transfer transfered = employeeService.transferAsset(transfer, fromEmployeeId);
//            transferURI = uriInfo.getBaseUriBuilder().path(uriSubPath + Long.toString(transfer.id)).build();
//            LOG.info("TRANSFERED URI : " + transferURI.toString());
//            employeeService.updateTranferedAssetWithlabel(transfered, transferURI);
//        } catch (NoResultException ex) {
//            transfer = null;
//        } catch (NotFoundException nf) {
//            return Response.status(Response.Status.NOT_FOUND).entity("Employee don't exist").build();
//        } catch (BadRequestException br) {
//            return Response.status(Status.BAD_REQUEST).entity("Ensure Asset is transferred from the current custodian").build();
//        } catch (ClientErrorException ce) {
//            return Response.status(Status.CONFLICT).entity("Asset cannot be transferred!").build();
//        }
//
//        if (transfer == null) return Response.status(Response.Status.NOT_FOUND).entity("Asset was not found").build();
//        return Response.created(transferURI).build();
//    }
//
//    @GET
//    @Path("{id}/allocates")
//    @Operation(summary = "Retrieves details of all allocations per employee")
//    @APIResponses({@APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(oneOf = {Allocation.class, Transfer.class}, type = SchemaType.ARRAY)), description = "Lists all the employee's allocations"), @APIResponse(responseCode = "204", description = "Nothing to display"), @APIResponse(responseCode = "400", description = "Invalid input")})
//    public Response employeeAllAssets(@Parameter(description = "Employee Identifier", required = true) @PathParam("id") @NotNull Long employeeId, @Parameter(description = "Parameter for querying status", required = false) @QueryParam("status") AllocationStatus filteredStatus) {
//        List<Object> allocationsOrTransfers = employeeService.employeeAssets(filteredStatus, employeeId);
//        if (allocationsOrTransfers.size() == 0) return Response.status(Status.NO_CONTENT).build();
//
//        return Response.ok(allocationsOrTransfers).build();
//    }

//    @GET
//    @Produces("image/png")
//    @Path("{employeeId}/assets/{assetId}")
//    @Transactional(Transactional.TxType.SUPPORTS)
//    @Operation(summary = "Previews the QR Code image")
//    @APIResponses({@APIResponse(responseCode = "200", content = @Content(mediaType = "image/png", schema = @Schema(implementation = String.class, format = "binary")), description = "QR code image"), @APIResponse(responseCode = "204", description = "Nothing to display"), @APIResponse(responseCode = "400", description = "Invalid input")})
//    public Response employeeQRPreview(@Parameter(description = "Employee Identifier", required = true) @PathParam("employeeId") @NotNull Long employeeId, @Parameter(description = "Asset Identifier", required = true) @PathParam("assetId") @NotNull Long assetId) {
//        Allocation allocated = Allocation.preview(employeeId, assetId);
//        Transfer transferred = Transfer.assetForQRPreview(employeeId, assetId);
//
//        if (allocated == null && transferred == null) return Response.status(Status.NO_CONTENT).build();
//        return allocated == null ? Response.ok(transferred.asset.label.qrByteString).build() : Response.ok(allocated.asset.label.qrByteString).build();
//    }

//    @GET
//    @Path("/{employeeId}/allocates/{id}")
//    @Transactional(Transactional.TxType.SUPPORTS)
//    @Operation(summary = "Returns the scanned QR Code details")
//    @APIResponses({@APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(oneOf = {Allocation.class, Transfer.class})), description = "Encoded QR Code details"), @APIResponse(responseCode = "204", description = "No record found"),})
//    public Response getQRDetails(@Parameter(description = "Employee identifier", required = true) @PathParam("employeeId") @NotNull Long employeeId, @Parameter(description = "Transfer or Allocation identifier", required = true) @PathParam("id") @NotNull Long id) {
//        Allocation allocated = Allocation.qrDetails(employeeId, id);
//        Transfer transfered = Transfer.qrDetails(employeeId, id);
//
//        if (allocated == null && transfered == null) return Response.status(Status.NO_CONTENT).build();
//        return allocated == null ? Response.ok(transfered).build() : Response.ok(allocated).build();
//    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates an existing employee")
    @APIResponses({@APIResponse(responseCode = "204", description = "Employee has been successfully updated"), @APIResponse(responseCode = "400", description = "Invalid input"), @APIResponse(responseCode = "404", description = "Employee to be updated does not exist in the database"), @APIResponse(responseCode = "415", description = "Format is not JSON"), @APIResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class)), description = "Employee payload is not the same as an entity object that needed to be updated")})
    public Response updateEmployee(@Parameter(description = "Employee identifier", required = true) @PathParam("id") @NotNull Long empId, @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Employee.class))) @Valid Employee employee) {
        if (!empId.equals(employee.id)) return Response.status(Response.Status.CONFLICT).entity(employee).build();
        else if (employee.department == null) return Response.status(Status.BAD_REQUEST).build();

        employee.address = null;
        try {
            employeeService.updateEmployee(employee, empId);
        } catch (NotFoundException nf) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes an existing employee")
    @APIResponses({@APIResponse(responseCode = "204", description = "Employee has been successfully deleted"), @APIResponse(responseCode = "400", description = "Invalid input"), @APIResponse(responseCode = "404", description = "Employee to be deleted does not exist in the database"), @APIResponse(responseCode = "500", description = "Employee not found")})
    public Response deleteEmployee(@Parameter(description = "Employee identifier", required = true) @PathParam("id") @NotNull Long empId) {
        try {
            employeeService.deleteEmployee(empId);
        } catch (EntityNotFoundException nfe) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
