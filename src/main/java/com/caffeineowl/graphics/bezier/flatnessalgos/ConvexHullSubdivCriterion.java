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

import com.caffeineowl.graphics.bezier.CubicSubdivisionCriterion;
import com.caffeineowl.graphics.bezier.QuadSubdivisionCriterion;

/**
 * Will require the subdivision of a {@link #shouldSplit(CubicCurve2D) cubic}
 * or a {@link #shouldSplit(QuadCurve2D) quadratic} curve any time
 * the cubic's {@link ConvexHullFlatnessAlgo#getSquaredFlatness(CubicCurve2D) squared flatness} 
 * (or the quad's {@link ConvexHullFlatnessAlgo#getSquaredFlatness(QuadCurve2D)} squared flatness})
 * is greater than a provided tolerance.
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com)
 */
public class ConvexHullSubdivCriterion
extends ConvexHullFlatnessAlgo
implements QuadSubdivisionCriterion, CubicSubdivisionCriterion {
  /**
   * The minimum tolerance allowed by this algo.
   * Currently, <code>1.25*Math.sqrt(Double.MIN_VALUE)</code>
   * (as the algorithm prefers to work with squared distances).
   */
  static public double minTolerance=1.25*Math.sqrt(Double.MIN_VALUE);
  /**
   * The squared value of the provided tolerance.
   */
  protected double sqTol=1e-10;
  
  /**
   * {@link #ConvexHullSubdivCriterion(double) Initialises} an instance with a
   * default tolerance of <code>1.0e-5</code>.
   */
  public ConvexHullSubdivCriterion() {
    this(1.0e-5);
  }
  
  /**
   * Initialises the instance with the provided tolerance.
   * @param tolerance the tolerance that governs when the
   * the algorithm will require another subdivision.
   * Note that if the 
   */
  public ConvexHullSubdivCriterion(double tolerance) {
    if(tolerance<0) {
      tolerance=-tolerance;
    }
    if(0.0==tolerance) {
      tolerance=2.0*Math.sqrt(Double.MIN_VALUE);
    }
    if(tolerance<ConvexHullSubdivCriterion.minTolerance) {
      tolerance=ConvexHullSubdivCriterion.minTolerance;
    }
    this.sqTol=tolerance*tolerance;
  }

  /**
   * Returns <code>true</code> whenever the 
   * {@link ConvexHullFlatnessAlgo#getSquaredFlatness(CubicCurve2D) defect}
   * is greater than the provided tolerance. The method performs the
   * comparison on the squared value.
   */
  final public boolean shouldSplit(CubicCurve2D curve) {
    double defect=this.getSquaredFlatness(curve);
    return defect>this.sqTol;
  }

  /**
   * Returns <code>true</code> whenever the 
   * {@link ConvexHullFlatnessAlgo#getSquaredFlatness(QuadCurve2D) defect}
   * is greater than the provided tolerance. The method performs the
   * comparison on the squared value.
   */
  final public boolean shouldSplit(QuadCurve2D curve) {
    double defect=this.getSquaredFlatness(curve);
    return defect>this.sqTol;
  }
}
