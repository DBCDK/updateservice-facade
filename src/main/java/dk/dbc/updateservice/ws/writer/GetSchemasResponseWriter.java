/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws.writer;

import dk.dbc.oss.ns.catalogingupdate.GetSchemasResult;
import dk.dbc.oss.ns.catalogingupdate.MessageEntry;
import dk.dbc.oss.ns.catalogingupdate.Messages;
import dk.dbc.oss.ns.catalogingupdate.Schema;
import dk.dbc.oss.ns.catalogingupdate.Type;
import dk.dbc.oss.ns.catalogingupdate.UpdateStatusEnum;
import dk.dbc.updateservice.dto.SchemaDTO;
import dk.dbc.updateservice.dto.SchemasResponseDTO;

public class GetSchemasResponseWriter {
    private GetSchemasResult getSchemasResult;

    public GetSchemasResponseWriter(SchemasResponseDTO schemasResponseDTO) {
        getSchemasResult = convertResponseFromInternalFormatToExternalFormat(schemasResponseDTO);
    }

    public GetSchemasResult getGetSchemasResult() {
        return getSchemasResult;
    }

    @SuppressWarnings("Duplicates")
    private GetSchemasResult convertResponseFromInternalFormatToExternalFormat(SchemasResponseDTO schemasResponseDTO) {
        getSchemasResult = new GetSchemasResult();
        if (schemasResponseDTO != null) {
            if (schemasResponseDTO.isError()) {
                getSchemasResult.setUpdateStatus(UpdateStatusEnum.FAILED);
                Messages messages = new Messages();
                MessageEntry messageEntry = new MessageEntry();
                messages.getMessageEntry().add(messageEntry);
                messageEntry.setType(Type.ERROR);
                messageEntry.setMessage(schemasResponseDTO.getErrorMessage());
                getGetSchemasResult().setMessages(messages);
            } else {
                getSchemasResult.setUpdateStatus(UpdateStatusEnum.OK);
                Schema schema;
                for (SchemaDTO schemaDTO : schemasResponseDTO.getSchemaDTOList()) {
                    schema = new Schema();
                    schema.setSchemaInfo(schemaDTO.getSchemaInfo());
                    schema.setSchemaName(schemaDTO.getSchemaName());
                    getSchemasResult.getSchema().add(schema);
                }
            }
        }
        return getSchemasResult;
    }
}
