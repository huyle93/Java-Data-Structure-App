/**
 * TreeManager -- build and manage quad tree for the image
 * 
 * rdb 04/11/10
 */

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

public class TreeManager
{
   //-------------------------- instance variables -----------------------
    ImageData imgData;
    private boolean showtree;
    private short[][] data;
    private int maxDepth, pathSize, depth, tolerance;
    private Node _node;
    private Node xnode;
    private short max, min;
    public static double rootaverage;
   /////////////////////////////////////////////////////////////////////
   // If you use Java Rectangles or our ARectangle to xnode your quadtree,
   //    You'll need an ArrayList or Vector or some kind of collection
   //    data structure to store them here, so you can rexnode them in 
   //    paintComponent. 
   // 
   /////////////////////////////////////////////////////////////////////
   //--------------------------- constructor -----------------------------
   public TreeManager( Component parent, ImageData imgData, 
                       int maxDepth, int maxErr )
   {
       showtree = true;
       data = imgData.getData();
       this.imgData = imgData;
       this.maxDepth = maxDepth;
       
       imgData.getSize().height = (int)(256*imgData.getScale());
       imgData.getSize().width = (int)(256*imgData.getScale());
       
       _node = new Node( imgData, maxDepth, depth, imgData.getLocation().getX(), imgData.getLocation().getY() );
       xnode = null;
   }
   //----------------------- showNode( ArrayList ) -------------------
   public void showNode( ArrayList<Integer> path )
   {
       pathSize = path.size();
      if( maxDepth < pathSize )
      {
        System.err.println( "ERROR: NO SUCH NODE " );
      }
      else//( !(maxDepth < pathSize ) )
      { 
        xnode = _node.showNode( path );
        System.out.println( "average:  " + xnode.getaverage() + 
                           "error:  " + xnode.getError() );
        
      } 
   }
   //----------------------- setShowTree( boolean ) -------------------
   public void setShowTree( boolean showTree )
   {
       showtree = showTree;
   }
   //----------------------- setErrorTolerance( int ) -------------------
   public void setErrorTolerance( int tol )
   {
       tolerance = tol;
   }
   //----------------------- setMaxDepth( int ) -------------------
   public void setMaxDepth( int maxDepth )
   {
       xnode = null;
       this.maxDepth = maxDepth;
       _node = new Node( imgData, maxDepth, depth, imgData.getLocation().getX(), imgData.getLocation().getY() );
   }
   //--------------------- paint( Graphics ) ---------------------------
   public void paint( Graphics g )
   {
      Graphics2D g2 = (Graphics2D) g;
      
      if( showtree )
      {
          _node.paint( g2, true, tolerance );
          if( xnode != null )
          {
              xnode.paint( g2, false, tolerance );
          }
      }
      /////////////////////////////////////////////////////////////////////
      // The TreeManager object needs to create (or have another class create)
      //    all the rectangles that define the current leaves of the 
      //    quadtree based on the current depth and error tolerance values.
      // You DO NOT need to xnode your rectangles using the java Rectangle 
      //    class. You could probably use our JRectangle or ARectangle classes.
      // If you use either Rectangle or ARectangle, you need to explicitly 
      //    re-xnode them in a method called from a paintComponent method.
      // This paint method is called from the ImagePanel's paintComponent since
      //    the rectangles need to be xnoden AFTER the image is generated, so
      //    they will be on top of the image.
      // You'll also have to add code here to TreeManager so that it "knows"
      //    the rectangle for the currently highlighted node (the last for
      //    which Show Node was invoked. In this code you need to check for
      //    that rectangle and change its color to something like RED and 
      //    make its stroke at least 3 pixels wide. 
      ///////////////////////////////////////////////////////////////////////

   }
   //------------------ main ------------------------------------------   
   public static void main( String [ ] args ) 
   {
      new QuadtreeApp( "QuadtreeApp" );
   }     
}