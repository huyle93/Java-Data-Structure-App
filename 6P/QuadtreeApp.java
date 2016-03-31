/**
 * QuadtreeApp -- App class for the ImageQuadtree assignment
 * rdb
 * 04/03/13
 */
import javax.swing.*;
import java.awt.*;

public class QuadtreeApp extends JFrame
{
   //---------------------- instance variables ----------------------
   private GUIPanel _appPanel;      // the app's JPanel
   
   //--------------------------- constructor -----------------------
   public QuadtreeApp( String title )     
   {
      super( title );
 
      this.setBackground( Color.LIGHT_GRAY );
      this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      
      _appPanel = new GUIPanel( this );
      this.add( _appPanel );
      
      this.setSize( _appPanel.getWidth(), _appPanel.getHeight() + 100 );

      this.setVisible( true );
   }
  //------------------ main ------------------------------------------   
   public static void main( String [ ] args ) 
   {
      new QuadtreeApp( "QuadtreeApp" );
   }
}
