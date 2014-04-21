package com.linkage.gas_station.more;


import gas.achartengine.GraphicalView;
import gas.achartengine.chart.BarChart;
import gas.achartengine.chart.LineChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.chartView.BarChartView;
import com.linkage.gas_station.util.hessian.AnalysisManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

public class HistoryActivity extends BaseActivity implements OnClickListener{
	
	private LinearLayout report = null;
	
	private Button button1 = null;
	private Button button2 = null;
	private TextView datetext = null;
	private int selectMonthIndex = 0;
	
	private ListView listView = null;
	private MyAdapter adapter = null;
	
	private ArrayList<String> monthList = null;
	
	private Map[] monthDataMap;
	private Map[] dayDataMap;
	
	private ArrayList<String> dateList = null;
	private ArrayList<String> dataCountList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_history);
		
		((GasStationApplication) getApplication()).tempActivity.add(HistoryActivity.this);
		
		init();
		getDataInit();
	}
	
	private void init() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		GregorianCalendar aGregorianCalendar = new GregorianCalendar();
		aGregorianCalendar.add(Calendar.DAY_OF_MONTH, - 1);
		String date = df.format(aGregorianCalendar.getTime());
		monthList = new ArrayList<String>();
		monthList.add(date);
		for (int i = 0; i < 6; i++) {
			aGregorianCalendar.add(Calendar.MONTH, - 1);
			date = df.format(aGregorianCalendar.getTime());
			monthList.add(date);
		}
		ImageView title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		TextView title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.more_his));
		
		report = (LinearLayout) findViewById(R.id.report);
		listView = (ListView) findViewById(R.id.listview);
		button1 = (Button) findViewById(R.id.button1);
//		findViewById(R.id.month_layout).setVisibility(View.INVISIBLE);
		button1.setOnClickListener(this);
		button2 = (Button) findViewById(R.id.button2);
		button2.setEnabled(false);
		button2.setOnClickListener(this);
		datetext = (TextView) findViewById(R.id.datetext);
		datetext.setText(monthList.get(selectMonthIndex).replace("-", "年")+"月");
		adapter = new MyAdapter();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.button1:
			button1.setEnabled(false);
			button2.setEnabled(true);
			button1.setBackgroundResource(R.drawable.gl_right_select);
			button1.setTextColor(Color.WHITE);
			button2.setBackgroundResource(R.drawable.gl_left_normal);
			button2.setTextColor(Color.BLACK);
			findViewById(R.id.month_layout).setVisibility(View.INVISIBLE);
			setData(true);
			showChartView(true);
			adapter.notifyDataSetChanged();
			break;
		case R.id.button2:
			findViewById(R.id.month_layout).setVisibility(View.VISIBLE);
			datetext.setText(monthList.get(selectMonthIndex).replace("-", "年")+"月");
			button1.setEnabled(true);
			button2.setEnabled(false);
			button1.setBackgroundResource(R.drawable.gl_right_normal);
			button1.setTextColor(Color.BLACK);
			button2.setBackgroundResource(R.drawable.gl_left_select);
			button2.setTextColor(Color.WHITE);
			setData(false);
			showChartView(false);
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int what = msg.what;
			switch (what) {
			case 0:
				setData(false);
				showChartView(false);
				listView.setAdapter(adapter);
				dismissProgressDialog();
				break;
			case 1:
				setData(false);
				showChartView(false);
				adapter.notifyDataSetChanged();
				dismissProgressDialog();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void setData(boolean isMonthData)
	{
		if(dateList == null)
		{
			dateList = new ArrayList<String>();
		}
		dateList.clear();
		if(dataCountList == null)
		{
			dataCountList = new ArrayList<String>();
		}
		dataCountList.clear();
		if(isMonthData)
		{
			if(monthDataMap == null || monthDataMap.length == 0)
			{
				showCustomToast(getString(R.string.no_data));
				return;
			}
			int size = monthDataMap.length;
			for(int i = 0 ; i < size ; i ++ )
			{
				Map<String, String> map = monthDataMap[i];
				dateList.add(map.get("month"));
				dataCountList.add(map.get("used_amount"));
			}
		}
		else
		{
			if(dayDataMap == null || dayDataMap.length == 0)
			{
				showCustomToast(getString(R.string.no_data));
				return;
			}
			int size = dayDataMap.length;
			for(int i = 0 ; i < size ; i ++ )
			{
				Map<String, String> map = dayDataMap[i];
				dateList.add(map.get("day"));
				dataCountList.add(map.get("used_amount"));
			}
		}
	}
	
	private void getDataInit()
	{
		showProgressDialog(R.string.tishi_loading);
		//初始化当前日期
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		GregorianCalendar aGregorianCalendar = new GregorianCalendar();
		aGregorianCalendar.add(Calendar.DAY_OF_MONTH, - 1);
		final String curDate = df.format(aGregorianCalendar.getTime());
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(HistoryActivity.this);
				boolean flag=true;
				int num=0;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(HistoryActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(HistoryActivity.this);
						
						AnalysisManager manager=GetWebDate.getHessionFactiory(HistoryActivity.this).create(AnalysisManager.class, currentUsedUrl+"/hessian/analysisManager", getClassLoader());
						monthDataMap=manager.getMonthAnalysis(Long.parseLong(list.get(0)), list.get(1));
						dayDataMap=manager.getDayAnalysis(Long.parseLong(list.get(0)), curDate , list.get(1));
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
			        } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							//ip 端口等错误  java.net.SocketTimeoutException
							else {
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									flag=false;
								}
							}
							
						}
						else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									flag=false;
								}
							}
						}
						else {
							wholeUrl.remove(currentUsedUrl);
							if(wholeUrl.size()>0) {
								currentUsedUrl=wholeUrl.get(0);
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								flag=false;
							}
						}
					}
				}
				
				handler.sendEmptyMessage(0);
			}}).start();
	}
	
	private void getdayData()
	{
		showProgressDialog(R.string.tishi_loading);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(HistoryActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(HistoryActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(HistoryActivity.this);
						
						AnalysisManager manager=GetWebDate.getHessionFactiory(HistoryActivity.this).create(AnalysisManager.class
								, currentUsedUrl+"/hessian/analysisManager"
								, getClassLoader());
						dayDataMap=manager.getDayAnalysis(Long.parseLong(list.get(0)), monthList.get(selectMonthIndex)
								, list.get(1));
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
			        } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							//ip 端口等错误  java.net.SocketTimeoutException
							else {
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									flag=false;
								}
							}
							
						}
						else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									flag=false;
								}
							}
						}
						else {
							wholeUrl.remove(currentUsedUrl);
							if(wholeUrl.size()>0) {
								currentUsedUrl=wholeUrl.get(0);
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								flag=false;
							}
						}
					}
				}				
				handler.sendEmptyMessage(1);
			}}).start();
	}
	
	private void showChartView(boolean which) {
		String type = BarChart.TYPE;
		report.removeAllViews();
		if(dateList.size() == 0 || dataCountList.size() == 0 || dateList.size() != dataCountList.size())
		{
			showCustomToast(getString(R.string.no_data));
			return;
		}

		BarChartView vc = new BarChartView();
		if(which)
		{
			type = BarChart.TYPE;
		}
		else
		{
			type = LineChart.TYPE;
		}
		String[] types = {type};
		vc.setType(types);
		vc.setSheng(dateList);
		vc.setCount(dataCountList);

		GraphicalView intent = vc.executeChart(this);
		report.addView(intent, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));	
	}
	
	private class MyAdapter extends BaseAdapter
	{
		TextView date;
		
		TextView count;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dateList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = HistoryActivity.this.getLayoutInflater().inflate(R.layout.activity_history_listview_item,
						null, false);

			}
			date = (TextView) convertView.findViewById(R.id.datetext);
			count = (TextView) convertView.findViewById(R.id.counttext);

			// 赋值
			date.setText(dateList.get(dateList.size() - 1 - position));
			count.setText(dataCountList.get(dataCountList.size() - 1 - position) + "M");
			return convertView;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(HistoryActivity.this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

}
