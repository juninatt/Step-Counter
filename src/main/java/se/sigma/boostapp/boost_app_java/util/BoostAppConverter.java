package se.sigma.boostapp.boost_app_java.util;

public interface BoostAppConverter<T, U> {

    U convert(T from);
    
}
