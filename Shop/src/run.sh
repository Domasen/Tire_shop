#!/bin/sh
export CLASSPATH=$CLASSPATH:/usr/share/java/postgresql.jar
javac Main.java UI.java DB.java
java Main