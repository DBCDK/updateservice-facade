/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws.marshall;

import dk.dbc.oss.ns.catalogingupdate.ObjectFactory;
import dk.dbc.oss.ns.catalogingupdate.UpdateRecordRequest;
import dk.dbc.updateservice.common.Constants;
import dk.dbc.updateservice.ws.reader.UpdateRequestReader;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

public class UpdateRecordRequestMarshaller {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(UpdateRecordRequestMarshaller.class);
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private static JAXBContext jaxbContext = null;

    private final UpdateRecordRequest updateRecordRequest;

    public UpdateRecordRequestMarshaller(UpdateRecordRequest updateRecordRequest) {
        this.updateRecordRequest = UpdateRequestReader.cloneWithoutPassword(updateRecordRequest);

        // Initializing JAXBContent is expensive so we only want to do it once
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(UpdateRecordRequest.class);
            } catch (JAXBException e) {
                throw new RuntimeException("Could not create JAXBContext");
            }
        }
    }

    private String marshal() {
        try {
            final JAXBElement<UpdateRecordRequest> jAXBElement = objectFactory.createUpdateRecordRequest(updateRecordRequest);
            final StringWriter stringWriter = new StringWriter();
            final Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.marshal(jAXBElement, stringWriter);

            return stringWriter.toString();
        } catch (JAXBException e) {
            LOGGER.catching(e);
            LOGGER.warn(Constants.MARSHALLING_ERROR_MSG);
            return new ReflectionToStringBuilder(updateRecordRequest, new RecursiveToStringStyle()).toString();
        }
    }

    @Override
    public String toString() {
        return marshal();
    }
}
