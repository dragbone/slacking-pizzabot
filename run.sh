#!/usr/bin/env bash
java -jar build/deploy/pizzaslackbot-alpha.jar $1 $2 > log.txt 2>&1
