#!/bin/bash

if [[ "$(uname)" = "Darwin" ]]; then
    PROFILE_PATH=$(greadlink -f "$1")
else
    PROFILE_PATH=$(readlink -f "$1")
fi


 . ${PROFILE_PATH}/settings

 echo "Join to contraсt with chaincode $2 ver. $3..."

SERVICE_URL="localhost:${SERVICE_BIND_PORT}"
curl -k --silent --show-error \
--key ${PROFILE_PATH}/crypto/users/admin/admin.key \
--cert ${PROFILE_PATH}/crypto/users/admin/admin.crt \
-H "Content-Type: application/json" \
--request POST \
https://${SERVICE_URL}/admin/contract-join \
-d   "{\"name\":\"$2\",\"founder\":\"$3\"}"

echo

if [[ "$?" -ne 0 ]]; then
  echo "Failed to join contract."
  exit 1
fi

 echo "======================================================================"