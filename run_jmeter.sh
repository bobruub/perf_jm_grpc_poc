#!/bin/bash
cd "$(dirname "$0")"
export JAVA_HOME="/usr/local/opt/openjdk"
nohup ../../_software/apache-jmeter-5.1.1/bin/jmeter -Jjmeter.laf=System & exit
 
