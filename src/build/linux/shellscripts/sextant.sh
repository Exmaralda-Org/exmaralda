#!/bin/sh

# WORKINGDIR=${0%/*}
WORKINGDIR=$(dirname "$0")

DYLD_LIBRARY_PATH=$WORKINGDIR
export DYLD_LIBRARY_PATH

JAVA_CMD="$WORKINGDIR/java-8-oracle/bin/java"

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
CP_JAVA_6=$WORKINGDIR/lib/commons-logging-1.2.jar:$WORKINGDIR/lib/swing-worker-1.2.jar:$WORKINGDIR/lib/mysql-connector-java-5.1.6-bin.jar::$WORKINGDIR/lib/BATIK.jar    

# ALL COMBINED
CP_JAVA=$CP_JAVA_1:$CP_JAVA_2:$CP_JAVA_3:$CP_JAVA_4:$CP_JAVA_5:$CP_JAVA_6:

echo $JAVA_CMD -classpath $CP_JAVA org.exmaralda.sextanttagger.launch.SextantLauncher
exec $JAVA_CMD -classpath $CP_JAVA org.exmaralda.sextanttagger.launch.SextantLauncher
