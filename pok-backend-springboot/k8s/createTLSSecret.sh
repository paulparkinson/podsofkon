#!/bin/bash

kubectl create secret tls ssl-certificate-secret --key $MY_STATE/tls/tls.key --cert $MY_STATE/tls/tls.crt -n