/**
 * ImagePanel - A JPanel expected to hold an image as well as other drawing
 *              elements "on top" of the image
 * 
 * @author rdb
 * 04/03/13
 */
import java.util.*;
import java.awt.Color;
import javax.swing.JOptionPane;
import java.io.File;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

public class ImagePanel extends JPanel
{
   //------------------- class variables -------------------------------
   //------------------instance variables ------------------------------
   private  int    _pWidth  = 750;    // panel width
   
   private ImageData     _imgData   = null;
   private Component     _parent = null;
   private BufferedImage _image = null;
   private int           _imageX;
   private int           _imageY;
   private int           _imageScale = 2;
   
   private TreeManager   _treeManager = null;
   
   //------------------- constructor -----------------------------------
   public ImagePanel( ImageData imgData, int pWidth, int pHeight )
   {
      this.setLayout( null );
      _pWidth = pWidth;
      _imgData = imgData;
      _image = _imgData.getImage();
      setSize( pHeight, pWidth );
      
      // center the image in the panel
      _imageX = ( pWidth - (int )_image.getWidth() * _imageScale ) / 2;
      
      _imgData.setLocation( new Point( _imageX, _imageY ));
      _imgData.setScale( _imageScale );
   }
   //--------------------setTreeManager( TreeManager ) --------------------
   /**
    * set reference to the TreeManager object
    */
   public void setTreeManager( TreeManager treeManager )
   {
      _treeManager = treeManager;
   }
      
   //------------------------- paintComponent ----------------------------
   /**
    * Need a paintComponent method, so we can draw the image, which is 
    * not a Component itself
    */
   public void paintComponent( Graphics g ) 
   {
      super.paintComponent( g );
      Graphics2D g2 = (Graphics2D) g;
      int        w  = getSize().width;  // get space for image
      int        h  = getSize().height;
      int        bw = _image.getWidth( this );      // get image size
      int        bh = _image.getHeight( this );
      
      AffineTransform at = new AffineTransform();
      at.scale( (float) _imageScale,  _imageScale );
      BufferedImageOp biop = new AffineTransformOp( at, 
                         AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
      
      g2.drawImage( _image, biop, _imageX, _imageY );  
      if ( _treeManager != null )
         _treeManager.paint( g );
   }
   //------------------ main ------------------------------------------   
   public static void main( String [ ] args ) 
   {
      new QuadtreeApp( "QuadtreeApp from GUIPanel" );
   }
   
}