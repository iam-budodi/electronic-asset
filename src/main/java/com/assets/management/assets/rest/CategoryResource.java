package com.assets.management.assets.rest;

import com.assets.management.assets.model.entity.Category;
import com.assets.management.assets.model.valueobject.SelectOptions;
import com.assets.management.assets.util.metadata.LinkHeaderPagination;
import io.quarkus.hibernate.orm.panache.Panache;
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
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;


@Path("/categories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRED)
@SecurityRequirement(name = "Keycloak")
@Tag(name = "Category Endpoint", description = "This API allows to group and CRUD related assets categories")
public class CategoryResource {

    @Inject
    LinkHeaderPagination headerPagination;

    @GET
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Retrieves all asset categories from the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class, type = SchemaType.ARRAY)),
                    description = "Lists all the categories"),
            @APIResponse(responseCode = "204", description = "No categories to display"),
            @APIResponse(responseCode = "404", description = "Categories is not found for a given name query")
    })
    public Response listCategories(
            @Context UriInfo uriInfo,
            @Parameter(description = "Page index", required = false) @QueryParam("page") @DefaultValue("0") Integer page,
            @Parameter(description = "Page size", required = false) @QueryParam("size") @DefaultValue("5") Integer size,
            @Parameter(description = "Search string", required = false) @QueryParam("search") String search,
            @Parameter(description = "Order property", required = false) @QueryParam("prop") @DefaultValue("name") String column,
            @Parameter(description = "Order direction", required = false) @QueryParam("order") @DefaultValue("asc") String direction) {

        PanacheQuery<Category> query = Category.listCategories(search, column, direction);
        Page currentPage = Page.of(page, size);
        query.page(currentPage);

        Long totalCount = query.count();
        List<Category> categoryForCurrentPage = query.list();
        int lastPage = query.pageCount();
        if (categoryForCurrentPage.size() == 0)
            return Response.status(Status.NO_CONTENT).build();

        String linkHeader = headerPagination.linkStream(uriInfo, currentPage, size, lastPage);

        return Response.ok(categoryForCurrentPage)
                .header("Link", linkHeader)
                .header("X-Total-Count", totalCount)
                .build();
    }

    @GET
    @Path("/{id}")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Returns asset category for a given identifier")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class)),
                    description = "Returns a found category"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Categories is not found for a given identifier")
    })
    public Response findCategory(@Parameter(description = "Category identifier", required = true) @PathParam("id") @NotNull @Min(1) Long categoryId) {
        return Category.findByIdOptional(categoryId)
                .map(category -> Response.ok(category).build())
                .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("/select")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Fetch only categories ID and name for all categories available to be used for client side selection options")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = SelectOptions.class, type = SchemaType.ARRAY)),
                    description = "Categories ID and name as key value pair objects for the categories available"),
            @APIResponse(responseCode = "204", description = "No category available in the database")
    })
    public Response categorySelectOptions() {
        List<SelectOptions> categories = Category.find("SELECT c.id, c.name FROM Category c")
                .project(SelectOptions.class).list();
        if (categories.size() == 0) return Response.status(Status.NO_CONTENT).build();
        return Response.ok(categories).build();
    }

    @POST
    @Operation(summary = "Creates a valid category and stores it into the database")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)),
                    description = "URI of the created category"),
            @APIResponse(responseCode = "400", description = "Invalid input")
    })
    public Response insertCategory(
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class)))
            @Valid Category category, @Context UriInfo uriInfo) {
        Category.persist(category);
        URI categoryURI = uriInfo.getAbsolutePathBuilder().path(Long.toString(category.id)).build();
        return Response.created(categoryURI).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Updates an existing asset category")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Category has been successfully updated"),
            @APIResponse(responseCode = "404", description = "Category to be updated does not exist in the database"),
            @APIResponse(responseCode = "415", description = "Format is not JSON"),
            @APIResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class)),
                    description = "Category payload is not the same as an entity object that needed to be updated")
    })
    public Response updateCategory(
            @Parameter(description = "Category identifier", required = true) @PathParam("id") @NotNull @Min(1) Long categoryId,
            @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Category.class)))
            @Valid Category category) {
        if (!categoryId.equals(category.id))
            return Response.status(Response.Status.CONFLICT).entity(category).build();

        return Category.findByIdOptional(categoryId).map(found -> {
            Panache.getEntityManager().merge(category);
            return Response.status(Status.NO_CONTENT).build();
        }).orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes an existing asset category")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Category has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Category to be deleted does not exist in the database"),
            @APIResponse(responseCode = "500", description = "Category not found")
    })
    public Response deleteCategory(@Parameter(description = "Category identifier", required = true) @PathParam("id") @NotNull @Min(1) Long categoryId) {
        return Category.deleteById(categoryId)
                ? Response.noContent().build()
                : Response.status(Status.NOT_FOUND).build();
    }
}
