package com.linkage.gas_station.main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.gonglve.GonglveActivity;
import com.linkage.gas_station.jiayou.JiayouActivity;
import com.linkage.gas_station.jpush.JPushReceiver;
import com.linkage.gas_station.more.MoreActivity;
import com.linkage.gas_station.util.AsyncImageLoad;
import com.linkage.gas_station.util.AsyncSingleImageLoad;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {
	
	TabHost host=null;
	RadioGroup main_group=null;
	ImageView suggest_new=null;
	
	static MainActivity instance=null;
	//初始号码
	long initPhoneNum=-1;
	
	public static MainActivity getInstance() {
		return instance;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tab_main);
		
		((GasStationApplication) MainActivity.this.getApplication()).isAppOpen=true;
	
		initPhoneNum=Long.parseLong(Util.getUserInfo(MainActivity.this).get(0));
		instance=MainActivity.this;

		//上传用户应用信息
		if(System.currentTimeMillis()-Util.getLoadAppTime(MainActivity.this)>1000*60*24*7) {
			Util.setLoadAppTime(MainActivity.this, System.currentTimeMillis());
			updatePackageInfo();
		}		
		checkNetwork();
		
		init();
	}
	
	public void init() {
		host=getTabHost();
		main_group=(RadioGroup) findViewById(R.id.main_group);
		suggest_new=(ImageView) findViewById(R.id.suggest_new);
		
		addTab("spec1", HomeActivity.class, R.drawable.home_gray);
		addTab("spec2", JiayouActivity.class, R.drawable.gastation_gray);
		addTab("spec3", GonglveActivity.class, R.drawable.sign_gray);
		addTab("spec4", MoreActivity.class, R.drawable.more_gray);
		host.setCurrentTab(0);
		((RadioButton) main_group.getChildAt(0)).setOnClickListener(new RadioButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(HomeActivity.getInstance()!=null) {
					HomeActivity.getInstance().jumpViewPager();
				}
			}});
		main_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch(checkedId) {
				case R.id.home:
					setRadioButton("spec1", R.drawable.home_blue, 0);
					break;
				case R.id.jiayou:
					((GasStationApplication) getApplicationContext()).jumpJiayouFrom=2;
					setRadioButton("spec2", R.drawable.gastation__blue, 1);
					break;
				case R.id.gonglve:
					setRadioButton("spec3", R.drawable.sign_blue, 2);
					break;
				case R.id.more:
					setRadioButton("spec4", R.drawable.more_blue, 3);
					break;
				}
			}
		});
		((RadioButton) main_group.getChildAt(0)).toggle();
		setRadioSource(R.drawable.home_blue, 0);
		
	}
	
	/**
	 * tabhost添加元素
	 * @param tag
	 * @param cls
	 * @param drawable
	 */
	public void addTab(String tag, Class<?> cls, int drawable) {
		TabSpec spec=host.newTabSpec(tag);
		spec.setContent(new Intent(MainActivity.this, cls));
		spec.setIndicator("", getResources().getDrawable(drawable));
		host.addTab(spec);
	}
	
	/**
	 * 设置tabhost初始状态
	 * @param drawableId
	 * @param childAt
	 */
	public void setRadioSource(int drawableId, int childAt) {
		Drawable d=getResources().getDrawable(drawableId);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		((RadioButton) main_group.getChildAt(childAt)).setCompoundDrawables(null, d, null, null);
	}
	
	/**
	 * 设置点击radioButton之后事件处理
	 * @param spec
	 * @param drawableId
	 * @param childAt
	 */
	public void setRadioButton(String spec, int drawableId, int childAt) {
		
		setRadioSource(R.drawable.home_gray, 0);
		setRadioSource(R.drawable.gastation_gray, 1);
		setRadioSource(R.drawable.sign_gray, 2);
		setRadioSource(R.drawable.more_gray, 3);
		
		host.setCurrentTabByTag(spec);
		Drawable d=getResources().getDrawable(drawableId);
		RadioButton button=(RadioButton) main_group.getChildAt(childAt);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		button.setCompoundDrawables(null, d, null, null);
	}
	
	//退出
	public void loginout() {
		try {
			new AlertDialog.Builder(MainActivity.this).setTitle(getResources().getString(R.string.tishi))
			.setMessage(getResources().getString(R.string.tishi_loginout))
			.setPositiveButton(getResources().getString(R.string.tishi_ok), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					((GasStationApplication) MainActivity.this.getApplication()).isAppOpen=false;
					finish();
				}
			}).setNegativeButton(getResources().getString(R.string.tishi_cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}}).show();
		} catch(Exception e) {
			finish();
		}
	}
	
	/**
	 * 跳转至加油选项卡
	 */
	public void jumpToJiayou(int num, int from) {
		((GasStationApplication) getApplicationContext()).jumpJiayouNum=num+10000*3;
		((RadioButton) main_group.getChildAt(1)).toggle();
		setRadioButton("spec2", R.drawable.gastation__blue, 1);
		((GasStationApplication) getApplicationContext()).jumpJiayouFrom=from;
	}
	
	/**
	 * 跳转至流量银行
	 * @param num
	 */
	public void jumpToFlowBank(int num) {
		((GasStationApplication) getApplication()).flowBankNum=num+10000*3;
		((RadioButton) main_group.getChildAt(2)).toggle();
		setRadioButton("spec3", R.drawable.gastation__blue, 2);
	}
	
	/**
	 * 跳转至短信退订
	 * @param num
	 */
	public void jumpToDx() {
		((RadioButton) main_group.getChildAt(3)).toggle();
		setRadioButton("spec4", R.drawable.gastation__blue, 3);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.app_loginout) {
			loginout();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub		
		super.onResume();
		
		if(((GasStationApplication) getApplication()).webTab!=0) {
			host.setCurrentTab(((GasStationApplication) getApplication()).webTab);
			((RadioButton) main_group.getChildAt(2)).toggle();
			setRadioButton("spec3", R.drawable.sign_blue, 2);
		}
		
		Util.setLoginOut(MainActivity.this, false);
		
		//判断是否有新信息
		if(((GasStationApplication) getApplication()).isNewSuggest) {
			suggest_new.setVisibility(View.VISIBLE);
		}
		else {
			suggest_new.setVisibility(View.GONE);
		}
		IntentFilter filter=new IntentFilter();
		filter.addAction(JPushReceiver.refreshSuggest);
		registerReceiver(receiver, filter);
		//当前加载的号码与初始化后的号码不一致的时候，则认为已经切换号码
		if(initPhoneNum!=-1&&initPhoneNum!=Long.parseLong(Util.getUserInfo(MainActivity.this).get(0))) {
			initPhoneNum=Long.parseLong(Util.getUserInfo(MainActivity.this).get(0));
			((RadioButton) main_group.getChildAt(0)).toggle();
			setRadioButton("spec1", R.drawable.home_blue, 0);
			checkNetwork();
		}
		//加油成功跳转首页
		if(((GasStationApplication) getApplicationContext()).isJumpToMonitor) {
			((RadioButton) main_group.getChildAt(0)).toggle();
			setRadioButton("spec1", R.drawable.home_blue, 0);
		}
		//团购失败跳转团购界面
		if(((GasStationApplication) getApplicationContext()).isJumpToTuan) {
			((RadioButton) main_group.getChildAt(2)).toggle();
			setRadioButton("spec3", R.drawable.sign_blue, 2);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopService(new Intent("com.linkage.CheckClientState"));
		((GasStationApplication) getApplicationContext()).isJumpToMonitor=false;
		((GasStationApplication) MainActivity.this.getApplication()).isAppOpen=false;
		Util.cleanBitmap(AsyncSingleImageLoad.getInstance(MainActivity.this).getGonglve_Title_2_Detail(), MainActivity.this);
		Util.cleanBitmap(AsyncImageLoad.getInstance(MainActivity.this).getChoice(1), MainActivity.this);
		super.onDestroy();
	}
	
	/**
	 * 跳转到首页
	 */
	public void jumpToHome() {
		((RadioButton) main_group.getChildAt(0)).toggle();
		setRadioButton("spec1", R.drawable.home_blue, 0);
	}
	
	//不可以切换标签栏
	public void not_able_change() {
		for(int i=0;i<main_group.getChildCount();i++) {
			((RadioButton) main_group.getChildAt(i)).setEnabled(false);
			((RadioButton) main_group.getChildAt(i)).setClickable(false);
		}
	}
	
	//可以切换标签栏
	public void able_change() {
		for(int i=0;i<main_group.getChildCount();i++) {
			((RadioButton) main_group.getChildAt(i)).setEnabled(true);
			((RadioButton) main_group.getChildAt(i)).setClickable(true);
		}
	}
	
	public void checkNetwork() {
		startService(new Intent("com.linkage.CheckClientState"));
		//网络连接提示
		ConnectivityManager mConnectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=mConnectivity.getActiveNetworkInfo();
        if(!(info!=null&&info.isAvailable())) {
        	Intent intent=new Intent(MainActivity.this, SysActivity.class);
        	startActivity(intent);
        }
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}

	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println(intent.getAction());
			suggest_new.setVisibility(View.VISIBLE);
		}};
		
	/**
	 * 上传用户本地应用	
	 */
	public void updatePackageInfo() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<String> str_list=new ArrayList<String>();
				PackageManager pm=getPackageManager();
				List<PackageInfo> pmInfo=pm.getInstalledPackages(0);
				for(int i=0;i<pmInfo.size();i++) {
					if((pmInfo.get(i).applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)!=0||  
						       (pmInfo.get(i).applicationInfo.flags&ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)!=0) {
						continue;
					}
					String str="";
					if(TrafficStats.getUidRxBytes(pmInfo.get(i).applicationInfo.uid)==TrafficStats.UNSUPPORTED||TrafficStats.getUidTxBytes(pmInfo.get(i).applicationInfo.uid)==TrafficStats.UNSUPPORTED) {
						str=pm.getApplicationLabel(pmInfo.get(i).applicationInfo).toString()+";0";
					}
					else {
						DecimalFormat df = new DecimalFormat("0.00");
						str=pm.getApplicationLabel(pmInfo.get(i).applicationInfo).toString()+";"+
								df.format(((double) TrafficStats.getUidRxBytes(pmInfo.get(i).applicationInfo.uid))/(1024*1024)+((double) TrafficStats.getUidTxBytes(pmInfo.get(i).applicationInfo.uid))/(1024*1024));
					}	
					str_list.add(str);
				}
				String[] str_array=new String[str_list.size()];
				for(int i=0;i<str_list.size();i++) {
					str_array[i]=str_list.get(i);
				}
				//上传
				LinkedList<String> wholeUrl=Util.getWholeUrl(MainActivity.this);
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MainActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MainActivity.this);
						PublicManager publicManager=GetWebDate.getHessionFactiory(MainActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
						publicManager.updateLocalApps(Long.parseLong(list.get(0)), str_array);
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
						System.out.println("上传用户本地应用成功");
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
			}}).start();
		
	}	
}
