#!/bin/sh

# WORKINGDIR=${0%/*}
WORKINGDIR=$(dirname "$0")

DYLD_LIBRARY_PATH=$WORKINGDIR
export DYLD_LIBRARY_PATH

JAVA_CMD="$JAVA_HOME/bin/java"
if [ -z "$JAVA_HOME" ]; then
    JAVA_CMD="java"
fi

CP_JAVA=$WORKINGDIR/swing-worker-1.2.jar:$WORKINGDIR/AppleJavaExtensions.jar:$WORKINGDIR/jcalendar-1.3.2.jar:$WORKINGDIR/xercesImpl.jar:$WORKINGDIR/xml-apis.jar:$WORKINGDIR/xmlParserAPIs.jar:$WORKINGDIR/jaxen-1.1.1.jar:$WORKINGDIR/jmf.jar:$WORKINGDIR/fobs4jmf.jar:$WORKINGDIR/EXMARaLDA.jar:$WORKINGDIR/jl1.0.jar:$WORKINGDIR/mp3spi1.9.4.jar:$WORKINGDIR/tritonus_share.jar:$WORKINGDIR/basicplayer3.0.jar:$WORKINGDIR/jctable.jar:$WORKINGDIR/commons-logging-1.1.jar:$WORKINGDIR/commons-lang-2.5.jar:$WORKINGDIR/xalan.jar:$WORKINGDIR/saxon8.jar:$WORKINGDIR/jdom.jar:$WORKINGDIR/mrj.jar

echo $JAVA_CMD -classpath $CP_JAVA org.exmaralda.coma.launcher.Launcher $1
exec $JAVA_CMD -classpath $CP_JAVA org.exmaralda.coma.launcher.Launcher $1
