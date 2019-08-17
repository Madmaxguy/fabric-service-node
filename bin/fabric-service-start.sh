#!/bin/bash

if [[ "$(uname)" = "Darwin" ]]; then
    PROFILE_PATH=$(greadlink -f "$1")
else
    PROFILE_PATH=$(readlink -f "$1")
fi

. ${PROFILE_PATH}/settings

echo "Starting Fabric Service Node ..."
INITIAL_NAME="fabric.service.node.${SERVICE_BIND_PORT}"
serviceID=`docker run -d \
 -e "INITIAL_NAME=${INITIAL_NAME}" \
 -e "PROFILE_PATH=${PROFILE_PATH}" \
 -e "SERVICE_BIND_PORT=${SERVICE_BIND_PORT}" \
 -e "SERVICE_EXTERNAL_ADDRESS=${SERVICE_EXTERNAL_ADDRESS}" \
 -e "DOCKER_SOCKET=unix:///host/var/run/docker.sock" \
 -p ${SERVICE_BIND_PORT}:${SERVICE_BIND_PORT} \
 --volume=${PROFILE_PATH}/hosts:/etc/hosts \
 --volume=${PROFILE_PATH}:/opt/profile \
 --volume=/var/run/:/host/var/run/ \
 --name $INITIAL_NAME \
 enterprisedlt/fabric-service-node`
echo "Service ID: ${serviceID}"

# await service node to start up
grep -m 1 "ServiceNode\$ - Started" <(docker logs -f ${serviceID} 2>&1)
