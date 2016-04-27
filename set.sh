#!/bin/bash

echo $1 $2
curl -v -X POST \
	-d '{"pattern":"'"$1"'","action":"'"$2"'"}' \
	-H "Content-Type: application/json" \
	http://localhost:8080/plugin/metrics/mappings/config
