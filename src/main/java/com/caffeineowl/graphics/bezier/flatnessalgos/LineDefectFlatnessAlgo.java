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

package com.caffeineowl.graphics.bezier.flatnessalgos;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;

import com.caffeineowl.graphics.DistUtils;
import com.caffeineowl.graphics.bezier.CubicFlatnessAlgorithm;
import com.caffeineowl.graphics.bezier.QuadFlatnessAlgorithm;

/**
 * The algorithm computes the defected as the distance between
 * the actual location control point(s) and the location of 
 * the control point(s) of a totally degenerated Bezier segment:<ul>
 * <li>for a quadratic Bezier, that's the mid point of the segment
 * connecting the anchor points, 
 * <li>for a totally degenerated cubic, the locations of the
 * control points are at <code>1/3</code> and <code>2/3</code>
 * positions of the segment that connects the anchor points.
 * </ul>
 * Note that, while this algorithm is the least computational
 * intensive, it applies the most strict flatness criterion:
 * the Bezier segment needs to be close to the total degeneration
 * to offer a good flatness.
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com) 
 */
public class LineDefectFlatnessAlgo
  implements CubicFlatnessAlgorithm, QuadFlatnessAlgorithm {
  
  /**
   * Use the Euclidian norm to compute the distance between two points
   */
  static public final int EUCL_DIST=0;
  /**
   * Use the Manhattan (taxi-cab) distance to compute the distance between two points
   */
  static public final int MNHT_DIST=1;
  /**
   * Use the Chebyshev (chess-board) distance to compute the distance between two points
   */
  static public final int CBSV_DIST=2;
  
  /**
   * The distance to use in computing the flatness. Must be one of 
   * {@link #EUCL_DIST}, {@link #MNHT_DIST} or {@link #CBSV_DIST}.
   */
  protected int       distType;
  /**
   * Relevant only if this flatness algo is applied for the
   * {@linkplain #getFlatness(CubicCurve2D) cubic}: when <code>true</code>,
   * the flatness is returned as the sum of the distances between the
   * control points and their ideal position of the degenerate cubic.
   * If <code>false</code>, the maximum of the two is returned as the
   * flatness.
   */
  protected boolean   usingSum;
  /**
   * Defaults to {@link #LineDefectFlatnessAlgo(int, boolean)},
   * with a {@linkplain LineDefectFlatnessAlgo#EUCL_DIST EUCL_DIST} for
   * the distance type and using the {@linkplain #usingSum sum} for the case
   * of cubics.
   */
  public LineDefectFlatnessAlgo() {
    this(LineDefectFlatnessAlgo.EUCL_DIST, true);
  }

  /**
   * Defaults to {@link #LineDefectFlatnessAlgo(int, boolean)},
   * with the specified distance type and using the 
   * {@linkplain #usingSum sum} for the case
   * of cubics.
   */
  public LineDefectFlatnessAlgo(int distType) {
    this(distType, true);
  }
  
  /**
   * Defaults to {@link #LineDefectFlatnessAlgo(int, boolean)},
   * with a {@linkplain LineDefectFlatnessAlgo#EUCL_DIST EUCL_DIST} for
   * the distance type and using the specified behaviour for the use of
   * {@linkplain #usingSum sum/max} when computing the flatness for cubics.
   * 
   */
  public LineDefectFlatnessAlgo(boolean useSumation) {
    this(LineDefectFlatnessAlgo.EUCL_DIST, useSumation);
  }
  
  /**
   * Initialised this instance with the specified {@linkplain #distType distance type} and
   * {@linkplain #usingSum summation} behaviour.
   * @param distType the distance type. Must be one of 
   *   {@link #EUCL_DIST}, {@link #MNHT_DIST} or {@link #CBSV_DIST}. If it is not, the
   *    {@link #EUCL_DIST} will be used.
   * @param useSum sum-or-max strategy to be used when computing the flatness of a cubic.
   */
  public LineDefectFlatnessAlgo(int distType, boolean useSum) {
    switch(distType) {
      case LineDefectFlatnessAlgo.MNHT_DIST:
      case LineDefectFlatnessAlgo.CBSV_DIST:
        this.distType=distType;
        break;
      default:
        this.distType=LineDefectFlatnessAlgo.EUCL_DIST;
        break;
    }
    this.usingSum=useSum;
  }

  /**
   * Yes, works fine for degenerated curves (curves on which the control point(s) are
   * on the line defined by its anchors).
   */
  final public boolean isDegenerationRobust() {
    return true;
  }

  /**
   * Returns <code>true</code> if the {@linkplain #distType distance type} is
   * {@link #EUCL_DIST} (since computing the squared distance is less
   * computational expensive than computing the distance), <code>false</code> if
   * the distance type is {@link #MNHT_DIST} or {@link #CBSV_DIST} (for those two,
   * computing the distance is less computational expensive than computing the
   * squared distance}.
   */
  final public boolean isSquaredFlatenessPreferred() {
    return this.distType==LineDefectFlatnessAlgo.EUCL_DIST;
  }

  /**
   * Computes the flatness of the cubic using the 
   * {@linkplain LineDefectFlatnessAlgo#distType distance type} and 
   * {@linkplain #usingSum sum/max} strategy used in 
   * {@linkplain #LineDefectFlatnessAlgo(int, boolean) initialisation}.
   * The flatness (actually the defect of the flatness - the higher, the worse) 
   * is computed based on the distance between the control points of the provided
   * curve and the "ideal" positions (at <tt>1/3</tt> and <tt>2/3</tt>
   * on the segment defined by the control points).
   */
  final public double getFlatness(CubicCurve2D curve) {
    double p0x=curve.getX1(), p0y=curve.getY1();
    double c0x=curve.getCtrlX1(), c0y=curve.getCtrlY1();
    double c1x=curve.getCtrlX2(), c1y=curve.getCtrlY2();
    double p1x=curve.getX2(), p1y=curve.getY2();
    double d0=0, d1=0;
    switch(this.distType) {
      case LineDefectFlatnessAlgo.MNHT_DIST:
        d0=DistUtils.pointToPointMnhtDist(c0x, c0y, (2.0*p0x+p1x)/3.0, (2.0*p0y+p1y)/3.0);
        d1=DistUtils.pointToPointMnhtDist(c1x, c1y, (p0x+2.0*p1x)/3.0, (p0y+2.0*p1y)/3.0);
        break;
      case LineDefectFlatnessAlgo.CBSV_DIST:
        d0=DistUtils.pointToPointCbsvDist(c0x, c0y, (2.0*p0x+p1x)/3.0, (2.0*p0y+p1y)/3.0);
        d1=DistUtils.pointToPointCbsvDist(c1x, c1y, (p0x+2.0*p1x)/3.0, (p0y+2.0*p1y)/3.0);
        break;
      default:
        d0=DistUtils.pointToPointEucDist(c0x, c0y, (2.0*p0x+p1x)/3.0, (2.0*p0y+p1y)/3.0);
        d1=DistUtils.pointToPointEucDist(c1x, c1y, (p0x+2.0*p1x)/3.0, (p0y+2.0*p1y)/3.0);
        break;
    }
    if(this.usingSum) {
      d0+=d1;
    }
    else if(d1>d0){
      d0=d1;
    }
    return d0;
  }

  /**
   * Computes the squared flatness of the cubic using the 
   * {@linkplain LineDefectFlatnessAlgo#distType distance type} and 
   * {@linkplain #usingSum sum/max} strategy used in 
   * {@linkplain #LineDefectFlatnessAlgo(int, boolean) initialisation}.
   * The flatness (actually the defect of the flatness - the higher, the worse) 
   * is computed based on the distance between the control points of the provided
   * curve and the "ideal" positions (at <tt>1/3</tt> and <tt>2/3</tt>
   * on the segment defined by the control points).
   */
  final public double getSquaredFlatness(CubicCurve2D curve) {
    double p0x=curve.getX1(), p0y=curve.getY1();
    double c0x=curve.getCtrlX1(), c0y=curve.getCtrlY1();
    double c1x=curve.getCtrlX2(), c1y=curve.getCtrlY2();
    double p1x=curve.getX2(), p1y=curve.getY2();
    double d0=0, d1=0;
    switch(this.distType) {
      case LineDefectFlatnessAlgo.MNHT_DIST:
        d0=DistUtils.pointToPointMnhtDist(c0x, c0y, (2.0*p0x+p1x)/3.0, (2.0*p0y+p1y)/3.0);
        d1=DistUtils.pointToPointMnhtDist(c1x, c1y, (p0x+2.0*p1x)/3.0, (p0y+2.0*p1y)/3.0);
        d0*=d0;
        d1*=d1;
        break;
      case LineDefectFlatnessAlgo.CBSV_DIST:
        d0=DistUtils.pointToPointCbsvDist(c0x, c0y, (2.0*p0x+p1x)/3.0, (2.0*p0y+p1y)/3.0);
        d1=DistUtils.pointToPointCbsvDist(c1x, c1y, (p0x+2.0*p1x)/3.0, (p0y+2.0*p1y)/3.0);
        d0*=d0;
        d1*=d1;
        break;
      default:
        d0=DistUtils.pointToPointSqEucDist(c0x, c0y, (2.0*p0x+p1x)/3.0, (2.0*p0y+p1y)/3.0);
        d1=DistUtils.pointToPointSqEucDist(c1x, c1y, (p0x+2.0*p1x)/3.0, (p0y+2.0*p1y)/3.0);
        break;
    }
    if(this.usingSum) {
      d0+=d1;
    }
    else if(d1>d0){
      d0=d1;
    }
    return d0;
  }

  /**
   * Computes the flatness of the quad curve using the 
   * {@linkplain LineDefectFlatnessAlgo#distType distance type} and 
   * {@linkplain #usingSum sum/max} strategy used in 
   * {@linkplain #LineDefectFlatnessAlgo(int, boolean) initialisation}.
   * The flatness (actually the defect of the flatness - the higher, the worse) 
   * is computed based on the distance between the control point of the provided
   * curve and the "ideal" position (the mid point on the segment defined by the 
   * control points).
   */
  final public double getFlatness(QuadCurve2D curve) {
    double p0x=curve.getX1(), p0y=curve.getY1();
    double cx=curve.getCtrlX(), cy=curve.getCtrlY();
    double p1x=curve.getX2(), p1y=curve.getCtrlY();
    double toRet=0.0;
    switch(this.distType) {
      case LineDefectFlatnessAlgo.MNHT_DIST:
        toRet=DistUtils.pointToPointMnhtDist(cx, cy, (p0x+p1x)/2.0, (p0y+p1y)/2.0);
        break;
      case LineDefectFlatnessAlgo.CBSV_DIST:
        toRet=DistUtils.pointToPointCbsvDist(cx, cy, (p0x+p1x)/2.0, (p0y+p1y)/2.0);
        break;
      default:
        toRet=DistUtils.pointToPointEucDist(cx, cy, (p0x+p1x)/2.0, (p0y+p1y)/2.0);
        break;
    }
    return toRet;
  }

  /**
   * Computes the squared flatness of the quad curve using the 
   * {@linkplain LineDefectFlatnessAlgo#distType distance type} and 
   * {@linkplain #usingSum sum/max} strategy used in 
   * {@linkplain #LineDefectFlatnessAlgo(int, boolean) initialisation}.
   * The flatness (actually the defect of the flatness - the higher, the worse) 
   * is computed based on the distance between the control point of the provided
   * curve and the "ideal" position (the mid point on the segment defined by the 
   * control points).
   */
  final public double getSquaredFlatness(QuadCurve2D curve) {
    double p0x=curve.getX1(), p0y=curve.getY1();
    double cx=curve.getCtrlX(), cy=curve.getCtrlY();
    double p1x=curve.getX2(), p1y=curve.getCtrlY();
    double toRet=0.0;
    switch(this.distType) {
      case LineDefectFlatnessAlgo.MNHT_DIST:
        toRet=DistUtils.pointToPointMnhtDist(cx, cy, (p0x+p1x)/2.0, (p0y+p1y)/2.0);
        toRet*=toRet;
        break;
      case LineDefectFlatnessAlgo.CBSV_DIST:
        toRet=DistUtils.pointToPointCbsvDist(cx, cy, (p0x+p1x)/2.0, (p0y+p1y)/2.0);
        toRet*=toRet;
        break;
      default:
        toRet=DistUtils.pointToPointSqEucDist(cx, cy, (p0x+p1x)/2.0, (p0y+p1y)/2.0);
        break;
    }
    return toRet;
  }
}
