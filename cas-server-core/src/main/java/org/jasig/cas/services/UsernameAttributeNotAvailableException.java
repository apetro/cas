package org.jasig.cas.services;

import org.jasig.cas.ticket.TicketException;

/**
 * Exceptional condition wherein the attribute a service is configured to use as the username attribute is no longer
 * available.  Conveys the service experiencing the problem and the name of the user attribute that is unavailable.
 */
public class UsernameAttributeNotAvailableException
        extends TicketException {

    public static final String CODE = "USERNAME_ATTRIBUTE_NOT_AVAILABLE";

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDesiredUserAttribute() {
        return desiredUserAttribute;
    }

    public void setDesiredUserAttribute(String desiredUserAttribute) {
        this.desiredUserAttribute = desiredUserAttribute;
    }

    private String service;

    private String desiredUserAttribute;

    public UsernameAttributeNotAvailableException(String service, String desiredUserAttribute) {
        super(CODE);
        this.service = service;
        this.desiredUserAttribute = desiredUserAttribute;
    }

}