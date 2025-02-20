#!/bin/bash
podman cp anek_utf8.txt namenode:/tmp/file.txt
podman exec namenode hdfs dfs -put /tmp/file.txt /