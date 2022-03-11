#!/bin/bash

mvn clean compile assembly:single
clear
java -jar target/abrielle-1.0-jar-with-dependencies.jar