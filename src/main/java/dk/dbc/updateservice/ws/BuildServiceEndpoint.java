/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws;

import dk.dbc.commons.jsonb.JSONBException;
import dk.dbc.oss.ns.catalogingbuild.Build;
import dk.dbc.oss.ns.catalogingbuild.BuildPortType;
import dk.dbc.oss.ns.catalogingbuild.BuildRequest;
import dk.dbc.oss.ns.catalogingbuild.BuildResponse;
import dk.dbc.oss.ns.catalogingbuild.BuildResult;
import dk.dbc.oss.ns.catalogingbuild.BuildStatusEnum;
import dk.dbc.updateservice.UpdateServiceBuildConnector;
import dk.dbc.updateservice.UpdateServiceBuildConnectorException;
import dk.dbc.updateservice.common.Constants;
import dk.dbc.updateservice.dto.BuildRequestDTO;
import dk.dbc.updateservice.dto.BuildResponseDTO;
import dk.dbc.updateservice.ws.reader.BuildRequestReader;
import dk.dbc.updateservice.ws.writer.BuildResultWriter;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jws.WebService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Web service entry point for Open Build SOAP service.
 * <p/>
 * This class implements the SOAP operations for our web service.
 */
@WebService(
        serviceName = "CatalogingBuildServices",
        portName = "BuildPort",
            endpointInterface = "dk.dbc.oss.ns.catalogingbuild.BuildPortType",
        targetNamespace = "http://oss.dbc.dk/ns/catalogingBuild",
        wsdlLocation = "WEB-INF/classes/META-INF/wsdl/build/catalogingBuild.wsdl",
        name = Constants.BUILD_SERVICE_ENDPOINT_NAME)
@Stateless
public class BuildServiceEndpoint implements BuildPortType {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(BuildServiceEndpoint.class);

    @Inject
    UpdateServiceBuildConnector updateServiceBuildConnector;

    DocumentBuilderFactory documentBuilderFactory;

    @PostConstruct
    public void postConstruct() {
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Build service web-service entrypoint.
     *
     * @param parameters The parameters to use for build.
     * @return BuildResult containing the new or converted template
     */
    @Override
    public BuildResult build(BuildRequest parameters) {
        LOGGER.entry();

        try {
            LOGGER.info("Build request: {}", buildRequestToString(parameters));

            final BuildRequestDTO buildRequestDTO = BuildRequestReader.getDTO(parameters);
            final BuildResponseDTO buildResponseDTO = updateServiceBuildConnector.buildRecord(buildRequestDTO);
            LOGGER.info("buildResponseDTO: {}", buildResponseDTO);
            final BuildResult buildResult = BuildResultWriter.get(buildResponseDTO, documentBuilderFactory);
            final String resultOutput = buildResultToString(buildResult);

            LOGGER.info("Build response: {}", resultOutput);

            return buildResult;
        } catch (UpdateServiceBuildConnectorException e) {
            LOGGER.error("Caught UpdateServiceBuildConnectorException", e);
            final BuildResult buildResult = new BuildResult();
            buildResult.setBuildStatus(BuildStatusEnum.FAILED_INTERNAL_ERROR);
            return buildResult;
        } catch (JSONBException e) {
            LOGGER.error("Exception when parsing the request", e);
            final BuildResult buildResult = new BuildResult();
            buildResult.setBuildStatus(BuildStatusEnum.FAILED_INTERNAL_ERROR);
            return buildResult;
        } catch (SAXException | ParserConfigurationException | IOException e) {
            LOGGER.error("Exception when parsing output record", e);
            final BuildResult buildResult = new BuildResult();
            buildResult.setBuildStatus(BuildStatusEnum.FAILED_INTERNAL_ERROR);
            return buildResult;
        } catch (Exception e) {
            LOGGER.error("Unexpected exception", e);
            final BuildResult buildResult = new BuildResult();
            buildResult.setBuildStatus(BuildStatusEnum.FAILED_INTERNAL_ERROR);
            return buildResult;
        } finally {
            LOGGER.exit();
        }
    }

    private String buildRequestToString(BuildRequest br) {
        String res;
        try {
            Build build = new Build();
            build.setBuildRequest(br);
            res = marshal(build, Build.class);
        } catch (JAXBException e) {
            LOGGER.catching(e);
            res = "<could not read input>";
        }
        return res;
    }

    private String buildResultToString(BuildResult br) {
        String res;
        try {
            BuildResponse buildResponse = new BuildResponse();
            buildResponse.setBuildResult(br);
            res = marshal(buildResponse, BuildResponse.class);
        } catch (JAXBException e) {
            LOGGER.catching(e);
            res = "<could not read output>";
        }
        return res;
    }

    private <T> String marshal(T data, Class<T> clazz) throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(data, stringWriter);
        return stringWriter.toString();
    }

}
