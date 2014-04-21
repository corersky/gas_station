/**
 * Copyright (C) 2009 - 2012 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gas.achartengine.chart;


import gas.achartengine.model.CategorySeries;
import gas.achartengine.model.Point;
import gas.achartengine.model.SeriesSelection;
import gas.achartengine.renderer.DefaultRenderer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;



import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;

/**
 * The pie chart rendering class.
 */
public class PieChart extends RoundChart {
  /** Handles returning values when tapping on PieChart. */
  private PieMapper mPieMapper;

  /**
   * Builds a new pie chart instance.
   * 
   * @param dataset the series dataset
   * @param renderer the series renderer
   */
  public PieChart(CategorySeries dataset, DefaultRenderer renderer) {
    super(dataset, renderer);
    mPieMapper = new PieMapper();
  }

  /**
   * The graphical representation of the pie chart.
   * 
   * @param canvas the canvas to paint to
   * @param x the top left x value of the view to draw to
   * @param y the top left y value of the view to draw to
   * @param width the width of the view to draw to
   * @param height the height of the view to draw to
   * @param paint the paint
   */
  @Override
  public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
    paint.setAntiAlias(mRenderer.isAntialiasing());
    paint.setStyle(Style.FILL);
    paint.setTextSize(mRenderer.getLabelsTextSize());
    int legendSize = getLegendSize(mRenderer, height / 5, 0);
    int left = x;
    int top = y;
    int right = x + width;
    int sLength = mDataset.getItemCount();
    double total = 0;
    String[] titles = new String[sLength];
    for (int i = 0; i < sLength; i++) {
      total += mDataset.getValue(i);
      titles[i] = mDataset.getCategory(i);
    }
    if (mRenderer.isFitLegend()) {
      legendSize = drawLegend(canvas, mRenderer, titles, left, right, y, width, height, legendSize,
          paint, true);
    }
    int bottom = y + height - legendSize;
    drawBackground(mRenderer, canvas, x, y, width, height, paint, false, DefaultRenderer.NO_COLOR);

    float currentAngle = mRenderer.getStartAngle();
    int mRadius = Math.min(Math.abs(right - left), Math.abs(bottom - top));
    //更改圆饼半径
    int radius = (int) (mRadius * 0.35 * mRenderer.getScale()) + 20;

    if (mCenterX == NO_VALUE) {
      mCenterX = (left + right) / 2;
    }
    if (mCenterY == NO_VALUE) {
      mCenterY = (bottom + top) / 2 + 10;
    }

    // Hook in clip detection after center has been calculated
    mPieMapper.setDimensions(radius, mCenterX, mCenterY);
    boolean loadPieCfg = !mPieMapper.areAllSegmentPresent(sLength);
    if (loadPieCfg) {
      mPieMapper.clearPieSegments();
    }

    float shortRadius = radius * 0.9f;
    float longRadius = radius * 1.1f;

    RectF oval = new RectF(mCenterX - radius, mCenterY - radius, mCenterX + radius, mCenterY
        + radius);
    RectF oval2 = new RectF(mCenterX - radius + 3, mCenterY - radius + 3, mCenterX + radius - 3, mCenterY
            + radius - 3);
    List<RectF> prevLabelsBounds = new ArrayList<RectF>();

    Paint paint2 = new Paint();
    paint2.setAntiAlias(mRenderer.isAntialiasing());
    paint2.setStyle(Style.FILL);
    paint2.setTextSize(mRenderer.getLabelsTextSize());
    Paint paint3 = new Paint();
    paint3.setAntiAlias(mRenderer.isAntialiasing());
    paint3.setStyle(Style.FILL);
    paint3.setTextSize(mRenderer.getLabelsTextSize());
    
    for (int i = 0; i < sLength; i++) {
      paint.setColor(mRenderer.getSeriesRendererAt(i).getColor());
      //实现饼图渐变色
      int[] gradientColors = mRenderer.getSeriesRendererAt(i).getColors();
      float[] gradientPositions = {0.0f , 0.95f , 1.0f};
      RadialGradient radialGradientShader=new RadialGradient(mCenterX,mCenterY, radius, gradientColors, gradientPositions, TileMode.CLAMP);
      paint2.setShader(radialGradientShader);
      int[] gradientColors2 = {gradientColors[0] , gradientColors[1]};
      float[] gradientPositions2 = {0.0f , 1.0f};
      RadialGradient radialGradientShader2=new RadialGradient(mCenterX,mCenterY, radius, gradientColors2, gradientPositions2, TileMode.CLAMP);
      paint3.setShader(radialGradientShader2);
      // 设置光源的方向
      float[] direction = new float[]{ 1, 1, 1 };
      //设置环境光亮度
      float light = 0.6f;
      // 选择要应用的反射等级
      float specular = 5;
      // 向mask应用一定级别的模糊
      float blur = 2.5f;
      EmbossMaskFilter em = new EmbossMaskFilter(direction, light, specular, blur);
      paint2.setMaskFilter(em);
      float value = (float) mDataset.getValue(i);
      float angle = (float) (value / total * 360);
      canvas.drawArc(oval, currentAngle, angle, true, paint2);
      canvas.drawArc(oval2, currentAngle, angle, true, paint3);
      drawLabel(canvas, mDataset.getCategory(i), mRenderer, prevLabelsBounds, mCenterX, mCenterY,
          shortRadius, longRadius, currentAngle, angle, left, right, mRenderer.getLabelsColor(),
          paint, true);
      if (mRenderer.isDisplayValues()) {
    	  //修改饼图上显示百分比的位置和文字定义
    	  DecimalFormat df = new DecimalFormat("0.0");
    	  String label = df.format(mDataset.getValue(i)) + "%";
          drawLabel(canvas, label, mRenderer, prevLabelsBounds, mCenterX,
              mCenterY, shortRadius * 3 / 5, longRadius * 3 / 5, currentAngle, angle, left, right,
              mRenderer.getLabelsColor(), paint, false);
      }

      // Save details for getSeries functionality
      if (loadPieCfg) {
        mPieMapper.addPieSegment(i, value, currentAngle, angle);
      }
      currentAngle += angle;
    }
    prevLabelsBounds.clear();
    drawLegend(canvas, mRenderer, titles, left, right, y, width, height, legendSize, paint, false);
    drawTitle(canvas, x, y, width, paint);
  }

  public SeriesSelection getSeriesAndPointForScreenCoordinate(Point screenPoint) {
    return mPieMapper.getSeriesAndPointForScreenCoordinate(screenPoint);
  }

}
