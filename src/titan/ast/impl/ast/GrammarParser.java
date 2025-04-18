package titan.ast.impl.ast;

import java.util.Arrays;
import java.util.LinkedList;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.GrammarAttribute.NfaTerminalGrammarAttribute;
import titan.ast.grammar.GrammarCharset;
import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContentEdge;
import titan.ast.grammar.PrimaryGrammarContent.NfaPrimaryGrammarContentEdgeType;
import titan.ast.grammar.regexp.GrammarRegExp;
import titan.ast.grammar.regexp.OneCharOptionCharsetRegExp;
import titan.ast.grammar.regexp.OneCharOptionCharsetRegExp.OptionChar;
import titan.ast.grammar.regexp.RegExp;
import titan.ast.grammar.regexp.RepeatTimes;
import titan.ast.grammar.regexp.SequenceCharsRegExp;

/**
 * .
 *
 * @author tian wei jun
 */
public class GrammarParser {

  private GrammarParser() {}

  public static OneCharOptionCharsetRegExp getOneCharOptionCharsetRegExp(String str) {
    char[] charArray = str.toCharArray();
    LinkedList<OptionChar> chars = new LinkedList<>();
    int indexOfChar = setCharsForOneCharOptionCharset(chars, charArray, 0);
    ++indexOfChar; // skip ']'
    RepeatTimes[] repeatTimes = getRepeatTimes(charArray, indexOfChar);
    return new OneCharOptionCharsetRegExp(repeatTimes[0], repeatTimes[1], chars);
  }

  public static SequenceCharsRegExp getSequenceCharsRegExp(String str) {
    char[] charArray = str.toCharArray();
    StringBuilder stringBuilder = new StringBuilder(str.length());
    int indexOfChar = setCharsForSequenceChars(stringBuilder, charArray, 0);
    String chars = stringBuilder.toString();
    ++indexOfChar; // skip '\''
    RepeatTimes[] repeatTimes = getRepeatTimes(charArray, indexOfChar);
    return new SequenceCharsRegExp(repeatTimes[0], repeatTimes[1], chars);
  }

  public static GrammarRegExp getGrammarRegExp(String grammarName) {
    return new GrammarRegExp(grammarName);
  }

  // Identifier RepeatTimes
  public static GrammarRegExp getGrammarUnitRegExpRepeatTimes(String str) {
    StringBuilder stringBuilder = new StringBuilder(str.length());
    char[] charArray = str.toCharArray();
    int indexOfChar = 0;
    for (; indexOfChar < charArray.length; indexOfChar++) {
      char ch = charArray[indexOfChar];
      if (isPrefixOfRepeatTimes(ch)) {
        break;
      }
      stringBuilder.append(ch);
    }
    String grammarName = stringBuilder.toString();
    RepeatTimes[] repeatTimes = getRepeatTimes(charArray, indexOfChar);
    return new GrammarRegExp(repeatTimes[0], repeatTimes[1], grammarName);
  }

  // ParenthesisUnitRegExpSuffixFragment : ')' RepeatTimes? ;
  public static RepeatTimes[] getRepeateTimesByParenthesisUnitRegExpSuffix(
      String parenthesisUnitRegExpSuffix) {
    char[] charArray = parenthesisUnitRegExpSuffix.toCharArray();
    return getRepeatTimes(charArray, 1); // skip ')'
  }

  private static RepeatTimes[] getRepeatTimes(char[] charArray, int indexOfChar) {
    RepeatTimes repMinTimes = RepeatTimes.numberTimes(1);
    RepeatTimes repMaxTimes = RepeatTimes.numberTimes(1);
    if (indexOfChar >= charArray.length) {
      return new RepeatTimes[] {repMinTimes, repMaxTimes};
    }

    char firstCh = charArray[indexOfChar];
    switch (firstCh) {
      case '?' -> {
        repMinTimes = RepeatTimes.numberTimes(0);
        repMaxTimes = RepeatTimes.numberTimes(1);
      }
      case '*' -> {
        repMinTimes = RepeatTimes.numberTimes(0);
        repMaxTimes = RepeatTimes.infinityTimes();
      }
      case '+' -> {
        repMinTimes = RepeatTimes.numberTimes(1);
        repMaxTimes = RepeatTimes.infinityTimes();
      }
      case '{' -> {
        RepeatTimes[] repeatTimes = getRepeatTimesByLeftBrace(charArray, indexOfChar);
        repMinTimes = repeatTimes[0];
        repMaxTimes = repeatTimes[1];
      }
      default -> {
        repMinTimes = RepeatTimes.numberTimes(1);
        repMaxTimes = RepeatTimes.numberTimes(1);
      }
    }
    return new RepeatTimes[] {repMinTimes, repMaxTimes};
  }

  // '{' naturalNumber '}' // '{' naturalNumber? ',' naturalNumber? '}'
  private static RepeatTimes[] getRepeatTimesByLeftBrace(char[] charArray, int indexOfChar) {
    StringBuilder repMinTimesStr = new StringBuilder(charArray.length - indexOfChar);
    boolean isEndParse = false;
    for (indexOfChar += 1; indexOfChar < charArray.length; indexOfChar++) {
      char ch = charArray[indexOfChar];
      if (ch == ',') {
        break;
      }
      if (ch == '}') {
        isEndParse = true;
        break;
      }
      repMinTimesStr.append(ch);
    }
    RepeatTimes repMinTimes = RepeatTimes.infinityTimes();
    RepeatTimes repMaxTimes = RepeatTimes.infinityTimes();
    if (isEndParse) { // 没有逗号，只能是｛2｝这种形式
      repMinTimes = RepeatTimes.numberTimes(Integer.parseInt(repMinTimesStr.toString(), 10));
      repMaxTimes = RepeatTimes.numberTimes(repMinTimes.times);
      return new RepeatTimes[] {repMinTimes, repMaxTimes};
    }
    StringBuilder repMaxTimesStr = new StringBuilder(charArray.length - indexOfChar);
    for (indexOfChar += 1; indexOfChar < charArray.length; indexOfChar++) {
      char ch = charArray[indexOfChar];
      if (ch == '}') {
        break;
      }
      repMaxTimesStr.append(ch);
    }
    if (!repMinTimesStr.isEmpty()) {
      repMinTimes = RepeatTimes.numberTimes(Integer.parseInt(repMinTimesStr.toString(), 10));
    }
    if (!repMaxTimesStr.isEmpty()) {
      repMaxTimes = RepeatTimes.numberTimes(Integer.parseInt(repMaxTimesStr.toString(), 10));
    }
    if (!RegExp.isRightRepeatTimes(repMinTimes, repMaxTimes)) {
      throw new AstRuntimeException(
          String.format(
              "{repMinTimes,repMaxTimes} is not right,error near : {%s,%s}",
              repMinTimesStr, repMaxTimesStr));
    }

    return new RepeatTimes[] {repMinTimes, repMaxTimes};
  }

  private static boolean isPrefixOfRepeatTimes(char ch) {
    return switch (ch) {
      case '?', '*', '+', '{' -> true;
      default -> false;
    };
  }

  public static int setCharsForOneCharOptionCharset(
      LinkedList<OptionChar> chars, char[] charArray, int indexOfChar) {
    OneCharOptionCharsetRegExpCreationDescriptor creationDescriptor =
        new OneCharOptionCharsetRegExpCreationDescriptor();
    StringBuilder stringBuilder = new StringBuilder(charArray.length - indexOfChar);
    char ch = charArray[indexOfChar];
    if (ch == '~') {
      creationDescriptor.isNot = true;
      ++indexOfChar; // charArray[indexOfChar]=='['
    }
    ++indexOfChar; // skip '['
    while (indexOfChar < charArray.length) {
      ch = charArray[indexOfChar];
      if (ch == '\\') {
        indexOfChar = setEscapeCharForOneCharOptionCharset(stringBuilder, charArray, indexOfChar);
        continue;
      }
      if (ch == ']') {
        break;
      }
      if (ch == '-') {
        creationDescriptor.indexsOfRangeFlag.add(stringBuilder.length());
      }
      stringBuilder.append(ch);
      ++indexOfChar;
    }
    creationDescriptor.originalChars = stringBuilder.toString().toCharArray();
    chars.addAll(creationDescriptor.getChars());
    return indexOfChar;
  }

  // '\'' CharForSequenceChars* '\'' RepeatTimes?
  public static int setCharsForSequenceChars(
      StringBuilder charsBuilder, char[] charArray, int indexOfChar) {
    StringBuilder stringBuilder = new StringBuilder(charArray.length - indexOfChar);
    ++indexOfChar; // skip'
    while (indexOfChar < charArray.length) {
      char ch = charArray[indexOfChar];
      if (ch == '\\') {
        indexOfChar = setEscapeCharForSequenceChars(stringBuilder, charArray, indexOfChar);
        continue;
      }
      if (ch == '\'') {
        break;
      }
      stringBuilder.append(ch);
      ++indexOfChar;
    }
    charsBuilder.append(stringBuilder.toString());
    return indexOfChar;
  }

  private static int setEscapeCharForOneCharOptionCharset(
      StringBuilder stringBuilder, char[] charArray, int indexOfChar) {
    ++indexOfChar; // skip '\\'
    char ch = charArray[indexOfChar];
    if (ch == 'x' || ch == 'X') {
      return setHexadecimalEscapeChar(charArray, indexOfChar, stringBuilder);
    }
    char escapeChar = ch;
    switch (ch) {
      case '-', ']', '\\' -> {}
      // 0\\abfnrtvs
      case '0' -> {
        escapeChar = 0;
      }
      case 'a' -> {
        escapeChar = 7;
      }
      case 'b' -> {
        escapeChar = 8;
      }
      case 'f' -> {
        escapeChar = 12;
      }
      case 'n' -> {
        escapeChar = 10;
      }
      case 'r' -> {
        escapeChar = 13;
      }
      case 't' -> {
        escapeChar = 9;
      }
      case 'v' -> {
        escapeChar = 11;
      }
      case 's' -> {
        escapeChar = ' ';
      }
    }
    stringBuilder.append(escapeChar);
    ++indexOfChar;
    return indexOfChar;
  }

  private static int setEscapeCharForSequenceChars(
      StringBuilder stringBuilder, char[] charArray, int indexOfChar) {
    ++indexOfChar; // skip '\\'
    char ch = charArray[indexOfChar];
    if (ch == 'x' || ch == 'X') {
      return setHexadecimalEscapeChar(charArray, indexOfChar, stringBuilder);
    }
    char escapeChar = ch;
    switch (ch) {
      case '\'', '\\' -> {}
      // 0\\abfnrtvs
      case '0' -> {
        escapeChar = 0;
      }
      case 'a' -> {
        escapeChar = 7;
      }
      case 'b' -> {
        escapeChar = 8;
      }
      case 'f' -> {
        escapeChar = 12;
      }
      case 'n' -> {
        escapeChar = 10;
      }
      case 'r' -> {
        escapeChar = 13;
      }
      case 't' -> {
        escapeChar = 9;
      }
      case 'v' -> {
        escapeChar = 11;
      }
      case 's' -> {
        escapeChar = ' ';
      }
    }
    stringBuilder.append(escapeChar);
    ++indexOfChar;
    return indexOfChar;
  }

  private static int setHexadecimalEscapeChar(
      char[] charArray, int indexOfChar, StringBuilder stringBuilder) {
    char[] hexNumberChars = new char[GrammarCharset.HEX_LENGTH_OF_TEXT_CHAR];
    int indexOfHexNumber = 0;
    int indexOfHexCharText = indexOfChar + 1;
    while (indexOfHexCharText < charArray.length && indexOfHexNumber < hexNumberChars.length) {
      char hexChar = charArray[indexOfHexCharText++];
      if (isHexDigitChar(hexChar)) {
        hexNumberChars[indexOfHexNumber++] = hexChar;
      } else {
        break;
      }
    }
    int sizeOfHexNumbers = indexOfHexNumber; // indexOfHexNumber==size of hexNumbers
    int vchar = 0;
    int multiples = 1;
    indexOfHexNumber = sizeOfHexNumbers - 1;
    while (indexOfHexNumber >= 0) {
      vchar += getIntByHexDigitChar(hexNumberChars[indexOfHexNumber]) * multiples;
      multiples *= 16;
      --indexOfHexNumber;
    }
    stringBuilder.append((char) vchar);
    return indexOfChar + sizeOfHexNumbers + 1;
  }

  public static int getIntByHexDigitChar(char tchar) {
    if (isDigitChar(tchar)) {
      return tchar - '0';
    }
    if (tchar >= 'a' && tchar <= 'f') {
      return 10 + tchar - 'a';
    }
    if (tchar >= 'A' && tchar <= 'F') {
      return 10 + tchar - 'A';
    }
    return 0;
  }

  public static boolean isHexDigitChar(char tchar) {
    return isDigitChar(tchar) || (tchar >= 'a' && tchar <= 'f') || (tchar >= 'A' && tchar <= 'F');
  }

  public static boolean isDigitChar(char tchar) {
    return tchar >= '0' && tchar <= '9';
  }

  // '#'  IdentifierFragment ;
  public static String getAliasByAndCompositeRegExpAlias(String nonformatAias) {
    return nonformatAias.substring(1);
  }

  // 'nfa' '(' IdentifierFragment ',' IdentifierFragment  ')' ;
  public static NfaTerminalGrammarAttribute getNfaTerminalGrammarAttribute(String str) {
    String start = "";
    String end = "";
    char[] charArray = str.toCharArray();
    int indexOfCharArray = 0;
    while (indexOfCharArray < charArray.length) {
      char ch = charArray[indexOfCharArray];
      if (ch == '(') {
        break;
      }
      ++indexOfCharArray;
    }
    ++indexOfCharArray; // skip '('
    StringBuilder stringBuilder = new StringBuilder(charArray.length);
    while (indexOfCharArray < charArray.length) {
      char ch = charArray[indexOfCharArray];
      if (ch == ',') {
        break;
      }
      stringBuilder.append(ch);
      ++indexOfCharArray;
    }
    start = stringBuilder.toString();
    ++indexOfCharArray; // skip ','
    stringBuilder.delete(0, stringBuilder.length());
    while (indexOfCharArray < charArray.length) {
      char ch = charArray[indexOfCharArray];
      if (ch == ')') {
        break;
      }
      stringBuilder.append(ch);
      ++indexOfCharArray;
    }
    end = stringBuilder.toString();
    return new NfaTerminalGrammarAttribute(start, end);
  }

  /*
  IdentifierFragment '\'' CharForSequenceChars* '\'' IdentifierFragment
    | IdentifierFragment '~'? '[' CharForOneCharOptionCharset* ']' IdentifierFragment
   */
  public static NfaPrimaryGrammarContentEdge getNfaEdge(String str) {
    char[] charArray = str.toCharArray();
    NfaPrimaryGrammarContentEdgeType type = null;
    // from
    StringBuilder stringBuilder = new StringBuilder(charArray.length);
    int indexOfChar = 0;
    while (indexOfChar < charArray.length) {
      char ch = charArray[indexOfChar];
      if (ch == '\'') {
        type = NfaPrimaryGrammarContentEdgeType.SEQUENCE_CHARS;
        break;
      }
      if (ch == '[' || ch == '~') {
        type = NfaPrimaryGrammarContentEdgeType.ONE_CHAR_OPTION_CHARSET;
        break;
      }
      stringBuilder.append(ch);
      ++indexOfChar;
    }
    String from = stringBuilder.toString();
    // chars
    char[] chars = new char[0];
    LinkedList<OptionChar> optionChars = new LinkedList<>();
    stringBuilder.delete(0, stringBuilder.length());
    switch (type) {
      case SEQUENCE_CHARS -> {
        indexOfChar = setCharsForSequenceChars(stringBuilder, charArray, indexOfChar);
        chars = stringBuilder.toString().toCharArray();
      }
      case ONE_CHAR_OPTION_CHARSET -> {
        indexOfChar = setCharsForOneCharOptionCharset(optionChars, charArray, indexOfChar);
      }
    }
    // to
    ++indexOfChar; // skip '\'' or ']'
    String to = new String(charArray, indexOfChar, charArray.length - indexOfChar);
    return new NfaPrimaryGrammarContentEdge(type, from, to, chars, optionChars);
  }

  // 'derive' '(' IdentifierFragment ')'
  public static String getRootTerminalGrammarNameByDerivedTerminalGrammarAttribute(
      String derivedTerminalGrammarAttribute) {
    return derivedTerminalGrammarAttribute.substring(
        GrammarCharset.KW_DERIVE.length() + 1, derivedTerminalGrammarAttribute.length() - 1);
  }

  private static class OneCharOptionCharsetRegExpCreationDescriptor {

    public boolean isNot = false;
    public char[] originalChars = new char[0];
    public boolean[] chars;
    LinkedList<Integer> indexsOfRangeFlag = new LinkedList<>();

    public LinkedList<OptionChar> getChars() {
      chars = new boolean[GrammarCharset.MAX_CHAR + 1];
      Arrays.fill(chars, false);
      // 处理[]中的chars
      buildCharsRegExpUnitOptionChars();
      // 处理~
      if (isNot) {
        for (int i = 0; i < chars.length; i++) {
          boolean isExistCh = chars[i];
          chars[i] = !isExistCh;
        }
      }
      return getOneCharOptionCharsetRegExpChars();
    }

    private LinkedList<OptionChar> getOneCharOptionCharsetRegExpChars() {
      LinkedList<OptionChar> retChars = new LinkedList<>();
      int min = -1;
      int max = -1;
      for (int ch = 0; ch < chars.length; ++ch) {
        if (chars[ch]) {
          // min不存在，max只能是不存在。
          if (min == -1) {
            min = ch;
            continue;
          }
          // min存在，max不存在
          if (max == -1) {
            if (min + 1 == ch) { // 连续
              max = ch;
              continue;
            }
            // 不连续
            retChars.add(new OptionChar(min, min));
            min = ch;
            max = -1;
            continue;
          }
          // min存在，max存在
          if (max + 1 == ch) { // 连续
            max = ch;
            continue;
          }
          // 不连续
          retChars.add(new OptionChar(min, max));
          min = ch;
          max = -1;
        }
      }

      if (min != -1) {
        if (max != -1) {
          retChars.add(new OptionChar(min, max));
        } else {
          retChars.add(new OptionChar(min, min));
        }
      }

      return retChars;
    }

    /** 处理- */
    private void buildCharsRegExpUnitOptionChars() {
      if (indexsOfRangeFlag.isEmpty()) {
        buildCharsRegExpUnitOptionCharSet(0, originalChars.length - 1);
      } else {
        int start = 0;
        int end = 0;
        for (Integer indexOfRangeFlag : indexsOfRangeFlag) {
          end = indexOfRangeFlag - 2;
          buildCharsRegExpUnitOptionCharSet(start, end); // before

          start = indexOfRangeFlag - 1;
          end = indexOfRangeFlag + 1;
          buildCharsRegExpUnitOptionRangeSet(start, end); // range

          start = end + 1;
        }

        end = originalChars.length - 1;
        buildCharsRegExpUnitOptionCharSet(start, end); // last
      }
    }

    private void buildCharsRegExpUnitOptionRangeSet(int startIndexOfChar, int endIndexOfChar) {
      if (startIndexOfChar >= 0
          && endIndexOfChar < originalChars.length
          && startIndexOfChar + 2 == endIndexOfChar) {
        int minChar = originalChars[startIndexOfChar] & 0x000000FF;
        int maxChar = originalChars[endIndexOfChar] & 0x000000FF;
        for (int ch = minChar; ch <= maxChar; ch++) {
          chars[ch] = true;
        }
      }
    }

    private void buildCharsRegExpUnitOptionCharSet(int startIndexOfChar, int endIndexOfChar) {
      if (startIndexOfChar >= 0
          && endIndexOfChar < originalChars.length
          && startIndexOfChar <= endIndexOfChar) {

        for (int i = startIndexOfChar; i <= endIndexOfChar; i++) {
          int ch = originalChars[i] & 0x000000FF;
          chars[ch] = true;
        }
      }
    }
  }
}
