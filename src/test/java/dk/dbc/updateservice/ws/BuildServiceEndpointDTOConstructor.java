package dk.dbc.updateservice.ws;

import dk.dbc.oss.ns.catalogingbuild.BibliographicRecord;
import dk.dbc.oss.ns.catalogingbuild.BuildRequest;
import dk.dbc.oss.ns.catalogingbuild.BuildResult;
import dk.dbc.oss.ns.catalogingbuild.BuildStatusEnum;
import dk.dbc.oss.ns.catalogingbuild.RecordData;
import dk.dbc.updateservice.dto.BibliographicRecordDTO;
import dk.dbc.updateservice.dto.BuildRequestDTO;
import dk.dbc.updateservice.dto.BuildResponseDTO;
import dk.dbc.updateservice.dto.BuildStatusEnumDTO;
import dk.dbc.updateservice.dto.RecordDataDTO;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

public class BuildServiceEndpointDTOConstructor {

    public static final Object BUILD_RECORD_FFU = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n" +
            "<record xmlns=\"info:lc/xmlns/marcxchange-v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd\">" +
            "<leader>00000n    2200000   4500</leader>" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"001\">" +
            "<subfield code=\"a\">126830963</subfield>" +
            "<subfield code=\"b\"></subfield>" +
            "<subfield code=\"f\">a</subfield>" +
            "</datafield>" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"004\">" +
            "<subfield code=\"r\"></subfield>" +
            "<subfield code=\"a\">e</subfield>" +
            "</datafield>" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"008\">" +
            "<subfield code=\"v\"></subfield>" +
            "</datafield>" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"009\">" +
            "<subfield code=\"a\"></subfield>" +
            "</datafield>" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"096\">" +
            "<subfield code=\"z\"></subfield>" +
            "</datafield>" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"245\">" +
            "<subfield code=\"a\"></subfield>" +
            "</datafield>" +
            "</record>";

    public static BuildRequest constructBuildRequest(String schemaName) {
        BuildRequest buildRequest = new BuildRequest();

        buildRequest.setSchemaName(schemaName);

        BibliographicRecord bibliographicRecord = new BibliographicRecord();
        bibliographicRecord.setRecordSchema("info:lc/xmlns/marcxchange-v1");
        bibliographicRecord.setRecordPacking("xml");

        buildRequest.setBibliographicRecord(bibliographicRecord);

        return buildRequest;
    }

    public static BuildRequestDTO constructBuildRequestDTO(String schemaName) {
        BuildRequestDTO buildRequestDTO = new BuildRequestDTO();
        buildRequestDTO.setSchemaName(schemaName);

        BibliographicRecordDTO bibliographicRecordDTO = new BibliographicRecordDTO();
        bibliographicRecordDTO.setRecordSchema("info:lc/xmlns/marcxchange-v1");
        bibliographicRecordDTO.setRecordPacking("xml");

        buildRequestDTO.setBibliographicRecordDTO(bibliographicRecordDTO);

        return buildRequestDTO;
    }

    public static BuildResult constructBuildResult() {
        BuildResult buildResult = new BuildResult();
        buildResult.setBuildStatus(BuildStatusEnum.OK);

        return buildResult;
    }

    public static BibliographicRecordDTO constructBibliographicRecordDTO(Object o) {
        BibliographicRecordDTO bibliographicRecordDTO = new BibliographicRecordDTO();
        bibliographicRecordDTO.setRecordSchema("info:lc/xmlns/marcxchange-v1");
        bibliographicRecordDTO.setRecordPacking("xml");

        RecordDataDTO recordDataDTO = new RecordDataDTO();
        recordDataDTO.setContent(Collections.singletonList(o));

        bibliographicRecordDTO.setRecordDataDTO(recordDataDTO);

        return bibliographicRecordDTO;
    }

    public static BuildResponseDTO constructBuildResponseDTO() {
        BuildResponseDTO buildResponseDTO = new BuildResponseDTO();
        buildResponseDTO.setBuildStatusEnumDTO(BuildStatusEnumDTO.OK);

        return buildResponseDTO;
    }

    public static BibliographicRecord constructBibliographicRecord(Object o, DocumentBuilderFactory documentBuilderFactory) throws IOException, SAXException, ParserConfigurationException {
        BibliographicRecord bibliographicRecord = new BibliographicRecord();
        bibliographicRecord.setRecordSchema("info:lc/xmlns/marcxchange-v1");
        bibliographicRecord.setRecordPacking("xml");

        RecordData recordData = new RecordData();
        Document document = convertStringToDocument((String) o, documentBuilderFactory);
        if (document != null) {
            recordData.getContent().add(document.getDocumentElement());
        }

        bibliographicRecord.setRecordData(recordData);

        return bibliographicRecord;
    }

    private static Document convertStringToDocument(String xmlString, DocumentBuilderFactory documentBuilderFactory) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

        return builder.parse(new InputSource(new StringReader(xmlString)));
    }

}
