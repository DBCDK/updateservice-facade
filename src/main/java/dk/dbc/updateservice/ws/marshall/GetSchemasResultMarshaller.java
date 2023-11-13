/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws.marshall;

import dk.dbc.oss.ns.catalogingupdate.GetSchemasResult;
import dk.dbc.oss.ns.catalogingupdate.ObjectFactory;
import dk.dbc.updateservice.common.Constants;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

public class GetSchemasResultMarshaller {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(GetSchemasResultMarshaller.class);
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private static JAXBContext jaxbContext = null;

    private final GetSchemasResult getSchemasResult;

    public GetSchemasResultMarshaller(GetSchemasResult getSchemasResult) {
        this.getSchemasResult = getSchemasResult;

        // Initializing JAXBContent is expensive so we only want to do it once
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(GetSchemasResult.class);
            } catch (JAXBException e) {
                throw new RuntimeException("Could not create JAXBContext");
            }
        }
    }

    private String marshal() {
        try {
            final JAXBElement<GetSchemasResult> jAXBElement = objectFactory.createGetSchemasResult(this.getSchemasResult);
            final StringWriter stringWriter = new StringWriter();
            final Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.marshal(jAXBElement, stringWriter);

            return stringWriter.toString();
        } catch (JAXBException e) {
            LOGGER.catching(e);
            LOGGER.warn(Constants.MARSHALLING_ERROR_MSG);
            return new ReflectionToStringBuilder(getSchemasResult, new RecursiveToStringStyle()).toString();
        }
    }

    @Override
    public String toString() {
        return marshal();
    }
}
