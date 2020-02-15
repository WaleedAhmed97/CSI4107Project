import java.util.ArrayList;
import java.io.*;

//  for query processing
class VectorQueryProcessing {
  
  //  used just for testing
  public static void main(String args[]) {
  }
  
  //  returns an array of strings for queries
  static String[] processQuery(String query) {
    
    System.out.println(query);
    
    return query.split(" ");
    
  }
}


