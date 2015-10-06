/*
  Copyright (c) 2006 Adrian Colomitchi

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
 * Utility functions to deal with lines, line segments, etc.
 * @author Adrian Colomitchi
 */
public final class DistUtils {
  /**
   * The square of the euclidian distance between two points.
   * @param p1x x coordinate of the first point
   * @param p1y y coordinate of the first point
   * @param p2x x coordinate of the second point
   * @param p2y y coordinate of the second point
   */
  static final public double pointToPointSqEucDist(
    double p1x, double p1y,
    double p2x, double p2y
  ) {
    p2x-=p1x; p2y-=p1y;
    return p2x*p2x+p2y*p2y;
  }
  
  /**
   * The the euclidian distance between two points. Computes it by calling
   * <code>Math.sqrt(@link #pointToLineSqEucDist(double, double, double, double, double, double))</code>
   * (i.e. more computational extensive than computing the square of the distance).
   * @param p1x x coordinate of the first point
   * @param p1y y coordinate of the first point
   * @param p2x x coordinate of the second point
   * @param p2y y coordinate of the second point
   */
  static final public double pointToPointEucDist(
    double p1x, double p1y,
    double p2x, double p2y
  ) {
    p2x-=p1x; p2y-=p1y;
    return Math.hypot(p2x, p2y);
  }
  
  /**
   * The the <a href="http://en.wikipedia.org/wiki/Manhattan_distance">Manhattan</a>
   * distance between two points.
   * @param p1x x coordinate of the first point
   * @param p1y y coordinate of the first point
   * @param p2x x coordinate of the second point
   * @param p2y y coordinate of the second point
   */
  static final public double pointToPointMnhtDist(
    double p1x, double p1y,
    double p2x, double p2y
  ) {
    p2x=Math.abs(p2x-p1x); p2y=Math.abs(p2y-p1y);
    return p2x+p2y;
  }

  /**
   * The the <a href="http://en.wikipedia.org/wiki/Chebyshev_distance">chess (Chebyshev)</a>
   * distance between two points.
   * @param p1x x coordinate of the first point
   * @param p1y y coordinate of the first point
   * @param p2x x coordinate of the second point
   * @param p2y y coordinate of the second point
   */
  static final public double pointToPointCbsvDist(
    double p1x, double p1y,
    double p2x, double p2y
  ) {
    p2x=Math.abs(p2x-p1x); p2y=Math.abs(p2y-p1y);
    return p2x>p2y ? p2x : p2y;
  }

  /**
   * Computes the square of the euclidian distance between a point and its'
   * projection on a line (the distance between a point and the line).
   * no matter if the projection falls outside the 
   * <code>[startX, startY]...[endX, endY]</code> segment.
   * @param pointX the x coordinate of the point
   * @param pointY the y coordinate of the point
   * @param startX the x coordinate of the first point on the line
   * @param startY the y coordinate of the first point on the line
   * @param endX the x coordinate of the second point on the line
   * @param endY the y coordinate of the second point on the line
   */
  static final public double pointToLineSqEucDist(
    double pointX, double pointY,
    double startX, double startY,
    double endX,   double endY
  ) {
    pointX-=startX; pointY-=startY;
    endX-=startX; endY-=startY;
    double crossProd=pointX*endY-pointY*endX;
    double sqSegLen=endX*endX+endY*endY;
    
    double toRet=crossProd*crossProd/sqSegLen;
    if(Double.isNaN(toRet)) { // 0.0/0.0 all 3 points are coincident
      toRet=0.0;
    }
    else if(Double.isInfinite(toRet)) { // not-zero/0 line is degenerate replace it by 
      toRet=pointX*pointX+pointY*pointY; // sq dist between the given point and the first point on the line 
    }
    return toRet;
  }
  
  /**
   * Computes the euclidian distance between a point and its'
   * projection on a line (distance between a point and the line).
   * Calls the <code>Math.sqrt({@link #pointToLineSqEucDist(double, double, double, double, double, double) pointToLineSqEucDist})</code>,
   * which makes this method more computational intensive than computing the
   * square of the distance.
   * @param pointX the x coordinate of the point
   * @param pointY the y coordinate of the point
   * @param startX the x coordinate of the first point on the line
   * @param startY the y coordinate of the first point on the line
   * @param endX the x coordinate of the second point on the line
   * @param endY the y coordinate of the second point on the line
   */
  static final public double pointToLineEucDist(
    double pointX, double pointY,
    double startX, double startY,
    double endX,   double endY
  ) {
    double sqDist=DistUtils.pointToLineSqEucDist(
      pointX, pointY, startX, startY, endX, endY
    );
    return Math.sqrt(sqDist);
  }
  
  /**
   * Computes the the minimum squared euclidian distance to the segment's point 
   * which is the closest to the original point. That is, if the projection of 
   * the given point falls on the segment, the squared distance to the 
   * projection is returned. Otherwise, the method returns the squared distance 
   * to the end of the segment that is closest to the given point.
   * @param pointX the x coordinate of the point
   * @param pointY the y coordinate of the point
   * @param startX the x coordinate of the segment's start
   * @param startY the y coordinate of the segment's start
   * @param endX the x coordinate of the segment's end 
   * @param endY the y coordinate of the segment's end
   * @return the squared euclidian distance from the given point to the closest
   * point on the segment.
   */
  static final public double pointToSegSqEucDist(
    double pointX, double pointY,
    double startX, double startY,
    double endX,   double endY
  ) {
    double segDx=endX-startX, segDy=endY-startY;
    double dx=pointX-endX, dy=pointY-endY;
    double dotProd=dx*segDx+dy*segDy;
    if(dotProd>=0) { // the end point is the closest
      return dx*dx+dy*dy;
    }
    dx=pointX-startX; dy=pointY-startY;
    dotProd=dx*segDx+dy*segDy;
    if(dotProd<=0) { // the start point is the closest
      return dx*dx+dy*dy;
    }
    // the projection of the point on the seg's support line lays on the segment itself
    // We also know that the segment is not degenerate: would it be so, then
    // the segDx==segDy==0 and the function would return at the first "if" statement
    double sqSqLen=segDx*segDx+segDy*segDy; // this is !not! zero
    double sqProjLen=(dotProd*dotProd)/sqSqLen;
    double toRet=dx*dx+dy*dy-sqProjLen;
    if(toRet<0) { // could be so only because of float-point rounding error
      toRet=0;
    }
    return toRet;
  }
  
  /** Computes the the minimum euclidian distance to the segment's point 
   * which is the closest to the original point. Returns the square root of the
   * {@link #pointToSegSqEucDist(double, double, double, double, double, double)}.
   */
  static final public double pointToSegEucDist(
    double pointX, double pointY,
    double startX, double startY,
    double endX,   double endY
  ) {
    return Math.sqrt(
      DistUtils.pointToSegSqEucDist(
        pointX, pointY,
        startX, startY, 
        endX, endY
      )
    );
  }
}
