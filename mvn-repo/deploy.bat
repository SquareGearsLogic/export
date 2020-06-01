@echo off
if exist "mvn-repo\export-1.7-2.0.0-7.zip" (
  del mvn-repo\export-1.7-2.0.0-7.zip
)
7z a -tzip mvn-repo\export-1.7-2.0.0-7.zip grails-app\* scripts\* src\* web-app\* application.properties ExportGrailsPlugin.groovy LICENSE.txt plugin.xml
PUSHD .
cd %~dp0
"C:\Program Files\JetBrains\IntelliJ IDEA\plugins\maven\lib\maven2\bin\mvn" deploy:deploy-file -DgroupId=org.sgl -DartifactId=export -Dversion=1.7-2.0.0-7 -Dpackaging=zip -Dfile=export-1.7-2.0.0-7.zip -DpomFile=export-1.7-2.0.0-7.pom -DrepositoryId=bintray-squaregearslogic-pub -Durl=https://api.bintray.com/maven/squaregearslogic/pub/export/;publish=1
POPD
@echo on