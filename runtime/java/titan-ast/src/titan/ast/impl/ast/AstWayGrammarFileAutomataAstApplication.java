package titan.ast.impl.ast;

import java.util.List;
import titan.ast.GrammarFileAutomataAstApplication;
import titan.ast.impl.ast.contextast.ContextAst;
import titan.ast.impl.ast.regexp.RegExpBuilder;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstWayGrammarFileAutomataAstApplication extends GrammarFileAutomataAstApplication {

  public AstWayGrammarFileAutomataAstApplication(List<String> grammarFilePaths) {
    super(grammarFilePaths);
  }

  protected void doBeforeNfa(List<String> grammarFilePaths) {
    for (String grammarFilePath : grammarFilePaths) {
      ContextAst contextAst = AstBuilder.build(grammarFilePath);
      new RegExpBuilder(contextAst).build();
    }
  }
}
