/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws.reader;

import dk.dbc.oss.ns.catalogingupdate.Authentication;
import dk.dbc.oss.ns.catalogingupdate.GetSchemasRequest;
import dk.dbc.updateservice.dto.AuthenticationDTO;
import dk.dbc.updateservice.dto.SchemasRequestDTO;

public class GetSchemasRequestReader extends AuthenticationReader {
    private SchemasRequestDTO schemasRequestDTO;

    public GetSchemasRequestReader(GetSchemasRequest getSchemasRequest) {
        schemasRequestDTO = convertRequestFromExternalFormatToInternalFormat(getSchemasRequest);
    }

    public SchemasRequestDTO getSchemasRequestDTO() {
        return schemasRequestDTO;
    }

    public static GetSchemasRequest cloneWithoutPassword(GetSchemasRequest getSchemasRequest) {
        GetSchemasRequest res = null;
        if (getSchemasRequest != null) {
            res = new GetSchemasRequest();
            if (getSchemasRequest.getAuthentication() != null) {
                res.setAuthentication(new Authentication());
                res.getAuthentication().setGroupIdAut(getSchemasRequest.getAuthentication().getGroupIdAut());
                res.getAuthentication().setPasswordAut("***");
                res.getAuthentication().setUserIdAut(getSchemasRequest.getAuthentication().getUserIdAut());
            }
            res.setTrackingId(getSchemasRequest.getTrackingId());
        }
        return res;
    }

    @SuppressWarnings("Duplicates")
    private SchemasRequestDTO convertRequestFromExternalFormatToInternalFormat(GetSchemasRequest getSchemasRequest) {
        SchemasRequestDTO schemasRequestDTO = null;
        if (getSchemasRequest != null) {
            schemasRequestDTO = new SchemasRequestDTO();
            AuthenticationDTO AuthenticationDTO = convertExternalAuthenticationToInternalAuthenticationDto(getSchemasRequest.getAuthentication());
            schemasRequestDTO.setAuthenticationDTO(AuthenticationDTO);
            schemasRequestDTO.setTrackingId(getSchemasRequest.getTrackingId());
        }
        return schemasRequestDTO;
    }
}
