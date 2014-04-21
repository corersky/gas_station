package com.linkage.gas_station.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MyCircleProgress extends View {
	
	Paint paint=null;
	Paint paint_outside=null;

	public MyCircleProgress(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public MyCircleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public MyCircleProgress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public void init() {
		paint=new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(10);
		paint.setColor(0x880000FF);
		
		paint_outside=new Paint();
		paint_outside.setAntiAlias(true);
		paint_outside.setStyle(Paint.Style.STROKE);
		paint_outside.setStrokeWidth(10);
		paint_outside.setColor(0x886600FF);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		canvas.drawArc(new RectF(0, 0, 100, 100), 0, 360, false, paint_outside);
		canvas.drawArc(new RectF(0, 0, 100, 100), 0, 180, false, paint);
		invalidate();
	}

}
