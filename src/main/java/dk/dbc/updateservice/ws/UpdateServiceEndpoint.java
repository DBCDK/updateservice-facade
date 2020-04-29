/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws;

import com.sun.xml.ws.developer.SchemaValidation;
import dk.dbc.jsonb.JSONBException;
import dk.dbc.updateservice.UpdateServiceUpdateConnector;
import dk.dbc.updateservice.UpdateServiceUpdateConnectorException;
import dk.dbc.updateservice.common.Constants;
import dk.dbc.updateservice.dto.SchemasRequestDTO;
import dk.dbc.updateservice.dto.SchemasResponseDTO;
import dk.dbc.updateservice.dto.UpdateRecordResponseDTO;
import dk.dbc.updateservice.dto.UpdateServiceRequestDTO;
import dk.dbc.updateservice.service.api.CatalogingUpdatePortType;
import dk.dbc.updateservice.service.api.GetSchemasRequest;
import dk.dbc.updateservice.service.api.GetSchemasResult;
import dk.dbc.updateservice.service.api.MessageEntry;
import dk.dbc.updateservice.service.api.Messages;
import dk.dbc.updateservice.service.api.UpdateRecordRequest;
import dk.dbc.updateservice.service.api.UpdateRecordResult;
import dk.dbc.updateservice.service.api.UpdateStatusEnum;
import dk.dbc.updateservice.ws.marshall.GetSchemasRequestMarshaller;
import dk.dbc.updateservice.ws.marshall.GetSchemasResultMarshaller;
import dk.dbc.updateservice.ws.marshall.UpdateRecordRequestMarshaller;
import dk.dbc.updateservice.ws.marshall.UpdateRecordResultMarshaller;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
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
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(UpdateServiceEndpoint.class);

    public static final String MARSHALLING_ERROR_MSG = "Got an error while marshalling input request, using reflection instead.";

    @Inject
    UpdateServiceUpdateConnector updateConnector;

    @Override
    public UpdateRecordResult updateRecord(UpdateRecordRequest updateRecordRequest) {
        try {
            final UpdateRecordRequestMarshaller updateRecordRequestMarshaller = new UpdateRecordRequestMarshaller(updateRecordRequest);
            LOGGER.info("Entering updateRecord, marshal(updateRecordRequest):\n{}", updateRecordRequestMarshaller);

            final UpdateRequestReader requestReader = new UpdateRequestReader(updateRecordRequest);
            final UpdateServiceRequestDTO updateServiceRequestDTO = requestReader.getUpdateServiceRequestDTO();

            final UpdateRecordResponseDTO updateRecordResponseDTO = updateConnector.updateRecord(updateServiceRequestDTO);

            final UpdateResponseWriter updateResponseWriter = new UpdateResponseWriter(updateRecordResponseDTO);

            final UpdateRecordResult updateRecordResult = updateResponseWriter.getUpdateRecordResult();

            final UpdateRecordResultMarshaller updateRecordResultMarshaller = new UpdateRecordResultMarshaller(updateRecordResult);
            LOGGER.info("Leaving updateRecord, marshal(updateRecordResult):\n{}", updateRecordResultMarshaller);

            return updateRecordResult;
        } catch (UpdateServiceUpdateConnectorException | JSONBException e) {
            LOGGER.error("Caught exception", e);

            return updateRecordResultError();
        }
    }

    @Override
    public GetSchemasResult getSchemas(GetSchemasRequest getSchemasRequest) {
        try {
            final GetSchemasRequestMarshaller getSchemasRequestMarshaller = new GetSchemasRequestMarshaller(getSchemasRequest);
            LOGGER.info("Entering getSchemas, marshal(getSchemasRequest):\n{}", getSchemasRequestMarshaller);

            final GetSchemasRequestReader getSchemasRequestReader = new GetSchemasRequestReader(getSchemasRequest);
            final SchemasRequestDTO schemasRequestDTO = getSchemasRequestReader.getSchemasRequestDTO();

            final SchemasResponseDTO schemasResponseDTO = updateConnector.getSchemas(schemasRequestDTO);

            final GetSchemasResponseWriter getSchemasResponseWriter = new GetSchemasResponseWriter(schemasResponseDTO);
            final GetSchemasResult getSchemasResult = getSchemasResponseWriter.getGetSchemasResult();

            final GetSchemasResultMarshaller getSchemasResultMarshaller = new GetSchemasResultMarshaller(getSchemasResult);
            LOGGER.info("Leaving getSchemas, marshal(updateRecordResult):\n{}", getSchemasResultMarshaller);

            return getSchemasResult;
        } catch (UpdateServiceGetSchemasConnectorException e) {
            LOGGER.error("Caught exception", e);

            return getSchemasResultError();
        }
    }

    private UpdateRecordResult updateRecordResultError() {
        final UpdateRecordResult updateRecordResult = new UpdateRecordResult();
        updateRecordResult.setUpdateStatus(UpdateStatusEnum.FAILED);
        updateRecordResult.setMessages(getErrorMessages());

        return updateRecordResult;
    }

    private GetSchemasResult getSchemasResultError() {
        GetSchemasResult getSchemasResult = new GetSchemasResult();
        getSchemasResult.setUpdateStatus(UpdateStatusEnum.FAILED);
        getSchemasResult.setMessages(getErrorMessages());

        return getSchemasResult;
    }

    private Messages getErrorMessages() {
        final MessageEntry messageEntry = new MessageEntry();
        messageEntry.setMessage("Internal server error");

        final Messages messages = new Messages();
        messages.getMessageEntry().add(messageEntry);

        return messages;
    }


}
