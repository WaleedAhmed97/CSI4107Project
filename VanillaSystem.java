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
    
    
    //  dictionary
    dictionary = new Dictionary(punctuation, stemmingRules);
    documents = dictionary.createDictionary(documents);
    
    
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
  
  
  void createDictionary() {
    dictionary = new Dictionary(punctuation, stemmingRules);
    documents = dictionary.createDictionary(documents);
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
              //  System.out.println(dictionaryTrackers[includedWords.size()+i]);
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
          //  System.out.println("adding document: " + docID);
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
}


