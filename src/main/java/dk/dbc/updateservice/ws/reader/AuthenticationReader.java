/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws.reader;

import dk.dbc.updateservice.dto.AuthenticationDTO;
import dk.dbc.updateservice.service.api.Authentication;

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
