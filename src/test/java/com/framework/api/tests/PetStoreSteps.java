package com.framework.api.tests;

import com.framework.api.endpoints.PetEndpoints;
import com.framework.api.models.Pet;
import com.framework.utils.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PetStoreSteps - Cucumber step definitions for PetStore API feature.
 *
 * <p>Response validation uses response.as(Pet.class) to deserialize into
 * Pet POJO, then validates each field using dedicated getters — no ObjectMapper,
 * no jsonPath string navigation.</p>
 *
 * <p>Design Patterns: Dependency Injection (PicoContainer), Builder (Pet payload)</p>
 */
public class PetStoreSteps {

    private static final Logger log = LogManager.getLogger(PetStoreSteps.class);

    private final ScenarioContext context;
    private final PetEndpoints petEndpoints;

    private Pet requestPet;     // Pet payload sent in request
    private Pet responsePet;    // Pet deserialized from response
    private Response response;

    public PetStoreSteps(ScenarioContext context) {
        this.context = context;
        this.petEndpoints = new PetEndpoints();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Given Steps
    // ─────────────────────────────────────────────────────────────────────────

    @Given("a new pet with name {string} and status {string}")
    public void aNewPetWithNameAndStatus(String name, String status) {
        long petId = generateUniquePetId();
        requestPet = new Pet.Builder()
                .id(petId)
                .name(name)
                .status(Pet.PetStatus.valueOf(status.toUpperCase()))
                .photoUrls("https://example.com/" + name.toLowerCase() + ".jpg")
                .category("Dogs")
                .build();

        context.set(ScenarioContext.Keys.PET_NAME, name);
        context.set(ScenarioContext.Keys.PET_ID, petId);
        log.info("Built pet request payload: {}", requestPet);
    }

    @Given("a pet exists in the system with name {string}")
    public void aPetExistsInTheSystemWithName(String name) {
        long petId = generateUniquePetId();
        Pet newPet = new Pet.Builder()
                .id(petId)
                .name(name)
                .status(Pet.PetStatus.AVAILABLE)
                .photoUrls("https://example.com/" + name.toLowerCase() + ".jpg")
                .build();

        Response createResponse = petEndpoints.addPet(newPet);

        // Deserialize response into Pet POJO and use getters for pre-condition check
        Pet created = createResponse.as(Pet.class);
        assertThat(createResponse.statusCode())
                .as("Pre-condition: pet creation should return 200")
                .isEqualTo(200);
        assertThat(created.getName())
                .as("Pre-condition: created pet name should match")
                .isEqualTo(name);

        context.set(ScenarioContext.Keys.PET_ID, created.getId());
        context.set(ScenarioContext.Keys.PET_NAME, created.getName());
        requestPet = newPet;

        log.info("Pre-condition pet created - id=[{}] name=[{}] status=[{}]",
                created.getId(), created.getName(), created.getStatus());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // When Steps
    // ─────────────────────────────────────────────────────────────────────────

    @When("the user sends a POST request to add the pet")
    public void theUserSendsAPostRequestToAddThePet() {
        log.info("POST /pet - adding pet: {}", requestPet.getName());
        response = petEndpoints.addPet(requestPet);

        // Deserialize immediately into Pet POJO
        if (response.statusCode() == 200) {
            responsePet = response.as(Pet.class);
            log.info("POST response deserialized - id=[{}] name=[{}] status=[{}]",
                    responsePet.getId(), responsePet.getName(), responsePet.getStatus());
        }

        context.set(ScenarioContext.Keys.RESPONSE, response);
        context.set(ScenarioContext.Keys.STATUS_CODE, response.statusCode());
    }

    @When("the user sends a GET request for the pet by ID")
    public void theUserSendsAGetRequestForThePetById() {
        Long petId = context.get(ScenarioContext.Keys.PET_ID, Long.class);
        log.info("GET /pet/{}", petId);
        response = petEndpoints.getPetById(petId);

        // Deserialize response into Pet POJO
        if (response.statusCode() == 200) {
            responsePet = response.as(Pet.class);
            log.info("GET response deserialized - id=[{}] name=[{}] status=[{}]",
                    responsePet.getId(), responsePet.getName(), responsePet.getStatus());
        }

        context.set(ScenarioContext.Keys.RESPONSE, response);
        context.set(ScenarioContext.Keys.STATUS_CODE, response.statusCode());
    }

    @When("the user sends a GET request for pet ID {long}")
    public void theUserSendsAGetRequestForPetId(long petId) {
        log.info("GET /pet/{} (non-existent)", petId);
        response = petEndpoints.getPetById(petId);
        context.set(ScenarioContext.Keys.RESPONSE, response);
        context.set(ScenarioContext.Keys.STATUS_CODE, response.statusCode());
    }

    @When("the user updates the pet status to {string}")
    public void theUserUpdatesThePetStatusTo(String newStatus) {
        Long petId   = context.get(ScenarioContext.Keys.PET_ID, Long.class);
        String petName = context.get(ScenarioContext.Keys.PET_NAME, String.class);

        Pet updatedPet = new Pet.Builder()
                .id(petId)
                .name(petName)
                .status(Pet.PetStatus.valueOf(newStatus.toUpperCase()))
                .photoUrls("https://example.com/updated.jpg")
                .build();

        log.info("PUT /pet - updating pet [{}] status to [{}]", petId, newStatus);
        response = petEndpoints.updatePet(updatedPet);

        // Deserialize updated response
        if (response.statusCode() == 200) {
            responsePet = response.as(Pet.class);
            log.info("PUT response deserialized - id=[{}] name=[{}] status=[{}]",
                    responsePet.getId(), responsePet.getName(), responsePet.getStatus());
        }

        context.set(ScenarioContext.Keys.RESPONSE, response);
        context.set(ScenarioContext.Keys.STATUS_CODE, response.statusCode());
    }

    @When("the user sends a DELETE request for the pet")
    public void theUserSendsADeleteRequestForThePet() {
        Long petId = context.get(ScenarioContext.Keys.PET_ID, Long.class);
        log.info("DELETE /pet/{}", petId);
        response = petEndpoints.deletePet(petId);
        context.set(ScenarioContext.Keys.RESPONSE, response);
        context.set(ScenarioContext.Keys.STATUS_CODE, response.statusCode());
    }

    @When("the user searches for pets with status {string}")
    public void theUserSearchesForPetsWithStatus(String status) {
        Pet.PetStatus petStatus = Pet.PetStatus.valueOf(status.toUpperCase());
        log.info("GET /pet/findByStatus?status={}", status);
        response = petEndpoints.findPetsByStatus(petStatus);
        context.set(ScenarioContext.Keys.RESPONSE, response);
        context.set(ScenarioContext.Keys.STATUS_CODE, response.statusCode());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Then Steps - validate using Pet POJO getters
    // ─────────────────────────────────────────────────────────────────────────

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedCode) {
        int actualCode = context.get(ScenarioContext.Keys.STATUS_CODE, Integer.class);
        assertThat(actualCode)
                .as("Response status code")
                .isEqualTo(expectedCode);
        log.info("Status code verified: expected=[{}] actual=[{}]", expectedCode, actualCode);
    }

    @And("the response should contain the pet name {string}")
    public void theResponseShouldContainThePetName(String expectedName) {
        // Validate via getter - no jsonPath string navigation
        assertThat(responsePet).as("Deserialized Pet should not be null").isNotNull();
        assertThat(responsePet.getName())
                .as("Pet name from response getter")
                .isEqualTo(expectedName);
        log.info("Pet.getName() verified: [{}]", responsePet.getName());
    }

    @And("the response should contain status {string}")
    public void theResponseShouldContainStatus(String expectedStatus) {
        // Validate via getter - no jsonPath string navigation
        assertThat(responsePet).as("Deserialized Pet should not be null").isNotNull();
        assertThat(responsePet.getStatus())
                .as("Pet status from response getter")
                .isEqualToIgnoringCase(expectedStatus);
        log.info("Pet.getStatus() verified: [{}]", responsePet.getStatus());
    }

    @And("the response should return a list of pets")
    public void theResponseShouldReturnAListOfPets() {
        // Deserialize as Pet array and validate via getters
        Pet[] pets = response.as(Pet[].class);
        assertThat(pets)
                .as("Response should return a non-empty pet array")
                .isNotNull()
                .isNotEmpty();

        // Log first pet details using getters
        Pet first = pets[0];
        log.info("First pet in list - id=[{}] name=[{}] status=[{}]",
                first.getId(), first.getName(), first.getStatus());

        // Validate each pet has at minimum a name via getter
        for (Pet pet : pets) {
            assertThat(pet.getId())
                    .as("Every pet in list should have an id")
                    .isNotNull();
        }

        log.info("Pet list returned with [{}] items", pets.length);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private long generateUniquePetId() {
        return ThreadLocalRandom.current().nextLong(100_000L, 999_999L);
    }
}
