package com.linkage.gas_station.util.chartView;


import gas.achartengine.ChartFactory;
import gas.achartengine.GraphicalView;
import gas.achartengine.chart.BarChart;
import gas.achartengine.chart.LineChart;
import gas.achartengine.chart.PointStyle;
import gas.achartengine.model.CategorySeries;
import gas.achartengine.model.XYMultipleSeriesDataset;
import gas.achartengine.renderer.XYMultipleSeriesRenderer;
import gas.achartengine.renderer.XYSeriesRenderer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * 柱形图公共类
 * @author mabin
 */
public class BarChartView extends AbstractDemoChart {

	private List<String> sheng = new ArrayList<String>();
	private List<String> count = new ArrayList<String>();
	private String[] titles = new String[] {"日期"};
	private String Ytitle = "流量(MB)";
	private String Xtitle = "";
	private String title = "";
	private int unit = 1;
	private boolean isClickAble = false;
	
	private boolean isYSmallThanZero = false;
	
	private String[] type = {BarChart.TYPE};
	
	/**
	 * 设置报表类型
	 * @param str
	 */
	public void setType (String[] str)
	{
		type = str;
	}
	
	/**
	 * 设置Y轴是否有小于0的值
	 * @param isSmall
	 */
	public void setYSmall(boolean isSmall){
		this.isYSmallThanZero = isSmall;
	}
	
	public void setTitles(String[] string)
	{
		this.titles = string;
	}
	
	public void setYtitle(String string)
	{
		this.Ytitle = string;
	}
	
	public void setXtitle(String string)
	{
		this.Xtitle = string;
	}
	
	public void setTitle(String string)
	{
		this.title = string;
	}
	
	public void setCount(List<String> count) {
		this.count = count;
	}

	public void setSheng(List<String> sheng) {
		this.sheng = sheng;
	}
	
	/**
	 * 设置是否可点击
	 * @param en
	 */
	public void setClickEnable(boolean en)
	{
		isClickAble = en;
	}
	
	/**
	 * 确定单位
	 * @param top
	 * @param yTitle
	 * @return
	 */
	private String getUnit(double top , String yTitle)
	{
		String[] str = yTitle.split("\\(");
		if(str.length == 1)
		{
			return yTitle;
		}
		if(top < 10000)
		{
			unit = 1;
		}
		else if(top < 100000000)
		{
			yTitle = str[0] + "(万" + str[1];
			unit = 10000;
		}
		else
		{
			yTitle = str[0] + "(亿" + str[1];
			unit = 100000000;
		}
		return yTitle;
	}
	
	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public GraphicalView executeChart(Context context) {
		double top = 0;
		double end = 0;
		List<double[]> values = new ArrayList<double[]>();
		double[] count_t = new double[count.size()];
		DecimalFormat df = new DecimalFormat("0.00"); 
		for(int j=0; j<count.size(); j++){
			count_t[j] = Double.parseDouble(count.get(j));
			if(top < count_t[j]){
				top = count_t[j];
			}
			if(end > count_t[j])
			{
				end = count_t[j];
			}
		}
		Ytitle = getUnit(top, Ytitle);
		for(int j=0; j<count.size(); j++){
			count_t[j] = Double.parseDouble(df.format(count_t[j]/unit));
		}
		top = top/unit;
		end = end/unit;
		
		values.add(count_t);
		
		int[] colors = new int[] { 0xff196404 };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE};
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		
		int dataCount = sheng.size();
		int dataDiv = 1;
		if(type[0].endsWith(LineChart.TYPE))
		{
			int sub = dataCount - 1;
		    int pointCount = 0;
		    if(sub < 7)
		    {
		        pointCount = sub;
		        if(pointCount == 0)
		        {
		            pointCount = 1;
		            sub = 1;
		        }
		    }
		    else if(sub < 11){
		        pointCount = sub / 2;
		    }
		    else if(sub < 19){
		        pointCount = sub / 3;
		    }
		    else if(sub < 25){
		        pointCount = sub / 4;
		    }
		    else if(sub < 31){
		        pointCount = sub / 5;
		    }
		    dataDiv = sub / pointCount;
		}
		
		if(isYSmallThanZero)
		{
			setChartSettings(renderer, title, Xtitle, Ytitle, 0.5, dataCount + 1 , end*1.2, top*1.2,
					Color.LTGRAY, Color.LTGRAY);
		}
		else
		{
			setChartSettings(renderer, title, Xtitle, Ytitle, 0.5, dataCount + 1 , 0, top*1.2,
					Color.LTGRAY, Color.LTGRAY);
		}

		renderer.setXLabels(0);
		renderer.setYLabels(8);

		for(int j=0; j<sheng.size(); j++){
			if(j%dataDiv == 0)
			{
				String string = sheng.get(j);
				String[] srt = string.split("年");
				if(srt.length == 2)
				{
					string = srt[1];
				}
				else
				{
					srt = sheng.get(j).split("月");
					if(srt.length == 2)
					{
						string = srt[1];
					}
				}
				renderer.addXTextLabel(j+1, string);
			}
			
		}

		renderer.setDisplayChartValues(true);
		renderer.setShowGrid(true);
		renderer.setShowLegend(false);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.CENTER);
		renderer.setZoomButtonsVisible(false);
		renderer.setGridColor(Color.WHITE);
		renderer.setPanLimits(new double[] { 0, count.size()+1, 0, 40 });
		renderer.setZoomLimits(new double[] { 0, count.size()+1, 0, 40 });
		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);
		XYSeriesRenderer rr = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
	    rr.setFillBelowLine(true);
	    rr.setFillBelowLineColor(0xff196404);
	    int[] colorsArray = {0xff196404 , 0xff58fe00};
	    rr.setColors(colorsArray);
	    rr.setGradientEnabled(true);
	    rr.setGradientStart(end, 0xff196404);
	    rr.setGradientStop(top, 0xff58fe00);

		renderer.setBarSpacing(1);
		
		if(isClickAble)
		{
			renderer.setClickEnabled(true);
			renderer.setSelectableBuffer(10);
		}
		
		//只有一个柱图时，防止显示太小（控件bug修复）
		if(count_t != null && count_t.length == 1 )
		{
			renderer.setSingleChar(true);
			renderer.setXAxisMax(3);
		}

		XYMultipleSeriesDataset dataset = buildBarDataset(titles, values);

		GraphicalView intent = ChartFactory.getCombinedXYChartView(context, dataset,
				renderer, type);
		
		return intent;
	}

	protected XYMultipleSeriesDataset buildBarDataset(String[] titles,
			List<double[]> values) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(v[k]);
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}

	public Intent execute(Context context) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
