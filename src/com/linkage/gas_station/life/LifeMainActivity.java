package com.linkage.gas_station.life;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.LifeModel;
import com.linkage.gas_station.model.LifePhoneModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.FftManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

public class LifeMainActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	ImageView title_refresh=null;
	ProgressBar title_refresh_progress=null;
	
	RelativeLayout life_water_layout=null;
	TextView life_water_num=null;
	ImageView life_water_zero=null;
	RelativeLayout life_ele_layout=null;
	TextView life_ele_num=null;
	ImageView life_ele_zero=null;
	RelativeLayout life_fire_layout=null;
	TextView life_fire_num=null;
	ImageView life_fire_zero=null;
	LinearLayout life_include_layout=null;
	LinearLayout life_permit_layout=null;
	
	//服务绑定标志位
	boolean[] isOpenService={false, false, false};
	//水电煤缓存
	ArrayList<LifePhoneModel> water_lifeModel_list=null;
	ArrayList<LifePhoneModel> ele_lifeModel_list=null;
	ArrayList<LifePhoneModel> fire_lifeModel_list=null;
	//当前选中的item
	int currentItem=-1;
	//刷新使用缓存view
	TextView life_with_data_submit_temp=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lifemain);
		
		((GasStationApplication) getApplication()).tempActivity.add(LifeMainActivity.this);
		
		water_lifeModel_list=new ArrayList<LifePhoneModel>();
		ele_lifeModel_list=new ArrayList<LifePhoneModel>();
		fire_lifeModel_list=new ArrayList<LifePhoneModel>();
		
		init();
	}
	
	public void init() {
		life_permit_layout=(LinearLayout) findViewById(R.id.life_permit_layout);
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.fft));
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(life_permit_layout.getVisibility()==View.INVISIBLE) {
					yzfQry();
				}
				else {
					if(currentItem==-1) {
						isOpenService();
					}
					else {
						loadOneItemData(currentItem, true);
					}
				}
			}});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);
		
		life_water_layout=(RelativeLayout) findViewById(R.id.life_water_layout);
		life_water_layout.setOnClickListener(rel_lis);
		life_water_num=(TextView) findViewById(R.id.life_water_num);
		life_water_zero=(ImageView) findViewById(R.id.life_water_zero);
		life_ele_layout=(RelativeLayout) findViewById(R.id.life_ele_layout);
		life_ele_layout.setOnClickListener(rel_lis);
		life_ele_num=(TextView) findViewById(R.id.life_ele_num);
		life_ele_zero=(ImageView) findViewById(R.id.life_ele_zero);
		life_fire_layout=(RelativeLayout) findViewById(R.id.life_fire_layout);
		life_fire_layout.setOnClickListener(rel_lis);
		life_fire_num=(TextView) findViewById(R.id.life_fire_num);
		life_fire_zero=(ImageView) findViewById(R.id.life_fire_zero);
		life_include_layout=(LinearLayout) findViewById(R.id.life_include_layout);
		
		yzfQry();
	}
	
	RelativeLayout.OnClickListener rel_lis=new RelativeLayout.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.life_water_layout:
				if(isOpenService[0]) {
					loadOneItemData(0, false);
				}
				else {
					Intent intent=new Intent(LifeMainActivity.this, LifeAddActivity.class);
					Bundle bundle=new Bundle();
					bundle.putBoolean("permitAll", false);
					bundle.putInt("num", 0);
					intent.putExtras(bundle);
					startActivityForResult(intent, 200);
				}
				break;
			case R.id.life_ele_layout:
				if(isOpenService[1]) {
					loadOneItemData(1, false);
				}
				else {
					Intent intent=new Intent(LifeMainActivity.this, LifeAddActivity.class);
					Bundle bundle=new Bundle();
					bundle.putBoolean("permitAll", false);
					bundle.putInt("num", 1);
					intent.putExtras(bundle);
					startActivityForResult(intent, 200);
				}
				break;
			case R.id.life_fire_layout:
				if(isOpenService[2]) {
					loadOneItemData(2, false);
				}
				else {
					Intent intent=new Intent(LifeMainActivity.this, LifeAddActivity.class);
					Bundle bundle=new Bundle();
					bundle.putBoolean("permitAll", false);
					bundle.putInt("num", 2);
					intent.putExtras(bundle);
					startActivityForResult(intent, 200);
				}
				break;
			}
		}};
		
	public void yzfQry() {
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map)msg.obj;
					if(map.get("result").toString().equals("1")) {
						isOpenService();
					}
					else {
						new AlertDialog.Builder(LifeMainActivity.this).setTitle("提示").setMessage(map.get("comments").toString()).setNegativeButton("取消", new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								finish();
							}}).setPositiveButton("立即开通", new DialogInterface.OnClickListener(){
	
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								yzfReg();
							}}).show();
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(LifeMainActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LifeMainActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LifeMainActivity.this);
						
						FftManager fftManager=GetWebDate.getHessionFactiory(LifeMainActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
						Map map=fftManager.yzfQry(Long.parseLong(list.get(0)), list.get(1));
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
	
	/**
	 * //开户 
	 */
	public void yzfReg() {
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
						showCustomToast("开户成功");
						isOpenService();
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(LifeMainActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LifeMainActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LifeMainActivity.this);
						
						FftManager fftManager=GetWebDate.getHessionFactiory(LifeMainActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
						Map map=fftManager.yzfReg(Long.parseLong(list.get(0)), list.get(1));
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
	
	public void isOpenService() {
		life_permit_layout.setVisibility(View.VISIBLE);
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg); 
				dismissProgressDialog();
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
				
				if(msg.what==1) {
					Map map=(Map)msg.obj;
					if(map.get("result").toString().equals("1")) {
						Object[] sfList=(Object[]) map.get("sfList");
						if(sfList.length>0) {
							isOpenService[0]=true;
						}
						else {
							isOpenService[0]=false;
						}
						Object[] dfList=(Object[]) map.get("dfList");
						if(dfList.length>0) {
							isOpenService[1]=true;
						}
						else {
							isOpenService[1]=false;
						}
						Object[] rqList=(Object[]) map.get("rqList");
						if(rqList.length>0) {
							isOpenService[2]=true;
						}
						else {
							isOpenService[2]=false;
						}
					}
					else {
						showCustomToast(map.get("comments").toString());
						isOpenService[0]=false;
						isOpenService[1]=false;
						isOpenService[2]=false;
					}
					if(!isOpenService[0]&&!isOpenService[1]&&!isOpenService[2]) {
						allNoneRefresh();
					}
					else {
						int showItem=showItem();					
						allRefresh();					
						loadOneItemData(showItem, true);
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(LifeMainActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LifeMainActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LifeMainActivity.this);
						
						FftManager fftManager=GetWebDate.getHessionFactiory(LifeMainActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
						Map map=fftManager.getBindSummary(list.get(1), Long.parseLong(list.get(0)));
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
	
	/**
	 * 加载当前标签下的数据
	 * @param num
	 * @param isRefresh
	 */
	public void loadOneItemData(final int pos_num, final boolean isRefreshNewData) {						
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();	
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);			

				if(msg.what==1) {
					life_include_layout.removeAllViews();
					switch(pos_num) {
					case 0:
						water_lifeModel_list.clear();
						break;
					case 1:
						ele_lifeModel_list.clear();
						break;
					case 2:
						fire_lifeModel_list.clear();
						break;
					}
					Map map=(Map)msg.obj; 

					int count=0;
					Object[] sdmList=null;
					switch(pos_num) {
					case 0:
						sdmList=(Object[]) map.get("sfList");
						break;
					case 1:
						sdmList=(Object[]) map.get("dfList");
						break;
					case 2:
						sdmList=(Object[]) map.get("rqList");
						break;
					}
					if(sdmList==null) {
						//全部删除之后无数据的刷新
						isOpenService[pos_num]=false;
						allNoneRefresh();
						currentItem=-1;
					}
					else {
						count=sdmList.length;						
						if(count>0) {
							isOpenService[pos_num]=true;
							for(int i=0;i<count;i++) {
								Map sdmList_map=(Map) sdmList[i];
								LifePhoneModel model_=new LifePhoneModel();
								model_.setPhone(sdmList_map.get("acc_nbr").toString());
								model_.setId(sdmList_map.get("buss_type_id").toString());
								if(!sdmList_map.get("owe").toString().equals("0")) {
									for(int j=0;j<1;j++) {
										LifeModel model=new LifeModel();
										model.setChecked(true);
										model.setOwe(sdmList_map.get("owe").toString());	
										model.setId(sdmList_map.get("buss_type_id").toString());
										model.setParentPhone(sdmList_map.get("acc_nbr").toString());
										model_.addModel(model);
									}
								}								
								switch(pos_num) {
								case 0:
									water_lifeModel_list.add(model_);
									break;
								case 1:
									ele_lifeModel_list.add(model_);
									break;
								case 2:
									fire_lifeModel_list.add(model_);
									break;
								}
							}
							
							allRefresh();
							switch(pos_num) {
							case 0:
								life_water_layout.setBackgroundResource(R.drawable.life_water_select);							
								life_include_layout.addView(loadLifeWithOneItem(0));
								break;
							case 1:
								life_ele_layout.setBackgroundResource(R.drawable.life_ele_select);
								life_include_layout.addView(loadLifeWithOneItem(1));
								break;
							case 2:
								life_fire_layout.setBackgroundResource(R.drawable.life_fire_select);
								life_include_layout.addView(loadLifeWithOneItem(2));
								break;
							}
						}
						else {
							//全部删除之后的刷新
							isOpenService[pos_num]=false;
							int showItem=showItem();	
							if(showItem!=-1) {
								allRefresh();					
								loadOneItemData(showItem, true);
							}
							else {
								allNoneRefresh();
								currentItem=-1;
							}
						}
					}
					
				}				
				else if(msg.what==2) {
					life_include_layout.removeAllViews();
					allRefresh();
					switch(pos_num) {
					case 0:
						life_water_layout.setBackgroundResource(R.drawable.life_water_select);
						life_include_layout.addView(loadLifeWithOneItem(0));
						break;
					case 1:
						life_ele_layout.setBackgroundResource(R.drawable.life_ele_select);
						life_include_layout.addView(loadLifeWithOneItem(1));
						break;
					case 2:
						life_fire_layout.setBackgroundResource(R.drawable.life_fire_select);
						life_include_layout.addView(loadLifeWithOneItem(2));
						break;
					}
				}
				else {
					allRefresh();
					switch(pos_num) {
					case 0:
						life_water_layout.setBackgroundResource(R.drawable.life_water_select);
						break;
					case 1:
						life_ele_layout.setBackgroundResource(R.drawable.life_ele_select);
						break;
					case 2:
						life_fire_layout.setBackgroundResource(R.drawable.life_fire_select);
						break;
					}
				}
				if(isOpenService[pos_num]) {
					currentItem=pos_num;
				}
 			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				LinkedList<String> wholeUrl=Util.getWholeUrl(LifeMainActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LifeMainActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				switch(pos_num) {
				case 0:
					if(!isRefreshNewData&&water_lifeModel_list.size()>0) {
						m.what=2;
					}
					else {
						while(flag) {
							try {
								ArrayList<String> list=Util.getUserInfo(LifeMainActivity.this);
								
								FftManager fftManager=GetWebDate.getHessionFactiory(LifeMainActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
								Map map=fftManager.getBindSummaryAndOwe(list.get(1), Long.parseLong(list.get(0)), "SF");
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
					}
					break;
				case 1:
					if(!isRefreshNewData&&ele_lifeModel_list.size()>0) {
						m.what=2;
					}
					else {
						while(flag) {
							try {
								ArrayList<String> list=Util.getUserInfo(LifeMainActivity.this);
								
								FftManager fftManager=GetWebDate.getHessionFactiory(LifeMainActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
								Map map=fftManager.getBindSummaryAndOwe(list.get(1), Long.parseLong(list.get(0)), "DF");
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
					}
					break;
				case 2:
					if(!isRefreshNewData&&fire_lifeModel_list.size()>0) {
						m.what=2;
					}
					else {
						while(flag) {
							try {
								ArrayList<String> list=Util.getUserInfo(LifeMainActivity.this);
								
								FftManager fftManager=GetWebDate.getHessionFactiory(LifeMainActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
								Map map=fftManager.getBindSummaryAndOwe(list.get(1), Long.parseLong(list.get(0)), "RQ");
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
					}
					break;
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	/**
	 * 最先加载的页面item
	 * @return
	 */
	public int showItem() {
		if(isOpenService[0]) {
			return 0;
		}
		if(!isOpenService[0]&&isOpenService[1]) {
			return 1;
		}
		if(!isOpenService[0]&&!isOpenService[1]&&isOpenService[2]) {
			return 2;
		}
		return -1;
	}
	
	public void allRefresh() {
		if(isOpenService[0]) {
			life_water_layout.setBackgroundResource(R.drawable.life_water_sel);
			if(getNum(water_lifeModel_list)==0) {
				life_water_num.setVisibility(View.INVISIBLE);
				life_water_zero.setVisibility(View.VISIBLE);
			}
			else {
				life_water_num.setText(""+getNum(water_lifeModel_list));
				life_water_num.setVisibility(View.VISIBLE);
				life_water_zero.setVisibility(View.INVISIBLE);
			}			
		}
		else {
			life_water_layout.setBackgroundResource(R.drawable.life_water_no);
			life_water_num.setVisibility(View.INVISIBLE);
			life_water_zero.setVisibility(View.INVISIBLE);
		}					
		if(isOpenService[1]) {
			life_ele_layout.setBackgroundResource(R.drawable.life_ele_sel);
			if(getNum(ele_lifeModel_list)==0) {
				life_ele_num.setVisibility(View.INVISIBLE);
				life_ele_zero.setVisibility(View.VISIBLE);
			}
			else {
				life_ele_num.setText(""+getNum(ele_lifeModel_list));
				life_ele_num.setVisibility(View.VISIBLE);
				life_ele_zero.setVisibility(View.INVISIBLE);
			}
		}
		else {
			life_ele_layout.setBackgroundResource(R.drawable.life_ele_no);
			life_ele_num.setVisibility(View.INVISIBLE);
			life_ele_zero.setVisibility(View.INVISIBLE);
		}					
		if(isOpenService[2]) {
			life_fire_layout.setBackgroundResource(R.drawable.life_fire_sel);
			if(getNum(fire_lifeModel_list)==0) {
				life_fire_num.setVisibility(View.INVISIBLE);
				life_fire_zero.setVisibility(View.VISIBLE);
			}
			else {
				life_fire_num.setText(""+getNum(fire_lifeModel_list));
				life_fire_num.setVisibility(View.VISIBLE);
				life_fire_zero.setVisibility(View.INVISIBLE);
			}
		}
		else {
			life_fire_layout.setBackgroundResource(R.drawable.life_fire_no);
			life_fire_num.setVisibility(View.INVISIBLE);
			life_fire_zero.setVisibility(View.INVISIBLE);
		}
	}
	
	public void allNoneRefresh() {
		life_include_layout.addView(loadLifeWithNone());
		life_water_layout.setBackgroundResource(R.drawable.life_water_no);
		life_water_num.setVisibility(View.INVISIBLE);
		life_water_zero.setVisibility(View.INVISIBLE);
		life_ele_layout.setBackgroundResource(R.drawable.life_ele_no);
		life_ele_num.setVisibility(View.INVISIBLE);
		life_ele_zero.setVisibility(View.INVISIBLE);
		life_fire_layout.setBackgroundResource(R.drawable.life_fire_no);
		life_fire_num.setVisibility(View.INVISIBLE);
		life_fire_zero.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 没有任何绑定信息时候记录
	 * @return
	 */
	public View loadLifeWithNone() {
		life_include_layout.removeAllViews();
		View view=LayoutInflater.from(LifeMainActivity.this).inflate(R.layout.activity_life_no_all, null);
		ImageView life_no_all=(ImageView) view.findViewById(R.id.life_no_all);
		life_no_all.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(LifeMainActivity.this, LifeAddActivity.class);
				Bundle bundle=new Bundle();
				bundle.putBoolean("permitAll", true);
				intent.putExtras(bundle);
				startActivityForResult(intent, 100);
			}});
		return view;
	}
	
	public View loadLifeWithOneItem(final int num) {
		life_include_layout.removeAllViews();
		View view=LayoutInflater.from(LifeMainActivity.this).inflate(R.layout.activity_life_with_data, null);
		TextView life_manager=(TextView) view.findViewById(R.id.life_manager);
		life_manager.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(LifeMainActivity.this, LifeManageActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt("currentItem", currentItem);
				intent.putExtras(bundle);
				startActivityForResult(intent, 200);
			}});
		final LinearLayout life_fire_with_data_include_layout=(LinearLayout) view.findViewById(R.id.life_fire_with_data_include_layout);
		ArrayList<LifePhoneModel> lifeModel_list=null;
		switch(num) {
		case 0:
			lifeModel_list=water_lifeModel_list;
			break;
		case 1:
			lifeModel_list=ele_lifeModel_list;
			break;
		case 2:
			lifeModel_list=fire_lifeModel_list;
			break;
		}
		for(int i=0;i<lifeModel_list.size();i++) {
			View view_title=LayoutInflater.from(LifeMainActivity.this).inflate(R.layout.activity_life_with_data_title, null);
			TextView life_fire_with_data_phone_type=(TextView) view_title.findViewById(R.id.life_fire_with_data_phone_type);
			switch(num) {
			case 0:
				life_fire_with_data_phone_type.setText("水费户号：");
				break;
			case 1:
				life_fire_with_data_phone_type.setText("电费户号：");
				break;
			case 2:
				life_fire_with_data_phone_type.setText("煤气费户号：");
				break;
			}
			TextView life_fire_with_data_phone=(TextView) view_title.findViewById(R.id.life_fire_with_data_phone);
			life_fire_with_data_phone.setText(lifeModel_list.get(i).getPhone());
			view_title.setTag(null);
			TextView life_fire_with_data_exist=(TextView) view_title.findViewById(R.id.life_fire_with_data_exist);
			if(lifeModel_list.get(i).getModel_list().size()==0) {
				life_fire_with_data_exist.setVisibility(View.VISIBLE);
			}
			else {
				life_fire_with_data_exist.setVisibility(View.GONE);
			}
			life_fire_with_data_include_layout.addView(view_title);
			for(int j=0;j<lifeModel_list.get(i).getModel_list().size();j++) {
				View view_sub=LayoutInflater.from(LifeMainActivity.this).inflate(R.layout.activity_life_with_data_subtitle, null);
				CheckBox life_fire_with_data_check=(CheckBox) view_sub.findViewById(R.id.life_fire_with_data_check);
				life_fire_with_data_check.setChecked(lifeModel_list.get(i).getModel_list().get(j).isChecked());
				TextView life_fire_with_data_subdesp=(TextView) view_sub.findViewById(R.id.life_fire_with_data_subdesp);
				switch(num) {
				case 0:
					life_fire_with_data_subdesp.setText("水费欠费"+Double.parseDouble(lifeModel_list.get(i).getModel_list().get(j).getOwe())/100+"元");
					break;
				case 1:
					life_fire_with_data_subdesp.setText("电费欠费"+Double.parseDouble(lifeModel_list.get(i).getModel_list().get(j).getOwe())/100+"元");
					break;
				case 2:
					life_fire_with_data_subdesp.setText("燃气费欠费"+Double.parseDouble(lifeModel_list.get(i).getModel_list().get(j).getOwe())/100+"元");
					break;
				}
				view_sub.setTag(lifeModel_list.get(i).getModel_list().get(j));
				life_fire_with_data_include_layout.addView(view_sub);
			}
		}
		final TextView life_with_data_submit=(TextView) view.findViewById(R.id.life_with_data_submit);
		life_with_data_submit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//代付款总额
				int totalNum_temp=0;
				
				life_with_data_submit.setEnabled(false);
				life_with_data_submit.setClickable(false);
				String typeAndidAndOwes_temp="";
				for(int i=0;i<life_fire_with_data_include_layout.getChildCount();i++) {
					if(life_fire_with_data_include_layout.getChildAt(i).getTag()!=null) {
						LifeModel model=(LifeModel)life_fire_with_data_include_layout.getChildAt(i).getTag();
						View view_temp=life_fire_with_data_include_layout.getChildAt(i);
						CheckBox life_fire_with_data_check=(CheckBox) view_temp.findViewById(R.id.life_fire_with_data_check);
						if(life_fire_with_data_check.isChecked()) {
							typeAndidAndOwes_temp+=model.getId()+":"+model.getParentPhone()+":"+model.getOwe()+";";
							totalNum_temp+=Integer.parseInt(model.getOwe());
						}						
					}
				}
				if(!typeAndidAndOwes_temp.equals("")) {
					life_with_data_submit_temp=life_with_data_submit;
					
					Intent intent=new Intent(LifeMainActivity.this, LifePayActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("typeAndidAndOwes", typeAndidAndOwes_temp);
					bundle.putInt("num", num);
					bundle.putInt("totalNum", totalNum_temp);
					intent.putExtras(bundle);
					startActivityForResult(intent, 300);
				}
				else {
					showCustomToast("请您选择至少一条欠费账单");
					life_with_data_submit.setEnabled(true);
					life_with_data_submit.setClickable(true);
				}
			}});
		return view;
	}
	
	/**
	 * 计算每个元素的个数
	 * @param model_list
	 * @return
	 */
	public int getNum(ArrayList<LifePhoneModel> model_list) {
		int temp=0;
		for(int i=0;i<model_list.size();i++) {
			for(int j=0;j<model_list.get(i).getModel_list().size();j++) {
				temp++;
			}
		}
		return temp;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK) {
			//单一类型存在后添加|管理界面返回
			if(requestCode==200) {
				int num=data.getExtras().getInt("num");
				isOpenService[num]=true;
				loadOneItemData(num, true);
			}
			//初始化无数据时候添加
			else if(requestCode==100) {
				isOpenService();
			}
			else if(requestCode==300) {
				int num=data.getExtras().getInt("num");
				loadOneItemData(num, true);
				
				life_with_data_submit_temp.setEnabled(true);
				life_with_data_submit_temp.setClickable(true);
			}
		}
		else {
			if(requestCode==300) {
				life_with_data_submit_temp.setEnabled(true);
				life_with_data_submit_temp.setClickable(true);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(LifeMainActivity.this);
	}
	
}
