package com.linkage.gas_station;

import com.linkage.gas_station.login.LoginOutActivity;
import com.linkage.gas_station.main.MainActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	
	private ProgressDialog _dialog = null;
	
	protected void showProgressDialog(int id)
	{
		if(_dialog == null)
    	{
			_dialog = new ProgressDialog(this);
    	}
		if(!_dialog.isShowing())
		{
			_dialog = ProgressDialog.show(this, "", getString(id) , true);
			_dialog.setCancelable(false);
			_dialog.setCanceledOnTouchOutside(false);
		}
	}
	
	protected void dismissProgressDialog()
	{
		if(_dialog != null &&_dialog.isShowing())
		{
			_dialog.dismiss();
		}
	}
	
	/**
	 * 自定义用户数据加载异常的toast
	 * @param str toast显示的文字
	 */
	public void showCustomToast(String str) {
		Toast toast=new Toast(BaseActivity.this);
		View view=LayoutInflater.from(BaseActivity.this).inflate(R.layout.custom_toast, null);
		TextView custom_toast_text=(TextView) view.findViewById(R.id.custom_toast_text);
		custom_toast_text.setText(str);
		toast.setDuration(2000);
		toast.setGravity(Gravity.TOP, 0, formatDipToPx(BaseActivity.this, 75));
		toast.setView(view);
		toast.show();
	}
	
	/**
	 * 自定义用户数据加载异常的toast fragment里面显示
	 * @param str toast显示的文字
	 */
	public static void showCustomToastWithContext(String str, Context context) {
		Toast toast=new Toast(context);
		View view=LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
		TextView custom_toast_text=(TextView) view.findViewById(R.id.custom_toast_text);
		custom_toast_text.setText(str);
		toast.setDuration(2000);
		toast.setGravity(Gravity.TOP, 0, formatDipToPx(context, 75));
		toast.setView(view);
		toast.show();
	}
	
	/**
	  * 把dip单位转成px单位
	  *
	  * @param context context对象
	  * @param dip dip数值
	  * @return
	  */
	 public static int formatDipToPx(Context context, int dip) {
		 DisplayMetrics dm = new DisplayMetrics();
		 ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		 return (int) Math.ceil(dip * dm.density);
	 }
	 
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(((GasStationApplication) getApplication()).isShowLoginOut) {
			((GasStationApplication) getApplication()).isShowLoginOut=false;
			
    		Intent intent_=new Intent();
	        intent_.setClass(BaseActivity.this, LoginOutActivity.class);
	        intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent_);
	        MainActivity.getInstance().not_able_change();
		}
	 }
	 
	 public void showCustomInfoDialog(String title) {
		 new AlertDialog.Builder(BaseActivity.this).setTitle("提示").setMessage(title).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).show();
	 }
}
