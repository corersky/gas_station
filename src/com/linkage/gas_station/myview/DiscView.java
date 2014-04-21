package com.linkage.gas_station.myview;

import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DiscView extends View {
	
	//设置原点坐标
	public float cx0=-1;
	public float cy0=-1;
	public float radium=-1;
	//当前手势的坐标
	float currentX=-1;
	//位置接口
	OnPositionListener lis=null;
	
	Context context=null;
	Paint paint=null;
	
	Bitmap bmp_slide_hole=BitmapFactory.decodeResource(getResources(), R.drawable.slide_hole);
	int bitmapExtra=16;

	public DiscView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public DiscView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public DiscView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public void init(Context context) {
		this.context=context;
		
		cx0=100*Util.getDisplay(context);
		cy0=100*Util.getDisplay(context);
		radium=56*Util.getDisplay(context);
		
		paint=new Paint();
		paint.setAntiAlias(true);
		//getExtraX();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			currentX=event.getX();
			if(currentX<cx0-radium/2) {
				lis.getPosition(0);
			}
			else if(currentX>=cx0-radium/2&&currentX<cx0) {
				lis.getPosition(1);
			}
			else if(currentX>=cx0&&currentX<radium/2+cx0) {
				lis.getPosition(2);
			}
			else if(currentX>=radium/2+cx0) {
				lis.getPosition(3);
			}
			else {
				lis.getPosition(0);
			}
			break;
		case MotionEvent.ACTION_UP:
			currentX=event.getX();
			if(lis!=null) {
				if(currentX<cx0-radium/2) {
					lis.getPosition(0);
					currentX=cx0-(float)Math.sqrt(radium*radium-radium/2*radium/2);
				}
				else if(currentX>=cx0-radium/2&&currentX<cx0) {
					lis.getPosition(1);
					currentX=cx0-radium/2;
				}
				else if(currentX>=cx0&&currentX<radium/2+cx0) {
					lis.getPosition(2);
					currentX=cx0+radium/2;
				}
				else if(currentX>=radium/2+cx0) {
					lis.getPosition(3);
					currentX=cx0+(float)Math.sqrt(radium*radium-radium/2*radium/2);
				}
				else {
					lis.getPosition(0);
					currentX=cx0-(float)Math.sqrt(radium*radium-radium/2*radium/2);
				}
			}
			break;
		}
		invalidate();
		return true;
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawColor(Color.TRANSPARENT);
//		canvas.drawPoint(cx0, cy0, paint);
//		canvas.drawBitmap(bmp_noChoice, cx0-(float)Math.sqrt(radium*radium-radium/2*radium/2)-bitmapExtra*2, cy0-radium/2-bitmapExtra*2, paint);
//		canvas.drawBitmap(bmp_noChoice, cx0-radium/2-bitmapExtra*2, cy0-(float)Math.sqrt(radium*radium-radium/2*radium/2)-bitmapExtra*2, paint);
//		canvas.drawBitmap(bmp_noChoice, cx0+radium/2+bitmapExtra, cy0-(float)Math.sqrt(radium*radium-radium/2*radium/2)-bitmapExtra*2, paint);
//		canvas.drawBitmap(bmp_noChoice, cx0+(float)Math.sqrt(radium*radium-radium/2*radium/2)+bitmapExtra, cy0-radium/2-bitmapExtra*2, paint);
		if(currentX!=-1) {
			canvas.drawBitmap(bmp_slide_hole, getXPoint(currentX)-bitmapExtra, getYPoint(currentX)-bitmapExtra, paint);
		}
		else {
			currentX=cx0-(float)Math.sqrt(radium*radium-radium/2*radium/2);
			canvas.drawBitmap(bmp_slide_hole, getXPoint(currentX)-bitmapExtra, getYPoint(currentX)-bitmapExtra, paint);
		}
		super.draw(canvas);
	}
	
	public float getYPoint(float x) {
		//x坐标在圆点左侧
		if((cx0-currentX)<radium&&cx0>currentX) {
			return cy0-(float)Math.sqrt(radium*radium-(cx0-x)*(cx0-x));
		}
		else if((currentX-cx0)<=radium&&currentX>cx0) {
			return cy0-(float)Math.sqrt(radium*radium-(x-cx0)*(x-cx0));
		}
		return cy0;
	}
	
	public float getXPoint(float currentX) {
		if(currentX+radium<cx0) {
			return cx0-radium;
		}
		else if(cx0+radium<currentX) {
			return cx0+radium;
		}
		else {
			return currentX;
		}
	}
	
	public void setOnPositionListener(OnPositionListener lis) {
		this.lis=lis;
	}
	
	public interface OnPositionListener {
		public void getPosition(int pos);
	}
}
