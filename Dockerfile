FROM docker-dbc.artifacts.dbccloud.dk/payara6-full:latest

ENV UPDATE_SERVICE_URL empty
ENV BUILD_SERVICE_URL empty

RUN echo "set server-config.transaction-service.timeout-in-seconds=12000" >> scripts/prebootcommandfile.txt

COPY app.json deployments/
COPY target/updateservice-facade-2.0-SNAPSHOT.war deployments/

LABEL INSTANCE_NAME="Name of the updateservice instance. Default is blank (Optional)"
LABEL MAINTAINER="febib@dbc.dk"
