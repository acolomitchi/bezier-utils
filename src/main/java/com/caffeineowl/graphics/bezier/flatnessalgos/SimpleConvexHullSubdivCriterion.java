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
 * A sundivion criterion based on the flatness returned by the
 * {@link SimpleConvexHullFlatness}.
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com)
 */
public class SimpleConvexHullSubdivCriterion
extends SimpleConvexHullFlatness
implements CubicSubdivisionCriterion, QuadSubdivisionCriterion {
  
  protected double sqTol=1e-10;
  
  public SimpleConvexHullSubdivCriterion() {
    this(1.0e-5);
  }
  
  public SimpleConvexHullSubdivCriterion(double tolerance) {
    if(tolerance<0) {
      tolerance=-tolerance;
    }
    if(0.0==tolerance) {
      tolerance=2.0*Math.sqrt(Double.MIN_VALUE);
    }
    this.sqTol=tolerance*tolerance;
  }

  final public boolean shouldSplit(CubicCurve2D curve) {
    double defect=this.getSquaredFlatness(curve);
    return defect>this.sqTol;
  }

  final public boolean shouldSplit(QuadCurve2D curve) {
    double defect=this.getSquaredFlatness(curve);
    return defect>this.sqTol;
  }

}
