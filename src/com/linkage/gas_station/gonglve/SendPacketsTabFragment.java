package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import com.linkage.gas_station.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost;
import android.widget.TextView;

public class SendPacketsTabFragment extends Fragment {
	
	View view=null;
	FragmentTabHost sendpackets_tab=null;
	
	String[] textArray={"流量红包", "土豪牛人榜", "圈子排名"};
	ArrayList<View> viewsArray=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null) {
			viewsArray=new ArrayList<View>();
			
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sendpacketstab, container, false);
			sendpackets_tab=(FragmentTabHost) view.findViewById(R.id.sendpackets_tab);
			sendpackets_tab.setup(getActivity(), getChildFragmentManager(), R.id.snedpackets_realtabcontent);
			sendpackets_tab.getTabWidget().setDividerDrawable(null);
			sendpackets_tab.setOnTabChangedListener(new OnTabChangeListener() {
				
				@Override
				public void onTabChanged(String arg0) {
					// TODO Auto-generated method stub
					for(int i=0;i<viewsArray.size();i++) {
						View view=viewsArray.get(i);
						TextView view_sendpacketstabicon_text=(TextView) view.findViewById(R.id.view_sendpacketstabicon_text);
						TextView view_sendpacketstabicon_image=(TextView) view.findViewById(R.id.view_sendpacketstabicon_image);
						if(arg0.equals(textArray[i])) {
							view_sendpacketstabicon_text.setTextColor(Color.parseColor("#124faa"));
							view_sendpacketstabicon_image.setBackgroundColor(Color.parseColor("#124faa"));
						}
						else {
							view_sendpacketstabicon_text.setTextColor(Color.parseColor("#777777"));
							view_sendpacketstabicon_image.setBackgroundColor(Color.TRANSPARENT);
						}
					}
				}
			});
			for(int i=0;i<3;i++) {
				View view=getTabSpecItem(textArray[i]);
				TabHost.TabSpec spec=sendpackets_tab.newTabSpec(textArray[i]).setIndicator(view);
				viewsArray.add(view);
				if(i==0) {
					sendpackets_tab.addTab(spec, RedEnvelopeFragment.class, null);
				}
				else if(i==1) {
					sendpackets_tab.addTab(spec, NiubilityFragment.class, null);
				}
				else if(i==2) {
					sendpackets_tab.addTab(spec, RankingFragment.class, null);
				}
			}
			sendpackets_tab.setCurrentTab(0);
		}
		ViewGroup parent=(ViewGroup) view.getParent();
		if(parent!=null) {
			parent.removeView(view);
		}
		return view;
	}
	
	private View getTabSpecItem(String name) {
		View view=LayoutInflater.from(getActivity()).inflate(R.layout.view_sendpacketstabicon, null);
		TextView view_sendpacketstabicon_text=(TextView) view.findViewById(R.id.view_sendpacketstabicon_text);
		view_sendpacketstabicon_text.setText(name);
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK&&requestCode==103) {
			getChildFragmentManager().findFragmentByTag("流量红包").onActivityResult(requestCode, resultCode, data);
		}
	}

}
