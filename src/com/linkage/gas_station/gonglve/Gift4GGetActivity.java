package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class Gift4GGetActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	
	TextView gift4g_amount_text=null;
	TextView gift4g_unused_text=null;
	EditText gift4g_operate_flow=null;
	TextView gift4g_operate_ok=null;
	ImageView gift4g_cylinder=null;
	
	//剩余流量跟踪流量
	int last=0;
	int total=0;
	
	TW tw=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gift4gget);
		
		last=(int) Double.parseDouble(getIntent().getExtras().getString("unrecevice_num"));
		total=Integer.parseInt(getIntent().getExtras().getString("total_num"));
		
		tw=new TW();
		((GasStationApplication) getApplication()).tempActivity.add(Gift4GGetActivity.this);
		
		init();
	}
	
	private void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("兑换流量");
		
		gift4g_amount_text=(TextView) findViewById(R.id.gift4g_amount_text);
		gift4g_unused_text=(TextView) findViewById(R.id.gift4g_unused_text);
		gift4g_operate_flow=(EditText) findViewById(R.id.gift4g_operate_flow);
		gift4g_operate_flow.setText(""+last);
		gift4g_operate_ok=(TextView) findViewById(R.id.gift4g_operate_ok);
		gift4g_operate_ok.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				get4GFlow();
			}});
		gift4g_cylinder=(ImageView) findViewById(R.id.gift4g_cylinder);
		
		initUIData();
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
					if(!gift4g_operate_flow.getText().equals(last)) {
						gift4g_operate_flow.setText(""+last);							
					}
					showNum=last;
				}
				else {
					showNum=Integer.parseInt(s.toString());
				}
				CharSequence text = gift4g_operate_flow.getText();
				if(text instanceof Spannable) {
					Spannable spanText = (Spannable)text;
					Selection.setSelection(spanText, text.length());
				}					
			}			
		}		
	}
	
	private void get4GFlow() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==100) {
					Map map=(Map) msg.obj;
					showCustomToast(map.get("comments").toString());
					switch(Integer.parseInt(map.get("result").toString())) {
					case 1:
						last=last-Integer.parseInt(gift4g_operate_flow.getText().toString());
						gift4g_operate_flow.setText(""+last);
						initUIData();
						((GasStationApplication) getApplicationContext()).isRefreshTuan=true;
						break;
					}
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(Gift4GGetActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(Gift4GGetActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(Gift4GGetActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(Gift4GGetActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map result=strategyManager.get4GFlow(Long.parseLong(list.get(0)), Double.parseDouble(gift4g_operate_flow.getText().toString()), getIntent().getExtras().getLong("activityId"), list.get(1));
						m.what=100;
						m.obj=result;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-2;
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
						m.what=-1;
					}
				}
				
				handler.sendMessage(m);				
			}
		}).start();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(Gift4GGetActivity.this);
	}
	
	/**
	 * 加载页面数据
	 */
	public void initUIData() {
		int ratio=0;
		if(total!=0) {
			ratio=(int) (last*100/total);
		}
		gift4g_amount_text.setText(""+total+"MB");
		gift4g_unused_text.setText(""+last+"MB");
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
		gift4g_cylinder.setImageResource(getResources().getIdentifier(getPackageName()+":drawable/cylinder_small_"+imageId, null,null));
	}

}
