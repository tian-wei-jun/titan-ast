package titan.ast.impl.ast.contextast;

public class RegExpTerminalGrammarAttributesAst extends NonterminalContextAst {

  @Override
  public void accept(Visitor visitor) {
    visitor.visitRegExpTerminalGrammarAttributesAst(this);
  }
}