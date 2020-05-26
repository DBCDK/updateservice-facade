package dk.dbc.updateservice.ws;

import dk.dbc.updateservice.UpdateServiceUpdateConnector;
import dk.dbc.updateservice.dto.SchemasRequestDTO;
import dk.dbc.updateservice.dto.UpdateServiceRequestDTO;
import dk.dbc.updateservice.service.api.GetSchemasRequest;
import dk.dbc.updateservice.service.api.GetSchemasResult;
import dk.dbc.updateservice.service.api.UpdateRecordRequest;
import dk.dbc.updateservice.service.api.UpdateRecordResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dk.dbc.updateservice.ws.DTOConstructor.constructBibliographicRecordDTO;
import static dk.dbc.updateservice.ws.DTOConstructor.constructBibliographicRecordId;
import static dk.dbc.updateservice.ws.DTOConstructor.constructGetSchemasRequest;
import static dk.dbc.updateservice.ws.DTOConstructor.constructGetSchemasResult_Failed;
import static dk.dbc.updateservice.ws.DTOConstructor.constructGetSchemasResult_OK;
import static dk.dbc.updateservice.ws.DTOConstructor.constructSchemasRequestDTO;
import static dk.dbc.updateservice.ws.DTOConstructor.constructSchemasResponseDTO_Failed;
import static dk.dbc.updateservice.ws.DTOConstructor.constructSchemasResponseDTO_OK;
import static dk.dbc.updateservice.ws.DTOConstructor.constructUpdateRecordRequest;
import static dk.dbc.updateservice.ws.DTOConstructor.constructUpdateRecordResponseDTO_AuthenticationError;
import static dk.dbc.updateservice.ws.DTOConstructor.constructUpdateRecordResponseDTO_OK;
import static dk.dbc.updateservice.ws.DTOConstructor.constructUpdateRecordResponseDTO_ValidationError;
import static dk.dbc.updateservice.ws.DTOConstructor.constructUpdateRecordResult_AuthenticationError;
import static dk.dbc.updateservice.ws.DTOConstructor.constructUpdateRecordResult_OK;
import static dk.dbc.updateservice.ws.DTOConstructor.constructUpdateRecordResult_ValidationError;
import static dk.dbc.updateservice.ws.DTOConstructor.constructUpdateServiceRequestDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateServiceEndpointTest {
    private static UpdateServiceEndpoint updateServiceEndpoint;

    @BeforeAll
    static void before() {
        updateServiceEndpoint = new UpdateServiceEndpoint();
        updateServiceEndpoint.updateConnector = mock(UpdateServiceUpdateConnector.class);
        updateServiceEndpoint.wsContext = mock(WebServiceContext.class);
    }

    @Test
    void update_Ok_NoXForwardedFor() throws Exception {
        final UpdateRecordRequest updateRecordRequest = constructUpdateRecordRequest("010100");
        updateRecordRequest.setBibliographicRecord(constructBibliographicRecordId());

        final UpdateServiceRequestDTO updateServiceRequestDTO = constructUpdateServiceRequestDTO("010100");
        updateServiceRequestDTO.setBibliographicRecordDTO(constructBibliographicRecordDTO());

        mockWsContext(null);

        when(updateServiceEndpoint.updateConnector.updateRecord(any(UpdateServiceRequestDTO.class), eq("localhost"))).thenReturn(constructUpdateRecordResponseDTO_OK());

        final UpdateRecordResult actual = updateServiceEndpoint.updateRecord(updateRecordRequest);
        final UpdateRecordResult expected = constructUpdateRecordResult_OK();

        assertThat("update result is as expected", actual, is(expected));
    }

    @Test
    void update_Ok_XForwardedFor() throws Exception {
        final UpdateRecordRequest updateRecordRequest = constructUpdateRecordRequest("010100");
        updateRecordRequest.setBibliographicRecord(constructBibliographicRecordId());

        final UpdateServiceRequestDTO updateServiceRequestDTO = constructUpdateServiceRequestDTO("010100");
        updateServiceRequestDTO.setBibliographicRecordDTO(constructBibliographicRecordDTO());

        mockWsContext("x-forwarded-for");

        when(updateServiceEndpoint.updateConnector.updateRecord(any(UpdateServiceRequestDTO.class), eq("x-forwarded-for"))).thenReturn(constructUpdateRecordResponseDTO_OK());

        final UpdateRecordResult actual = updateServiceEndpoint.updateRecord(updateRecordRequest);
        final UpdateRecordResult expected = constructUpdateRecordResult_OK();

        assertThat("update result is as expected", actual, is(expected));
    }

    @Test
    void update_ValidationError() throws Exception {
        final UpdateRecordRequest updateRecordRequest = constructUpdateRecordRequest("010100");
        updateRecordRequest.setBibliographicRecord(constructBibliographicRecordId());

        final UpdateServiceRequestDTO updateServiceRequestDTO = constructUpdateServiceRequestDTO("010100");
        updateServiceRequestDTO.setBibliographicRecordDTO(constructBibliographicRecordDTO());

        mockWsContext(null);

        when(updateServiceEndpoint.updateConnector.updateRecord(any(UpdateServiceRequestDTO.class), eq("localhost"))).thenReturn(constructUpdateRecordResponseDTO_ValidationError());

        final UpdateRecordResult actual = updateServiceEndpoint.updateRecord(updateRecordRequest);
        final UpdateRecordResult expected = constructUpdateRecordResult_ValidationError();

        assertThat("update result is as expected", actual, is(expected));
    }

    @Test
    void update_AuthenticationError() throws Exception {
        final UpdateRecordRequest updateRecordRequest = constructUpdateRecordRequest("010100");
        updateRecordRequest.setBibliographicRecord(constructBibliographicRecordId());

        final UpdateServiceRequestDTO updateServiceRequestDTO = constructUpdateServiceRequestDTO("010100");
        updateServiceRequestDTO.setBibliographicRecordDTO(constructBibliographicRecordDTO());

        mockWsContext("not-authorized");

        when(updateServiceEndpoint.updateConnector.updateRecord(any(UpdateServiceRequestDTO.class), eq("not-authorized"))).thenReturn(constructUpdateRecordResponseDTO_AuthenticationError());

        final UpdateRecordResult actual = updateServiceEndpoint.updateRecord(updateRecordRequest);
        final UpdateRecordResult expected = constructUpdateRecordResult_AuthenticationError();

        assertThat("update result is as expected", actual, is(expected));
    }

    @Test
    void getSchemasRequest_Empty() throws Exception {
        final int schemaCount = 0;
        final GetSchemasRequest getSchemasRequest = constructGetSchemasRequest("010100");
        final SchemasRequestDTO schemasRequestDTO = constructSchemasRequestDTO("010100");

        when(updateServiceEndpoint.updateConnector.getSchemas(eq(schemasRequestDTO))).thenReturn(constructSchemasResponseDTO_OK(schemaCount));

        final GetSchemasResult actual = updateServiceEndpoint.getSchemas(getSchemasRequest);
        final GetSchemasResult expected = constructGetSchemasResult_OK(schemaCount);

        assertThat("getSchemas contains 0 schemas", actual.getSchema().size(), is(schemaCount));
        assertThat("getSchemas matches expected", actual, is(expected));
    }

    @Test
    void getSchemasRequest_Single() throws Exception {
        final int schemaCount = 1;
        final GetSchemasRequest getSchemasRequest = constructGetSchemasRequest("010100");
        final SchemasRequestDTO schemasRequestDTO = constructSchemasRequestDTO("010100");

        when(updateServiceEndpoint.updateConnector.getSchemas(eq(schemasRequestDTO))).thenReturn(constructSchemasResponseDTO_OK(schemaCount));

        final GetSchemasResult actual = updateServiceEndpoint.getSchemas(getSchemasRequest);
        final GetSchemasResult expected = constructGetSchemasResult_OK(schemaCount);

        assertThat("getSchemas contains 1 schema", actual.getSchema().size(), is(schemaCount));
        assertThat("getSchemas matches expected", actual, is(expected));
    }

    @Test
    void getSchemasRequest_Multiple() throws Exception {
        final int schemaCount = 3;
        final GetSchemasRequest getSchemasRequest = constructGetSchemasRequest("010100");
        final SchemasRequestDTO schemasRequestDTO = constructSchemasRequestDTO("010100");

        when(updateServiceEndpoint.updateConnector.getSchemas(eq(schemasRequestDTO))).thenReturn(constructSchemasResponseDTO_OK(schemaCount));

        final GetSchemasResult actual = updateServiceEndpoint.getSchemas(getSchemasRequest);
        final GetSchemasResult expected = constructGetSchemasResult_OK(schemaCount);

        assertThat("getSchemas contains 3 schemas", actual.getSchema().size(), is(schemaCount));
        assertThat("getSchemas matches expected", actual, is(expected));
    }

    @Test
    void getSchemasRequest_Failed() throws Exception {
        final GetSchemasRequest getSchemasRequest = constructGetSchemasRequest("010100");
        final SchemasRequestDTO schemasRequestDTO = constructSchemasRequestDTO("010100");

        when(updateServiceEndpoint.updateConnector.getSchemas(eq(schemasRequestDTO))).thenReturn(constructSchemasResponseDTO_Failed());

        final GetSchemasResult actual = updateServiceEndpoint.getSchemas(getSchemasRequest);
        final GetSchemasResult expected = constructGetSchemasResult_Failed();

        assertThat("Update returns OK", actual, is(expected));
    }

    private void mockWsContext(String xForwardedFor) {
        final MessageContext messageContext = mock(MessageContext.class);
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getAuthType()).thenReturn(null);
        when(httpServletRequest.getContextPath()).thenReturn(null);
        when(httpServletRequest.getContentType()).thenReturn(null);
        when(httpServletRequest.getContentLengthLong()).thenReturn(0L);
        when(httpServletRequest.getRequestURI()).thenReturn(null);
        when(httpServletRequest.getRemoteAddr()).thenReturn("localhost");
        when(httpServletRequest.getRemoteHost()).thenReturn(null);
        when(httpServletRequest.getRemotePort()).thenReturn(0);
        final List<String> headers = new ArrayList<>();
        if (xForwardedFor != null) {
            headers.add("X-Forwarded-For");
            when(httpServletRequest.getHeader(eq("X-Forwarded-For"))).thenReturn(xForwardedFor);
        }
        when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(headers));
        when(messageContext.get(MessageContext.SERVLET_REQUEST)).thenReturn(httpServletRequest);
        when(updateServiceEndpoint.wsContext.getMessageContext()).thenReturn(messageContext);
    }

}
