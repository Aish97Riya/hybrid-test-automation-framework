package com.framework.api.endpoints;

import com.framework.api.client.ApiClient;
import com.framework.api.models.Pet;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

/**
 * PetEndpoints - REST endpoint methods for PetStore /pet resource.
 *
 * <p>SOLID: Single Responsibility - only defines HTTP interactions for /pet.</p>
 * <p>SOLID: Open/Closed - new endpoints added without modifying existing methods.</p>
 * <p>SOLID: Interface Segregation - extends {@link ApiEndpoint} contract.</p>
 *
 * <p>All methods return raw {@link Response} to keep assertions in test layer (separation of concerns).</p>
 */
public class PetEndpoints implements ApiEndpoint {

    private static final Logger log = LogManager.getLogger(PetEndpoints.class);

    // ─────────────────────────────────────────────────────────────────────────
    // Endpoint constants
    // ─────────────────────────────────────────────────────────────────────────

    public static final String PET_BASE        = "/pet";
    public static final String PET_BY_ID       = "/pet/{petId}";
    public static final String PET_BY_STATUS   = "/pet/findByStatus";
    public static final String PET_UPLOAD_IMAGE = "/pet/{petId}/uploadFile";

    // ─────────────────────────────────────────────────────────────────────────
    // CRUD Operations
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * POST /pet - Add a new pet.
     *
     * @param pet Pet payload
     * @return Response
     */
    public Response addPet(Pet pet) {
        log.info("POST /pet - Adding pet: {}", pet.getName());
        return given()
                .spec(ApiClient.getRequestSpec())
                .body(pet)
                .when()
                .post(PET_BASE)
                .then()
                .log().ifError()
                .extract().response();
    }

    /**
     * GET /pet/{petId} - Retrieve pet by ID.
     *
     * @param petId pet identifier
     * @return Response
     */
    public Response getPetById(long petId) {
        log.info("GET /pet/{} - Fetching pet", petId);
        return given()
                .spec(ApiClient.getRequestSpec())
                .pathParam("petId", petId)
                .when()
                .get(PET_BY_ID)
                .then()
                .log().ifError()
                .extract().response();
    }

    /**
     * PUT /pet - Update an existing pet.
     *
     * @param pet updated Pet payload
     * @return Response
     */
    public Response updatePet(Pet pet) {
        log.info("PUT /pet - Updating pet: {}", pet.getId());
        return given()
                .spec(ApiClient.getRequestSpec())
                .body(pet)
                .when()
                .put(PET_BASE)
                .then()
                .log().ifError()
                .extract().response();
    }

    /**
     * DELETE /pet/{petId} - Delete a pet by ID.
     *
     * @param petId pet identifier
     * @return Response
     */
    public Response deletePet(long petId) {
        log.info("DELETE /pet/{} - Deleting pet", petId);
        return given()
                .spec(ApiClient.getRequestSpec())
                .pathParam("petId", petId)
                .when()
                .delete(PET_BY_ID)
                .then()
                .log().ifError()
                .extract().response();
    }

    /**
     * GET /pet/findByStatus - Find pets by status.
     *
     * @param status Pet.PetStatus value
     * @return Response
     */
    public Response findPetsByStatus(Pet.PetStatus status) {
        log.info("GET /pet/findByStatus?status={}", status);
        return given()
                .spec(ApiClient.getRequestSpec())
                .queryParam("status", status.name().toLowerCase())
                .when()
                .get(PET_BY_STATUS)
                .then()
                .log().ifError()
                .extract().response();
    }
}
