package de.andreasschmitt.export.exporter

import de.andreasschmitt.export.builder.ExcelBuilder
import groovy.util.logging.Log4j
import jxl.format.Alignment
import jxl.format.Colour

/**
 * @author Andreas Schmitt
 *
 */
@Log4j
class DefaultExcelExporter extends AbstractExporter {

  private static final int MAX_PER_SHEET = 60000 // See https://github.com/gpc/export/pull/23

  /**
   * Legacy interface that spills a single sheet into a workbook.
   * @param outputStream  - Where to;
   * @param data          - List of Objects(Maps) rows in sheet;
   * @param fields        - List<String> header names.
   * @throws ExportingException
   * TODO: optimize this code with exportSheets
   */
  protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException {
    try {
      ExcelBuilder builder = new ExcelBuilder(this.parameters.isDebug)

      // Enable/Disable header output
      boolean isHeaderEnabled = true
      if (getParameters().containsKey("header.enabled")) {
        isHeaderEnabled = getParameters().get("header.enabled")
      }

      boolean useZebraStyle = false
      if (getParameters().containsKey("zebraStyle.enabled")) {
        useZebraStyle = getParameters().get("zebraStyle.enabled")
      }

      boolean widthAutoSize = getParameters().get("column.width.autoSize")!=false

      int maxPerSheet = MAX_PER_SHEET
      if (getParameters().containsKey("max.rows.persheet")) {
        maxPerSheet = getParameters().get("max.rows.persheet")
        maxPerSheet = maxPerSheet < MAX_PER_SHEET ? maxPerSheet : MAX_PER_SHEET
      }

      String title = getParameters().get("title")?:'Sheet'
      Map labels = fields.collectEntries{[it,getLabel(it)]}
      builder {
        workbook(outputStream: outputStream) {
          processSheet(getDelegate() as ExcelBuilder, title, data, fields, labels,
              isHeaderEnabled, useZebraStyle, widthAutoSize, maxPerSheet, getParameters())
        }
      }

      builder.write()
    }
    catch (Exception e) {
      throw new ExportingException("Error during export", e)
    }
  }

  /**
   * Writes multiple sheets into a workbook
   * @param outputStream  - Where to;
   * @param sheets        - Map of sheet title and its configuration, that is also a Map of:
   *        ~ fields                - row Object(Map) fields to export;
   *        ~ labels                - aliases for fields;
   *        ~ header.enabled        - boolean show/hide header;
   *        ~ titles.mergeCells     - merge all header cells;
   *        ~ zebraStyle.enabled    - boolean striped table;
   *        ~ max.rows.persheet     - boolean breaks down data rows into multiple enumerated sheets with same title (default is MAX_PER_SHEET);
   *        ~ column.width.autoSize - boolean default is true;
   *        ~ column.widths         - list of column widths, Nulls will apply 'column.width.autoSize';
   *        ~ rows                  - List of Objects(Maps) rows in sheet;
   *        ~ column.formats        - ExcelFormat type of cell, like date, currency, text, etc... (default is text);
   *        ~ column.formatters     - Closure that formats cell value { domain, value -> return value };
   *        ~ header.format         - ExcelFormat to format all headers same way;
   *        ~ header.formats        - Map of header index and its ExcelFormat, so it's a 'header.format' per column.
   * @throws ExportingException
   */
  protected void exportSheets(OutputStream outputStream, Map sheets) throws ExportingException {
    Boolean isDebug = sheets.find { title, Map sheetParams -> sheetParams.isDebug }
    ExcelBuilder builder = new ExcelBuilder(isDebug)
    builder {
      workbook(outputStream: outputStream) {
        ExcelBuilder currentWorkbook = getDelegate()
        sheets.each { title, Map sheetParams ->
          boolean isHeaderEnabled = true
          if (sheetParams.containsKey("header.enabled")) {
            isHeaderEnabled = sheetParams.get("header.enabled")
          }

          boolean useZebraStyle = false
          if (sheetParams.containsKey("zebraStyle.enabled")) {
            useZebraStyle = sheetParams.get("zebraStyle.enabled")
          }

          boolean widthAutoSize = sheetParams.get("column.width.autoSize")!=false

          int maxPerSheet = MAX_PER_SHEET
          if (sheetParams.containsKey("max.rows.persheet")) {
            maxPerSheet = sheetParams.get("max.rows.persheet")
            maxPerSheet = maxPerSheet < MAX_PER_SHEET ? maxPerSheet : MAX_PER_SHEET
          }

          processSheet(currentWorkbook, (title?:"Export") as String, sheetParams.rows as List, sheetParams.fields as List,  sheetParams.labels as Map,
              isHeaderEnabled, useZebraStyle, widthAutoSize, maxPerSheet, sheetParams)
        }
      }
    }

    builder.write()
  }

  private void processSheet(ExcelBuilder workbook, String sheetName, List data, List fields, Map labels,
                            boolean isHeaderEnabled, boolean useZebraStyle, boolean widthAutoSize, int maxPerSheet, Map sheetParams){
    def (sheets, limitPerSheet) = computeSheetsAndLimit(data, maxPerSheet)
    def startIndex = 0
    def endIndex = limitPerSheet
    for (int j = 1; j <= sheets; j++) {
      def dataPerSheet = data.subList(startIndex, endIndex)

      processMaxRowsPerSheet (workbook, sheetName + (sheets>1?"-$j":''),
          dataPerSheet, fields, labels, isHeaderEnabled, useZebraStyle, widthAutoSize, sheetParams)

      startIndex = endIndex
      endIndex = endIndex + limitPerSheet > data.size() ? data.size() : endIndex + limitPerSheet
    }
  }

  private void processMaxRowsPerSheet(ExcelBuilder workbook, String sheetName, List data, List fields, Map labels,
                            boolean isHeaderEnabled, boolean useZebraStyle, boolean widthAutoSize, Map sheetParams){
    workbook.sheet (name: sheetName,
                    widths: sheetParams.get("column.widths"),
                    numberOfFields: fields.size(),
                    widthAutoSize: widthAutoSize) {

      format(name: "title") {
        Alignment alignment = Alignment.GENERAL
        if (sheetParams.containsKey('titles.alignment')) {
          alignment = Alignment."${sheetParams.get('titles.alignment')}"
        }
        font(name: "arial", bold: true, size: 14, alignment: alignment)
      }

      format(name: "header") {
        if (useZebraStyle) {
          font(name: "arial", bold: true, backColor: Colour.GRAY_80, foreColor: Colour.WHITE, useBorder: true)
        } else {
          // Use default header format
          font(name: "arial", bold: true)
        }
      }
      format(name: "odd") {
        font(backColor: Colour.GRAY_25, useBorder: true)
      }
      format(name: "even") {
        font(backColor: Colour.WHITE, useBorder: true)
      }

      int rowIndex = 0

      // Option for titles on top of data table
      def titles = sheetParams.get("titles")
      titles.each {
        cell(row: rowIndex, column: 0, value: it, format: "title")
        rowIndex++
      }

      //Create header
      if (isHeaderEnabled) {
        final def headerFormat=sheetParams.get("header.format")
        final Map headerFormats=sheetParams.get("header.formats")
        //WritableCellFormat format = headerFormats.containsKey() headerFormat

        fields.eachWithIndex { field, index ->
          def format = headerFormats?.get(index)?:headerFormat
          String value = labels.containsKey(field) ? labels[field] : field
          cell(row: rowIndex, column: index, value: value, format: format?:"header")
        }

        rowIndex++
      }

      final Map columnFormats=sheetParams.get("column.formats")
      //Rows
      data.eachWithIndex { object, k ->

        fields.eachWithIndex { field, i ->
          def format = useZebraStyle ? ( (k % 2) == 0 ? "even" : "odd" ) : ''
          format = columnFormats?.containsKey(field)?columnFormats[field]:format
          if (format?.hasProperty('formatter') && format.formatter && !formatters.containsKey(field))
            formatters.put(field, format.formatter)

          Object value = getValue(object, field)
          cell(row: k + rowIndex, column: i, value: value, format: format)
        }
      }

      if (sheetParams.get('titles.mergeCells')) {
        //Merge title cells
        titles.eachWithIndex { title, index ->
          mergeCells(startColumn: 0, startRow: index, endColumn: fields.size(), endRow: index)
        }
      }
    }

  }

  private static List computeSheetsAndLimit(List data, Integer maxPerSheet) {
    if (!data)
      throw new ExportingException("Error during export: Empty data!")

    def limitPerSheet = data.size() > maxPerSheet ? maxPerSheet : data.size()
    def sheetsCount = Math.ceil(data.size() / limitPerSheet)
    log.debug("limitPerSheet:$limitPerSheet ::: sheetsCount:$sheetsCount")
    return [sheetsCount, limitPerSheet]
  }

}