package titan.ast.grammar.regexp;


/**
 * .
 *
 * @author tian wei jun
 */
public class ParenthesisRegExp extends UnitRegExp {

  public OrCompositeRegExp orCompositeRegExp;

  public ParenthesisRegExp(OrCompositeRegExp orCompositeRegExp) {
    super(RegExpType.PARENTHESIS);
    this.orCompositeRegExp = orCompositeRegExp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    ParenthesisRegExp that = (ParenthesisRegExp) o;
    return orCompositeRegExp.equals(that.orCompositeRegExp);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + orCompositeRegExp.hashCode();
    return result;
  }
}
