Grails2 Export Plugin [![Build Status](https://travis-ci.com/SquareGearsLogic/export.svg?branch=grails2-1.7-2.0.0)](https://travis-ci.com/SquareGearsLogic/export)
====================

This is a forked merge of 
[v1.7](https://github.com/gpc/export/tree/grails2) (for Grails2)
and 
[v2.0.0](https://github.com/gpc/export) (for grails3) with some extra bells and whistles.  
- [Original v2.0.0 documentation](http://gpc.github.io/export/)  
- Original v1.6 for Grails2 is the latest [available on maven](https://mvnrepository.com/artifact/org.grails.plugins/export/1.6)

Why?
-----------
Some Excel features of underlying [JXL 2.6.12](http://jexcelapi.sourceforge.net/) library that I need are missing in this plugin or not implemented as I want, 
like pages, column styling, etc.  
Also, latest features and bugfixes are only available for Grails3 version of the plugin, and not backported to Grails2 version...  
So, first commit of this branch is a pure merge of dead 1.7 and latest 2.0.0.
A new branch will be spawned for every upgrade of base line (if it ever happens at all).

What are the new features?
-----------
1.7-2.0.0-5:
- 1.6 was running since Grails 2.3.8, I'm using 2.5.3 for TravisCI.  
If you don't like that - just change minimum version in application.properties file in your local maven.
- There is back compatibility with original v1.6 in general but...
- I see no reason why column autosizing is not turned-on by default: it perfectly resizes small columns, and doesn't resize large columns too much. So it's turned-on by default now. 
- multiple sheets (only Excel implemented for now):
```groovy
Map labels = ['someFieldName':'Some Field Name']
List fields = ['someFieldName']
Map sheets = ['first sheet title': [fields: fields, labels: labels, rows: listWithYourData,
                                    "column.formats": [ someFieldName: (new ExcelFormat()).TIMES() ]],
              'another sheet': [rows: listWithOtherPageData]]

exportService.export(mimeType, response, sheets)
// OR
//setupResponse(type, response, filename, extension)
//exportService.export(mimeType, response.outputStream, sheets)
// Old style still works fine, if someone still uses it:
// response.contentType = Holders.config.grails.mime.types[params.exportFormat]
// response.setHeader("Content-disposition", "attachment; filename=test.xls")
// exportService.export('excel', response.outputStream, myRows, fields, labels, formatters, parameters)
```

- Columns can be fancy and handle native formating. Here are some more examples:
```groovy
def textFormat = new ExcelFormat()
def currencyFormat = new ExcelFormat(NumberFormats.ACCOUNTING_FLOAT)
def dateTimeFormat = new ExcelFormat(DateFormats.FORMAT9)
dateTimeFormat.setFormatter { domain, value -> ... }
def sameInOneLine = new ExcelFormat(DateFormats.FORMAT9, { domain, value -> ... })
def fancyFormat = (new ExcelFormat()).TAHOMA().bold().noBold().struckout().VIOLET().italic().pointSize(10).wrapText().CENTRE().TOP().MINUS_45().backColor(Colour.AQUA).MIDDLE()
```
Read more about built-in Excel [date](http://jexcelapi.sourceforge.net/resources/javadocs/current/docs/jxl/write/DateFormats.html) and [number](http://jexcelapi.sourceforge.net/resources/javadocs/current/docs/jxl/write/NumberFormats.html) cell formats at JXL site.  
Now it is possible to set that format for all headers ``"header.format":format`` and/or individually ``"header.formats": [1:column1headerFormat,5:column5headerFormat]``  
- Change column size individually ``"column.widths": [null, 40]`` - here we set it only for second one, the rest will be autosized.
- To disable autosizing set ``'column.width.autoSize':false`` 
- Using Log4j

1.7-2.0.0-1: 
- initial merge of 1.7 and 2.0.0
- bug: Column autosize applies on size() of rows instead of columns [#28](https://github.com/gpc/export/pull/28)

Installation or Upgrade:
-----------
This plugin is not available on maven yet, so...  
For the first time, to migrate from original 1.6:  
- Stop grails;
- Remove 'export:1.6' from your BuildConfig.groovy;
- Clean, run, watch for this notification "``Uninstalled plugin (export)``"
cancel once you see it... or wait for build to fail due to missing plugin.

**Option 1)** use "install_plugin_export.bat" that automates "option 2" process:  

- Stop grails;
- Add new plugin to your BuildConfig.groovy normally:
```groovy
    compile (':export:1.7-2.0.0-5') {
		excludes 'bcprov-jdk14', 'bcmail-jdk14'    // to support birt-report:4.3 dependency hell
    }
```
- Download and run "install_plugin_export.bat" [from latest release](https://github.com/SquareGearsLogic/export/releases/tag/1.7-2.0.0-5);
- Run grails. It may prompt you for plugin upgrade - say yes.

**Option 2)** manually install zip into local maven repository and install plugin from there, like you normally do:  

- Get .zip and .pom files [from latest release](https://github.com/SquareGearsLogic/export/releases/tag/1.7-2.0.0-5)
- If you running Grails < v2.3 you can use 
```
grails install-plugin
```

If you running Grails between v2.3 and 3.0, you should use maven itself:
```
mvn install:install-file -Dfile=export-1.7-2.0.0-5.zip -DgroupId=org.grails.plugins -DartifactId=export -Dversion=1.7-2.0.0-5 -Dpackaging=zip
```
- Go to folder
```
Linux: ~/.m2/repository/org/grails/plugins/export/1.7-2.0.0-5/
Windows: %HOMEPATH%\.m2\repository\org\grails\plugins\export\1.7-2.0.0-5\
```
and replace pom file with the one from release (If anyone knows how to integrate pom into zip - please let me know).

- Add new plugin to your BuildConfig.groovy normally:
```groovy
    compile (':export:1.7-2.0.0-5') {
		excludes 'bcprov-jdk14', 'bcmail-jdk14'    // to support birt-report:4.3 dependency hell
    }
```
- Run grails. It may prompt you for plugin upgrade - say yes.

**Option 3)** manual local/dev installation without maven:  

- Get only .zip file [from latest release](https://github.com/SquareGearsLogic/export/releases/tag/1.7-2.0.0-5)
and simply unzip to ```PROJECT_DIR/.grails/projects/PROJECT_NAME/plugins/export-1.7-2.0.0-5/```
- add line somewhere at the top of your BuildConfig.groovy, outside of plugins scope:
```
grails.plugin.location.export="PROJECT_DIR/.grails/projects/PROJECT_NAME/plugins/export-1.7-2.0.0-5"
```
- Run grails.
- For any dependency issues see BuildConfig.groovy in plugin directory.
