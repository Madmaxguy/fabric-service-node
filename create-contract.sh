#!/bin/bash

if [[ "$(uname)" = "Darwin" ]]; then
    SCRIPT=$(greadlink -f "$0")
    HF_NET_HOME=$(dirname "$SCRIPT")
    PROFILE_PATH=$(greadlink -f "$1")
else
    SCRIPT=$(readlink -f "$0")
    HF_NET_HOME=$(dirname "$SCRIPT")
    PROFILE_PATH=$(readlink -f "$1")
fi

. ${PROFILE_PATH}/settings

echo "Creating contract ..."

SERVICE_URL="localhost:${SERVICE_BIND_PORT}"
curl -k --silent --show-error \
--key ${PROFILE_PATH}/crypto/users/admin/admin.key \
--cert ${PROFILE_PATH}/crypto/users/admin/admin.crt \
--request POST \
--data-binary "@$2" https://${SERVICE_URL}/admin/create-contract

if [[ "$?" -ne 0 ]]; then
  echo "Failed to create contract!"
  exit 1
fi

echo "======================================================================"