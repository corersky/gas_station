package com.linkage.gas_station.life;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.AreaModel;
import com.linkage.gas_station.util.AreaParse;

public class AreaRegionActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	
	PinnedSectionListView area_region_listview=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_arearegion);
		
		((GasStationApplication) getApplication()).tempActivity.add(AreaRegionActivity.this);
		
		init();
	}
	
	public void init() {
		
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.area));
		
		AreaParse ap=new AreaParse();
		final ArrayList<AreaModel> model_list=ap.getAreaParse(loadData());		

		ArrayList<Item> result = new ArrayList<Item>();
		for (int i = 0; i < model_list.size(); i++) {
			if(model_list.get(i).getType()==1) {
				result.add(new Item(Item.SECTION, model_list.get(i).getName()));
			}
			else if(model_list.get(i).getType()==2) {
				result.add(new Item(Item.ITEM, model_list.get(i).getName()));
			}
		}	
		area_region_listview=(PinnedSectionListView) findViewById(R.id.area_region_listview);
		MyPinnedSectionListAdapter adapter = new MyPinnedSectionListAdapter(
				this, R.layout.adapter_arearegion, R.id.area_text, result);
		area_region_listview.setAdapter(adapter);
		area_region_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(model_list.get(position).getType()!=1) {
					Intent intent=getIntent();
					Bundle bundle=new Bundle();
					bundle.putString("name", model_list.get(position).getParentName()+" "+model_list.get(position).getName());
					bundle.putInt("id", model_list.get(position).getId());
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}				
			}
		});
	}
	
	public String loadData() {
		try {
			InputStream is=getAssets().open("js.xml");
			InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String result="";
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                result+=lineTxt;
            }
            read.close();
            is.close();
            return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static class Item {
		public static final int ITEM = 0;
		public static final int SECTION = 1;
		
		public final int type;
		public final String text;
		
		public Item(int type, String text) {
			this.type = type;
			this.text = text;
		}
		
		@Override public String toString() {
			return text;
		}
	}
	
	private static class MyPinnedSectionListAdapter extends ArrayAdapter<Item> implements PinnedSectionListAdapter {
		
		Context context=null;
		
		public MyPinnedSectionListAdapter(Context context, int resource, int textViewResourceId, List<Item> objects) {
			super(context, resource, textViewResourceId, objects);
			this.context=context;
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) super.getView(position, convertView, parent);
			view.setTextColor(Color.DKGRAY);
			if(getItem(position).type == Item.SECTION) {
				view.setBackgroundColor(context.getResources().getColor(R.color.area_city));
			}
			else {
				view.setBackgroundColor(Color.WHITE);
			}
			return view;
		}
		
		@Override public int getViewTypeCount() {
			return 2;
		}
		
		@Override public int getItemViewType(int position) {
			return getItem(position).type;
		}

		@Override public boolean isItemViewTypePinned(int viewType) {
			return viewType == Item.SECTION;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(AreaRegionActivity.this);
	}

}
