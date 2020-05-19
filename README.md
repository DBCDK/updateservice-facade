# SOAP facade for updateservice

### How to run:
```
export UPDATE_SERVICE_URL=http://update-service.<env>.svc.cloud.dbc.dk/UpdateService/rest
export BUILD_SERVICE_URL=http://update-service.<env>.svc.cloud.dbc.dk/UpdateService/rest
export UPDATESERVICE_FACADE_PORT=<available port or ignore for default 8080>

./build && ./start

unset UPDATESERVICE_FACADE_PORT
unset UPDATE_SERVICE_URL
unset BUILD_SERVICE_URL
```
