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
import dk.dbc.updateservice.ws.reader.GetSchemasRequestReader;
import dk.dbc.updateservice.ws.reader.UpdateRequestReader;
import dk.dbc.updateservice.ws.writer.GetSchemasResponseWriter;
import dk.dbc.updateservice.ws.writer.UpdateResponseWriter;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.Enumeration;

@SchemaValidation(outbound = false)
@WebService(
        serviceName = "UpdateService",
        portName = "CatalogingUpdatePort",
        endpointInterface = "dk.dbc.updateservice.service.api.CatalogingUpdatePortType",
        targetNamespace = "http://oss.dbc.dk/ns/catalogingUpdate",
        wsdlLocation = "WEB-INF/classes/META-INF/wsdl/update/catalogingUpdate.wsdl",
        name = Constants.UPDATE_SERVICE_ENDPOINT_NAME)
@Stateless
public class UpdateServiceEndpoint implements CatalogingUpdatePortType {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(UpdateServiceEndpoint.class);

    @Inject
    UpdateServiceUpdateConnector updateConnector;

    @Resource
    WebServiceContext wsContext;

    @Override
    public UpdateRecordResult updateRecord(UpdateRecordRequest updateRecordRequest) {
        try {
            String xForwardedFor = null;

            final UpdateRequestReader updateRequestReader = new UpdateRequestReader(updateRecordRequest);
            final UpdateServiceRequestDTO updateServiceRequestDTO = updateRequestReader.getUpdateServiceRequestDTO();
            final UpdateRecordRequestMarshaller updateRecordRequestMarshaller = new UpdateRecordRequestMarshaller(updateRecordRequest);
            LOGGER.info("Entering Updateservice, marshal(updateServiceRequestDto):\n{}", updateRecordRequestMarshaller);

            final MessageContext mc = wsContext.getMessageContext();
            final HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
            LOGGER.info("REQUEST:");
            LOGGER.info("======================================");
            LOGGER.info("Auth type: {}", req.getAuthType());
            LOGGER.info("Context path: {}", req.getContextPath());
            LOGGER.info("Content type: {}", req.getContentType());
            LOGGER.info("Content length: {}", req.getContentLengthLong());
            LOGGER.info("URI: {}", req.getRequestURI());
            LOGGER.info("Client address: {}", req.getRemoteAddr());
            LOGGER.info("Client host: {}", req.getRemoteHost());
            LOGGER.info("Client port: {}", req.getRemotePort());
            LOGGER.info("Headers");
            LOGGER.info("--------------------------------------");
            Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                if ("X-Forwarded-For".equalsIgnoreCase(name)) {
                    xForwardedFor = req.getHeader(name);
                    LOGGER.info("Found X-Forwarded-For header: {}", xForwardedFor);
                }
                LOGGER.info("{}: {}", name, req.getHeader(name));
            }
            LOGGER.info("--------------------------------------");

            if (xForwardedFor == null) {
                LOGGER.info("Did not find X-Forwarded-For header so using client ip '{}' instead", req.getRemoteAddr());
                xForwardedFor = req.getRemoteAddr();
            }

            LOGGER.info("Setting X-Forwarded-for header to '{}'", xForwardedFor);

            final UpdateRecordResponseDTO updateRecordResponseDTO = updateConnector.updateRecord(updateServiceRequestDTO, xForwardedFor);
            final UpdateResponseWriter updateResponseWriter = new UpdateResponseWriter(updateRecordResponseDTO);

            final UpdateRecordResultMarshaller updateRecordResultMarshaller = new UpdateRecordResultMarshaller(updateResponseWriter.getResponse());
            LOGGER.info("Leaving UpdateService, marshal(updateRecordResult):\n{}", updateRecordResultMarshaller);

            return updateResponseWriter.getResponse();
        } catch (UpdateServiceUpdateConnectorException | JSONBException e) {
            LOGGER.error("Caught exception", e);

            return updateRecordResultError();
        }
    }

    @Override
    public GetSchemasResult getSchemas(GetSchemasRequest getSchemasRequest) {
        try {
            GetSchemasResult getSchemasResult;
            final GetSchemasRequestReader getSchemasRequestReader = new GetSchemasRequestReader(getSchemasRequest);
            final SchemasRequestDTO schemasRequestDTO = getSchemasRequestReader.getSchemasRequestDTO();
            final GetSchemasRequestMarshaller getSchemasRequestMarshaller = new GetSchemasRequestMarshaller(getSchemasRequest);
            LOGGER.info("Entering getSchemas, marshal(schemasRequestDTO):\n{}", getSchemasRequestMarshaller);

            final SchemasResponseDTO schemasResponseDTO = updateConnector.getSchemas(schemasRequestDTO);

            final GetSchemasResponseWriter getSchemasResponseWriter = new GetSchemasResponseWriter(schemasResponseDTO);
            getSchemasResult = getSchemasResponseWriter.getGetSchemasResult();

            final GetSchemasResultMarshaller getSchemasResultMarshaller = new GetSchemasResultMarshaller(getSchemasResult);
            LOGGER.info("Leaving getSchemas, marshal(getSchemasResult):\n{}", getSchemasResultMarshaller);

            return getSchemasResult;
        } catch (JSONBException e) {
            LOGGER.error("Caught JSONBException exception", e);
            return getSchemasResultError();
        } catch (UpdateServiceUpdateConnectorException e) {
            LOGGER.error("Caught UpdateServiceUpdateConnectorException exception", e);
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
