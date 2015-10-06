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
 * Computes the flatness based on the maximum distance between any of the control
 * points of a cubic Bezier ({@link #getFlatness(CubicCurve2D)} or quadratic
 * Bezier ({@link #getFlatness(QuadCurve2D)} and the line defined by the
 * anchor points of the Bezier curve.<p>
 * Some notes:<ul>
 * <li> the algorithm is based on {@link com.caffeineowl.graphics.DistUtils#pointToLineSqEucDist(double, double, double, double, double, double)
 * DistUtils.pointToLineSqEucDist(...)}: therefore using it to compute 
 * the {@link #getSquaredFlatness(CubicCurve2D) squared}
 * {@link #getFlatness(CubicCurve2D) flatness} is {@link #isSquaredFlatenessPreferred() preferred};
 * <li> the algorithm is <b>not</b> robust in concerning the degenerated curves:
 * for instance any Bezier curve with all the control points on the same line as
 * the anchor points will report a <code>0</code> flatness, even the curves cannot
 * by represented by the segment connecting the anchor points (think of control points
 * residing <b>outside</b> the segment defined by the anchor points).
 * </ul>
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com)
 */
public class SimpleConvexHullFlatness
  implements CubicFlatnessAlgorithm, QuadFlatnessAlgorithm {

  /**
   * Returns <code>false</code>
   */
  final public boolean isDegenerationRobust() {
    return false;
  }

  final public boolean isSquaredFlatenessPreferred() {
    return true;
  }

  final public double getFlatness(CubicCurve2D curve) {
    return Math.sqrt(this.getSquaredFlatness(curve));
  }

  final public double getSquaredFlatness(CubicCurve2D curve) {
    double x1=curve.getX1(), y1=curve.getY1();
    double x2=curve.getX2(), y2=curve.getY2();
    double sqD1=DistUtils.pointToLineSqEucDist(curve.getCtrlX1(), curve.getCtrlY1(), x1, y1, x2, y2);
    double sqD2=DistUtils.pointToLineSqEucDist(curve.getCtrlX2(), curve.getCtrlY2(), x1, y1, x2, y2);
    return Math.max(sqD1, sqD2);
  }

  final public double getFlatness(QuadCurve2D curve) {
    return Math.sqrt(this.getSquaredFlatness(curve));
  }

  final public double getSquaredFlatness(QuadCurve2D curve) {
    return DistUtils.pointToLineSqEucDist(
      curve.getCtrlX(), curve.getCtrlY(),
      curve.getX1(), curve.getY1(), 
      curve.getX2(), curve.getY2()
    );
  }

}
