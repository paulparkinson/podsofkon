#!/bin/bash

set -e

IMAGE_NAME=podsofkon
#IMAGE_VERSION=latest
IMAGE_VERSION=0.1

export IMAGE=${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_VERSION}
export IMAGE_VERSION=$IMAGE_VERSION

mvn clean package

docker build -t=$IMAGE .
#docker buildx build --platform=linux/amd64 -t=$IMAGE .

docker push "$IMAGE"
if [  $? -eq 0 ]; then
    docker rmi "$IMAGE"
fi

