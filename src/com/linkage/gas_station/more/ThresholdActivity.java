package com.linkage.gas_station.more;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;

public class ThresholdActivity extends BaseActivity implements OnClickListener{
	
	private RelativeLayout type_1 = null;
	private RelativeLayout type_2 = null;
	private RelativeLayout type_3 = null;
	private RelativeLayout type_4 = null;
	private RelativeLayout type_5 = null;
	private RelativeLayout type_6 = null;
	private RelativeLayout type_7 = null;
	private ImageView type_image_1 = null;
	private ImageView type_image_2 = null;
	private ImageView type_image_3 = null;
	private ImageView type_image_4 = null;
	private ImageView type_image_5 = null;
	private ImageView type_image_6 = null;
	private ImageView type_image_7 = null;
	
	private Button sure_button = null;
	
	private int select_type = 0;
	private ArrayList<ImageView> imageList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_threshold);
		
		((GasStationApplication) getApplication()).tempActivity.add(ThresholdActivity.this);
		
		Bundle bundle = getIntent().getExtras();
		select_type = bundle.getInt("select_type");
		init();
	}
	
	public void init() {
		imageList = new ArrayList<ImageView>();
		type_1 = (RelativeLayout) findViewById(R.id.type_1);
		type_1.setOnClickListener(this);
		type_image_1 = (ImageView) findViewById(R.id.type_image_1);
		imageList.add(type_image_1);
		type_2 = (RelativeLayout) findViewById(R.id.type_2);
		type_2.setOnClickListener(this);
		type_image_2 = (ImageView) findViewById(R.id.type_image_2);
		imageList.add(type_image_2);
		type_3 = (RelativeLayout) findViewById(R.id.type_3);
		type_3.setOnClickListener(this);
		type_image_3 = (ImageView) findViewById(R.id.type_image_3);
		imageList.add(type_image_3);
		type_4 = (RelativeLayout) findViewById(R.id.type_4);
		type_4.setOnClickListener(this);
		type_image_4 = (ImageView) findViewById(R.id.type_image_4);
		imageList.add(type_image_4);
		type_5 = (RelativeLayout) findViewById(R.id.type_5);
		type_5.setOnClickListener(this);
		type_image_5 = (ImageView) findViewById(R.id.type_image_5);
		imageList.add(type_image_5);
		type_6 = (RelativeLayout) findViewById(R.id.type_6);
		type_6.setOnClickListener(this);
		type_image_6 = (ImageView) findViewById(R.id.type_image_6);
		imageList.add(type_image_6);
		type_7 = (RelativeLayout) findViewById(R.id.type_7);
		type_7.setOnClickListener(this);
		type_image_7 = (ImageView) findViewById(R.id.type_image_7);
		imageList.add(type_image_7);
		sure_button = (Button) findViewById(R.id.sure_button);
		sure_button.setOnClickListener(this);
		imageList.get(select_type).setImageResource(R.drawable.pop_select);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.type_1:
			for(ImageView image : imageList)
			{
				image.setImageDrawable(null);
			}
			type_image_1.setImageResource(R.drawable.pop_select);
			select_type = 0;
			break;
		case R.id.type_2:
			for(ImageView image : imageList)
			{
				image.setImageDrawable(null);
			}
			type_image_2.setImageResource(R.drawable.pop_select);
			select_type = 1;
			break;
		case R.id.type_3:
			for(ImageView image : imageList)
			{
				image.setImageDrawable(null);
			}
			type_image_3.setImageResource(R.drawable.pop_select);
			select_type = 2;
			break;
		case R.id.type_4:
			for(ImageView image : imageList)
			{
				image.setImageDrawable(null);
			}
			type_image_4.setImageResource(R.drawable.pop_select);
			select_type = 3;
			break;
		case R.id.type_5:
			for(ImageView image : imageList)
			{
				image.setImageDrawable(null);
			}
			type_image_5.setImageResource(R.drawable.pop_select);
			select_type = 4;
			break;
		case R.id.type_6:
			for(ImageView image : imageList)
			{
				image.setImageDrawable(null);
			}
			type_image_6.setImageResource(R.drawable.pop_select);
			select_type = 5;
			break;
		case R.id.type_7:
			for(ImageView image : imageList)
			{
				image.setImageDrawable(null);
			}
			type_image_7.setImageResource(R.drawable.pop_select);
			select_type = 6;
			break;
		case R.id.sure_button:
			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("select_type", select_type);
			data.putExtras(bundle);
			setResult(RESULT_OK, data);
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(ThresholdActivity.this);
	}
		
}
