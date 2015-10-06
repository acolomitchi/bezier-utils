/*
  Copyright (c) 2008 Adrian Colomitchi

  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation
  files (the "Software"), to deal in the Software without 
  restriction, including without limitation the rights to use, 
  copy, modify, merge, publish, distribute, sublicense, and/or 
  sell copies of the Software, and to permit persons to whom the
  Software is furnished to do so, subject to the following 
  conditions:

  The above copyright notice and this permission notice 
  shall be included in all copies or substantial portions
  of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF
  ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
  TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
  PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
  SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
  ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
 */

package com.caffeineowl.graphics;

/**
 * Class to implement various algorithms related with 
 * planar (2D) geometry: intersections, areas, etc.
 * @author Adrian Colomitchi
 */
public class GeomUtils {
  /**
   * Maximum tolerance to be used when dealing with distances.
   * Values under this tolerance are considered as <code>0</code> (zero);
   * or, locations separated by distances less than this value will be 
   * considered as being the same/indiscernible. 
   */
  static protected double distTolerance=1.0e-5;
  
  /**
   * Maximum tolerance to be used when dealing with angles.
   * Values under this tolerance are considered as <code>0</code> (zero).
   */
  static protected double angleTolerance=Math.PI*1.0e-5/180.0/3600.0;
  
  /**
   * Maximum tolerance to be used when dealing with areas.
   * Values under this tolerance are considered as <code>0</code> (zero).
   */
  static protected double areaTolerance=1.0e-10;
  
  
  
  /**
   * @return the angleTolerance
   */
  final public static double getDistTolerance() {
    return distTolerance;
  }
  /**
   * @param distTolerance the distTolerance to set
   */
  final public static void setDistTolerance(double distTolerance) {
    GeomUtils.distTolerance=distTolerance;
  }
  /**
   * @return the angleTolerance
   */
  final public static double getAngleTolerance() {
    return angleTolerance;
  }
  /**
   * @param angleTolerance the angleTolerance to set
   */
  final public static void setAngleTolerance(double angleTolerance) {
    GeomUtils.angleTolerance=angleTolerance;
  }
  /**
   * @return the areaTolerance
   */
  public static double getAreaTolerance() {
    return areaTolerance;
  }
  /**
   * @param areaTolerance the areaTolerance to set
   */
  public static void setAreaTolerance(double areaTolerance) {
    GeomUtils.areaTolerance=areaTolerance;
  }
  /**
   * The points will be considered 
   * <a href="http://mathworld.wolfram.com/Collinear.html">collinear</a> 
   * (on the same straight line, including the degenerate cases)
   * <a href="http://mathworld.wolfram.com/Iff.html">iff</a>
   * the area of the triangle defined by the three points is zero.
   * The method actually compute the double of this area, by using
   * the <a href="http://mathworld.wolfram.com/CrossProduct.html">cross-product</a>
   * of (<b>b</b>-<b>a</b>)&nbsp;<b><code>x</code></b>&nbsp;(<b>c</b>-<b>a</b>):
   * if this is below the {@link #setAreaTolerance tolerance}, the points will
   * be considered collinear.
   */
  final static boolean arePointsCollinear(
    double ax, double ay,
    double bx, double by,
    double cx, double cy
  ) {
    double dx1=bx-ax, dy1=by-ay;
    double dx2=cx-ax, dy2=cy-ay;
    double crossProd=dx1*dy2-dx2*dy1;
    if(crossProd<0) {
      crossProd=-crossProd;
    }
    return (crossProd<=GeomUtils.areaTolerance);
  }
  
  /**
   * The points <tt>p0</tt> and <tt>p1</tt>
   * will be considered separated by the line passing through
   * <tt>start</tt> and <tt>end</tt>
   * if, while "staying in <tt>start</tt> and looking towards <tt>end</tt>,
   * one case see the <tt>p0</tt> by turning their head on the opposite
   * side of the line as for seeing <tt>p1</tt>".
   * As the "angle of turning" will have the same sign as the 
   * <a href="http://mathworld.wolfram.com/CrossProduct.html">cross-product</a>,
   * between the vector defining the two directions, the method
   * simply relies on the fact that the <tt><a href="http://mathworld.wolfram.com/Sign.html">signum</a></tt> 
   * of the two cross-products must be opposite.<p>
   * Note: if the line defined by <tt>start</tt> and <tt>end</tt> is a degenerate one
   * (i.e. the <tt>start</tt> and <tt>end</tt> points are equal or very close on of the other)
   * the return value will be strongly affected by the computation errors, but mostly
   * the return value will be given by the value of the <tt>strict</tt> parameter.
   * @param strict if <tt>true</tt>, having one of the <tt>p0</tt> or <tt>p1</tt>
   *  on the <tt>start->end</tt> line will cause this method to return <code>false</code>
   *  (i.e. the points must be really separated by the line, with none of them on the line).
   *  If <code>false</code>, then any (or both) of the points being on the line
   *  will cause a positive response.
   */
  final static boolean lineSeparatesPoints(
    double startx, double starty,
    double endx, double endy,
    double p0x, double p0y,
    double p1x, double p1y,
    boolean strict
  ) {
    double dx=endx-startx, dy=endy-starty;
    double d0x=p0x-startx, d0y=p0y-starty;
    double d1x=p1x-startx, d1y=p1y-starty;
    
    double dotProd0=dx*d0y-dy*d0x;
    double dotProd1=dx*d1y-dy*d1x;
    
    double sgn=Math.signum(dotProd0)*Math.signum(dotProd1);
    return strict ? (sgn <0 ) : (sgn <= 0);
  }
}
