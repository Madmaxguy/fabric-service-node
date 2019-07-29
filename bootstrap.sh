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

echo "Bootstrapping organization ..."

SERVICE_URL="localhost:${SERVICE_BIND_PORT}"

curl -k --silent --show-error \
--request GET \
--key ${PROFILE_PATH}/crypto/users/admin/admin.key \
--cert ${PROFILE_PATH}/crypto/users/admin/admin.crt \
https://${SERVICE_URL}/bootstrap

if [[ "$?" -ne 0 ]]; then
  echo "Failed to bootstrap!"
  exit 1
fi

echo "======================================================================"
