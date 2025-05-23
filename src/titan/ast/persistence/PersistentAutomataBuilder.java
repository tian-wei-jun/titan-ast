package titan.ast.persistence;

import java.io.File;
import java.io.OutputStream;
import titan.ast.AstContext;
import titan.ast.logger.Logger;
import titan.ast.util.FileUtil;

/**
 * 生成持久化自动机.
 *
 * @author tian wei jun
 */
public class PersistentAutomataBuilder {

  public void build(String persistentAutomataFilePath) {
    File outputFile = FileUtil.makeFile(persistentAutomataFilePath);

    savePersistentDataToFile(outputFile);

    Logger.info(
        String.format(
            "[PersistentAutomataBuilder build] : file of autumata is in '%s'",
            persistentAutomataFilePath));
  }

  private void savePersistentDataToFile(File outputFile) {
    PersistentData persistentData = new PersistentData(AstContext.get());
    new PersistentDataFile().save(persistentData, outputFile);
  }

  public void savePersistentDataToOutputStream(OutputStream outputStream) {
    PersistentData persistentData = new PersistentData(AstContext.get());
    new PersistentDataFile().save(persistentData, outputStream);
  }
}
