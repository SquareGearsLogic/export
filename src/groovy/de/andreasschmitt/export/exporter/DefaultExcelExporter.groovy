package de.andreasschmitt.export.exporter

import de.andreasschmitt.export.builder.ExcelBuilder
import groovy.util.logging.Log4j
import jxl.format.Alignment
import jxl.format.Colour
import jxl.write.WritableCellFormat

/**
 * @author Andreas Schmitt
 *
 */
@Log4j
class DefaultExcelExporter extends AbstractExporter {

  private static final int MAX_PER_SHEET = 60 //000 // See https://github.com/gpc/export/pull/23

  protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException {
    try {
      def builder = new ExcelBuilder()

      // Enable/Disable header output
      boolean isHeaderEnabled = true
      if (getParameters().containsKey("header.enabled")) {
        isHeaderEnabled = getParameters().get("header.enabled")
      }

      boolean useZebraStyle = false
      if (getParameters().containsKey("zebraStyle.enabled")) {
        useZebraStyle = getParameters().get("zebraStyle.enabled")
      }

      int maxPerSheet = MAX_PER_SHEET
      if (getParameters().containsKey("max.rows.persheet")) {
        maxPerSheet = getParameters().get("max.rows.persheet")
        maxPerSheet = maxPerSheet < MAX_PER_SHEET ? maxPerSheet : MAX_PER_SHEET
      }

      def (sheets, limitPerSheet) = computeSheetsAndLimit(data, maxPerSheet)
      def startIndex = 0
      def endIndex = limitPerSheet

      builder {
        workbook(outputStream: outputStream) {
          for (int j = 1; j <= sheets; j++) {
            def dataPerSheet = data.subList(startIndex, endIndex)

            processSheet (getDelegate(), getParameters().get("title") + "-$j" ?: "Export-$j",
                          dataPerSheet, fields,
                          isHeaderEnabled, useZebraStyle,
                          getParameters().get("column.width.autoSize")!=false)

            startIndex = endIndex
            endIndex = endIndex + limitPerSheet > data.size() ? data.size() : endIndex + limitPerSheet
          }
        }
      }

      builder.write()
    }
    catch (Exception e) {
      throw new ExportingException("Error during export", e)
    }
  }

  private void processSheet(ExcelBuilder workbook, String sheetName, List data, List fields,
                            boolean isHeaderEnabled, boolean useZebraStyle, boolean widthAutoSize){
    workbook.sheet (name: sheetName,
                    widths: getParameters().get("column.widths"),
                    numberOfFields: fields.size(),
                    widthAutoSize: widthAutoSize) {

      format(name: "title") {
        Alignment alignment = Alignment.GENERAL
        if (getParameters().containsKey('titles.alignment')) {
          alignment = Alignment."${getParameters().get('titles.alignment')}"
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
      def titles = getParameters().get("titles")
      titles.each {
        cell(row: rowIndex, column: 0, value: it, format: "title")
        rowIndex++
      }

      //Create header
      if (isHeaderEnabled) {
        final def headerFormat=getParameters().get("header.format")
        final Map headerFormats=getParameters().get("header.formats")
        //WritableCellFormat format = headerFormats.containsKey() headerFormat

        fields.eachWithIndex { field, index ->
          def format = headerFormats.get(index)?:headerFormat
          String value = getLabel(field)
          cell(row: rowIndex, column: index, value: value, format: format?:"header")
        }

        rowIndex++
      }

      final Map columnFormats=getParameters().get("column.formats")
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

      if (getParameters().get('titles.mergeCells')) {
        //Merge title cells
        titles.eachWithIndex { title, index ->
          mergeCells(startColumn: 0, startRow: index, endColumn: fields.size(), endRow: index)
        }
      }
    }

  }

  private static List computeSheetsAndLimit(List data, maxPerSheet) {
    if (!data)
      throw new ExportingException("Error during export: Empty data!")

    def limitPerSheet = data.size() > maxPerSheet ? maxPerSheet : data.size()
    def sheetsCount = Math.ceil(data.size() / limitPerSheet)
    log.debug("limitPerSheet:$limitPerSheet ::: sheetsCount:$sheetsCount")
    return [sheetsCount, limitPerSheet]
  }

}