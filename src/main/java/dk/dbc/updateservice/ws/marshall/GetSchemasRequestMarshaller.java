/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws.marshall;

import dk.dbc.oss.ns.catalogingupdate.GetSchemasRequest;
import dk.dbc.oss.ns.catalogingupdate.ObjectFactory;
import dk.dbc.updateservice.common.Constants;
import dk.dbc.updateservice.ws.reader.GetSchemasRequestReader;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

public class GetSchemasRequestMarshaller {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(GetSchemasRequestMarshaller.class);
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private static JAXBContext jaxbContext = null;

    private final GetSchemasRequest getSchemasRequest;

    public GetSchemasRequestMarshaller(GetSchemasRequest getSchemasRequest) {
        this.getSchemasRequest = GetSchemasRequestReader.cloneWithoutPassword(getSchemasRequest);

        // Initializing JAXBContent is expensive so we only want to do it once
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(GetSchemasRequest.class);
            } catch (JAXBException e) {
                throw new RuntimeException("Could not create JAXBContext");
            }
        }
    }

    private String marshal() {
        try {
            final JAXBElement<GetSchemasRequest> jAXBElement = objectFactory.createGetSchemasRequest(getSchemasRequest);
            final StringWriter stringWriter = new StringWriter();
            final Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.marshal(jAXBElement, stringWriter);

            return stringWriter.toString();
        } catch (JAXBException e) {
            LOGGER.catching(e);
            LOGGER.warn(Constants.MARSHALLING_ERROR_MSG);
            return new ReflectionToStringBuilder(getSchemasRequest, new RecursiveToStringStyle()).toString();
        }
    }

    @Override
    public String toString() {
        return marshal();
    }
}
