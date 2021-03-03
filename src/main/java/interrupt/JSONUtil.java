package interrupt;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;


import org.json.*;

/**
 * Provides JSON I/O functionality for the app (both for local and online JSON files),
 * called by ScheduleUtil.
 */
public class JSONUtil {

    // From Stack Overflow answerer Roland Illig: https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java 
    
    /**
     * Builds a String using output from Reader object.
     * @author Roland Illig on Stack Overflow
     * @param rd Reader object.
     * @throws IOException if any issues arise with I/O functionality.
     * @return 
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
      }

    // From Stack Overflow answerer Roland Illig: https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java 
    
    /**
     * Creates a JSONObject from contents of a JSON file, given a URL to that JSON file.
     * By referencing a URL instead of a local document, changes can be made online which the
     * app can adjust to without having to edit source documents.
     * @author Roland Illig on Stack Overflow
     * @param url String representation of a JSON file's URL.
     * @throws IOException if any issues arise with I/O functionality.
     * @throws JSONException if there are any issues with JSON.
     * @return JSONObject created from JSON file at given URL.
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
          BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
          String jsonText = readAll(rd);
          JSONObject json = new JSONObject(jsonText);
          return json;
        } finally {
          is.close();
        }
    }

    // From video tutorial: https://www.youtube.com/watch?v=4rBeFDnw_oo&t=1605s by Jose Rodriguez Rivas

    /**
     * Creates an InputStream given a filePath name in String form.
     * @author Jose Rodriguez Rivas on YouTube
     * @param filePath Path to file.
     * @return InputStream that uses file at filePath as its input.
     */
    public static InputStream fileToInputStream(String filePath) {
		
      try {
        InputStream in = JSONUtil.class.getResourceAsStream(filePath);
        return in;
      } catch (Exception e) {
        
      }
      return null;
      
    }

    // From video tutorial: https://www.youtube.com/watch?v=4rBeFDnw_oo&t=1605s by Jose Rodriguez Rivas

    /**
     * Returns the entirety of a JSON file in String form, given file path.
     * @author Jose Rodriguez Rivas on YouTube
     * @param filePath Path to file.
     * @throws IOException if any issues with I/O arise.
     * @return JSON file as a String.
     */
    public static String getJSONStringFromFile(String filePath) throws IOException {
      Scanner scan;
      InputStream in = fileToInputStream(filePath);
      scan = new Scanner(in);
      
      String json = scan.useDelimiter("\\Z").next();
      
      scan.close();
      in.close();
      
      return json;
    }
    
    // From video tutorial: https://www.youtube.com/watch?v=4rBeFDnw_oo&t=1605s by Jose Rodriguez Rivas

    /**
     * Creates a JSONObject from a JSON file, given a file path.
     * @author Jose Rodriguez Rivas on YouTube
     * @param filePath Path to file.
     * @throws JSONException
     * @throws IOException if any issues with I/O arise.
     * @return JSONObject from JSON file at filePath.
     */
    public static JSONObject createJSONObjectFromFile(String filePath) throws JSONException, IOException {
      return new JSONObject(getJSONStringFromFile(filePath));
    }
    
    // From video tutorial: https://www.youtube.com/watch?v=4rBeFDnw_oo&t=1605s by Jose Rodriguez Rivas

    /**
     * Verifies that a given JSONObject exists by checking that the object's
     * key matches the key parameter.
     * @author Jose Rodriguez Rivas on YouTube
     * @param obj Given JSONObject to be verified.
     * @param key Key that uniquely identifies a JSONObject (in String form).
     * @return True if the JSONObject exists, false otherwise.
     */
    public static boolean objectExists(JSONObject obj, String key) {
      Object o;
      
      try {
        o = obj.get(key);
      } catch (Exception e) { // Error getting key = DNE
        return false;
      }
      
      return o != null;
      
    }

}