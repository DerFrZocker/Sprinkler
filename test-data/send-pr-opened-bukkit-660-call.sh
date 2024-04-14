#!/bin/sh

curl -H "X-Event-Key: pr:opened" -X POST -d @payloads/send-pr-opened-bukkit-660-call.json localhost:3141/api/v1/bitbucket-webhook/
