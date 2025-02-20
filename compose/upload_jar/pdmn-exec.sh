#!/bin/bash
podman cp HDFS-1.0-SNAPSHOT.jar namenode:/tmp/wc.jar
podman exec namenode hadoop jar /tmp/wc.jar /file.txt $@