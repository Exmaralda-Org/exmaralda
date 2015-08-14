#!/bin/sh

# this shell script has been tested successfully on SUSE Linux with Java 6
# this shell script has been tested successfully on UBUNTU Linux with Java 6

# WORKINGDIR=${0%/*}
WORKINGDIR=$(dirname "$0")

DYLD_LIBRARY_PATH=$WORKINGDIR
export DYLD_LIBRARY_PATH

JAVA_CMD="$JAVA_HOME/bin/java"
if [ -z "$JAVA_HOME" ]; then
    JAVA_CMD="java"
fi

CP_JAVA=$WORKINGDIR/ipsk.jar:$WORKINGDIR/xercesImpl.jar:$WORKINGDIR/AppleJavaExtensions.jar:$WORKINGDIR/xml-apis.jar:$WORKINGDIR/xmlParserAPIs.jar:$WORKINGDIR/jaxen-1.1.1.jar:$WORKINGDIR/jmf.jar:$WORKINGDIR/fobs4jmf.jar:$WORKINGDIR/EXMARaLDA.jar:$WORKINGDIR/basicplayer3.0.jar:$WORKINGDIR/jctable.jar:$WORKINGDIR/commons-logging-1.1.jar:$WORKINGDIR/xalan.jar:$WORKINGDIR/saxon8.jar:$WORKINGDIR/jdom.jar:$WORKINGDIR/swing-worker-1.2.jar:$WORKINGDIR/QTJava.zip:$WORKINGDIR/mysql-connector-java-5.1.6-bin.jar

echo $JAVA_CMD -classpath $CP_JAVA org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT
exec $JAVA_CMD -classpath $CP_JAVA org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT


