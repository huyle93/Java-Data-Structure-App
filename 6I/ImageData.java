/**
 * ImageData.java -- Create a gray scale BufferedImage from a simple 
 *       text input file. Input format:
 *       -- first 2 numbers in the file are the rows and cols of the data
 *       -- the rest of the data are r * c int values in the range (0,255)
 * rdb 04/03/13
 */

import java.io.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class ImageData //extend BufferedImage??? 
{  
   //--------------------- instance variables ------------------------
   private DataBufferByte _buffer;
   private int            _width  = -1;
   private int            _height = -1;
   private short[][]      _data;
   private Dimension      _size;
   private BufferedImage  _image;
   private Point          _loc;    // location of the image
   private float          _scale;  // scale factor at draw time
   /**
    * Create an image from the specified filename. Note that if you are 
    * running on Windows, the location of the file must be in your classpath.
    *
    * @param dp the DrawingPanel for this image
    * @param file the filename to take this image form (gif or jpg). 
    */
   public ImageData( String file ) 
   {
      Scanner in = openFile( file );
      if ( readData( in ) )
         _image = makeImage( _buffer, _width, _height, "yellow" );
      else
         System.err.println( "No image built" );
   }
   
   //------------------------- getSize() ---------------------------------
   /**
    * Get the dimensions of this ImageData
    *
    * @return the <code>Dimension</code> for this ImageData
    */
   public java.awt.Dimension getSize () 
   {
      if ( _size == null )
         _size = new Dimension( _width, _height );
      return _size;
   }

   //--------------------- getImage() ----------------------------------
   /**
    * Get the BufferedImage 
    */
   public BufferedImage getImage() 
   {
      return _image;
   }
   
   //------------------------ getData() --------------------------------
   /**
    * Get the data 
    */
   public short[][] getData() 
   {
      return _data;
   }
   //----------------------- setLocation( Point ) -------------------
   /**
    * set the location of the image in the panel; these values must
    * be requested at draw time in order for any effect to happen.
    */
   public void setLocation( Point p )
   {
      _loc = p;
   }
   //----------------------- getLocation( ) -------------------
   /**
    * return the location.
    */
   public Point getLocation()
   {
      return _loc;
   }
   //----------------------- setScale( float ) -------------------
   /**
    * set the display scale factor.  This value must
    * be requested at draw time in order for any effect to happen.
    */
   public void setScale( float scale )
   {
      _scale = scale;
   }
   //----------------------- getScale( ) -------------------
   /**
    * return the location.
    */
   public float getScale()
   {
      return _scale;
   }
   //-------------------------- openFile( String ) ---------------------
   /**
    * Create and return a Scanner for the specified file
    */
   private Scanner openFile( String fileName )
   {
      Scanner scanner = null;
      try
      {
            scanner = new Scanner( new File( fileName ));
      }
      catch ( IOException ioe )
      {
         System.err.println( "***Error -- can't open " + fileName );
         System.exit( -1 );
      }
      return scanner;
   }
   //--------------------- readData( Scanner ) ------------------------
   /**
    * read the data using the scanner
    */
   private boolean readData( Scanner in )
   {
      String msg = null;
      int downClamps = 0;
      int upClamps = 0;
      try {
         _height = in.nextInt();
         _width = in.nextInt();
         _data = new short[ _height ][ _width ];
         byte[] bytes = new byte[ _height * _width ];
         int b = 0;
         
         for ( int r = 0; r < _height; r++ )
         {
            for ( int c = 0; c < _width; c++ )
            {
               short val =  in.nextShort();
               if ( val > 255 )
               {
                  val = 255;
                  downClamps++;
               }
               else if ( val < 0 )
               {
                  val = 0;
                  upClamps++;
               }                  
               _data[ r ][ c ] = val;
               bytes[ b++ ] = (byte) val;               
            }
         }
         _buffer = new DataBufferByte( bytes, bytes.length );
      } 
      catch ( InputMismatchException ife )
      {
         msg = "Input file can only contain integers";
      }
      catch ( NoSuchElementException nsee )
      {
         if ( _width == -1 || _height == -1 )
            msg = "Does file start with 'width, height' values?";
         else
            msg = "Data value count doesn't match specified size";
      }
      if ( msg != null )
         System.err.println( "***** ImageData.readData error: " + msg );
      if ( upClamps > 0 || downClamps > 0 )
         System.err.println( "*** Warning: input data out of range:\n" + 
                  "           " + upClamps + " values < 0;    " 
                                + downClamps + " values > 255" );
      return msg == null;
   }
   
   //-------------- makeImage  --------------------------------
   /**
    * Map a Java DataBufferByte version of the data to a Java Image object
    */
   BufferedImage makeImage( DataBufferByte data, int w, int h, 
                                   String lutName )
   {
      IndexColorModel cm = getLUTColorModel( lutName );
      BufferedImage  bimg = new BufferedImage( w, h, 
                         BufferedImage.TYPE_BYTE_INDEXED, cm );
      SampleModel sm = new ComponentSampleModel( 
                         DataBuffer.TYPE_BYTE, w, h, 1, w, new int[]{0} );
      
      Raster imgRaster = Raster.createWritableRaster( 
                                                     sm, data, new Point( 0,0) );
      bimg.setData( imgRaster );
      return bimg;
   }
   
   //-------------- getLookupTable --------------------------------
   /**
    * Create a color lookup table to represent the image.
    * This is a bit of a hack; we really ought to be able to read a lookup
    * table from a file.
    */
   static IndexColorModel getLUTColorModel( String lutName )
   {
      byte[] r = new byte[ 256 ];
      byte[] g = new byte[ 256 ];
      byte[] b = new byte[ 256 ];
      
      // We support lookup tables for: 
      //             gray, red, blue, green, cyan, magenta, yellow
      // The r, g, and b arrays should contain values from 0 to 255 or all 0. 
      // The different lookup table names determine which arrays are 0 and 
      // which are 0..255 (a linear ramp).      
      String validLUTs[] = 
      { "gray", "red", "green", "blue", "cyan", "magenta", "yellow" };
      
      // each lut either has a linear ramp or all 0 for the rgb arrays; 
      // encode that info for each table in a parallel String array with 
      // 1 character for each of rgb and where the character 'r' is for ramp, 
      // and '0' for zeroes
      String rgbCodes[]  = 
      { "rrr",  "r00", "0r0",   "00r",  "0rr",  "r0r",     "rr0" };
      
      // First figure out which lut we have
      if ( lutName == null )  // if none, use gray
         lutName = "gray";     
      String code = null; 
      for ( int i = 0; i < validLUTs.length && code == null; i++ )
      {
         if ( lutName.equalsIgnoreCase( validLUTs[ i ]))
            code = rgbCodes[ i ];
      }
      if ( code == null )
      {
         System.err.println( "ERROR: Invalid Lookup table name: " + lutName +
                            "Using gray scale." );
         code = "rrr";                              
      }
      
      // now fill in the r,g, and b arrays with a ramp or 0
      for ( int i = 0; i < 256 ; i++ )
      {
         if ( code.charAt( 0 ) == 'r' )
            r[i] =  (byte) i;
         else 
            r[i] = 0;
         if ( code.charAt( 1 ) == 'r' )
            g[i] =  (byte) i;
         else 
            g[i] = 0;
         if ( code.charAt( 2 ) == 'r' )
            b[i] =  (byte) i;
         else 
            b[i] = 0;
      }     
      return new IndexColorModel( 8, 256, r, g, b );
   }
   
   public static void main( String[] args )
   {
      ImageData di = new ImageData( "head100.dat" );
      /**********
      for ( int r = 0; r < di._height; r++ )
      {
         for ( int c = 0; c < di._width; c++ )
         {
            System.out.print( di._data[ r ][ c ] + " " );
         }
         System.out.println();
      }
      System.out.println();
      /****/
   }
}
