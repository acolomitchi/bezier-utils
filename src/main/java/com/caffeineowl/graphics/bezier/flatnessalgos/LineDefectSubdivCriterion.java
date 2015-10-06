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
 * the cubic's {@link LineDefectFlatnessAlgo#getFlatness(CubicCurve2D) flatness} 
 * (or the quad's {@link LineDefectFlatnessAlgo#getFlatness(QuadCurve2D) flatness})
 * is greater than a provided tolerance.
 * <p>Actually, because the {@link LineDefectFlatnessAlgo} may 
 * {@linkplain LineDefectFlatnessAlgo#isSquaredFlatenessPreferred() prefer}
 * either the flatness or the squared flatness, the decision to subdivide
 * is {@linkplain LineDefectFlatnessAlgo#isSquaredFlatenessPreferred() taken} based
 * on whatever is less expensive to compute, thus either the {@linkplain #tol precision}
 * or the {@linkplain #sqTol squared precision} will be used.
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com)
 */
public class LineDefectSubdivCriterion 
extends LineDefectFlatnessAlgo
implements CubicSubdivisionCriterion, QuadSubdivisionCriterion {
  
  static protected final double defaultTol=1.0e-5;
  
  /**
   * The precision to use in deciding whether or not the curve should be split.
   */
  protected double tol;
  /**
   * The squared precision to use in deciding whether or not the curve should be split.
   */
  protected double sqTol;
  
  /**
   * {@linkplain #LineDefectSubdivCriterion(int, boolean, double) Initialises}
   * this instance to use {@linkplain LineDefectFlatnessAlgo#EUCL_DIST euclidian distance},
   * positively make use of {@linkplain LineDefectFlatnessAlgo#usingSum summation}
   * for computing the defect of cubic curves and the {@linkplain #defaultTol default}
   * precision.
   */
  public LineDefectSubdivCriterion() {
    this(LineDefectFlatnessAlgo.EUCL_DIST, true, LineDefectSubdivCriterion.defaultTol);
  }
  
  /**
   * {@linkplain #LineDefectSubdivCriterion(int, boolean, double) Initialises}
   * this instance to use the specified {@linkplain LineDefectFlatnessAlgo#distType distance type},
   * positively make use of {@linkplain LineDefectFlatnessAlgo#usingSum summation}
   * (when computing the defect of cubic curves) and the {@linkplain #defaultTol default}
   * precision.
   */
  public LineDefectSubdivCriterion(int distType) {
    this(distType, true, LineDefectSubdivCriterion.defaultTol);
  }
  
  /**
   * {@linkplain #LineDefectSubdivCriterion(int, boolean, double) Initialises}
   * this instance to use {@linkplain LineDefectFlatnessAlgo#EUCL_DIST euclidian distance},
   * the specified {@linkplain LineDefectFlatnessAlgo#usingSum sum/max} strategy
   * (when computing the defect of cubic curves) and the {@linkplain #defaultTol default}
   * precision.
   */
  public LineDefectSubdivCriterion(boolean useSum) {
    this(LineDefectFlatnessAlgo.EUCL_DIST, useSum, LineDefectSubdivCriterion.defaultTol);
  }

  /**
   * {@linkplain #LineDefectSubdivCriterion(int, boolean, double) Initialises}
   * this instance to use {@linkplain LineDefectFlatnessAlgo#EUCL_DIST euclidian distance},
   * positively make use of {@linkplain LineDefectFlatnessAlgo#usingSum summation}
   * for computing the defect of cubic curves and the specified
   * precision.
   */
  public LineDefectSubdivCriterion(double tolerance) {
    this(LineDefectFlatnessAlgo.EUCL_DIST, true, tolerance);
  }
  
  /**
   * {@linkplain #LineDefectSubdivCriterion(int, boolean, double) Initialises}
   * this instance to use specified {@linkplain LineDefectFlatnessAlgo#distType distance type},
   * and {@linkplain LineDefectFlatnessAlgo#usingSum sum/max} strategy
   * for computing the defect of cubic curves and the {@linkplain #defaultTol default}
   * precision.
   */
  public LineDefectSubdivCriterion(int distType, boolean useSum) {
    this(distType, useSum, LineDefectSubdivCriterion.defaultTol);
  }
  
  /**
   * {@linkplain #LineDefectSubdivCriterion(int, boolean, double) Initialises}
   * this instance to use the specified {@linkplain LineDefectFlatnessAlgo#distType distance type },
   * positively make use of {@linkplain LineDefectFlatnessAlgo#usingSum summation}
   * for computing the defect of cubic curves and the specified precision.
   * precision.
   */
  public LineDefectSubdivCriterion(int distType, double tolerance) {
    this(distType, true, tolerance);
  }

  /**
   * {@linkplain #LineDefectSubdivCriterion(int, boolean, double) Initialises}
   * this instance to use {@linkplain LineDefectFlatnessAlgo#EUCL_DIST euclidian distance},
   * make use of the specified {@linkplain LineDefectFlatnessAlgo#usingSum sum/max} strategy
   * for computing the defect of cubic curves and the specified 
   * precision.
   */
  public LineDefectSubdivCriterion(boolean useSum, double tolerance) {
    this(LineDefectFlatnessAlgo.EUCL_DIST, useSum, tolerance);
  }

  /**
   * {@linkplain #LineDefectSubdivCriterion(int, boolean, double) Initialises}
   * this instance to use the specified values for {@linkplain LineDefectFlatnessAlgo#distType distance type},
   * {@linkplain LineDefectFlatnessAlgo#usingSum sum/max} strategy (when computing the 
   * defect of cubic curves) and precision.
   */
  public LineDefectSubdivCriterion(int distType, boolean useSum, double tolerance) {
    super(distType, useSum);
    if(tolerance<0) {
      tolerance=-tolerance;
    }
    if(0.0==tolerance) {
      tolerance=2.0*Math.sqrt(Double.MIN_VALUE);
    }
    this.tol=tolerance;
    this.sqTol=tolerance*tolerance;
  }

  /**
   * Returns <code>true</code> whenever the {@link #getFlatness(CubicCurve2D) defect}
   * of approximating the provided cubic by a line segment is higher than the
   * {@linkplain #tol tolerance} for which this instance is initialised with.
   * The method chooses whether or not using the {@linkplain #getFlatness(CubicCurve2D) direct}
   * or {@link #getSquaredFlatness(CubicCurve2D) squared} defect is 
   * {@linkplain #isSquaredFlatenessPreferred() less expensive} to compute and reacts accordingly.
   */
  final public boolean shouldSplit(CubicCurve2D curve) {
    double defect;
    boolean toRet=false;
    if(this.isSquaredFlatenessPreferred()) {
      defect=this.getSquaredFlatness(curve);
      toRet=(defect > this.sqTol);
    }
    else {
      defect=this.getFlatness(curve);
      toRet=(defect > this.tol);
    }
    return toRet;
  }

  /**
   * Returns <code>true</code> whenever the {@link #getFlatness(QuadCurve2D) defect}
   * of approximating the provided quad by a line segment is higher than the
   * {@linkplain #tol tolerance} for which this instance is initialised with.
   * The method chooses whether or not using the {@linkplain #getFlatness(QuadCurve2D) direct}
   * or {@link #getSquaredFlatness(QuadCurve2D) squared} defect is 
   * {@linkplain #isSquaredFlatenessPreferred() less expensive} to compute and 
   * reacts accordingly.
   */
  final public boolean shouldSplit(QuadCurve2D curve) {
    double defect;
    boolean toRet=false;
    if(this.isSquaredFlatenessPreferred()) {
      defect=this.getSquaredFlatness(curve);
      toRet=(defect > this.sqTol);
    }
    else {
      defect=this.getFlatness(curve);
      toRet=(defect > this.tol);
    }
    return toRet;
  }

}
