package com.linkage.gas_station.share;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.ContactModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.CommonManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

public class ShareActivity_New extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	ImageView title_refresh=null;
	TextView share_content=null;
	TextView share_button=null;
	ProgressDialog pd=null;
	
	LinearLayout share_name_layout=null;
	LinearLayout share_phone_layout=null;
	EditText share_input_layout_edittext=null;
	ImageView share_input_layout_image=null;
	ImageView share_name_image=null;
	
	//保存的全部电话号码
	ArrayList<String> phone_num_list=null;
	//根据分辨率不同放大的倍数
	double scale=1;
	//联系人layout总宽度
	int layout_width=0;
	
	LinearLayout.LayoutParams params=null;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_share_new);
		
		phone_num_list=new ArrayList<String>();
		
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		scale=dm.density;
	
		params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin=3;
		params.bottomMargin=3;
		params.leftMargin=3;
		params.rightMargin=3;
		
		((GasStationApplication) getApplication()).tempActivity.add(ShareActivity_New.this);
		
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
		title_name.setText(getResources().getString(R.string.more_share));
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setImageResource(R.drawable.zhifu_history);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(ShareActivity_New.this, ShareDetailActivity.class);
				startActivity(intent);
			}});
		
		share_name_layout=(LinearLayout) findViewById(R.id.share_name_layout);
		share_phone_layout=(LinearLayout) findViewById(R.id.share_phone_layout);
		share_input_layout_edittext=(EditText) findViewById(R.id.share_input_layout_edittext);
		share_input_layout_image=(ImageView) findViewById(R.id.share_input_layout_image);
		share_input_layout_image.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!share_input_layout_edittext.getText().toString().equals("")) {
					phone_num_list.add(share_input_layout_edittext.getText().toString());
					share_input_layout_edittext.setText("");
					addPhoneNum();
				}
			}});
		share_name_image=(ImageView) findViewById(R.id.share_name_image);
		share_name_image.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(((GasStationApplication) getApplicationContext()).getModel_list()==null) {
					Util.getContactData(ShareActivity_New.this);
				}
				Intent intent=new Intent(ShareActivity_New.this, SelectContactsActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt("type", 1);
				intent.putExtras(bundle);
				startActivityForResult(intent, 100);
			}});
		share_content=(TextView) findViewById(R.id.share_content);
		if(Util.getUserArea(ShareActivity_New.this).equals("0971")) {
			share_content.setText(Util.getUserInfo(ShareActivity_New.this).get(0)+getResources().getString(R.string.share_content_qh));
		}
		else if(Util.getUserArea(ShareActivity_New.this).equals("2500")) {
			share_content.setText(Util.getUserInfo(ShareActivity_New.this).get(0)+getResources().getString(R.string.share_content_js));			
		}
		share_button=(TextView) findViewById(R.id.share_button);
		share_button.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int contact_temp=0;
				if(((GasStationApplication) getApplicationContext()).getModel_list()!=null) {
					for(int i=0;i<((GasStationApplication) getApplicationContext()).getModel_list().size();i++) {
						if(((GasStationApplication) getApplicationContext()).getModel_list().get(i).isChecked()) {
							contact_temp++;
						}
					}
				}
				if(phone_num_list.size()>0||contact_temp>0||!share_input_layout_edittext.getText().toString().equals("")) {
					sharePhoneNum();
					showCustomToast(getResources().getString(R.string.share_now));
				}
				else {
					showCustomToast(getResources().getString(R.string.share_null));
				}
			}});
		ViewTreeObserver os=share_name_layout.getViewTreeObserver();
		os.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				share_name_layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				layout_width=share_name_layout.getWidth();
			}
		});
	}
	
	/**
	 * 电话号码视图
	 */
	public void addPhoneNum() {
		//将之前全部视图清楚
		share_phone_layout.removeAllViews();
		ArrayList<Double> width_list=new ArrayList<Double>();
		ArrayList<View> view_list=new ArrayList<View>();
		for(int i=0;i<phone_num_list.size();i++) {
			//标志位便于删除
			final int temp_i=i;
			View view=LayoutInflater.from(ShareActivity_New.this).inflate(R.layout.contact_select_layout, null);
			final TextView contact_text=(TextView) view.findViewById(R.id.contact_text);
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(contact_text.getCurrentTextColor()==Color.BLACK) {
						contact_text.setTextColor(Color.RED);
					}
					else {
						phone_num_list.remove(temp_i);
						addPhoneNum();
					}					
				}
			});
			contact_text.setText(phone_num_list.get(i));
			width_list.add(Util.getNameLength(phone_num_list.get(i))*10*scale+6);
			view_list.add(view);
			//share_phone_layout.addView(view, params);
		}
		changeView(width_list, view_list, share_phone_layout);
	}
	
	/**
	 * 联系人视图
	 */
	public void addContact() {
		share_name_layout.removeAllViews();	
		ArrayList<Double> width_list=new ArrayList<Double>();
		ArrayList<View> view_list=new ArrayList<View>();
		for(int i=0;i<((GasStationApplication) getApplicationContext()).getModel_list().size();i++) {
			//标志位便于删除
			final int temp_i=i;
			if(((GasStationApplication) getApplicationContext()).getModel_list().get(i).isChecked()) {				
				View view=LayoutInflater.from(ShareActivity_New.this).inflate(R.layout.contact_select_layout, null);
				final TextView contact_text=(TextView) view.findViewById(R.id.contact_text);
				view.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(contact_text.getCurrentTextColor()==Color.BLACK) {
							contact_text.setTextColor(Color.RED);
						}
						else {
							((GasStationApplication) getApplicationContext()).getModel_list().get(temp_i).setChecked(false);
							addContact();
						}						
					}
				});
				String name=((GasStationApplication) getApplicationContext()).getModel_list().get(i).getName();
				contact_text.setText(name);
				//share_name_layout.addView(view, params);
				width_list.add(Util.getNameLength(name)*10*scale+6);
				view_list.add(view);
			}
		}
		changeView(width_list, view_list, share_name_layout);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK) {
			//进行迭代，layout动态加载
			addContact();			
		}
	}
	
	
	
	/**
	 * 修正布局
	 * @param width_list
	 * @param view_list
	 */
	public void changeView(ArrayList<Double> width_list, ArrayList<View> view_list, LinearLayout layout) {

		//当前子LinearLayout
		LinearLayout currentChildLayout=null;
		//当前子LinearLayout宽度
		float currentChildLayout_width=0;
		for(int i=0;i<width_list.size();i++) {			
			if(width_list.get(i)+currentChildLayout_width<layout_width) {
				if(currentChildLayout_width==0) {
					currentChildLayout=new LinearLayout(ShareActivity_New.this);
					currentChildLayout.setOrientation(LinearLayout.HORIZONTAL);
					layout.addView(currentChildLayout, params);
				}
				currentChildLayout.addView(view_list.get(i), params);
				currentChildLayout_width+=width_list.get(i);
			}
			else {
				currentChildLayout=new LinearLayout(ShareActivity_New.this);
				currentChildLayout.setOrientation(LinearLayout.HORIZONTAL);
				layout.addView(currentChildLayout, params);
				currentChildLayout.addView(view_list.get(i), params);
				currentChildLayout_width=0;
				currentChildLayout_width+=width_list.get(i);
			}
		}
	}
	
	/**
	 * 分享号码
	 */
	public void sharePhoneNum() {
		pd=ProgressDialog.show(ShareActivity_New.this, getResources().getString(R.string.tishi), "提交中");
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.share_error));
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.share_comp));
				}
				share_name_layout.removeAllViews();
				share_phone_layout.removeAllViews();
				share_input_layout_edittext.setText("");
				//数据状态清除
				if(((GasStationApplication) getApplicationContext()).getModel_list()!=null) {
					for(int i=0;i<((GasStationApplication) getApplicationContext()).getModel_list().size();i++) {
						((GasStationApplication) getApplicationContext()).getModel_list().get(i).setChecked(false);
					}
				}
				phone_num_list.clear();
				pd.dismiss();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(ShareActivity_New.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ShareActivity_New.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(ShareActivity_New.this);
						
						CommonManager commonManager=GetWebDate.getHessionFactiory(ShareActivity_New.this).create(CommonManager.class, currentUsedUrl+"/hessian/commonManager", getClassLoader());
						String str="";
						for(int i=0;i<phone_num_list.size();i++) {
							String temp=phone_num_list.get(i);
							if(temp.indexOf("+86")!=-1) {
								temp=temp.substring(3, temp.length());
							}
							str+=temp+";";
						}
						ArrayList<ContactModel> contact_num_list=((GasStationApplication) getApplicationContext()).getModel_list();
						if(contact_num_list!=null) {
							int contact_temp=0;
							for(int i=0;i<contact_num_list.size();i++) {
								if(contact_num_list.get(i).isChecked()) {
									contact_temp++;
								}
							}
							int temp_num=0;
							for(int i=0;i<contact_num_list.size();i++) {
								if(contact_num_list.get(i).isChecked()) {
									String temp=contact_num_list.get(i).getPhoneNum();
									if(temp.indexOf("+86")!=-1) {
										temp=temp.substring(3, temp.length());
									}
									str+=temp+";";
									temp_num++;
								}								
							}
						}						
						//加载edittext里面的数据
						if(share_input_layout_edittext!=null&&!share_input_layout_edittext.getText().toString().equals("")) {
							str+=share_input_layout_edittext.getText().toString();
						}
						if(str.lastIndexOf(";")==str.length()-1) {
							str=str.substring(0, str.length()-1);
						}
						int result=commonManager.softwareRecommend(Long.parseLong(list.get(0)), 1, str);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//数据状态清除
		if(((GasStationApplication) getApplicationContext()).getModel_list()!=null) {
			for(int i=0;i<((GasStationApplication) getApplicationContext()).getModel_list().size();i++) {
				((GasStationApplication) getApplicationContext()).getModel_list().get(i).setChecked(false);
			}
		}
		((GasStationApplication) getApplication()).tempActivity.remove(ShareActivity_New.this);
	}

}
