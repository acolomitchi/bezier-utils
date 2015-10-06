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

import java.awt.geom.QuadCurve2D;

import com.caffeineowl.graphics.bezier.QuadFlatnessAlgorithm;
import com.caffeineowl.graphics.bezier.QuadSubdivisionCriterion;

/**
 * A {@link QuadSubdivisionCriterion} which does its job by using a
 * provided {@link QuadFlatnessAlgorithm} and a tolerance:
 * the criterion will ask for yet one subdivision whenever 
 * algorithm returns a flatness value that's greater than
 * the required tolerance.
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com) 
 */
public class GenericQuadSubdivCriterion
implements QuadSubdivisionCriterion {
  private static final double defaultTol=1.0e-5;
  private static final QuadFlatnessAlgorithm defaultFlatness=
    new ConvexHullFlatnessAlgo();
  
  /**
   * The flatness algorithm.
   */
  protected QuadFlatnessAlgorithm flatnessAlgo=null;
  /**
   * The tolerance.
   */
  protected double tol;
  /**
   * Squared tolerance, for the {@link QuadFlatnessAlgorithm}
   * that {@link QuadFlatnessAlgorithm#isSquaredFlatenessPreferred() prefers}
   * working with squared distances.
   */
  protected double sqTol;
  
  /**
   * {@link #GenericQuadSubdivCriterion(QuadFlatnessAlgorithm, double) Initialises}
   * a new instance with a {@link ConvexHullFlatnessAlgo} as the flatness algorithm
   * and a tolerance of <code>1.0e-5</code>.
   */
  public GenericQuadSubdivCriterion() {
    this(
      GenericQuadSubdivCriterion.defaultFlatness, 
      GenericQuadSubdivCriterion.defaultTol
    );
  }

  /**
   * {@link #GenericQuadSubdivCriterion(QuadFlatnessAlgorithm, double) Initialises}
   * a new instance with a {@link ConvexHullFlatnessAlgo} as the flatness algorithm
   * and the provided tolerance.
   */
  public GenericQuadSubdivCriterion(double tolerance) {
    this(GenericQuadSubdivCriterion.defaultFlatness, tolerance);
  }

  /**
   * {@link #GenericQuadSubdivCriterion(QuadFlatnessAlgorithm, double) Initialises}
   * a new instance the provided flatness algo
   * and a tolerance of <code>1.0e-5</code>.
   */
  public GenericQuadSubdivCriterion(QuadFlatnessAlgorithm algo) {
    this(algo, GenericQuadSubdivCriterion.defaultTol);
  }

  /**
   * {@link #GenericQuadSubdivCriterion(QuadFlatnessAlgorithm, double) Initialises}
   * using the provided parameters.
   */
  public GenericQuadSubdivCriterion(QuadFlatnessAlgorithm algo, double tolerance) {
    if(null==algo) {
      throw new NullPointerException();
    }
    this.flatnessAlgo=algo;
    if(0.0==tolerance) {
      tolerance=2.0*Math.sqrt(Double.MIN_VALUE);
    }
    this.tol=tolerance;
    this.sqTol=tolerance*tolerance;
  }

  /**
   * Returns <code>true</code> whenever the {@link #flatnessAlgo}
   * returns a value greater than the required tolerance.
   * If the flatness algorithm {@link QuadFlatnessAlgorithm#isSquaredFlatenessPreferred() prefers}
   * the computation of squared distances, the comparison is
   * made against the {@link #sqTol squared tolerance}.
   */
  public boolean shouldSplit(QuadCurve2D curve) {
    if(this.flatnessAlgo.isSquaredFlatenessPreferred()) {
      return this.flatnessAlgo.getSquaredFlatness(curve)>this.sqTol;
    }
    return this.flatnessAlgo.getFlatness(curve)>this.tol;
  }

}
