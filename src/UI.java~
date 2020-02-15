  import javax.swing.*;
  import javax.swing.event.ListSelectionEvent;
  import javax.swing.event.ListSelectionListener;
  import javax.swing.table.DefaultTableModel;
  import javax.swing.table.TableModel;

  import java.awt.*;

  public class UI extends JFrame {
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
      
      if(type==1){
        String[] list=Vanilla.condense(Vanilla.booleanQueryProcessing.processQuery(info));
        for(String word: list){
          if(!correct.inDictionary(word)){
            correct.getSuggestions(word);
            
            
            
            
          }}
        
        
        if(Vanilla.booleanSearchWithQuery(info)!=null){
          index=VanillaSystem.booleanSearchWithQuery(info);
          model.addColumn("Course Codes");
          for(int i:index) {
            
            model.addRow(new Object [] {VanillaSystem.documents[i].title});
            
          }
        }else{
          int dialogResult = JOptionPane.showConfirmDialog(null, "There were not results for this search.\n\n Did you mean X?");
          if(dialogResult==JOptionPane.YES_OPTION) {
            
            
            
          }
        }
      }else if(type==0){
        String[]list=Vanilla.vectorQueryProcessing.processQuery(info);
        for(String word: list){
          if(!correct.inDictionary(word)){
            
            
          }}
        
        
        
        if(Vanilla.vectorSearchWithQuery(info)!=null){
          index=VanillaSystem.vectorSearchWithQuery(info);
          model.addColumn("Course Codes");
          for(int i:index) {
            model.addRow(new Object [] {VanillaSystem.documents[i].title});
            
          }
          
          
          
        }else{
          int dialogResult = JOptionPane.showConfirmDialog(null, "There were not results for this search.\n\n Did you mean X?");
          if(dialogResult==JOptionPane.YES_OPTION) {
            
            
            
          }
          
        }
        
        
      }
      
      
      
      
      return model;
      
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
          JScrollPane jsp = new JScrollPane(jta);
          jsp.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
          
          JOptionPane.showMessageDialog(null, jsp);
          
          
          
        }
      }); 
      
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      result.setFillsViewportHeight( true );
      
      
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
      BooleanButton.addActionListener(f -> result.setModel(makeModel(search.getText()),1));
      
      
      
    }
    
    
    
    
  }