package com.linkage.gas_station.gonglve;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

import com.baidu.mobstat.StatService;
import com.lidroid.xutils.BitmapUtils;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.jiayou.JiayouCardDetailActivity;
import com.linkage.gas_station.life.LifeMainActivity;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.market.DirectionalFlowActivity;
import com.linkage.gas_station.memberday.BusinessesActivity;
import com.linkage.gas_station.memberday.MemberActivity;
import com.linkage.gas_station.model.GifTableListModel;
import com.linkage.gas_station.model.GonglveTuanModel;
import com.linkage.gas_station.model.ProductGroupModel;
import com.linkage.gas_station.more.MoreActivity;
import com.linkage.gas_station.myview.FixedSpeedScroller;
import com.linkage.gas_station.myview.MyScrollLayout;
import com.linkage.gas_station.myview.MyScrollLayout.PageListener;
import com.linkage.gas_station.oil_treasure.TreasureMainActivity;
import com.linkage.gas_station.qqapi.QQActivity;
import com.linkage.gas_station.share.ShareActivity_New;
import com.linkage.gas_station.sinaweiboapi.WBMainActivity;
import com.linkage.gas_station.util.FlowBankParse;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;
import com.linkage.gas_station.wxapi.SendWeixin;
import com.linkage.gas_station.yxapi.SendYixin;

public class GonglveActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_refresh=null;
	ProgressBar title_refresh_progress=null;
	LinearLayout gonglve_bank_layout=null;
	RelativeLayout gonglve_tuan_layout=null;
	MyScrollLayout gonglve_tuan=null;
	LinearLayout gonglve_tip_layout=null;
	LinearLayout activity_result_layout=null;
	ImageView gonglve_choice_l=null;
	ImageView gonglve_choice_r=null;
	//输入号码EditText
	EditText phoneNumEditText=null;
	
	ArrayList<GonglveTuanModel> modelList2=null;

	Timer timer=null;
	Timer timer_yzm=null;
	UploadTimer timetask=null;
	//初始号码
	long initPhoneNum=-1;
	//当前版本支持的活动类型
	int[] allowedType={2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
	//活动加载标志位
	boolean isLoadActivity=false;
	boolean isLoadBank=false;
	//加载选项卡
	int defaultLoad=1;
	//选择转增的流量
	int choiceFlow=0;
	//上一次点击领取流量的事件
	long lastClickShare=0;
	
	LinearLayout market_layout=null;
	LinearLayout market_adv_num_layout=null;
	AutoScrollViewPager market_adv=null;
	MarketNewAdapter adapter=null;
	
	//广告图片视图的个数
	ArrayList<View> advView=null;	
	ArrayList<ImageView> pointView=null;	
	int currentItem=0;
	//跳转的item
	int refreshNum=0;

	BitmapUtils bitmapUtils=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gonglve);
		
		initPhoneNum=Long.parseLong(Util.getUserInfo(GonglveActivity.this).get(0));
		modelList2=new ArrayList<GonglveTuanModel>();
		advView=new ArrayList<View>();
		pointView=new ArrayList<ImageView>();	
		
		((GasStationApplication) getApplicationContext()).isRefreshTuan=false;
		
		bitmapUtils=new BitmapUtils(this);
		bitmapUtils.configDefaultLoadingImage(R.drawable.gonglve_title_2_default);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.gonglve_title_2_default);
		
		init();
	}
	
	public void init() {
		activity_result_layout=(LinearLayout) findViewById(R.id.activity_result_layout);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.gonglve));
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isLoadActivity&&defaultLoad==1) {
					showCustomToast("活动正在加载中，请稍后");
				}
				else if(isLoadBank&&defaultLoad==2) {
					showCustomToast("流量银行正在加载中，请稍后");
				}
				else if(!isLoadActivity&&defaultLoad==1) {
					MainActivity.getInstance().not_able_change();
					loadCustomTuan();
				}
				else if(!isLoadBank&&defaultLoad==2) {
					MainActivity.getInstance().not_able_change();
					if(Util.getUserArea(GonglveActivity.this).equals("0971")) {
						loadNoMarketView();
					}
					else {
						loadMarketView();
					}
				}				
			}});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);
		gonglve_tip_layout=(LinearLayout) findViewById(R.id.gonglve_tip_layout);
		gonglve_tuan_layout=(RelativeLayout) findViewById(R.id.gonglve_tuan_layout);
 		gonglve_tuan=(MyScrollLayout) findViewById(R.id.gonglve_tuan);
 		gonglve_tuan.setPageListener(new PageListener() {
			
			@Override
			public void page(int page) {
				// TODO Auto-generated method stub
				
			}
		});
 		gonglve_bank_layout=(LinearLayout) findViewById(R.id.gonglve_bank_layout);
 		gonglve_choice_l=(ImageView) findViewById(R.id.gonglve_choice_l);
 		gonglve_choice_l.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isLoadActivity&&!isLoadBank) {
					defaultLoad=1;
					gonglve_tuan_layout.setVisibility(View.VISIBLE);
					gonglve_bank_layout.setVisibility(View.GONE);
					gonglve_choice_l.setImageResource(R.drawable.gl_left_on);
					gonglve_choice_r.setImageResource(R.drawable.gl_right);
				}				
			}});
 		gonglve_choice_r=(ImageView) findViewById(R.id.gonglve_choice_r);
 		gonglve_choice_r.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isLoadActivity&&!isLoadBank) {
					defaultLoad=2;
					gonglve_tuan_layout.setVisibility(View.GONE);
					gonglve_bank_layout.setVisibility(View.VISIBLE);	
					gonglve_choice_l.setImageResource(R.drawable.gl_left);
					gonglve_choice_r.setImageResource(R.drawable.gl_right_on);
				}
			}});
		
		MainActivity.getInstance().not_able_change();
		
		if(((GasStationApplication) getApplication()).flowBankNum!=-1) {
			defaultLoad=2;
			gonglve_choice_l.setImageResource(R.drawable.gl_left);
			gonglve_choice_r.setImageResource(R.drawable.gl_right_on);
			((GasStationApplication) getApplication()).flowBankNum=-1;
		}
		else {
			gonglve_choice_l.setImageResource(R.drawable.gl_left_on);
			gonglve_choice_r.setImageResource(R.drawable.gl_right);
		}
		
		loadCustomTuan();
		if(Util.getUserArea(GonglveActivity.this).equals("0971")) {
			loadNoMarketView();
		}
		else {
			loadMarketView();
		}
	}
	
	/**
	 * 加载活动
	 * @param type
	 */
	public void loadCustomTuan() {
		
		isLoadActivity=true;
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				if(msg.what==1) {
					//团购数据刷新完毕
					((GasStationApplication) getApplicationContext()).isRefreshTuan=false;
					gonglve_tuan.removeAllViews();
					final ArrayList<GonglveTuanModel> temp2=msg.getData().getParcelableArrayList("adapter2");
					modelList2.clear();
					modelList2.addAll(temp2);	
					for(int i=0;i<temp2.size();i++) {
						final int num=i;
						View view=null;
						DisplayMetrics dm=new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dm);
						if(dm.density==1.5) {
							view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.adapter_gonglve_title_2_15, null);
						}
						else if(dm.density==1.0) {
							view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.adapter_gonglve_title_2_10, null);
						}
						else {
							view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.adapter_gonglve_title_2_20, null);
						}
						final RelativeLayout activity_layout=(RelativeLayout) view.findViewById(R.id.activity_layout);
						final RelativeLayout activity_2_layout=(RelativeLayout) view.findViewById(R.id.activity_2_layout);
						activity_2_layout.setVisibility(View.GONE);
						final TextView activity_text=(TextView) view.findViewById(R.id.activity_text);
						final RelativeLayout tuan_layout=(RelativeLayout) view.findViewById(R.id.tuan_layout);
						final int activity_type=Integer.parseInt(temp2.get(i).getActivity_type());
						if(activity_type==2 || activity_type==4 || activity_type==5 || activity_type==6) {
							tuan_layout.setVisibility(View.VISIBLE);
							activity_layout.setVisibility(View.GONE);
						}
						else if(activity_type==7) {
							if(temp2.get(num).getIs_jion()==1) {
								activity_text.setText(Html.fromHtml("您当前还剩<font color='red'>"+((int) (temp2.get(num).getUnrecevice_num()))+"M</font>流量未领取"));
							}
							else {
								activity_text.setText(Html.fromHtml("参与计划即可获赠<font color='red'>"+temp2.get(num).getTotal_num()+"M</font>流量"));
							}
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==11) {
							if(temp2.get(num).getIs_jion()==1) {
								activity_text.setText(Html.fromHtml("您当前还剩<font color='red'>"+((int) (temp2.get(num).getUnrecevice_num()))+"M</font>流量未领取"));
							}
							else {
								activity_text.setText("");
							}
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==3) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
							activity_text.setText(Html.fromHtml("您当前还有<font color='red'>"+((int) (temp2.get(i).getUnrecevice_num()*100))+"M</font>流量未领取"));
						}
						else if(activity_type==8) {
							activity_text.setGravity(Gravity.RIGHT);
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
							activity_text.setText(Html.fromHtml("参与计划即可获赠<font color='red'>50M</font>流量"));
						}
						else if(activity_type==9||activity_type==10) {
							activity_text.setGravity(Gravity.RIGHT);
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==12) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==13) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==14) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==15) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==16) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==17) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==18) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==19) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==20) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==21) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
							activity_2_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==22) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						else if(activity_type==23) {
							tuan_layout.setVisibility(View.GONE);
							activity_layout.setVisibility(View.VISIBLE);
						}
						TextView tuan_tip=(TextView) view.findViewById(R.id.tuan_tip);
						if(activity_type==3 || activity_type==4 || activity_type==5 || activity_type==6|| activity_type==7|| activity_type==8|| activity_type==9|| activity_type==10|| activity_type==11|| activity_type==12|| activity_type==13|| activity_type==14|| activity_type==15|| activity_type==16|| activity_type==17|| activity_type==18|| activity_type==19|| activity_type==20|| activity_type==21 ||activity_type==22 ||activity_type==23) {
							tuan_tip.setText(temp2.get(i).getActivity_description());
						}
						final ImageView gonglve_title_2_pic=(ImageView) view.findViewById(R.id.gonglve_title_2_pic);
//						Bitmap bmp=AsyncSingleImageLoad.getInstance(GonglveActivity.this).loadImageBmp(temp2.get(i).getActivity_image_name(), new SingleImageCallBack() {
//
//							@Override
//							public void loadImage(Bitmap bmp) {
//								// TODO Auto-generated method stub
//								gonglve_title_2_pic.setImageBitmap(bmp);
//							}}, "Gonglve_Title_2_Detail");
//						if(bmp==null) {
//							gonglve_title_2_pic.setImageResource(R.drawable.gonglve_title_2_default);
//						}
//						else {
//							gonglve_title_2_pic.setImageBitmap(bmp);
//						}
						bitmapUtils.display(gonglve_title_2_pic, temp2.get(i).getActivity_image_name());
						TextView offer_cost=(TextView) view.findViewById(R.id.offer_cost);
						if(!temp2.get(i).getOffer_cost().equals("")) {
							offer_cost.setText("￥"+(Integer.parseInt(temp2.get(i).getOffer_cost())/100));
						}						
						TextView old_cost=(TextView) view.findViewById(R.id.old_cost);
						if(!temp2.get(i).getOld_cost().equals("")) {
							old_cost.setText("￥"+(Integer.parseInt(temp2.get(i).getOld_cost())/100));
							old_cost.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
						}						
						TextView persons=(TextView) view.findViewById(R.id.persons);
						if(activity_type!=6) {
							persons.setText(Html.fromHtml("已有<font color='red'>"+temp2.get(i).getPersons().toString()+"</font>人购买"));
						}
						else {
							persons.setText(Html.fromHtml("本月已签到<font color='red'>"+temp2.get(i).getTotal_num()+"</font>次，已领取<font color='red'>"+temp2.get(i).getTotal_num()*5+"</font>M流量"));
						}
						TextView activity_end_time=(TextView) view.findViewById(R.id.activity_end_time);
						activity_end_time.setTag(temp2.get(i).getActivity_end_time().toString());
						activity_end_time.setText(Util.getLastTime(temp2.get(i).getActivity_end_time().toString()));
						ImageView activity_2_go_1=(ImageView) view.findViewById(R.id.activity_2_go_1);
						ImageView activity_2_go_2=(ImageView) view.findViewById(R.id.activity_2_go_2);
						final String cost=temp2.get(i).getOffer_cost();
						final String amount=temp2.get(i).getOffer_amount();
						final String offer_id=temp2.get(i).getOffer_id();
						final String activityId=temp2.get(i).getActivity_id();
						final String activity_rule=temp2.get(i).getActivity_rule();
						final String activity_url=temp2.get(i).getActivity_url();
						final String activity_name=temp2.get(i).getActivity_name();
						final ImageView tuan_go=(ImageView) view.findViewById(R.id.tuan_go);
						final ImageView activity_go=(ImageView) view.findViewById(R.id.activity_go);
						final int isJoin=temp2.get(i).getIs_jion();
						if(activity_type==2) {
							tuan_go.setImageResource(R.drawable.tangou_button);
						}
						else if(activity_type==3) {
							tuan_go.setImageResource(R.drawable.sharebutton);
							if(isJoin==0) {
								activity_go.setImageResource(R.drawable.join_button1);
							}
							else {
								activity_go.setImageResource(R.drawable.join_button2);
							}
							activity_go.setOnClickListener(new ImageView.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if(temp2.get(num).getIs_jion()==0) {
										Builder builder=new AlertDialog.Builder(GonglveActivity.this);
										View view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.dialog_text, null);
										final EditText editsystext=(EditText) view.findViewById(R.id.editsystext);
										builder.setView(view);
										builder.setTitle("请您输入推荐人手机号码");
										builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method stub
												if(!editsystext.getText().toString().equals("")) {
													ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
													if(editsystext.getText().toString().equals(list.get(0))) {
														showCustomToast("推荐人手机号不能为本人手机号");
													}
													else {
														String ct = "^((133)|(153)|(18[0,1,9]))\\d{8}$";
														if(editsystext.getText().toString().matches(ct)) {
															showCustomToast("正在提交，请您耐心等待");
															joinActivity(editsystext.getText().toString(), activity_go, temp2.get(num), activity_text);
														}
														else {
															showCustomToast("请输入天翼手机号码");
														}
													}
												}
												else {
													showCustomToast("请您输入推荐人手机号码");
												}
											}
										});
										builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method stub
												
											}
										});
										AlertDialog dialog=builder.create();
										dialog.show();
									}
									else {
										if(System.currentTimeMillis()-lastClickShare>1000*2) {											
											receiveFlow(Long.parseLong(temp2.get(num).getOffer_id()), activity_text, activity_go);
											activity_go.setClickable(false);
											activity_go.setEnabled(false);
										}										
									}
								}});
						}
						else if(activity_type==4||activity_type==5) {
							tuan_go.setImageResource(R.drawable.qianggou_button);
						}
						else if(activity_type==6) {
							tuan_go.setImageResource(R.drawable.shake_button);
						}
						else if(activity_type==7) {
							activity_go.setImageResource(R.drawable.get_double);
							activity_go.setOnClickListener(new ImageView.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									get_double_flow_select(temp2.get(num).getActivity_id(), temp2.get(num).getActivity_url());
								}});
							if(isJoin==0) {
								tuan_go.setImageResource(R.drawable.join_double);
							}
							else {
								tuan_go.setImageResource(R.drawable.join_double_no);
							}
						}
						else if(activity_type==8) {
							activity_go.setVisibility(View.INVISIBLE);
							tuan_go.setImageResource(R.drawable.join_double);
						}
						else if(activity_type==9) {
							activity_go.setVisibility(View.INVISIBLE);
							tuan_go.setImageResource(R.drawable.join_wulin);
						}
						else if(activity_type==10) {
							activity_go.setVisibility(View.INVISIBLE);
							tuan_go.setImageResource(R.drawable.join_button2);
						}
						else if(activity_type==11) {
							activity_go.setVisibility(View.INVISIBLE);
							tuan_go.setImageResource(R.drawable.get_double_2014);
						}
						else if(activity_type==12) {
							activity_go.setVisibility(View.INVISIBLE);
							tuan_go.setImageResource(R.drawable.join_double_no);
						}
						else if(activity_type==13) {
							tuan_go.setImageResource(R.drawable.qxjy_buy);
							activity_go.setImageResource(R.drawable.qxjy_receiver);
							activity_go.setOnClickListener(new ImageView.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if(temp2.get(num).getIs_jion()==1) {
										openQXJYInfo();
									}
									else {
										receiveTickets(temp2.get(num));
									}
									
							}});
						}
						else if(activity_type==14) {
							activity_go.setVisibility(View.INVISIBLE);
							tuan_go.setImageResource(R.drawable.luckdraw);
						}
						else if(activity_type==15) {
							activity_go.setVisibility(View.INVISIBLE);
							tuan_go.setImageResource(R.drawable.jyb);
						}
						else if(activity_type==16) {
							activity_go.setVisibility(View.INVISIBLE);
							tuan_go.setImageResource(R.drawable.luckdraw);
						}
						else if(activity_type==17) {
							activity_go.setVisibility(View.INVISIBLE);
							tuan_go.setImageResource(R.drawable.movie_question_button);
						}
						else if(activity_type==18||activity_type==20||activity_type==22) {
							if(activity_type==18||activity_type==20) {
								activity_go.setImageResource(R.drawable.sharebutton);
							}
							else if(activity_type==22) {
								activity_go.setImageResource(R.drawable.want_red);
							}
							activity_go.setOnClickListener(new ImageView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub 
									final Dialog dialog=new Dialog(GonglveActivity.this, R.style.shareDialog);
									dialog.setCanceledOnTouchOutside(true);
									View view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.view_sharelayout, null);
									TextView gonglve_share_cancel=(TextView) view.findViewById(R.id.gonglve_share_cancel);
									gonglve_share_cancel.setOnClickListener(new TextView.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											dialog.dismiss();
										}});
									ImageView gonglve_qqkj_logo_share=(ImageView) view.findViewById(R.id.gonglve_qqkj_logo_share);
									gonglve_qqkj_logo_share.setOnClickListener(new ImageView.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialog.dismiss();
											((GasStationApplication) getApplicationContext()).shareType=6;
											if(activity_type==22) {
												((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId+"0");
											}
											else {
												((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId);
											}
											String currentUsedUrl="";
											try {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
											} catch(Exception e) {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
											}
											((GasStationApplication) getApplicationContext()).content=activity_rule;
											Intent intent=new Intent(GonglveActivity.this, QQActivity.class);
											Bundle bundle=new Bundle();
											bundle.putString("title", activity_name);
											if(activity_type==22) {
												bundle.putString("url", currentUsedUrl+activity_url+"?activityId="+(activityId+"0"));
												bundle.putString("text", "免费流量红包来了！抢！");
											}
											else {
												bundle.putString("url", currentUsedUrl+activity_url+"?activityId="+activityId);
												bundle.putString("text", activity_rule);
											}
											bundle.putString("send_imageUrl", "http://a2.mzstatic.com/us/r30/Purple6/v4/98/a8/48/98a84887-be7a-9402-24ce-59284e6bf0f8/mzl.rwwplqzr.175x175-75.jpg");
											bundle.putString("type", "qqkj");
											intent.putExtras(bundle);
											startActivity(intent);
										}});
									ImageView gonglve_yixin_pengyou_share=(ImageView) view.findViewById(R.id.gonglve_yixin_pengyou_share);
									gonglve_yixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialog.dismiss();
											((GasStationApplication) getApplicationContext()).shareType=5;
											if(activity_type==22) {
												((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId+"0");
											}
											else {
												((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId);
											}
											String currentUsedUrl="";
											try {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
											} catch(Exception e) {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
											}
											if(activity_type==22) {
												((GasStationApplication) getApplicationContext()).content="免费流量红包来了！抢！";
											}
											else {
												((GasStationApplication) getApplicationContext()).content=activity_rule;
											}
											SendYixin yixin=new SendYixin();
											if(activity_type!=22) {
												yixin.sendYixin(GonglveActivity.this,activity_rule, currentUsedUrl+activity_url+"?activityId="+activityId, activity_name, true);
											}
											else {
												yixin.sendYixin(GonglveActivity.this,"免费流量红包来了！抢！", currentUsedUrl+activity_url+"?activityId="+(activityId+"0"), activity_name, true);
											}
										}});
									ImageView gonglve_weixin_pengyou_share=(ImageView) view.findViewById(R.id.gonglve_weixin_pengyou_share);
									gonglve_weixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialog.dismiss();
											((GasStationApplication) getApplicationContext()).shareType=3;
											if(activity_type==22) {
												((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId+"0");
											}
											else {
												((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId);
											}
											String currentUsedUrl="";
											try {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
											} catch(Exception e) {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
											}
											if(activity_type==22) {
												((GasStationApplication) getApplicationContext()).content="免费流量红包来了！抢！";
											}
											else {
												((GasStationApplication) getApplicationContext()).content=activity_rule;
											}
											
											SendWeixin weixin=new SendWeixin();
											if(activity_type!=22) {
												weixin.sendWeixin(GonglveActivity.this, activity_name+"\n"+activity_rule, currentUsedUrl+activity_url+"?activityId="+activityId, activity_name+"\n"+activity_rule, true);
											}
											else {
												weixin.sendWeixin(GonglveActivity.this, activity_name+"\n"+"免费流量红包来了！抢！", currentUsedUrl+activity_url+"?activityId="+(activityId+"0"), activity_name+"\n"+"免费流量红包来了！抢！", true);
											}
										}});
									ImageView gonglve_sinaweibo_logo_share=(ImageView) view.findViewById(R.id.gonglve_sinaweibo_logo_share);
									gonglve_sinaweibo_logo_share.setOnClickListener(new ImageView.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialog.dismiss();
											((GasStationApplication) getApplicationContext()).shareType=4;
											if(activity_type==22) {
												((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId+"0");
											}
											else {
												((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId);
											}
											String currentUsedUrl="";
											try {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
											} catch(Exception e) {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
											}
											if(activity_type==22) {
												((GasStationApplication) getApplicationContext()).content="免费流量红包来了！抢！";
											}
											else {
												((GasStationApplication) getApplicationContext()).content=activity_rule;
											}
											Intent intent=new Intent(GonglveActivity.this, WBMainActivity.class);
											Bundle bundle=new Bundle();
											bundle.putString("title", activity_name);
											if(activity_type==22) {
												bundle.putString("url", currentUsedUrl+activity_url+"?activityId="+(activityId+"0"));
												bundle.putString("text", "免费流量红包来了！抢！");
											}
											else {
												bundle.putString("url", currentUsedUrl+activity_url+"?activityId="+activityId);
												bundle.putString("text", activity_rule);
											}
											bundle.putString("defaultText", "流量加油站");
											intent.putExtras(bundle);
											startActivity(intent);
										}});
									dialog.setContentView(view);
									dialog.show();
								}});
							if(activity_type==18) {
								tuan_go.setImageResource(R.drawable.qianggou_button);
							}
							else if(activity_type==22) {
								tuan_go.setImageResource(R.drawable.send_red);
							}
							else if(activity_type==20) {
								tuan_go.setImageResource(R.drawable.wap_button);
							}						
						}
						else if(activity_type==19) {
							activity_go.setImageResource(R.drawable.bussiness_button); 
							activity_go.setOnClickListener(new ImageView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent=new Intent(GonglveActivity.this, BusinessesActivity.class);
									startActivity(intent);
								}});
							tuan_go.setImageResource(R.drawable.member_button);	
						}
						else if(activity_type==21) {
							activity_2_go_2.setImageResource(R.drawable.web_goldcoin);
							activity_2_go_2.setOnClickListener(new ImageView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									receiveCoin(activityId);
								}});
							activity_2_go_1.setImageResource(R.drawable.sharebutton);
							activity_2_go_1.setOnClickListener(new ImageView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub 
									final Dialog dialog=new Dialog(GonglveActivity.this, R.style.shareDialog);
									dialog.setCanceledOnTouchOutside(true);
									View view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.view_sharelayout, null);
									TextView gonglve_share_cancel=(TextView) view.findViewById(R.id.gonglve_share_cancel);
									gonglve_share_cancel.setOnClickListener(new TextView.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											dialog.dismiss();
										}});
									ImageView gonglve_qqkj_logo_share=(ImageView) view.findViewById(R.id.gonglve_qqkj_logo_share);
									gonglve_qqkj_logo_share.setOnClickListener(new ImageView.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialog.dismiss();
											((GasStationApplication) getApplicationContext()).shareType=6;
											((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId);
											String currentUsedUrl="";
											try {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
											} catch(Exception e) {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
											}
											((GasStationApplication) getApplicationContext()).content=activity_rule;
											Intent intent=new Intent(GonglveActivity.this, QQActivity.class);
											Bundle bundle=new Bundle();
											bundle.putString("title", activity_name);
											bundle.putString("url", currentUsedUrl+activity_url+"?activityId="+activityId);
											bundle.putString("text", activity_rule);
											bundle.putString("send_imageUrl", "http://a2.mzstatic.com/us/r30/Purple6/v4/98/a8/48/98a84887-be7a-9402-24ce-59284e6bf0f8/mzl.rwwplqzr.175x175-75.jpg");
											bundle.putString("type", "qqkj");
											intent.putExtras(bundle);
											startActivity(intent);
										}});
									ImageView gonglve_yixin_pengyou_share=(ImageView) view.findViewById(R.id.gonglve_yixin_pengyou_share);
									gonglve_yixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialog.dismiss();
											((GasStationApplication) getApplicationContext()).shareType=5;
											((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId);
											String currentUsedUrl="";
											try {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
											} catch(Exception e) {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
											}
											((GasStationApplication) getApplicationContext()).content=activity_rule;
											SendYixin yixin=new SendYixin();
											yixin.sendYixin(GonglveActivity.this,activity_rule, currentUsedUrl+activity_url+"?activityId="+activityId, activity_name, true);
										}});
									ImageView gonglve_weixin_pengyou_share=(ImageView) view.findViewById(R.id.gonglve_weixin_pengyou_share);
									gonglve_weixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialog.dismiss();
											((GasStationApplication) getApplicationContext()).shareType=3;
											((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId);
											String currentUsedUrl="";
											try {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
											} catch(Exception e) {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
											}
											((GasStationApplication) getApplicationContext()).content=activity_rule;
											SendWeixin weixin=new SendWeixin();
											weixin.sendWeixin(GonglveActivity.this, activity_name+"\n"+activity_rule, currentUsedUrl+activity_url+"?activityId="+activityId, activity_name+"\n"+activity_rule, true);
										}});
									ImageView gonglve_sinaweibo_logo_share=(ImageView) view.findViewById(R.id.gonglve_sinaweibo_logo_share);
									gonglve_sinaweibo_logo_share.setOnClickListener(new ImageView.OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialog.dismiss();
											((GasStationApplication) getApplicationContext()).shareType=4;
											((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(activityId);
											String currentUsedUrl="";
											try {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
											} catch(Exception e) {
												currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
											}
											((GasStationApplication) getApplicationContext()).content=activity_rule;
											Intent intent=new Intent(GonglveActivity.this, WBMainActivity.class);
											Bundle bundle=new Bundle();
											bundle.putString("title", activity_name);
											bundle.putString("url", currentUsedUrl+activity_url+"?activityId="+activityId);
											bundle.putString("text", activity_rule);
											bundle.putString("defaultText", "流量加油站");
											intent.putExtras(bundle);
											startActivity(intent);
										}});
									dialog.setContentView(view);
									dialog.show();
								}});
							tuan_go.setImageResource(R.drawable.wap_button);
						}
						else if(activity_type==23) {
							activity_go.setImageResource(R.drawable.qianggou_button);
							activity_go.setOnClickListener(new ImageView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent=new Intent(GonglveActivity.this, RecordListActivity.class);
									startActivity(intent);
								}});
							tuan_go.setImageResource(R.drawable.qianggou_button);
						}
						tuan_go.setOnClickListener(new ImageView.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(activity_type==2) {
									((GasStationApplication) getApplicationContext()).jumpJiayouFrom=4;
									Intent intent=new Intent();
									intent.setClass(GonglveActivity.this, JiayouCardDetailActivity.class);
									Bundle bundle=new Bundle();
									bundle.putString("from", "tuangou");
									bundle.putString("cost", cost);
									bundle.putString("amount", amount);
									bundle.putString("desp", "流量团购  ");
									bundle.putString("offer_id", offer_id);
									intent.putExtras(bundle);
									startActivity(intent);
								}
								else if(activity_type==3) {
									Intent intent=new Intent();
									intent.setClass(GonglveActivity.this, ShareActivity_New.class);
									startActivity(intent);
								}
								else if(activity_type==4) {
									loadJumpActivity(temp2.get(num).getActivity_id(), Integer.parseInt(temp2.get(num).getActivity_type()));
								}
								else if(activity_type==5) {
									loadJumpActivity(temp2.get(num).getActivity_id(), Integer.parseInt(temp2.get(num).getActivity_type()));
								}
								else if(activity_type==6) {
									Intent intent=new Intent(GonglveActivity.this, ShakeActivity.class);
									Bundle bundle=new Bundle();
									bundle.putString("activityId", temp2.get(num).getActivity_id());
									intent.putExtras(bundle);
									startActivity(intent);								
								}
								else if(activity_type==7) {
									if(temp2.get(num).getIs_jion()==0) {
										checkIsValid(temp2.get(num).getActivity_id(), cost, amount);
									}
								}
								else if(activity_type==8) {
									MainActivity.getInstance().jumpToJiayou(0, 4);
								}
								else if(activity_type==9) {
									checkWulinAssemblyRole();
								}
								else if(activity_type==10) {
									checkReceiveCarFlow(Long.parseLong(temp2.get(num).getActivity_id()));
								}
								else if(activity_type==11) {
									getDouble_flow_partake2(Long.parseLong(temp2.get(num).getActivity_id()), temp2.get(num).getActivity_url());
								}
								else if(activity_type==12) {
									Intent intent=new Intent(GonglveActivity.this, Movie_Choice_Activity.class);
									Bundle bundle=new Bundle();
									bundle.putString("activity_id", temp2.get(num).getActivity_id());
									intent.putExtras(bundle);
									startActivity(intent);
								}
								else if(activity_type==13) {			
									loadQXJY(temp2.get(num).getActivity_id(), Integer.parseInt(temp2.get(num).getActivity_type()));
								}
								else if(activity_type==14) {
									Intent intent=new Intent(GonglveActivity.this, LuckDrawActivity.class);
									Bundle bundle=new Bundle();
									bundle.putString("activityName", temp2.get(num).getActivity_name());
									bundle.putLong("activityId", Long.parseLong(temp2.get(num).getActivity_id()));
									bundle.putString("desp", temp2.get(num).getActivity_url());
									intent.putExtras(bundle);
									startActivity(intent);
								}
								else if(activity_type==15) {
									Intent intent=new Intent(GonglveActivity.this, TreasureMainActivity.class);
									startActivity(intent);
								}
								else if(activity_type==16) {
									Intent intent=new Intent(GonglveActivity.this, LuckDrawActivity.class);
									Bundle bundle=new Bundle();
									bundle.putString("activityName", temp2.get(num).getActivity_name());
									bundle.putLong("activityId", Long.parseLong(temp2.get(num).getActivity_id()));
									bundle.putString("desp", temp2.get(num).getActivity_url());
									intent.putExtras(bundle);
									startActivity(intent);
								}
								else if(activity_type==17) {
									Intent intent=new Intent(GonglveActivity.this, MovieQuestionActivity.class);
									Bundle bundle=new Bundle();
									bundle.putString("desp", temp2.get(num).getActivity_url());
									intent.putExtras(bundle);
									startActivity(intent);
								}
								else if(activity_type==18) {
									loadJumpActivity(temp2.get(num).getActivity_id(), Integer.parseInt(temp2.get(num).getActivity_type()));
								}
								else if(activity_type==19) {
									Intent intent=new Intent(GonglveActivity.this, MemberActivity.class);
									startActivity(intent);
								}
								else if(activity_type==20||activity_type==21) {
									String currentUsedUrl="";
									try {
										currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
									} catch(Exception e) {
										currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
									}
									Uri url=Uri.parse(currentUsedUrl+activity_url+"?activityId="+activityId); 
									Intent it = new Intent(Intent.ACTION_VIEW, url);   
									startActivity(it);
								}
								else if(activity_type==22) {
									Intent intent_temp=new Intent(GonglveActivity.this, SendPacketsActivity.class);
									Bundle bundle_temp=new Bundle();
									bundle_temp.putString("activityId", ""+activityId);
									bundle_temp.putString("activity_rule", activity_rule);
									bundle_temp.putString("activity_name", activity_name);
									bundle_temp.putString("activity_url", activity_url);
									intent_temp.putExtras(bundle_temp);
									startActivity(intent_temp);
								}
								else if(activity_type==23) {
									loadJumpActivity(temp2.get(num).getActivity_id(), Integer.parseInt(temp2.get(num).getActivity_type()));
								}
							}});
						LinearLayout tangou_point=(LinearLayout) view.findViewById(R.id.tangou_point);
						if(temp2.size()>1) {
							for(int j=0;j<temp2.size();j++) {
								ImageView image=new ImageView(GonglveActivity.this);
								if(j==i) {
									image.setImageResource(R.drawable.intro_select);
								}
								else {
									image.setImageResource(R.drawable.intro_noselect);
								}
								LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								params.leftMargin=5;
								params.rightMargin=5;
								tangou_point.addView(image, params);
							}
						}						
						gonglve_tuan.addView(view);
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								
								if(refreshNum!=0) {
									System.out.println(refreshNum);
									boolean isFind=false;
									for(int i=0;i<modelList2.size();i++) {
										if(Integer.parseInt(modelList2.get(i).getActivity_id())==refreshNum) {
											gonglve_tuan.snapToScreen(i);
											refreshNum=0;
											((GasStationApplication) getApplication()).webTab=0;
											isFind=true;
										}
									}
									if(!isFind) {
										refreshNum=0;
										((GasStationApplication) getApplication()).webTab=0;
									}
								}
							}
						}, 1000);
						
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				if(defaultLoad==1) {
					gonglve_tuan_layout.setVisibility(View.VISIBLE);
					if(msg.what==1) {
						if(gonglve_tuan.getChildCount() == 0) {
							gonglve_tuan.setVisibility(View.GONE);
							gonglve_tip_layout.setVisibility(View.VISIBLE);
						}
						else {
							gonglve_tuan.setVisibility(View.VISIBLE);
							gonglve_tip_layout.setVisibility(View.GONE);
						}
					}
					else {
						gonglve_tuan.setVisibility(View.GONE);
						gonglve_tip_layout.setBackgroundResource(R.drawable.net_error);
						gonglve_tip_layout.setVisibility(View.VISIBLE);
					}
				}				
				isLoadActivity=false;
				if(!isLoadActivity&&!isLoadBank) {
					title_refresh.setVisibility(View.VISIBLE);
					title_refresh_progress.setVisibility(View.INVISIBLE);
					MainActivity.getInstance().able_change();
				}
			}
			
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				//所有已支持的活动类型
				HashMap<String, String> map_allowed=new HashMap<String, String>();
				for(int j=0;j<allowedType.length;j++) {
					map_allowed.put(""+allowedType[j], ""+allowedType[j]);
				}
				//流量活动列表
				ArrayList<GonglveTuanModel> temp2=new ArrayList<GonglveTuanModel>();
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.getActivityList(Long.parseLong(list.get(0)), list.get(1));
						for(int i=0;i<map.length;i++) {
							GonglveTuanModel model=new GonglveTuanModel();
							model.setActivity_id(map[i].get("activity_id").toString());
							model.setActivity_type(map[i].get("activity_type").toString());
							//判断是否为不支持的新活动
							if(!map_allowed.containsKey(map[i].get("activity_type").toString())) {
								continue;
							}
							model.setActivity_name(map[i].get("activity_name").toString());
							model.setActivity_end_time(map[i].get("activity_end_time").toString());
							String tempPic="http://su.bdimg.com/static/skin/img/logo_white.png";
							if(map[i].get("activity_image_name")!=null) {
								tempPic=currentUsedUrl+map[i].get("activity_image_name").toString();
							}
							DisplayMetrics dm=new DisplayMetrics();
							getWindowManager().getDefaultDisplay().getMetrics(dm);
							String extra="";
							if(dm.density==1.0) {
								extra="_480";
							}
							else if(dm.density==1.5) {
								extra="_800";
							}
							else {
								extra="_1280";
							}
							model.setActivity_image_name(tempPic.substring(0, tempPic.lastIndexOf("."))+extra+tempPic.substring(tempPic.lastIndexOf(".")));						
							model.setOffer_amount(map[i].get("offer_amount")==null?"":map[i].get("offer_amount").toString());
							model.setOffer_cost(map[i].get("offer_cost")==null?"":map[i].get("offer_cost").toString());
							model.setOld_cost(map[i].get("old_cost")==null?"":map[i].get("old_cost").toString());
							model.setPersons(map[i].get("persons")==null?"":map[i].get("persons").toString());
							model.setOffer_id(map[i].get("offer_id")==null?"":map[i].get("offer_id").toString());
							model.setActivity_description(map[i].get("activity_description")==null?"":map[i].get("activity_description").toString());
							model.setIs_jion(map[i].get("is_jion")==null?0:Integer.parseInt(map[i].get("is_jion").toString()));
							model.setUnrecevice_num(map[i].get("unrecevice_num")==null?0:Double.parseDouble(map[i].get("unrecevice_num").toString()));
							model.setTotal_num(map[i].get("total_num")==null?0:Integer.parseInt(map[i].get("total_num").toString()));
							model.setIs_sign(map[i].get("is_sign")==null?0:Integer.parseInt(map[i].get("is_sign").toString()));
							model.setActivity_url(map[i].get("activity_url")==null?"":map[i].get("activity_url").toString());
							model.setOffer_name(map[i].get("offer_name")==null?"":map[i].get("offer_name").toString());
							model.setActivity_rule(map[i].get("activity_rule")==null?"":map[i].get("activity_rule").toString());
							temp2.add(model);
						}
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
				Bundle bundle=new Bundle();
				bundle.putParcelableArrayList("adapter2", temp2);
				m.setData(bundle);
				handler.sendMessage(m);
			}}).start();
	}
	
	private void receiveCoin(final String activityId) {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				switch (msg.what) {
				case -1:
					showCustomToast(getResources().getString(R.string.timeout_exp));
					break;
				case -2:
					showCustomToast("链路连接失败");
				case 100:
					Map result=(Map) msg.obj;
					showCustomToast(result.get("comments").toString());
					break;
				default:
					break;
				}
			
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map result=strategyManager.receiveCoin(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(activityId));
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
	
	//弹出框选择套餐活动活动
	private void loadJumpActivity(final String activity_id, final int activity_type) {
		showProgressDialog(R.string.tishi_loading);
		
		final Handler gonglueHandler = new Handler() {
			public void handleMessage(Message msg) {
				dismissProgressDialog();
				switch (msg.what) {
				case -1:
					showCustomToast(getResources().getString(R.string.timeout_exp));
					break;
				case -2:
					showCustomToast("链路连接失败");
				case 100:
					Map[] map = (Map[]) msg.obj;
					ArrayList<String> list = new ArrayList<String>();
					for(int i = 0; i < map.length ; i ++) {
						String str1="";
						String str2="";
						String str3="";
						if(activity_type==4) {
							str1 = (String) map[i].get("offer_id");
							str2 = (String) map[i].get("offer_name");
							str3 = (String) map[i].get("offer_name2");
						}
						else if(activity_type==18) {
							str1 = (String) map[i].get("offer_id");
							str2 = (String) map[i].get("offer_name");
							str3 = "";
						}
						else if(activity_type==23) {
							str1 = (String) map[i].get("offer_id");
							str2 = (String) map[i].get("offer_name");
							str3 = "";
						}
						else {
							str1 = (String) map[i].get("id");
							str2 = (String) map[i].get("offer_name")+"$"+(String) map[i].get("offer_name3");
							str3 = (String) map[i].get("offer_name2");
						}
						String object = str2+"&"+str3+"&"+str1+"&0&0";
						list.add(object);
					}
					if(list.size() == 0) {
						showCustomToast("当前无可订购套餐。");
						return;
					}
					Intent intent=new Intent(GonglveActivity.this, SelectTypeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("list", list);
					bundle.putInt("activity_id", Integer.parseInt(activity_id));
					bundle.putInt("activity_type", activity_type);
					intent.putExtras(bundle);
					startActivity(intent);
					break;
				default:
					break;
				}
			};
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] result=strategyManager.activity6_packages(Long.parseLong(list.get(0)), Long.parseLong(activity_id), list.get(1));
						for(int i=0;i<result.length;i++) {
							System.out.println(result[i]);
						}
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
				
				gonglueHandler.sendMessage(m);				
			}}).start();
	}
	
	/**
	 * 全心券意领取
	 * @param model
	 */
	private void receiveTickets(final GonglveTuanModel model) {
		showProgressDialog(R.string.tishi_loading);
		final Handler receiveTickets_handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				switch (msg.what) {
				case -1:
					showCustomToast(getResources().getString(R.string.timeout_exp));
					break;
				case -2:
					showCustomToast("链路连接失败");
				case 100:
					Map map = (Map) msg.obj;
					if(Integer.parseInt(map.get("result").toString())==1) {
						model.setIs_jion(1);  
						openQXJYInfo();
					}
					showCustomToast(map.get("comments").toString());
					break;
				default:
					break;
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map result=strategyManager.receiveTickets(Long.parseLong(list.get(0)), list.get(1));
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
				
				receiveTickets_handler.sendMessage(m);				
			}
		}).start();
	}
	
	/**
	 * 全心券意用户券信息
	 */
	private void openQXJYInfo() {
		Intent intent=new Intent(GonglveActivity.this, QXJYReceiverActivity.class);
		startActivity(intent);
	}
	
	//弹出框选择全心券意活动
	private void loadQXJY(final String activity_id, final int activity_type) {
		showProgressDialog(R.string.tishi_loading);
		
		final Handler gonglueHandler = new Handler() {
			public void handleMessage(Message msg) {
				dismissProgressDialog();
				switch (msg.what) {
				case -1:
					showCustomToast(getResources().getString(R.string.timeout_exp));
					break;
				case -2:
					showCustomToast("链路连接失败");
				case 100:
					Map[] map = (Map[]) msg.obj;
					ArrayList<String> list = new ArrayList<String>();
					for(int i = 0; i < map.length ; i ++) {
						String str1="";
						String str2="";
						String str3="";
						str1 = (String) map[i].get("offer_id");
						str2 = (String) map[i].get("offer_name");
						String object = str2+"&"+str3+"&"+str1+"&"+map[i].get("cost")+"&"+map[i].get("offer_type_id");
						list.add(object);
					}
					if(list.size() == 0) {
						showCustomToast("当前无可订购套餐。");
						return;
					}
					Intent intent=new Intent(GonglveActivity.this, SelectTypeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("list", list);
					bundle.putInt("activity_id", Integer.parseInt(activity_id));
					bundle.putInt("activity_type", activity_type);
					intent.putExtras(bundle);
					startActivity(intent);
					break;
				default:
					break;
				}
			};
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] result=strategyManager.activity26_packages(Long.parseLong(list.get(0)), Long.parseLong(activity_id), list.get(1));
						for(int i=0;i<result.length;i++) {
							System.out.println(result[i]);
						}
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
				
				gonglueHandler.sendMessage(m);				
			}}).start();
	}
	
	
	/**
	 * 倍增计划查询用户当前流量信息
	 * @param activityId
	 * @param activity_url
	 */
	public void get_double_flow_select(final String activityId, final String activity_url) {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==-1) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==1) {
					Map map=(Map)msg.obj;
					Intent intent=new Intent(GonglveActivity.this, Double_flow_operate_activity.class);
					Bundle bundle=new Bundle();
					bundle.putString("activityId", activityId);
					bundle.putString("total_num", map.get("total_flow").toString());
					bundle.putString("unrecevice_num", map.get("residue_flow").toString());
					bundle.putString("activity_url", activity_url);
					intent.putExtras(bundle);
					startActivity(intent);
				}
				else if(msg.what==2) {
					showCustomToast("获取倍赠计划数据出现异常");
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map result=strategyManager.double_flow_select(Long.parseLong(list.get(0)), Long.parseLong(activityId), list.get(1));
						if(result==null) {
							m.what=2;
						}
						else {
							m.what=1;
							m.obj=result;
						}
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
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(((GasStationApplication) getApplication()).webTab!=0) {
			defaultLoad=1;
			gonglve_tuan_layout.setVisibility(View.VISIBLE);
			gonglve_bank_layout.setVisibility(View.GONE);
			gonglve_choice_l.setImageResource(R.drawable.gl_left_on);
			gonglve_choice_r.setImageResource(R.drawable.gl_right);
			refreshNum=((GasStationApplication) getApplication()).webTab;
			if(modelList2.size()>0&&!isLoadActivity) {
				boolean isFind=false;
				for(int i=0;i<modelList2.size();i++) {
					if(Integer.parseInt(modelList2.get(i).getActivity_id())==((GasStationApplication) getApplication()).webTab) {
						gonglve_tuan.snapToScreen(i);
						((GasStationApplication) getApplication()).webTab=0;
						refreshNum=0;
						isFind=true;
					}
				}
				if(!isFind) {
					((GasStationApplication) getApplication()).webTab=0;
					refreshNum=0;
				}
			}
		}
		
		if(((GasStationApplication) getApplicationContext()).isRefreshTuan) {
			gonglve_tuan_layout.setVisibility(View.VISIBLE);
			gonglve_bank_layout.setVisibility(View.GONE);
			gonglve_tuan.removeAllViews();
			MainActivity.getInstance().not_able_change();
			loadCustomTuan();
		}
		if(((GasStationApplication) getApplicationContext()).isJumpToTuan) {
			((GasStationApplication) getApplicationContext()).isJumpToTuan=false;
		}
		
		timer=new Timer();
		timetask=new UploadTimer();
		timer.schedule(timetask, new Date(), 1000);
		
		if(initPhoneNum!=-1&&initPhoneNum!=Long.parseLong(Util.getUserInfo(GonglveActivity.this).get(0))) {
			if(((GasStationApplication) getApplication()).flowBankNum!=-1) {
				defaultLoad=2;
				((GasStationApplication) getApplication()).flowBankNum=-1;
				gonglve_choice_l.setImageResource(R.drawable.gl_left);
				gonglve_choice_r.setImageResource(R.drawable.gl_right_on);
			}
			else {
				defaultLoad=1;
				gonglve_choice_l.setImageResource(R.drawable.gl_left_on);
				gonglve_choice_r.setImageResource(R.drawable.gl_right);
			}
			gonglve_tuan_layout.setVisibility(View.GONE);
			gonglve_bank_layout.setVisibility(View.GONE);
			gonglve_tuan.removeAllViews();
			gonglve_bank_layout.removeAllViews();
			
			MainActivity.getInstance().not_able_change();
			loadCustomTuan();			
			if(Util.getUserArea(GonglveActivity.this).equals("0971")) {
				loadNoMarketView();
			}
			else {
				loadMarketView();
			}
			initPhoneNum=Long.parseLong(Util.getUserInfo(GonglveActivity.this).get(0));
		}
		//一般情况或者切换号码之和仍然用原号码登陆
		else if(initPhoneNum!=-1&&initPhoneNum==Long.parseLong(Util.getUserInfo(GonglveActivity.this).get(0))&&!isLoadActivity&&!isLoadBank) {
			if(((GasStationApplication) getApplication()).flowBankNum!=-1) {
				defaultLoad=2;
				((GasStationApplication) getApplication()).flowBankNum=-1;
				gonglve_choice_l.setImageResource(R.drawable.gl_left);
				gonglve_choice_r.setImageResource(R.drawable.gl_right_on);
				gonglve_tuan_layout.setVisibility(View.GONE);
				gonglve_bank_layout.setVisibility(View.VISIBLE);
			}
			MainActivity.getInstance().able_change();
		}
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timer.cancel();
		StatService.onPause(this);
	}
	
	class UploadTimer extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message m=new Message();
			handler_updateTime.sendMessage(m);
		}		
	}
	
	Handler handler_updateTime=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(gonglve_tuan!=null) {
				if(gonglve_tuan.getChildCount()>0) {
					for(int i=0;i<gonglve_tuan.getChildCount();i++) {
						View view=gonglve_tuan.getChildAt(i);
						TextView activity_end_time=(TextView) view.findViewById(R.id.activity_end_time);
						activity_end_time.setText(Util.getLastTime(activity_end_time.getTag().toString()));
					}
				}
			}
		}
	};
	
	/**
	 * 分享活动
	 * @param RephoneNum
	 */
	public void joinActivity(final String RephoneNum, final ImageView activity_go, final GonglveTuanModel model, final TextView activity_text) {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				
				if(msg.what==-1) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else {
					Map result=(Map) msg.obj;
					showCustomToast(result.get("comments").toString());
					activity_go.setImageResource(R.drawable.join_button2);
					activity_text.setText(Html.fromHtml("您当前还有<font color='red'>"+((int) (Double.parseDouble(result.get("unrecevice_num").toString())*100))+"M</font>流量未领取"));
					model.setIs_jion(1);
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map result=strategyManager.joinActivity0510(Long.parseLong(list.get(0)), RephoneNum, list.get(1));
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
			}}).start();
	}
	
	/**
	 * 领取流量
	 */
	public void receiveFlow(final Long offerId, final TextView activity_text, final ImageView activity_go) {
		showProgressDialog(R.string.tishi);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				activity_go.setClickable(true);
				activity_go.setEnabled(true);
				if(msg.what==-1) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else {
					Map result=(Map) msg.obj;
					showCustomToast(result.get("comments").toString());
					if(Integer.parseInt(result.get("result").toString())==1) {
						activity_result_layout.setVisibility(View.VISIBLE);
						activity_text.setText(Html.fromHtml("您当前还有<font color='red'>"+((int) (Double.parseDouble(result.get("unrecevice_num").toString())*100))+"M</font>流量未领取"));
						((GasStationApplication) getApplicationContext()).isJumpToMonitor=true;
						TranslateAnimation animation=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 2.5f);
						animation.setDuration(3000);
						animation.setAnimationListener(new AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								// TODO Auto-generated method stub		
								MainActivity.getInstance().jumpToHome();
								activity_result_layout.setVisibility(View.GONE);
							}
						});
						activity_result_layout.startAnimation(animation);
					}
				}
				lastClickShare=System.currentTimeMillis();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map result=strategyManager.receiveFlow0510(Long.parseLong(list.get(0)), offerId, list.get(1));
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
			}}).start();
	}
	
	/**
	 * 领取倍增计划流量
	 */
	public void getDouble_flow_partake2(final long activityId, final String activity_url) {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==-1) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else {
					final Map result=(Map) msg.obj;
					if(result.get("result").toString().equals("1")) {
						new AlertDialog.Builder(GonglveActivity.this).setTitle("参与成功，您已获赠"+result.get("total_flow").toString()+"M流量").setMessage("上月流量费用为："+result.get("charge").toString()+"元\n"
								+"赠送流量为："+result.get("total_flow").toString()+"M\n"+activity_url).setPositiveButton("确定", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Intent intent=new Intent(GonglveActivity.this, CarActivity.class);
										Bundle bundle=new Bundle();
										bundle.putString("total_flow", result.get("total_flow").toString());
										bundle.putString("residue_flow", result.get("residue_flow").toString());
										bundle.putString("activityId", ""+activityId);
										bundle.putBoolean("needRefresh", true);
										intent.putExtras(bundle);
										startActivity(intent);
									}}).show();

					}
					else if(result.get("result").toString().equals("2")) {
						Intent intent=new Intent(GonglveActivity.this, CarActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString("total_flow", result.get("total_flow").toString());
						bundle.putString("residue_flow", result.get("residue_flow").toString());
						bundle.putString("activityId", ""+activityId);
						bundle.putBoolean("needRefresh", true);
						intent.putExtras(bundle);
						startActivity(intent);
					}
					else {
						showCustomToast(result.get("comments").toString());
					}
				}	
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map result=strategyManager.double_flow_partake2(Long.parseLong(list.get(0)), list.get(1), activityId);
						m.what=1;
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
			}}).start();
	}
	
	/**
	 * 验证用户是否合法
	 * @param activity_id
	 * @param cost
	 * @param amount
	 */
	public void checkIsValid(final String activity_id, final String cost, final String amount) {
		
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				Map map=(Map) msg.obj;
				if(msg.what==1) {
					if(map.get("result").toString().equals("1")) {
						Intent intent=new Intent();
						intent.setClass(GonglveActivity.this, JiayouCardDetailActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString("from", "double_flow");
						bundle.putString("activity_id", activity_id);
						bundle.putString("cost", cost);
						bundle.putString("amount", amount);
						bundle.putString("desp", "流量倍增计划  ");
						intent.putExtras(bundle);
						startActivity(intent);
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.validUser(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(activity_id));
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
	 * 查看武林大会角色信息
	 */
	public void checkWulinAssemblyRole() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					if(map.get("result").toString().equals("4")) {
						showCustomToast(map.get("comments").toString());
					}
					else {
						Intent intent=new Intent(GonglveActivity.this, WulinAssemblyActivity.class);
						Bundle bundle=new Bundle();
						if(map.get("result").toString().equals("1")) {
							if(map.get("isInit").toString().equals("1")) {
								bundle.putInt("WulinAssemblyType", 2);
								bundle.putLong("unionId", Long.parseLong(map.get("unionId").toString()));
							}
							else if(map.get("isInit").toString().equals("2")) {
								bundle.putInt("WulinAssemblyType", 1);
								bundle.putLong("unionId", Long.parseLong(map.get("unionId").toString()));
							}
						}
						if(map.get("result").toString().equals("2")) {
							if(map.get("isInit").toString().equals("1")) {
								bundle.putInt("WulinAssemblyType", 4);
								bundle.putLong("tribeId", Long.parseLong(map.get("tribeId").toString()));
							}
							else if(map.get("isInit").toString().equals("2")) {
								bundle.putInt("WulinAssemblyType", 3);
								bundle.putLong("tribeId", Long.parseLong(map.get("tribeId").toString()));
							}
						}
						if(map.get("result").toString().equals("3")) {
							bundle.putInt("WulinAssemblyType", 5);
							bundle.putString("residue_flow", map.get("residue_flow").toString());
							bundle.putString("total_flow", map.get("total_flow").toString());
						}
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==2) {
					showCustomToast("获取人员角色信息失败");
				}
				else if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getWulinRole(Long.parseLong(list.get(0)), list.get(1));
						if(map==null) {
							m.what=2;
						}
						else {
							m.obj=map;
							m.what=1;
						}
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-1;
			        } catch(Exception e) {
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
	
	public void checkReceiveCarFlow(final Long activityId) {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					if(map.get("result").toString().equals("1")) {
						Map map_result=(Map)map.get("data");
						Intent intent=new Intent(GonglveActivity.this, CarActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString("total_flow", map_result.get("total_flow").toString());
						bundle.putString("residue_flow", map_result.get("residue_flow").toString());
						bundle.putString("activityId", ""+activityId);
						bundle.putBoolean("needRefresh", false);
						intent.putExtras(bundle);
						startActivity(intent);
					}
					else if(map.get("result").toString().equals("4")) {
						showCustomToast(map.get("comments").toString());
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getFreeFlow(Long.parseLong(list.get(0)), list.get(1), activityId);
						m.obj=map;
						m.what=1;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-1;
			        } catch(Exception e) {
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
	 * 加载流量超市界面
	 */
	public void loadMarketView() {
		
		isLoadBank=false;
		gonglve_bank_layout.removeAllViews();
//		View view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.activity_market_view, null);
//		final ImageView market_24_pack=(ImageView) view.findViewById(R.id.market_24_pack);
//		final ImageView market_pack=(ImageView) view.findViewById(R.id.market_pack);
//		final ImageView market_fft=(ImageView) view.findViewById(R.id.market_fft);
//		market_24_pack.setOnClickListener(new ImageView.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				market_24_pack.setImageResource(R.drawable.market_24_pack_press);
//				market_pack.setImageResource(R.drawable.market_pack_nor);
//				market_fft.setImageResource(R.drawable.market_fft_nor);
//			}});		
//		market_pack.setOnClickListener(new ImageView.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				market_24_pack.setImageResource(R.drawable.market_24_pack_nor);
//				market_pack.setImageResource(R.drawable.market_pack_press);
//				market_fft.setImageResource(R.drawable.market_fft_nor);
//				Intent intent_=new Intent(GonglveActivity.this, DirectionalFlowActivity.class);
//				startActivity(intent_);
//			}});
//		market_fft.setOnClickListener(new ImageView.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				market_24_pack.setImageResource(R.drawable.market_24_pack_nor);
//				market_pack.setImageResource(R.drawable.market_pack_nor);
//				market_fft.setImageResource(R.drawable.market_fft_press);
//				Intent intent_=new Intent(GonglveActivity.this, LifeMainActivity.class);
//				startActivity(intent_);
//			}});
//		DiscView discview=(DiscView) view.findViewById(R.id.market_disc);
//		discview.setOnPositionListener(new OnPositionListener() {
//			
//			@Override
//			public void getPosition(int pos) {
//				// TODO Auto-generated method stub
//				switch(pos) {
//				case 0:
//					market_24_pack.setImageResource(R.drawable.market_24_pack_nor);
//					market_pack.setImageResource(R.drawable.market_pack_press);
//					market_fft.setImageResource(R.drawable.market_fft_nor);
//					break;
//				case 1:
//					market_24_pack.setImageResource(R.drawable.market_24_pack_press);
//					market_pack.setImageResource(R.drawable.market_pack_nor);
//					market_fft.setImageResource(R.drawable.market_fft_nor);
//					break;
//				case 2:
//					market_24_pack.setImageResource(R.drawable.market_24_pack_nor);
//					market_pack.setImageResource(R.drawable.market_pack_nor);
//					market_fft.setImageResource(R.drawable.market_fft_press);
//					break;
//				case 3:
//					break;
//				}
//			}
//		});		
		
		View view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.activity_market_new, null);
		market_adv_num_layout=(LinearLayout) view.findViewById(R.id.market_adv_num_layout);
		market_adv=(AutoScrollViewPager) view.findViewById(R.id.market_adv);
		market_layout=(LinearLayout) view.findViewById(R.id.market_layout);
		setViewPagerScrollSpeed(market_adv);
		
		gonglve_bank_layout.addView(view);
		
		ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
		if(list.get(1).equals("0510")) {
			subject_doSearch();
			addAdvLayoutView();
		}
		else {
			subject_doSearch_other();
			addAdvLayoutView_other();
		}
	}
	
	private void loadNoMarketView() {
		isLoadBank=false;
		gonglve_bank_layout.removeAllViews();
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity=Gravity.CENTER;
		ImageView image=new ImageView(GonglveActivity.this);
		image.setImageResource(R.drawable.no_huodong_market);
		gonglve_bank_layout.addView(image, params);
	}
	
	private void setViewPagerScrollSpeed(ViewPager market_adv) {  
        try {  
            Field mScroller = null;  
            mScroller = ViewPager.class.getDeclaredField("mScroller");  
            mScroller.setAccessible(true);   
            FixedSpeedScroller scroller = new FixedSpeedScroller(market_adv.getContext());  
            mScroller.set(market_adv, scroller);  
        } catch (NoSuchFieldException e){  
              
        } catch (IllegalArgumentException e){  
              
        } catch (IllegalAccessException e){  
              
        }  
    }
	
	/**
	 * 加载广告条
	 */
	private void subject_doSearch() {
		final int count=3;
		for(int i=0;i<count;i++) {
			final int pos_=i;
			View view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.advitem, null);
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch(pos_%count) {
					case 0:
						Intent intent_1=new Intent(GonglveActivity.this, LifeMainActivity.class);
						startActivity(intent_1);								
						break;
					case 1:
						Intent intent_2=new Intent(GonglveActivity.this, DirectionalFlowActivity.class);
						startActivity(intent_2);
						break;
					case 2:
						Intent intent_3=new Intent(GonglveActivity.this, TreasureMainActivity.class);
						startActivity(intent_3);
						break;
					}
				}
			});
			if(i==0) {
				view.setBackgroundResource(R.drawable.adv_default);
			}
			else if(i==1) {
				view.setBackgroundResource(R.drawable.adv_default2);
			}
			else {
				view.setBackgroundResource(R.drawable.adv_default3);
			}
			advView.add(view);
			
			ImageView image=(ImageView)LayoutInflater.from(GonglveActivity.this).inflate(R.layout.adv_point_image, null);
			if(i==0) {
				image.setImageResource(R.drawable.shareloc_online);
			}
			else {
				image.setImageResource(R.drawable.shareloc_offline);
			}
			pointView.add(image);
			market_adv_num_layout.addView(image);	
		}
		adapter=new MarketNewAdapter(advView);
		market_adv.setAdapter(adapter);
		market_adv.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				currentItem=arg0;
				for(int i=0;i<market_adv_num_layout.getChildCount();i++) {
					pointView.get(i).setImageResource(R.drawable.shareloc_offline);
				}
				pointView.get(arg0%market_adv_num_layout.getChildCount()).setImageResource(R.drawable.shareloc_online);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}			
			
		});
		market_adv.startAutoScroll();
	}
	
	/**
	 * 加载广告条
	 */
	private void subject_doSearch_other() {

		// TODO Auto-generated method stub
		final int count=2;
		for(int i=0;i<count*2;i++) {
			final int pos_=i;
			View view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.advitem, null);
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch(pos_%count) {
					case 0:
						Intent intent_1=new Intent(GonglveActivity.this, LifeMainActivity.class);
						startActivity(intent_1);								
						break;
					case 1:
						Intent intent_2=new Intent(GonglveActivity.this, DirectionalFlowActivity.class);
						startActivity(intent_2);
						break;
					}
				}
			});
			if(i%count==0) {
				view.setBackgroundResource(R.drawable.adv_default);
			}
			else {
				view.setBackgroundResource(R.drawable.adv_default2);
			}
			advView.add(view);
			
			ImageView image=(ImageView)LayoutInflater.from(GonglveActivity.this).inflate(R.layout.adv_point_image, null);
			if(i==0) {
				image.setImageResource(R.drawable.shareloc_online);
			}
			if(i%count==0) {
				market_adv_num_layout.addView(image);
				pointView.add(image);
			}	
		}
		adapter=new MarketNewAdapter(advView);
		market_adv.setAdapter(adapter);
		market_adv.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				currentItem=arg0;
				for(int i=0;i<market_adv_num_layout.getChildCount();i++) {
					pointView.get(i).setImageResource(R.drawable.shareloc_offline);
				}
				pointView.get(arg0%market_adv_num_layout.getChildCount()).setImageResource(R.drawable.shareloc_online);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}			
			
		});
		market_adv.startAutoScroll();			
	
	}

	/**
	 * 根据加载总数显示滑动层布局
	 * @param totalNum
	 */
	public void addAdvLayoutView() {
		int[] res_ids={R.drawable.market_down_1, R.drawable.market_down_2, R.drawable.market_down_5, R.drawable.market_down_0};
		String[] names={"特色流量包", "费费通", "聚油宝", "敬请期待"};
		int totalNum=4;
		int line=totalNum%3==0?totalNum/3:totalNum/3+1;
		for(int i=0;i<line;i++) {
			View view_line=LayoutInflater.from(this).inflate(R.layout.chatservice_chatlayoutline, null);
			LinearLayout chat_layout_line=(LinearLayout) view_line.findViewById(R.id.adv_layout_line);
			int row=3;
			if(i==(line-1)) {
				row=totalNum-3*(line-1);
			}
			for(int j=0;j<3;j++) {
				View view_row=LayoutInflater.from(this).inflate(R.layout.chatservice_chatlayoutrow, null);
				LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				params.weight=1;
				if(j>=row) {
					view_row.setVisibility(View.INVISIBLE);
					chat_layout_line.addView(view_row, params);
					continue;
				}
				ImageView chat_layout_item_image=(ImageView) view_row.findViewById(R.id.adv_layout_item_image);
				final int position_=i*3+j;
				chat_layout_item_image.setImageResource(res_ids[position_]);
				chat_layout_item_image.setOnClickListener(new ImageView.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						switch(position_) {
						case 0:
							Intent intent_2=new Intent(GonglveActivity.this, DirectionalFlowActivity.class);
							startActivity(intent_2);
							break;
						case 1:
							Intent intent_1=new Intent(GonglveActivity.this, LifeMainActivity.class);
							startActivity(intent_1);
							break;
						case 2:
							Intent intent_3=new Intent(GonglveActivity.this, TreasureMainActivity.class);
							startActivity(intent_3);
							break;
						case 3:
							break;
						}
					}});
				TextView chat_layout_item_text=(TextView) view_row.findViewById(R.id.chat_layout_item_text);
				chat_layout_item_text.setText(names[position_]);
				chat_layout_line.addView(view_row, params);
			}
			market_layout.addView(view_line);
		}
	}	
	
	/**
	 * 根据加载总数显示滑动层布局
	 * @param totalNum
	 */
	public void addAdvLayoutView_other() {
		int[] res_ids={R.drawable.market_down_1, R.drawable.market_down_2, R.drawable.market_down_0};
		String[] names={"特色流量包", "费费通", "敬请期待"};
		int totalNum=3;
		int line=totalNum%3==0?totalNum/3:totalNum/3+1;
		for(int i=0;i<line;i++) {
			View view_line=LayoutInflater.from(this).inflate(R.layout.chatservice_chatlayoutline, null);
			LinearLayout chat_layout_line=(LinearLayout) view_line.findViewById(R.id.adv_layout_line);
			int row=3;
			if(i==(line-1)) {
				row=totalNum-3*(line-1);
			}
			for(int j=0;j<3;j++) {
				View view_row=LayoutInflater.from(this).inflate(R.layout.chatservice_chatlayoutrow, null);
				LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				params.weight=1;
				if(j>=row) {
					view_row.setVisibility(View.INVISIBLE);
					chat_layout_line.addView(view_row, params);
					continue;
				}
				ImageView chat_layout_item_image=(ImageView) view_row.findViewById(R.id.adv_layout_item_image);
				final int position_=i*3+j;
				chat_layout_item_image.setImageResource(res_ids[position_]);
				chat_layout_item_image.setOnClickListener(new ImageView.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						switch(position_) {
						case 0:
							Intent intent_2=new Intent(GonglveActivity.this, DirectionalFlowActivity.class);
							startActivity(intent_2);
							break;
						case 1:
							Intent intent_1=new Intent(GonglveActivity.this, LifeMainActivity.class);
							startActivity(intent_1);
							break;
						case 2:
							
							break;
						}
					}});
				TextView chat_layout_item_text=(TextView) view_row.findViewById(R.id.chat_layout_item_text);
				chat_layout_item_text.setText(names[position_]);
				chat_layout_line.addView(view_row, params);
			}
			market_layout.addView(view_line);
		}
	}
	
	/**
	 * 加载流量银行滚动界面
	 */
	public void loadBankView() {

		gonglve_bank_layout.removeAllViews();
		
		isLoadBank=true;
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		
		final Handler handler=new Handler() {
			@Override
			// TODO Auto-generated method stub
			public void handleMessage(Message msg) {
				DisplayMetrics dm=new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				if(msg.what==1) {					
					final Map map=(Map) msg.obj;
					String result=map.get("result").toString();
					String comments=map.get("comments").toString();
//					String result="1";
//					String comments="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//							"<OutputInfo>" +
//							"<giftable_num>3</giftable_num><comments>流量银行查询接口</comments>" +
//							"<product_group><product_id>3</product_id><product_name>天翼乐享3G上网版289元套餐</product_name><accu_type_id>13</accu_type_id><unused_amount>100MB</unused_amount></product_group>" +
//							"<product_group><product_id>2</product_id><product_name>天翼乐享3G上网版289元套餐</product_name><accu_type_id>12</accu_type_id><unused_amount>0MB</unused_amount></product_group>" +
//							"<product_group><product_id>1</product_id><product_name>天翼乐享3G上网版289元套餐</product_name><accu_type_id>11</accu_type_id><unused_amount>0MB</unused_amount></product_group>" +
//							"</OutputInfo>";
					System.out.println(comments);
					
					if(result.equals("1")) {
						
						FlowBankParse parse=new FlowBankParse();
						final GifTableListModel model=parse.getGifTableListModel(comments);
						
						if(model.getGiftable_num()==0||model.getProductGroupModel_list()==null) {
							LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							ImageView image=new ImageView(GonglveActivity.this);
							image.setImageResource(R.drawable.no_flow_bank_num);
							gonglve_bank_layout.addView(image, params);
							
							if(defaultLoad==2) {
								gonglve_tuan_layout.setVisibility(View.GONE);
								gonglve_bank_layout.setVisibility(View.VISIBLE);
							}
							isLoadBank=false;
							if(!isLoadActivity&&!isLoadBank) {
								title_refresh.setVisibility(View.VISIBLE);
								title_refresh_progress.setVisibility(View.INVISIBLE);
								MainActivity.getInstance().able_change();
							}
							
							return;
						}
						
						View view=null;
						if(dm.density==1.0) {
							view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.activity_flow_bank_10, null);
						}
						else if(dm.density==1.5) {
							view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.activity_flow_bank_15, null);
						}
						else {
							view=LayoutInflater.from(GonglveActivity.this).inflate(R.layout.activity_flow_bank, null);
						}
						final ImageView flow_bank_ask=(ImageView) view.findViewById(R.id.flow_bank_ask);
						flow_bank_ask.setOnClickListener(new ImageView.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent=new Intent(GonglveActivity.this, WarnInfoBankActivity.class);
								Bundle bundle=new Bundle();
								bundle.putString("instruction", map.get("instruction").toString());
								intent.putExtras(bundle);
								startActivity(intent);
							}});
						final LinearLayout flow_bank_has=(LinearLayout) view.findViewById(R.id.flow_bank_has);
						final RelativeLayout flow_bank_jiayou_layout=(RelativeLayout) view.findViewById(R.id.flow_bank_jiayou_layout);
						final RelativeLayout flow_bank_nounused_layout=(RelativeLayout) view.findViewById(R.id.flow_bank_nounused_layout);
						final TextView flowbank_number_l=(TextView) view.findViewById(R.id.flowbank_number_l);
						//得到总可转流量
						final double unUsed=getTotalUsedFlow(model, flowbank_number_l);
						final ImageView flow_bank_choice_100=(ImageView) view.findViewById(R.id.flow_bank_choice_100);
						flow_bank_choice_100.setImageResource(R.drawable.flow_bank_100_green_sel);
						final ImageView flow_bank_choice_300=(ImageView) view.findViewById(R.id.flow_bank_choice_300);
						flow_bank_choice_300.setImageResource(R.drawable.flow_bank_300_green_sel);
						final ImageView flow_bank_choice_500=(ImageView) view.findViewById(R.id.flow_bank_choice_500);
						flow_bank_choice_500.setImageResource(R.drawable.flow_bank_500_green_sel);
						
						flow_bank_choice_100.setOnClickListener(new ImageView.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//得到初始化布局状态
								getFlowBankLevel(model, unUsed, flow_bank_choice_100, flow_bank_choice_300, flow_bank_choice_500);								
								flow_bank_choice_100.setImageResource(R.drawable.flow_bank_100_red);	
								choiceFlow=100;
							}});						
						flow_bank_choice_300.setOnClickListener(new ImageView.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//得到初始化布局状态
								getFlowBankLevel(model, unUsed, flow_bank_choice_100, flow_bank_choice_300, flow_bank_choice_500);								
								flow_bank_choice_300.setImageResource(R.drawable.flow_bank_300_red);	
								choiceFlow=300;
							}});						
						flow_bank_choice_500.setOnClickListener(new ImageView.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//得到初始化布局状态
								getFlowBankLevel(model, unUsed, flow_bank_choice_100, flow_bank_choice_300, flow_bank_choice_500);								
								flow_bank_choice_500.setImageResource(R.drawable.flow_bank_500_red);
								choiceFlow=500;
							}});
						//得到初始化布局状态
						getFlowBankLevel(model, unUsed, flow_bank_choice_100, flow_bank_choice_300, flow_bank_choice_500);								
						
						if(((GasStationApplication) getApplicationContext()).flowBankNum!=-1) {
							((GasStationApplication) getApplicationContext()).flowBankNum=-1;
						}	
						final EditText bank_phonenum=(EditText) view.findViewById(R.id.bank_phonenum);
						bank_phonenum.addTextChangedListener(new phoneWatcher());
						
						phoneNumEditText=bank_phonenum;
						TextView bank_ok=(TextView) view.findViewById(R.id.bank_ok);
						bank_ok.setOnClickListener(new TextView.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(choiceFlow==0) {
									showCustomToast("请您输入转增的流量");
									return ;
								}
								if(bank_phonenum.getText().toString().equals("")) {
									showCustomToast("请您输入被转增人的手机号码");
									return ;
								}
								if(bank_phonenum.getText().toString().length()==11) {
									if(bank_phonenum.getText().toString().equals(Util.getUserInfo(GonglveActivity.this).get(0))) {
										showCustomToast("推荐人手机号不能为本人手机号");
										return;
									}
									else {
										String ct = "^((133)|(153)|(18[0,1,9]))\\d{8}$";
										if(!bank_phonenum.getText().toString().matches(ct)) {
											showCustomToast("请输入天翼手机号码");
											return;
										}
									}
								}
								checkIsValid(model, bank_phonenum);
								
							}});
						ImageView flow_bank_history=(ImageView) view.findViewById(R.id.flow_bank_history);
						flow_bank_history.setOnClickListener(new ImageView.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent=new Intent(GonglveActivity.this, FlowBankHistoryActivity.class);
								startActivityForResult(intent, 2000);
							}});
						ImageView flow_bank_jiayou_go=(ImageView) view.findViewById(R.id.flow_bank_jiayou_go);
						flow_bank_jiayou_go.setOnClickListener(new ImageView.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								MainActivity.getInstance().jumpToJiayou(0, 4);
							}});
						
						//判断是否存在可转增流量
						if(unUsed>0) {
							flow_bank_has.setVisibility(View.VISIBLE);
							flow_bank_nounused_layout.setVisibility(View.GONE);
							flow_bank_jiayou_layout.setVisibility(View.GONE);
							gonglve_bank_layout.addView(view);
						}
						else {
							LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							ImageView image=new ImageView(GonglveActivity.this);
							image.setImageResource(R.drawable.no_flow_bank_flow);
							gonglve_bank_layout.setOnClickListener(new LinearLayout.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									MainActivity.getInstance().jumpToJiayou(0, 4);
								}});
							gonglve_bank_layout.setBackgroundColor(Color.parseColor("#e8e8e8"));
							gonglve_bank_layout.addView(image, params);
						}						
					}
					else {
						LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						ImageView image=new ImageView(GonglveActivity.this);
						image.setImageResource(R.drawable.no_huodong_flow_bank);
						gonglve_bank_layout.addView(image, params);
					}
				}
				else {
					LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ImageView image=new ImageView(GonglveActivity.this);
					image.setImageResource(R.drawable.net_error);
					gonglve_bank_layout.addView(image, params);
				}

				if(defaultLoad==2) {
					gonglve_tuan_layout.setVisibility(View.GONE);
					gonglve_bank_layout.setVisibility(View.VISIBLE);
				}
				isLoadBank=false;
				if(!isLoadActivity&&!isLoadBank) {
					title_refresh.setVisibility(View.VISIBLE);
					title_refresh_progress.setVisibility(View.INVISIBLE);
					MainActivity.getInstance().able_change();
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getGiftedList(Long.parseLong(list.get(0)), list.get(1));
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
	 * 获取全部可转流量
	 * @param model
	 * @param flowbank_number_l
	 */
	public double getTotalUsedFlow(GifTableListModel model, TextView flowbank_number_l) {
		double canUsed=0;
		if(model.getGiftable_num()==0) {
			flowbank_number_l.setText("0");
		}
		else {
			int forNum=model.getGiftable_num();
			ArrayList<String> unused_amount_list=new ArrayList<String>();
			for(int i=0;i<model.getProductGroupModel_list().size();i++) {
				String num=""+model.getProductGroupModel_list().get(i).getUnused_amount();
				unused_amount_list.add(num);
			}
			for(int i=0;i<forNum;i++) {
				if(getSortArrayList(unused_amount_list)<100) {
					break;
				}
				else if(getSortArrayList(unused_amount_list)>=500) {
					canUsed+=500;
					double max=getSortArrayList(unused_amount_list);
					unused_amount_list.remove(""+max);
					unused_amount_list.add(""+(max-500));
				}
				else if(getSortArrayList(unused_amount_list)>=300&&getSortArrayList(unused_amount_list)<500) {
					canUsed+=300;
					double max=getSortArrayList(unused_amount_list);
					unused_amount_list.remove(""+max);
					unused_amount_list.add(""+(max-300));
				}
				else if(getSortArrayList(unused_amount_list)>=100&&getSortArrayList(unused_amount_list)<300) {
					canUsed+=100;
					double max=getSortArrayList(unused_amount_list);
					unused_amount_list.remove(""+max);
					unused_amount_list.add(""+(max-100));
				}
			}
			flowbank_number_l.setText(""+(int) canUsed);
		}
		return canUsed;
	}
	
	/**
	 * 本次可转档次
	 * @param model
	 * @param canUsed
	 * @param flow_bank_choice_1
	 * @param flow_bank_choice_2
	 * @param flow_bank_choice_3
	 */
	public void getFlowBankLevel(GifTableListModel model, double canUsed, ImageView flow_bank_choice_100, ImageView flow_bank_choice_300, ImageView flow_bank_choice_500) {
		ArrayList<String> unused_amount_list=new ArrayList<String>();
		for(int i=0;i<model.getProductGroupModel_list().size();i++) {
			String num=""+model.getProductGroupModel_list().get(i).getUnused_amount();
			unused_amount_list.add(num);
		}
		if(canUsed<100) {
			flow_bank_choice_100.setImageResource(R.drawable.flow_bank_100_gray);
			flow_bank_choice_100.setEnabled(false);
			flow_bank_choice_100.setClickable(false);
			flow_bank_choice_300.setImageResource(R.drawable.flow_bank_300_gray);
			flow_bank_choice_300.setEnabled(false);
			flow_bank_choice_300.setClickable(false);
			flow_bank_choice_500.setImageResource(R.drawable.flow_bank_500_gray);
			flow_bank_choice_500.setEnabled(false);
			flow_bank_choice_500.setClickable(false);
		}
		else if(canUsed>=100&&canUsed<300) {
			flow_bank_choice_100.setImageResource(R.drawable.flow_bank_100_green_sel);
			flow_bank_choice_100.setEnabled(true);
			flow_bank_choice_100.setClickable(true);
			flow_bank_choice_300.setImageResource(R.drawable.flow_bank_300_gray);
			flow_bank_choice_300.setEnabled(false);
			flow_bank_choice_300.setClickable(false);
			flow_bank_choice_500.setImageResource(R.drawable.flow_bank_500_gray);
			flow_bank_choice_500.setEnabled(false);
			flow_bank_choice_500.setClickable(false);
		}
		else if(canUsed>=300&&canUsed<500) {
			flow_bank_choice_100.setImageResource(R.drawable.flow_bank_100_green_sel);
			flow_bank_choice_100.setEnabled(true);
			flow_bank_choice_100.setClickable(true);
			flow_bank_choice_300.setImageResource(R.drawable.flow_bank_300_green_sel);
			flow_bank_choice_300.setEnabled(true);
			flow_bank_choice_300.setClickable(true);
			flow_bank_choice_500.setImageResource(R.drawable.flow_bank_500_gray);
			flow_bank_choice_500.setEnabled(false);
			flow_bank_choice_500.setClickable(false);
		}
		else if(canUsed>=500) {
			if(model.getGiftable_num()>=3||getSortArrayList(unused_amount_list)>=500) {
				flow_bank_choice_100.setImageResource(R.drawable.flow_bank_100_green_sel);
				flow_bank_choice_100.setEnabled(true);
				flow_bank_choice_100.setClickable(true);
				flow_bank_choice_300.setImageResource(R.drawable.flow_bank_300_green_sel);
				flow_bank_choice_300.setEnabled(true);
				flow_bank_choice_300.setClickable(true);
				flow_bank_choice_500.setImageResource(R.drawable.flow_bank_500_green_sel);
				flow_bank_choice_500.setEnabled(true);
				flow_bank_choice_500.setClickable(true);
			}
			else {
				flow_bank_choice_100.setImageResource(R.drawable.flow_bank_100_green_sel);
				flow_bank_choice_100.setEnabled(true);
				flow_bank_choice_100.setClickable(true);
				flow_bank_choice_300.setImageResource(R.drawable.flow_bank_300_green_sel);
				flow_bank_choice_300.setEnabled(true);
				flow_bank_choice_300.setClickable(true);
				flow_bank_choice_500.setImageResource(R.drawable.flow_bank_500_green_sel);
				flow_bank_choice_500.setEnabled(false);
				flow_bank_choice_500.setClickable(false);
			}
		}
	}
	
	/**
	 * 转赠拆分
	 * @param model
	 * @param selectedFlow
	 */
	public void giftFlow(GifTableListModel model, int selectedFlow, String flowPhone) {
		String productId="";
		String accuTypeId="";
		String amount="";
		//所有流量集合
		ArrayList<ProductGroupModel> unused_amount_list=new ArrayList<ProductGroupModel>();
		for(int i=0;i<model.getProductGroupModel_list().size();i++) {
			ProductGroupModel p_=model.getProductGroupModel_list().get(i);
			unused_amount_list.add((ProductGroupModel)p_.clone());
		}
		if(selectedFlow==500) {
			int index=getSortArrayList_1(unused_amount_list);
			ProductGroupModel model_result=unused_amount_list.get(index);
			double num=model_result.getUnused_amount();
			if(num>=500) {
				productId=model_result.getProduct_id();
				accuTypeId=model_result.getAccu_type_id();
				amount=""+(500*1024);
			}
			else {
				for(int i=0;i<3;i++) {
					int index_temp=getSortArrayList_1(unused_amount_list);
					ProductGroupModel temp=unused_amount_list.get(index_temp);
					if(i==0) {
						double num_temp=temp.getUnused_amount();						
						unused_amount_list.get(index_temp).setUnused_amount(num_temp-300);
						productId+=unused_amount_list.get(index_temp).getProduct_id()+"&";
						accuTypeId+=unused_amount_list.get(index_temp).getAccu_type_id()+"&";
						amount+=""+(300*1024)+"&";
					}
					else if(i==1) {
						double num_temp=temp.getUnused_amount();						
						unused_amount_list.get(index_temp).setUnused_amount(num_temp-100);
						productId+=unused_amount_list.get(index_temp).getProduct_id()+"&";
						accuTypeId+=unused_amount_list.get(index_temp).getAccu_type_id()+"&";
						amount+=""+(100*1024)+"&";
					}
					else if(i==2) {
						double num_temp=temp.getUnused_amount();						
						unused_amount_list.get(index_temp).setUnused_amount(num_temp-100);
						productId+=unused_amount_list.get(index_temp).getProduct_id();
						accuTypeId+=unused_amount_list.get(index_temp).getAccu_type_id();
						amount+=""+(100*1024);
					}
				}
			}
		}
		else if(selectedFlow==300) {
			int index=getSortArrayList_1(unused_amount_list);
			ProductGroupModel model_result=unused_amount_list.get(index);
			double num=model_result.getUnused_amount();
			if(num>=300) {
				productId=model_result.getProduct_id();
				accuTypeId=model_result.getAccu_type_id();
				amount=""+(300*1024);
			}
			else {
				for(int i=0;i<3;i++) {
					int index_temp=getSortArrayList_1(unused_amount_list);
					ProductGroupModel temp=unused_amount_list.get(index_temp);
					double num_temp=temp.getUnused_amount();						
					unused_amount_list.get(index_temp).setUnused_amount(num_temp-100);
					if(i==0||i==1) {
						productId+=unused_amount_list.get(index_temp).getProduct_id()+"&";
						accuTypeId+=unused_amount_list.get(index_temp).getAccu_type_id()+"&";
						amount+=""+(100*1024)+"&";
					}
					else if(i==2) {
						productId+=unused_amount_list.get(index_temp).getProduct_id();
						accuTypeId+=unused_amount_list.get(index_temp).getAccu_type_id();
						amount+=""+(100*1024);
					}
				}
			}
		}
		else if(selectedFlow==100) {
			int index=getSortArrayList_1(unused_amount_list);
			ProductGroupModel model_result=unused_amount_list.get(index);
			double num=model_result.getUnused_amount();
			if(num>=100) {
				productId=model_result.getProduct_id();
				accuTypeId=model_result.getAccu_type_id();
				amount=""+(100*1024);
			}
		}
		System.out.println(productId+"  "+accuTypeId+"  "+amount);
		Intent intent=new Intent(GonglveActivity.this, Flow_Bank_Pay_Activity.class);
		Bundle bundle=new Bundle();
		bundle.putString("productId", productId);
		bundle.putString("accuTypeId", accuTypeId);
		bundle.putString("amount", amount);
		bundle.putString("flowNum", ""+selectedFlow);
		bundle.putString("flowPhone", flowPhone);
		bundle.putInt("giftable_num", model.getGiftable_num());
		intent.putExtras(bundle);
		startActivityForResult(intent, 1000);		
	}
	
	/**
	 * 取当前集合最大值
	 * @param str_list
	 * @return
	 */
	public double getSortArrayList(ArrayList<String> str_list) {
		double max=0;
		for(int i=0;i<str_list.size();i++) {
			if(Double.parseDouble(str_list.get(i))>=max) {
				max=Double.parseDouble(str_list.get(i));
			}
		}
		return max;
	}
	
	/**
	 * 取当前集合最大值的索引
	 * @param str_list
	 * @return
	 */
	public int getSortArrayList_1(ArrayList<ProductGroupModel> model_list) {
		double max=0;
		int index=0;
		for(int i=0;i<model_list.size();i++) {
			double num=model_list.get(i).getUnused_amount();
			if(num>=max) {
				max=num;
				index=i;
			}
		}
		return index;
	}
	
	class phoneWatcher implements TextWatcher {

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
			ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
			if(s.toString().length()==11) {
				if(s.toString().equals(list.get(0))) {
					showCustomToast("推荐人手机号不能为本人手机号");
				}
				else {
					String ct = "^((133)|(153)|(18[0,1,9]))\\d{8}$";
					if(!s.toString().matches(ct)) {
						showCustomToast("请输入天翼手机号码");
					}
				}
			}
		}
	}
	

	/**
	 * 验证用户是否合法
	 */
	public void checkIsValid(final GifTableListModel model, final EditText bank_phonenum) {		
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				Map map=(Map) msg.obj;
				if(msg.what==1) {
					if(map.get("result").toString().equals("1")) {
						giftFlow(model, choiceFlow, bank_phonenum.getText().toString());
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(GonglveActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(GonglveActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(GonglveActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(GonglveActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.checkValid(Long.parseLong(list.get(0)), Long.parseLong(bank_phonenum.getText().toString()), list.get(1));
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==RESULT_OK) {
			if(requestCode==1000) {
				if(Util.getUserArea(GonglveActivity.this).equals("0971")) {
					loadNoMarketView();
				}
				else {
					loadMarketView();
				}
			}
			else if(requestCode==2000) {
				if(phoneNumEditText!=null) {
					phoneNumEditText.setText(data.getExtras().getString("history"));
				}
			}
		}
	}
	
}
