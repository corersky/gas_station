package com.linkage.gas_station.myview;

import com.linkage.gas_station.util.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MyActivityExchangeScrollView extends HorizontalScrollView {
	
	Context context=null;
	ScreenChangeListener lis=null;

	public MyActivityExchangeScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public void init(Context context) {
		this.context=context;
	}
	
	public void setOnScreenChangeListener(ScreenChangeListener lis) {
		this.lis=lis;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(ev.getAction()==MotionEvent.ACTION_UP) {
			if(getScrollX()<=Util.getScreenWidth(context)/4) {
				smoothScrollTo(0, 0);
				lis.onScreenChange(0);
			}
			else if(getScrollX()>Util.getScreenWidth(context)/4&&getScrollX()<Util.getScreenWidth(context)/2) {
				smoothScrollTo(Util.getScreenWidth(context), 0);
				lis.onScreenChange(1);
			}
			else if(getScrollX()>=Util.getScreenWidth(context)/2&&getScrollX()<Util.getScreenWidth(context)/4*3) {
				smoothScrollTo(0, 0);
				lis.onScreenChange(0);
			}
			else if(getScrollX()>=Util.getScreenWidth(context)/4*3) {
				smoothScrollTo(Util.getScreenWidth(context), 0);
				lis.onScreenChange(1);
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	public interface ScreenChangeListener {
		public void onScreenChange(int screen);
	}
	
}

