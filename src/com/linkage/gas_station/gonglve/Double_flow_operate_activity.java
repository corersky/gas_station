package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class Double_flow_operate_activity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	ImageView title_refresh=null;
	ImageView cylinder=null;
	TextView amount_text=null;
	TextView unused_text=null;
	EditText double_flow_operate_phonenum=null;
	EditText double_flow_operate_flow=null;
	TextView double_flow_operate_ok=null;
	TextView double_flow_operate_self=null;
	TextView double_flow_operate_gift=null;
	ImageView double_flow_operate_self_box=null;
	ImageView double_flow_operate_gift_box=null;
	LinearLayout double_flow_operate_self_box_layout=null;
	LinearLayout double_flow_operate_gift_box_layout=null;
	ProgressDialog pd=null;
	LinearLayout double_flow_operate_phonenum_layout=null;
	TextView double_flow_desp=null;
	ImageView double_notice=null;
	LinearLayout double_flow_operate_flow_layout=null;
	
	int action=1;
	//剩余流量跟踪流量
	int last=0;
	int total=0;
	
	DisplayMetrics dm=null;
	TW tw=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(dm.density==1.5) {
			setContentView(R.layout.activity_double_flow_operate_15);
		}
		else if(dm.density==1.0) {
			setContentView(R.layout.activity_double_flow_operate_10);
		}
		else {
			setContentView(R.layout.activity_double_flow_operate_20);
		}
		
		last=(int) Double.parseDouble(getIntent().getExtras().getString("unrecevice_num"));
		total=Integer.parseInt(getIntent().getExtras().getString("total_num"));
		
		tw=new TW();
		((GasStationApplication) getApplication()).tempActivity.add(Double_flow_operate_activity.this);
		
		init();
	}
	
	public void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("流量倍增");
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setImageResource(R.drawable.cymx);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(Double_flow_operate_activity.this, DoubleFlowListActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("activityId", Long.parseLong(getIntent().getExtras().getString("activityId")));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		double_flow_operate_phonenum_layout=(LinearLayout) findViewById(R.id.double_flow_operate_phonenum_layout);
		double_flow_operate_gift_box=(ImageView) findViewById(R.id.double_flow_operate_gift_box);
		double_flow_operate_self_box=(ImageView) findViewById(R.id.double_flow_operate_self_box);
		double_flow_operate_self_box_layout=(LinearLayout) findViewById(R.id.double_flow_operate_self_box_layout);
		double_flow_operate_self_box_layout.setOnClickListener(new LinearLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				action=1;
				double_flow_operate_phonenum_layout.setVisibility(View.GONE);
				double_flow_operate_gift_box.setImageResource(R.drawable.double_check);
				double_flow_operate_self_box.setImageResource(R.drawable.double_checked);
				double_flow_desp.setText("领取流量：");
				LinearLayout.LayoutParams params=(LayoutParams) double_flow_operate_flow_layout.getLayoutParams();
				if(dm.density==1.5) {
					params.topMargin=35;
				}
				else if(dm.density==1.0) {
					params.topMargin=25;
				}
				else {
					params.topMargin=55;
				}
				double_flow_operate_flow_layout.setLayoutParams(params);
			}});
		double_flow_operate_gift_box_layout=(LinearLayout) findViewById(R.id.double_flow_operate_gift_box_layout);
		double_flow_operate_gift_box_layout.setOnClickListener(new LinearLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				action=2;
				double_flow_operate_phonenum_layout.setVisibility(View.VISIBLE);
				double_flow_operate_gift_box.setImageResource(R.drawable.double_checked);
				double_flow_operate_self_box.setImageResource(R.drawable.double_check);
				double_flow_desp.setText("转赠流量：");
				LinearLayout.LayoutParams params=(LayoutParams) double_flow_operate_flow_layout.getLayoutParams();
				params.topMargin=10;
				double_flow_operate_flow_layout.setLayoutParams(params);
			}});
		double_flow_operate_self=(TextView) findViewById(R.id.double_flow_operate_self);
		double_flow_operate_gift=(TextView) findViewById(R.id.double_flow_operate_gift);
		cylinder=(ImageView) findViewById(R.id.cylinder);
		amount_text=(TextView) findViewById(R.id.amount_text);
		unused_text=(TextView) findViewById(R.id.unused_text);
		double_flow_operate_phonenum=(EditText) findViewById(R.id.double_flow_operate_phonenum);
		double_flow_operate_flow=(EditText) findViewById(R.id.double_flow_operate_flow);
		double_flow_operate_flow.setText(""+last);
		double_flow_operate_flow.addTextChangedListener(tw);
		double_flow_operate_ok=(TextView) findViewById(R.id.double_flow_operate_ok);
		double_flow_operate_ok.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(action==1) {
					double_flow_operate("0");
				}
				else {
					if(double_flow_operate_phonenum.getText().toString().equals("")) {
						showCustomToast("请输入被赠送人的手机号吗");
					}
					else {
						double_flow_operate(double_flow_operate_phonenum.getText().toString());
					}
				}
			}});
		double_flow_desp=(TextView) findViewById(R.id.double_flow_desp);
		double_notice=(ImageView) findViewById(R.id.double_notice);
		double_notice.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Double_flow_operate_activity.this, WarnInfoActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("warninfo", getIntent().getExtras().getString("activity_url"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		double_flow_operate_flow_layout=(LinearLayout) findViewById(R.id.double_flow_operate_flow_layout);
		LinearLayout.LayoutParams params=(LayoutParams) double_flow_operate_flow_layout.getLayoutParams();
		if(dm.density==1.5) {
			params.topMargin=35;
		}
		else if(dm.density==1.0) {
			params.topMargin=25;
		}
		else{
			params.topMargin=55;
		}
		double_flow_operate_flow_layout.setLayoutParams(params);
		initUIData();
	}
	
	/**
	 * 流量倍增领取或转赠
	 */
	public void double_flow_operate(final String toPhoneNum) {
		pd=ProgressDialog.show(Double_flow_operate_activity.this, getResources().getString(R.string.tishi), "提交中");
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					showCustomToast(map.get("comments").toString());
					switch(Integer.parseInt(map.get("result").toString())) {
					case 1:
						last=last-Integer.parseInt(double_flow_operate_flow.getText().toString());
						double_flow_operate_flow.setText(""+last);
						initUIData();
						((GasStationApplication) getApplicationContext()).isRefreshTuan=true;
						break;
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				pd.dismiss();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(Double_flow_operate_activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(Double_flow_operate_activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(Double_flow_operate_activity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(Double_flow_operate_activity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.double_flow_operate(Long.parseLong(list.get(0)), action, Long.parseLong(toPhoneNum), Double.parseDouble(double_flow_operate_flow.getText().toString()), Long.parseLong(getIntent().getExtras().getString("activityId")), list.get(1));
						if(map==null) {
							m.what=0;
						}
						else {
							m.what=1;
							m.obj=map;
						}
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-1;
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
						m.what=0;
					}
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(Double_flow_operate_activity.this);
	}
	
	/**
	 * 加载页面数据
	 */
	public void initUIData() {
		int ratio=0;
		if(total!=0) {
			ratio=(int) (last*100/total);
		}
		amount_text.setText(""+total+"MB");
		unused_text.setText(""+last+"MB");
		getImageRes(ratio);	
	}
	
	/**
	 * 返回图片id
	 * @param ratio
	 * @return
	 */
	public void getImageRes(int ratio) {
		String imageId=String.valueOf(ratio/10)+String.valueOf(ratio%10>=5?5:0);
		//需要判断是不是小于5并且大于0
		if(imageId.equals("00")&&ratio%10>0) {
			imageId="05";
		}
		cylinder.setImageResource(getResources().getIdentifier(getPackageName()+":drawable/cylinder_small_"+imageId, null,null));
	}
	
	class TW implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			int showNum=-1;
			if(!Util.convertNull(s.toString()).equals("")) {
				if(Integer.parseInt(s.toString())>last) {
					if(!double_flow_operate_flow.getText().equals(last)) {
						double_flow_operate_flow.setText(""+last);							
					}
					showNum=last;
				}
				else {
					showNum=Integer.parseInt(s.toString());
				}
				CharSequence text = double_flow_operate_flow.getText();
				if(text instanceof Spannable) {
					Spannable spanText = (Spannable)text;
					Selection.setSelection(spanText, text.length());
				}					
			}			
		}		
	}	
}
