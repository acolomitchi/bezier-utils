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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Arrays;

import com.caffeineowl.graphics.bezier.flatnessalgos.ConvexHullSubdivCriterion;

/**
 * Utility functions for processing B&eacute;zier curves.
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com)
 */
public class BezierUtils {
  
  static public final double minPrecision=Math.sqrt(Double.MIN_VALUE);

  static private ConvexHullSubdivCriterion defaultSubdivCriterion=
    new ConvexHullSubdivCriterion();

  static private final CubicSubdivisionCriterion defaultCubicSubdivCriterion=
    BezierUtils.defaultSubdivCriterion;
  
  static private final QuadSubdivisionCriterion defaultQuadSubdivCriterion=
    BezierUtils.defaultSubdivCriterion;
  
  
  /**
   * Subdivides a cubic B&eacute;zier at a value for the curve's parameter
   * of <code>1/2</code>. The method will return the resulted cubic segments
   * in the two output parameters (<code>first</code> and <code>second</code>).
   * It is safe for whichever of them to be the same as the provided cubic,
   * the computation will be correct, however the original cubic will be
   * overwritten. The method will not check if the both of the
   * result cubic segments point to the same instance:
   * in this case the last to be computed (the <code>second</code>) 
   * will overwrite the previous.<br>
   * The method uses de Casteljau algorithm.
   * @param cubic the cubic B&eacute;zier to split
   * @param first the place where the first half cubic that results
   *              upon subdivision is stored. If <code>null</code>,
   *              the method will interpret this as "don't care about first
   *              segment" and will not compute it.
   * @param second the place where the second half cubic that results
   *              upon subdivision is stored. If <code>null</code>,
   *              the method will interpret this as "don't care about second
   *              segment" and will not compute it.
   * @return <code>true</code> if the computation was performed and finished
   *   successfuly, <code>false</code> otherwise (i.e a <code>null</code> value
   *   for the <code>cubic</code> parameter).
   */
  static public boolean halfSplitCurve(CubicCurve2D cubic, CubicCurve2D first, CubicCurve2D second) {
    boolean toRet=null!=cubic;
    if(toRet && ((null!=first) || (null!=second))) {
      double x0=cubic.getX1();
      double x1=cubic.getX2();
      double cx0=cubic.getCtrlX1();
      double cx1=cubic.getCtrlX2();
      double y0=cubic.getY1();
      double y1=cubic.getY2();
      double cy0=cubic.getCtrlY1();
      double cy1=cubic.getCtrlY2();

      double p0x=(x0+cx0)/2.0;
      double p0y=(y0+cy0)/2.0;
      double p1x=(cx0+cx1)/2.0;
      double p1y=(cy0+cy1)/2.0;
      double p2x=(cx1+x1)/2.0;
      double p2y=(cy1+y1)/2.0;

      double p01x=(p0x+p1x)/2.0;
      double p01y=(p0y+p1y)/2.0;
      double p12x=(p1x+p2x)/2.0;
      double p12y=(p1y+p2y)/2.0;

      double dpx=(p01x+p12x)/2.0;
      double dpy=(p01y+p12y)/2.0;

      if(null!=first) {
        first.setCurve(x0, y0, p0x, p0y, p01x, p01y, dpx, dpy);
      }

      if(null!=second) {
        second.setCurve(dpx, dpy, p12x, p12y, p2x, p2y, x1, y1);
      }
    }
    return toRet;
  }
  
  /**
   * Subdivides a quadratic B&eacute;zier at a value for the curve's parameter
   * of <code>1/2</code>. The method will return the resulted quadratic B&eacute;zier segments
   * in the two output parameters (<code>first</code> and <code>second</code>).
   * It is safe for whichever of them to be the same as the provided quadratic,
   * the computation will be correct, however the original quadratic will be
   * overwritten. The method will not check if the both of the
   * result quadratic curves point to the same instance:
   * in this case the last to be computed (the <code>second</code>) 
   * will overwrite the previous.<br>
   * The method uses de Casteljau algorithm.
   * @param quad the cubic B&eacute;zier to split
   * @param first the place where the first quadratic half that results
   *              upon subdivision is stored. If <code>null</code>,
   *              the method will interpret this as "don't care about first
   *              segment" and will not compute it.
   * @param second the place where the second quadratic half that results
   *              upon subdivision is stored. If <code>null</code>,
   *              the method will interpret this as "don't care about second
   *              segment" and will not compute it.
   * @return <code>true</code> if the computation was performed and finished
   *   Successfully, <code>false</code> otherwise (i.e a <code>null</code> value
   *   for the <code>quad</code> parameter).
   */
  static public boolean halfSplitCurve(QuadCurve2D quad, QuadCurve2D first, QuadCurve2D second) {
    boolean toRet=null!=quad;
    if(toRet && ((null!=first) || (null!=second))) {
      double sx0=quad.getX1();
      double sx1=quad.getX2();
      double scx=quad.getCtrlX();
      double scy=quad.getCtrlY();
      double sy0=quad.getY1();
      double sy1=quad.getY2();

      double p0x=(sx0+scx)/2.0;
      double p0y=(sy0+scy)/2.0;
      double p1x=(scx+sx1)/2.0;
      double p1y=(scy+sy1)/2.0;

      double dpx=(p0x+p1x)/2.0;
      double dpy=(p0y+p1y)/2.0;

      if(null!=first) {
        first.setCurve(sx0, sy0, p0x, p0y, dpx, dpy);
      }

      if(null!=second) {
        second.setCurve(dpx, dpy, p1x, p1y, sx1, sy1);
      }
    }
    return toRet;
  }
  
  /**
   * Subdivides a cubic B&eacute;zier at a given value for the curve's parameter.
   * The method will return the resulted cubic segments
   * in the two output parameters (<code>first</code> and <code>second</code>).
   * It is safe for whichever of them to be the same as the provided cubic,
   * the computation will be correct, however the original cubic will be
   * overwritten. The method will not check if the both of the
   * result cubic segments point to the same instance:
   * in this case the last to be computed (the <code>second</code>) 
   * will overwrite the previous.<br>
   * The method uses de Casteljau algorithm.
   * @param cubic the cubic B&eacute;zier to split
   * @param tSplit the value for parameter where the split should occur.
   *               If out of the 0..1 range, the function does nothing
   *               and returns <code>false</code>.
   * @param first the place where the first cubic segment that results
   *              upon subdivision is stored. If <code>null</code>,
   *              the method will interpret this as "don't care about first
   *              segment" and will not compute it.
   * @param second the place where the second cubic segment that results
   *              upon subdivision is stored. If <code>null</code>,
   *              the method will interpret this as "don't care about second
   *              segment" and will not compute it.
   * @return <code>true</code> if the computation was performed and finished
   *   successfully, <code>false</code> otherwise (i.e a <code>null</code> value
   *   for the <code>cubic</code> parameter, or a value for the <code>tSplit</code>
   *   out of the <code>[0..1]</code> range).
   */
  static public boolean splitCurve(CubicCurve2D cubic, double tSplit, CubicCurve2D first, CubicCurve2D second) {
    boolean toRet=tSplit>=0 && tSplit<=1 && null!=cubic;
    if(toRet && ((null!=first) || (null!=second))) {
      double x0=cubic.getX1();
      double x1=cubic.getX2();
      double cx0=cubic.getCtrlX1();
      double cx1=cubic.getCtrlX2();
      double y0=cubic.getY1();
      double y1=cubic.getY2();
      double cy0=cubic.getCtrlY1();
      double cy1=cubic.getCtrlY2();

      double p0x=x0+(tSplit*(cx0-x0));
      double p0y=y0+(tSplit*(cy0-y0));
      double p1x=cx0+(tSplit*(cx1-cx0));
      double p1y=cy0+(tSplit*(cy1-cy0));
      double p2x=cx1+(tSplit*(x1-cx1));
      double p2y=cy1+(tSplit*(y1-cy1));

      double p01x=p0x+(tSplit*(p1x-p0x));
      double p01y=p0y+(tSplit*(p1y-p0y));
      double p12x=p1x+(tSplit*(p2x-p1x));
      double p12y=p1y+(tSplit*(p2y-p1y));

      double dpx=p01x+(tSplit*(p12x-p01x));
      double dpy=p01y+(tSplit*(p12y-p01y));

      if(null!=first) {
        first.setCurve(x0, y0, p0x, p0y, p01x, p01y, dpx, dpy);
      }

      if(null!=second) {
        second.setCurve(dpx, dpy, p12x, p12y, p2x, p2y, x1, y1);
      }
    }
    return toRet;
  }
  
  
  
  /**
   * Subdivides a quadratic B&eacute;zier at a given value for the curve's parameter.
   * The method will return the resulted quadratic B&eacute;zier segments
   * in the two output parameters (<code>first</code> and <code>second</code>).
   * It is safe for whichever of them to be the same as the provided quadratic,
   * the computation will be correct, however the original quadratic will be
   * overwritten. The method will not check if the both of the
   * result quadratic curves point to the same instance:
   * in this case the last to be computed (the <code>second</code>) 
   * will overwrite the previous.<br>
   * The method uses de Casteljau algorithm.
   * @param quad the cubic B&eacute;zier to split
   * @param tSplit the value for parameter where the split should occur.
   *               If out of the 0..1 range, the function does nothing
   *               and returns <code>false</code>.
   * @param first the place where the first quadratic segment that results
   *              upon subdivision is placed. If <code>null</code>,
   *              the method will interpret this as "don't care about first
   *              segment" and will not compute it.
   * @param second the place where the second quadratic segment that results
   *              upon subdivision is placed. If <code>null</code>,
   *              the method will interpret this as "don't care about second
   *              segment" and will not compute it.
   * @return <code>true</code> if the computation was performed and finished
   *   successfully, <code>false</code> otherwise (i.e a <code>null</code> value
   *   for the <code>quad</code> parameter, or a value for the <code>tSplit</code>
   *   out of the <code>[0..1]</code> range).
   */
  static public boolean splitCurve(QuadCurve2D quad, double tSplit, QuadCurve2D first, QuadCurve2D second) {
    boolean toRet=tSplit>=0 && tSplit<=1 && null!=quad;
    if(toRet && ((null!=first) || (null!=second))) {
      double sx0=quad.getX1();
      double sx1=quad.getX2();
      double scx=quad.getCtrlX();
      double scy=quad.getCtrlY();
      double sy0=quad.getY1();
      double sy1=quad.getY2();

      double p0x=sx0+(tSplit*(scx-sx0));
      double p0y=sy0+(tSplit*(scy-sy0));
      double p1x=scx+(tSplit*(sx1-scx));
      double p1y=scy+(tSplit*(sy1-scy));

      double dpx=p0x+(tSplit*(p1x-p0x));
      double dpy=p0y+(tSplit*(p1y-p0y));

      if(null!=first) {
        first.setCurve(sx0, sy0, p0x, p0y, dpx, dpy);
      }

      if(null!=second) {
        second.setCurve(dpx, dpy, p1x, p1y, sx1, sy1);
      }
    }
    return toRet;
  }
  
  /**
   * Subdivides a cubic B&eacute;zier in more than one subdivision points.
   * The method calls repeatedly the 
   * {@link #splitCurve(CubicCurve2D, double, CubicCurve2D, CubicCurve2D) single
   * point split} method.
   * @param curve the curve to subdivide 
   * @param params the array of curve parameters where to split the curve. 
   * Of course, they need to be in the [0..1) range (1.0 excluded) - otherwise
   * unpredictable results might happen (in fact they are quite predictable, by I'm
   * too lazy to check the effect). The method does not check this. Uh, by the
   * way, the params are supposed to be sorted in ascending order: the method
   * calls <code>java.utils.Arrays.sort(double[])</code> to make sure they are
   * (so, if they are not, be warned - the method will have a side effect
   * on the parameters).
   * @param resultsHere array to store the 
   * @return the cubic segments resulted in the splitting process 
   * If the <code>resultHere</code> has enough space to accommodate, then the 
   * first <code>params.length+1</code> are filled in and <code>resultsHere</code>
   * is returned. If the <code>resultHere</code> is not large enough to 
   * contain all the resulted points (or is <code>null</code>), 
   * the array is re-allocated (old content is copied into the newly allocated
   * one), filled in and returned.
   */
  static public CubicCurve2D[] splitCurve(
    CubicCurve2D curve,
    double[] params,
    CubicCurve2D[] resultsHere
  )
  {
    Arrays.sort(params);
  
    CubicCurve2D firstPart=new CubicCurve2D.Double();
    CubicCurve2D remainder=new CubicCurve2D.Double();
    remainder.setCurve(curve);
  
    if(resultsHere==null) {
      resultsHere=new CubicCurve2D[params.length+1];
    } else if(resultsHere.length<=params.length) {
      CubicCurve2D[] newRes=new CubicCurve2D[params.length+1];
      System.arraycopy(resultsHere, 0, newRes, 0, resultsHere.length);
      resultsHere=newRes;
    }
    
    double lastParam=0;
    int i;
  
    for(i=0; (i<params.length) && (params[i]>0.0); i++) {
      BezierUtils.splitCurve(
        remainder,
        (params[i]-lastParam)/(1-lastParam),
        firstPart,
        remainder
      );
  
      if(null==resultsHere[i]) {
        resultsHere[i]=new CubicCurve2D.Double();
      }
  
      resultsHere[i].setCurve(firstPart);
      lastParam=params[i];
    }
  
    if(null==resultsHere[i]) {
      resultsHere[i]=new CubicCurve2D.Double();
    }
  
    resultsHere[i].setCurve(remainder);
  
    return resultsHere;
  }

  /**
   * Subdivides a quadratic B&eacute;zier in more than one subdivision points.
   * The method calls repeatedly the 
   * {@link #splitCurve(QuadCurve2D, double, QuadCurve2D, QuadCurve2D) single
   * point split} method.
   * @param curve the curve to subdivide 
   * @param params the array of curve parameters where to split the curve. 
   * Of course, they need to be in the [0..1) range (1.0 excluded) - otherwise
   * unpredictable results might happen (in fact they are quite predictable, by I'm
   * too lazy to check the effect). The method does not check this. Uh, by the
   * way, the params are supposed to be sorted in ascending order: the method
   * calls <code>java.utils.Arrays.sort(double[])</code> to make sure they are
   * (so, if they are not, be warned - the method will have a side effect
   * on the parameters).
   * @param resultsHere array to store the 
   * @return the quadratic segments resulted in the splitting process 
   * If the <code>resultHere</code> has enough space to accommodate, then the 
   * first <code>params.length+1</code> are filled in and <code>resultsHere</code>
   * is returned. If the <code>resultHere</code> is not large enough to 
   * contain all the resulted points (or is <code>null</code>), 
   * the array is re-allocated (old content is copied into the newly allocated
   * one), filled in and returned.
   */
  static public QuadCurve2D[] splitCurve(
    QuadCurve2D curve,
    double[] params,
    QuadCurve2D[] resultsHere
  )
  {
    Arrays.sort(params);
  
    QuadCurve2D firstPart=new QuadCurve2D.Double();
    QuadCurve2D remainder=new QuadCurve2D.Double();
    remainder.setCurve(curve);
  
    if(resultsHere==null) {
      resultsHere=new QuadCurve2D[params.length+1];
    } else if(resultsHere.length<=params.length) {
      QuadCurve2D[] newRes=new QuadCurve2D[params.length+1];
      System.arraycopy(resultsHere, 0, newRes, 0, resultsHere.length);
      resultsHere=newRes;
    }
  
    double lastParam=0;
    int i;
  
    for(i=0; (i<params.length) && (params[i]>0.0); i++) {
      BezierUtils.splitCurve(
        remainder,
        (params[i]-lastParam)/(1-lastParam),
        firstPart,
        remainder
      );
  
      if(null==resultsHere[i]) {
        resultsHere[i]=new QuadCurve2D.Double();
      }
  
      resultsHere[i].setCurve(firstPart);
      lastParam=params[i];
    }
  
    if(null==resultsHere[i]) {
      resultsHere[i]=new QuadCurve2D.Double();
    }
  
    resultsHere[i].setCurve(remainder);
    return resultsHere;
  }

  /**
   * Computes the location of the point on a cubic B&eacute;zier corresponding to a 
   * given value of the parameter <code>t</code>, and returns the position in
   * an output parameter (if provided). If the provided output parameter is 
   * <code>null</code>, the method will allocate a <code>Point2D</code> 
   * and return it. Otherwise, if successful, the method will return the 
   * provided <code>Point2D</code> instance to be used for storing the result.<br>
   * The method will not restrict the the <code>t</code> 
   * parameter to values between <code>[0..1]</code>, using whichever value is
   * provided.
   * <p>The method uses the parametrical expression of the cubic B&eacute;zier: 
   * this is because it requires <code>20</code> additions/substractions
   * and <code>10</code> multiplications,
   * while the de Casteljau algorithm requires <code>24</code> additions
   * and <code>12</code> multiplications.
   * @param t value for the curve parameter for which to compute the 
   * corresponding position
   * @param curve the cubic curve. If <code>null</code>, the method will
   * return <code>null</code>.
   * @param resultHere the output parameter where the position is to be stored.
   * If <code>null</code>, the method will create a new <code>Point2D</code>
   * instance, use it for storing the requested value and return it.
   * @return the position on the cubic curve corresponding to the provided
   * value of the parameter<code>t</code>, or <code>null</code> if the provided
   * <code>curve</code> is <code>null</code>.
   */
  static public Point2D pointOnCurve(
    double t,
    CubicCurve2D curve,
    Point2D resultHere
  )
  {
    if(null!=curve) {
      double x1=curve.getX1(), y1=curve.getY1();
      double cx1=curve.getCtrlX1(), cy1=curve.getCtrlY1();
      double cx2=curve.getCtrlX2(), cy2=curve.getCtrlY2();
      double x2=curve.getX1(), y2=curve.getY1();
      // Coefficients of the parametric representation of the cubic
      double ax=cx1-x1, ay=cy1-y1;
      double bx=cx2-cx1-ax, by=cy2-cy1-ay;
      double cx=x2-cx2-ax-bx-bx; // instead of ...-ax-2*bx. Does it worth?
      double cy=y2-cy2-ay-by-by;
  
      double x=x1+(t*((3*ax)+(t*((3*bx)+(t*cx)))));
      double y=y1+(t*((3*ay)+(t*((3*by)+(t*cy)))));
  
      if(null==resultHere) {
        resultHere=new Point2D.Double(x, y);
      } else {
        resultHere.setLocation(x, y);
      }
    } else {
      resultHere=null;
    } 
    return resultHere;
  }

  /**
   * Computes the location of the point on a quadratic B&eacute;zier corresponding to a 
   * given value of the parameter <code>t</code>, and returns the position in
   * an output parameter (if provided). If the provided output parameter is 
   * <code>null</code>, the method will allocate a <code>Point2D</code> 
   * and return it. Otherwise, if successful, the method will return the 
   * provided <code>Point2D</code> instance to be used for storing the result.<br>
   * The method will not restrict the the <code>t</code> 
   * parameter to values between <code>[0..1]</code>, using whichever value is
   * provided.
   * <p>Uses the parametric expression of the quadratic B&eacute;zier: 
   * this is because it requires <code>10</code> additions/substractions
   * and <code>6</code> multiplications,
   * while the de Casteljau algorithm requires <code>12</code> additions
   * and <code>6</code> multiplications.
   * @param t value for the curve parameter for which to compute the 
   * corresponding position.
   * @param curve the quadratic curve. If <code>null</code>, the method will
   * return <code>null</code>.
   * @param resultHere the output parameter where the position is to be stored.
   * If <code>null</code>, the method will create a new <code>Point2D</code>
   * instance, use it for storing the requested value and return it.
   * @return the position on the quadratic curve corresponding to the provided
   * value of the parameter<code>t</code>, or <code>null</code> if the provided
   * <code>curve</code> is <code>null</code>.
   */
  static public Point2D pointOnCurve(
    double t,
    QuadCurve2D curve,
    Point2D resultHere
  )
  {
    if(null!=curve) {
      double x1=curve.getX1(), y1=curve.getY1();
      double cx=curve.getCtrlX(), cy=curve.getCtrlY();
      double x2=curve.getX1(), y2=curve.getY1();
      // Coefficients of the parametric representation of the cubic
      double ax=cx-x1, ay=cy-y1;
      double bx=x2-cx-ax, by=y2-cy-ay;
      
      double x=x1+t*(2*ax+t*bx);
      double y=y1+t*(2*ay+t*by);
  
      if(null==resultHere) {
        resultHere=new Point2D.Double(x, y);
      } else {
        resultHere.setLocation(x, y);
      }
    } else {
      resultHere=null;
    } 
    return resultHere;
  }
  
  public static void pointAndTangentOnCurve(
    /*in*/double t, /*in*/CubicCurve2D curve, 
    /*out*/Point2D point, /*out*/Line2D tangent
  ) {
    if(null!=curve && (null!=point || null!=tangent)) {
      if(null==tangent) {
        BezierUtils.pointOnCurve(t, curve, point);
      }
      else {
        double ax0=curve.getX1(), ay0=curve.getY1();
        double ax1=curve.getX1(), ay1=curve.getY2();
        double cx0=curve.getCtrlX1(), cy0=curve.getCtrlY1();
        double cx1=curve.getCtrlX2(), cy1=curve.getCtrlY2();
        
        double ipx0=ax0+(cx0-ax0)*t, ipy0=ay0+(cy0-ay0)*t;
        double ipx1=cx0+(cx1-cx0)*t, ipy1=cy0+(cy1-cy0)*t;
        double ipx2=cx1+(ax1-cx1)*t, ipy2=cy1+(ay1-cy1)*t;
        
        double tx0=ipx0+(ipx1-ipx0)*t, ty0=ipy0+(ipy1-ipy0)*t;
        double tx1=ipx1+(ipx2-ipx1)*t, ty1=ipy1+(ipy2-ipy1)*t;
        tangent.setLine(tx0, ty0, tx1, ty1);
        
        if(null!=point) {
          point.setLocation(tx0+(tx1-tx0)*t, ty0+(ty1-ty0)*t);
        }
      }
    }
  }
  
  public static void pointAndTangentOnCurve(
  /*in*/double t, /*in*/QuadCurve2D curve, 
  /*out*/Point2D point, /*out*/Line2D tangent
  ) {
    if(null!=curve && (null!=point || null!=tangent)) {
      if(null==tangent) {
        BezierUtils.pointOnCurve(t, curve, point);
      }
      else {
        double ax0=curve.getX1(), ay0=curve.getY1();
        double ax1=curve.getX1(), ay1=curve.getY2();
        double cx=curve.getCtrlX(), cy=curve.getCtrlY();
        
        double tx0=ax0+(cx-ax0)*t, ty0=ay0+(cy-ay0)*t;
        double tx1=cx+(ax1-cx)*t, ty1=cy+(ay1-cy)*t;
        tangent.setLine(tx0, ty0, tx1, ty1);
        
        if(null!=point) {
          point.setLocation(tx0+(tx1-tx0)*t, ty0+(ty1-ty0)*t);
        }
      }
    }
  }

  public static void midPointAndTangentOnCurve(
    /*in*/CubicCurve2D curve, 
    /*out*/Point2D point, /*out*/Line2D tangent
  ) {
    if(null!=curve && (null!=point || null!=tangent)) {
      double ax0=curve.getX1(), ay0=curve.getY1();
      double ax1=curve.getX1(), ay1=curve.getY2();
      double cx0=curve.getCtrlX1(), cy0=curve.getCtrlY1();
      double cx1=curve.getCtrlX2(), cy1=curve.getCtrlY2();
      
      double ipx0=(cx0+ax0)*0.5, ipy0=(cy0+ay0)*0.5;
      double ipx1=(cx1+cx0)*0.5, ipy1=(cy1+cy0)*0.5;
      double ipx2=(ax1+cx1)*0.5, ipy2=(ay1+cy1)*0.5;
      
      double tx0=(ipx1+ipx0)*0.5, ty0=(ipy1+ipy0)*0.5;
      double tx1=(ipx2+ipx1)*0.5, ty1=(ipy2+ipy1)*0.5;
      if(null!=tangent) {
        tangent.setLine(tx0, ty0, tx1, ty1);
      }
      if(null!=point) {
        point.setLocation((tx1+tx0)*0.5, (ty1+ty0)*0.5);
      }
    }
  }

  public static void minPointAndTangentOnCurve(
  /*in*/QuadCurve2D curve, 
  /*out*/Point2D point, /*out*/Line2D tangent
  ) {
    if(null!=curve && (null!=point || null!=tangent)) {
      double ax0=curve.getX1(), ay0=curve.getY1();
      double ax1=curve.getX1(), ay1=curve.getY2();
      double cx=curve.getCtrlX(), cy=curve.getCtrlY();
      
      double tx0=(cx+ax0)*0.5, ty0=(cy+ay0)*0.5;
      double tx1=(ax1+cx)*0.5, ty1=(ay1-cy)*0.5;
      
      if(null!=tangent) {
        tangent.setLine(tx0, ty0, tx1, ty1);
      }
      
      if(null!=point) {
        point.setLocation((tx1+tx0)*0.5, (ty1+ty0)*0.5);
      }
    }
  }

  /**
   * Computes the parameter values corresponding to the inflexion points of a
   * cubic B&eacute;zier (if any) and returns them within a provided <code>double[]</code>
   * array. To be on a safe side, the array have to be preallocated to a minimum 
   * length of 2 (a cubic B&eacute;zier can have up to <code>2</code> inflexion points).<br>
   * Note that the method will return the values for the parameter
   * only if they fall in the <code>(0, 1)</code> range (excluding the
   * range ends).<br>
   * Note that if the curve has <code>2</code> inflexion points, they will be 
   * placed into the results array in increasing order of their corresponding 
   * curve's parameter values.
   * <p>To obtain the <code>(x, y)</code> location of the inflexion point(s),
   * use the {@link #pointOnCurve(double, CubicCurve2D, Point2D)} method.
   * <p>See also: 
   * <a href="http://www.caffeineowl.com/graphics/2d/vectorial/cubic-inflexion.html">http://www.caffeineowl.com/graphics/2d/vectorial/cubic-inflexion.html</a>
   * @param curve the cubic B&eacute;zier for which the inflexion points are
   * to be computed for. If <code>null</code>, the method will throw a
   * <code>NullPointerException</code>.
   * @param params the array where the values (in the <code>(0, 1)</code> range)
   * for the curve parameter corresponding to the inflexion points are to be 
   * returned. If <code>null</code>, a <code>NullPointerException</code> 
   * will be thrown <i>if the curve has inflexion points</i>. If the length of
   * the array is not enough to accommodate all the values (at most <code>2</code>),
   * an <code>ArrayIndexOutOfBoundsException</code> is thrown.
   * @return the number of the inflexion points that were found.
   */
  static public int computeInflexion(CubicCurve2D curve, double[] params)
  {
    int toRet=0;
    // unpack the coordinates of the points defining the curve
    double p0x=curve.getX1(), p0y=curve.getY1();
    double c0x=curve.getCtrlX1(), c0y=curve.getCtrlY1();
    double c1x=curve.getCtrlX2(), c1y=curve.getCtrlY2();
    double p1x=curve.getX2(), p1y=curve.getY2();
    
    // aux variables involved in computing the numerator
    // in the formula (fraction) of a cubic B&eacute;zier curvature
    // (see the http://www.caffeineowl.com/graphics/2d/vectorial/cubic-inflexion.html)
    double ax=c0x-p0x;
    double ay=c0y-p0y;
    double bx=c1x-c0x-ax;
    double by=c1y-c0y-ay;
    double cx=p1x-c1x-ax-bx-bx;
    double cy=p1y-c1y-ay-by-by;
  
    // coefficients of the polynomial in t that acts as the numerator
    // of the formula (fraction) of a cubic B&eacute;zier curvature.
    // (see the http://www.caffeineowl.com/graphics/2d/vectorial/cubic-inflexion.html)
    double c0=(ax*by)-(ay*bx);
    double c1=(ax*cy)-(ay*cx);
    double c2=(bx*cy)-(by*cx);
  
    if(c2!=0.0) { // quadratic equation
  
      double discr=(c1*c1)-(4*c0*c2);
      c2*=2;
  
      if(discr==0) {
        double root=-c1/c2;
  
        if((root>0) && (root<1)) {
          toRet=1;
          params[0]=root;
        }
      } else if(discr>0) {
        discr=Math.sqrt(discr);
  
        double root=(-c1-discr)/c2;
        if((root>0) && (root<1)) { //collect it only if between [0..1]
          params[toRet++]=root;
        }
        
        root=(-c1+discr)/c2;
        if((root>0) && (root<1)) { //collect it only if between [0..1]
          if(toRet>0 && params[0]>root) {
            params[1]=params[0];
            params[0]=root;
          }
          else {
            params[toRet++]=root;
          }
        }
        
        if(2==toRet && (params[0]>params[1])) {
          double aux=params[0];
          params[0]=params[1];
          params[1]=aux;
        }
      }
    } else if(c1!=0.0) { // linear equation c1*t+c0=0
      double root=-c0/c1;
      if((root>0) && (root<1)) {
        params[toRet++]=root;
      }
    }  
    // else its a totally degenerate curve: a single point
    return toRet;
  }

  /**
   * Method to perform an adaptive halving of a cubic B&eacute;zier, based
   * on a {@link CubicSubdivisionCriterion} (which tells when a cubic
   * is good enough not to need splitting in two anymore).
   * @param curve the curve to be split
   * @param tMin the value of the <tt>t</tt> parameter that corresponds to the
   *    start of the <tt>curve</tt> - as the algo is recursive, after the first
   *    subdivision this may not be <tt>0</tt> (zero) any more (e.g. the
   *    second half of the original curve will start at <tt>t=0.5</tt>)
   * @param tMax tMin the value of the <tt>t</tt> parameter that corresponds to the
   *    end of the <tt>curve</tt> - as the algo is recursive, after the first
   *    subdivision this may not be <tt>1</tt> (one) any more (e.g. the
   *    first half of the original curve will end at <tt>t=0.5</tt>)
   * @param subdivCriterion the subdivision criterion telling when the curve
   *   no longer needs splitting
   * @param segConsumer the consumer of cubic segments that will be "fed"
   *   with the cubic segments resulting from sub-division (may just accumulate 
   *   them perform a processing on them - like transforming them into lie segments).
   */
  static private void adaptiveHalving(
    CubicCurve2D curve, double tMin, double tMax,
    CubicSubdivisionCriterion           subdivCriterion,
    CubicSegmentConsumer                segConsumer
  ) {
    if(subdivCriterion.shouldSplit(curve)) {
      CubicCurve2D firstHalf=new CubicCurve2D.Double();
      CubicCurve2D secondHalf=new CubicCurve2D.Double();
      double tMid=(tMin+tMax)/2.0;
      BezierUtils.halfSplitCurve(curve, firstHalf, secondHalf);
      BezierUtils.adaptiveHalving(firstHalf, tMin, tMid, subdivCriterion, segConsumer);
      BezierUtils.adaptiveHalving(secondHalf, tMid, tMax, subdivCriterion, segConsumer);
    }
    else if(null!=segConsumer){
      segConsumer.processSegment(curve, tMin, tMax);
    }
  }
  
  /**
   * Method to perform an adaptive halving of a cubic B&eacute;zier, based
   * on a {@link CubicSubdivisionCriterion} (which tells when a cubic
   * is good enough not to need splitting in two anymore). The result
   * of the subdivision is guaranteed to make a "daisy-chain" from the
   * original curve (i.e. the end of the prev cubic segment will always 
   * be the start of the new one). The results of the subdivision are fed
   * into a {@link CubicSegmentConsumer} for custom further processing.
   * @param curve the curve to be split
   * @param subdivCriterion the subdivision criterion telling when the curve
   *   no longer needs splitting
   * @param segConsumer the consumer of cubic segments that will be "fed"
   *   with the cubic segments resulting from sub-division (may just accumulate 
   *   them perform a processing on them - like transforming them into lie segments).
   */
  static public void adaptiveHalving(
    CubicCurve2D                        curve,
    CubicSubdivisionCriterion           subdivCriterion,
    CubicSegmentConsumer                segConsumer
  ) {
    if(null==curve) {
      throw new NullPointerException();
    }
    if(null==subdivCriterion) {
      subdivCriterion=BezierUtils.defaultCubicSubdivCriterion;
    }
    BezierUtils.adaptiveHalving(curve, 0.0, 1.0, subdivCriterion, segConsumer);
  }
  
  /**
   * Method to perform an adaptive halving of a cubic B&eacute;zier, based
   * on a {@link QuadSubdivisionCriterion} (which tells when a quad B&eacute;zier
   * is good enough not to need splitting in two anymore).
   * @param curve the curve to be split
   * @param tMin the value of the <tt>t</tt> parameter that corresponds to the
   *    start of the <tt>curve</tt> - as the algo is recursive, after the first
   *    subdivision this may not be <tt>0</tt> (zero) any more (e.g. the
   *    second half of the original curve will start at <tt>t=0.5</tt>)
   * @param tMax tMin the value of the <tt>t</tt> parameter that corresponds to the
   *    end of the <tt>curve</tt> - as the algo is recursive, after the first
   *    subdivision this may not be <tt>1</tt> (one) any more (e.g. the
   *    first half of the original curve will end at <tt>t=0.5</tt>)
   * @param subdivCriterion the subdivision criterion telling when the curve
   *   no longer needs splitting
   * @param segConsumer the consumer of cubic segments that will be "fed"
   *   with the cubic segments resulting from sub-division (may just accumulate 
   *   them perform a processing on them - like transforming them into lie segments).
   */
  static private void adaptiveHalving(
    QuadCurve2D curve, double tMin, double tMax,
    QuadSubdivisionCriterion           subdivCriterion,
    QuadSegmentConsumer               segConsumer
  ) {
    if(subdivCriterion.shouldSplit(curve)) {
      QuadCurve2D firstHalf=new QuadCurve2D.Double();
      QuadCurve2D secondHalf=new QuadCurve2D.Double();
      double tMid=(tMin+tMax)/2.0;
      BezierUtils.halfSplitCurve(curve, firstHalf, secondHalf);
      BezierUtils.adaptiveHalving(firstHalf, tMin, tMid, subdivCriterion, segConsumer);
      BezierUtils.adaptiveHalving(secondHalf, tMid, tMax, subdivCriterion, segConsumer);
    }
    else if(null!=segConsumer){
      segConsumer.processSegment(curve, tMin, tMax);
    }
  }

  /**
   * Method to perform an adaptive halving of a quadratic B&eacute;zier, based
   * on a {@link QuadSubdivisionCriterion} (which tells when a quad B&eacute;zier
   * is good enough not to need splitting in two anymore). The result
   * of the subdivision is guaranteed to make a "daisy-chain" from the
   * original curve (i.e. the end of the prev cubic segment will always 
   * be the start of the new one).The results of the subdivision are fed
   * into a {@link CubicSegmentConsumer} for custom further processing.
   * @param curve the curve to be split
   * @param subdivCriterion the subdivision criterion telling when the curve
   *   no longer needs splitting
   * @param segConsumer the consumer of cubic segments that will be "fed"
   *   with the cubic segments resulting from sub-division (may just accumulate 
   *   them perform a processing on them - like transforming them into lie segments).
   */
  static public void adaptiveHalving(
    QuadCurve2D curve,
    QuadSubdivisionCriterion           subdivCriterion,
    QuadSegmentConsumer               segConsumer
  ) {
    if(null==curve) {
      throw new NullPointerException();
    }
    if(null==subdivCriterion) {
      subdivCriterion=BezierUtils.defaultQuadSubdivCriterion;
    }
    BezierUtils.adaptiveHalving(curve, 0.0, 1.0, subdivCriterion, segConsumer);
  }
  
  /**
   * Performs an adaptive halving of a cubic B&eacute;zier, base on a provided
   * {@link CubicSubdivisionCriterion} (which tells when a cubic B&eacute;zier
   * is good enough not to need splitting in two anymore). The result
   * of the subdivision is returned as an array of cubics, guaranteed to 
   * make a "daisy-chain" from the original curve (i.e. the end of the 
   * prev cubic segment will always  be the start of the new one).<p>
   * The method simply calls into 
   * {@link #adaptiveHalving(CubicCurve2D, CubicSubdivisionCriterion, CubicSegmentConsumer)}
   * using an {@link CubicArrayListConsumer} as an accumulator, returning
   * the accumulated values once the halving ends.
   * @param curve the curve to be subdivided
   * @param subdivCriterion the subdivision criterion
   * @return the result of the halving process as an array of cubic segments
   */
  static public CubicCurve2D[] adaptiveHalving(
    CubicCurve2D curve, CubicSubdivisionCriterion subdivCriterion
  ) {
    CubicArrayListConsumer acumulator=new CubicArrayListConsumer();
    BezierUtils.adaptiveHalving(curve, subdivCriterion, acumulator);
    CubicCurve2D[] toRet=new CubicCurve2D[acumulator.segs.size()];
    acumulator.segs.toArray(toRet);
    return toRet;
  }

  /**
   * Performs an adaptive halving of a quadratic B&eacute;zier, base on a provided
   * {@link QuadSubdivisionCriterion} (which tells when a quad B&eacute;zier
   * is good enough not to need splitting in two anymore). The result
   * of the subdivision is returned as an array of cubics, guaranteed to 
   * make a "daisy-chain" from the original curve (i.e. the end of the 
   * prev cubic segment will always  be the start of the new one).<p>
   * The method simply calls into 
   * {@link #adaptiveHalving(QuadCurve2D, QuadSubdivisionCriterion, QuadSegmentConsumer)}
   * using an {@link QuadArrayListConsumer} as an accumulator, returning
   * the accumulated values once the halving finishes.
   * @param curve the curve to be subdivided
   * @param subdivCriterion the subdivision criterion
   * @return the result of the halving process as an array of quad segments
   */
  static public QuadCurve2D[] adaptiveHalving(
    QuadCurve2D curve, QuadSubdivisionCriterion subdivCriterion
  ) {
    QuadArrayListConsumer acumulator=new QuadArrayListConsumer();
    BezierUtils.adaptiveHalving(curve, subdivCriterion, acumulator);
    QuadCurve2D[] toRet=new QuadCurve2D[acumulator.segs.size()];
    acumulator.segs.toArray(toRet);
    return toRet;
  }
  
  /**
   * The <a href="http://www.caffeineowl.com/graphics/2d/vectorial/cubic2quad01.html#the-algo">adaptive
   * degree reduction</a> algorithm, implemented to return the resulted "daisy-chain" of the approximating
   * quadratic segments as an array. The method calls into 
   * {@link #adaptiveDegreeReduction(CubicCurve2D, double, QuadSegmentConsumer)} with a
   * {@link QuadArrayListConsumer} accumulator, and returns the accumulated segments
   * once the algorithm ends.
   * @param cubic the cubic to be approximated by quad segments
   * @param precision the desired precision of approximation. If drawing the resulted
   *   quad segments, it doesn't make any sense to use less than half of the
   *   resolution of the device used to display it (e.g. if drawing on a screen,
   *   and the <tt>x, y<</tt> coordinates are pixels, use <tt>0.5</tt> as a minimum
   *   for the precision, even if for most of the cases a value of 0.75 or even 1.0 will
   *   be sufficient).
   * @return the resulted quad segments approximating the original curve with the 
   *   specified precision.
   */
  static public QuadCurve2D[] adaptiveDegreeReduction(
    CubicCurve2D cubic, double precision
  ) {
    QuadArrayListConsumer acumulator=new QuadArrayListConsumer();
    BezierUtils.adaptiveDegreeReduction(cubic, precision, acumulator);
    QuadCurve2D[] toRet=new QuadCurve2D[acumulator.segs.size()];
    acumulator.segs.toArray(toRet);
    return toRet;
  }
  
  /**
   * Converts a cubic B&eacute;zier to a quadratic one, using the
   * <a href="http://www.caffeineowl.com/graphics/2d/vectorial/cubic2quad01.html#mid-point-approx">mid-point
   * approximation</a>
   * @param cubic the cubic
   * @param quad the approximating quadratic.
   */
  static final private void applyMidPointApprox(CubicCurve2D cubic, QuadCurve2D quad) {
    double a0x=cubic.getX1(), a0y=cubic.getY1();
    double a1x=cubic.getX2(), a1y=cubic.getY2();
    double p0x=(3*cubic.getCtrlX1()-a0x)/2.0;
    double p0y=(3*cubic.getCtrlY1()-a0y)/2.0;
    double p1x=(3*cubic.getCtrlX2()-a1x)/2.0;
    double p1y=(3*cubic.getCtrlY2()-a1y)/2.0;
    quad.setCurve(a0x, a0y, (p0x+p1x)/2, (p0y+p1y)/2, a1x, a1y);
  }

  static final private double v18div_sqrt3=18.0/Math.sqrt(3.0);
  
  static final private double v3div_sq36=3.0/(36.0*36);
  
  /**
   * The adaptive halving criterion for approximating a cubic by
   * the <a href="http://www.caffeineowl.com/graphics/2d/vectorial/cubic2quad01.html#mid-point-approx">mid-point 
   * approximation</a> quadratic.
   */
  static final class MidPointApproxSubdivCriterion
  implements CubicSubdivisionCriterion {
    /**
     * The square of the tolerance (degree of precision) accepted 
     * by this criterion.
     */
    protected double sqTol;
    
    /**
     * Constructs an instance tuned for the specified precision
     * (the minimum accepted being <tt>1.0e-5</tt>)
     * @param prec the desired precision.
     */
    MidPointApproxSubdivCriterion(double prec) {
      this.sqTol=prec*prec;
      if(this.sqTol<1.0e-10) {
        this.sqTol=1.0e-5;
      }
    }

    /**
     * Returns <code>true</code> whenever the square of the
     * <a href="http://www.caffeineowl.com/graphics/2d/vectorial/cubic2quad01.html#pseudoQuadDist">distance</a>
     * between the control points of 
     * <tt>0-</tt> and <tt>1-</tt> approximations of the given cubic 
     * multiplied by <tt>1/432=1/(sqrt(3)/36)<sup>2</sup></tt>
     * is less or equal the {@linkplain #sqTol square} of the requested 
     * precision.
     */
    @Override
    public boolean shouldSplit(CubicCurve2D c) {
      double dx=c.getCtrlX2()-c.getCtrlX1();
      dx=c.getX2()-c.getX1()-dx-dx-dx;
      double dy=c.getCtrlY2()-c.getCtrlY1();
      dy=c.getY2()-c.getY1()-dy-dy-dy;
      double sqDist=dx*dx+dy*dy;
      return (sqDist*BezierUtils.v3div_sq36) <= this.sqTol;
    }
  }
  
  /**
   * A {@link CubicSegmentConsumer} that wraps around a
   * {@link QuadSegmentConsumer} and, upon receiving
   * a cubic segment, 
   * {@link BezierUtils#applyMidPointApprox(CubicCurve2D, QuadCurve2D) applies} the
   * mid point approximation and passed the resulted segment 
   * into the {@linkplain #actualConsumer wrapped} consumer.
   */
  static final class MidPointApproxTransformer
  implements CubicSegmentConsumer {
    /**
     * The wrapped consumer.
     */
    QuadSegmentConsumer actualConsumer;
    
    /**
     * Constructor
     * @param readConsumer the {@link QuadSegmentConsumer consumer}
     *   to be used after processing.
     */
    MidPointApproxTransformer(QuadSegmentConsumer readConsumer) {
      this.actualConsumer=readConsumer;
    }

    /**
     * Transforms the received cubic segment into a quadratic
     * one by {@linkplain BezierUtils#applyMidPointApprox(CubicCurve2D, QuadCurve2D) applying}
     * the mid-point approximation. Once the transformation is complete,
     * the resulted segment is {@linkplain QuadSegmentConsumer#processSegment(QuadCurve2D, double, double) fed}
     * into the {@link #actualConsumer wrapped} quad consumer.
     */
    @Override
    public void processSegment(
      CubicCurve2D segment, 
      double startT,  double endT
    ) {
      QuadCurve2D.Double midPointApprox=new QuadCurve2D.Double();
      BezierUtils.applyMidPointApprox(segment, midPointApprox);
      this.actualConsumer.processSegment(midPointApprox, startT, endT);
    }
  }
  static public void adaptiveHalvingDegreeReduction(
    CubicCurve2D cubic, double precision,
    QuadSegmentConsumer resultHere
  ) {
    MidPointApproxSubdivCriterion criterion=
      new MidPointApproxSubdivCriterion(precision);
    MidPointApproxTransformer transformer=new MidPointApproxTransformer(resultHere);
    BezierUtils.adaptiveHalving(cubic, criterion, transformer);
  }
  
  /**
   * The very core of the <tt>adaptive cubic B&eacute;zier degree reduction</a>.
   * @param cubic the cubic to be approximated
   * @param precision the desired precision
   * @param startT the value of the <tt>t</tt> curve parameter corresponding to
   *  the start of the cubic curve (may not be <t>0.0</tt> - the method may recursive)
   * @param endT the value of the <tt>t</tt> curve parameter corresponding to
   *  the end of the cubic curve (may not be <tt>1.0 </tt>- the method may recursive)
   * @param resultHere the consumer to be fed with the results of the adaprive division.
   */
  static private void doAdaptiveDegreeReduction(
    CubicCurve2D cubic, double precision,
    double startT, double endT,
    QuadSegmentConsumer resultHere
  ) {
    QuadCurve2D.Double quad=null;
    double a0x=cubic.getX1(), a0y=cubic.getY1();
    double a1x=cubic.getX2(), a1y=cubic.getY2();
    double p0x=(3*cubic.getCtrlX1()-a0x)/2.0;
    double p0y=(3*cubic.getCtrlY1()-a0y)/2.0;
    double p1x=(3*cubic.getCtrlX2()-a1x)/2.0;
    double p1y=(3*cubic.getCtrlY2()-a1y)/2.0;
    
    double defect=BezierUtils.v18div_sqrt3*Math.hypot(p1x-p0x, p1y-p0y);
    if(defect>=1.0) { // a single segment
      quad=new QuadCurve2D.Double();
      quad.setCurve(a0x, a0y, (p0x+p1x)/2, (p0y+p1y)/2, a1x, a1y);
      resultHere.processSegment(quad, startT, endT);
    }
    else if(defect>=0.125) { // 0.125=(0.5)^3
      CubicCurve2D.Double firstHalf=new CubicCurve2D.Double();
      CubicCurve2D.Double secondHalf=new CubicCurve2D.Double();
      double midT=(startT+endT)/2.0;
      // split the curve in 2 halves
      BezierUtils.halfSplitCurve(cubic, firstHalf, secondHalf);

      quad=new QuadCurve2D.Double();
      BezierUtils.applyMidPointApprox(firstHalf, quad);
      resultHere.processSegment(quad, startT, midT);

      quad=new QuadCurve2D.Double();
      BezierUtils.applyMidPointApprox(secondHalf, quad);
      resultHere.processSegment(quad, midT, endT);
    }
    else { // div t is less than 0.5, go on 1 step adaptive
      double t=Math.cbrt(defect);
      double tes[]={t, 1.0-t};
      CubicCurve2D segs[]={null, null, null};
      BezierUtils.splitCurve(cubic, tes, segs);
      t=endT-startT; // t now contains deltaT
      tes[0]=startT+tes[0]*t; // because t now contains deltaT
      tes[1]=startT+tes[1]*t;// because t now contains deltaT
      quad=new QuadCurve2D.Double();
      BezierUtils.applyMidPointApprox(segs[0], quad);
      resultHere.processSegment(quad, startT, tes[0]);
      segs[0]=null; // let the GC reclaim the memory, if needed
      quad=null;
      
      BezierUtils.doAdaptiveDegreeReduction(segs[1], precision, tes[0], tes[1], resultHere);
      segs[1]=null; // let the GC reclaim the memory, if needed
      
      quad=new QuadCurve2D.Double();
      BezierUtils.applyMidPointApprox(segs[2], quad);
      resultHere.processSegment(quad, tes[1], endT);
      segs[0]=null; // let the GC reclaim the memory, if needed
      quad=null;
    }
  }
  
  /**
    * The <a href="http://www.caffeineowl.com/graphics/2d/vectorial/cubic2quad01.html#the-algo">adaptive
    * degree reduction</a> algorithm, implemented to return the resulted "daisy-chain" of the approximating
    * quadratic segments as an array. The resulted segments are "fed" into
    * a {@link QuadSegmentConsumer} for further processing (or just storing).
    * @param cubic the cubic to be approximated by quad segments
    * @param precision the desired precision of approximation. If drawing the resulted
    *   quad segments, it doesn't make any sense to use less than half of the
    *   resolution of the device used to display it (e.g. if drawing on a screen,
    *   and the <tt>x, y<</tt> coordinates are pixels, use <tt>0.5</tt> as a minimum
    *   for the precision, even if for most of the cases a value of 0.75 or even 1.0 will
    *   be sufficient).
    */
  static public void adaptiveDegreeReduction(
    CubicCurve2D cubic, double precision, 
    QuadSegmentConsumer resultHere
  ) {
    if(precision<0) {
      precision=-precision;
    }
    if(precision==0.0) {
      precision=1.0e-5;
    }
    if(null!=cubic && null!=resultHere) {
      BezierUtils.doAdaptiveDegreeReduction(cubic, precision, 0.0, 1.0, resultHere);
    }
  }
  
  /**
   * A {@link QuadSegmentConsumer} that stores the 
   * {@linkplain #processSegment(QuadCurve2D, double, double) received}
   * quad segments in an {@link #segs array list}.
   */
  static public class QuadArrayListConsumer
  implements QuadSegmentConsumer {
    protected ArrayList<QuadCurve2D> segs=new ArrayList<QuadCurve2D>();

    public void processSegment(QuadCurve2D curve, double startT, double endT) {
      this.segs.add(curve);
    }
  }
  /**
   * A {@link CubicSegmentConsumer} that stores the 
   * {@linkplain #processSegment(CubicCurve2D, double, double) received}
   * cubic segments in an {@link #segs array list}.
   */
  static public class CubicArrayListConsumer
  implements CubicSegmentConsumer {
    protected ArrayList<CubicCurve2D> segs=new ArrayList<CubicCurve2D>();

    public void processSegment(CubicCurve2D curve, double startT, double endT) {
      this.segs.add(curve);
    }
  }
}
