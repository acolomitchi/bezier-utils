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

package com.caffeineowl.graphics.bezier;

import java.awt.geom.CubicCurve2D;

/**
 * Interface to be adopted by classes able to compute the flatness
 * of cubic Bezier. Flatness: how well a cubic curve is approximated
 * by a straight segment.
 * <p>For some of the algorithms will be easier (less
 * computational expensive) to compute the square of the flatness,
 * for others the unsquared flatness will be preferred: the consumer of
 * a <code>CubicFlatnessAlgorithm</code>'s services can obtain which
 * way is the preferred one by calling the {@link #isSquaredFlatenessPreferred()}
 * method.
 * <p>Also, a flatness computation algorithm may or may not be robust in
 * concerning degenerate cubic Bezier curves (i.e. curves with all the
 * points collinear): such cases may show a <code>0</code> flatness without
 * any warranty that, indeed, the curve can be approximated by the segment
 * connecting the anchor points. Whether this is the case or not can be
 * obtained by calling the {@link #isDegenerationRobust()} method.
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com)
 */
public interface CubicFlatnessAlgorithm {
  
  /**
   * Must return <code>true</code> if the flatness is non-zero for any
   * cubic curve that is not equal with the segment connecting the
   * cubic's anchors (can handle degenerated cubic curves), <code>false</code>
   * otherwise.
   */
  public boolean isDegenerationRobust();
  
  /**
   * Should return <code>true</code> if the squared flatness is computed
   * easier (i.e. less CPU expensive) than the flatness.
   */
  public boolean isSquaredFlatenessPreferred();
  
  /**
   * Should return the value of the flatness for the provided curve.
   */
  public double getFlatness(CubicCurve2D curve);

  /**
   * Should return the value of the flatness for the provided curve.
   */
  public double getSquaredFlatness(CubicCurve2D curve);
  
}
