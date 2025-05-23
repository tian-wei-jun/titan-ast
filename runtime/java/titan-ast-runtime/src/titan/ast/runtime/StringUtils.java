package titan.ast.runtime;

/**
 * StringUtils.
 *
 * @author tian wei jun
 */
class StringUtils {

  static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  static boolean isNotEmpty(String str) {
    return !StringUtils.isEmpty(str);
  }

  /**
   * 判断字符串是不是空白符.
   *
   * @param str 字符串
   * @return 字符串是不是空白符,是的话返回true否则false
   */
  static boolean isBlank(String str) {
    int strLen;
    if (str == null || (strLen = str.length()) == 0) {
      return true;
    }
    for (int indexOfChar = 0; indexOfChar < strLen; indexOfChar++) {
      char ch = str.charAt(indexOfChar);
      if (!Character.isWhitespace(ch)) {
        return false;
      }
    }
    return true;
  }

  static boolean isNotBlank(String str) {
    return !StringUtils.isBlank(str);
  }
}
