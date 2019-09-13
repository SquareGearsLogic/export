package de.andreasschmitt.export.exporter

/**
 * @author Andreas Schmitt
 *
 */
class ExporterNotFoundException extends Exception {

  ExporterNotFoundException() {
    super()
  }

  ExporterNotFoundException(String message) {
    super(message)
  }

  ExporterNotFoundException(Throwable throwable) {
    super(throwable)
  }

  ExporterNotFoundException(String message, Throwable throwable) {
    super(message, throwable)
  }

}