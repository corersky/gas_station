package com.linkage.gas_station.gonglve;

import android.os.Bundle;
import android.view.Window;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.R;

public class RandomShareRedEnvelopesActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_randomshareredenvelopes);
	}

}
