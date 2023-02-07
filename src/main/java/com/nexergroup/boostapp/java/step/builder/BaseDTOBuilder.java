package com.nexergroup.boostapp.java.step.builder;

/**
 * The BaseDTOBuilder class is an abstract class that provides a template for building DTO (Data Transfer Objects) objects
 * DTO(Data Transfer Objects) using the builder pattern. It provides a template for creating the DTO objects
 * and a build method that returns the final DTO object after building.
 *
 * @param <T> The type of the DTO object that the builder will create
 * @see com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO
 * @see com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO
 * @see com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO
 */
abstract class BaseDTOBuilder<T> {

    /**
     *  The DTO instance to be returned after building
     */
    protected T dto;

    /**
     * Creates an instance of the DTO object and assigns it to the dto field
     */
    public BaseDTOBuilder() {
        this.dto = createDto();
    }

    /**
     * Creates an instance of the DTO object
     *
     * @return An instance of the DTO object
     */
    protected abstract T createDto();

    /**
     * Returns the DTO object after building
     *
     * @return The DTO object
     */
    public T build() {
        return dto;
    }
}
