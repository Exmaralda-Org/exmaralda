:: Go to the current directory

cd /D "%~dp0"

set LIB_DIRECTORY=.\lib
set NATIVE_LIB_DIRECTORY=%LIB_DIRECTORY%\native

:: set the path to the java.exe the first line is when java is set as a system variable
:: the other lines should point to the actual path
set JAVA_CMD=java
:: set JAVA_CMD="C:\Program Files\Java\jdk-17.0.3+7\bin\java.exe"

:: BASE
set CP_JAVA_1=EXMARaLDA_nb.jar;.\lib\jctable.jar

:: XML
set CP_JAVA_2=.\lib\jdom.jar;.\lib\xalan.jar;.\lib\xercesImpl.jar;.\lib\saxon9.jar;.\lib\saxon9-dom.jar;.\lib\serializer.jar;.\lib\jaxen-1.1.6.jar;.\lib\jaxb-api-2.3.0.jar

:: JSON
set CP_JAVA_3=.\lib\jackson-annotations-2.9.10.jar;.\lib\jackson-core-2.9.10.jar;.\lib\jackson-databind-2.9.10.jar

:: AUDIO\VIDEO
set CP_JAVA_4=.\lib\ips.audiotools.jar;.\lib\ips.commons.jar;.\lib\elan-6.4.jar
set CP_JAVA_4b=.\lib\javafx-base-18.0.1-win.jar;.\lib\javafx-controls-18.0.1-win.jar;.\lib\javafx-graphics-18.0.1-win.jar;.\lib\javafx-media-18.0.1-win.jar;.\lib\javafx-swing-18.0.1-win.jar

:: HTTP CLIENT
set CP_JAVA_5=.\lib\httpcore-4.4.3.jar;.\lib\httpclient-4.5.1.jar;.\lib\httpmime-4.5.1.jar

:: OTHER STUFF
set CP_JAVA_6=.\lib\commons-logging-1.2.jar;.\lib\BATIK.jar;.\lib\tt4j.jar;.\lib\weblicht.jar;.\lib\subtitle-0.9.2-SNAPSHOT-jar-with-dependencies.jar

:: ALL COMBINED
set CP_JAVA=%CP_JAVA_1%;%CP_JAVA_2%;%CP_JAVA_3%;%CP_JAVA_4%;%CP_JAVA_4b%;%CP_JAVA_5%;%CP_JAVA_6%

%JAVA_CMD% -classpath %CP_JAVA% -Djava.library.path=%NATIVE_LIB_DIRECTORY% org.exmaralda.partitureditor.partiture.PartiturEditor %1
