package de.andreasschmitt.export.exporter

/**
 * @author Andreas Schmitt
 *
 */
abstract class AbstractExporter implements Exporter {

  List exportFields = []
  Map labels = [:]
  Map formatters = [:]
  Map parameters = [:]

  void export(OutputStream outputStream, List data) throws ExportingException {
    if (exportFields?.size() > 0) {
      exportData(outputStream, data, exportFields)
    } else if(data[0]!=null) {
      exportData(outputStream, data, ExporterUtil.getFields(data[0]))
    }
  }

  void export(OutputStream outputStream, Map sheets) throws ExportingException {
    sheets.each{title, Map config->
      if (!config.containsKey('fields') && config.containsKey('rows') && config.rows[0]!=null)
        config.fields = ExporterUtil.getFields(config.rows[0])
    }
    exportSheets(outputStream, sheets)
  }

  protected String getLabel(String field) {
    if (labels.containsKey(field)) {
      return labels[field]
    }

    return field
  }

  protected Object formatValue(Object domain, Object object, String field) {
    if (formatters?.containsKey(field)) {
      return formatters[field].call(domain, object)
    }

    return object
  }

  protected Object getValue(Object domain, String field) {
    return formatValue(domain, ExporterUtil.getNestedValue(domain, field), field)
  }

  protected Writer getOutputStreamWriter(OutputStream outputStream) {
    Writer outputStreamWriter

    if (parameters?.containsKey("encoding")) {
      outputStreamWriter = new OutputStreamWriter(outputStream, parameters["encoding"])
    } else {
      outputStreamWriter = new OutputStreamWriter(outputStream)
    }

    return outputStreamWriter
  }

  abstract protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException

  abstract protected void exportSheets(OutputStream outputStream, Map sheets) throws ExportingException

}