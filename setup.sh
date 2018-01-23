#!/bin/bash

# run with `source` to get defined env variables into the current shell

[[ $_ == $0 ]] && echo "Script is expected to be sourced" && exit 1
[ ! -d task1 ] && echo "We are not in the byteman workshop directory" && return

export BYTEMAN_WORKSHOP="$PWD"
[ ! -d libs ] && mkdir "$BYTEMAN_WORKSHOP/libs"


# JAVA
if [ ! -d "$BYTEMAN_WORKSHOP/libs/jdk-8u161-linux-x64.tar.gz" ]; then
  [ ! -f "$BYTEMAN_WORKSHOP/libs/jdk-8u161-linux-x64.tar.gz" ] &&\
    echo "Download the 'jdk8u161' from https://www.java.com/en/download/linux_manual.jsp and place it to '$BYTEMAN_WORKSHOP/libs'" && return
  tar -C "$BYTEMAN_WORKSHOP/libs" -xzf "$BYTEMAN_WORKSHOP/libs/jdk-8u161-linux-x64.tar.gz"
fi

export JAVA_HOME="$BYTEMAN_WORKSHOP/libs/jdk1.8.0_161"
export PATH="$JAVA_HOME/bin:$PATH"


# MAVEN
if [ ! -d "$BYTEMAN_WORKSHOP/libs/apache-maven-3.5.2" ]; then
  wget http://www-eu.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.zip -O $BYTEMAN_WORKSHOP/libs/maven352.zip
  unzip -d "$BYTEMAN_WORKSHOP/libs" "$BYTEMAN_WORKSHOP/libs/maven352.zip"
fi

export MAVEN_HOME="$BYTEMAN_WORKSHOP/libs/apache-maven-3.5.2"
export PATH="$MAVEN_HOME/bin:$PATH"


# BYTEMAN
if [ ! -d "$BYTEMAN_WORKSHOP/libs/byteman-download-4.0.0" ]; then
  wget http://downloads.jboss.org/byteman/4.0.0/byteman-download-4.0.0-bin.zip -O "$BYTEMAN_WORKSHOP/libs/byteman400.zip"
  unzip -d "$BYTEMAN_WORKSHOP/libs/" "$BYTEMAN_WORKSHOP/libs/byteman400.zip"
fi

export BYTEMAN_HOME="$BYTEMAN_WORKSHOP/libs/byteman-download-4.0.0"
export BYTEMAN_JAR="$BYTEMAN_HOME/lib/byteman.jar"

# check
echo ; echo
echo 'Checking the outcome of the script:'

mvn -version
file "$BYTEMAN_JAR"