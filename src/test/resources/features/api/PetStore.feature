# ============================================================
# Feature: PetStore API - Pet Resource
# API: https://petstore.swagger.io/v2
# Tags: @API @Smoke @Regression
# ============================================================

@API
Feature: PetStore API - Pet CRUD Operations
  As an API consumer
  I want to create, read, update and delete pets in PetStore
  So that I can validate the API contract

  @Smoke @API_Pet
  Scenario: Add a new pet to the store
    Given a new pet with name "Buddy" and status "available"
    When the user sends a POST request to add the pet
    Then the response status code should be 200
    And the response should contain the pet name "Buddy"
    And the response should contain status "available"

  @Smoke @API_Pet
  Scenario: Retrieve a pet by ID
    Given a pet exists in the system with name "Fluffy"
    When the user sends a GET request for the pet by ID
    Then the response status code should be 200
    And the response should contain the pet name "Fluffy"

  @Regression @API_Pet
  Scenario: Update an existing pet's status
    Given a pet exists in the system with name "Max"
    When the user updates the pet status to "sold"
    Then the response status code should be 200
    And the response should contain status "sold"

  @Regression @API_Pet
  Scenario: Delete a pet from the store
    Given a pet exists in the system with name "Rocky"
    When the user sends a DELETE request for the pet
    Then the response status code should be 200

  @Smoke @API_Pet
  Scenario: Find pets by available status
    When the user searches for pets with status "available"
    Then the response status code should be 200
    And the response should return a list of pets

  @Regression @API_Pet
  Scenario: Get a non-existent pet returns 404
    When the user sends a GET request for pet ID 999999999
    Then the response status code should be 404
