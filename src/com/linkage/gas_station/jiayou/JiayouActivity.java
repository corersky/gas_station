package com.linkage.gas_station.jiayou;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import hong.specialEffects.wheel.OnWheelChangedListener;
import hong.specialEffects.wheel.OnWheelScrollListener;
import hong.specialEffects.wheel.WheelView;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.model.OilListModel;
import com.linkage.gas_station.myview.GalleryFlow;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.OilManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class JiayouActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_refresh=null;
	LinearLayout jiayou_bg_layout=null;
	
	GalleryFlow jiayou_gallery=null;
	AdapterJiayou adapter=null;
	TextView jiayou_desp_1=null;
	TextView jiayou_desp_2=null;
	WheelView jiayou_choice_1=null;
	LinearLayout jiayou_choice_2=null;
	TextView jiayou_choice_2_left1=null;
	EditText jiayou_choice_2_center1=null;
	TextView jiayou_choice_2_right1=null;
	TextView jiayou_choice_2_left2=null;
	EditText jiayou_choice_2_center2=null;
	TextView jiayou_choice_2_right2=null;
	ImageView jiayou_wantjiayou=null;
	
	//当前选中图片列
	int currentChoice=0;
	HashMap<Integer, ArrayList<OilListModel>> model_list=null;
	//视图滑动判断
	public boolean scrolling1 = false;
	//视图滑动位置
	int currentItem1=0;
	//刷新标志位
	HashMap<Integer, Boolean> model_refresh=null;
	//初始号码
	long initPhoneNum=-1;
	//图片名称
	ArrayList<String> name=null;
	//显示加油类型总个数
	int can_jiayou_num=3;
	
	TextWatcher_t1 t1=null;
	TextWatcher_t2 t2=null;
	
	int[] province_array=new int[10];
	String[] desp_title_province_array=new String[10];
	String[] desp_bottom_province_array=new String[10];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		name=new ArrayList<String>();
		model_list=new HashMap<Integer, ArrayList<OilListModel>>();
		model_refresh=new HashMap<Integer, Boolean>();		

		init();
	}
	
	public void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.jiayou));
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setImageResource(R.drawable.jiayou_detail);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(JiayouActivity.this, OrderTrackActivity.class);
				startActivity(intent);
			}});
		jiayou_bg_layout=(LinearLayout) findViewById(R.id.jiayou_bg_layout);
	}
	
	/**
	 * 加入内容视图
	 */
	public void loadView() {
		jiayou_bg_layout.removeAllViews();
		View view=null;
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(dm.density==1.0||dm.density==1.5) {
			view=LayoutInflater.from(JiayouActivity.this).inflate(R.layout.activity_jiayou_15, null);
		}
		else {
			view=LayoutInflater.from(JiayouActivity.this).inflate(R.layout.activity_jiayou_20, null);
		}		
		
		jiayou_desp_1=(TextView) view.findViewById(R.id.jiayou_desp_1);
		jiayou_desp_2=(TextView) view.findViewById(R.id.jiayou_desp_2);
		jiayou_gallery=(GalleryFlow) view.findViewById(R.id.jiayou_gallery); 
		jiayou_gallery.setSpacing((int) (-40*((double) dm.heightPixels)/800));
		adapter=new AdapterJiayou(JiayouActivity.this, dm.heightPixels, name);
		//在滚动结束之后调用setOnItemSelectedListener
		jiayou_gallery.setCallbackDuringFling(false);
		jiayou_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				currentChoice=arg2;
				jiayou_desp_1.setText(desp_title_province_array[arg2%can_jiayou_num]);
				jiayou_desp_2.setText(desp_bottom_province_array[arg2%can_jiayou_num]);
				if(province_array[arg2%can_jiayou_num]!=-1) {
					jiayou_choice_1.setVisibility(View.VISIBLE);
					jiayou_choice_2.setVisibility(View.GONE);
					//若出现异常，则在此处重新加载
					if(!model_list.containsKey(province_array[arg2%can_jiayou_num])&&!model_refresh.get(province_array[arg2%can_jiayou_num])) {
						jiayou_choice_1.setViewAdapter(null);
						loadWheel(province_array[arg2%can_jiayou_num]);
					} 
					else {
						if(model_list.containsKey(province_array[arg2%can_jiayou_num])) {
							if(province_array[arg2%can_jiayou_num]==4) {
								//定向流量只有一个滚轮
								setWheelPos(province_array[arg2%can_jiayou_num], 1);
							}
							else {
								setWheelPos(province_array[arg2%can_jiayou_num], 2);
							}
						}						
					}
				}
				else {
					jiayou_choice_2.setVisibility(View.VISIBLE);
					jiayou_choice_1.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		jiayou_gallery.setAdapter(adapter);
		jiayou_gallery.setSelection(0+10000*3);
		jiayou_choice_1=(WheelView) view.findViewById(R.id.jiayou_choice_1);
		jiayou_choice_2=(LinearLayout) view.findViewById(R.id.jiayou_choice_2);
		jiayou_choice_2_left1=(TextView) view.findViewById(R.id.jiayou_choice_2_left1);
		jiayou_choice_2_left1.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jiayou_choice_2_left2.setBackgroundResource(R.drawable.jiayou_unselect_left);
				jiayou_choice_2_left2.setTextColor(Color.GRAY);
				jiayou_choice_2_left2.setText(getResources().getString(R.string.jiayou_hint2_desp));
				jiayou_choice_2_center2.setHint("");
				jiayou_choice_2_center2.setText("");
				jiayou_choice_2_center2.setEnabled(false);
				jiayou_choice_2_center2.setBackgroundResource(R.drawable.jiayou_unselect_center);
				jiayou_choice_2_center2.removeTextChangedListener(t2);
				jiayou_choice_2_right2.setBackgroundResource(R.drawable.jiayou_unselect_right);

				jiayou_choice_2_left1.setBackgroundResource(R.drawable.jiayou_select_left);
				jiayou_choice_2_left1.setTextColor(Color.WHITE);
				jiayou_choice_2_left1.setText(getResources().getString(R.string.jiayou_hint1_desp));
				jiayou_choice_2_center1.setHint(getResources().getString(R.string.jiayou_hint1));
				jiayou_choice_2_center1.setEnabled(true);
				jiayou_choice_2_center1.setText("");
				jiayou_choice_2_center1.setBackgroundResource(R.drawable.jiayou_select_center);
				jiayou_choice_2_center1.addTextChangedListener(t1);
				jiayou_choice_2_right1.setBackgroundResource(R.drawable.jiayou_select_right);
			}});
		jiayou_choice_2_center1=(EditText) view.findViewById(R.id.jiayou_choice_2_center1);
		jiayou_choice_2_right1=(TextView) view.findViewById(R.id.jiayou_choice_2_right1);
		jiayou_choice_2_left2=(TextView) view.findViewById(R.id.jiayou_choice_2_left2);
		jiayou_choice_2_left2.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jiayou_choice_2_left1.setBackgroundResource(R.drawable.jiayou_unselect_left);
				jiayou_choice_2_left1.setTextColor(Color.GRAY);
				jiayou_choice_2_left1.setText(getResources().getString(R.string.jiayou_hint1_desp));
				jiayou_choice_2_center1.setHint("");
				jiayou_choice_2_center1.setText("");
				jiayou_choice_2_center1.setEnabled(false);
				jiayou_choice_2_center1.setBackgroundResource(R.drawable.jiayou_unselect_center);
				jiayou_choice_2_center1.removeTextChangedListener(t1);
				jiayou_choice_2_right1.setBackgroundResource(R.drawable.jiayou_unselect_right);
				
				jiayou_choice_2_left2.setBackgroundResource(R.drawable.jiayou_select_left);
				jiayou_choice_2_left2.setTextColor(Color.WHITE);
				jiayou_choice_2_left2.setText(getResources().getString(R.string.jiayou_hint2_desp));
				jiayou_choice_2_center2.setHint(getResources().getString(R.string.jiayou_hint2));
				jiayou_choice_2_center2.setText("");
				jiayou_choice_2_center2.setEnabled(true);
				jiayou_choice_2_center2.setBackgroundResource(R.drawable.jiayou_select_center);
				jiayou_choice_2_center2.addTextChangedListener(t2);
				jiayou_choice_2_right2.setBackgroundResource(R.drawable.jiayou_select_right);
			}});
		jiayou_choice_2_center2=(EditText) view.findViewById(R.id.jiayou_choice_2_center2);
		jiayou_choice_2_center2.setEnabled(false);
		jiayou_choice_2_right2=(TextView) view.findViewById(R.id.jiayou_choice_2_right2);
		
		t1=new TextWatcher_t1();
		jiayou_choice_2_center1.addTextChangedListener(t1);
		t2=new TextWatcher_t2();
		
		jiayou_wantjiayou=(ImageView) view.findViewById(R.id.jiayou_wantjiayou);
		jiayou_wantjiayou.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(currentChoice%can_jiayou_num==0&&!model_list.containsKey(province_array[0])) {
					if(Util.getUserArea(JiayouActivity.this).equals("0971")) {
						showCustomToast("全国流量数据获取失败，请重新获取后再加油");
					}
					else {
						showCustomToast(getResources().getString(R.string.jiayou_1_error));
					}					
					return ;
				}
				
				if(currentChoice%can_jiayou_num==1&&(jiayou_choice_2_center2.getText().toString().equals("")||jiayou_choice_2_center2.getText().toString().equals("0")||jiayou_choice_2_center1.getText().toString().equals("")||jiayou_choice_2_center1.getText().toString().equals("0"))) {
					//江苏定制
					if(Util.getUserArea(JiayouActivity.this).equals("2500")) {
						showCustomToast(getResources().getString(R.string.jiayou_error1));
						return ;
					}					
				}
				
				if(currentChoice%can_jiayou_num==2&&!model_list.containsKey(province_array[2])) {
					if(Util.getUserArea(JiayouActivity.this).equals("0971")) {
						showCustomToast("省内流量数据获取失败，请重新获取后再加油");
					}
					else {
						showCustomToast(getResources().getString(R.string.jiayou_3_error));
					}
					
					return ;
				}
				
				if(currentChoice%can_jiayou_num==3&&!model_list.containsKey(province_array[3])) {
					showCustomToast("腾讯手机游戏流量数据获取失败，请重新获取后再加油");
					return ;
				}
				
				switch(currentChoice%can_jiayou_num) {
				case 0:
					if(model_list.containsKey(province_array[0])) {
						Intent intent=new Intent();
						Bundle bundle=new Bundle();
						intent.setClass(JiayouActivity.this, JiayouDetaiActivity.class);
						bundle.putString("offerId", model_list.get(province_array[0]).get(currentItem1).getOffer_id());
						bundle.putString("offer_name", model_list.get(province_array[0]).get(currentItem1).getOffer_name());
						bundle.putString("offer_description", getResources().getString(R.string.jiayou_desp1));
						bundle.putString("type", "simple_station");
						intent.putExtras(bundle);
						startActivity(intent);
					}
					else {
						showCustomToast("数据正在加载中...");
					}
					break;
				case 1:
					//青海定制
					if(Util.getUserArea(JiayouActivity.this).equals("0971")) {
						if(model_list.containsKey(province_array[1])) {
							Intent intent=new Intent();
							Bundle bundle=new Bundle();
							intent.setClass(JiayouActivity.this, JiayouCardDetailActivity.class);
							bundle.putString("from", "jiayoucard");
							bundle.putString("cost", model_list.get(province_array[1]).get(currentItem1).getOffer_cost().substring(0, model_list.get(province_array[1]).get(currentItem1).getOffer_cost().indexOf("元")));
							bundle.putString("amount", model_list.get(province_array[1]).get(currentItem1).getOffer_description().substring(0, model_list.get(province_array[1]).get(currentItem1).getOffer_description().indexOf("M")));
							bundle.putString("desp", "流量卡  ");
							intent.putExtras(bundle);
							startActivity(intent);
						}	
						else {
							showCustomToast("数据正在加载中...");
						}
					}
					//江苏定制
					else {
						Intent intent=new Intent();
						Bundle bundle=new Bundle();
						intent.setClass(JiayouActivity.this, JiayouCardDetailActivity.class);
						bundle.putString("from", "jiayoucard");
						bundle.putString("cost", jiayou_choice_2_center2.getText().toString());
						bundle.putString("amount", jiayou_choice_2_center1.getText().toString());
						bundle.putString("desp", "长期有效流量  ");
						intent.putExtras(bundle);
						startActivity(intent);
					}					
					break;
				case 2:
					if(model_list.containsKey(province_array[2])) {
						Intent intent=new Intent();
						Bundle bundle=new Bundle();
						intent.setClass(JiayouActivity.this, JiayouDetaiActivity.class);
						bundle.putString("offerId", model_list.get(province_array[2]).get(currentItem1).getOffer_id());
						bundle.putString("offer_name", model_list.get(province_array[2]).get(currentItem1).getOffer_name());						
						//青海定制
						if(Util.getUserArea(JiayouActivity.this).equals("0971")) {
							bundle.putString("type", "simple_station");
							bundle.putString("offer_description", getResources().getString(R.string.jiayou_desp5));
						}
						//江苏定制
						else {
							bundle.putString("type", "night_station");
							bundle.putString("offer_description", getResources().getString(R.string.jiayou_desp3));
						}
						intent.putExtras(bundle);
						startActivity(intent);
					}
					else {
						showCustomToast("数据正在加载中...");
					}
					break;
				case 3:
					if(model_list.containsKey(province_array[3])) {
						new AlertDialog.Builder(JiayouActivity.this).setTitle("提示").setMessage(model_list.get(province_array[3]).get(currentItem1).getOffer_content()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent intent=new Intent();
								Bundle bundle=new Bundle();
								intent.setClass(JiayouActivity.this, JiayouDetaiActivity.class);
								bundle.putString("offerId", model_list.get(province_array[3]).get(currentItem1).getOffer_id());
								bundle.putString("offer_name", model_list.get(province_array[3]).get(currentItem1).getOffer_name());
								bundle.putString("offer_description", getResources().getString(R.string.jiayou_desp5));
								bundle.putString("type", "dingxiang_station");
								intent.putExtras(bundle);
								startActivity(intent);
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						}).show();
					}
					else {
						showCustomToast("数据正在加载中...");
					}
					break;
				}
				
			}});
		
		for(int i=0;i<province_array.length;i++) {
			if(province_array[i]!=-1) {
				//loadWheel(province_array[i]);
				model_refresh.put(province_array[i], false);
			}			
		}	
		jiayou_bg_layout.addView(view);
	}
	
	/**
	 * 设置滚轮视图数据
	 * @param pos
	 */
	public void setWheelPos(int pos, int rowNum) {
		currentItem1=0;
		jiayou_choice_1.setVisibleItems(3);
		jiayou_choice_1.setViewAdapter(new AdapterQuanguo(JiayouActivity.this, model_list.get(pos), rowNum));
		jiayou_choice_1.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				
			}
		});
		jiayou_choice_1.addScrollingListener(new OnWheelScrollListener() {
			
			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub
				scrolling1=true;
			}
			
			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				scrolling1=false;
				currentItem1=wheel.getCurrentItem();
			}
		});
		jiayou_choice_1.setCurrentItem(model_list.get(pos).size()/2);
	}
	
	/**
	 * 流量设置
	 */
	public void loadWheel(final int typeNum) {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				model_refresh.put(typeNum, false);
				if(msg.obj==null) {
					switch(typeNum) {
					case 1:
						showCustomToast("标准流量加载失败");
						break;
					case 2:
						showCustomToast("夜间专用流量加载失败");
						break;
					case 4:
						showCustomToast("定向流量加载失败");
						break;
					case 7:
						showCustomToast("流量卡加载失败");
						break;
					case 6:
						showCustomToast("省内流量加载失败");
						break;
					}
				}
				else {
					Map[] map=(Map[]) msg.obj;
					ArrayList<OilListModel> model_=new ArrayList<OilListModel>();
					for(int i=0;i<map.length*20;i++) {
						OilListModel model=new OilListModel();
						model.setOffer_id(map[i%map.length].get("offer_id").toString());
						model.setOffer_name(map[i%map.length].get("offer_name").toString());
						model.setOffer_description(map[i%map.length].get("offer_description").toString());
						model.setOffer_type_id(map[i%map.length].get("offer_type_id").toString());
						model.setOffer_cost(map[i%map.length].get("offer_cost").toString());
						try {
							model.setOffer_content(Util.convertNull(map[i%map.length].get("offer_content").toString()));
						} catch(Exception e) {
							
						}						
						//model.setOffer_image_name(map[i%map.length].get("offer_image_name").toString());
						model_.add(model);
					}
					model_list.put(typeNum, model_);
					if(province_array[currentChoice%can_jiayou_num]==typeNum) {
						if(province_array[currentChoice%can_jiayou_num]==4) {
							setWheelPos(province_array[currentChoice%can_jiayou_num], 1);
						}
						else {
							setWheelPos(province_array[currentChoice%can_jiayou_num], 2);
						}						
					}
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(JiayouActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(JiayouActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(JiayouActivity.this);
						
						OilManager oilManager=GetWebDate.getYaoHessionFactiory(JiayouActivity.this).create(OilManager.class, currentUsedUrl+"/hessian/oilManager", getClassLoader());
						Map[] map=oilManager.getOilList(Long.parseLong(list.get(0)), typeNum, "", list.get(1));
						m.obj=map;
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			MainActivity.getInstance().loginout();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	class TextWatcher_t1 implements TextWatcher {
		
		//上一次输入的内容
		String temp="";

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(temp.equals(s.toString())) {
				return ;
			}
			if(s.toString().equals("")) {
				return ;
			}
			if(Integer.parseInt(s.toString())==0) {
				jiayou_choice_2_center1.setText("");
				showCustomToast("指定流量起始数字不能为0");
			}
			try {
				DecimalFormat df=new DecimalFormat("0.0");
				if(Integer.parseInt(s.toString())>10000) {
					jiayou_choice_2_center1.setText("10000");
					temp="10000";
					jiayou_choice_2_center2.setText("2000.0");
				}
				else {
					jiayou_choice_2_center2.setText(df.format(Double.parseDouble(s.toString())/5));
					temp=""+df.format(Double.parseDouble(s.toString())/5);
				}
				CharSequence text = jiayou_choice_2_center1.getText();
				if(text instanceof Spannable) {
					Spannable spanText = (Spannable)text;
					Selection.setSelection(spanText, text.length());
				}
			} catch(Exception e) {
				jiayou_choice_2_center2.setText("");
			}
		}

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
		
	}
	
	class TextWatcher_t2 implements TextWatcher {
		
		//上一次输入的内容
		String temp="";

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(temp.equals(s.toString())) {
				return ;
			}
			if(s.toString().equals("")) {
				return ;
			}
			if(Integer.parseInt(s.toString())==0) {
				jiayou_choice_2_center2.setText("");
				showCustomToast("指定金额起始数字不能为0");
			}
			try {
				DecimalFormat df=new DecimalFormat("#.0");
				if(Integer.parseInt(s.toString())>2000) {
					jiayou_choice_2_center2.setText("2000");
					temp="2000";
					jiayou_choice_2_center1.setText("10000.0");
				}
				else {
					jiayou_choice_2_center1.setText(df.format(Double.parseDouble(s.toString())*5));
					temp=""+df.format(Double.parseDouble(s.toString())*5);
				}
				CharSequence text = jiayou_choice_2_center2.getText();
				if(text instanceof Spannable) {
					Spannable spanText = (Spannable)text;
					Selection.setSelection(spanText, text.length());
				}
			} catch(Exception e) {
				jiayou_choice_2_center1.setText("");
			}
		}

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
		
	}
	
	protected void onResume() {
		super.onResume();
		if(initPhoneNum==-1) {
			loadData();
			initPhoneNum=Long.parseLong(Util.getUserInfo(JiayouActivity.this).get(0));			
			loadView();
		}
		//当前加载的号码与初始化后的号码不一致的时候，则认为已经切换号码，重新加载
		else if(initPhoneNum!=-1&&initPhoneNum!=Long.parseLong(Util.getUserInfo(JiayouActivity.this).get(0))) {
			loadData();
			initPhoneNum=Long.parseLong(Util.getUserInfo(JiayouActivity.this).get(0));			
			loadView();
			return;
		}
		//一般情况或者切换号码之和仍然用原号码登陆
		else if(initPhoneNum!=-1&&initPhoneNum==Long.parseLong(Util.getUserInfo(JiayouActivity.this).get(0))) {
			MainActivity.getInstance().able_change();
		}
		if(((GasStationApplication) getApplicationContext()).jumpJiayouNum!=-1) {
			jiayou_gallery.setSelection(((GasStationApplication) getApplicationContext()).jumpJiayouNum);
			currentChoice=((GasStationApplication) getApplicationContext()).jumpJiayouNum;
			((GasStationApplication) getApplicationContext()).jumpJiayouNum=-1;
		}

		StatService.onResume(this);
	};
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}
	
	/**
	 * 加载初始化数据
	 */
	public void loadData() {

		name.clear();
		//青海定制
		if(Util.getUserArea(JiayouActivity.this).equals("0971")) {
			can_jiayou_num=3;
			province_array[0]=1;
			province_array[1]=7;
			province_array[2]=6;
			name.add("jiayou_1_0971");
			name.add("jiayou_2_0971");
			name.add("jiayou_3_0971");			

			desp_title_province_array[0]=getResources().getString(R.string.jiayou_desp_1);
			desp_title_province_array[1]=".全国漫游（不含港澳台）.全天候.3个月有效";
			desp_title_province_array[2]="省内漫游、全天候、当月有效";
			
			desp_bottom_province_array[0]="0.06-0.17元/MB不等，加的越多越便宜（限当月有效）";
			desp_bottom_province_array[1]="0.10-0.17元/MB不等，加得越多越便宜(3个月有效)";
			desp_bottom_province_array[2]="0.06-0.10元/MB不等，加得越多越便宜(限当月有效)";			
		}
		//江苏定制
		else if(Util.getUserArea(JiayouActivity.this).equals("2500")) {
			can_jiayou_num=4;
			province_array[0]=1;
			province_array[1]=-1;
			province_array[2]=2;
			province_array[3]=4;
			name.add("jiayou_1");
			name.add("jiayou_2");
			name.add("jiayou_3");
			name.add("jiayou_4");
			
			desp_title_province_array[0]=getResources().getString(R.string.jiayou_desp_1);
			desp_title_province_array[1]=getResources().getString(R.string.jiayou_desp_2);
			desp_title_province_array[2]=getResources().getString(R.string.jiayou_desp_3);
			desp_title_province_array[3]=getResources().getString(R.string.jiayou_desp_1);
			
			desp_bottom_province_array[0]=getResources().getString(R.string.jiayou_desp_1_1);
			desp_bottom_province_array[1]=getResources().getString(R.string.jiayou_desp_1_2);
			desp_bottom_province_array[2]=getResources().getString(R.string.jiayou_desp_1_3);
			desp_bottom_province_array[3]="定向流量，限指定应用省内漫游使用，当月有效。";
		}
		model_refresh.clear();
		model_list.clear();
	}

}
