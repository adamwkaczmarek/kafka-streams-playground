#!/bin/bash

# Remember to add secret with docker credentials!
set -e
# Build kafka connect
docker build -t akaczmarek/kafka-connect
