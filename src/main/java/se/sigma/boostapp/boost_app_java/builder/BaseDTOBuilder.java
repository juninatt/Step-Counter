package se.sigma.boostapp.boost_app_java.builder;

/**
 * The BaseDTOBuilder class is an abstract class that serves as a base for building
 * DTO objects using the builder pattern. It provides a template for creating the DTO object
 * and a build method that returns the DTO object.
 *
 * @param <T> The type of the DTO object that the builder will create
 */
abstract class BaseDTOBuilder<T> {

    /**
     *  The dto instance to be returned after building
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
