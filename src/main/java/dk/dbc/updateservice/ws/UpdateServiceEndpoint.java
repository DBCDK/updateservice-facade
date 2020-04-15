/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws;

import com.sun.xml.ws.developer.SchemaValidation;
import dk.dbc.updateservice.common.Constants;
import dk.dbc.updateservice.service.api.CatalogingUpdatePortType;
import dk.dbc.updateservice.service.api.GetSchemasRequest;
import dk.dbc.updateservice.service.api.GetSchemasResult;
import dk.dbc.updateservice.service.api.UpdateRecordRequest;
import dk.dbc.updateservice.service.api.UpdateRecordResult;
import dk.dbc.updateservice.service.api.UpdateStatusEnum;

import javax.ejb.Stateless;
import javax.jws.WebService;

@SchemaValidation(outbound = false)
@WebService(
        serviceName = "UpdateService",
        portName = "CatalogingUpdatePort",
        endpointInterface = "dk.dbc.updateservice.service.api.CatalogingUpdatePortType",
        targetNamespace = "http://oss.dbc.dk/ns/catalogingUpdate",
        wsdlLocation = "WEB-INF/classes/META-INF/wsdl/update/catalogingUpdate.wsdl",
        name = Constants.UPDATE_SERVICE_VERSION)
@Stateless
public class UpdateServiceEndpoint implements CatalogingUpdatePortType {


    @Override
    public UpdateRecordResult updateRecord(UpdateRecordRequest updateRecordRequest) {
        final UpdateRecordResult updateRecordResult = new UpdateRecordResult();
        updateRecordResult.setUpdateStatus(UpdateStatusEnum.OK);

        return updateRecordResult;
    }

    @Override
    public GetSchemasResult getSchemas(GetSchemasRequest getSchemasRequest) {
        final GetSchemasResult getSchemasResult = new GetSchemasResult();
        getSchemasResult.setUpdateStatus(UpdateStatusEnum.OK);

        return getSchemasResult;
    }


}
