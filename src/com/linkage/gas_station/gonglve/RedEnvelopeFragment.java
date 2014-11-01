package com.linkage.gas_station.gonglve;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.qqapi.QQActivity;
import com.linkage.gas_station.sinaweiboapi.WBMainActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.wxapi.SendWeixin;
import com.linkage.gas_station.yxapi.SendYixin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RedEnvelopeFragment extends Fragment {
	
	View view=null;
	
	ImageView fragment_redenvelope_commit=null;
	LinearLayout fragment_redenvelope_search=null;
	ImageView snedredenvelope_left=null;
	ImageView snedredenvelope_right=null;
	
	int choice=-1;
	String seqId="";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null) {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_redenvelope, null);
			fragment_redenvelope_commit=(ImageView) view.findViewById(R.id.fragment_redenvelope_commit);
			fragment_redenvelope_commit.setOnClickListener(new ImageView.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(choice!=-1) {
						Intent intent=new Intent(getActivity(), CommonMessageDialogActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString("offerId", ""+choice);
						bundle.putString("activityId", getActivity().getIntent().getExtras().getString("activityId"));
						intent.putExtras(bundle);
						getParentFragment().startActivityForResult(intent, 103);
					}
					else {
						BaseActivity.showCustomToastWithContext("请选择一种红包类型", getActivity());
					}
				}});
			fragment_redenvelope_search=(LinearLayout) view.findViewById(R.id.fragment_redenvelope_search);
			fragment_redenvelope_search.setOnClickListener(new LinearLayout.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(getActivity(), RedenvelopeSearchActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("activityId", getActivity().getIntent().getExtras().getString("activityId"));
					bundle.putString("activity_rule", getActivity().getIntent().getExtras().getString("activity_rule"));
					bundle.putString("activity_name", getActivity().getIntent().getExtras().getString("activity_name"));
					bundle.putString("activity_url", getActivity().getIntent().getExtras().getString("activity_url"));
					intent.putExtras(bundle);
					startActivity(intent);
				}});
			snedredenvelope_left=(ImageView) view.findViewById(R.id.snedredenvelope_left);
			snedredenvelope_left.setOnClickListener(new ImageView.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					snedredenvelope_left.setImageResource(R.drawable.snedredenvelope_left_choice);
					snedredenvelope_right.setImageResource(R.drawable.snedredenvelope_right_nor);
					
					choice=88;
				}});
			snedredenvelope_right=(ImageView) view.findViewById(R.id.snedredenvelope_right);
			snedredenvelope_right.setOnClickListener(new ImageView.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					snedredenvelope_left.setImageResource(R.drawable.snedredenvelope_left_nor);
					snedredenvelope_right.setImageResource(R.drawable.snedredenvelope_right_choice);
					
					choice=89;
				}});
		}
		ViewGroup parent=(ViewGroup)view.getParent();
		if(parent!=null) {
			parent.removeView(view);
		}
		return view;
	}
	
	private void showShare() {
		final Dialog dialog=new Dialog(getActivity(), R.style.shareDialog);
		dialog.setCanceledOnTouchOutside(true);
		View view=LayoutInflater.from(getActivity()).inflate(R.layout.view_sharelayout, null);
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
				
				((GasStationApplication) getActivity().getApplicationContext()).shareType=6;
				((GasStationApplication) getActivity().getApplicationContext()).activityId=Integer.parseInt(getActivity().getIntent().getExtras().getString("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(getActivity()).get(0):((GasStationApplication) getActivity().getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getActivity().getApplicationContext()).content=getActivity().getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(getActivity(), QQActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", getActivity().getIntent().getExtras().getString("activity_name"));
				bundle.putString("url", currentUsedUrl+getActivity().getIntent().getExtras().getString("activity_url")+"?activityId="+getActivity().getIntent().getExtras().getString("activityId")+"&seqId="+seqId);
				bundle.putString("text", getActivity().getIntent().getExtras().getString("activity_rule"));
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
				
				((GasStationApplication) getActivity().getApplicationContext()).shareType=5;
				((GasStationApplication) getActivity().getApplicationContext()).activityId=Integer.parseInt(getActivity().getIntent().getExtras().getString("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(getActivity()).get(0):((GasStationApplication) getActivity().getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getActivity().getApplicationContext()).content=getActivity().getIntent().getExtras().getString("activity_rule");
				SendYixin yixin=new SendYixin();
				yixin.sendYixin(getActivity() , getActivity().getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getActivity().getIntent().getExtras().getString("activity_url")+"?activityId="+getActivity().getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getActivity().getIntent().getExtras().getString("activity_name"), R.drawable.ic_launcher, true);
				
			}});
		ImageView gonglve_weixin_pengyou_share=(ImageView) view.findViewById(R.id.gonglve_weixin_pengyou_share);
		gonglve_weixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
				((GasStationApplication) getActivity().getApplicationContext()).shareType=3;
				((GasStationApplication) getActivity().getApplicationContext()).activityId=Integer.parseInt(getActivity().getIntent().getExtras().getString("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(getActivity()).get(0):((GasStationApplication) getActivity().getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getActivity().getApplicationContext()).content=getActivity().getIntent().getExtras().getString("activity_rule");
				SendWeixin weixin=new SendWeixin();
				weixin.sendWeixin(getActivity(), getActivity().getIntent().getExtras().getString("activity_name")+"\n"+getActivity().getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getActivity().getIntent().getExtras().getString("activity_url")+"?activityId="+getActivity().getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getActivity().getIntent().getExtras().getString("activity_name")+"\n"+getActivity().getIntent().getExtras().getString("activity_rule"), R.drawable.ic_launcher, true);
			}});
		ImageView gonglve_sinaweibo_logo_share=(ImageView) view.findViewById(R.id.gonglve_sinaweibo_logo_share);
		gonglve_sinaweibo_logo_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
				((GasStationApplication) getActivity().getApplicationContext()).shareType=4;
				((GasStationApplication) getActivity().getApplicationContext()).activityId=Integer.parseInt(getActivity().getIntent().getExtras().getString("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(getActivity()).get(0):((GasStationApplication) getActivity().getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getActivity().getApplicationContext()).content=getActivity().getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(getActivity(), WBMainActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", getActivity().getIntent().getExtras().getString("activity_name"));
				bundle.putString("url", currentUsedUrl+getActivity().getIntent().getExtras().getString("activity_url")+"?activityId="+getActivity().getIntent().getExtras().getString("activityId")+"&seqId="+seqId);
				bundle.putString("text", getActivity().getIntent().getExtras().getString("activity_rule"));
				bundle.putString("defaultText", "流量加油站");
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		dialog.setContentView(view);
		dialog.show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK&&requestCode==103) {
			seqId=data.getExtras().getString("seqId");
			new AlertDialog.Builder(getActivity()).setTitle("购买红包成功").setMessage("现在立即分享到朋友圈?").setPositiveButton("立即分享", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					showShare();
				}
			}).setNegativeButton("稍后分享", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
			}).show();
		}
	}

}
