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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class BuildResultWriter {

    public static BuildResult get(BuildResponseDTO buildResponseDTO) {
        BuildResult buildResult = new BuildResult();
        buildResult.setBuildStatus(get(buildResponseDTO.getBuildStatusEnumDTO()));
        if (buildResponseDTO.getBuildStatusEnumDTO() == BuildStatusEnumDTO.OK) {
            BibliographicRecordDTO bibliographicRecordDTO = buildResponseDTO.getBibliographicRecordDTO();
            BibliographicRecord bibliographicRecord = new BibliographicRecord();
            RecordData recordData = new RecordData();

            /*
                The objects in recordData.content must be w3c Element. However when we receive the content from the REST
                service the data has been serialized to String. So before we add the content to the XML DTO we first have
                to convert each element to an XML document.
             */
            for (Object o : bibliographicRecordDTO.getRecordDataDTO().getContent()) {
                Document document = convertStringToDocument((String) o);
                if (document != null) {
                    recordData.getContent().add(document.getDocumentElement());
                }
            }

            bibliographicRecord.setRecordData(recordData);

            if (bibliographicRecordDTO.getExtraRecordDataDTO() != null) {
                ExtraRecordData extraRecordData = new ExtraRecordData();
                extraRecordData.getContent().addAll(bibliographicRecordDTO.getExtraRecordDataDTO().getContent());
                bibliographicRecord.setExtraRecordData(extraRecordData);
            }
            bibliographicRecord.setRecordPacking(bibliographicRecordDTO.getRecordPacking());
            bibliographicRecord.setRecordSchema(bibliographicRecordDTO.getRecordSchema());

            buildResult.setBibliographicRecord(bibliographicRecord);
        }
        return buildResult;
    }

    private static Document convertStringToDocument(String xmlString) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();

            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BuildStatusEnum get(BuildStatusEnumDTO buildStatusEnumDTO) {
        switch (buildStatusEnumDTO) {
            case OK: return BuildStatusEnum.OK;
            case FAILED_INTERNAL_ERROR: return BuildStatusEnum.FAILED_INTERNAL_ERROR;
            case FAILED_INVALID_SCHEMA: return BuildStatusEnum.FAILED_INVALID_SCHEMA;
            case FAILED_INVALID_RECORD_SCHEMA: return BuildStatusEnum.FAILED_INVALID_RECORD_SCHEMA;
            case FAILED_UPDATE_INTERNAL_ERROR: return BuildStatusEnum.FAILED_UPDATE_INTERNAL_ERROR;
            case FAILED_INVALID_RECORD_PACKING: return BuildStatusEnum.FAILED_INVALID_RECORD_PACKING;
        }
        return null;
    }

}
