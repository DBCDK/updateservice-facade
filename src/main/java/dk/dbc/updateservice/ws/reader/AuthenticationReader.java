/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws.reader;

import dk.dbc.oss.ns.catalogingupdate.Authentication;
import dk.dbc.updateservice.dto.AuthenticationDTO;

public class AuthenticationReader {

    protected AuthenticationDTO convertExternalAuthenticationToInternalAuthenticationDto(Authentication authentication) {
        AuthenticationDTO authenticationDTO = null;
        if (authentication != null) {
            authenticationDTO = new AuthenticationDTO();
            authenticationDTO.setGroupId(authentication.getGroupIdAut());
            authenticationDTO.setPassword(authentication.getPasswordAut());
            authenticationDTO.setUserId(authentication.getUserIdAut());
        }
        return authenticationDTO;
    }
}
