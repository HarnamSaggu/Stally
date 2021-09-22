package stally;

import java.util.ArrayList;
import java.util.HashMap;

public class FindFunctionsResult {
   private String code;
   private HashMap<String, ArrayList<Token>> functionsAndDefinitions;

   public FindFunctionsResult(String code, HashMap<String, ArrayList<Token>> functionsAndDefinitions) {
      this.code = code;
      this.functionsAndDefinitions = functionsAndDefinitions;
   }

   public String getCode() {
      return code;
   }

   public HashMap<String, ArrayList<Token>> getFunctionsAndDefinitions() {
      return functionsAndDefinitions;
   }
}
