FROM openjdk:17-alpine

MAINTAINER Darmokhval

RUN apk add bash

RUN mkdir /app
WORKDIR /app

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

COPY target/rest_with_liquibase-0.0.1-SNAPSHOT.jar app.jar