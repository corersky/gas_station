package com.linkage.gas_station.bbs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.BbsManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

public class BBSSendActivity extends BaseActivity {
	
	ImageView title_refresh=null;
	TextView title_name=null;
	ImageView title_back=null;
	
	LinearLayout bbs_send_title_layout=null;
	EditText bbs_send_title=null;
	EditText bbs_send_content=null;
	TextView hasnum=null;
	
	Long forumId=0l;
	//字符限制个数
	int num=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bbssend);
		
		init();
	}
	
	public void init() {

		hasnum=(TextView) findViewById(R.id.hasnum);
		title_name=(TextView) findViewById(R.id.title_name);
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		if(getIntent().getExtras().getString("type").equals("replyFroum")) {
			title_name.setText("回帖");
			title_refresh.setImageResource(R.drawable.reply_image);
			num=50;
			hasnum.setText("50");
		}
		else {
			title_name.setText("发帖");
			title_refresh.setImageResource(R.drawable.fatie_image);
			num=200;
			hasnum.setText("200");
		}
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getIntent().getExtras().getString("type").equals("replyFroum")) {
					replyFroum();
				}
				else {
					saveNewFroum();
				}
			}});
		bbs_send_title_layout=(LinearLayout) findViewById(R.id.bbs_send_title_layout);
		bbs_send_title=(EditText) findViewById(R.id.bbs_send_title);
		bbs_send_title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
		if(getIntent().getExtras().getString("type").equals("replyFroum")) {
			bbs_send_title_layout.setVisibility(View.GONE);
		}
		else {
			bbs_send_title_layout.setVisibility(View.VISIBLE);
		}
		bbs_send_content=(EditText) findViewById(R.id.bbs_send_content);
		bbs_send_content.addTextChangedListener(new TextWatcher() { 
            private CharSequence temp; 
            private int selectionStart; 
            private int selectionEnd; 
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { 
            
            } 
            public void onTextChanged(CharSequence s, int start, int before, int count) { 
                 temp = s; 
            } 
            public void afterTextChanged(Editable s) { 
                int number = num - s.length(); 
                hasnum.setText("" + number); 
                selectionStart = bbs_send_content.getSelectionStart(); 
                selectionEnd = bbs_send_content.getSelectionEnd(); 
                if (temp.length() > num) { 
                    s.delete(selectionStart - 1, selectionEnd); 
                    int tempSelection = selectionEnd; 
                    bbs_send_content.setText(s); 
                    bbs_send_content.setSelection(tempSelection);//设置光标在最后 
                } 
            } 
		}); 
	}
	
	public void saveNewFroum() {
		
		if(bbs_send_title.getText().toString().equals("")||bbs_send_content.getText().toString().equals("")) {
			showCustomToast("请您输入完整的帖子信息");
			return ;
		}
			
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					showCustomToast(map.get("comments").toString());
					if(map.get("result").toString().equals("1")) {
						Intent intent=new Intent();
						intent.setAction("refresh");
						sendBroadcast(intent);
					}
					Intent in=getIntent();
					setResult(RESULT_OK, in);
					finish();
				}
				else if(msg.what==-1) {
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(BBSSendActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BBSSendActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(BBSSendActivity.this);
						
						BbsManager bbsManager=GetWebDate.getHessionFactiory(BBSSendActivity.this).create(BbsManager.class, currentUsedUrl+"/hessian/bbsManager", getClassLoader());
						long temp_time=(forumId==0?System.currentTimeMillis():forumId);
						forumId=temp_time;
						Map map=bbsManager.saveNewFroum(forumId, bbs_send_title.getText().toString(), bbs_send_content.getText().toString(), Long.parseLong(list.get(0)), 2);
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
	
	public void replyFroum() {
		if(bbs_send_content.getText().toString().equals("")) {
			showCustomToast("请您输入完整的帖子信息");
			return ;
		}
		showProgressDialog(R.string.tishi);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					showCustomToast(map.get("comments").toString());
					Intent in=getIntent();
					setResult(RESULT_OK, in);
					finish();
				}
				else if(msg.what==-1) {
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(BBSSendActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BBSSendActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(BBSSendActivity.this);
						
						BbsManager bbsManager=GetWebDate.getHessionFactiory(BBSSendActivity.this).create(BbsManager.class, currentUsedUrl+"/hessian/bbsManager", getClassLoader());
						long temp_time=(forumId==0?System.currentTimeMillis():forumId);
						forumId=temp_time;
						Map map=bbsManager.replyFroum(forumId, getIntent().getExtras().getLong("forumId"), bbs_send_content.getText().toString(), Long.parseLong(list.get(0)), 2);
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
}
