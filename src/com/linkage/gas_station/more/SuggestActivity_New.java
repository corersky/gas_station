package com.linkage.gas_station.more;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.jpush.JPushReceiver;
import com.linkage.gas_station.model.FeedBackModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.CommonManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

public class SuggestActivity_New extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	ProgressDialog pd=null;
	ListView suggest_listview=null;
	EditText suggest_content=null;
	TextView suggest_button=null;
	SuggestAdapter adapter=null;
	ArrayList<FeedBackModel> model_list=null;
	
	//起始item位置
	int firstItem=0;
	//加载标志位
	boolean isLoad=false;
	ArrayList<HashMap<String, Object>> suggest_list=null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_suggest_new);
		
		((GasStationApplication) getApplication()).tempActivity.add(SuggestActivity_New.this);
		model_list=new ArrayList<FeedBackModel>();
		
		init();
	}
	
	public void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.more_suggest));
		suggest_content=(EditText) findViewById(R.id.suggest_content);
		suggest_button=(TextView) findViewById(R.id.suggest_button);
		suggest_button.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				System.out.println(getUNICODEBytes(suggest_content.getText().toString()));
				feedBack();
			}});
		suggest_listview=(ListView) findViewById(R.id.suggest_list);
		adapter=new SuggestAdapter(model_list, SuggestActivity_New.this);
		suggest_listview.setAdapter(adapter);
		getSuggestList();
	}
	
	public static String getUNICODEBytes(String s) {
		   try {
		    StringBuffer out = new StringBuffer();
		    byte[] bytes = s.getBytes("unicode");
		    for (int i = 2; i < bytes.length - 1; i += 2) {
		     out.append("");
		     String str = Integer.toHexString(bytes[i + 1] & 0xff);
		     for (int j = str.length(); j < 2; j++) {
		      out.append("0");
		     }
		     out.append(str);
		     String str1 = Integer.toHexString(bytes[i] & 0xff);
		     for (int j = str1.length(); j < 2; j++) {
		      out.append("0");
		     }
		     out.append(str1);
		    }
		    return out.toString();
		   } catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		    return null;
		   }
		}
	
	public void getSuggestList() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.obj!=null) {
					model_list.clear();
					Map[] map=(Map[]) msg.obj;
					for(int i=0;i<map.length;i++) {
						Map map_temp=map[i];
						FeedBackModel model=new FeedBackModel();
						model.setGenerate_time(Util.convertNull(map_temp.get("generate_time").toString()).equals("")?"":map_temp.get("generate_time").toString());
						model.setDeal_time(map_temp.get("deal_time")==null?"":map_temp.get("deal_time").toString());
						model.setFeed_back(map_temp.get("feed_back")==null?"":map_temp.get("feed_back").toString());
						model.setReply(map_temp.get("reply")==null?"":map_temp.get("reply").toString());
						model_list.add(model);
					}
					if(map.length==0) {
						//温馨提示
						FeedBackModel model=new FeedBackModel();
						model.setGenerate_time("");
						SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						model.setDeal_time(format.format(new Date(System.currentTimeMillis())));
						model.setFeed_back("");
						model.setReply("您好！这里是流量加油站客服中心，有问题请在这里反馈。");
						model_list.add(model);
					}
					adapter.notifyDataSetChanged();
					suggest_listview.setSelection(model_list.size()-1);
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(SuggestActivity_New.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(SuggestActivity_New.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}				
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(SuggestActivity_New.this);
						
						CommonManager commonManager=GetWebDate.getHessionFactiory(SuggestActivity_New.this).create(CommonManager.class, currentUsedUrl+"/hessian/commonManager", getClassLoader());
						Map[] map=commonManager.feedBackList(Long.parseLong(list.get(0)), 0, 100);
						if(map==null) {
							m.obj=new Map[]{};
						}
						else {
							m.obj=map;
						}
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.obj=null;
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
						m.obj=null;
					}
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	public void feedBack() {
		
		if(suggest_content.getText().toString().trim().equals("")) {
			showCustomToast(getResources().getString(R.string.suggest_null));
			return ;
		}
		
		pd=ProgressDialog.show(SuggestActivity_New.this, getResources().getString(R.string.tishi), getResources().getString(R.string.tishi_loading));
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				pd.dismiss();
				if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.suggest_fail));
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					//自动反馈
					SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					FeedBackModel model=new FeedBackModel();
					model.setGenerate_time(format.format(new Date(System.currentTimeMillis())));
					model.setDeal_time("");
					model.setFeed_back(suggest_content.getText().toString());
					model.setReply("");
					model_list.add(model);
					
					adapter.notifyDataSetChanged();
					suggest_listview.setSelection(model_list.size()-1);
					showCustomToast(getResources().getString(R.string.suggest_comp));
					suggest_content.setText("");
				}				
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(SuggestActivity_New.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(SuggestActivity_New.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(SuggestActivity_New.this);
						
						CommonManager commonManager=GetWebDate.getHessionFactiory(SuggestActivity_New.this).create(CommonManager.class, currentUsedUrl+"/hessian/commonManager", getClassLoader());
						int result=commonManager.feedBack(Long.parseLong(list.get(0)), suggest_content.getText().toString());
						m.what=result;
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter=new IntentFilter();
		filter.addAction(JPushReceiver.refreshSuggest);
		registerReceiver(receiver, filter);
		((GasStationApplication) getApplicationContext()).isNewSuggest=false;
	}
	
	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println(intent.getAction());
			getSuggestList();
			((GasStationApplication) getApplicationContext()).isNewSuggest=false;
		}};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}
			
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(SuggestActivity_New.this);
		((GasStationApplication) getApplicationContext()).isNewSuggest=false;
	}
}
