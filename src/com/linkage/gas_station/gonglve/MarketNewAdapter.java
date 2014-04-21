package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MarketNewAdapter extends PagerAdapter {
	
	ArrayList<View> arrayView=null;
	
	public MarketNewAdapter(ArrayList<View> arrayView) {
		this.arrayView=arrayView;
	}
	
	@Override  
    public int getCount() {  
        return Integer.MAX_VALUE;  
    }  

    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        return arg0 == arg1;  
    }  

    @Override  
    public int getItemPosition(Object object) {  
        // TODO Auto-generated method stub  
        return super.getItemPosition(object);  
    }  

    @Override  
    public void destroyItem(View arg0, int arg1, Object arg2) {  
        // TODO Auto-generated method stub  
        //((ViewPager) arg0).removeView(list.get(arg1));  
    }  

    @Override  
    public Object instantiateItem(View arg0, int arg1) {  
        // TODO Auto-generated method stub  
        try{  
        	((ViewPager) arg0).addView(arrayView.get(arg1%arrayView.size()),0);  
        }catch (Exception e) {  
            // TODO: handle exception  
        }  
        return arrayView.get(arg1%arrayView.size());  
    }  

    @Override  
    public void restoreState(Parcelable arg0, ClassLoader arg1) {  
        // TODO Auto-generated method stub  

    }  

    @Override  
    public Parcelable saveState() {  
        // TODO Auto-generated method stub  
        return null;  
    }  

    @Override  
    public void startUpdate(View arg0) {  
        // TODO Auto-generated method stub  

    }  

    @Override  
    public void finishUpdate(View arg0) {  
        // TODO Auto-generated method stub  

    }
}
