package com.linkage.gas_station.main;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

public class HomeAdapter extends FragmentPagerAdapter {
	
	ArrayList<Fragment> fragment_list=null;
	FragmentManager fm=null;

	public HomeAdapter(FragmentManager fm, ArrayList<Fragment> fragment_list) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.fragment_list=fragment_list;
		this.fm=fm;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return fragment_list.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragment_list.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}
	
	public void setFragments(ArrayList<Fragment> fragments) {
		if(this.fragment_list != null){
			FragmentTransaction ft = fm.beginTransaction();
			for(Fragment f:this.fragment_list){
				ft.remove(f);
			}
			ft.commit();
			ft=null;
			fm.executePendingTransactions();
		}
		this.fragment_list = fragments;
		notifyDataSetChanged();
	}

}
