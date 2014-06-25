package com.linkage.gas_station.bbs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.BbsForumIdModel;
import com.linkage.gas_station.model.BbsForumModel;
import com.linkage.gas_station.myview.XListView;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.BbsManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

public class BBSDetailActivity extends BaseActivity implements XListView.IXListViewListener {
	
	RelativeLayout main_title_layout=null;
	ImageView title_refresh=null;
	TextView title_name=null;
	ImageView title_back=null;
	
	TextView bbs_mainno=null;
	private XListView bbs_list_view=null;
	BBSCommentsAdapter adapter=null;
	ArrayList<BbsForumIdModel> model_list=null;
	BbsForumModel model=null;
	
	//加载状态 -1:下拉刷新  0：普通刷新  1：上拉刷新
	int loadFlag=0;
	//当前页面加载数
	int totalLoad=15;
	//当前页码
	int page=1;
	boolean isLOading=false;
	
	long forumId=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bbsmain_detail);
		
		model=(BbsForumModel) getIntent().getExtras().getSerializable("model");
		forumId=getIntent().getExtras().getLong("forumId");
		model_list=new ArrayList<BbsForumIdModel>();
		model_list.add(null);
		
		((GasStationApplication) getApplication()).tempActivity.add(BBSDetailActivity.this);
		
		init();
	}
	
	public void init() {
		main_title_layout=(RelativeLayout) findViewById(R.id.main_title_layout);
		main_title_layout.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("加油站论坛");
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setImageResource(R.drawable.reply_image);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(BBSDetailActivity.this, BBSSendActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("type", "replyFroum");
				bundle.putLong("forumId", forumId);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1001);
			}});
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		bbs_mainno=(TextView) findViewById(R.id.bbs_mainno);
		bbs_list_view=(XListView) findViewById(R.id.bbs_list_view);
		bbs_list_view.setPullRefreshEnable(true);
		bbs_list_view.setPullLoadEnable(false);
		bbs_list_view.setXListViewListener(this);
		bbs_list_view.setRefreshTime(getTime());
		adapter=new BBSCommentsAdapter(model_list, BBSDetailActivity.this, model);
		bbs_list_view.setAdapter(adapter);
		
		getBbsForumList();
	}
	
	private void onLoad() {
		bbs_list_view.stopRefresh();
		bbs_list_view.stopLoadMore();
		bbs_list_view.setRefreshTime(getTime());
    }
	
	private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }
	
	public void getBbsForumList() {
		showProgressDialog(R.string.tishi_loading);
		isLOading=true;
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				isLOading=false;
				if(msg.what==1) {
					Object[] map_array=(Object[]) msg.obj;	
					if(map_array.length==0) {
						if(model_list.size()==1) {
							bbs_mainno.setVisibility(View.VISIBLE);
						}
						else {
							bbs_mainno.setVisibility(View.GONE);
						}
					}
					else {
						ArrayList<BbsForumIdModel> model_list_temp=new ArrayList<BbsForumIdModel>();
						for(int i=0;i<map_array.length;i++) {
							HashMap map=(HashMap) map_array[i];
							BbsForumIdModel model=new BbsForumIdModel();
							model.setForum_id(Long.parseLong(map.get("forum_id").toString()));
							model.setGenerate_time(map.get("generate_time").toString());
							model.setNote_content(map.get("note_content")==null?"":map.get("note_content").toString());
							model.setNote_id(Long.parseLong(map.get("note_id").toString()));
							model.setPhone_num(map.get("phone_num").toString());
							model.setRn(Integer.parseInt(map.get("rn").toString()));
							model.setArea_name(map.get("area_name").toString());
							model.setPhoneType(map.get("phone_type")==null?2:Integer.parseInt(map.get("phone_type").toString()));
							model_list_temp.add(model);
						}
						if(loadFlag==-1) {
							model_list.clear();
							model_list.add(null);
							onLoad();
						}
						if(model_list_temp.size()<totalLoad) {
							bbs_list_view.setPullLoadEnable(false);
						}
						else {
							bbs_list_view.setPullLoadEnable(true);							
						}
						model_list.addAll(model_list_temp);
						adapter.notifyDataSetChanged();
						if(model_list.size()==1) {
							bbs_mainno.setVisibility(View.VISIBLE);
						}
						else {
							bbs_mainno.setVisibility(View.GONE);
						}
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");	
					if(model_list.size()==1) {
						bbs_mainno.setVisibility(View.VISIBLE);
					}
					else {
						bbs_mainno.setVisibility(View.GONE);
					}	
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
					if(model_list.size()==1) {
						bbs_mainno.setVisibility(View.VISIBLE);
					}
					else {
						bbs_mainno.setVisibility(View.GONE);
					}
				}
				onLoad();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(BBSDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BBSDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						BbsManager bbsManager=GetWebDate.getHessionFactiory(BBSDetailActivity.this).create(BbsManager.class, currentUsedUrl+"/hessian/bbsManager", getClassLoader());
						HashMap[] map=bbsManager.getBbsForumById(forumId, (page-1)*totalLoad, totalLoad);
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
	public void onRefresh() {
		// TODO Auto-generated method stub
		if(isLOading) {
			return;
		}
		page=1;
		loadFlag=-1;
		getBbsForumList();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if(isLOading) {
			return;
		}
		page+=1;
		loadFlag=1;
		getBbsForumList();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1001&&resultCode==RESULT_OK) {
			model.setReply_times(model.getReply_times()+1);
			page=1;
			loadFlag=-1;
			bbs_list_view.setStart();
			getBbsForumList();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(BBSDetailActivity.this);
	}
}
