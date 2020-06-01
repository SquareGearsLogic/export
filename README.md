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
1.7-2.0.0-7:
- Plugin v1.6 was running since Grails 2.3.8, I'm using 2.5.3 for TravisCI.  
If you don't like that - just change minimum version in application.properties file in your local maven.
- There is back compatibility with original v1.6 in general but...
- I see no reason why column autosizing is not turned-on by default: it perfectly resizes small columns, and doesn't resize large columns too much. So it's turned-on by default now. 
- multiple sheets (only Excel implemented for now):
```groovy
List listWithYourData = [[someFieldName: 42]]
Map labels = ['someFieldName':'Some Field Name']
List fields = ['someFieldName']
Map sheets = ['first sheet title': [fields: fields, labels: labels, rows: listWithYourData,
                                    "column.formats": [ someFieldName: (new ExcelFormat()).TIMES() ]],
              'another sheet': [rows: listWithOtherPageData]]

exportService.export('excel', response, 'myReport', 'xls', sheets)
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
Here is the list of all supported formats:

| What can be changed | Function Name |
|-|-|
| Fonts or .font(WritableFont.*) | ARIAL, TIMES, COURIER, TAHOMA |
| Foreground Colours, or colour(Colour.*), or noColour() or .backColor(Colour.*) or noBackColor() | UNKNOWN, BLACK, WHITE, DEFAULT_BACKGROUND1, DEFAULT_BACKGROUND, PALETTE_BLACK, RED, BRIGHT_GREEN, BLUE, YELLOW, PINK, TURQUOISE, DARK_RED, GREEN, DARK_BLUE, DARK_YELLOW, VIOLET, TEAL, GREY_25_PERCENT, GREY_50_PERCENT, PERIWINKLE, PLUM2, IVORY, LIGHT_TURQUOISE2, DARK_PURPLE', CORAL, OCEAN_BLUE, ICE_BLUE, DARK_BLUE2, PINK2, YELLOW2, TURQOISE2, VIOLET2, DARK_RED2, TEAL2, BLUE2, SKY_BLUE, LIGHT_TURQUOISE, LIGHT_GREEN, VERY_LIGHT_YELLOW', PALE_BLUE, ROSE, LAVENDER, TAN, LIGHT_BLUE, AQUA, LIME, GOLD, LIGHT_ORANGE, ORANGE, BLUE_GREY, GREY_40_PERCENT, DARK_TEAL, SEA_GREEN, DARK_GREEN, OLIVE_GREEN, BROWN, PLUM, INDIGO, GREY_80_PERCENT, AUTOMATIC, GRAY_80, GRAY_50, GRAY_25 |
| Orientations | HORIZONTAL, VERTICAL, PLUS_90, MINUS_90, PLUS_45, MINUS_45, STACKED |
| Horizontal Alignment | GENERAL, LEFT, CENTRE, RIGHT, FILL, H_AUTO |
| Vertical Alignment | TOP, MIDDLE, BOTTOM, V_AUTO |
| Font Weight | pointSize(int)/defaultSize, bold/noBold, struckout/noStruckout, italic/noItalic, underline/underlineSingle/underlineSingleAccounting/underlineDouble/underlineDoubleAccounting/noUnderline |
| Wrappers | wrapText/noWrapText, ident, lock/unlock, srink/expand |

(Learn more about built-in Excel [date](http://jexcelapi.sourceforge.net/resources/javadocs/current/docs/jxl/write/DateFormats.html) and [number](http://jexcelapi.sourceforge.net/resources/javadocs/current/docs/jxl/write/NumberFormats.html) cell formats at JXL site)

It is now possible to set that format for all headers ``"header.format":format`` and/or individually ``"header.formats": [1:column1headerFormat,5:column5headerFormat]``  
- Change column size individually ``"column.widths": [null, 40]`` - here we set it only for second one, the rest will be autosized.
- To disable autosizing set ``'column.width.autoSize':false`` 
- Using Log4j
- Debug logging can be activated py passing "isDebug=true" within "old style parameters" or with sheet params.

1.7-2.0.0-1: 
- initial merge of 1.7 and 2.0.0
- bug: Column autosize applies on size() of rows instead of columns [#28](https://github.com/gpc/export/pull/28)

Installation or Upgrade:
-----------
Since plugin name overlaps with original one, in order to migrate from original 1.6 for the first time, do this:  
- Stop grails;
- Remove 'export:1.6' from your BuildConfig.groovy;
- Clean, run, watch for this notification "``Uninstalled plugin (export)``", wait for build to fail due to missing plugin.

**Option 1)** Preferred way:

- Simply add new plugin to your BuildConfig.groovy in "plugins" scope:
```groovy
  repositories {
    mavenRepo "https://dl.bintray.com/squaregearslogic/pub/"
  }
  plugins {
    compile ('org.sgl:export:1.7-2.0.0-7') {
      excludes 'bcprov-jdk14', 'bcmail-jdk14'    // to support birt-report:4.3 dependency hell
    }
  }
```
or in "dependencies" scope as ``org.sgl:export:zip:1.7-2.0.0-7``
- Run grails.
- For any dependency issues see BuildConfig.groovy in plugin directory.

**Option 2)** manual local/dev installation without maven:  

- Get only .zip file [from latest release](https://github.com/SquareGearsLogic/export/releases/tag/1.7-2.0.0-7)
and simply unzip to ```PROJECT_DIR/.grails/projects/PROJECT_NAME/plugins/export-1.7-2.0.0-7/```
- add line somewhere at the top of your BuildConfig.groovy, outside of plugins scope:
```groovy
grails.plugin.location.export="PROJECT_DIR/.grails/projects/PROJECT_NAME/plugins/export-1.7-2.0.0-7"
```
- Run grails.
- For any dependency issues see BuildConfig.groovy in plugin directory.

**Option 3)** For Maven based projects:
Your pom.xml should look like this:
```
...
  <repositories>
    <repository>
      <id>bintray-squaregearslogic-pub</id>
      <url>https://dl.bintray.com/squaregearslogic/pub</url>
      <releases>
        <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
    </repository>
  </repositories>
  <dependencies>
    <dependency>
      <groupId>org.sgl</groupId>
      <artifactId>export</artifactId>
      <version>1.7-2.0.0-7</version>
      <type>zip</type>
      <scope>test</scope>
    </dependency>
  </dependencies>
...
```

