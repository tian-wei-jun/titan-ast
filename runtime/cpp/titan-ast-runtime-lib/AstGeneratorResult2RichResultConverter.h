//
// Created by tian wei jun on 2024/10/26.
//

#ifndef AST_RUNTIME_RUNTIME_ASTGENERATORRESULT2RICHRESULTCONVERTER_H_
#define AST_RUNTIME_RUNTIME_ASTGENERATORRESULT2RICHRESULTCONVERTER_H_

#include "Result.h"
#include <cstdint>
using byte = uint8_t;

class DLL_PUBLIC AstGeneratorResult2RichResultConverter {
public:
  AstGeneratorResult2RichResultConverter();
  ~AstGeneratorResult2RichResultConverter();
  void setNewline(byte newline);
  byte getNewline();
  RichAstGeneratorResult *convert(AstGeneratorResult *astGeneratorResult);

private:
  byte newline;

private:
  RichTokensResult *convert2RichTokensResult(TokensResult *tokensResult);
  RichTokenParseErrorData *
  convert2RichTokenGeneratorErrorData(TokenParseErrorData *tokenParseErrorData);
  LineNumberDetail *buildLineNumberDetail(std::list<Token *> *tokens);
  RichAstResult *convert2RichAstResult(AstResult *astResult,
                                       LineNumberDetail *lineNumberDetail);
  RichAstParseErrorData *
  convert2RichAstParseErrorData(AstParseErrorData *astParseErrorData,
                                LineNumberDetail *lineNumberDetail);
};

#endif // AST_RUNTIME_RUNTIME_ASTGENERATORRESULT2RICHRESULTCONVERTER_H_