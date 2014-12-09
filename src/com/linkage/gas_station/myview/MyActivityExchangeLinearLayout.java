package com.linkage.gas_station.myview;

import com.linkage.gas_station.util.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MyActivityExchangeLinearLayout extends LinearLayout {
	
	Context context=null;

	public MyActivityExchangeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		this.context=context;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(Util.getScreenWidth(context), Util.dip2px(context, 50));
	}
}
