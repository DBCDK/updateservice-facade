FROM docker.dbc.dk/payara5-full:latest

ENV UPDATESERVICE_URL empty

COPY app.json deployments/
COPY target/updateservice-2.0-SNAPSHOT.war deployments/

LABEL INSTANCE_NAME="Name of the updateservice instance. Default is blank (Optional)"
LABEL MAINTAINER="meta-scrum@dbc.dk"