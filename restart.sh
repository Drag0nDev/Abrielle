#!/bin/bash

git pull -f https://github.com/Drag0nDev/Abrielle.git
mvn clean compile assembly:single
clear
java -jar target/abrielle-1.0-jar-with-dependencies.jar