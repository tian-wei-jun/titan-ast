package titan.ast.runtime;

/**
 * Application for Test.
 *
 * @author tian wei jun
 */
public class Application {

  public static void main(String[] args) {
    String automataFilePath = null;
    String sourceCodeFilePath = null;
    if (null != args && args.length >= 2) {
      automataFilePath = args[0];
      sourceCodeFilePath = args[1];
    } else {
      return;
    }
    // testRuntimeAstApplication(automataFilePath, sourceCodeFilePath);
    testRuntimeRichAstApplication(automataFilePath, sourceCodeFilePath);
  }

  private static void testRuntimeRichAstApplication(
      String automataFilePath, String sourceCodeFilePath) {
    RuntimeAutomataRichAstApplication runtimeAstApplication =
        new RuntimeAutomataRichAstApplication();
    try {
      runtimeAstApplication.setContext(automataFilePath);
    } catch (AutomataDataIoException e) {
      Logger.info(e.getMessage());
      return;
    }
    RichAstGeneratorResult astGeneratorResult =
        runtimeAstApplication.buildRichAst(sourceCodeFilePath);
    if (astGeneratorResult.isOk()) {
      runtimeAstApplication.displayGraphicalViewOfAst(astGeneratorResult.getOkAst());
    } else {
      Logger.info(astGeneratorResult.getErrorMsg());
    }
  }

  private static void testRuntimeAstApplication(
      String automataFilePath, String sourceCodeFilePath) {
    RuntimeAutomataAstApplication runtimeAstApplication = new RuntimeAutomataAstApplication();
    try {
      runtimeAstApplication.setContext(automataFilePath);
    } catch (AutomataDataIoException e) {
      Logger.info(e.getMessage());
      return;
    }
    AstGeneratorResult astGeneratorResult = runtimeAstApplication.buildAst(sourceCodeFilePath);
    if (astGeneratorResult.isOk()) {
      runtimeAstApplication.displayGraphicalViewOfAst(astGeneratorResult.getOkAst());
    } else {
      Logger.info(astGeneratorResult.getErrorMsg());
    }
  }
}
