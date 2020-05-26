package dk.dbc.updateservice.ws;

import dk.dbc.common.records.MarcField;
import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.binding.SubField;
import dk.dbc.updateservice.dto.AuthenticationDTO;
import dk.dbc.updateservice.dto.BibliographicRecordDTO;
import dk.dbc.updateservice.dto.MessageEntryDTO;
import dk.dbc.updateservice.dto.RecordDataDTO;
import dk.dbc.updateservice.dto.SchemaDTO;
import dk.dbc.updateservice.dto.SchemasRequestDTO;
import dk.dbc.updateservice.dto.SchemasResponseDTO;
import dk.dbc.updateservice.dto.TypeEnumDTO;
import dk.dbc.updateservice.dto.UpdateRecordResponseDTO;
import dk.dbc.updateservice.dto.UpdateServiceRequestDTO;
import dk.dbc.updateservice.dto.UpdateStatusEnumDTO;
import dk.dbc.updateservice.service.api.Authentication;
import dk.dbc.updateservice.service.api.BibliographicRecord;
import dk.dbc.updateservice.service.api.GetSchemasRequest;
import dk.dbc.updateservice.service.api.GetSchemasResult;
import dk.dbc.updateservice.service.api.MessageEntry;
import dk.dbc.updateservice.service.api.Messages;
import dk.dbc.updateservice.service.api.RecordData;
import dk.dbc.updateservice.service.api.Schema;
import dk.dbc.updateservice.service.api.Type;
import dk.dbc.updateservice.service.api.UpdateRecordRequest;
import dk.dbc.updateservice.service.api.UpdateRecordResult;
import dk.dbc.updateservice.service.api.UpdateStatusEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DTOConstructor {

    private static final List<Object> content = Arrays.asList("<record xmlns=\"info:lc/xmlns/marcxchange-v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/standards/iso25577/marcxchange-1-1.xsd\">\n" +
            "                            <leader>dbfhfgh2</leader>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"001\">\n" +
            "                                <subfield code=\"a\">68693268</subfield>\n" +
            "                                <subfield code=\"b\">870979</subfield>\n" +
            "                                <subfield code=\"c\">20181108150337</subfield>\n" +
            "                                <subfield code=\"d\">20131129</subfield>\n" +
            "                                <subfield code=\"f\">a</subfield>\n" +
            "                                <subfield code=\"t\">faust</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"004\">\n" +
            "                                <subfield code=\"r\">n</subfield>\n" +
            "                                <subfield code=\"a\">e</subfield>\n" +
            "                                <subfield code=\"x\">n</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"008\">\n" +
            "                                <subfield code=\"t\">h</subfield>\n" +
            "                                <subfield code=\"v\">9</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"025\">\n" +
            "                                <subfield code=\"a\">5237167</subfield>\n" +
            "                                <subfield code=\"2\">viaf</subfield>\n" +
            "                                <subfield code=\"&amp;\">VIAF</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"025\">\n" +
            "                                <subfield code=\"a\">0000000013134949</subfield>\n" +
            "                                <subfield code=\"2\">isni</subfield>\n" +
            "                                <subfield code=\"&amp;\">VIAF</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"040\">\n" +
            "                                <subfield code=\"a\">DBC</subfield>\n" +
            "                                <subfield code=\"b\">dan</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"043\">\n" +
            "                                <subfield code=\"c\">dk</subfield>\n" +
            "                                <subfield code=\"&amp;\">VIAF</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"100\">\n" +
            "                                <subfield code=\"a\">Meilby</subfield>\n" +
            "                                <subfield code=\"h\">Mogens</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"375\">\n" +
            "                                <subfield code=\"a\">1</subfield>\n" +
            "                                <subfield code=\"2\">iso5218</subfield>\n" +
            "                                <subfield code=\"&amp;\">VIAF</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"d08\">\n" +
            "                                <subfield code=\"o\">autogenereret</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"xyz\">\n" +
            "                                <subfield code=\"u\">MEILBYMOGENS</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"z98\">\n" +
            "                                <subfield code=\"a\">Minus korrekturprint</subfield>\n" +
            "                            </datafield>\n" +
            "                            <datafield ind1=\"0\" ind2=\"0\" tag=\"z99\">\n" +
            "                                <subfield code=\"a\">VIAF</subfield>\n" +
            "                            </datafield>\n" +
            "                        </record>");

    public static Authentication constructAuthentication(String user, String password, String groupId) {
        final Authentication authentication = new Authentication();
        authentication.setUserIdAut(user);
        authentication.setPasswordAut(password);
        authentication.setGroupIdAut(groupId);

        return authentication;
    }

    public static AuthenticationDTO constructAuthenticationDTO(String user, String password, String groupId) {
        final AuthenticationDTO authenticationDTO = new AuthenticationDTO();
        authenticationDTO.setGroupId(groupId);
        authenticationDTO.setPassword("");
        authenticationDTO.setUserId("");

        return authenticationDTO;
    }

    public static GetSchemasRequest constructGetSchemasRequest(String groupId) {
        final GetSchemasRequest getSchemasRequest = new GetSchemasRequest();
        getSchemasRequest.setAuthentication(constructAuthentication("", "", groupId));

        return getSchemasRequest;
    }

    public static SchemasRequestDTO constructSchemasRequestDTO(String groupId) {
        final SchemasRequestDTO schemasRequestDTO = new SchemasRequestDTO();
        schemasRequestDTO.setAuthenticationDTO(constructAuthenticationDTO("", "", groupId));

        return schemasRequestDTO;
    }

    public static GetSchemasResult constructGetSchemasResult_OK(int count) {
        final GetSchemasResult getSchemasResult = new GetSchemasResult();
        getSchemasResult.setUpdateStatus(UpdateStatusEnum.OK);

        final List<Schema> schemaList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final Schema schema = new Schema();
            schema.setSchemaInfo("Schema " + i + " info");
            schema.setSchemaName("schema_" + i);

            schemaList.add(schema);
        }

        getSchemasResult.getSchema().addAll(schemaList);

        return getSchemasResult;
    }

    public static GetSchemasResult constructGetSchemasResult_Failed() {
        final GetSchemasResult getSchemasResult = new GetSchemasResult();
        getSchemasResult.setUpdateStatus(UpdateStatusEnum.FAILED);
        final MessageEntry messageEntry = new MessageEntry();
        messageEntry.setMessage("Failed");
        messageEntry.setType(Type.ERROR);

        final Messages messages = new Messages();
        messages.getMessageEntry().add(messageEntry);

        getSchemasResult.setMessages(messages);

        return getSchemasResult;
    }

    public static SchemasResponseDTO constructSchemasResponseDTO_OK(int count) {
        final SchemasResponseDTO schemasResponseDTO = new SchemasResponseDTO();
        schemasResponseDTO.setUpdateStatusEnumDTO(UpdateStatusEnumDTO.OK);

        final List<SchemaDTO> schemaDTOList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final SchemaDTO schemaDTO = new SchemaDTO();
            schemaDTO.setSchemaInfo("Schema " + i + " info");
            schemaDTO.setSchemaName("schema_" + i);

            schemaDTOList.add(schemaDTO);
        }

        schemasResponseDTO.setSchemaDTOList(schemaDTOList);

        return schemasResponseDTO;
    }

    public static SchemasResponseDTO constructSchemasResponseDTO_Failed() {
        final SchemasResponseDTO schemasResponseDTO = new SchemasResponseDTO();
        schemasResponseDTO.setUpdateStatusEnumDTO(UpdateStatusEnumDTO.FAILED);
        schemasResponseDTO.setErrorMessage("Failed");
        schemasResponseDTO.setError(true);

        return schemasResponseDTO;
    }

    public static UpdateRecordRequest constructUpdateRecordRequest(String groupId) {
        final UpdateRecordRequest updateRecordRequest = new UpdateRecordRequest();
        updateRecordRequest.setAuthentication(constructAuthentication("", "", groupId));
        updateRecordRequest.setTrackingId("Test");
        updateRecordRequest.setSchemaName("dbcautoritet");

        return updateRecordRequest;
    }

    public static UpdateServiceRequestDTO constructUpdateServiceRequestDTO(String groupId) {
        final UpdateServiceRequestDTO updateServiceRequestDTO = new UpdateServiceRequestDTO();
        updateServiceRequestDTO.setAuthenticationDTO(constructAuthenticationDTO("", "", groupId));
        updateServiceRequestDTO.setTrackingId("Test");
        updateServiceRequestDTO.setSchemaName("dbcautoritet");

        return updateServiceRequestDTO;
    }

    public static BibliographicRecord constructBibliographicRecordId() {
        final BibliographicRecord bibliographicRecord = new BibliographicRecord();
        bibliographicRecord.setRecordPacking("xml");
        bibliographicRecord.setRecordSchema("info:lc/xmlns/marcxchange-v1");

        final RecordData recordData = new RecordData();
        recordData.getContent().add(content);

        bibliographicRecord.setRecordData(recordData);

        return bibliographicRecord;
    }

    public static BibliographicRecordDTO constructBibliographicRecordDTO() {
        final BibliographicRecordDTO bibliographicRecordDTO = new BibliographicRecordDTO();
        bibliographicRecordDTO.setRecordPacking("xml");
        bibliographicRecordDTO.setRecordSchema("info:lc/xmlns/marcxchange-v1");

        final RecordDataDTO recordDataDTO = new RecordDataDTO();
        recordDataDTO.setContent(content);

        return bibliographicRecordDTO;
    }

    public static UpdateRecordResponseDTO constructUpdateRecordResponseDTO_OK() {
        final UpdateRecordResponseDTO updateRecordResponseDTO = new UpdateRecordResponseDTO();
        updateRecordResponseDTO.setUpdateStatusEnumDTO(UpdateStatusEnumDTO.OK);

        return updateRecordResponseDTO;
    }

    public static UpdateRecordResponseDTO constructUpdateRecordResponseDTO_ValidationError() {
        final UpdateRecordResponseDTO updateRecordResponseDTO = new UpdateRecordResponseDTO();
        updateRecordResponseDTO.setUpdateStatusEnumDTO(UpdateStatusEnumDTO.FAILED);

        final MessageEntryDTO messageEntryDTO = new MessageEntryDTO();
        messageEntryDTO.setType(TypeEnumDTO.ERROR);
        messageEntryDTO.setMessage("Indholdet i forespørgslen overholder ikke minimumskrav for en gyldig marc post (check 001 *a og *b)");

        final List<MessageEntryDTO> messageEntryDTOS = new ArrayList<>();
        messageEntryDTOS.add(messageEntryDTO);

        updateRecordResponseDTO.addMessageEntryDtos(messageEntryDTOS);

        return updateRecordResponseDTO;
    }

    public static UpdateRecordResponseDTO constructUpdateRecordResponseDTO_AuthenticationError() {
        final UpdateRecordResponseDTO updateRecordResponseDTO = new UpdateRecordResponseDTO();
        updateRecordResponseDTO.setUpdateStatusEnumDTO(UpdateStatusEnumDTO.FAILED);

        final MessageEntryDTO messageEntryDTO = new MessageEntryDTO();
        messageEntryDTO.setType(TypeEnumDTO.ERROR);
        messageEntryDTO.setMessage("Authentication error");

        final List<MessageEntryDTO> messageEntryDTOS = new ArrayList<>();
        messageEntryDTOS.add(messageEntryDTO);

        updateRecordResponseDTO.addMessageEntryDtos(messageEntryDTOS);

        return updateRecordResponseDTO;
    }

    public static UpdateRecordResult constructUpdateRecordResult_OK() {
        final UpdateRecordResult updateRecordResult = new UpdateRecordResult();
        updateRecordResult.setUpdateStatus(UpdateStatusEnum.OK);

        return updateRecordResult;
    }

    public static UpdateRecordResult constructUpdateRecordResult_ValidationError() {
        final UpdateRecordResult updateRecordResult = new UpdateRecordResult();
        updateRecordResult.setUpdateStatus(UpdateStatusEnum.FAILED);

        final MessageEntry messageEntry = new MessageEntry();
        messageEntry.setType(Type.ERROR);
        messageEntry.setMessage("Indholdet i forespørgslen overholder ikke minimumskrav for en gyldig marc post (check 001 *a og *b)");

        final Messages messages = new Messages();
        messages.getMessageEntry().add(messageEntry);

        updateRecordResult.setMessages(messages);

        return updateRecordResult;
    }

    public static UpdateRecordResult constructUpdateRecordResult_AuthenticationError() {
        final UpdateRecordResult updateRecordResult = new UpdateRecordResult();
        updateRecordResult.setUpdateStatus(UpdateStatusEnum.FAILED);

        final MessageEntry messageEntry = new MessageEntry();
        messageEntry.setType(Type.ERROR);
        messageEntry.setMessage("Authentication error");

        final Messages messages = new Messages();
        messages.getMessageEntry().add(messageEntry);

        updateRecordResult.setMessages(messages);

        return updateRecordResult;
    }
}
