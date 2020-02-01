import java.util.ArrayList;
import java.io.*;
import java.io.FileNotFoundException; 


class VanillaSystem {
  
  
  static char[] punctuation = {'"', '\'', ',', '.', '-', '(', ')', '{', '}', '\\', '/', '=', '+', ';', ':', '|'};  //  used for punctuation removal
  
  
  
  public static void main(String args[]) {
    System.out.println("Program test");
    
    //  should print empty line
    System.out.println(("Program test").substring(0, 0));
    
    
    //  preprocessing
    try {
      //  create arrayList of raw documents (word bags) to be made into a dictionary later
      ArrayList<RawDocument> rawDocuments = new ArrayList<RawDocument>();
      boolean french = false;  //  turned on if french and ingores lines
      //  read lines
      //  source: https://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java
      File classes = new File("classes.txt.txt");
      BufferedReader br = new BufferedReader(new FileReader(classes));
      String line = br.readLine();  //  get first line
      while (line != null) {
        //  process line
        //  if it detects that the first 3 letters are "ADM", "PSY", or "MAT", it creates a new document starting with that line
        //  System.out.println(line);
        if (line.length() > 8)  //  minimum length for course code
          if (line.substring(0, 3).equals("ADM") || line.substring(0, 3).equals("PSY") || line.substring(0, 3).equals("MAT")) {  //  check for course code beginning
          //  check if english course
          if (Character.getNumericValue(line.charAt(5)) < 5) {  //  english section
            rawDocuments.add(new RawDocument(rawDocuments.size(), line.substring(0, 8)));
            french = false;  //  set french flag to false
          }
          //  course is french
          else {
            french = true;  //  turn french flag on
          }
        }
        
        //  check if currently english
        if (!french) {
          //  cut on spaces
          String[] words = line.split(" ");
          //  cycle through words
          for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
              //  System.out.println(words[i]);
              words[i] = cleanPunctuation(words[i]);  //  remove punctuation from a word
              //  System.out.println(words[i]);
              
              //  check if the word is a course code; if so, check if next is a number and add as phrase; otherwise, disregard
              boolean courseCode = false;
              if (words[i].length() == 3) {  //  minimum length for course code
                if (words[i].equals("ADM") || words[i].equals("PSY") || words[i].equals("MAT")) {  //  check for course code beginning
                  //  check if following word is there
                  if (words.length > i+1)
                    //  clean next word
                    //  check if next word is a number
                    if (isNumber(words[i+1])) {
                    courseCode = true;  //  set course code flag so as to know not to stem
                    //  add to current word for phrase treatment
                    words[i] += words[i+1];
                  }
                }
              }
              
              //  else, stem
              if (!courseCode) {
                
                //  DO STEMMING
                
              }
              
            }
          }
          //  send off list of words to raw document
          rawDocuments.get(rawDocuments.size()-1).addWords(words);
          //  send off words to dictionary to be added
        }
        
        //  get next line
        line = br.readLine();
      }
    }
    catch (Exception e) {
      System.out.println("Did not work");
      System.out.println(e);
    }
  }
  
  
  
  //  cleans punctuation from a word
  static String cleanPunctuation(String s) {
    //  back
    while (punctation(s.charAt(s.length() - 1))) {
      s = s.substring(0, s.length()-1);
      //  exit if length is 0
      if (s.length() == 0)
        break;
    }
    
    //  check if word is empty (as in, it was only punctuation and has been all removed)
    //  (running further would cause crash)
    if (s.length() == 0)
      return s;
    
    //  front
    while (punctation(s.charAt(0)))
      s = s.substring(1, s.length());
    //  return result
    return s;
  }
  
  
  //  function to check if the input character is in the punctuation array
  static boolean punctation(char c) {
    for (int i = 0; i < punctuation.length; i++)
      if (new Character(c).equals(new Character(punctuation[i])))
      return true;
    return false;
  }
  
  
  //  function to check if the input string is a number
  static boolean isNumber(String s) {
    char[] numbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    for (int i = 0; i < s.length(); i++) {
      Character c = new Character(s.charAt(i));
      boolean charIsNum = false;
      for (int n = 0; n < numbers.length; n++) {
        if (!(c.equals(new Character(numbers[n]))))
          charIsNum = true;
      }
      if (!charIsNum)
        return false;
    }
    return true;
  }
}


//  stores the bag of words from each line of a document
class RawDocument {
  int id;
  String title;
  ArrayList<String> words = new ArrayList<String>();
  
  RawDocument(int i, String t) {
    id = i;
    title = t;
    System.out.println("Creating course: " + t);
  }
  
  void addWords(ArrayList<String> w) {
    for (String s : w)
      words.add(s);
  }
  void addWords(String[] w) {
    for (String s : w)
      words.add(s);
  }
}