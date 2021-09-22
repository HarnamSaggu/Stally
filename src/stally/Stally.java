// TODO: 27/07/2021 SIMPLIFY LEXING BY IMPLEMENTING FUNCTION

package stally;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stally {
   private final static Pattern PATTERN = Pattern.compile("\\(\\w+\\)\\s*=\\s*\\{");
   private final static Scanner SCANNER = new Scanner(System.in);

   private boolean printExitMessage;
   private int exitCode;
   private String sourceCode;
   private ArrayList<Token> tokens;
   private HashMap<Integer, String> tapes;
   private HashMap<Integer, Integer> heads;
   private HashMap<Integer, Integer> zeroPositions;
   private boolean halt;
   private int tapeIndex;
   HashMap<String, ArrayList<Token>> functionsAndDefinitions;

   public Stally(String sourceCode) {
      printExitMessage = true;
      setSourceCode(sourceCode);
   }

   public void setSourceCode(String sourceCode) {
      this.sourceCode = sourceCode;
      FindFunctionsResult result = findFunctions(sourceCode);
      this.sourceCode = result.getCode();
      functionsAndDefinitions = result.getFunctionsAndDefinitions();
      tokens = lex(this.sourceCode);
   }

   public static FindFunctionsResult findFunctions(String code) {
      HashMap<String, String> functionsAndDefinitions = new HashMap<>();
      Matcher matcher = PATTERN.matcher(code);
      int start;
      int end;
      while (matcher.find()) {
         String group = matcher.group();
         start = code.indexOf(group);
         end = start + group.length();
         String codeAfter = code.substring(end - 1);
         int startChar = -1;
         int openLevel = 0;
         int endLevel = 0;
         int endChar = 0;
         for (int j = 0; j < codeAfter.length(); j++) {
            if (codeAfter.charAt(j) == '{') {
               if (startChar == -1) {
                  startChar = j;
               }
               openLevel++;
            }
            if (codeAfter.charAt(j) == '}') {
               endLevel++;
               if (openLevel == endLevel) {
                  endChar = j;
                  break;
               }
            }
         }
         String definition = codeAfter.substring(startChar + 1, endChar);
         functionsAndDefinitions.put(group.split("\\s*=")[0], definition);
         StringBuilder temp = new StringBuilder(code);
         temp.delete(start, end + definition.length() + 1);
         code = temp.toString();
      }

//      {
//         for (String function : functionsAndDefinitions.keySet()) {
//            ArrayList<Integer> indexes = findAllOccurrences(code, function);
//            ArrayList<Integer> approvedIndexes = new ArrayList<>();
//            for (Integer index : indexes) {
//               String codeBefore = code.substring(0, index);
//               int count = 0;
//               for (int j = 0; j < codeBefore.length(); j++) {
//                  if (codeBefore.charAt(j) == '"') {
//                     count++;
//                  }
//                  if (j + 1 < codeBefore.length()) {
//                     if (codeBefore.charAt(j) == '\\' && codeBefore.charAt(j + 1) == '"') {
//                        j++;
//                     }
//                  }
//               }
//               if (count % 2 == 0) {
//                  approvedIndexes.add(index);
//               }
//            }
//
//            ArrayList<Integer> splitIndexes = new ArrayList<>();
//            splitIndexes.add(0);
//            for (Integer approvedIndex : approvedIndexes) {
//               splitIndexes.add(approvedIndex);
//               splitIndexes.add(approvedIndex + function.length());
//            }
//            splitIndexes.add(code.length() - 1);
//
//            ArrayList<String> parts = new ArrayList<>();
//            for (int i = 0; i < splitIndexes.size() - 1; i++) {
//               parts.add(code.substring(splitIndexes.get(i), splitIndexes.get(i + 1)));
//               for (int j = 0; j < parts.size() - 1; j++) {
//                  String part = parts.get(j);
//                  int count = 0;
//                  for (int k = 0; k < part.length(); k++) {
//                     if (part.charAt(k) == '"') {
//                        count++;
//                     }
//                     if (k + 1 < part.length()) {
//                        if (part.charAt(k) == '\\' && part.charAt(k + 1) == '"') {
//                           k++;
//                        }
//                     }
//                  }
//                  if (count % 2 == 0 && parts.get(i).equals(function)) {
//                     parts.set(i, functionsAndDefinitions.get(function));
//                  }
//               }
//            }
//            StringBuilder codeBuilder = new StringBuilder();
//            for (String part : parts) {
//               codeBuilder.append(part);
//            }
//            code = codeBuilder.toString();
//         }
//      }

      HashMap<String, ArrayList<Token>> functionsWithTokenDefinitions = new HashMap<>();
      for (String function : functionsAndDefinitions.keySet()) {
         functionsWithTokenDefinitions.put(function, lex(functionsAndDefinitions.get(function) + " "));
      }

      return new FindFunctionsResult(code, functionsWithTokenDefinitions);
   }

   public static ArrayList<Token> lex(String code) {
      ArrayList<Token> tokens = new ArrayList<>();
      for (int i = 0; i < code.length(); i++) {
         char character = code.charAt(i);
         if (character == '<') {
            String identifier = "move-left";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == '>') {
            String identifier = "move-right";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == ';') {
            String identifier = "halt";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 0));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 0));
            }
         } else if (character == '{') {
            String codeAfter = code.substring(i);
            int startChar = -1;
            int openLevel = 0;
            int endLevel = 0;
            int endChar = 0;
            for (int j = 0; j < codeAfter.length(); j++) {
               if (codeAfter.charAt(j) == '{') {
                  if (startChar == -1) {
                     startChar = j;
                  }
                  openLevel++;
               }
               if (codeAfter.charAt(j) == '}') {
                  endLevel++;
                  if (openLevel == endLevel) {
                     endChar = j;
                     break;
                  }
               }
            }
            openLevel = 0;
            endLevel = 0;
            String ifStr = codeAfter.substring(startChar + 1, endChar);
            int commaIndex = 0;
            char symbol;
            for (int j = 0; j < ifStr.length(); j++) {
               symbol = ifStr.charAt(j);
               if (symbol == '{') {
                  openLevel++;
               } else if (symbol == '}') {
                  endLevel++;
               } else if (symbol == ',' && openLevel == endLevel) {
                  commaIndex = j;
               }
            }
            String a = ifStr.substring(0, commaIndex);
            String b = ifStr.substring(commaIndex + 1);
            ArrayList<Token> tokens0 = lex(a + " ");
            ArrayList<Token> tokens1 = lex(b + " ");
            tokens.add(new Token("start-if", tokens0, tokens1));
            i += ifStr.length();
         } else if (character == '[') {
            String codeAfter = code.substring(i);
            int startChar = -1;
            int openLevel = 0;
            int endLevel = 0;
            int endChar = 0;
            for (int j = 0; j < codeAfter.length(); j++) {
               if (codeAfter.charAt(j) == '[') {
                  if (startChar == -1) {
                     startChar = j;
                  }
                  openLevel++;
               }
               if (codeAfter.charAt(j) == ']') {
                  endLevel++;
                  if (openLevel == endLevel) {
                     endChar = j;
                     break;
                  }
               }
            }
            ArrayList<Token> tokens0 = lex(codeAfter.substring(startChar + 1, endChar) + " ");
            tokens.add(new Token("start-while", tokens0));
            i += endChar - startChar;
         } else if (character == '+') {
            String identifier = "write-1";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == '-') {
            String identifier = "write-0";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == '!') {
            String identifier = "print-raw-binary";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == '*') {
            String identifier = "input-raw-binary";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == '@') {
            String identifier = "set-tape";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 0));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 0));
            }
         } else if (character == '£') {
            String identifier = "print-unicode";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == '$') {
            String identifier = "print-decimal";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == '&') {
            String identifier = "input-decimal";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == '%') {
            String identifier = "input-unicode";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 1));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 1));
            }
         } else if (character == '^') {
            String identifier = "move-to";
            StringBuilder token = new StringBuilder();
            String codeAfter = code.substring(i + 1);
            if (codeAfter.length() == 0) {
               tokens.add(new Token(identifier, 0));
               continue;
            }
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("-|\\d")) {
               token.append(codeAfter.charAt(count));
               i++;
               count++;
               if (count == codeAfter.length()) return tokens;
            }
            if (token.length() > 0) {
               tokens.add(new Token(identifier, Integer.parseInt(token.toString())));
            } else {
               tokens.add(new Token(identifier, 0));
            }
         } else if (character == '"') {
            String codeAfter = code.substring(i + 1);
            StringBuilder msg = new StringBuilder();
            int msgLength = 0;
            for (int j = 0; j < codeAfter.length(); j++) {
               if (codeAfter.charAt(j) == '\\') {
                  switch (codeAfter.charAt(++j)) {
                     case 'n' -> msg.append("\n");
                     case 't' -> msg.append("\t");
                     case 'b' -> msg.append("\b");
                     case '\\' -> msg.append("\\");
                     case '"' -> msg.append("\"");
                  }
                  msgLength += 2;
                  continue;
               }
               if (codeAfter.charAt(j) == '"') {
                  break;
               }
               msg.append(codeAfter.charAt(j));
               msgLength++;
            }
            tokens.add(new Token("print-string", msg.toString()));
            i += msgLength + 1;
         } else if (character == '(') {
            String codeAfter = code.substring(i);
            StringBuilder function = new StringBuilder();
            int count = 0;
            while ((codeAfter.charAt(count) + "").matches("\\(|\\)|\\w")) {
               function.append(codeAfter.charAt(count));
               count++;
            }
            tokens.add(new Token("function", function.toString()));
            i += function.length() - 1;
         }
      }
      return tokens;
   }

   private static ArrayList<Integer> findAllOccurrences(String text, String find) {
      ArrayList<Integer> indexes = new ArrayList<>();
      int index = 0;
      while (index != -1) {
         index = text.indexOf(find, index);
         if (index != -1) {
            indexes.add(index);
            index++;
         }
      }
      return indexes;
   }

   public int run() {
      tapes = new HashMap<>();
      heads = new HashMap<>();
      zeroPositions = new HashMap<>();
      tapeIndex = 0;
      halt = false;
      exitCode = 0;
      tapes.put(0, "0");
      heads.put(0, 0);
      zeroPositions.put(0, 0);
      runSegment(tokens);
      System.out.println("\nCompleted execution " + exitCode);
      return exitCode;
   }

   public int runWithSetup() {
      halt = false;
      exitCode = 0;
      runSegment(tokens);
      return exitCode;
   }

   private void runSegment(ArrayList<Token> tokens) {
      for (Token token : tokens) {
         if (halt) {
            return;
         }
         switch (token.identifier) {
            case "move-left" -> headLeft(token.value);

            case "move-right" -> headRight(token.value);

            case "halt" -> {
               exitCode = token.value;
               halt = true;
               return;
            }

            case "start-if" -> {
               if (getCurrentBit() == '1') {
                  runSegment(token.tokens0);
               } else {
                  runSegment(token.tokens1);
               }
            }

            case "start-while" -> {
               while (getCurrentBit() == '1' && !halt) {
                  runSegment(token.tokens0);
               }
            }

            case "write-1" -> {
               int repeat = token.value;
               for (int j = 0; j < repeat; j++) {
                  String[] chars = getTape().split("");
                  chars[getHead()] = "1";
                  tapes.put(tapeIndex, String.join("", chars));
                  if (j + 1 < repeat) headRight(1);
               }
            }

            case "write-0" -> {
               int repeat = token.value;
               for (int j = 0; j < repeat; j++) {
                  String[] chars = getTape().split("");
                  chars[getHead()] = "0";
                  tapes.put(tapeIndex, String.join("", chars));
                  if (j + 1 < repeat) headRight(1);
               }
            }

            case "print-raw-binary" -> {
               int repeat = token.value;
               for (int j = 0; j < repeat; j++) {
                  System.out.print(getCurrentBit());
                  if (j + 1 < repeat) headRight(1);
               }
            }

            case "input-raw-binary" -> {
               int repeat = token.value;
               String value = SCANNER.nextInt() + "";
               for (int j = 0; j < repeat; j++) {
                  String[] chars = getTape().split("");
                  if (j < value.length()) {
                     chars[getHead()] = String.valueOf(value.charAt(j));
                  }
                  tapes.put(tapeIndex, String.join("", chars));
                  if (j + 1 < repeat) headRight(1);
               }
            }

            case "set-tape" -> {
               int index = token.value;
               if (!tapes.containsKey(index)) {
                  tapes.put(index, "0");
                  heads.put(index, 0);
                  zeroPositions.put(index, 0);
               }
               tapeIndex = index;
            }

            case "print-string" -> System.out.print(token.str);

            case "print-unicode" -> {
               StringBuilder binary = new StringBuilder();
               int repeat = token.value;
               for (int j = 0; j < repeat; j++) {
                  binary.append(getCurrentBit());
                  if (j + 1 < repeat) headRight(1);
               }
               System.out.print((char) Integer.parseInt(binary.toString(), 2));
            }

            case "print-decimal" -> {
               StringBuilder binary = new StringBuilder();
               int repeat = token.value;
               for (int j = 0; j < repeat; j++) {
                  binary.append(getCurrentBit());
                  if (j + 1 < repeat) headRight(1);
               }
               System.out.print(Integer.parseInt(binary.toString(), 2));
            }

            case "input-decimal" -> {
               int decimal = SCANNER.nextInt();
               int repeat = token.value;
               String binary = String.format("%" + repeat + "s", Integer.toBinaryString(decimal))
                       .replaceAll(" ", "0");
               for (int j = 0; j < repeat; j++) {
                  String[] chars = getTape().split("");
                  chars[getHead()] = String.valueOf(binary.charAt(j));
                  tapes.put(tapeIndex, String.join("", chars));
                  if (j + 1 < repeat) headRight(1);
               }
            }

            case "input-unicode" -> {
               int decimal = SCANNER.next().charAt(0);
               int repeat = token.value;
               String binary = String.format("%" + repeat + "s", Integer.toBinaryString(decimal))
                       .replaceAll(" ", "0");
               for (int j = 0; j < repeat; j++) {
                  String[] chars = getTape().split("");
                  chars[getHead()] = String.valueOf(binary.charAt(j));
                  tapes.put(tapeIndex, String.join("", chars));
                  if (j + 1 < repeat) headRight(1);
               }
            }

            case "move-to" -> {
               int index = token.value;
               int a = getZeroPosition() + index;
               int b = getHead() - a;
               if (b > 0) {
                  headLeft(b);
               } else {
                  headRight(Math.abs(b));
               }
            }

            case "function" -> {
               runSegment(functionsAndDefinitions.get(token.str));
            }
         }
      }
   }

   public void headLeft(int value) {
      for (int i = 0; i < value; i++) {
         heads.put(tapeIndex, getHead() - 1);
         if (getHead() < 0) {
            tapes.put(tapeIndex, "0" + getTape());
            heads.put(tapeIndex, getHead() + 1);
            zeroPositions.put(tapeIndex, getZeroPosition() + 1);
         }
      }
   }

   public void headRight(int value) {
      for (int i = 0; i < value; i++) {
         heads.put(tapeIndex, getHead() + 1);
         if (getHead() >= getTape().length()) {
            tapes.put(tapeIndex, getTape() + "0");
         }
      }
   }

   public int getHead() {
      return heads.get(tapeIndex);
   }

   public String getTape() {
      return tapes.get(tapeIndex);
   }

   public int getZeroPosition() {
      return zeroPositions.get(tapeIndex);
   }

   public char getCurrentBit() {
      return getTape().charAt(getHead());
   }

   public ArrayList<Token> getTokens() {
      return tokens;
   }

   public void setTokens(ArrayList<Token> tokens) {
      this.tokens = tokens;
   }

   public HashMap<Integer, String> getTapes() {
      return tapes;
   }

   public void setTapes(HashMap<Integer, String> tapes) {
      this.tapes = tapes;
   }

   public HashMap<Integer, Integer> getHeads() {
      return heads;
   }

   public void setHeads(HashMap<Integer, Integer> heads) {
      this.heads = heads;
   }

   public HashMap<Integer, Integer> getZeroPositions() {
      return zeroPositions;
   }

   public void setZeroPositions(HashMap<Integer, Integer> zeroPositions) {
      this.zeroPositions = zeroPositions;
   }

   public int getTapeIndex() {
      return tapeIndex;
   }

   public void setTapeIndex(int tapeIndex) {
      this.tapeIndex = tapeIndex;
   }

   public String getSourceCode() {
      return sourceCode;
   }

   public boolean isPrintExitMessage() {
      return printExitMessage;
   }

   public void setPrintExitMessage(boolean printExitMessage) {
      this.printExitMessage = printExitMessage;
   }
}
