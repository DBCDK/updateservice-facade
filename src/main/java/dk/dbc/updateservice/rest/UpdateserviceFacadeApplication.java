/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.updateservice.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by stp on 25/11/15.
 */
@ApplicationPath("rest")
public class UpdateserviceFacadeApplication extends Application {
    private static final Set<Class<?>> classes = new HashSet<>(
            Arrays.asList(StatusBean.class)
    );

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
