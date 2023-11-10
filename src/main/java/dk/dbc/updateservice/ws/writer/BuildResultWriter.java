/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws.writer;

import dk.dbc.oss.ns.catalogingbuild.BibliographicRecord;
import dk.dbc.oss.ns.catalogingbuild.BuildResult;
import dk.dbc.oss.ns.catalogingbuild.BuildStatusEnum;
import dk.dbc.oss.ns.catalogingbuild.ExtraRecordData;
import dk.dbc.oss.ns.catalogingbuild.RecordData;
import dk.dbc.updateservice.dto.BibliographicRecordDTO;
import dk.dbc.updateservice.dto.BuildResponseDTO;
import dk.dbc.updateservice.dto.BuildStatusEnumDTO;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class BuildResultWriter {

    public static BuildResult get(BuildResponseDTO buildResponseDTO, DocumentBuilderFactory documentBuilderFactory) throws IOException, SAXException, ParserConfigurationException {
        BuildResult buildResult = new BuildResult();
        buildResult.setBuildStatus(get(buildResponseDTO.getBuildStatusEnumDTO()));
        if (buildResponseDTO.getBuildStatusEnumDTO() == BuildStatusEnumDTO.OK) {
            BibliographicRecordDTO bibliographicRecordDTO = buildResponseDTO.getBibliographicRecordDTO();
            BibliographicRecord bibliographicRecord = new BibliographicRecord();
            RecordData recordData = new RecordData();

            /*
                The objects in recordData.content must be w3c Element. However, when we receive the content from the REST
                service the data has been serialized to String. So before we add the content to the XML DTO we first have
                to convert each element to an XML document.
             */
            for (Object o : bibliographicRecordDTO.getRecordDataDTO().getContent()) {
                Document document = convertStringToDocument((String) o, documentBuilderFactory);
                if (document != null) {
                    recordData.getContent().add(document.getDocumentElement());
                }
            }

            bibliographicRecord.setRecordData(recordData);

            if (bibliographicRecordDTO.getExtraRecordDataDTO() != null) {
                ExtraRecordData extraRecordData = new ExtraRecordData();
                for (Object o : bibliographicRecordDTO.getExtraRecordDataDTO().getContent()) {
                    Document document = convertStringToDocument((String) o, documentBuilderFactory);
                    if (document != null) {
                        extraRecordData.getContent().add(document.getDocumentElement());
                    }
                }
                bibliographicRecord.setExtraRecordData(extraRecordData);
            }
            bibliographicRecord.setRecordPacking(bibliographicRecordDTO.getRecordPacking());
            bibliographicRecord.setRecordSchema(bibliographicRecordDTO.getRecordSchema());

            buildResult.setBibliographicRecord(bibliographicRecord);
        }
        return buildResult;
    }

    private static Document convertStringToDocument(String xmlString, DocumentBuilderFactory documentBuilderFactory) throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

        return builder.parse(new InputSource(new StringReader(xmlString)));
    }

    private static BuildStatusEnum get(BuildStatusEnumDTO buildStatusEnumDTO) {
        switch (buildStatusEnumDTO) {
            case OK:
                return BuildStatusEnum.OK;
            case FAILED_INTERNAL_ERROR:
                return BuildStatusEnum.FAILED_INTERNAL_ERROR;
            case FAILED_INVALID_SCHEMA:
                return BuildStatusEnum.FAILED_INVALID_SCHEMA;
            case FAILED_INVALID_RECORD_SCHEMA:
                return BuildStatusEnum.FAILED_INVALID_RECORD_SCHEMA;
            case FAILED_UPDATE_INTERNAL_ERROR:
                return BuildStatusEnum.FAILED_UPDATE_INTERNAL_ERROR;
            case FAILED_INVALID_RECORD_PACKING:
                return BuildStatusEnum.FAILED_INVALID_RECORD_PACKING;
        }
        return null;
    }

}
