package com.onyx.exception;

/**
 * Created by timothy.osborn on 12/4/14.
 *
 * Trying to save of fetch on an attribute that does not exist on the entity or is
 * not specified as an entity
 *
 */
public class AttributeMissingException extends EntityException{

    public static final String ENTITY_MISSING_ATTRIBUTE = "Entity attribute does not exist";
    public static final String ILLEGAL_ACCESS_ATTRIBUTE = "Illegal access for attribute";

    @SuppressWarnings("unused")
    public AttributeMissingException()
    {

    }

    /**
     * Constructor with message and cause
     *
     * @param message Error message
     * @param cause Root cause
     */
    public AttributeMissingException(@SuppressWarnings("SameParameterValue") String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor with message
     *
     * @param message Error message
     */
    public AttributeMissingException(String message)
    {
        super(message);
    }
}
