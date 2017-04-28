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
JAVA_CMD="$WORKINGDIR/java-8-oracle/bin/java"


#CP_JAVA=$WORKINGDIR/lib/ipsk.jar:$WORKINGDIR/lib/ojdbc6.jar:$WORKINGDIR/lib/QTJava.zip:$WORKINGDIR/lib/AppleJavaExtensions.jar:$WORKINGDIR/lib/xercesImpl.jar:$WORKINGDIR/lib/xml-apis.jar:$WORKINGDIR/lib/xmlParserAPIs.jar:$WORKINGDIR/lib/jaxen-1.1.1.jar:$WORKINGDIR/lib/jmf.jar:$WORKINGDIR/lib/fobs4jmf.jar:$WORKINGDIR/lib/EXMARaLDA.jar:$WORKINGDIR/lib/jl1.0.jar:$WORKINGDIR/lib/mp3spi1.9.4.jar:$WORKINGDIR/lib/tritonus_share.jar:$WORKINGDIR/lib/basicplayer3.0.jar:$WORKINGDIR/lib/jctable.jar:$WORKINGDIR/lib/commons-logging-1.1.jar:$WORKINGDIR/lib/xalan.jar:$WORKINGDIR/lib/saxon8.jar:$WORKINGDIR/lib/jdom.jar
# BASE
CP_JAVA_1=$WORKINGDIR/lib/EXMARaLDA.jar:$WORKINGDIR/lib/jctable.jar
# XML
CP_JAVA_2=$WORKINGDIR/lib/jdom.jar:$WORKINGDIR/lib/xalan.jar:$WORKINGDIR/lib/xercesImpl.jar:$WORKINGDIR/lib/xml-apis.jar:$WORKINGDIR/lib/saxon9he.jar:$WORKINGDIR/lib/serializer.jar:$WORKINGDIR/lib/jaxen-1.1.6.jar
# MAC OS
CP_JAVA_3=$WORKINGDIR/lib/mrj.jar:$WORKINGDIR/lib/AppleJavaExtensions.jar:$WORKINGDIR/lib/QTJava.zip
# AUDIO/VIDEO
CP_JAVA_4=$WORKINGDIR/lib/fobs4jmf.jar:$WORKINGDIR/lib/jmf.jar:$WORKINGDIR/lib/ipsk.jar:$WORKINGDIR/lib/elan.jar:$WORKINGDIR/lib/tritonus_share-0.3.6.jar
# HTTP CLIENT
CP_JAVA_5=$WORKINGDIR/lib/httpcore-4.4.3.jar:$WORKINGDIR/lib/httpclient-4.5.1.jar:$WORKINGDIR/lib/httpmime-4.5.1.jar
# OTHER STUFF
CP_JAVA_6=$WORKINGDIR/lib/commons-logging-1.2.jar:$WORKINGDIR/lib/swing-worker-1.2.jar:$WORKINGDIR/lib/bounce.jar
    
# ALL COMBINED
CP_JAVA=$CP_JAVA_1:$CP_JAVA_2:$CP_JAVA_3:$CP_JAVA_4:$CP_JAVA_5:$CP_JAVA_6


echo $JAVA_CMD -classpath $CP_JAVA -Djava.library.path=$DYLD_LIBRARY_PATH org.exmaralda.orthonormal.application.ApplicationFrame
exec $JAVA_CMD -classpath $CP_JAVA -Djava.library.path=$DYLD_LIBRARY_PATH org.exmaralda.orthonormal.application.ApplicationFrame
