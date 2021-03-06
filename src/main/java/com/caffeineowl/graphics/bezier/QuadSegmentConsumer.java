/*
  Copyright (c) 2008 Adrian Colomitchi

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

import java.awt.geom.QuadCurve2D;

/**
 * Behaviour for classes used to process/store 
 * <code>java.awt.geom.QuadCurve2D</code> segments resulted from 
 * subdivisions (see 
 * {@link BezierUtils#adaptiveHalving(QuadCurve2D, QuadSubdivisionCriterion, QuadSegmentConsumer)})
 *  or approximations (see FIXME TBD). 
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com)
 */
public interface QuadSegmentConsumer {
  /**
   * The caller passes to the consumer a new segment to be processed.
   * @param segment the quad segment
   * @param startT the value for parameter to which the segment's start corresponds
   * on the original curve.
   * @param endT the value for parameter to which the segment's finish end corresponds
   * on the original curve.
   */
  public void processSegment(QuadCurve2D segment, double startT, double endT);
}
