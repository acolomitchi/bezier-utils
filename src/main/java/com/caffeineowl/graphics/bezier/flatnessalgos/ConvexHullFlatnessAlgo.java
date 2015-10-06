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

/**
 * Computes the flatness of a <code>QuadCurve2D</code> and/or
 * <code>CubicCurve2D</code> based on the (maximum) distance
 * between the anchor point(s) and the segment defined by the
 * anchor points. The method used 
 * {@link DistUtils#pointToSegSqEucDist pointToSegSqEucDist(...)}
 * therefore:<ul>
 * <li>the algorithm {@link #isSquaredFlatenessPreferred() do prefers}
 * to compute the square flatness.
 * <li>is {@link #isDegenerationRobust() degeneration} robust
 * <ul>
 * @see SimpleConvexHullFlatness
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com)
 */
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;

import com.caffeineowl.graphics.DistUtils;
import com.caffeineowl.graphics.bezier.CubicFlatnessAlgorithm;
import com.caffeineowl.graphics.bezier.QuadFlatnessAlgorithm;

public class ConvexHullFlatnessAlgo
  implements QuadFlatnessAlgorithm, CubicFlatnessAlgorithm {

  /**
   * Always return <code>true</code>., and that's because the 
   * {@link DistUtils#pointToSegSqEucDist(double, double, double, double, double, double)}
   * method is used, so if any of the anchor points is on the line defined by the 
   * anchor points but falls outside the segment, then the method will return a
   * non-zero distance for it.
   */
  public final boolean isDegenerationRobust() {
    return true;
  }

  /**
   * Always return <code>true</code>.
   */
  public final boolean isSquaredFlatenessPreferred() {
    return true;
  }

  /**
   * Returns the square root of the {@link #getSquaredFlatness(QuadCurve2D)}
   * (so use {@link #getSquaredFlatness(QuadCurve2D)} if possible instead
   * of this one, it will be less computational intensive).
   */
  public final double getFlatness(QuadCurve2D curve) {
    return Math.sqrt(this.getSquaredFlatness(curve));
  }

  /**
   * Returns the {@link DistUtils#pointToPointSqEucDist(double, double, double, double) 
   * square distance} between the control point of the curve and
   * the segment defined by its anchor points.
   * 
   */
  public final double getSquaredFlatness(QuadCurve2D curve) {
    return DistUtils.pointToSegSqEucDist(
      curve.getCtrlX(), curve.getCtrlY(), 
      curve.getX1(), curve.getY1(), 
      curve.getX2(), curve.getY2()
    );
  }

  /**
   * Returns the square root of the {@link #getSquaredFlatness(CubicCurve2D)}
   * (so use {@link #getSquaredFlatness(CubicCurve2D)} if possible instead
   * of this one, it will be less computational intensive).
   */
  public final double getFlatness(CubicCurve2D curve) {
    return Math.sqrt(this.getSquaredFlatness(curve));
  }

  /**
   * Returns the maximum {@link DistUtils#pointToPointSqEucDist(double, double, double, double)
   * squared distance} between the two control points and the
   * segment defined by the curve's anchor points.
   */
  public final double getSquaredFlatness(CubicCurve2D curve) {
    double sx=curve.getX1(), sy=curve.getY1();
    double ex=curve.getX2(), ey=curve.getY2();
    double sqDist1=DistUtils.pointToSegSqEucDist(
      curve.getCtrlX1(), curve.getCtrlY1(), 
      sx, sy, ex, ey
    );
    double sqDist2=DistUtils.pointToSegSqEucDist(
      curve.getCtrlX2(), curve.getCtrlY2(), 
      sx, sy, ex, ey
    );
    return sqDist1>sqDist2 ? sqDist1 : sqDist2;
  }

}
