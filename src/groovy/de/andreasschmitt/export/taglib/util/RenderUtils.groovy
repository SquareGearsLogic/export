package de.andreasschmitt.export.taglib.util

import grails.util.Holders
import org.apache.commons.codec.digest.DigestUtils

import java.rmi.server.UID
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Andreas Schmitt
 *
 */
class RenderUtils {

  /**
   * Create unique ID as hex representation.
   */
  static String getUniqueId() {
    return DigestUtils.md5Hex(new UID().toString())
  }

  /**
   *
   * @param pluginName
   * @param contextPath
   *
   */
  static String getResourcePath(String pluginName, String contextPath) {
    def plugin = Holders.pluginManager.allPlugins.find { it.name == 'export' }
    String pluginVersion = plugin?.version

    "${contextPath}/plugins/${pluginName.toLowerCase()}-$pluginVersion"
  }

  /**
   *
   * @param pluginResourcePath
   *
   */
  static String getApplicationResourcePath(String pluginResourcePath) {
    try {
      Pattern pattern = Pattern.compile("(.*)/plugins.*")
      Matcher matcher = pattern.matcher(pluginResourcePath)

      if (matcher.matches()) {
        return matcher.group(1)
      }
    }
    catch (Exception e) {
      return ""
    }
  }

}
