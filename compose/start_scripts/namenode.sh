#!/bin/bash

if [ ! -d "/opt/hadoop/data/name/current" ]; then
    hdfs namenode -format
fi
hdfs namenode