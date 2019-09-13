package de.andreasschmitt.export

import de.andreasschmitt.export.exporter.Exporter
import de.andreasschmitt.export.exporter.ExportingException

import javax.servlet.http.HttpServletResponse

class ExportService {

  boolean transactional = false

  def exporterFactory
  def grailsApplication

  void setupResponse(String type, HttpServletResponse response, String filename, String extension){
    response.contentType = grailsApplication.config.grails.mime.types[type]
    response.setHeader("Content-disposition", "attachment; filename=\"${filename}.${extension}\"")
  }

  void export(String type, OutputStream outputStream, List objects, Map formatters, Map parameters) throws ExportingException {
    export(type, outputStream, objects, null, null, formatters, parameters)
  }

  void export(String type, OutputStream outputStream, List objects, List fields, Map labels, Map formatters, Map parameters) throws ExportingException {
    Exporter exporter = exporterFactory.createExporter(type, fields, labels, formatters, parameters)
    exporter.export(outputStream, objects)
  }

  void export(String type, HttpServletResponse response, String filename, String extension, List objects, Map formatters, Map parameters) throws ExportingException {
    export(type, response, filename, extension, objects, null, null, formatters, parameters)
  }

  void export(String type, HttpServletResponse response, String filename, String extension, List objects, List fields, Map labels, Map formatters, Map parameters) throws ExportingException {
    setupResponse(type, response, filename, extension)

    Exporter exporter = exporterFactory.createExporter(type, fields, labels, formatters, parameters)
    exporter.export(response.outputStream, objects)
  }

}
