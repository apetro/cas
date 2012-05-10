/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.services.web.support;

import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.services.persondir.IPersonAttributeDao;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * RegisteredServiceValidator ensures that a new RegisteredService does not have
 * a conflicting Service Id with another service already in the registry.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 */
public final class RegisteredServiceValidator implements Validator {

    /** Default length, which matches what is in the view. */
    private static final int DEFAULT_MAX_DESCRIPTION_LENGTH = 300;

    /** ServiceRegistry to look up services. */
    @NotNull
    private ServicesManager servicesManager;

    /** The maximum length of the description we will accept. */
    @Min(0)
    private int maxDescriptionLength = DEFAULT_MAX_DESCRIPTION_LENGTH;

    @NotNull
    private IPersonAttributeDao personAttributeDao;

    /**
     * Supports RegisteredService objects.
     * 
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(final Class clazz) {
        return RegisteredService.class.isAssignableFrom(clazz);
    }

    public void validate(final Object o, final Errors errors) {
        final RegisteredService r = (RegisteredService) o;

        if (r.getServiceId() != null) {
            for (final RegisteredService service : this.servicesManager
                .getAllServices()) {
                if (r.getServiceId().equals(service.getServiceId())
                    && r.getId() != service.getId()) {
                    errors.rejectValue("serviceId",
                        "registeredService.serviceId.exists", null);
                    break;
                }
            }
        }

        if (r.getDescription() != null
            && r.getDescription().length() > this.maxDescriptionLength) {
            errors.rejectValue("description",
                "registeredService.description.length", null);
        }

        // validate that the alternate username attribute is either the meta-value
        // '(default)', the meta-value '(generated opaque identifier)', or is an attribute that the backing source
        // of user attributes might provide

        if (r.getUsernameAttribute() != null
                && ! r.getUsernameAttribute().equals("(default)")
                && ! r.getUsernameAttribute().equals("(generated opaque identifier)")) {

            Set availableAttributes = this.personAttributeDao.getPossibleUserAttributeNames();

            // not all DAOs are able to declare their available attributes; null indicates this DAO can't
            // in which case we assume the value is valid

            if (availableAttributes != null) {
                if (!availableAttributes.contains(r.getUsernameAttribute())) {
                    errors.rejectValue("usernameAttribute", "registeredService.usernameAttribute.notAvailable",
                            "This attribute is not available from configured user attribute sources.");
                }
            }

        }

    }

    public void setServicesManager(final ServicesManager serviceRegistry) {
        this.servicesManager = serviceRegistry;
    }

    public void setMaxDescriptionLength(final int maxLength) {
        this.maxDescriptionLength = maxLength;
    }

    public IPersonAttributeDao getPersonAttributeDao() {
        return personAttributeDao;
    }

    public void setPersonAttributeDao(IPersonAttributeDao personAttributeDao) {
        this.personAttributeDao = personAttributeDao;
    }
}
