package titan.ast.test.diy;

import titan.ast.grammar.GrammarFileAutomataAstApplication;
import titan.ast.logger.Logger;
import titan.ast.runtime.RichAstGeneratorResult;

public class DiyShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String[] testArgs = {
      "-grammarFilePath",
      "D://github-pro/titan/titan-ast/test/diy/diy.grammar",
      "-sourceFilePath",
      "D://github-pro/titan/titan-ast/test/diy/diy.txt",
      "-graphicalViewOfAst"
    };

    // new CommandLineAstApplication().run(testArgs);

    GrammarFileAutomataAstApplication grammarFileAutomataAstApplication =
        new GrammarFileAutomataAstApplication();
    grammarFileAutomataAstApplication.setAstAutomataContext(testArgs[1]);
    grammarFileAutomataAstApplication.buildRuntimeAutomataAstApplication();

    RichAstGeneratorResult richAstGeneratorResult =
        grammarFileAutomataAstApplication.buildAst(testArgs[3]);
    if (richAstGeneratorResult.isOk()) {
      grammarFileAutomataAstApplication.displayGraphicalViewOfAst(
          richAstGeneratorResult.getOkAst());
    } else {
      Logger.info(richAstGeneratorResult.getErrorMsg());
    }
  }
}
