package de.andreasschmitt.export

import groovy.util.logging.Log4j
import spock.lang.Specification
import jxl.write.NumberFormats
import de.andreasschmitt.export.taglib.util.ExcelFormat

class ExportSpec extends Specification {
  def exportService

  void "test export 1 sheet"() {
    setup:
      Map labels = ['someFieldName':'Some Field Name']
      List fields = ['someFieldName']
      List data = [fields.collect {[it,42]}]
      Map sheets = ["First Sheet": [rows: data, fields: fields, labels: labels, "column.formats": ["Money": new ExcelFormat(NumberFormats.ACCOUNTING_FLOAT)]]]

      ByteArrayOutputStream fakeOut = new ByteArrayOutputStream()
      exportService.export('excel', fakeOut, sheets)
    expect:
      fakeOut.size() >0// 13824
  }

  void "test export 2 sheets"() {
    setup:
      Map labels = ['someFieldName':'Some Field Name']
      List fields = ['someFieldName']
      List data = [fields.collect {[it,42]}]
      Map sheets = ["First Sheet":  [rows: data, fields: fields, labels: labels, "column.formats": ["Money": new ExcelFormat(NumberFormats.ACCOUNTING_FLOAT)]],
                    "Second Sheet": [rows: data, fields: fields, labels: labels, "column.formats": ["Money": new ExcelFormat(NumberFormats.ACCOUNTING_FLOAT)]]]

      ByteArrayOutputStream fakeOut = new ByteArrayOutputStream()
      exportService.export('excel', fakeOut, sheets)
    expect:
      fakeOut.size() >0
  }


  void "test legacy export"() {
    setup:
      Map labels = ['someFieldName':'Some Field Name']
      List fields = ['someFieldName']
      List data = [fields.collect {[it,42]}]
      Map formatters = [someFieldName : {domain, value -> value}]
      Map parameters = [quoteCharacter: '"']

      ByteArrayOutputStream fakeOut = new ByteArrayOutputStream()
      exportService.export('excel', fakeOut, data, fields, labels, formatters, parameters)
    expect:
      fakeOut.size() >0
  }
}