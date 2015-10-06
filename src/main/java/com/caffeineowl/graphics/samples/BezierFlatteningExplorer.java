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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.LineMetrics;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.caffeineowl.graphics.samples.BezierFlatteningPanel.DistanceType;
import com.caffeineowl.graphics.samples.BezierFlatteningPanel.FlatnessAlgoType;

public class BezierFlatteningExplorer
extends JPanel {
  
  static class TitledBezierFlatteningPanel
  extends BezierFlatteningPanel {
    static Font titleFont=new Font("serif", Font.PLAIN, 12);
    String title;

    /**
     * @param representingCubic
     */
    public TitledBezierFlatteningPanel(
      boolean representingCubic,
      String title
    ) {
      super(representingCubic);
      this.title=title;
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2=(Graphics2D)g;
      Rectangle2D titleBounds=
        TitledBezierFlatteningPanel.titleFont.getStringBounds(
          this.title, 
          g2.getFontRenderContext()
        )
      ;
      g2.setFont(TitledBezierFlatteningPanel.titleFont);
      g2.setPaint(this.getDotPaint());
      g2.drawString(
        this.title, 
        (int)((this.getWidth()-titleBounds.getWidth())/2), 
        (int)(5+titleBounds.getHeight())
      );
    }
    
  }
  
  protected TitledBezierFlatteningPanel robustConvHullPanel;
  protected TitledBezierFlatteningPanel simpleConvHullPanel;
  protected TitledBezierFlatteningPanel eucLineDefectPanel;
  protected TitledBezierFlatteningPanel mhtLineDefectPanel;
  protected TitledBezierFlatteningPanel cbsLineDefectPanel;

  class SynchroListener
  implements BezierPanelListener {
    boolean duringSynchr=false;

    /** Transfer a curve change to other panels (except the source) */
    @Override
    public void curveChanged(BezierPanel src) {
      if(false==this.duringSynchr) {
        this.duringSynchr=true;
        TitledBezierFlatteningPanel[] toSync=new TitledBezierFlatteningPanel[4];
        int pos=0;
        if(BezierFlatteningExplorer.this.robustConvHullPanel!=src) {
          toSync[pos++]=BezierFlatteningExplorer.this.robustConvHullPanel;
        }
        if(BezierFlatteningExplorer.this.simpleConvHullPanel!=src) {
          toSync[pos++]=BezierFlatteningExplorer.this.simpleConvHullPanel;
        }
        if(BezierFlatteningExplorer.this.eucLineDefectPanel!=src) {
          toSync[pos++]=BezierFlatteningExplorer.this.eucLineDefectPanel;
        }
        if(BezierFlatteningExplorer.this.mhtLineDefectPanel!=src) {
          toSync[pos++]=BezierFlatteningExplorer.this.mhtLineDefectPanel;
        }
        if(BezierFlatteningExplorer.this.cbsLineDefectPanel!=src) {
          toSync[pos++]=BezierFlatteningExplorer.this.cbsLineDefectPanel;
        }
        CubicCurve2D srcCurve=src.getRepresentedCubic();
        for(pos=0; pos<toSync.length; pos++) {
          toSync[pos].setRepresentedCubic(srcCurve);
        }
        this.duringSynchr=false;
      }
    }
  }
  
  class PrecHandler
  implements ActionListener, ChangeListener {
    boolean handling=false;
    @Override
    public void actionPerformed(ActionEvent e) {
      // coming from precTextField
      if(false==this.handling) {
        this.handling=true;
        String precVal=BezierFlatteningExplorer.this.precTextField.getText();
        BezierFlatteningExplorer.this.toPrecSlider(precVal);
        this.handling=false;
      }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      // coming from precSlider
      if(false==this.handling) {
        this.handling=true;
        BezierFlatteningExplorer.this.precTextField.setText(""+fromPrecSlider());
        this.handling=false;
      }
    }
  }
  
  protected JSlider       precSlider;
  protected JTextField    precTextField;
  protected JRadioButton  exploreCubics;
  protected JRadioButton  exploreQuads;
  
  public BezierFlatteningExplorer() {

    Dimension prefSize=new Dimension(300, 300);
    this.robustConvHullPanel=new TitledBezierFlatteningPanel(
      true, "Robust convex hull flattening"
    );
    this.robustConvHullPanel.setPreferredSize(prefSize);
    this.robustConvHullPanel.setFlatnessAlgoType(FlatnessAlgoType.ROBUST_CONVEX_HULL);
    this.simpleConvHullPanel=new TitledBezierFlatteningPanel(
      true, "Naive convex hull flattening"
    );
    this.simpleConvHullPanel.setPreferredSize(prefSize);
    this.simpleConvHullPanel.setFlatnessAlgoType(FlatnessAlgoType.SIMPLE_CONVEX_HULL);

    this.eucLineDefectPanel=new TitledBezierFlatteningPanel(
      true, "Line def. (Euc dist) flattening"
    );
    this.eucLineDefectPanel.setPreferredSize(prefSize);
    this.eucLineDefectPanel.setFlatnessAlgoType(FlatnessAlgoType.LINE_DEFECT);
    this.eucLineDefectPanel.setDistanceType(DistanceType.EUCLID);

    this.mhtLineDefectPanel=new TitledBezierFlatteningPanel(
      true, "Line def. (Taxi dist) flattening"
    );
    this.mhtLineDefectPanel.setPreferredSize(prefSize);
    this.mhtLineDefectPanel.setFlatnessAlgoType(FlatnessAlgoType.LINE_DEFECT);
    this.mhtLineDefectPanel.setDistanceType(DistanceType.MANHATTAN);
    
    this.cbsLineDefectPanel=new TitledBezierFlatteningPanel(
      true, "Line def. (Chess dist) flattening"
    );
    this.cbsLineDefectPanel.setPreferredSize(prefSize);
    this.cbsLineDefectPanel.setFlatnessAlgoType(FlatnessAlgoType.LINE_DEFECT);
    this.cbsLineDefectPanel.setDistanceType(DistanceType.CHEBYSHEV);
    
    SynchroListener sync=new SynchroListener();
    this.robustConvHullPanel.addCurveChangeListener(sync);
    this.simpleConvHullPanel.addCurveChangeListener(sync);
    this.eucLineDefectPanel.addCurveChangeListener(sync);
    this.mhtLineDefectPanel.addCurveChangeListener(sync);
    this.cbsLineDefectPanel.addCurveChangeListener(sync);
    
    this.precSlider=new JSlider(SwingConstants.HORIZONTAL, 0, 100, 50);
    this.precSlider.setMajorTickSpacing(10);
    this.precSlider.setPaintTicks(true);
    this.precTextField=new JTextField("2");
    PrecHandler precHandler=new PrecHandler();
    this.precSlider.addChangeListener(precHandler);
    this.precTextField.addActionListener(precHandler);
    this.toPrecSlider("2");

    ActionListener cubicQuadSwitcher=new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        BezierFlatteningExplorer.this.pickCurveType(e.getSource());
      }
    };
    this.exploreCubics=new JRadioButton("... cubics");
    this.exploreCubics.addActionListener(cubicQuadSwitcher);
    this.exploreQuads=new JRadioButton("... quads");
    this.exploreQuads.addActionListener(cubicQuadSwitcher);
    ButtonGroup btnGrp=new ButtonGroup();
    btnGrp.add(this.exploreCubics);
    btnGrp.add(this.exploreQuads);
    this.exploreCubics.setSelected(true);
    
    // layouting
    this.setLayout(new GridLayout(2, 3));
    this.add(this.eucLineDefectPanel);
    this.add(this.mhtLineDefectPanel);
    this.add(this.cbsLineDefectPanel);
    this.add(this.robustConvHullPanel);
    this.add(this.simpleConvHullPanel);
    
    
    JPanel radioSupport=new JPanel();
    radioSupport.setBorder(BorderFactory.createTitledBorder("Now exploring"));
    radioSupport.setLayout(new GridLayout(2, 1));
    radioSupport.add(this.exploreCubics);
    radioSupport.add(this.exploreQuads);
    radioSupport.setBackground(Color.white);
    
    JPanel precSupport=new JPanel();
    precSupport.setLayout(new BorderLayout());
    precSupport.setBorder(BorderFactory.createTitledBorder("Precision"));
    precSupport.add(this.precSlider, BorderLayout.CENTER);
    precSupport.add(this.precTextField, BorderLayout.NORTH);
    precSupport.setBackground(Color.white);

    JPanel ctrlsSupport=new JPanel();
    ctrlsSupport.setLayout(new BorderLayout());
    ctrlsSupport.add(radioSupport, BorderLayout.NORTH);
    ctrlsSupport.add(precSupport, BorderLayout.SOUTH);
    ctrlsSupport.setBackground(Color.white);
    
    this.precSlider.setBackground(new Color(231, 231, 255));
    
    this.add(ctrlsSupport);
 }
  
  protected void pickCurveType(Object source) {
    if(source==this.exploreCubics) {
      this.robustConvHullPanel.setRepresentingCubic(true);
      this.simpleConvHullPanel.setRepresentingCubic(true);
      this.cbsLineDefectPanel.setRepresentingCubic(true);
      this.mhtLineDefectPanel.setRepresentingCubic(true);
      this.eucLineDefectPanel.setRepresentingCubic(true);
    }
    else {
      this.robustConvHullPanel.setRepresentingCubic(false);
      this.simpleConvHullPanel.setRepresentingCubic(false);
      this.cbsLineDefectPanel.setRepresentingCubic(false);
      this.mhtLineDefectPanel.setRepresentingCubic(false);
      this.eucLineDefectPanel.setRepresentingCubic(false);
    }
  }

  double fromPrecSlider() {
    int sliderVal=this.precSlider.getValue();
    // mid representing 2^0, a step of mid-range/10 representing a multiplication/division by 2
    double sliderRange=this.precSlider.getMaximum()-this.precSlider.getMinimum();
    double sliderMid=(this.precSlider.getMaximum()+this.precSlider.getMinimum())/2.0;
    double toRet=10*(sliderVal-sliderMid)/sliderRange;
    toRet=Math.pow(2, toRet);

    this.simpleConvHullPanel.setTolerance(toRet);
    this.robustConvHullPanel.setTolerance(toRet);
    this.eucLineDefectPanel.setTolerance(toRet);
    this.mhtLineDefectPanel.setTolerance(toRet);
    this.cbsLineDefectPanel.setTolerance(toRet);
    return toRet;
  }
  
  void toPrecSlider(String valStr) {
    double val=-1;
    try {
      val=Double.parseDouble(valStr);
    }
    catch(NumberFormatException e) {
      // ignore
    }
    if(val>0) {
      double sliderVal=Math.log(val)/Math.log(2);
      
      double sMin=this.precSlider.getMinimum();
      double sMax=this.precSlider.getMaximum();

      sliderVal=(sMax-sMin)*sliderVal/10+(sMax+sMin)/2;
      sliderVal=Math.max(sliderVal, sMin);
      sliderVal=Math.min(sliderVal, sMax);
      this.precSlider.setValue((int)sliderVal);
      this.simpleConvHullPanel.setTolerance(val);
      this.robustConvHullPanel.setTolerance(val);
      this.eucLineDefectPanel.setTolerance(val);
      this.mhtLineDefectPanel.setTolerance(val);
      this.cbsLineDefectPanel.setTolerance(val);
    }
    else {
      JOptionPane.showMessageDialog(this, 
        "<html>Only strictly positive precisions, please.<br>Got: "+valStr,
        "Bad precision/tolerance",
        JOptionPane.ERROR_MESSAGE
      );
      this.precTextField.setText(""+this.fromPrecSlider());
      this.precTextField.selectAll();
    }
  }
  /**
   * @param args
   */
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      // ignore
    }
    JFrame frm=new JFrame("Bezier flattening algos explorer");
    frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    BezierFlatteningExplorer panel=new BezierFlatteningExplorer();
    
    frm.getContentPane().setLayout(new BorderLayout());
    frm.getContentPane().add(panel, BorderLayout.CENTER);
    
    frm.pack();
    frm.setVisible(true);
  }

}
