import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.lang.Object;

public class Node extends ARectangle 
{
    //----------instance variables ------------------//
    private double minR, minC, maxR, maxC, midR, midC, dx, dy, x, y, width, height;
    ImageData imagedata;
    double scale;
    
    private int count, depth, maxDepth;
    private short[][] data;
    private Color c;
    private double error;
    private Graphics2D g2;
    private Node[] quads;
    // ------------ constructors ------------//
    public Node( ImageData imagedata, int maxDepth, int depth, double x1, double y1 )
    {
        
        this.imagedata = imagedata;
        this.depth = depth;
        this.maxDepth = maxDepth;
        data = imagedata.getData();
        
        scale = imagedata.getScale();
        
        
        height = (imagedata.getSize().getHeight() * Math.pow(0.5, depth + 1 )) * scale;
        width = (imagedata.getSize().getWidth() * Math.pow( 0.5, depth + 1 )) * scale;
        
        minR = x1;
        minC = y1;
        
        maxR = width -  minR;
        maxC = height - minC;
        midR = (minR + maxR) / 2;
        midC = (minC + maxC) / 2;
        
        x = minR; 
        y = minC; 
        
        makeBranch( depth, maxDepth, x, y, width, height );
        
        getError();
        getMax();
        getMin();
        
    }
    /**
     * makeBranch recursively
     */ 
    public void makeBranch( int depth ,int maxDepth, double x, double y, double width, double height )
    {
        
        if(depth < maxDepth)
        {
            quads = new Node[4];
            
            Node q0 = new Node(imagedata, maxDepth, depth + 1, x, y );
            quads[0] = q0;
            
            Node q1 = new Node(imagedata, maxDepth, depth + 1, x + width / 2, y );
            quads[1] = q1;
            
            Node q2 = new Node(imagedata, maxDepth, depth + 1, x, y + height / 2 );
            quads[2] = q2;
            
            Node q3 = new Node(imagedata, maxDepth, depth + 1, x + width / 2, y + height / 2);
            quads[3] = q3;
        }
        
    }
    /**
     */ 
    public Node showNode( ArrayList<Integer> path )
    {
        Node c = this;
        for( int i = 0; i < path.size(); i++ )
        {
            c = c.quads[ path.get(i) - 1 ]; 
            
        }
        return c;
    }
    /*
     */
    public int getMax()
    {
        short da[][] = imagedata.getData();
        short maxValue = Short.MIN_VALUE;
        for( int i = (int) ((x/scale) - 21.5); i < (int)( ( ( x + width) / scale) - 21.5); i++ )
        {
            for(int j = (int)(y /scale); j < (int)( (y + height) / scale); j++ )
            {
                
                if(da[i][j] > maxValue)
                {
                    maxValue = da[i][j];
                }
            }
        }
        return maxValue;
    }
    public short getMin()
    {
        short[][] da = imagedata.getData();
        short minValue = Short.MAX_VALUE;
        
        for( int i = (int) ( ( x / scale ) - 21.5); i < (int)( ( ( x + width) / scale) - 21.5); i++ )
        {
            for(int j = (int)(y /scale); j < (int)( (y + height) / scale); j++ )
            {
                if(da[i][j] < minValue)
                {
                    minValue = da[i][j];
                }
            }
        }
        return minValue;
    }
    /**
     */
    public void paint(Graphics g, boolean b, int tol ) 
    {
        g2 = (Graphics2D) g;
        if( b )
        {
            g2.setColor( Color.WHITE );
        }
        else 
        {
            g2.setColor( Color.RED );
        }
        if( depth > 0 )
        {
            g2.drawRect( (int)x, (int)y, (int)minR + (int)maxR, (int)minC +(int)maxC );
        }
        
        if( quads != null && b )
        {
            if( tol == 0 || error > (tol/100) * getRoota() )
            {
                
                for( int i = 0; i < quads.length ; i ++ )
                { 
                    quads[ i ].paint(g, true, tol); 
                }
            }
        }      
    }
    //********************************************************
    
    
    public int getaverage()
    {
        double sum = 0.0;
        double count = 0.0;
        for( int i = (int) ((x/scale) - 21.5); i < (int)( ( ( x + width) / scale) - 21.5); i++ )
        {
            for(int j = (int)(y /scale); j < (int)( (y + height) / scale); j++ )
            {
                count++;
                sum = sum + data[i][j];
            }
        }
        return (int)(sum/count);
    }
    public double getError()
    {
        double ave = this.getaverage();
        error = Math.max( ave - getMin(), getMax() - ave );
        return error;
    }
    
    public int getRoota()
    {
        short[][] a = imagedata.getData();
        
        double sum = 0.0;
        double count = 0.0;
        for( int i = 0; i < data.length; i++ )
        {
            for(int j = 0; j < data[i].length; j++ )
            {
                sum = sum + a[i][j];
                count++;
            }
        }
        
        return (int)(sum/count);
    }
    
}
