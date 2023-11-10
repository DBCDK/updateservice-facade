/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.rest;

import dk.dbc.serviceutils.ServiceStatus;

import jakarta.ejb.Stateless;
import jakarta.ws.rs.Path;

@Stateless
@Path("/api")
public class StatusBean implements ServiceStatus {
}
