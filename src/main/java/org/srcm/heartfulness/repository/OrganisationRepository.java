package org.srcm.heartfulness.repository;

import org.srcm.heartfulness.model.Organisation;

/**
 * Repository interface for managing <code>Organisation</code> domain objects.
 *
 * Created by vsonnathi on 11/28/15.
 */
public interface OrganisationRepository {

    public Organisation findByNameAndWebsite(String name, String website);

    public Organisation findById(int organizationId);

    public void save(Organisation organisation);
}
