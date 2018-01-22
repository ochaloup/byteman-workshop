#!/bin/bash

# run with `source` to get defined env variables into the current shell

[[ $_ == $0 ]] && echo "Script is expected to be sourced" && exit 1
[ ! -d task1 ] && echo "We are not in the byteman workshop directory" && return

export BYTEMAN_WORKSHOP="$PWD"
[ ! -d libs ] && mkdir "$BYTEMAN_WORKSHOP/libs"


# JAVA
if [ ! -d "$BYTEMAN_WORKSHOP/libs/jdk1.8.0_161" ]; then
   [ ! -f "$BYTEMAN_WORKSHOP/libs/jdk-8u161-linux-x64.tar.gz" ] &&\
      # echo "Download the 'jdk8u161' from http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html and place it to '$BYTEMAN_WORKSHOP/libs'" && return
      wget -c --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u161-b12/2f38c3b165be4555a1fa6e98c45e0808/jdk-8u161-linux-x64.tar.gz -O "$BYTEMAN_WORKSHOP"/libs/jdk-8u161-linux-x64.tar.gz
  tar -C "$BYTEMAN_WORKSHOP/libs" -xzf "$BYTEMAN_WORKSHOP"/libs/jdk-8u*.tar.gz
fi

export JAVA_HOME="$BYTEMAN_WORKSHOP/libs/jdk1.8.0_161"
export PATH="$JAVA_HOME/bin:$PATH"


# MAVEN
if [ ! -d "$BYTEMAN_WORKSHOP/libs/apache-maven-3.5.2" ]; then
   [ ! -f "$BYTEMAN_WORKSHOP/libs/maven352.zip" ]  &&\
      wget http://www-eu.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.zip -O $BYTEMAN_WORKSHOP/libs/maven352.zip
  unzip -d "$BYTEMAN_WORKSHOP/libs" "$BYTEMAN_WORKSHOP/libs/maven352.zip"
fi

export MAVEN_HOME="$BYTEMAN_WORKSHOP/libs/apache-maven-3.5.2"
export PATH="$MAVEN_HOME/bin:$PATH"

# WILDFLY
# if [ ! -d "$BYTEMAN_WORKSHOP/libs/wildfly-11.0.0.Final" ]; then
#   [ ! -f "$BYTEMAN_WORKSHOP/libs/wildfly11.zip" ] &&\
#       wget http://download.jboss.org/wildfly/11.0.0.Final/wildfly-11.0.0.Final.zip -O "$BYTEMAN_WORKSHOP/libs/wildfly11.zip"
#   unzip -d "$BYTEMAN_WORKSHOP/libs" "$BYTEMAN_WORKSHOP/libs/wildfly11.zip"
# fi
# export JBOSS_HOME="$BYTEMAN_WORKSHOP/libs/wildfly-11.0.0.Final"

# BYTEMAN
if [ ! -d "$BYTEMAN_WORKSHOP/libs/byteman-download-4.0.0" ]; then
   [ ! -f "$BYTEMAN_WORKSHOP/libs/byteman400.zip" ] &&\
       wget http://downloads.jboss.org/byteman/4.0.0/byteman-download-4.0.0-bin.zip -O "$BYTEMAN_WORKSHOP/libs/byteman400.zip"
   unzip -d "$BYTEMAN_WORKSHOP/libs/" "$BYTEMAN_WORKSHOP/libs/byteman400.zip"
fi

export BYTEMAN_HOME="$BYTEMAN_WORKSHOP/libs/byteman-download-4.0.0"
export BYTEMAN_JAR="$BYTEMAN_HOME/lib/byteman.jar"

if [ ! -f "$BYTEMAN_WORKSHOP"/task1/target/byteman-workshop-task*.jar ]; then
  echo ; echo
  echo 'Java compilation'
  mvn install -DskipTests
fi

# check
echo ; echo
echo 'Checking the outcome of the script:'
echo ' >>> mvn -version'
mvn -version
echo ' >>> file $BYTEMAN_JAR'
file "$BYTEMAN_JAR"
echo ' >>> echo $BYTEMAN_HOME'
echo $BYTEMAN_HOME
