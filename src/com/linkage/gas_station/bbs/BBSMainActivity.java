package com.linkage.gas_station.bbs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.BbsForumModel;
import com.linkage.gas_station.myview.XListView;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.BbsManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

public class BBSMainActivity extends BaseActivity implements XListView.IXListViewListener {
	
	private XListView bbs_list_view=null;
	BBSAdapter adapter=null;
	
	ArrayList<BbsForumModel> model_list=null;
	
	//加载状态 -1:下拉刷新  0：普通刷新  1：上拉刷新
	int loadFlag=0;
	//当前页面加载数
	int totalLoad=15;
	//当前页码
	int page=1;
	//是否正在加载
	boolean isLOading=false;
	//是否当前页面
	boolean isCurrentPage=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bbsmain);
		
		model_list=new ArrayList<BbsForumModel>();
		
		init();
		
		IntentFilter filter=new IntentFilter();
		filter.addAction("refresh");
		registerReceiver(receiver, filter);
	}
	
	public void init() {
		
		bbs_list_view=(XListView) findViewById(R.id.bbs_list_view);
		bbs_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(BBSMainActivity.this, BBSDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("forumId", model_list.get(position-1).getForum_id());
				bundle.putSerializable("model", model_list.get(position-1));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		bbs_list_view.setPullRefreshEnable(true);
		bbs_list_view.setPullLoadEnable(false);
		bbs_list_view.setXListViewListener(this);
		bbs_list_view.setRefreshTime(getTime());
		adapter=new BBSAdapter(model_list, BBSMainActivity.this);
		bbs_list_view.setAdapter(adapter);
		bbs_list_view.setStart();
		
		getBbsForumList();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if(isLOading) {
			return;
		}
		loadFlag=-1;
		page=1;
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
	
	private void onLoad() {
		bbs_list_view.stopRefresh();
		bbs_list_view.stopLoadMore();
		bbs_list_view.setRefreshTime(getTime());
    }
	
	private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }
	
	public void getBbsForumList() {
		isLOading=true;
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				isLOading=false;
				if(msg.what==1) {
					Object[] map_array=(Object[]) msg.obj;	
					if(map_array.length==0) {
						showCustomToast("暂无相关帖子信息");
					}
					else {
						ArrayList<BbsForumModel> model_list_temp=new ArrayList<BbsForumModel>();
						for(int i=0;i<map_array.length;i++) {
							BbsForumModel model=new BbsForumModel();
							HashMap map=(HashMap) map_array[i];
							model.setForum_content(map.get("forum_content")==null?"":map.get("forum_content").toString());
							model.setForum_id(Long.parseLong(map.get("forum_id").toString()));
							model.setForum_name(map.get("forum_name")==null?"":map.get("forum_name").toString());
							model.setForum_type(Integer.parseInt(map.get("forum_type").toString()));
							model.setGenerate_time(map.get("generate_time").toString());
							model.setIs_top(Integer.parseInt(map.get("is_top").toString()));
							model.setLast_modify_time(map.get("last_modify_time").toString());
							model.setPhone_num(map.get("phone_num").toString());
							model.setReply_times(Integer.parseInt(map.get("reply_times").toString()));
							model.setRn(Integer.parseInt(map.get("rn").toString()));
							model.setPhoneType(map.get("phone_type")==null?2:Integer.parseInt(map.get("phone_type").toString()));
							model_list_temp.add(model);
						}
						if(loadFlag==-1) {
							model_list.clear();
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
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");					
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				onLoad();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(BBSMainActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BBSMainActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						BbsManager bbsManager=GetWebDate.getHessionFactiory(BBSMainActivity.this).create(BbsManager.class, currentUsedUrl+"/hessian/bbsManager", getClassLoader());
						HashMap[] map=bbsManager.getBbsForumList(getIntent().getExtras().getInt("type"), (page-1)*totalLoad, totalLoad);
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
	
	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(getIntent().getExtras().getInt("type")==-1&&intent.getAction().equals("refresh")) {
				if(isLOading) {
					return;
				}
				if(isCurrentPage) {
					bbs_list_view.setStart();
				}				
				loadFlag=-1;
				page=1;
				getBbsForumList();
			}
		}};
		
	protected void onResume() {
		super.onResume();
		isCurrentPage=true;
	};
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isCurrentPage=false;
	}
	
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	};	
}
