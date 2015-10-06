/*
 * (the MIT licence is in use for the present Software and documentation)
 *
 * Copyright (c) 2004 Adrian Colomitchi
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.caffeineowl.graphics.samples;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;


/**
 * <p></p>
 * 
 * <p>
 * <i>Creation date: Sep 11, 2004</i>
 * </p>
 *
 * @author Adrian Colomitchi
 */
public class BezierPanel extends JPanel
{
  private static final long serialVersionUID =4502036303710475820L;
  
  protected int oldW=200;
  protected int oldH=200;
  protected Point2D c0=new Point2D.Double();
  protected Point2D c1=new Point2D.Double();
  protected Point2D p0=new Point2D.Double();
  protected Point2D p1=new Point2D.Double();
  protected Point2D lastClicked=null;
  protected boolean representingCubic=true;
  protected boolean labelsShown=true;
  protected Paint curvePaint=Color.BLACK;
  protected Paint handlePaint=Color.LIGHT_GRAY;
  protected Paint dotPaint=Color.RED;
  protected ArrayList listeners=new ArrayList();

  /** Inner use */
  private   boolean notifyListeners=true;

  public BezierPanel()
  {
    this.setBackground(Color.WHITE);
    p0.setLocation(50, 180);
    p1.setLocation(150, 180);
    c0.setLocation(180, 50);
    c1.setLocation(20, 50);

    this.addMouseListener(
      new MouseAdapter() {
        public void mousePressed(MouseEvent e)
        {
          Point2D loc=e.getPoint();
          mouseSelection(loc);
        }

        public void mouseReleased(MouseEvent e)
        {
          BezierPanel.this.lastClicked=null;
        }
      }
    );
    this.addMouseMotionListener(
      new MouseMotionAdapter() {
        public void mouseDragged(MouseEvent e)
        {
          if(
            (null!=BezierPanel.this.lastClicked)
              && (e.getX()>=0)
              && (e.getX()<BezierPanel.this.getWidth())
              && (e.getY()>=0)
              && (e.getY()<BezierPanel.this.getHeight())
          ) {
            BezierPanel.this.lastClicked.setLocation(e.getPoint());
            BezierPanel.this.dragGesture();
          }
        }
      }
    );
  }

  public void addCurveChangeListener(BezierPanelListener listener)
  {
    this.listeners.remove(listener);
    this.listeners.add(listener);
  }

  public void removeCurveChangeListener(BezierPanelListener listener)
  {
    this.listeners.remove(listener);
  }
  
  protected BezierPanelListener[] getBezierPanelListeners() {
    BezierPanelListener[] toRet=new BezierPanelListener[this.listeners.size()];
    this.listeners.toArray(toRet);
    return toRet;
  }

  protected void dragGesture()
  {
    this.fireCurveChanged();
    BezierPanel.this.repaint(50);
  }
  
  protected void fireCurveChanged() {
    if(this.notifyListeners) {
      BezierPanelListener[] lists=this.getBezierPanelListeners();
      for(int i=0; i<lists.length; i++) {
        lists[i].curveChanged(this);
      }
    }
  }
  
  public CubicCurve2D getRepresentedCubic() {
    CubicCurve2D toRet=new CubicCurve2D.Double();
    toRet.setCurve(this.p0, this.c0, this.c1, this.p1);
    return toRet;
  }
  
  public void setRepresentedCubic(CubicCurve2D c) {
    if(null!=c) {
      this.p0.setLocation(c.getP1());
      this.p1.setLocation(c.getP2());
      this.c0.setLocation(c.getCtrlP1());
      this.c1.setLocation(c.getCtrlP2());
      this.dragGesture();
    }
  }

  public QuadCurve2D getRepresentedQuad() {
    QuadCurve2D toRet=new QuadCurve2D.Double();
    toRet.setCurve(this.p0, this.c0, this.p1);
    return toRet;
  }

  public void setBounds(int x, int y, int w, int h)
  {
    super.setBounds(x, y, w, h);

    if((w!=0) && (h!=0)) {
      c0.setLocation(c0.getX()/oldW*w, c0.getY()/oldH*h);
      c1.setLocation(c1.getX()/oldW*w, c1.getY()/oldH*h);
      p0.setLocation(p0.getX()/oldW*w, p0.getY()/oldH*h);
      p1.setLocation(p1.getX()/oldW*w, p1.getY()/oldH*h);
      this.oldW=w;
      this.oldH=h;
    }
  }

  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);

    double midX=(this.p0.getX()+this.p1.getX())/2.0;
    double midY=(this.p0.getY()+this.p1.getY())/2.0;

    Graphics2D g2=(Graphics2D) g;
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    Shape c=null;

    if(this.isRepresentingCubic()) {
      CubicCurve2D cu=new CubicCurve2D.Double();
      cu.setCurve(this.p0, this.c0, this.c1, this.p1);
      c=cu;
    } else {
      QuadCurve2D cu=new QuadCurve2D.Double();
      cu.setCurve(this.p0, this.c0, this.p1);
      c=cu;
    }

    g2.setPaint(this.curvePaint);
    g2.setStroke(new BasicStroke(2.0f));
    g2.draw(c);

    Line2D.Double line=new Line2D.Double();

    g2.setPaint(this.handlePaint);
    g2.setStroke(new BasicStroke(0.5f));

    line.setLine(this.p0, this.c0);
    g2.draw(line);

    if(this.isRepresentingCubic()) {
      line.setLine(this.p1, this.c1);
    } else {
      line.setLine(this.p1, this.c0);
    }

    g2.draw(line);

    g2.setPaint(this.dotPaint);

    Rectangle2D rect=new Rectangle2D.Double();
    rect.setFrame(p0.getX()-2, p0.getY()-2, 5, 5);
    g2.fill(rect);
    rect.setFrame(p1.getX()-2, p1.getY()-2, 5, 5);
    g2.fill(rect);
    rect.setFrame(c0.getX()-2, c0.getY()-2, 5, 5);
    g2.fill(rect);

    if(this.labelsShown) {
      this.drawLabel(
        g2,
        "P1",
        midX+(1.1*(p0.getX()-midX)),
        midY+(1.1*(p0.getY()-midY))
      );
      this.drawLabel(
        g2,
        "P2",
        midX+(1.1*(p1.getX()-midX)),
        midY+(1.1*(p1.getY()-midY))
      );
    }

    if(this.isRepresentingCubic()) {
      this.drawLabel(
        g2,
        "C1",
        midX+(1.1*(c0.getX()-midX)),
        midY+(1.1*(c0.getY()-midY))
      );
      rect.setFrame(c1.getX()-2, c1.getY()-2, 5, 5);
      g2.fill(rect);

      if(this.labelsShown) {
        this.drawLabel(
          g2,
          "C2",
          midX+(1.1*(c1.getX()-midX)),
          midY+(1.1*(c1.getY()-midY))
        );
      }
    } else {
      this.drawLabel(
        g2,
        "C",
        midX+(1.1*(c0.getX()-midX)),
        midY+(1.1*(c0.getY()-midY))
      );
    }
  }

  public Paint getCurvePaint()
  {
    return curvePaint;
  }

  public Paint getDotPaint()
  {
    return dotPaint;
  }

  public Paint getHandlePaint()
  {
    return handlePaint;
  }

  public void setCurvePaint(Paint paint)
  {
    curvePaint=paint;
  }

  public void setDotPaint(Paint paint)
  {
    dotPaint=paint;
  }

  public void setHandlePaint(Paint paint)
  {
    handlePaint=paint;
  }

  public boolean isRepresentingCubic()
  {
    return representingCubic;
  }

  public void setRepresentingCubic(boolean b)
  {
    representingCubic=b;
    if(this.isDisplayable()) {
      this.repaint(50);
    }
  }

  protected void drawLabel(Graphics2D g2, String label, double x, double y)
  {
    Font labelFont=new Font("monospaced", Font.BOLD, 11);
    FontRenderContext frc=g2.getFontRenderContext();
    TextLayout tl=new TextLayout(label, labelFont, frc);
    Rectangle2D rect=tl.getBounds();
    tl.draw(g2, (float) (x-(rect.getWidth()/2)), (float) (y-rect.getHeight()));
  }

  protected void fillDiamond(
    Graphics2D g2,
    double x,
    double y,
    GeneralPath aux
  )
  {
    if(null==aux) {
      aux=new GeneralPath();
    } else {
      aux.reset();
    }

    aux.moveTo((float) x, (float) y-4);
    aux.lineTo((float) (x-3), (float) y);
    aux.lineTo((float) x, (float) (y+4));
    aux.lineTo((float) (x+3), (float) y);
    aux.closePath();
    g2.fill(aux);
  }

  protected void drawDiamond(
    Graphics2D g2,
    double x,
    double y,
    GeneralPath aux
  )
  {
    if(null==aux) {
      aux=new GeneralPath();
    } else {
      aux.reset();
    }

    aux.moveTo((float) x, (float) y-4);
    aux.lineTo((float) (x-3), (float) y);
    aux.lineTo((float) x, (float) (y+4));
    aux.lineTo((float) (x+3), (float) y);
    aux.closePath();
    g2.draw(aux);
  }

  /**
   * @param loc
   */
  protected void mouseSelection(Point2D loc) {
    if(loc.distance(this.p0)<3) {
      this.lastClicked=p0;
    } else if(loc.distance(this.p1)<3) {
      this.lastClicked=p1;
    } else if(loc.distance(this.c0)<3) {
      this.lastClicked=c0;
    } else if(this.representingCubic && (loc.distance(this.c1)<3)) {
      this.lastClicked=c1;
    }
  }
}
