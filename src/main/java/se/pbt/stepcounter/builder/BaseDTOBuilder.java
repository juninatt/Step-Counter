package se.pbt.stepcounter.builder;

/**
 * The BaseDTOBuilder class is an abstract class that provides a template for building DTO (Data Transfer Objects) objects
 * using the builder pattern.
 *
 * @param <T> The type of the DTO object that the builder will create
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
