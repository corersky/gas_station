package com.linkage.gas_station.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ShareLinearLayout extends LinearLayout {

	public ShareLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ShareLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		lis.sizeChange(w, h, oldw, oldh);
	}
	
	public interface SizeChangedListener {
		public void sizeChange(int w, int h, int oldw, int oldh);
	}
	
	SizeChangedListener lis=null;
	
	public void setOnSizeChangedListener(SizeChangedListener lis) {
		this.lis=lis;
	}

}
