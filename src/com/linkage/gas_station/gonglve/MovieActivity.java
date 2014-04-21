package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.R;
import com.linkage.gas_station.myview.HVScrollView;
import com.linkage.gas_station.myview.HVScrollView.OnScrollYListener;
import com.linkage.gas_station.myview.HVScrollView.ZoomListener;

public class MovieActivity extends BaseActivity {
	
	HVScrollView movie_hsv_layout=null;
	ScrollView movie_row_scroll_layout=null;
	LinearLayout movie_layout=null;
	LinearLayout movie_row_layout=null;
	
	public static boolean isDrag=false;
	private double f=1;
	
	ArrayList<TextView> textViews=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_movie);
		
		textViews=new ArrayList<TextView>();
		
		init();
	}
	
	public void init() {
		movie_row_scroll_layout=(ScrollView) findViewById(R.id.movie_row_scroll_layout);
		movie_row_scroll_layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		movie_hsv_layout=(HVScrollView) findViewById(R.id.movie_hsv_layout);
		movie_hsv_layout.setListener(new ZoomListener() {

			@Override
			public void reduce() {
				addZoom();
			}

			@Override
			public void add() {
				reduceZoom();
			}
		});
		movie_hsv_layout.setOnScrollYListener(new OnScrollYListener() {
			
			@Override
			public void getScrollY(int deltaY) {
				// TODO Auto-generated method stub
				movie_row_scroll_layout.scrollBy(0, deltaY);
			}
		});
		movie_layout=(LinearLayout) findViewById(R.id.movie_layout);
		movie_row_layout=(LinearLayout) findViewById(R.id.movie_row_layout);
		
		LinearLayout.LayoutParams params_lines=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		for(int i=0;i<20;i++) {
			LinearLayout.LayoutParams row_lines=new LinearLayout.LayoutParams(50, 50);
			row_lines.leftMargin=5;
			row_lines.rightMargin=5;
			row_lines.topMargin=5;
			row_lines.bottomMargin=5;
			LinearLayout line_layout=new LinearLayout(this);
			line_layout.setOrientation(LinearLayout.HORIZONTAL);
			for(int j=0;j<20;j++) {
				TextView text=getTextView(""+(j+1));
				line_layout.addView(text, row_lines);
				textViews.add(text);
				if(j==0) {
					TextView row_text=getTextView(""+(i+1));
					movie_row_layout.addView(row_text, row_lines);
					textViews.add(row_text);
				}
			}
			movie_layout.addView(line_layout, params_lines);
		}
		
	}
	
	public TextView getTextView(String row) {
		TextView text=new TextView(this);
		text.setGravity(Gravity.CENTER);
		text.setBackgroundColor(Color.YELLOW);
		text.setPadding(5, 5, 5, 5);
		text.setText(row);
		text.setTextSize(15);
		return text;
	}

	protected void reduceZoom() {
		if(isDrag) {
			return;
		}
		isDrag = true;
		f -= 0.1;
		if(f < 1) {
			f = 1;
		} 
		setButton();
		isDrag = false;
	}

	protected void addZoom() {
		if(isDrag) {
			return;
		}
		isDrag = true;
		f += 0.1;
		if(f > 5) {
			f = 5;
		} 
		setButton();
		isDrag = false;
	}
	
	public void setButton() {
		for(int i=0;i<textViews.size();i++) {
			LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)textViews.get(i).getLayoutParams();
			params.width=(int) (50 * f);
			params.height=(int) (50 * f);
			textViews.get(i).setLayoutParams(params);
		}

	}
}
