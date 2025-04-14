package titan.ast.test.visitor;

import titan.ast.CommandLineAstApplication;

/**
 * .
 *
 * @author tian wei jun
 */
public class BuildingAstVisitorTest {
  public static void main(String[] args) {
    String[] testArgs =
        new String[] {
          "-grammarFilePaths",
          "D:\\github-pro\\titan\\titan-ast\\runtime\\java\\titan-ast\\src\\resources\\titanAstGrammar.txt",
          "-astVisitorFileDirectory",
          // "D:\\github-pro\\titan\\titan-ast\\runtime\\java\\titan-ast\\src\\titan\\ast\\impl\\ast\\contextast\\",
          "C:\\Users\\june\\Desktop\\s\\",
          "titan.ast.impl.ast.contextast"
        };

    new CommandLineAstApplication().run(testArgs);
  }
}
