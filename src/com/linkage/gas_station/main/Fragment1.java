package com.linkage.gas_station.main;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.Flow_Group_Model;
import com.linkage.gas_station.model.OutputInfoModel;
import com.linkage.gas_station.myview.Fragment1_View;
import com.linkage.gas_station.myview.Fragment1_View.PageListener;
import com.linkage.gas_station.util.GetConnData;
import com.linkage.gas_station.util.Util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Fragment1 extends Fragment {
	
	OnJumpListener listener=null;
	int currentPage=0;
	boolean flag=true;
	
	OutputInfoModel model=null;
	ImageView cylinder_1=null;
	ImageView bg_small_t=null;
	ImageView button_go_1=null;
	ImageView button_go_flow_bank_1=null;
	TextView amount_text_1=null;
	TextView unused_text_1=null;
	Fragment1_View extra_layout=null;
		
	GetConnData cData=null;
	
	//跳转油桶接口
	public interface OnJumpListener {
		public void jumpToDetail();
	};
	
	public void setOnJumpListener(OnJumpListener listener) {
		this.listener=listener;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=null;
		DisplayMetrics dm=new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(dm.density==1.5) {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home1_15, null);
		}
		else if(dm.density==1.0) {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home1_10, null);
		}
		else {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home1_20, null);
		}
		cylinder_1=(ImageView) view.findViewById(R.id.cylinder_1);
		bg_small_t=(ImageView) view.findViewById(R.id.bg_small_t);
		if(Util.getUserArea(getActivity()).equals("0971")) {
			bg_small_t.setImageResource(R.drawable.bg_big_0971);
		}
		else {
			bg_small_t.setImageResource(R.drawable.bg_big);
		}
		cylinder_1.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					listener.jumpToDetail();
				} catch(Exception e) {
					
				}
			}});
		button_go_1=(ImageView) view.findViewById(R.id.button_go_1);
		button_go_flow_bank_1=(ImageView) view.findViewById(R.id.button_go_flow_bank_1);
		if(!Util.getUserArea(getActivity()).equals("2500")) {
			button_go_flow_bank_1.setVisibility(View.INVISIBLE);
		}
		amount_text_1=(TextView) view.findViewById(R.id.amount_text_1);
		unused_text_1=(TextView) view.findViewById(R.id.unused_text_1);
		extra_layout=(Fragment1_View) view.findViewById(R.id.extra_layout);
		extra_layout.setPageListener(new PageListener() {
			
			@Override
			public void page(int page) {
				// TODO Auto-generated method stub
				
			}
		});
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
		button_go_1.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.getInstance().jumpToJiayou(0, 1);
			}});
		button_go_flow_bank_1.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.getInstance().jumpToFlowBank(3);
			}});
		//动态添加的页面类型集合
		ArrayList<String> add_temp=new ArrayList<String>();
		if(Util.getUserArea(getActivity()).equals("2500")) {
			add_temp.add("3");
			add_temp.add("5");
		}
		else if(Util.getUserArea(getActivity()).equals("0971")) {
			add_temp.add("4");
			add_temp.add("5");
		}
//		for(int j=0;j<model.getModel_list().size();j++) {
//			System.out.println(model.getModel_list().get(j).getFlow_type_id());
//			if(model.getModel_list().get(j).getFlow_type_id()!=1&&
//					model.getModel_list().get(j).getFlow_type_id()!=3&&
//							model.getModel_list().get(j).getFlow_type_id()!=5&&
//									model.getModel_list().get(j).getFlow_type_id()!=11) {
//				add_temp.add(""+model.getModel_list().get(j).getFlow_type_id());
//			}
//		}
		//计算需要添加的页面个数
		int size=add_temp.size()%2==0?add_temp.size()/2:add_temp.size()/2+1;
		for(int j=0;j<size;j++) {
			ArrayList<String> add_type=new ArrayList<String>();
			//当前页码中的数量小于等于总个数，则为2个，否则就显示1个
			if(add_temp.size()>=2*(j+1)) {
				add_type.add(add_temp.get(j*2));
				add_type.add(add_temp.get(j*2+1));
			}
			else {
				add_type.add(add_temp.get(j*2));
			}
			extra_layout.addView(getExtraView(add_type));
		}
		//切换页码位置
		//changePage(size);
		
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
		//如果没有1只有11，则用11替换1
		if(!flow_type_id1_exists&&flow_type_id11_pos!=-1) {
			model.getModel_list().get(flow_type_id11_pos).setFlow_type_id(1);
			model.getModel_list().get(flow_type_id11_pos).setFlow_type_name("标准流量");
			flow_type_id11_pos=-1;
		}
		
		for(int i=0;i<model.getModel_list().size();i++) {
			//主副卡时候直接跳过
			if(model.getModel_list().get(i).getFlow_type_id()==11) {
				continue;
			}
			Flow_Group_Model fg_model=model.getModel_list().get(i);
			if(fg_model.getFlow_type_id()==1) {
				initUIData(fg_model, amount_text_1, unused_text_1, cylinder_1, 0);
			}
		}
	}
	
	/**
	 * 返回图片id
	 * @param ratio
	 * @return
	 */
	public int getImageRes(int ratio, ImageView cylinder_, int pos) {
		String imageId=String.valueOf(ratio/10)+String.valueOf(ratio%10>=5?5:0);
		//需要判断是不是小于5并且大于0
		if(imageId.equals("00")&&ratio%10>0) {
			imageId="05";
		}
		int res_id=0;
		if(pos==0) {
			res_id=getResources().getIdentifier(getActivity().getPackageName()+":drawable/cylinder_"+imageId, null,null);
		}
		else if(pos==1) {
			res_id=getResources().getIdentifier(getActivity().getPackageName()+":drawable/cylinder_small_another_"+imageId, null,null);
		}
		else {
			res_id=getResources().getIdentifier(getActivity().getPackageName()+":drawable/cylinder_small_"+imageId, null,null);
		}
		return res_id;
	}
	
	/**
	 * 加载页面数据
	 * @param fg_model
	 * @param amount_text_
	 * @param unused_text_
	 * @param cylinder_
	 * @param button_go_
	 */
	public void initUIData(Flow_Group_Model fg_model, TextView amount_text_, TextView unused_text_, ImageView cylinder_, int pos) {
		double unused=Double.parseDouble(fg_model.getFlow_type_unused().substring(0, fg_model.getFlow_type_unused().length()-2));
		double amount=Double.parseDouble(fg_model.getFlow_type_amount().substring(0, fg_model.getFlow_type_amount().length()-2));
		int ratio=(int) (unused*100/amount);
		amount_text_.setText(fg_model.getFlow_type_amount());
		unused_text_.setText(fg_model.getFlow_type_unused());
		cylinder_.setImageResource(getImageRes(ratio, cylinder_, pos));		
	}
	
	/**
	 * 得到不同页码视图
	 * @param gas_type
	 * @return
	 */
	public View getExtraView(ArrayList<String> gas_type) {
		View view=null;
		DisplayMetrics dm=new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(dm.density==1.5) {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home1_extra_15, null);
		}
		else if(dm.density==1.0) {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home1_extra_10, null);
		}
		else {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home1_extra_20, null);
		}
		LinearLayout layout_e_1=(LinearLayout) view.findViewById(R.id.layout_e_1);
		ImageView cylinder_e_1=(ImageView) view.findViewById(R.id.cylinder_e_1);
		ImageView bg_small_l=(ImageView) view.findViewById(R.id.bg_small_l);
		if(Util.getUserArea(getActivity()).equals("0971")) {
			bg_small_l.setImageResource(R.drawable.bg_small_01_0971);
		}
		else {
			bg_small_l.setImageResource(R.drawable.bg_small_01);
		}
		cylinder_e_1.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					listener.jumpToDetail();
				} catch(Exception e) {
					
				}
			}});
		ImageView button_go_e_1=(ImageView) view.findViewById(R.id.button_go_e_1);
		try {
			if((Util.getUserArea(getActivity()).equals("2500")&&gas_type.get(0).equals("3"))||(Util.getUserArea(getActivity()).equals("0971")&&gas_type.get(0).equals("4"))) {
				button_go_e_1.setVisibility(View.VISIBLE);
				button_go_e_1.setOnClickListener(new ImageView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MainActivity.getInstance().jumpToJiayou(2, 1);
						//MainActivity.getInstance().jumpToFlowBank(2);
					}});
			}
			else {
				button_go_e_1.setVisibility(View.INVISIBLE);
			}
		} catch(Exception e) {
			
		}
		TextView amount_text_e_1=(TextView) view.findViewById(R.id.amount_text_e_1);
		TextView unused_text_e_1=(TextView) view.findViewById(R.id.unused_text_e_1);
		for(int j=0;j<model.getModel_list().size();j++) {
			try {
				if(model.getModel_list().get(j).getFlow_type_id()==Integer.parseInt(gas_type.get(0))) {
					initUIData(model.getModel_list().get(j), amount_text_e_1, unused_text_e_1, cylinder_e_1, 1);
				}
			} catch(Exception e) {
				layout_e_1.setVisibility(View.INVISIBLE);
			}
		}
				
		LinearLayout layout_e_2=(LinearLayout) view.findViewById(R.id.layout_e_2);
		ImageView cylinder_e_2=(ImageView) view.findViewById(R.id.cylinder_e_2);
		ImageView bg_small_r=(ImageView) view.findViewById(R.id.bg_small_r);
		if(Util.getUserArea(getActivity()).equals("0971")) {
			bg_small_r.setImageResource(R.drawable.bg_small_02_0971);
		}
		else {
			bg_small_r.setImageResource(R.drawable.bg_small_02);
		}
		cylinder_e_2.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					listener.jumpToDetail();
				} catch(Exception e) {
					
				}
				
			}});
		ImageView button_go_e_2=(ImageView) view.findViewById(R.id.button_go_e_2);
		try {
			if(gas_type.get(1).equals("5")) {
				button_go_e_2.setVisibility(View.VISIBLE);
				button_go_e_2.setOnClickListener(new ImageView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MainActivity.getInstance().jumpToJiayou(1, 1);
						//MainActivity.getInstance().jumpToFlowBank(1);
					}});
			}
			else {
				button_go_e_2.setVisibility(View.INVISIBLE);
			}
		} catch(Exception e) {
			
		}
		TextView amount_text_e_2=(TextView) view.findViewById(R.id.amount_text_e_2);
		TextView unused_text_e_2=(TextView) view.findViewById(R.id.unused_text_e_2);
		for(int j=0;j<model.getModel_list().size();j++) {
			try {
				if(model.getModel_list().get(j).getFlow_type_id()==Integer.parseInt(gas_type.get(1))) {
					initUIData(model.getModel_list().get(j), amount_text_e_2, unused_text_e_2, cylinder_e_2, 2);
				}
			} catch(Exception e) {
				layout_e_2.setVisibility(View.INVISIBLE);
			}
		}
		return view;
	}
	
	/**
	 * 切换页码位置
	 */
	public void changePage(final int size) {
		
		currentPage=0;
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				extra_layout.snapToScreen(msg.what);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(flag) {
					try {
						Message m=new Message();
						if(size-1>currentPage) {
							currentPage++;							
						}
						else {
							currentPage=0;
						}
						m.what=currentPage;
						handler.sendMessage(m);
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}}).start();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		flag=false;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		flag=false;
	}

}
