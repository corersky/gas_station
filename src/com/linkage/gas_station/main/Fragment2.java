package com.linkage.gas_station.main;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.Flow_Type_Group_Model;
import com.linkage.gas_station.model.OutputInfoModel;
import com.linkage.gas_station.util.GetConnData;
import com.linkage.gas_station.util.Util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Fragment2 extends Fragment {
	
	OutputInfoModel model=null;
	LinearLayout home2_layout=null;
	//是否收缩标签
	boolean isCollapse=false;
		
	GetConnData cData=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home2, null);
		home2_layout=(LinearLayout) view.findViewById(R.id.home2_layout);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		cData=new GetConnData(getActivity());
//		if(!isDataLaunch) {
//			model=(OutputInfoModel) getArguments().getSerializable("info");
//		}
		model=cData.getMonitor();
		//主副卡位置
		int flow_type_id11_pos=-1;
		//标准流量里找到主副卡的位置，便于之后使用
		for(int j=0;j<model.getModel_list().size();j++) {
			if(model.getModel_list().get(j).getFlow_type_id()==11) {
				flow_type_id11_pos=j;
			}
		}
		//是否存在标准流量
		boolean flow_type_id1_exists=false;
		for(int j=0;j<model.getModel_list().size();j++) {
			if(model.getModel_list().get(j).getFlow_type_id()==1) {
				flow_type_id1_exists=true;
				break;
			}
		}
		//标准流量不存在并且主副卡存在的情况下，将主副卡设置为标准流量
		boolean is1NotExistsAnd11Exists=false;
		if(!flow_type_id1_exists&&flow_type_id11_pos!=-1) {
			is1NotExistsAnd11Exists=true;
			model.getModel_list().get(flow_type_id11_pos).setFlow_type_id(1);
			if(Util.getUserArea(getActivity()).equals("0971")) {
				model.getModel_list().get(flow_type_id11_pos).setFlow_type_name("全国流量");
			}
			else {
				model.getModel_list().get(flow_type_id11_pos).setFlow_type_name("标准流量");
			}
			flow_type_id11_pos=-1;
		}
		//统计使用详情类型个数
		int totalNum=0;
		for(int i=0;i<model.getModel_list().size();i++) {
			for(int w=0;w<model.getModel_list().get(i).getModel_list().size();w++) {
				totalNum++;
			}
		}		
		if(totalNum>5) {
			isCollapse=true;
		}
		else {
			isCollapse=false;
		}
		for(int i=0;i<model.getModel_list().size();i++) {
			//主副卡时候直接跳过
			if(model.getModel_list().get(i).getFlow_type_id()==11) {
				continue;
			}			
			View view_title=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home2_title, null);
			LinearLayout home2_title_layout=(LinearLayout) view_title.findViewById(R.id.home2_title_layout);
			TextView title2_title=(TextView) view_title.findViewById(R.id.title2_title);
			TextView title2_flow=(TextView) view_title.findViewById(R.id.title2_flow);
			title2_title.setText(model.getModel_list().get(i).getFlow_type_name());
			String flow_num=model.getModel_list().get(i).getFlow_type_unused();
			title2_flow.setText(Html.fromHtml("(剩余"+"<font color='red'>"+flow_num.subSequence(0, flow_num.length()-2)+"</font>MB)"));
			final LinearLayout title2_layout=(LinearLayout) view_title.findViewById(R.id.title2_layout);
			final ImageView title2_show_hide=(ImageView) view_title.findViewById(R.id.title2_show_hide);
			home2_title_layout.setOnClickListener(new LinearLayout.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(title2_layout.getVisibility()==View.VISIBLE) {
						title2_layout.setVisibility(View.GONE);
						title2_show_hide.setImageResource(R.drawable.arrow_hide);
					}
					else {
						title2_layout.setVisibility(View.VISIBLE);
						title2_show_hide.setImageResource(R.drawable.arrow_show);
					}
				}});
			if(isCollapse) {
				title2_layout.setVisibility(View.GONE);
				title2_show_hide.setImageResource(R.drawable.arrow_hide);
			}
			else {
				title2_layout.setVisibility(View.VISIBLE);
				title2_show_hide.setImageResource(R.drawable.arrow_show);
			}
			if(!model.getModel_list().get(i).getFlow_type_amount().equals("0MB")) {
				for(int j=0;j<model.getModel_list().get(i).getModel_list().size();j++) {
					if(i==0) {
						if(!is1NotExistsAnd11Exists) {
							addView(i, j, title2_layout, model.getModel_list().get(i).getModel_list().get(j), model.getModel_list().get(i).getModel_list().size(), flow_type_id11_pos==-1?false:true, false);
						}
						else {
							addView(i, j, title2_layout, model.getModel_list().get(i).getModel_list().get(j), model.getModel_list().get(i).getModel_list().size(), flow_type_id11_pos==-1?false:true, true);
						}
					}
					else {
						addView(i, j, title2_layout, model.getModel_list().get(i).getModel_list().get(j), model.getModel_list().get(i).getModel_list().size(), false, false);
					}
				}
				
				if(model.getModel_list().get(i).getFlow_type_id()==1&&flow_type_id11_pos!=-1) {
					for(int j=0;j<model.getModel_list().get(flow_type_id11_pos).getModel_list().size();j++) {
						addView(i, j, title2_layout, model.getModel_list().get(flow_type_id11_pos).getModel_list().get(j), model.getModel_list().get(flow_type_id11_pos).getModel_list().size(), false, true);
					}
				}
			}
			else {
				View view_item=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home2_item2, null);
				TextView item2_2_pb_used=(TextView) view_item.findViewById(R.id.item2_2_pb_used);
				item2_2_pb_used.setText(Html.fromHtml(getResources().getString(R.string.home_used)+"<font color='red'>"+model.getModel_list().get(i).getFlow_type_used()+"</font>"));
				title2_layout.addView(view_item);
			}	
			home2_layout.addView(view_title);
		}
	}
	
	/**
	 * 更新进度条状态 注意：就算不在线程里面，也要在handler里面才能更新
	 * @param pb
	 * @param ratio
	 */
	public void changeProgress(final ProgressBar pb, final int ratio, final boolean isAntherColor, final int ratio_self) {
		Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(!isAntherColor) {
					pb.setProgress(ratio);
				}
				else {
					pb.setSecondaryProgress(ratio);
					pb.setProgress(ratio_self);
				}
			}
		};
		Message m=new Message();
		handler.sendMessage(m);
	}
	
	/**
	 * 动态添加视图
	 * @param i
	 * @param j
	 * @param title2_layout
	 * @param model_
	 * @param totalSize
	 * @param isAddLastLine 是否要添加最后一条线， 在1存在的情况下，如果有11，1的最后一条线就一定要添加上去
	 * @param isAntherColor 是否换一个颜色显示进度条
	 */
	public void addView(int i, int j, LinearLayout title2_layout, Flow_Type_Group_Model model_, int totalSize, boolean isAddLastLine, boolean isAntherColor) {

		Flow_Type_Group_Model ftg_model=model_;
		View view_item=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home2_item, null);
		TextView item2_title=(TextView) view_item.findViewById(R.id.item2_title);
		item2_title.setText(ftg_model.getProduct_name());
		ProgressBar item2_pb=(ProgressBar) view_item.findViewById(R.id.item2_pb);
		double used=Double.parseDouble(ftg_model.getProduct_used().substring(0, ftg_model.getProduct_used().length()-2));
		double amount=Double.parseDouble(ftg_model.getProduct_amount().substring(0, ftg_model.getProduct_amount().length()-2));
		int ratio=(int) (used*100/amount);
		if(used==0) {
			ratio=0;
		}
		else if(used>0&&ratio<1) {
			ratio=1;
		}
		else if(used!=amount&&ratio>=99) {
			ratio=99;
		}
		
		int ratio_self=(int) (Util.getSelf(getActivity())*100/amount);
		if(Util.getSelf(getActivity())==0) {
			ratio_self=0;
		}
		else if(Util.getSelf(getActivity())>0&&ratio_self<1) {
			ratio_self=1;
		}
		else if(Util.getSelf(getActivity())!=amount&&ratio_self>=99){
			ratio_self=99;
		}
		
		changeProgress(item2_pb, ratio, false, 0);
		ProgressBar item2_pb_=(ProgressBar) view_item.findViewById(R.id.item2_pb_);
		
		if(isAntherColor) {
			changeProgress(item2_pb_, ratio, true, ratio_self);
			item2_pb_.setVisibility(View.VISIBLE);
			item2_pb.setVisibility(View.GONE);
		}
		else {
			item2_pb.setVisibility(View.VISIBLE);
			item2_pb_.setVisibility(View.GONE);
		}
		TextView item2_pb_used=(TextView) view_item.findViewById(R.id.item2_pb_used);
		if(isAntherColor) {
			item2_pb_used.setText(Html.fromHtml(getResources().getString(R.string.home_used)+"<font color='red'>"+ftg_model.getProduct_used()+"</font>（本卡<font color='red'>"+Util.getSelf(getActivity())+"MB</font>）"));
		}
		else {
			item2_pb_used.setText(Html.fromHtml(getResources().getString(R.string.home_used)+"<font color='red'>"+ftg_model.getProduct_used()+"</font>"));
		}
		TextView item2_pb_unused=(TextView) view_item.findViewById(R.id.item2_pb_unused);
		item2_pb_unused.setText(Html.fromHtml(getResources().getString(R.string.home_unused)+"<font color='red'>"+ftg_model.getProduct_unused()+"</font>"));
		ImageView detail_bg_first_line=(ImageView) view_item.findViewById(R.id.detail_bg_first_line);
		if(isAddLastLine) {
			detail_bg_first_line.setVisibility(View.VISIBLE);
		}
		else {
			if(j==totalSize-1) {
				detail_bg_first_line.setVisibility(View.GONE);
			}
			else {
				detail_bg_first_line.setVisibility(View.VISIBLE);
			}
		}
		title2_layout.addView(view_item);
	
	}
	
}
