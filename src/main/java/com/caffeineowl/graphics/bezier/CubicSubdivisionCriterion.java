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

/**
 * Interface to be implemented by algorithms that tell if a 
 * <code>java.awt.geom.CubicCurve2D</code> should be 
 * {@link BezierUtils#adaptiveHalving(CubicCurve2D, CubicSubdivisionCriterion) sub-divided}
 * in order to be more accurate represented by a straight segment.
 * @author Adrian Colomitchi (acolomitchi(monkey_tail)gmail.com)
 */
public interface CubicSubdivisionCriterion {
  public boolean shouldSplit(CubicCurve2D curve);
}
