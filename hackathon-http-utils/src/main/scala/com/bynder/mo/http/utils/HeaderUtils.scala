package com.bynder.mo.http.utils
import java.util.Locale
object HeaderUtils {

  /** Normalize an HTTP header name.
    *
    * @param name
    *   the header name
    * @return
    *   the normalized header name
    */
  @inline
  def normalize(name: String): String = name.toLowerCase(Locale.ENGLISH)
}
