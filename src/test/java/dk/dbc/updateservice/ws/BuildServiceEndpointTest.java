package dk.dbc.updateservice.ws;

import dk.dbc.oss.ns.catalogingbuild.BuildRequest;
import dk.dbc.oss.ns.catalogingbuild.BuildResult;
import dk.dbc.updateservice.UpdateServiceBuildConnector;
import dk.dbc.updateservice.dto.BuildRequestDTO;
import dk.dbc.updateservice.dto.BuildResponseDTO;
import dk.dbc.updateservice.ws.writer.BuildResultWriter;
import org.junit.jupiter.api.BeforeAll;

import javax.xml.parsers.DocumentBuilderFactory;

import static dk.dbc.updateservice.ws.BuildServiceEndpointDTOConstructor.BUILD_RECORD_FFU;
import static dk.dbc.updateservice.ws.BuildServiceEndpointDTOConstructor.constructBibliographicRecord;
import static dk.dbc.updateservice.ws.BuildServiceEndpointDTOConstructor.constructBibliographicRecordDTO;
import static dk.dbc.updateservice.ws.BuildServiceEndpointDTOConstructor.constructBuildRequest;
import static dk.dbc.updateservice.ws.BuildServiceEndpointDTOConstructor.constructBuildRequestDTO;
import static dk.dbc.updateservice.ws.BuildServiceEndpointDTOConstructor.constructBuildResponseDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BuildServiceEndpointTest {

    private static BuildServiceEndpoint buildServiceEndpoint;

    @BeforeAll
    static void before() {
        buildServiceEndpoint = new BuildServiceEndpoint();
        buildServiceEndpoint.updateServiceBuildConnector = mock(UpdateServiceBuildConnector.class);
        buildServiceEndpoint.documentBuilderFactory = DocumentBuilderFactory.newInstance();
    }

    //@Test
    // TODO Test doesn't work yet. For some reason the constructBibliographicRecord function is unable to convert the input record to an XML document
    void build_NoRecord() throws Exception {
        final BuildRequest buildRequest = constructBuildRequest("ffu");
        final BuildRequestDTO buildRequestDTO = constructBuildRequestDTO("ffu");

        final BuildResponseDTO buildResponseDTO = constructBuildResponseDTO();
        buildResponseDTO.setBibliographicRecordDTO(constructBibliographicRecordDTO(BUILD_RECORD_FFU));

        when(buildServiceEndpoint.updateServiceBuildConnector.buildRecord(eq(buildRequestDTO))).thenReturn(buildResponseDTO);

        BuildResult actual = buildServiceEndpoint.build(buildRequest);
        BuildResult expected = BuildResultWriter.get(buildResponseDTO, buildServiceEndpoint.documentBuilderFactory);
        expected.setBibliographicRecord(constructBibliographicRecord(BUILD_RECORD_FFU, buildServiceEndpoint.documentBuilderFactory));

        assertThat("BuildResult without input record", actual, is(expected));
    }

    //@Test
    // TODO Not done yet
    void build_Record() throws Exception {
        final BuildRequest buildRequest = constructBuildRequest("ffu");
        //buildRequest.setBibliographicRecord(constructBibliographicRecord(INPUT_RECORD_AUTHORIY, buildServiceEndpoint.documentBuilderFactory));

        final BuildRequestDTO buildRequestDTO = constructBuildRequestDTO("ffu");
        //buildRequestDTO.setBibliographicRecordDTO(constructBibliographicRecordDTO(INPUT_RECORD_AUTHORIY));

        final BuildResponseDTO buildResponseDTO = constructBuildResponseDTO();
        buildResponseDTO.setBibliographicRecordDTO(constructBibliographicRecordDTO(BUILD_RECORD_FFU));

        when(buildServiceEndpoint.updateServiceBuildConnector.buildRecord(eq(buildRequestDTO))).thenReturn(buildResponseDTO);

        BuildResult actual = buildServiceEndpoint.build(buildRequest);
        BuildResult expected = BuildResultWriter.get(buildResponseDTO, buildServiceEndpoint.documentBuilderFactory);
        expected.setBibliographicRecord(constructBibliographicRecord(BUILD_RECORD_FFU, buildServiceEndpoint.documentBuilderFactory));

        assertThat("BuildResult with input record", actual, is(expected));
    }
}
