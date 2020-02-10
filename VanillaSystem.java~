import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import java.io.FileNotFoundException; 


class VanillaSystem {
  
  
  
  static QueryProcessing queryProcessing = new QueryProcessing();
  
  
    //  all documents
   static ProperDocument[] documents;  //  to be initialized after the construction and processing of raw documents
    
    
  
  static char[] punctuation = {'"', '\'', ',', '.', '-', '(', ')', '{', '}', '\\', '/', '=', '+', ';', ':', '|'};  //  used for punctuation removal
  
  static Dictionary dictionary;
  
  
  /*
   Stemming rules:
   Every word to be stemmed is duplicated, meaning that, in addition to the base word, a new RawDocumentWord is saved to wordPostings with the same position
   -s -> -
   -es -> -e
   -ies -> -y
   -ing -> -
   -ing -> -e
   -tion -> -te
   -tion -> -e
   -ation -> -e
   -ers -> -e
   -ers -> -
   -er -> -e
   -er -> -
   -ly -> -
   -ed -> -e
   */
  static String[] stemmingRules = {"s", "", "es", "", "ies", "y", "ing", "", "ing", "e", "tion", "te", "tion", "e", "ation", "e", "ers", "e", "ers", "", "er", "e", "er", "", "ly", "", "ed", "e"};
  
  
  //  different trackers
  boolean caseFolding = true;
  boolean stemming = true;
  
  
  
  public static void main(String args[]) {
    System.out.println("Program test");
    
    //  should print empty line
    System.out.println(("Program test").substring(0, 0));
    
    queryProcessing.processQuery("test AND (this OR that)");
    
    
    createDictionary();
    
    
    //  testing time
    int[] results;
    results = searchWithQuery("effectively AND NOT baccalaureate");
    for (int i : results)
      System.out.println(i);
    results = searchWithQuery("effectively AND baccalaureate");
    for (int i : results)
      System.out.println(i);
    results = searchWithQuery("effectively");
    for (int i : results) {
      System.out.println(i);
      documents[i].displayDocument();
    }
    results = searchWithQuery("student AND NOT MAT");
    for (int i : results)
      System.out.println(i);
  }
  
  public static void createDictionary() {
    
    
    //  dictionary
    dictionary = new Dictionary();
    
    
    
    //  preprocessing
    try {
      //  create arrayList of raw documents (word bags) to be made into a dictionary later
      ArrayList<RawDocument> rawDocuments = new ArrayList<RawDocument>();
      boolean french = false;  //  turned on if french and ingores lines
      int positionCounter = 1;  //  required to keep postition of words between document lines (must be 1 as 1 is subtracted)
      //  read lines
      //  source: https://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java
      File classes = new File("classes.txt");
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
            positionCounter = 1;  //  reset positionCounter
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
          //  word/posting object
          ArrayList<RawDocumentWord> wordPostings = new ArrayList<RawDocumentWord>();
          //  cycle through words
          for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
              //  System.out.println(words[i]);
              words[i] = cleanPunctuation(words[i]);  //  remove punctuation from a word
              //  System.out.println(words[i]);
              
              //  now that all punctuation has bben trimmed, save to wordPostings to preserve posting (as positions will be messed up during stemming)
              wordPostings.add(new RawDocumentWord(words[i], i+positionCounter-1));
              
              //  basic phrase recognition for course codes
              //  check if the word is a course code; if so, check if next is a number and add as phrase; otherwise, disregard
              boolean courseCode = false;
              if (words[i].length() == 3) {  //  minimum length for course code
                if (words[i].equals("ADM") || words[i].equals("PSY") || words[i].equals("MAT")) {  //  check for course code beginning
                  //  check if following word is there
                  if (words.length > i+1)
                    //  clean next word
                    words[i+1] = cleanPunctuation(words[i+1]);
                  //  check if next word is a number
                  if (isNumber(words[i+1])) {
                    courseCode = true;  //  set course code flag so as to know not to stem
                    //  add to current word for phrase treatment
                    words[i] += words[i+1];
                    //  wordPostings.get(i).word += words[i+1];  //  modify the word posting as well
                    wordPostings.add(new RawDocumentWord(words[i], i+positionCounter-1));  //  add course code to posting
                  }
                }
              }
              
              //  else, stem
              if (!courseCode) {
                
                //  stemming
                for (int j = 0; j < stemmingRules.length; j += 2) {
                  if (words[i].length() > stemmingRules[j].length())  //  check if within length
                    if (words[i].substring(words[i].length()-stemmingRules[j].length(), words[i].length()).equals(stemmingRules[j]))  //  check for matching ending
                    wordPostings.add(new RawDocumentWord(words[i].substring(0, words[i].length()-stemmingRules[j].length())+stemmingRules[j+1], i+positionCounter-1));  //  add new wordPosting with same posting position
                }
              }
              
            }
            //  otherwise, if a 0 length word was found (likely some double space or punction turned to nothing), then decrement positionCounter so that it will be skipped in the position counting for posting
            else {
              positionCounter--;
            }
          }
          //  send off list of words to raw document
          rawDocuments.get(rawDocuments.size()-1).addWords(wordPostings);
          
          //  add line to document
          rawDocuments.get(rawDocuments.size()-1).addLine(line);
          
          //  add length to position counter
          positionCounter += words.length;
        }
        
        //  get next line
        line = br.readLine();
      }
      
      
      
      //  all documents have been scanned and stored as RawDocuments with RawDocumentWord word/postings
      //  now, create proper documents from raw documents (RawDocumentWord to DictionaryWord)
      documents = new ProperDocument[rawDocuments.size()];
      for (int i = 0; i < rawDocuments.size(); i++) {
        documents[i] = new ProperDocument(rawDocuments.get(i));
        //  create dictionary compiling all proper documents
        dictionary.addDocument(documents[i]);
      }
      
      //  print out document 1 (0)
      //  documents[0].printDocument();
      
      System.out.println();
      System.out.println();
      
      //  print out dictionary
      System.out.println("Finished constructing dictionary");
      System.out.println();
      //  dictionary.printDictionary();
      
      
    }
    catch (Exception e) {
      System.out.println("Did not work");
      System.out.println(e);
    }
  }
  
  
  //  an entered query is turned into queries and then queried on the dictionary to turn back relevent pages (array of the document IDs)
  static int[] searchWithQuery(String query) {
    //  get queries
    ArrayList<ArrayList<String>> queries = queryProcessing.processQuery(query);
    
    //  resulting docs
    ArrayList<Integer> docs = new ArrayList<Integer>();
    
    //  search with each query
    for (ArrayList<String> q : queries) {
      //  sort words into contain and not
      ArrayList<String> contains = new ArrayList<String>();
      ArrayList<String> not = new ArrayList<String>();
      for (String word : q) {
        if (word.charAt(0) == '!')
          not.add(word.substring(1, word.length()));
        else
          contains.add(word);
      }
      //  get DictionaryWords
      ArrayList<DictionaryWord> includedWords = new ArrayList<DictionaryWord>();
      for (String word : contains) {
        includedWords.add(dictionary.getWord(word));
        if (includedWords.get(includedWords.size()-1) == null)
          System.out.println("Null found: " + word);
      }
      ArrayList<DictionaryWord> removedWords = new ArrayList<DictionaryWord>();
      for (String word : not) {
        removedWords.add(dictionary.getWord(word));
        if (removedWords.get(removedWords.size()-1) == null)
          System.out.println("Null found: " + word);
      }
      
      //  search through dictionary words
      //  to do this, have a tracker for each DictionaryWord in both lists. If all of the docs in includeWords match, and all the docs in removedWords are higher, add the doc to the list
      int[] dictionaryTrackers = new int[includedWords.size()+removedWords.size()];
      for (int i : dictionaryTrackers)
        i = 0;
      
      boolean finished = false;
      while (!finished) {
        //  check all docs
        boolean matchingIncluded = true;
        int docID = includedWords.get(0).postings.get(dictionaryTrackers[0]).docID;
        for (int i = 1; i<includedWords.size(); i++) {
          //  if docID is different
          if (docID != includedWords.get(i).postings.get(dictionaryTrackers[i]).docID) {
            matchingIncluded = false;
            break;
          }
        }
        //  if docIDs matched, increase all removedWords while they are less than docID
        if (matchingIncluded) {
          for (int i = 0; i<removedWords.size(); i++) {
            //  System.out.println(i);
            while(removedWords.get(i).postings.get(dictionaryTrackers[includedWords.size()+i]).docID < docID && dictionaryTrackers[includedWords.size()+i] < removedWords.get(i).postings.size()-2) {
              dictionaryTrackers[includedWords.size()+i]++;
              System.out.println(dictionaryTrackers[includedWords.size()+i]);
            }
            //  test if any of the removedWords match docID
            if (removedWords.get(i).postings.get(dictionaryTrackers[includedWords.size()+i]).docID == docID) {
              matchingIncluded = false;
              break;
            }
          }
        }
        
        //  check if matchingIncluded is (still) true, and if so, add the docID to the list
        if (matchingIncluded) {
          System.out.println("adding document: " + docID);
          docs.add(docID);
        }
        
        //  increase includedWord with the lowest docID
        docID = includedWords.get(0).postings.get(dictionaryTrackers[0]).docID;
        int increase = 0;
        for (int i = 1; i < includedWords.size(); i++) {
          if (docID > includedWords.get(i).postings.get(dictionaryTrackers[i]).docID) {
            docID = includedWords.get(i).postings.get(dictionaryTrackers[0]).docID;
            increase = i;
          }
        }
        dictionaryTrackers[increase]++;
        
        //  check if finished (any of the dictionary trackers are out of bounds; would only happen to the most recently increased one)
        if (dictionaryTrackers[increase] == includedWords.get(increase).postings.size())
          finished = true;
      }
    }
    
    //  at this point, all matching documents have been added to the docID arrayList, so sort, remove duplicates, and return the list of document IDs
    Collections.sort(docs);
    for (int i = 1; i < docs.size(); i++) {
      if (docs.get(i) == docs.get(i-1)) {
        docs.remove(i);
        i--;
      }
    }
    //  convert arrayList to array
    int[] documents = new int[docs.size()];
    for (int i = 0; i < docs.size(); i++) {
      documents[i] = docs.get(i);
    }
    return documents;
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


//  stores the bag of words/postings from each line of a document
class RawDocument {
  int id;
  String title;
  ArrayList<RawDocumentWord> words = new ArrayList<RawDocumentWord>();
  String description = "";
  
  RawDocument(int i, String t) {
    id = i;
    title = t;
    //  System.out.println("Creating course: " + t);
  }
  
  void addWords(ArrayList<RawDocumentWord> w) {
    for (RawDocumentWord word : w)
      words.add(word);
  }
  void addWords(RawDocumentWord[] w) {
    for (RawDocumentWord word : w)
      words.add(word);
  }
  void addLine(String line) {
    description = description + " " + line;
  }
}

//  simply stores the word and a posting
//  required as the stemming duplicates the word, so the position cannot be reliably used for posting position
class RawDocumentWord {
  
  String word;
  int post;
  
  RawDocumentWord(String w, int p) {
    word = w;
    post = p;
  }
}

//  stores the bag of words/postings from each line of a document
class ProperDocument {
  int id;
  String title;
  ArrayList<DictionaryWord> words = new ArrayList<DictionaryWord>();
  String description;
  
  ProperDocument(RawDocument doc) {
    //  set basic info
    id = doc.id;
    title = doc.title;
    description = doc.description;
    //  System.out.println("Creating proper course: " + title);
    //  convert RawDocumentWord to Posting
    //  create single of each
    for (RawDocumentWord wordPosting : doc.words) {
      words.add(new DictionaryWord(wordPosting.word));
      int[] postings = {wordPosting.post};
      words.get(words.size()-1).addPosting(new Posting(id, postings));
    }
    //  compound duplicates
    for (int i = 0; i < words.size(); i++) {
      for (int j = 0; j < words.size(); j++) {
        if (i != j) {  //  don't do on same word
          if (words.get(i).word.compareTo(words.get(j).word) == 0)  {  //  words are identical
            //  add posting positing to former and remove later
            words.get(i).postings.get(0).addPosting(words.get(j).postings.get(0).postings[0]);
            words.remove(j);
          }
        }
      }
    }
  }
  
  //  prints the document to the console
  void printDocument() {
    System.out.println("Document " + id + ":");
    for (DictionaryWord word : words) {
      System.out.println();
      System.out.print(word.word + " || ");
      for (Posting posting : word.postings) {
        System.out.print(posting.docID + ": {");
        System.out.print(posting.postings[0]);
        for (int i = 1; i<posting.postings.length; i++) {
          System.out.print(", " + posting.postings[i]);
        }
        System.out.print("} | ");
      }
    }
  }
  
  //  prints the document as to be displayed
  void displayDocument() {
    System.out.println(title + ":");
    String[] lines = description.split("  ");
    for (String line : lines)
    System.out.println(line);
  }
}


//  dictionary class
class Dictionary {
  
  ArrayList<DictionaryWord> words = new ArrayList<DictionaryWord>();
  
  Dictionary() {
  }
  
  //  integrates the Proper document into the dictionary
  void addDocument(ProperDocument doc) {
    //  scan all dictionary words in doc
    for (DictionaryWord docWord : doc.words) {
      //  check for matching word in dictionary
      boolean foundMatch = false;
      for (DictionaryWord dicWord : words) {
        if (docWord.word.compareTo(dicWord.word) == 0) {
          foundMatch = true;
          //  add posting
          dicWord.addPosting(docWord.postings.get(0));
        }
      }
      //  if match could not be found, add (shallow copy of) word to dictionary
      if (!foundMatch) {
        DictionaryWord dw = new DictionaryWord(docWord.word);
        dw.addPosting(docWord.postings.get(0));  //  can do 0 as each document only has 1 posting (containing 1+ positions)
        words.add(dw);
      }
    }
  }
  
  //  returns true if the dictionary contains the input word
  boolean hasWord(String w) {
    for (DictionaryWord word : words) {
      if (word.word.equals(w))
        return true;
    }
    return false;
  }
  
  //  returns the DictionaryWord matching the input word
  DictionaryWord getWord(String w) {
    for (DictionaryWord word : words) {
      if (word.word.equals(w))
        return word;
    }
    return null;
  }
  
  //  prints the dictionary to the console
  void printDictionary() {
    System.out.println("Dictionary:");
    for (DictionaryWord word : words) {
      System.out.println();
      System.out.print(word.word + " || ");
      for (Posting posting : word.postings) {
        System.out.print(posting.docID + ": {");
        System.out.print(posting.postings[0]);
        for (int i = 1; i<posting.postings.length; i++) {
          System.out.print(", " + posting.postings[i]);
        }
        System.out.print("} | ");
      }
    }
  }
}

//  stores a word and postings
class DictionaryWord {
  
  String word;
  
  ArrayList<Posting> postings = new ArrayList<Posting>();
  
  DictionaryWord(String w) {
    word = w;
  }
  
  void addPosting(Posting p) {
    postings.add(p);
  }
}

//  class for storing postings
class Posting {
  
  int docID;
  int[] postings;
  
  Posting(int id, int[] p) {
    docID = id;
    postings = p;
  }
  
  void addPosting(int p) {
    int[] postings2 = new int[postings.length+1];
    for (int i = 0; i < postings.length; i++)
      postings2[i] = postings[i];
    postings2[postings.length] = p;
    postings = postings2;
  }
}