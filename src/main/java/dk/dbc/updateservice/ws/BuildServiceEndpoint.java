/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.ws;

import dk.dbc.oss.ns.catalogingbuild.BuildPortType;
import dk.dbc.oss.ns.catalogingbuild.BuildRequest;
import dk.dbc.oss.ns.catalogingbuild.BuildResult;
import dk.dbc.oss.ns.catalogingbuild.BuildStatusEnum;

import javax.ejb.Stateless;
import javax.jws.WebService;

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
        wsdlLocation = "WEB-INF/classes/META-INF/wsdl/build/catalogingBuild.wsdl")
@Stateless
public class BuildServiceEndpoint implements BuildPortType {

    /**
     * Build service web-service entrypoint.
     *
     * @param parameters The parameters to use for build.
     * @return BuildResult containing the new or converted template
     */
    @Override
    public BuildResult build(BuildRequest parameters) {
        final BuildResult buildResult = new BuildResult();
        buildResult.setBuildStatus(BuildStatusEnum.OK);
        return buildResult;
    }


}
