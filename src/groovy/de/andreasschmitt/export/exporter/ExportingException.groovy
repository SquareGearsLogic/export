package de.andreasschmitt.export.exporter

/**
 * @author Andreas Schmitt
 *
 */
class ExportingException extends Exception {

  ExportingException() {
    super()
  }

  ExportingException(String message) {
    super(message)
  }

  ExportingException(Throwable throwable) {
    super(throwable)
  }

  ExportingException(String message, Throwable throwable) {
    super(message, throwable)
  }

}