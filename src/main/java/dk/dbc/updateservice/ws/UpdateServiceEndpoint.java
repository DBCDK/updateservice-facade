/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws;

import com.sun.xml.ws.developer.SchemaValidation;
import dk.dbc.commons.jsonb.JSONBException;
import dk.dbc.oss.ns.catalogingupdate.CatalogingUpdatePortType;
import dk.dbc.oss.ns.catalogingupdate.GetSchemasRequest;
import dk.dbc.oss.ns.catalogingupdate.GetSchemasResult;
import dk.dbc.oss.ns.catalogingupdate.MessageEntry;
import dk.dbc.oss.ns.catalogingupdate.Messages;
import dk.dbc.oss.ns.catalogingupdate.UpdateRecordRequest;
import dk.dbc.oss.ns.catalogingupdate.UpdateRecordResult;
import dk.dbc.oss.ns.catalogingupdate.UpdateStatusEnum;
import dk.dbc.updateservice.UpdateServiceUpdateConnector;
import dk.dbc.updateservice.UpdateServiceUpdateConnectorException;
import dk.dbc.updateservice.common.Constants;
import dk.dbc.updateservice.dto.SchemasRequestDTO;
import dk.dbc.updateservice.dto.SchemasResponseDTO;
import dk.dbc.updateservice.dto.UpdateRecordResponseDTO;
import dk.dbc.updateservice.dto.UpdateServiceRequestDTO;
import dk.dbc.updateservice.ws.marshall.GetSchemasRequestMarshaller;
import dk.dbc.updateservice.ws.marshall.GetSchemasResultMarshaller;
import dk.dbc.updateservice.ws.marshall.UpdateRecordRequestMarshaller;
import dk.dbc.updateservice.ws.marshall.UpdateRecordResultMarshaller;
import dk.dbc.updateservice.ws.reader.GetSchemasRequestReader;
import dk.dbc.updateservice.ws.reader.UpdateRequestReader;
import dk.dbc.updateservice.ws.writer.GetSchemasResponseWriter;
import dk.dbc.updateservice.ws.writer.UpdateResponseWriter;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jws.WebService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.Enumeration;

@SchemaValidation(outbound = false)
@WebService(serviceName = "UpdateService", portName = "CatalogingUpdatePort", endpointInterface = "dk.dbc.oss.ns.catalogingupdate.CatalogingUpdatePortType", targetNamespace = "http://oss.dbc.dk/ns/catalogingUpdate", wsdlLocation = "WEB-INF/classes/META-INF/wsdl/update/catalogingUpdate.wsdl", name = Constants.UPDATE_SERVICE_ENDPOINT_NAME)
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

            UpdateRequestReader updateRequestReader = new UpdateRequestReader(updateRecordRequest);
            UpdateServiceRequestDTO updateServiceRequestDTO = updateRequestReader.getUpdateServiceRequestDTO();
            UpdateRecordRequestMarshaller updateRecordRequestMarshaller = new UpdateRecordRequestMarshaller(updateRecordRequest);
            LOGGER.info("Entering Updateservice, marshal(updateServiceRequestDto):\n{}", updateRecordRequestMarshaller);

            MessageContext mc = wsContext.getMessageContext();
            HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
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

            UpdateRecordResponseDTO updateRecordResponseDTO = updateConnector.updateRecord(updateServiceRequestDTO, xForwardedFor);
            UpdateResponseWriter updateResponseWriter = new UpdateResponseWriter(updateRecordResponseDTO);

            UpdateRecordResultMarshaller updateRecordResultMarshaller = new UpdateRecordResultMarshaller(updateResponseWriter.getResponse());
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
            GetSchemasRequestReader getSchemasRequestReader = new GetSchemasRequestReader(getSchemasRequest);
            SchemasRequestDTO schemasRequestDTO = getSchemasRequestReader.getSchemasRequestDTO();
            GetSchemasRequestMarshaller getSchemasRequestMarshaller = new GetSchemasRequestMarshaller(getSchemasRequest);
            LOGGER.info("Entering getSchemas, marshal(schemasRequestDTO):\n{}", getSchemasRequestMarshaller);

            SchemasResponseDTO schemasResponseDTO = updateConnector.getSchemas(schemasRequestDTO);

            GetSchemasResponseWriter getSchemasResponseWriter = new GetSchemasResponseWriter(schemasResponseDTO);
            getSchemasResult = getSchemasResponseWriter.getGetSchemasResult();

            GetSchemasResultMarshaller getSchemasResultMarshaller = new GetSchemasResultMarshaller(getSchemasResult);
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
        UpdateRecordResult updateRecordResult = new UpdateRecordResult();
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
        MessageEntry messageEntry = new MessageEntry();
        messageEntry.setMessage("Internal server error");

        Messages messages = new Messages();
        messages.getMessageEntry().add(messageEntry);

        return messages;
    }

}
