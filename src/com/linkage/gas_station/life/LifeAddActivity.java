package com.linkage.gas_station.life;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.FftManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

public class LifeAddActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	
	TextView life_add_phone=null;
	EditText life_add_num=null;
	ImageView life_add_type=null;
	TextView life_add_area=null;
	RelativeLayout life_add_choice_area=null;
	TextView life_add_comp=null;
	TextView life_add_submit=null;
	//手动选择的item
	int lastItem=0;
	String areaCode="";
	String choice_bus_type_id="";
	String[] str={"水费", "电费", "煤气费"};
	String[] area_str={"南京市", "无锡市", "徐州市", "常州市", "苏州市", "南通市", "连云港市", "淮安市", "盐城市", "扬州市", "镇江市", "泰州市", "宿迁市"};
	String[] area_province_code={"025", "0510", "0516", "0519", "0512", "0513", "0518", "0517", "0515", "0514", "0511", "0523", "0527"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lifeadd);
		
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
		title_name.setText(getResources().getString(R.string.fft_add));
		
		life_add_phone=(TextView) findViewById(R.id.life_add_phone);
		ArrayList<String> list=Util.getUserInfo(LifeAddActivity.this);
		life_add_phone.setText("翼支付账号："+list.get(0));
		life_add_num=(EditText) findViewById(R.id.life_add_num);
		life_add_type=(ImageView) findViewById(R.id.life_add_type);
		life_add_type.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getIntent().getExtras().getBoolean("permitAll")) {
					choice_type();
				}				
			}});
		life_add_area=(TextView) findViewById(R.id.life_add_area);
		life_add_choice_area=(RelativeLayout) findViewById(R.id.life_add_choice_area);
		life_add_choice_area.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				choice_area();
			}});
		life_add_comp=(TextView) findViewById(R.id.life_add_comp);
		life_add_submit=(TextView) findViewById(R.id.life_add_submit);
		life_add_submit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!areaCode.equals("")&&!choice_bus_type_id.equals("")&&!life_add_num.equals("")) {
					addBindInfo();	
				}
				else {
					showCustomToast("请选择地区以及相应的业务公司");
				}			
			}});
		
		if(!getIntent().getExtras().getBoolean("permitAll")) {
			switch(getIntent().getExtras().getInt("num")) {
			case 0:
				life_add_type.setImageResource(R.drawable.life_add_water);
				break;
			case 1:
				life_add_type.setImageResource(R.drawable.life_add_ele);
				break;
			case 2:
				life_add_type.setImageResource(R.drawable.life_add_fire);
				break;
			}
			lastItem=getIntent().getExtras().getInt("num");
		}
		else {
			lastItem=0;
			life_add_type.setImageResource(R.drawable.life_add_water);
		}
	}
	
	/**
	 * 水电煤类型选择
	 */
	public void choice_type() {
		life_add_area.setText("");
		life_add_comp.setText("");
		choice_bus_type_id="";
		areaCode="";
		final AlertDialog dlg = new AlertDialog.Builder(this).setItems(str, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				lastItem=which;
				switch(which) {
				case 0:
					life_add_type.setImageResource(R.drawable.life_add_water);
					break;
				case 1:
					life_add_type.setImageResource(R.drawable.life_add_ele);
					break;
				case 2:
					life_add_type.setImageResource(R.drawable.life_add_fire);
					break;
				}
			}
		}).create();
		dlg.show();
	}
	
	/**
	 * 区域选择
	 */
	public void choice_area() {
		final AlertDialog dlg = new AlertDialog.Builder(this).setItems(area_str, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				life_add_area.setText(area_str[which]);
				areaCode=area_province_code[which];
				getAreaSdm();
			}
		}).create();
		dlg.show();
	}
	
	/**
	 * 公司选择
	 * @param map
	 */
	public void choice_Comp(Map[] map) {
		final String[] type_descript=new String[map.length];
		final String[] bus_type_id=new String[map.length];
		for(int i=0;i<map.length;i++) {
			type_descript[i]=map[i].get("type_descript").toString();
			bus_type_id[i]=map[i].get("bus_type_id").toString();
		}
		final AlertDialog dlg = new AlertDialog.Builder(this).setItems(type_descript, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				life_add_comp.setText(type_descript[which]);
				choice_bus_type_id=bus_type_id[which];
			}
		}).create();
		dlg.show();
	}
	
	/**
	 * 按地区获取水、电、煤公司信息
	 */
	public void getAreaSdm() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map[] map=(Map[])msg.obj;
					if(map.length==0) {
						showCustomToast("该地区无支持相应业务的公司");
						life_add_area.setText("");
						life_add_comp.setText("");
						choice_bus_type_id="";
						areaCode="";
					}
					else {
						choice_Comp(map);
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
					life_add_area.setText("");
					life_add_comp.setText("");
					choice_bus_type_id="";
					areaCode="";
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(LifeAddActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LifeAddActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LifeAddActivity.this);
						
						FftManager fftManager=GetWebDate.getHessionFactiory(LifeAddActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
						String type="";
						switch(lastItem) {
						case 0:
							type="SF";
							break;
						case 1:
							type="DF";
							break;
						case 2:
							type="RQ";
							break;
						}
						Map[] map_array=fftManager.getAreaSdm(""+areaCode, type);
						m.obj=map_array;
						m.what=1;
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
	};
	
	/**
	 * 添加账户
	 */
	public void addBindInfo() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();	
				if(msg.what==1) {
					Map map=(Map)msg.obj; 
					if(map.get("result").toString().equals("1")) {
						showCustomToast("添加账户成功");
						Intent intent=getIntent();
						Bundle bundle=new Bundle();
						if(!getIntent().getExtras().getBoolean("permitAll")) {
							bundle.putInt("num", getIntent().getExtras().getInt("num"));
						}
						else {
							bundle.putInt("num", lastItem);
						}
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
						finish();
					}
					else {
						showCustomToast(map.get("comments").toString());
					}
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(LifeAddActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LifeAddActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LifeAddActivity.this);
						
						FftManager fftManager=GetWebDate.getHessionFactiory(LifeAddActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
						String type="";
						switch(lastItem) {
						case 0:
							type="SF";
							break;
						case 1:
							type="DF";
							break;
						case 2:
							type="RQ";
							break;
						}
						Map map=fftManager.mofidyBindInfo(list.get(1), Long.parseLong(list.get(0)), type, choice_bus_type_id, life_add_num.getText().toString(), 1);
						m.obj=map;
						m.what=1;
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
