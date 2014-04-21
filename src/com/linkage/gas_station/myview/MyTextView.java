package com.linkage.gas_station.myview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {
	
	//最终显示金额
	int maxNum=0;
	//每次递增金额
	int perNum=0;
	//当前金额
	int currentNum=0;

	public double getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	Handler handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(currentNum<maxNum) {
				currentNum+=perNum;
				setText(""+currentNum);
				handler.sendMessageDelayed(new Message(), 50);
			}
			else {
				currentNum=maxNum;
				setText(""+currentNum);
			}
		}
	};
	
	public void setStart() {
		currentNum=0;
		perNum=maxNum/10;
		if(perNum==0) {
			perNum=1;
		}
		handler.sendMessage(new Message());
	}

}
