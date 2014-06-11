package com.linkage.gas_station.memberday;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.BitmapUtils;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.R;

public class MemberShowCodeActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	ImageView showcode_image=null;
	TextView showcode_name=null;
	TextView showcode_code=null;
	
	BitmapUtils bitmapUtils=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_showcode);
		
		bitmapUtils=new BitmapUtils(MemberShowCodeActivity.this);
		bitmapUtils.configDefaultLoadingImage(R.drawable.ic_launcher);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);
		
		init();
	}
	
	private void init() {
		
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("��Ա��");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
		showcode_name=(TextView) findViewById(R.id.showcode_name);
		showcode_name.setText(getIntent().getExtras().getString("name"));
		showcode_code=(TextView) findViewById(R.id.showcode_code);
		showcode_code.setText(getIntent().getExtras().getString("id"));
		showcode_image=(ImageView) findViewById(R.id.showcode_image);
		try {			
			Bitmap bmp=Create2DCode(new String(getIntent().getExtras().getString("name").getBytes("UTF-8"), "ISO-8859-1")+"!#$&"+getIntent().getExtras().getString("id"));
			showcode_image.setImageBitmap(bmp);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//���ɶ�ά��
	public Bitmap Create2DCode(String str) throws WriterException {  
		//���ɶ�ά����,����ʱָ����С,��Ҫ������ͼƬ�Ժ��ٽ�������,������ģ������ʶ��ʧ��  
		BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 300, 300);  
		int width = matrix.getWidth();  
		int height = matrix.getHeight();  
		//��ά����תΪһά��������,Ҳ����һֱ��������  
		int[] pixels = new int[width * height];  
		for (int y = 0; y < height; y++) {  
			for (int x = 0; x < width; x++) {  
				if(matrix.get(x, y)){  
					pixels[y * width + x] = 0xff000000;  
				}                   
			}  
		}  
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
		//ͨ��������������bitmap,����ο�api  
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
		//��ʼ����ͼ��
		File file=new File(Environment.getExternalStorageDirectory()+"/personal.jpg");
		if(file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		Bitmap bitmap_ = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(bitmap_);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.save();
		canvas.restore();

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			bitmap_.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return bitmap_;
	}

}