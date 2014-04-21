package com.linkage.gas_station.jpush;

import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.more.SuggestActivity_New;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class JPushOpenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent=new Intent();
        intent.setClass(JPushOpenActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        
        //意见反馈通知会修改此标志位
        if(((GasStationApplication) getApplicationContext()).isNewSuggest) {
        	//此处直接置为false，是因为从外部页面直接打开，已经无需再刷新Ui了
        	((GasStationApplication) getApplicationContext()).isNewSuggest=false;
        	Intent intent2=new Intent();
            intent2.setClass(JPushOpenActivity.this, SuggestActivity_New.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent2);
        }
        
        finish();
	}

}
