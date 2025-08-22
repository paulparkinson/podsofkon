#!/bin/bash

kubectl get secret order-db-tns-admin-secret -n msdataworkshop -o yaml \
  | sed 's/name: order-db-tns-admin-secret/name: podsofkon-db-tns-admin-secret/' \
  | sed 's/namespace: msdataworkshop/namespace: podsofkon/' \
  | kubectl apply -n podsofkon -f -