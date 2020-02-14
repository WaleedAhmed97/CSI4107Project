import java.util.ArrayList;
import java.io.*;

//  for query processing
class VectorQueryProcessing {
  
  //  used just for testing
  public static void main(String args[]) {
  }
  
  //  returns a list of lists of strings, each list being the a list of Strings for the AND queries
  static ArrayList<ArrayList<String>> processQuery(String query) {
    
    System.out.println(query);
    
    //  checks that all NOTs are properly bracketed
    query = enforceNotBrackets(query);
    
    //  build query tree
    QueryNode head = new QueryNode(query);
    
    System.out.println(query);
    
    System.out.println();
    head.printQuery();
    
    //  wierdly enough, the NOT is not pushed if it is the head
    //  so do that manually
    if (head.parts.get(0).equals("NOT")) {
      head.children.get(0).not = true;
      head.parts.remove(0);
      head.printQuery();
    }
    
    
    //  extend nots
    extendNot(head);
    //  set not
    setNot(head);
    
    
    /*
     //  compress tree
     //  do head first
     while (head.type == -1 && head.children.size() == 1) {
     head = head.children.get(0);
     }
     */
    
    
    ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();
    //  get queries
    queries = getQueries(head);
    /*
     System.out.println();
     printQueries(queries);
     System.out.println();
     System.out.println();
     System.out.println();
     System.out.println();
     */
    
    return queries;
  }
  
  static ArrayList<ArrayList<String>> getQueries(QueryNode node) {
    ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();
    //  OR
    //  for the OR, add a new ArrayList<String> for each child
    if (node.type == 0) {
      for (QueryNode child : node.children) {
        ArrayList<ArrayList<String>> childQueries = getQueries(child);
        for (ArrayList<String> query : childQueries)
          queries.add(query);
      }
    }
    //  AND
    //  for the AND, cross the children together
    if (node.type == 1) {
      //  start with the base of 2 children and then add from there
      //  first 2 children
      queries = crossQueries(getQueries(node.children.get(0)), getQueries(node.children.get(1)));
      //  next children
      for (int i = 2; i < node.children.size(); i++)
        queries = crossQueries(queries, getQueries(node.children.get(i)));
    }
    //  word
    if (node.type == -1) {
      if (node.children.size() == 0) {
        queries.add(new ArrayList<String>());
        queries.get(queries.size()-1).add(node.parts.get(0));
      }
      else
        queries = getQueries(node.children.get(0));
    }
    
    return queries;
  }
  
  
  //  checks (or makes sure) that all NOTs have proper bracket boundings (as they may not work propely otherwise)
  static String enforceNotBrackets(String query) {
    //  parse query for NOT
    for (int i = 0; i < query.length()-3; i++) {
      if (query.substring(i, i+3).equals("NOT")) {
        boolean addBracket = false;
        //  move i
        i = i+3;
        //  skip spaces
        while(query.charAt(i) == ' ')
          i++;
        //  check opening bracket
        if (query.charAt(i) != '(') {
          addBracket = true;
        }
        //  if brackets must be added
        if (addBracket) {
          int j = i;
          //  add opening bracket
          query = query.substring(0, i) + "(" + query.substring(i, query.length());
          //  skip spaces
          j++;
          while(query.charAt(j) == ' ') {
            j++;
            if (j == query.length())
              break;
          }
          //  find next space or closing bracket
          //  if (j < query.length()) {
          while (query.charAt(j) != ')' && query.charAt(j) != ' ') {
            j++;  //  using j so as to preserve i so that NOTs are not missed
            if (j == query.length())
              break;
          }
          //  }
          //  at next space or closing bracket
          //  add closing bracket
          query = query.substring(0, j) + ")" + query.substring(j, query.length());
        }
      }
    }
    return query;
  }
  
  
  
  //  extends NOTs in the input tree
  static void extendNot(QueryNode node) {
    //  call extendNot on children
    for (QueryNode child : node.children) {
      extendNot(child);
    }
    //  check if NOT and extend if so
    if (node.not) {
      //  if (node.type != -1 || (node.type == -1 && node.children.size() > 0)) {  //  check not the word itself
      //  swap type
      if (node.type != -1)
        node.type = 1-node.type;
      //  remove not
      if (node.children.size() > 0)
        node.not = !node.not;
      //  set all children to not and extend not
      for (QueryNode child : node.children) {
        child.not = !child.not;
        extendNot(child);
      }
      //    }
    }
  }
  
  //  places a ! in front of all applicable words for not
  static void setNot(QueryNode node) {
    //  if it is the word, place a ! infront of it, then check for !! and remove if so
    if (node.type == -1 && node.children.size() == 0) {
      if (node.not) {
        ArrayList<String> newWord = new ArrayList<String>();
        newWord.add("!" + node.parts.get(0));
        node.parts = newWord;
        //  it's throwing wierd errors when I try to overwrite it directly
      }
    }
    //  otherwise, call on children
    else {
      for (QueryNode child : node.children) {
        setNot(child);
      }
    }
  }
  
  
  //  crosses the 2D arrayLists of the 2 input nodes
  //  {{A}, {B}}, {{C}, {D}} -> {{A, C}, {A, D}, {B, C}, {B, D}}
  static ArrayList<ArrayList<String>> crossQueries(ArrayList<ArrayList<String>> nodeA, ArrayList<ArrayList<String>> nodeB) {  //  2 children
    ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();
    //  for each in first, add it to each in second
    //  go through each of childA x childB
    for (int i = 0; i < nodeA.size(); i++) {
      for (int j = 0; j < nodeB.size(); j++) {
        //  create list containing each word for the select list of childA and childB
        ArrayList<String> query = new ArrayList<String>();
        for (String word : nodeA.get(i))
          query.add(word);
        for (String word : nodeB.get(j))
          query.add(word);
        //  add new query to queries
        queries.add(query);
      }
    }
    return queries;
  }
  
  
  //  print queries
  static void printQueries(ArrayList<ArrayList<String>> queries) {
    System.out.println();
    System.out.print("{");
    for (int i = 0; i < queries.size(); i++) {
      System.out.print("{");
      for (int j = 0; j < queries.get(i).size(); j++) {
        System.out.print(queries.get(i).get(j));
        if (j < queries.get(i).size()-1)
          System.out.print(", ");
      }
      System.out.print("}");
      if (i < queries.size()-1)
        System.out.print(", ");
    }
    System.out.print("}");
  }
}


