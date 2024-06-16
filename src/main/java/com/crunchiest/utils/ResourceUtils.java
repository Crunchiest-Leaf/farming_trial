package com.crunchiest.utils;

import com.crunchiest.FarmingTrial;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
* Utility methods for resource relevant tasks.
*/
public class ResourceUtils {
  /**
  * Copies README.md from resources to the plugin's data folder if it doesn't already exist.
  */
  public static void copyReadmeToPluginFolder(FarmingTrial plugin) {
    // Define the resource path for README.md
    String resourcePath = "README.md";
    
    // Get InputStream for the resource
    InputStream inputStream = plugin.getResource(resourcePath);
    
    if (inputStream != null) {
      // Define output file path (in plugin's data folder)
      File outFile = new File(plugin.getDataFolder(), "README.md");
      
      // Copy the resource to the output file if it doesn't exist
      if (!outFile.exists()) {
        try (OutputStream outputStream = new FileOutputStream(outFile)) {
          byte[] buffer = new byte[1024];
          int length;
          while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
          }
          plugin.logInfo("README.md copied successfully!");
        } catch (IOException e) {
          plugin.logWarning("Failed to copy README.md!");
          e.printStackTrace();
        } finally {
          try {
            inputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } else {
      plugin.logWarning("README.md not found in resources!");
    }
  }
}
