		  import javax.swing.*;
		  import javax.swing.event.ListSelectionEvent;
		  import javax.swing.event.ListSelectionListener;
		  import javax.swing.table.DefaultTableModel;
		  import javax.swing.table.TableModel;
		
		  import java.awt.*;
import java.util.ArrayList;
		
		  public class UI extends JFrame {
			int pick;
		    private JTextField search = new JTextField(30);
		    private JButton VSMButton = new JButton("VSM Search");
		    private JButton BooleanButton = new JButton("Boolean Search");
		    private JTable result = new JTable();
		    private JPanel panel = new JPanel();
		    private JScrollPane scrollPane = new JScrollPane(result);
		    private VanillaSystem Vanilla = new VanillaSystem();
		    private SpellCorrector correct= new SpellCorrector(Vanilla.dictionary);
		    int index[];
		    
		    
		    
		    public static void main(String[] args) {
		      new UI("UOttawa Course Finder");
		    }
		    
		    
		    
		    private DefaultTableModel makeModel(String info,int type) {
		    	
		      DefaultTableModel model = new DefaultTableModel();
		      model.addColumn("Course Codes");
		      if(type==1){
		        String old;
		        String[]list=Vanilla.vectorQueryProcessing.processQuery(info);
		        for(String word: list){
		          
		          if(word.charAt(0)=='!'){
		            word=word.substring(1,word.length()-1);
		          }
		          old=word;
		          if(!correct.inDictionary(word)){
		            //if not in dictionary popup with suggestions to replace. once clicked, replace word with suggestion. 
		        	   makeDialog(word,correct.getSuggestions(word));   	  
		        	  
		           
		          }
		            //  replace with new word
		            for(int i =0; i < info.length()-old.length();i++){
		              if (info.substring(i, i+old.length()).equals(old)) {
		                info = info.substring(0, i) + word + info.substring(i+old.length(), info.length());
		                i = word.length()-2;
		              }
		              
		          }}
		        
		        
		        // index=VanillaSystem.booleanSearchWithQuery(info);
		        
		    //  for(int i:index) {
		            
		        // model.addRow(new Object [] {VanillaSystem.documents[i].title});
		            
		    //  }
		       
		        
		      }else if(type==0){
		        String old;
		        String[]list=Vanilla.vectorQueryProcessing.processQuery(info);
		        for(String word: list){
		          
		          if(word.charAt(0)=='!'){
		            word=word.substring(1,word.length()-1);
		          }
		          old=word;
		          if(!correct.inDictionary(word)){
		            //if not in dictionary popup with suggestions to replace. once clicked, replace word with suggestion. 
		            
		          
		          
		            //word = 
		          }
		            //  replace with new word
		            for(int i =0; i < info.length()-old.length();i++){
		              if (info.substring(i, i+old.length()).equals(old)) {
		                info = info.substring(0, i) + word + info.substring(i+old.length(), info.length());
		                i = word.length()-2;
		              }
		              
		          }}
		        
		      
		      
		         // index=VanillaSystem.vectorSearchWithQuery(info);
		         // model.addColumn("Course Codes");
		         // for(int i:index) {
		          //  model.addRow(new Object [] {VanillaSystem.documents[i].title});
		            
		         // }
		          
		          
		          
		    
		        }
		        
		          return model;
		      }
		      
		      
		      
		      
		    public int makeDialog(String word,ArrayList<String> choices) {
		    	
		    	DefaultTableModel model=new DefaultTableModel();
		    	JTable table=new JTable();
		    	JDialog choose = new JDialog();
		    	model.addColumn("Possible Suggestions");
		    	
	        	choose.setTitle("Which of the options below did you mean instead of "+word+"?");
	        	choose.setVisible(true);
	        	choose.setSize(500, 250);
	        	choose.setResizable(false);
	        	
	        	choose.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
	        	choose.getContentPane().setLayout( new BorderLayout() );
	        	
	        	
	        	
	        	 for(String correction: correct.getSuggestions(word)) {
	            	 model.addRow(new Object [] {correction});
	            	 
	            	
	            }
	        	table.setModel(model);
	        	table.setDefaultEditor(Object.class, null);
	        	table.getTableHeader().setReorderingAllowed(false);
	        	JScrollPane scroller = new JScrollPane(table);
	        
	        	choose.add(scroller);
	        	
	        	table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			        public void valueChanged(ListSelectionEvent event) {
			        
			        pick=table.getSelectedRow();
			        choose.dispose();
			        System.out.println(pick);
			        }});
		    	
	        	return pick;
		    }
		      
		    
		
		    
		    private String printdescription(String []lines ) {
		      String line="";
		      
		      for(String i:lines) {
		        line+="\n"+i;
		      }
		      
		      
		      return line;
		    }
		    
		    
		    private UI(String title) throws HeadlessException {
		      super(title);
		      Vanilla.createDictionary();
		      
		      setSize(650, 600);
		      setResizable(false);
		      scrollPane.setPreferredSize(new Dimension (600,500)); 
		      addComponents();
		      Table();
		      
		      //used from https://stackoverflow.com/questions/10128064/jtable-selected-row-click-event
		      result.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
		        public void valueChanged(ListSelectionEvent event) {
		          
		          
		          //used from https://www.tutorialspoint.com/how-can-we-implement-a-long-text-of-the-joptionpane-message-dialog-in-java
		          JTextArea jta = new JTextArea(20, 50);
		          jta.setText(Vanilla.documents[index[result.getSelectedRow()]].title+"\n\n"+
		                      printdescription(Vanilla.documents[index[result.getSelectedRow()]].description.split("  ")));
		          
		          jta.setEditable(false);
		          jta.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		          JScrollPane jsp = new JScrollPane(jta);
		          jsp.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		          
		          
		          JOptionPane.showMessageDialog(null, jsp);
		          
		          
		          
		        }
		      });
		      
		      
		      
		      
		      
		      
		      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		      result.setFillsViewportHeight( true );
		      result.getTableHeader().setReorderingAllowed(false);
		      
		      revalidate();
		      repaint();
		      
		      
		      
		      
		      setVisible(true);
		      
		    }
		    
		    
		    
		    
		    private void addComponents() {
		      panel.add(search,BorderLayout.EAST);
		      panel.add(VSMButton,BorderLayout.CENTER);
		      panel.add(BooleanButton,BorderLayout.WEST);
		      panel.add(scrollPane);
		      add(panel);
		      
		      
		    }
		    
		    private void Table() {
		      VSMButton.addActionListener(e -> result.setModel(makeModel(search.getText(),0)));
		      BooleanButton.addActionListener(f -> result.setModel(makeModel(search.getText(),1)));
		      
		      
		      
		    }
		  }
		    
		    
		    
		    
		  