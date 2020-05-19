/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws.writer;

import dk.dbc.updateservice.dto.DoubleRecordFrontendDTO;
import dk.dbc.updateservice.dto.MessageEntryDTO;
import dk.dbc.updateservice.dto.UpdateRecordResponseDTO;
import dk.dbc.updateservice.service.api.DoubleRecordEntries;
import dk.dbc.updateservice.service.api.DoubleRecordEntry;
import dk.dbc.updateservice.service.api.MessageEntry;
import dk.dbc.updateservice.service.api.Messages;
import dk.dbc.updateservice.service.api.Type;
import dk.dbc.updateservice.service.api.UpdateRecordResult;
import dk.dbc.updateservice.service.api.UpdateStatusEnum;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Class to generate a complete response.
 * <p>
 * Usage:
 * <pre>
 *  UpdateResponseWriter writer = new UpdateResponseWriter();
 *  writer.addValidateResults(valErrorsList);
 *  writer.setUpdateStatus(UpdateStatusEnum.VALIDATION_ERROR);
 *
 *  UpdateRecordResult response = writer.getResponse();
 * </pre>
 * After the sequence the variable <code>response</code> will contain a
 * complete valid response that can be returned thought the JavaEE container.
 */
public class UpdateResponseWriter {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(UpdateResponseWriter.class);

    private final UpdateRecordResult updateRecordResult;

    public UpdateResponseWriter(UpdateRecordResponseDTO updateRecordResponseDTO) {
        updateRecordResult = convertResponseFromInternalFormatToExternalFormat(updateRecordResponseDTO);
    }

    public UpdateRecordResult getResponse() {
        return updateRecordResult;
    }

    private UpdateRecordResult convertResponseFromInternalFormatToExternalFormat(UpdateRecordResponseDTO updateRecordResponseDTO) {
        final UpdateRecordResult updateRecordResult = new UpdateRecordResult();
        updateRecordResult.setUpdateStatus(convertUpdateStatusEnumFromInternalToExternalFormat(updateRecordResponseDTO));
        if (updateRecordResponseDTO.getDoubleRecordKey() != null) {
            updateRecordResult.setDoubleRecordKey(updateRecordResponseDTO.getDoubleRecordKey());
            final DoubleRecordEntries doubleRecordEntries = new DoubleRecordEntries();
            updateRecordResult.setDoubleRecordEntries(doubleRecordEntries);
            if (updateRecordResponseDTO.getDoubleRecordFrontendDTOS() != null && !updateRecordResponseDTO.getDoubleRecordFrontendDTOS().isEmpty()) {
                for (DoubleRecordFrontendDTO doubleRecordFrontendDTO : updateRecordResponseDTO.getDoubleRecordFrontendDTOS()) {
                    doubleRecordEntries.getDoubleRecordEntry().add(convertDoubleRecordEntryFromInternalToExternalFormat(doubleRecordFrontendDTO));
                }
            }
        }
        if (updateRecordResponseDTO.getMessageEntryDTOS() != null) {
            final Messages messages = new Messages();
            updateRecordResult.setMessages(messages);
            if (updateRecordResponseDTO.getMessageEntryDTOS() != null && !updateRecordResponseDTO.getMessageEntryDTOS().isEmpty()) {
                for (MessageEntryDTO med : updateRecordResponseDTO.getMessageEntryDTOS()) {
                    messages.getMessageEntry().add(convertMessageEntryFromInternalToExternalFormat(med));
                }
            }
        }
        return updateRecordResult;
    }

    private UpdateStatusEnum convertUpdateStatusEnumFromInternalToExternalFormat(UpdateRecordResponseDTO updateRecordResponseDTO) {
        if (updateRecordResponseDTO != null && updateRecordResponseDTO.getUpdateStatusEnumDTO() != null) {
            switch (updateRecordResponseDTO.getUpdateStatusEnumDTO()) {
                case OK:
                    return UpdateStatusEnum.OK;
                case FAILED:
                    return UpdateStatusEnum.FAILED;
                default:
                    break;
            }
        }
        return null;
    }

    private MessageEntry convertMessageEntryFromInternalToExternalFormat(MessageEntryDTO messageEntryDTO) {
        final MessageEntry messageEntry = new MessageEntry();
        messageEntry.setType(convertInternalTypeEnumDtoToExternalType(messageEntryDTO));
        messageEntry.setMessage(messageEntryDTO.getMessage());
        messageEntry.setCode(messageEntryDTO.getCode());
        messageEntry.setUrlForDocumentation(messageEntryDTO.getUrlForDocumentation());
        messageEntry.setOrdinalPositionOfField(messageEntryDTO.getOrdinalPositionOfField());
        messageEntry.setOrdinalPositionOfSubfield(messageEntryDTO.getOrdinalPositionOfSubfield());
        messageEntry.setOrdinalPositionInSubfield(messageEntryDTO.getOrdinalPositionInSubfield());
        return messageEntry;
    }

    private Type convertInternalTypeEnumDtoToExternalType(MessageEntryDTO messageEntryDTO) {
        if (messageEntryDTO != null && messageEntryDTO.getType() != null) {
            switch (messageEntryDTO.getType()) {
                case ERROR:
                    return Type.ERROR;
                case FATAL:
                    return Type.FATAL;
                case WARNING:
                    return Type.WARNING;
                default:
                    break;
            }
        }
        LOGGER.warn("Got messageEntryDTO without type, returning ERROR as type, messageEntryDTO: " + messageEntryDTO);
        return Type.ERROR;
    }

    private DoubleRecordEntry convertDoubleRecordEntryFromInternalToExternalFormat(DoubleRecordFrontendDTO doubleRecordFrontendDTO) {
        final DoubleRecordEntry doubleRecordEntry = new DoubleRecordEntry();
        doubleRecordEntry.setPid(doubleRecordFrontendDTO.getPid());
        doubleRecordEntry.setMessage(doubleRecordFrontendDTO.getMessage());
        return doubleRecordEntry;
    }
}
