/**
 * GUIPanel.java -- For ImageQuadtree assignment 
 *                 Creates interaction widgets and a panel for an image
 *
 * 04/03/13
 */
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;

public class GUIPanel extends JPanel 
{
   //------------------------- instance variables -----------------------
   private JPanel      _thisPanel;
   private ImagePanel  _imagePanel = null; 
   private TreeManager _treeMgr;
   private int         _width  = 600;     // width of the panel
   private int         _height = 600;     // height of the panel
   private int         _maxErr = 0;     // error tolerance
   private boolean     _showTree  = true; // show boundary of each quadtree node
   private int         _maxDepth  = 3;    // max dept of the quadtree
   
   //------------------------- constructor ----------------------------
   /**
    * GUIPanel constructor creates the game and waits for interaction.
    */
   public GUIPanel( Container frame ) 
   {
      super();  
      _thisPanel = this;
      setLayout( new BorderLayout() );    
      this.setBackground( Color.WHITE );
      
      setSize( _width, _height );
      
      Component buttonMenu = makeButtonMenu();
      this.add( buttonMenu, BorderLayout.NORTH );
      
      Component controlMenu = makeControlMenu();
      this.add( controlMenu, BorderLayout.SOUTH );
      
      buildImageTree( "head100.dat" );
   }
   //-------------------- buildImageTree( String ) -------------------
   /**
    * create the imageData object, the ImageQuadtree over it, and a
    * TreeManager object.
    */
   private void buildImageTree( String fileName )
   {
      setSize( _width, _height );
      
      ImageData imageData = new ImageData( fileName );
      if ( _imagePanel != null )
         this.remove( _imagePanel );
      _imagePanel = new ImagePanel( imageData, _width, _height - 100 );
      this.add( _imagePanel, BorderLayout.CENTER );
            
      _treeMgr = new TreeManager( this, imageData, _maxDepth, _maxErr );
      _imagePanel.setTreeManager( _treeMgr ); 
      doLayout();
   }
   //------------------- makeControlMenu --------------------------------
   /**
    * create a control menu that includes only a Slider for the error
    */
   private Component makeControlMenu( )
   {
      JPanel sMenu = new JPanel(); 
      LabeledSlider errSlider = new LabeledSlider( "Error", 0, 100, _maxErr );
      ///////////////////////////////////////////////////////////////////
      // Code below is an example of an "anonymous" class. We want to extend
      // ChangeListener, but are only going to define 1 method. Rather than
      // creating a whole new class somewhere else in the file, Java lets
      // you define a class without a name at the same time that you
      // create the one instance you will need of that class.
      // Just follow the new statement with { .. }; and include the method
      // (or methods) you want to override.
      ChangeListener  cl = new ChangeListener()
      {  
         public void stateChanged( ChangeEvent ev )
         {  
            JSlider slider = (JSlider) ev.getSource();
            Number value = (Number) slider.getValue();
            if ( !slider.getValueIsAdjusting() )
            {
               _maxErr = value.intValue();
               _treeMgr.setErrorTolerance( _maxErr );
               //_imagePanel.repaint();
               _thisPanel.repaint();
            }
         }
      };
      errSlider.addChangeListener( cl );
      errSlider.setSize( 50, 100 );
      errSlider.getJSlider().setMajorTickSpacing( 10 );
      sMenu.add( errSlider );
      
      LabeledSpinner dSpin = new LabeledSpinner( "MaxDepth", 0, 5, _maxDepth );
      ChangeListener  spincl = new ChangeListener()
      {  
         public void stateChanged( ChangeEvent ev )
         {  
            JSpinner spinner = (JSpinner) ev.getSource();
            Number value = (Number) spinner.getValue();
            _treeMgr.setMaxDepth( value.intValue() );
            _maxDepth = value.intValue();
            _thisPanel.repaint();
         }
      };
      dSpin.addChangeListener( spincl );
      sMenu.add( dSpin );
      
      return sMenu;
   }
   //------------------- makeButtonMenu --------------------------------
   /**
    * create the button menu for this application
    */
   private Component makeButtonMenu()
   {
      JPanel bMenu = new JPanel( new GridLayout( 1, 0 )); 
      JButton button = new JButton( "Read file" );
      bMenu.add( button );
      button.addActionListener( new ButtonListener( "read" ));

      button = new JButton( "Show Node" );
      bMenu.add( button );
      button.addActionListener( new ButtonListener( "node" ));

      /****************
      button = new JButton( "Save Block" );
      bMenu.add( button );
      button.addActionListener( new ButtonListener( "save" ));
      /***********************************************************/

      ButtonGroup treeShowGroup = new ButtonGroup();
      JRadioButton rbutton = new JRadioButton( "Show tree" );      
      rbutton.addActionListener( new ButtonListener( "tree" ));
      bMenu.add( rbutton );
      treeShowGroup.add( rbutton );
      rbutton.setSelected( true );
      
      rbutton = new JRadioButton( "Hide tree" );      
      rbutton.addActionListener( new ButtonListener( "hide" ));
      bMenu.add( rbutton );
      treeShowGroup.add( rbutton );
      bMenu.setSize( 40, 400 );
      
      return bMenu;
   }
   //+++++++++++++++++ ButtonListener inner class ++++++++++++++++++++++++
   /**
    * ButtonListener -- distributes button events to appropriate methods
    *                   of the GUIPanel class.
    */
   public class ButtonListener implements ActionListener
   {
      String _buttonKey;
      public ButtonListener( String buttonKey )
      {
         _buttonKey = buttonKey;
      }
      public void actionPerformed( ActionEvent ev )
      {
         String msg = null;
         if ( _buttonKey.equals( "read" ) ) // re-read the file
            handleRead();
         else if ( _buttonKey.equals( "node" ) ) // show a node
            handleShowNode();
         //else if ( _buttonKey.equals( "save" ) ) // save a subblock in a file
            //_treeMgr.saveBlock( );
         else if ( _buttonKey.equals( "tree" ) ) // redraw the image
            _treeMgr.setShowTree( true );
         else if ( _buttonKey.equals( "hide" ) ) // redraw the image
            _treeMgr.setShowTree( false );
         
         _thisPanel.repaint();
      }
   }
   //------------------- handleRead -----------------------------------
   /**
    * respond to a Read button press
    */
   private void handleRead()
   {
      String fileName = Utilities.getFileName();
      if ( fileName != null && fileName.length() > 0 )
      {
         if ( _imagePanel != null )
            _thisPanel.remove( _imagePanel );
         buildImageTree( fileName );
      }
   }
   //------------------- handleShowNode -----------------------------------
   /**
    * respond to a Show Node button press
    */
   private void handleShowNode()
   {
      String msg = "Enter path to node";
      String pathString = JOptionPane.showInputDialog( null, msg );
      if ( pathString == null || pathString.length() == 0 ) 
         return;
      Scanner scan = new Scanner( pathString );
      ArrayList<Integer> path = new ArrayList<Integer>();
      while ( scan.hasNextInt() )
         path.add( new Integer( scan.nextInt() ));
      _treeMgr.showNode( path );
   }
   
   //------------------ main ------------------------------------------   
   public static void main( String [ ] args ) 
   {
      new QuadtreeApp( "QuadtreeApp from GUIPanel" );
   }
}   
