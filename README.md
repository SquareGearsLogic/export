Grails2 Export Plugin
====================

Forked merge of original
[1.7](https://github.com/gpc/export/tree/grails2) 
and latest features from 
[2.0.0](https://github.com/gpc/export)

Why?
-----------
Some features of underlying library that I need are missing in this plugin or not implemented as I want,
like pages, column styling, etc. 
Also, I'm still working with grails 2, but latest features of 2.0.0, that works only with Grails3, are not merged back to Grails2 version...
So, first commit of this branch is a pure merge of dead 1.7 and latest 2.0.0.
A new branch will be spawned for every upgrade of base line (if it ever happends at all).

What are the new features?
-----------
1.7-2.0.0-2:
- I see no reason why column autosizing is not turned-on by default: it perfectly resizes small columns, and doesn't resize large collumns too much. So it's on by default now. To disable it set ```Map parameters=['column.width.autoSize':false]``` 
- supports column formating. Here is a quick example how to assign "currenty" type to a column:
```groovy
    Map labels = ['paymentAmt':'Payment Amount']
    List fields = ['paymentAmt']
Map parameters = ["column.formats": [paymentAmt: new WritableCellFormat(NumberFormats.ACCOUNTING_FLOAT)]]
def formatters = [paymentAmt : { domain, value ->
    String strVal = "${value}".replace('fr.','').replaceAll(/[^\d.]/,'')
    return new BigDecimal(strVal?:'0')
  }]

    response.contentType = Holders.config.grails.mime.types[params.exportFormat]
    response.setHeader("Content-disposition", "attachment; filename=test.xls")
    exportService.export('excel', response.outputStream, myList, fields, labels, formatters, parameters)
```
1.7-2.0.0-1: 
- initial merge of 1.7 and 2.0.0
- bug: Column autosize applies on size() of rows instead of columns [#28](https://github.com/gpc/export/pull/28)

Installation or Upgrade:
-----------
Remove 'export:1.6' from your BuildConfig.groovy
clean, run, watch for this notification:
```"Uninstalled plugin (export)"```
cancel once you see it... or wait to fail build because of the missing plugin.

**Option 1)** local/dev installation:

Simply unzip files to ```PROJECT_DIR/.grails/projects/cwa/plugins/export-1.7-2.0.0-2/```
and add line somewhere at the top of your BuildConfig.groovy, outside of plugins scope
```
grails.plugin.location.export="PROJECT_DIR/.grails/projects/cwa/plugins/export-1.7-2.0.0-2"
```
For any dependency issues see BuildConfig.groovy in plugin directory.

**Option 2)** install zip into local maven repository and install plugin from there, like you normally do:

If you running Grails < v2.3 you can use 
```
grails install-plugin
```

If you running Grails between v2.3 and 3.0 do this:
```
mvn install:install-file -Dfile=export-1.7-2.0.0-2.zip -DgroupId=org.grails.plugins -DartifactId=export -Dversion=1.7-2.0.0-2 -Dpackaging=zip
```
go to folder
```
Linux: ~/.m2/repository/org/grails/plugins/export/1.7-2.0.0-2/
Windows: %HOMEPATH%\.m2\repository\org\grails\plugins\export\1.7-2.0.0-2\
```
and replace pom file with the one from release (If anyone knows how to integrate pom into zip - please let me know).

Add new plugin to your BuildConfig.groovy normally:
```
    compile (':export:1.7-2.0.0-2') {
      excludes 'itext', 'itext-rtf'    // to support birt-report:4.3 dependency hell
    }
```
