/*
  Copyright (c) 2010 Adrian Colomitchi

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

package com.caffeineowl.graphics.samples;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.CubicCurve2D.Double;

import javax.swing.JFrame;

import com.caffeineowl.graphics.bezier.BezierUtils;
import com.caffeineowl.graphics.bezier.CubicFlatnessAlgorithm;
import com.caffeineowl.graphics.bezier.CubicSegmentConsumer;
import com.caffeineowl.graphics.bezier.CubicSubdivisionCriterion;
import com.caffeineowl.graphics.bezier.QuadFlatnessAlgorithm;
import com.caffeineowl.graphics.bezier.QuadSegmentConsumer;
import com.caffeineowl.graphics.bezier.QuadSubdivisionCriterion;
import com.caffeineowl.graphics.bezier.flatnessalgos.ConvexHullSubdivCriterion;
import com.caffeineowl.graphics.bezier.flatnessalgos.LineDefectFlatnessAlgo;
import com.caffeineowl.graphics.bezier.flatnessalgos.LineDefectSubdivCriterion;
import com.caffeineowl.graphics.bezier.flatnessalgos.SimpleConvexHullFlatness;
import com.caffeineowl.graphics.bezier.flatnessalgos.SimpleConvexHullSubdivCriterion;

public class BezierFlatteningPanel 
extends BezierPanel {
  
  static Font numSegsFont=new Font("monospaced", Font.PLAIN, 12);
  
  static class QuadOrCubicSegsFormatter
  implements CubicSegmentConsumer, QuadSegmentConsumer {
    GeneralPath segsChain;
    int numSegs;

    public QuadOrCubicSegsFormatter() {
      this.segsChain=new GeneralPath();
    }

    public int getNumSegs() {
      return this.numSegs;
    }


    @Override
    public void processSegment(CubicCurve2D segment, double startT, double endT) {
      if(0.0==startT) {
        this.segsChain.reset();
        this.segsChain.moveTo(segment.getX1(), segment.getY1());
        this.numSegs=0;
      }
      this.segsChain.lineTo(segment.getX2(), segment.getY2());
      this.numSegs++;
    }

    @Override
    public void processSegment(QuadCurve2D segment, double startT, double endT) {
      if(0.0==startT) {
        this.segsChain.reset();
        this.segsChain.moveTo(segment.getX1(), segment.getY1());
        this.numSegs=0;
      }
      this.segsChain.lineTo(segment.getX2(), segment.getY2());
      this.numSegs++;
    }

    public GeneralPath getSegsChain() {
      return this.segsChain;
    }
  }
  
  static class FlattenerByAdaptiveHalving
  implements BezierPanelListener {
    
    BezierFlatteningPanel served;
    
    FlattenerByAdaptiveHalving(BezierFlatteningPanel parent) {
      this.served=parent;
    }

    @Override
    public void curveChanged(BezierPanel panel) {
      if(this.served==panel) {
        if(this.served.isRepresentingCubic()) {
          CubicSubdivisionCriterion crit=this.served.createCubicSubdivCriterion();
          CubicCurve2D c=this.served.getRepresentedCubic();
          BezierUtils.adaptiveHalving(c, crit, this.served.getSegsFormatter());
        }
        else {
          QuadSubdivisionCriterion crit=this.served.createQuadSubdivCriterion();
          QuadCurve2D c=this.served.getRepresentedQuad();
          BezierUtils.adaptiveHalving(c, crit, this.served.getSegsFormatter());
        }
        if(this.served.isDisplayable()) {
          this.served.repaint(33);
        }
      }
    }
    
  }

  public enum FlatnessAlgoType {
    SIMPLE_CONVEX_HULL,
    ROBUST_CONVEX_HULL,
    LINE_DEFECT
  };
  
  public enum DistanceType {
    EUCLID,
    MANHATTAN,
    CHEBYSHEV
  };
  
  FlatnessAlgoType flatnessAlgoType=FlatnessAlgoType.ROBUST_CONVEX_HULL;
  DistanceType     distanceType=DistanceType.EUCLID;
  
  double tolerance=50.0;
  Color  linePaint;
  Stroke lineStroke;
  QuadOrCubicSegsFormatter  segsFormatter;
  
  FlattenerByAdaptiveHalving flattener;
  
  
  BezierFlatteningPanel(boolean representingCubic) {
    super();
    this.setRepresentingCubic(representingCubic);
    // appearance
    this.linePaint=Color.magenta;
    this.lineStroke=new BasicStroke(
      1.0f, 
      BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, 
      new float[] {5, 5}, 0.0f
    );

    // logic
    this.segsFormatter=new QuadOrCubicSegsFormatter();
    this.flattener=new FlattenerByAdaptiveHalving(this);
    this.addCurveChangeListener(this.flattener);
    this.flattener.curveChanged(this); // ask to recompute
  }
  
  protected final CubicSubdivisionCriterion createCubicSubdivCriterion() {
    CubicSubdivisionCriterion toRet=null;
    switch(this.flatnessAlgoType) {
      case LINE_DEFECT:
        switch(this.distanceType) {
          case CHEBYSHEV:
            toRet=new LineDefectSubdivCriterion(LineDefectFlatnessAlgo.CBSV_DIST, this.tolerance);
            break;
          case MANHATTAN:
            toRet=new LineDefectSubdivCriterion(LineDefectFlatnessAlgo.MNHT_DIST, this.tolerance);
            break;
          default:
            toRet=new LineDefectSubdivCriterion(LineDefectFlatnessAlgo.EUCL_DIST, this.tolerance);
            break;
        }
        break;
      case SIMPLE_CONVEX_HULL:
        toRet=new SimpleConvexHullSubdivCriterion(this.tolerance);
        break;
      default:
        toRet=new ConvexHullSubdivCriterion(this.tolerance);
        break;
    }
    return toRet;
  }

  protected final QuadSubdivisionCriterion createQuadSubdivCriterion() {
    QuadSubdivisionCriterion toRet=null;
    switch(this.flatnessAlgoType) {
      case LINE_DEFECT:
        toRet=new LineDefectSubdivCriterion(this.tolerance);
        break;
      case SIMPLE_CONVEX_HULL:
        toRet=new SimpleConvexHullSubdivCriterion(this.tolerance);
        break;
      default:
        toRet=new ConvexHullSubdivCriterion(this.tolerance);
        break;
    }
    return toRet;
  }
  
  public QuadOrCubicSegsFormatter getSegsFormatter() {
    return this.segsFormatter;
  }

  public double getTolerance() {
    return this.tolerance;
  }
  
  public FlatnessAlgoType getFlatnessAlgoType() {
    return this.flatnessAlgoType;
  }

  public void setFlatnessAlgoType(FlatnessAlgoType flatnessAlgoType) {
    this.flatnessAlgoType=flatnessAlgoType;
    this.flattener.curveChanged(this); // recompute
  }

  public DistanceType getDistanceType() {
    return this.distanceType;
  }

  public void setDistanceType(DistanceType distanceType) {
    this.distanceType=distanceType;
    this.flattener.curveChanged(this); // recompute
  }

  public void setTolerance(double tolerance) {
    this.tolerance=tolerance;
    this.flattener.curveChanged(this); // recompute
  }

  public Color getLinePaint() {
    return this.linePaint;
  }

  public void setLinePaint(Color linePaint) {
    this.linePaint=linePaint;
    this.repaint(50);
  }

  public Stroke getLineStroke() {
    return this.lineStroke;
  }

  public void setLineStroke(Stroke lineStroke) {
    this.lineStroke=lineStroke;
    this.repaint(50);
  }

  public GeneralPath getSegsChain() {
    return this.segsFormatter.getSegsChain();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g); // paint the curve
    Graphics2D g2=(Graphics2D)g;
    g2.setPaint(this.getLinePaint());
    g2.setStroke(this.getLineStroke());
    g2.draw(this.getSegsChain());
    
    // printing the number of resulted segments
    StringBuffer numTxt=new StringBuffer("Num segs: ");
    numTxt.append(this.segsFormatter.getNumSegs());
    g2.setFont(BezierFlatteningPanel.numSegsFont);
    LineMetrics lm=
      BezierFlatteningPanel.numSegsFont.getLineMetrics(
        numTxt.toString(), g2.getFontRenderContext()
      )
    ;
    g2.setPaint(this.getDotPaint());
    g2.drawString(numTxt.toString(), 10, this.getHeight()-10-lm.getAscent());
  }
  
  @Override
  public void setBounds(int x, int y, int w, int h) {
    super.setBounds(x, y, w, h);
    if(null!=this.flattener) {
      this.flattener.curveChanged(this); // ask a recomputation
    }
  }
  
  

  @Override
  public void setRepresentingCubic(boolean b) {
    super.setRepresentingCubic(b);
    if(null!=this.flattener) {
      this.flattener.curveChanged(this); // ask a recomputation
    }
  }
  
}
