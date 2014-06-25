package com.linkage.gas_station.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.BindInfoModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.FftManager;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;

public class LifeManageActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	
	TextView life_namager_add=null;
	DragSortListView life_namager_list=null;
	SimpleAdapter adapter=null;
	ArrayList<HashMap<String, Object>> uilist=null;
	ArrayList<BindInfoModel> model_list=null;
	
	int currentItem=-1;
	boolean isDoSomething=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_life_manager);
		
		((GasStationApplication) getApplication()).tempActivity.add(LifeManageActivity.this);
		
		currentItem=getIntent().getExtras().getInt("currentItem");
		uilist=new ArrayList<HashMap<String, Object>>();
		model_list=new ArrayList<BindInfoModel>();
		
		init();
	}
	
	public void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isDoSomething) {
					Intent intent=getIntent();
					Bundle bundle=new Bundle();
					bundle.putInt("num", currentItem);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);					
				}
				finish();
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		switch(currentItem) {
		case 0:
			title_name.setText("水费账号管理");		
			break;
		case 1:
			title_name.setText("电费账号管理");
			break;
		case 2:
			title_name.setText("燃气费账号管理");
			break;
		}
		
		life_namager_add=(TextView) findViewById(R.id.life_namager_add);
		life_namager_add.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(LifeManageActivity.this, LifeAddActivity.class);
				Bundle bundle=new Bundle();
				bundle.putBoolean("permitAll", false);
				bundle.putInt("num", currentItem);
				intent.putExtras(bundle);
				startActivityForResult(intent, 600);
			}});
		life_namager_list=(DragSortListView) findViewById(R.id.life_namager_list);
		adapter=new SimpleAdapter(this, uilist, R.layout.adapter_lifemanage, new String[]{"name"}, new int[]{R.id.lifemanage_num});
		life_namager_list.setAdapter(adapter);
		life_namager_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(LifeManageActivity.this).setTitle("提示").setMessage("确定删除该条账户？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int arg0) {
						// TODO Auto-generated method stub

						isDoSomething=true;
						addBindInfo(position);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).show();
			}
		});
		life_namager_list.setRemoveListener(new RemoveListener() {
			
			@Override
			public void remove(final int which) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(LifeManageActivity.this).setTitle("提示").setMessage("确定删除该条账户？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int arg0) {
						// TODO Auto-generated method stub

						isDoSomething=true;
						addBindInfo(which);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						adapter.notifyDataSetChanged();
					}
				}).show();
				
			}
		});
		
		loadOpenService();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK&&requestCode==600) {
			loadOpenService();
			isDoSomething=true;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(isDoSomething) {
			Intent intent=getIntent();
			Bundle bundle=new Bundle();
			bundle.putInt("num", currentItem);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 删除账户
	 */
	public void addBindInfo(final int position) {
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
						showCustomToast("删除账户成功");
						uilist.remove(position);
						model_list.remove(position);
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
				adapter.notifyDataSetChanged();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(LifeManageActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LifeManageActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						
						ArrayList<String> list=Util.getUserInfo(LifeManageActivity.this);
						FftManager fftManager=GetWebDate.getHessionFactiory(LifeManageActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
						String type="";
						switch(currentItem) {
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
						Map map=fftManager.mofidyBindInfo(list.get(1), Long.parseLong(list.get(0)), type, model_list.get(position).getBuss_type_id(), model_list.get(position).getAcc_nbr(), 2);
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
	
	public void loadOpenService() {
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
						model_list.clear();
						uilist.clear();
						Object[] sdqfList=null;
						switch(currentItem) {
						case 0:
							sdqfList=(Object[]) map.get("sfList");							
							break;
						case 1:
							sdqfList=(Object[]) map.get("dfList");
							break;
						case 2:
							sdqfList=(Object[]) map.get("rqList");
							break;
						}
						for(int i=0;i<sdqfList.length;i++) {
							Map map_sfList=(Map) sdqfList[i];
							
							HashMap<String, Object> map_show=new HashMap<String, Object>();
							map_show.put("name", map_sfList.get("acc_nbr").toString());
							uilist.add(map_show);
							
							BindInfoModel model=new BindInfoModel();
							model.setAcc_nbr(map_sfList.get("acc_nbr").toString());
							model.setAcct_name(map_sfList.get("acct_name").toString());
							model.setAgent_code(map_sfList.get("agent_code").toString());
							model.setArea_code(map_sfList.get("area_code").toString());
							model.setBuss_type_id(map_sfList.get("buss_type_id").toString());
							model.setTrans_code(map_sfList.get("trans_code").toString());
							model_list.add(model);
						}
						adapter.notifyDataSetChanged();
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(LifeManageActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LifeManageActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LifeManageActivity.this);
						
						FftManager fftManager=GetWebDate.getHessionFactiory(LifeManageActivity.this).create(FftManager.class, currentUsedUrl+"/hessian/fftManager", getClassLoader());
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		((GasStationApplication) getApplication()).tempActivity.remove(LifeManageActivity.this);
	}

}
