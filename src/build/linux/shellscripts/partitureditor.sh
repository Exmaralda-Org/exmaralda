#!/bin/sh

# this shell script has been tested successfully on SUSE Linux with Java 6
# this shell script has been tested successfully on UBUNTU Linux with Java 6


# WORKINGDIR=${0%/*}
WORKINGDIR=$(dirname "$0")

DYLD_LIBRARY_PATH=$WORKINGDIR/lib
export DYLD_LIBRARY_PATH

# NEW 16-12-2015: Use embedded JRE
# JAVA_CMD="$JAVA_HOME/bin/java"
# if [ -z "$JAVA_HOME" ]; then
#     JAVA_CMD="java"
# fi

JAVA_CMD="$WORKINGDIR/adoptopenjdk-11-hotspot-amd64/bin/java"

# BASE
CP_JAVA_1=$WORKINGDIR/lib/EXMARaLDA.jar:$WORKINGDIR/lib/jctable.jar

# XML
CP_JAVA_2=$WORKINGDIR/lib/jdom.jar:$WORKINGDIR/lib/xalan.jar:$WORKINGDIR/lib/xercesImpl.jar:$WORKINGDIR/lib/saxon9.jar:$WORKINGDIR/lib/saxon9-dom.jar:$WORKINGDIR/lib/serializer.jar:$WORKINGDIR/lib/jaxen-1.1.6.jar:$WORKINGDIR/lib/jaxb-api-2.3.0.jar


# AUDIO/VIDEO
CP_JAVA_4=$WORKINGDIR/lib/ips.audiotools.jar:$WORKINGDIR/lib/ips.commons.jar:$WORKINGDIR/lib/elan-5.8.jar
CP_JAVA_4b=$WORKINGDIR/lib/javafx-base-11.jar:$WORKINGDIR/lib/javafx-base-11-linux.jar:$WORKINGDIR/lib/javafx-controls-11.jar:$WORKINGDIR/lib/javafx-graphics-11.jar:$WORKINGDIR/lib/javafx-graphics-11-linux.jar:$WORKINGDIR/lib/javafx-media-11.jar:$WORKINGDIR/lib/javafx-media-11-linux.jar:$WORKINGDIR/lib/javafx-swing-11.jar:$WORKINGDIR/lib/javafx-swing-11-linux.jar

# HTTP CLIENT
CP_JAVA_5=$WORKINGDIR/lib/httpcore-4.4.3.jar:$WORKINGDIR/lib/httpclient-4.5.1.jar:$WORKINGDIR/lib/httpmime-4.5.1.jar

# OTHER STUFF
CP_JAVA_6=$WORKINGDIR/lib/commons-logging-1.2.jar:$WORKINGDIR/lib/BATIK.jar:$WORKINGDIR/lib/tt4j.jar:$WORKINGDIR/lib/weblicht.jar

    
# ALL COMBINED
CP_JAVA=$CP_JAVA_1:$CP_JAVA_2:$CP_JAVA_4:$CP_JAVA_4b:$CP_JAVA_5:$CP_JAVA_6


echo $JAVA_CMD -classpath $CP_JAVA -Djava.library.path=$DYLD_LIBRARY_PATH org.exmaralda.partitureditor.partiture.PartiturEditor $1
exec $JAVA_CMD -classpath $CP_JAVA -Djava.library.path=$DYLD_LIBRARY_PATH org.exmaralda.partitureditor.partiture.PartiturEditor $1
