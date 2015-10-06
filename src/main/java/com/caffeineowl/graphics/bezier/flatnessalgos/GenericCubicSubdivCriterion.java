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

import com.caffeineowl.graphics.bezier.CubicFlatnessAlgorithm;
import com.caffeineowl.graphics.bezier.CubicSubdivisionCriterion;

/**
 * A {@link CubicSubdivisionCriterion} which does its job by using a
 * provided {@link CubicFlatnessAlgorithm} and a tolerance:
 * the criterion will ask for yet one subdivision whenever 
 * algorithm returns a flatness value that's greater than
 * the required tolerance.
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com) 
 */
public class GenericCubicSubdivCriterion 
implements CubicSubdivisionCriterion {

  private static final double defaultTol=1.0e-5;
  private static final CubicFlatnessAlgorithm defaultFlatness=
    new ConvexHullFlatnessAlgo();
  
  /**
   * The flatness algorithm.
   */
  protected CubicFlatnessAlgorithm flatnessAlgo=null;
  /**
   * The tolerance.
   */
  protected double tol;
  /**
   * Squared tolerance, for the {@link CubicFlatnessAlgorithm}
   * that {@link CubicFlatnessAlgorithm#isSquaredFlatenessPreferred() prefers}
   * working with squared distances.
   */
  protected double sqTol;
  
  /**
   * {@link #GenericCubicSubdivCriterion(CubicFlatnessAlgorithm, double) Initalises}
   * a new instance with a {@link ConvexHullFlatnessAlgo} as the flatness algorithm
   * and a tolerance of <code>1.0e-5</code>.
   */
  public GenericCubicSubdivCriterion() {
    this(
      GenericCubicSubdivCriterion.defaultFlatness, 
      GenericCubicSubdivCriterion.defaultTol
    );
  }

  /**
   * {@link #GenericCubicSubdivCriterion(CubicFlatnessAlgorithm, double) Initialises}
   * a new instance with a {@link ConvexHullFlatnessAlgo} as the flatness algorithm
   * and the provided tolerance.
   */
  public GenericCubicSubdivCriterion(double tolerance) {
    this(GenericCubicSubdivCriterion.defaultFlatness, tolerance);
  }
  
  /**
   * {@link #GenericCubicSubdivCriterion(CubicFlatnessAlgorithm, double) Initialises}
   * a new instance the provided flatness algo
   * and a tolerance of <code>1.0e-5</code>.
   */
  public GenericCubicSubdivCriterion(CubicFlatnessAlgorithm algo) {
    this(algo, GenericCubicSubdivCriterion.defaultTol);
  }
  
  /**
   * {@link #GenericCubicSubdivCriterion(CubicFlatnessAlgorithm, double) Initialises}
   * using the provided parameters.
   */
  public GenericCubicSubdivCriterion(CubicFlatnessAlgorithm algo, double tolerance) {
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
   * If the flatness algorithm {@link CubicFlatnessAlgorithm#isSquaredFlatenessPreferred() prefers}
   * the computation of squared distances, the comparison is
   * made against the {@link #sqTol squared tolerance}.
   */
  public boolean shouldSplit(CubicCurve2D curve) {
    if(this.flatnessAlgo.isSquaredFlatenessPreferred()) {
      return this.flatnessAlgo.getSquaredFlatness(curve)>this.sqTol;
    }
    return this.flatnessAlgo.getFlatness(curve)>this.tol;
  }

}
