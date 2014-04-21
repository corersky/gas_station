package com.linkage.gas_station.myview;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.linkage.gas_station.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GonglveListView extends ListView {
	
	View headView=null;
	ImageView refresh_img=null;
	ProgressBar refresh_pb=null;
	TextView refresh_text=null;
	TextView refresh_time=null;
	
	RotateAnimation start_a=null;
	RotateAnimation start_b=null;

	boolean isRecord=false;
	//设置下拉比例
	int radio=3;
	//开始滑动位置
	int startY=0;
	//是否达到下拉刷新
	boolean isBack=false;
	//当前状态 0.没有任何操作 1.下拉刷新 2.松开刷新 3.正在刷新
	int state=0;
	//headview的高度
	int headviewHeight=0;
	//刷新时间
	String refreshTime="";
	
	OnRefreshListener listener=null;

	public GonglveListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public GonglveListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public GonglveListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public void init(Context context) {
		start_a=new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		start_a.setInterpolator(new LinearInterpolator());
		start_a.setFillAfter(true);
		start_a.setDuration(500);
		
		start_b=new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		start_b.setInterpolator(new LinearInterpolator());
		start_b.setFillAfter(true);
		start_b.setDuration(500);
		
		headView=LayoutInflater.from(context).inflate(R.layout.gonglve_headview, null);
		refresh_img=(ImageView) headView.findViewById(R.id.refresh_img);
		refresh_pb=(ProgressBar) headView.findViewById(R.id.refresh_pb);
		refresh_text=(TextView) headView.findViewById(R.id.refresh_text);
		refresh_time=(TextView) headView.findViewById(R.id.refresh_time);
		refresh_time.setText("最后更新：");
		measureView(headView);
		headviewHeight=headView.getMeasuredHeight();
		headView.setPadding(0, -headviewHeight, 0, 0);
		headView.invalidate();
		addHeaderView(headView, null, false);
	}
	
	public void measureView(View child) {
		ViewGroup.LayoutParams params=child.getLayoutParams();
		if(params==null) {
			params=new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec=ViewGroup.getChildMeasureSpec(0, 0+0, params.width);
		int childHeightSpec=0;
		if(params.height>0) {
			childHeightSpec=MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
		}
		else {
			childHeightSpec=MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		int tempY=(int)ev.getY();
		switch(ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(listener.getFirstItem()==0&&!isRecord) {
				isRecord=true;
				startY=tempY;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(state!=3) {
				if(isRecord&&state==0) {
					if(tempY-startY>0) {
						state=1;
						changeView();
					}				
				}
				if(state==1) {	
					setSelection(0);
					headView.setPadding(0, (int) (-headviewHeight+((int) tempY/1.6-startY)), 0, 0);
					if(-headviewHeight+(tempY-startY)/radio>0) {
						state=2;
						isBack=true;
						changeView();
					}
					else if(tempY<=startY) {
						state=0;
						changeView();
					}				
				}
				if(state==2) {
					setSelection(0);
					headView.setPadding(0, (int) (-headviewHeight+(tempY/1.6-startY)), 0, 0);
					if(-headviewHeight+(tempY-startY)/radio<=0&&tempY>startY) {
						state=1;
						changeView();
					}
					else if(tempY<=startY) {
						state=0;
						changeView();
					}
				}
			}			
			break;
		case MotionEvent.ACTION_UP:
			isRecord=false;
			isBack=false;
			if(state==2) {
				state=3;
				listener.loadData();
			}
			if(state==1) {
				state=0;
			}
			changeView();
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	public void changeView() {
		switch(state) {
		case 0:
			headView.setPadding(0, -headviewHeight, 0, 0);
			refresh_img.setVisibility(View.VISIBLE);
			refresh_pb.setVisibility(View.INVISIBLE);
			refresh_img.clearAnimation();
			refresh_img.setImageResource(R.drawable.arrow);
			refresh_text.setText("下拉即可更新...");
			break;
		case 1:
			if(isBack) {
				isBack=false;
				refresh_img.clearAnimation();
				refresh_img.startAnimation(start_b);
				refresh_text.setText("下拉即可更新...");
			}
			break;
		case 2:
			refresh_img.clearAnimation();
			refresh_img.startAnimation(start_a);
			refresh_text.setText("松开即可更新...");
			break;
		case 3:
			refresh_img.clearAnimation();
			refresh_img.setVisibility(View.INVISIBLE);
			refresh_pb.setVisibility(View.VISIBLE);
			headView.setPadding(0, 0, 0, 0);
			refresh_text.setText("正在刷新");
			break;
		}
	}
	
	public void loadComp() {
		state=0;
		changeView();
	}
	
	/**
	 * 更新时间修改
	 */
	public void setRefresh_time() {
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		refresh_time.setText("最后更新："+format.format(new Date()));
	}
	
	public interface OnRefreshListener {
		public void loadData();
		public int getFirstItem();
	}
	
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.listener=listener;
	}
	
	public void setStart() {
		headView.setPadding(0, 0, 0, 0);
		refresh_pb.setVisibility(View.VISIBLE);
		refresh_img.setVisibility(View.INVISIBLE);
		refresh_text.setText("正在刷新");
	}
	
}
