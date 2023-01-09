package se.sigma.boostapp.boost_app_java.converter;

public interface BoostAppConverter<T, U> {

    U convert(T from);
    
}
