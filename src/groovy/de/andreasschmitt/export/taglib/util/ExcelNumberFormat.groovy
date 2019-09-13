package de.andreasschmitt.export.taglib.util

import groovy.util.logging.Log4j
import jxl.biff.DisplayFormat
import jxl.format.Colour
import jxl.format.UnderlineStyle
import jxl.write.DateFormat
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.NumberFormat

@Log4j
class ExcelNumberFormat extends ExcelFormat {
  private static final Closure NUMBER_FORMATTER = { domain, value ->
    String strVal = "${value}".replace('fr.','').replaceAll(/[^\d.]/,'')
    return new BigDecimal(strVal?:'0')
  }

  ExcelNumberFormat(WritableFont font) {
    super(font)
    formatter = NUMBER_FORMATTER
  }

  ExcelNumberFormat(String fontName=null) {
    super(fontName)
    formatter = NUMBER_FORMATTER
  }

  ExcelNumberFormat(DisplayFormat format) {
    super(format)
    formatter = NUMBER_FORMATTER
  }

  ExcelNumberFormat(WritableFont font, DisplayFormat format) {
    super(font, format)
    formatter = NUMBER_FORMATTER
  }

  ExcelNumberFormat(String fontName, DisplayFormat format) {
    super(fontName, format)
    formatter = NUMBER_FORMATTER
  }

}
