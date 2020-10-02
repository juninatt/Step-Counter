package se.sigma.boostapp.boost_app_java.exception;

import java.util.UUID;
/**
 * 
 * Not found exception
 *
 */
public class NotFoundException extends RuntimeException{
    public NotFoundException() {
        super("Not found");

    }
}
