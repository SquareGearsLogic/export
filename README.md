Grails2 Export Plugin
====================

Forked merge of original
[1.7](https://github.com/gpc/export/tree/grails2) 
and latest features from 
[2.0.0](https://github.com/gpc/export)

Why?
-----------
Some Excel features of underlying library that I need are missing in this plugin or not implemented as I want, 
like pages, column styling, etc.  
Also, I'm still working with grails 2, but latest features of 2.0.0, that works only with Grails3, are not merged back to Grails2 version...  
So, first commit of this branch is a pure merge of dead 1.7 and latest 2.0.0.
A new branch will be spawned for every upgrade of base line (if it ever happens at all).

What are the new features?
-----------
1.7-2.0.0-4:
- All existing ExcelFormat constructors now handle formatters as well:
```groovy
new ExcelFormat(..., { domain, value -> ... })
```
1.7-2.0.0-3:
- Chained header and column formatting, like 
```groovy
def cellFormat = (new ExcelFormat()).TAHOMA().bold().noBold().struckout().VIOLET().italic().pointSize(10).wrapText().CENTRE().TOP().MINUS_45().backColor(Colour.AQUA).MIDDLE()
```
It is possible to set that format for all headers ``"header.format":format`` and/or individually ``"header.formats": [1:column1headerFormat,5:column5headerFormat]`` 
- Change column size individually ``"column.widths": [null, 40]`` - here we set it only for second one, the rest will be autosized.
- format can handle cell value type (currency bundles text formatter as well!):
```groovy
def textFormat = new ExcelFormat()
def currencyFormat = new ExcelFormat(NumberFormats.ACCOUNTING_FLOAT)
def dateTimeFormat = new ExcelFormat(DateFormats.FORMAT9)
dateTimeFormat.setFormatter { domain, value -> ... }
```
- new interface to handle multiple sheets (only Excel implemented for now):
```groovy
Map sheets = ['first sheet title': [fields: fields, labels: labels, rows: rows1,
                                    "column.formats": [ someFieldName: (new ExcelFormat()).TIMES() ]]
              'another sheet': [rows:rows2]]

exportService.export(mimeType, response, sheets)
// OR
//setupResponse(type, response, filename, extension)
//exportService.export(mimeType, response.outputStream, sheets)
```
- added ```import groovy.util.logging.Log4j``` annotation

2.1.7-2.0.0-2:
- I see no reason why column autosizing is not turned-on by default: it perfectly resizes small columns, and doesn't resize large columns too much. So it's on by default now. To disable it set ``Map parameters=['column.width.autoSize':false]`` 
- supports column formating. Here is a quick example how to assign "currency" type to a column:
```groovy
Map labels = ['paymentAmt':'Payment Amount']
List fields = ['paymentAmt']
Map parameters = ['column.formats': ['paymentAmt': new WritableCellFormat(NumberFormats.ACCOUNTING_FLOAT)]]
def formatters = ['paymentAmt' : { domain, value ->
                                 String strVal = "${value}".replace('fr.','').replaceAll(/[^\d.]/,'')
                                 return new BigDecimal(strVal?:'0')
                               }
				  ]
List myRows = [['paymentAmt':'$9,876.5432'], ['paymentAmt':'$1,234.5678']]
response.contentType = Holders.config.grails.mime.types[params.exportFormat]
response.setHeader("Content-disposition", "attachment; filename=test.xls")
exportService.export('excel', response.outputStream, myRows, fields, labels, formatters, parameters)
```
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
    compile (':export:1.7-2.0.0-4') {
		excludes 'bcprov-jdk14', 'bcmail-jdk14'    // to support birt-report:4.3 dependency hell
    }
```
- Download and run "install_plugin_export.bat" [from latest release](https://github.com/SquareGearsLogic/export/releases/tag/1.7-2.0.0-4);
- Run grails. It may prompt you for plugin upgrade - say yes.

**Option 2)** manually install zip into local maven repository and install plugin from there, like you normally do:  

- Get .zip and .pom files [from latest release](https://github.com/SquareGearsLogic/export/releases/tag/1.7-2.0.0-4)
- If you running Grails < v2.3 you can use 
```
grails install-plugin
```

If you running Grails between v2.3 and 3.0, you should use maven itself:
```
mvn install:install-file -Dfile=export-1.7-2.0.0-4.zip -DgroupId=org.grails.plugins -DartifactId=export -Dversion=1.7-2.0.0-4 -Dpackaging=zip
```
- Go to folder
```
Linux: ~/.m2/repository/org/grails/plugins/export/1.7-2.0.0-4/
Windows: %HOMEPATH%\.m2\repository\org\grails\plugins\export\1.7-2.0.0-4\
```
and replace pom file with the one from release (If anyone knows how to integrate pom into zip - please let me know).

- Add new plugin to your BuildConfig.groovy normally:
```groovy
    compile (':export:1.7-2.0.0-4') {
		excludes 'bcprov-jdk14', 'bcmail-jdk14'    // to support birt-report:4.3 dependency hell
    }
```
- Run grails. It may prompt you for plugin upgrade - say yes.

**Option 3)** manual local/dev installation without maven:  

- Get only .zip file [from latest release](https://github.com/SquareGearsLogic/export/releases/tag/1.7-2.0.0-4)
and simply unzip to ```PROJECT_DIR/.grails/projects/cwa/plugins/export-1.7-2.0.0-4/```
- add line somewhere at the top of your BuildConfig.groovy, outside of plugins scope:
```
grails.plugin.location.export="PROJECT_DIR/.grails/projects/cwa/plugins/export-1.7-2.0.0-4"
```
- Run grails.
- For any dependency issues see BuildConfig.groovy in plugin directory.
