package stally;

import java.util.ArrayList;

public class Token {
   String identifier;
   int value;
   String str;
   ArrayList<Token> tokens0;
   ArrayList<Token> tokens1;

   public Token(String identifier) {
      this.identifier = identifier;
   }

   public Token(String identifier, int value) {
      this.identifier = identifier;
      this.value = value;
   }

   public Token(String identifier, String str) {
      this.identifier = identifier;
      this.str = str;
   }

   public Token(String identifier, ArrayList<Token> tokens) {
      this.identifier = identifier;
      this.tokens0 = tokens;
   }

   public Token(String identifier, ArrayList<Token> tokens0, ArrayList<Token> tokens1) {
      this.identifier = identifier;
      this.tokens0 = tokens0;
      this.tokens1 = tokens1;
   }

   @Override
   public String toString() {
      if (str == null & tokens0 == null & tokens1 == null) {
         return "Token[" + identifier + "]\t{value: " + value + "}";
      } else if (str != null & tokens0 == null & tokens1 == null) {
         return "Token[" + identifier + "]\t{str: " + str + "}";
      } else if (str == null & tokens0 != null & tokens1 == null) {
         return "Token[" + identifier + "]\t{tokens0: " + tokens0 + "}";
      }
      return "Token[" + identifier + "]\t{tokens0: " + tokens0 + "\ttokens1: " + tokens1 + "}";
   }

   public String getString() {
      return "Token[" + identifier + "]\t{value: " + value
              + ":\tstr: " + str
              + ":\ttokens0: " + tokens0
              + "\ttokens1: " + tokens1 + "}";
    }
}
