FROM docker.dbc.dk/payara5-full:java11-6

ENV UPDATE_SERVICE_URL empty
ENV BUILD_SERVICE_URL empty

COPY app.json deployments/
COPY target/updateservice-facade-2.0-SNAPSHOT.war deployments/

LABEL INSTANCE_NAME="Name of the updateservice instance. Default is blank (Optional)"
LABEL MAINTAINER="meta-scrum@dbc.dk"