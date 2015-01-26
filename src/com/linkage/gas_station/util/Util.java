package com.linkage.gas_station.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.jpush.JPushReceiver;
import com.linkage.gas_station.model.ContactModel;
import com.linkage.gas_station.model.OutputInfoModel;
import com.linkage.gas_station.util.hessian.CommonManager;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;
import com.linkage.gasstationjni.GasJni;

public class Util {
	
	/**
	 * 获取手机通讯录中的联系人信息
	 * @param context
	 * @return
	 */
	public static HashMap<String, ArrayList<ContactModel>> getPhoneContacts(Context context) {		
		//只获取姓名及电话号码
		final String[] PHONES_PROJECTION=new String[]{Phone.DISPLAY_NAME, Phone.NUMBER};
		HashMap<String, ArrayList<ContactModel>> map=new HashMap<String, ArrayList<ContactModel>>();		
		ContentResolver cr=context.getContentResolver();
		Cursor cs=cr.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
		if(cs!=null) {
			cs.moveToFirst();
			for(int i=0;i<cs.getCount();i++) {
				cs.moveToPosition(i);
				ContactModel model=new ContactModel();
				model.setName(cs.getString(0));
				model.setPhoneNum(cs.getString(1));
				String firstLetter=getFirstPinyin(cs.getString(0));
				model.setLetter(firstLetter);
				if(map.containsKey(firstLetter)) {
					map.get(firstLetter).add(model);
				}
				else {
					ArrayList<ContactModel> list=new ArrayList<ContactModel>();
					list.add(model);
					map.put(firstLetter, list);
				}				
			}
			cs.close();
		}
		return map;
	}
	
	/**
	 * 获取首字母
	 * @param str 文字字符串
	 * @return
	 */
	public static String getFirstPinyin(String str) {
		String result="";
		char[] nameChar=str.toCharArray();
		HanyuPinyinOutputFormat format=new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		//汉字取首字母
		try {
			if(nameChar[0]>128) {
				result=String.valueOf(PinyinHelper.toHanyuPinyinStringArray(nameChar[0])[0].charAt(0));
			}
			//大写字母转小写
			else if(nameChar[0]>=65&&nameChar[0]<=90) {
				result=String.valueOf(nameChar[0]).toLowerCase();
			}
			//小写字母直接返回
			else if(nameChar[0]>=97&&nameChar[0]<=122) {
				result=String.valueOf(nameChar[0]);
			}
			//异常字符用#表示
			else {
				result="#";
			}
		} catch(Exception e) {
			result="#";
		}		
		return result;
	}
	
	/**
	 * 获取文字宽度
	 * @param str
	 * @return
	 */
	public static int getNameLength(String str) {
		char[] c=str.toCharArray();
		int length=0;
		for(int i=0;i<c.length;i++) {
			//汉字占2个字符
			if(c[i]>128) {
				length+=2;
			}
			else {
				length+=1;
			}
		}
		return length;
	} 

	/**
     * 下载离线图片
     * @param url
     */
    public static void downloadOfflinePic(final String url_) {
    	String path="";
    	path=Environment.getExternalStorageDirectory().getPath()+"/gas/";
    	try {
    		URL url=new URL(url_);
			URLConnection conn=url.openConnection();
			int total=conn.getContentLength();
			HttpGet httpRequest=new HttpGet(url_);
			HttpResponse resp=new DefaultHttpClient().execute(httpRequest);
			if(resp.getStatusLine().getStatusCode()==200) {
				InputStream is=resp.getEntity().getContent();
				BufferedInputStream bis=new BufferedInputStream(is);
				String filePath=path+url_.substring(url_.lastIndexOf("/")+1, url_.length());
	            File file=new File(filePath);
				if(!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fos=new FileOutputStream(file);
				BufferedOutputStream bos=new BufferedOutputStream(fos);
				byte[] byte_=new byte[4096];
				int count=0;
				int loadNum=0;
				int downloadPercent=0;				    	
		    	while((count=bis.read(byte_))!=-1) {
					loadNum+=count;
					int percent=loadNum*100/total;
					if(percent-downloadPercent>10) {
						downloadPercent=percent;
					}
					bos.write(byte_, 0, count);
				}
				bos.flush();
				bos.close();
				fos.flush();
				fos.close();
				bis.close();
				is.close();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 获取唯一机器码
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
    	try {
    		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        	return tm.getDeviceId();
    	} catch(Exception e) {
    		return "";
    	}
   	
    }
    
    /**
     * 获取wifi mac地址
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
    	try {
    		String macAddress = "";
    	    WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	    WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
    	    if (null != info) {
    	        macAddress = info.getMacAddress();
    	    }
    	    if(!Util.convertNull(macAddress).equals("")) {
    	    	if(macAddress.indexOf(":")!=-1) {
    	    		macAddress=macAddress.replace(":", "");
    	    		return macAddress;
    	    	}
    	    }
    	    return "";
    	} catch(Exception e) {
    		return "";
    	}   	
	}
    
    /**
     * 获取手机IMSI
     * @param context
     * @return
     */
    public static String getIMSINum(Context context) {
    	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	return tm.getSubscriberId();
    }
    
    //删除多图片缓存
  	public static synchronized void cleanBitmap(LinkedHashMap<String, SoftReference<Bitmap>> map, Context context) {
  		try {
  			synchronized(map) {
  				Object[] obj=map.keySet().toArray();
  				for(int i=0;i<obj.length;i++) {			
  					SoftReference<Bitmap> sr=map.remove(obj[i]);
  					if(sr!=null) {
  						Bitmap bmp=sr.get();
  						if(bmp!=null&&!bmp.isRecycled()) {
  							bmp.recycle();
  							bmp=null;
  						}
  					}
  				}
  			}
  		} catch(Exception e) {
  			
  		}				
  	}
  	
  	//删除单页面图片
  	public static synchronized void cleanBitmap(ArrayList<Bitmap> bmp_list) {
  		for(int i=0;i<bmp_list.size();i++) {
  			Bitmap bmp=bmp_list.get(i);
  			if(bmp!=null&&!bmp.isRecycled()) {
  				bmp.recycle();
  				bmp=null;
  			}
  		}
  	}
  	
  	public static String convertNull(String returnValue) {
        try {
            returnValue = (returnValue==null||(returnValue!=null&&returnValue.equals("null")))?"":returnValue;
        } catch (Exception e) {
            returnValue = "";
        }
        return returnValue;
    }
  	
  	/**
  	 * 获取用户手机号以及地区码
  	 * @param context
  	 * @return
  	 */
  	public static ArrayList<String> getUserInfo(Context context) {
  		ArrayList<String> list=new ArrayList<String>();
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		list.add(sp.getString("phoneNum", ""));
  		list.add(sp.getString("area_code", ""));
  		list.add(sp.getString("imsi", ""));
  		list.add(sp.getString("password", ""));
  		return list;
  	}
  	
  	/**
  	 * 设置用户手机号以及地区码
  	 * @param context
  	 * @param phoneNum
  	 * @param area_code
  	 */
  	public static void setUserInfo(Context context, String phoneNum, String area_code, String imsi) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		editor.putString("phoneNum", phoneNum);
  		editor.putString("area_code", area_code);
  		editor.putString("imsi", imsi);
  		GasJni hj=new GasJni();
  		editor.putString("password", hj.readMessageFromJNI(phoneNum, getDeviceId(context)));
  		editor.commit();
  	}
  	
  	public static String getPassWord(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		return sp.getString("password", "");
  	}
  	
  	public static void setPassWord(Context context, String phoneNum) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		GasJni hj=new GasJni();
  		editor.putString("password", hj.readMessageFromJNI(phoneNum, getDeviceId(context)));
  		editor.commit();
  	}
  	
  	/**
  	 * 获取用户的地区专属url
  	 * @param context
  	 * @return
  	 */
  	public static String getStartURL(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		return sp.getString("startUrl", "");
  	}
  	
  	/**
  	 * 设置用户专属url
  	 * @param context
  	 * @param startUrl
  	 */
  	public static void setStartUrl(Context context, String startUrl) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		editor.putString("startUrl", startUrl);
  		editor.commit();
  	}
  	
  	/**
  	 * 设置用户专属省份
  	 * @param context
  	 * @param userArea
  	 */
  	public static synchronized void setUserArea(Context context, String userArea) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		editor.putString("userArea", userArea);
  		editor.commit();
  	}
  	
  	/**
  	 * 获取用户专属省份
  	 * @param context
  	 * @return
  	 */
  	public static String getUserArea(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		return sp.getString("userArea", "");
  	}
  	
  	/**
  	 * 获取app上传时间
  	 * @param context
  	 * @return
  	 */
  	public static long getLoadAppTime(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		return sp.getLong("uploadApp", 0);
  	}
  	
  	/**
  	 * 设置app上传时间
  	 * @param context
  	 * @param time
  	 */
  	public static void setLoadAppTime(Context context, long time) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		editor.putLong("uploadApp", time);
		editor.commit();
  	}
  	
  	/**
  	 * 设置各省可以全部选取的url
  	 * @param context
  	 * @param wholeUrl
  	 */
  	public static void setWholeUrl(Context context, String wholeUrl) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		editor.putString("wholeUrl", wholeUrl);
  		editor.commit();
  	}
  	
  	/**
  	 * 得到各省可以全部选取的url
  	 * @param context
  	 * @return
  	 */
  	public static LinkedList<String> getWholeUrl(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		String temp=sp.getString("wholeUrl", "");
  		LinkedList<String> temp_list=new LinkedList<String>();
  		if(!temp.equals("")) {
  			for(int i=0;i<temp.split("&").length;i++) {
  	  			temp_list.add(temp.split("&")[i]);
  	  		}
  		} 		
  		return temp_list;
  	}
  	
  	/**
  	 * 设置服务器时间差值
  	 * @param context
  	 * @param time
  	 */
  	public static void setTimeExtra(Context context, long time) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		editor.putLong("time_extra", time-System.currentTimeMillis());
		editor.commit();
  	}
  	
  	/**
  	 * 获取服务器时间差值
  	 * @param context
  	 * @return
  	 */
  	public static long getTimeExtra(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		return sp.getLong("time_extra", 0);
  	}
  	
  	/**
  	 * 设置退出登录标志位
  	 * @param context
  	 * @param flag
  	 */
  	public static void setLoginOut(Context context, boolean flag) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		editor.putBoolean("isLoginOut", flag);
		editor.commit();
  	}
  	
  	/**
  	 * 获取唯一更新判断
  	 * @param context
  	 * @return
  	 */
  	public static String getSimpleCode(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		return sp.getString("machinceCode", "");
  	}
  	
  	/**
  	 * 更新唯一记录
  	 * @param context
  	 * @param code
  	 */
  	public static void setSimpleCode(Context context, String code) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		editor.putString("machinceCode", code);
  		editor.commit();
  	}
  	
  	/**
  	 * 获取登录 
  	 * @param context
  	 * @return
  	 */
  	public static boolean isLoginOut(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		return sp.getBoolean("isLoginOut", false);
  	}
  	
  	/**
  	 * 流量银行插入记录
  	 * @param context
  	 * @param phoneNum
  	 */
  	public static void insertFlowBankHistory(Context context, String phoneNum) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		String totalHistory=sp.getString("flow_bank", "");
  		if(totalHistory.indexOf(phoneNum)==-1) {
  			totalHistory+=phoneNum+"&";
  			SharedPreferences.Editor editor=sp.edit();
  			editor.putString("flow_bank", totalHistory);
  			editor.commit();
  		}
  	}
  	
  	/**
  	 * 获取流量银行历史记录
  	 * @param context
  	 * @return
  	 */
  	public static String getFlowBankHistory(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		return sp.getString("flow_bank", "");
  	}
  	
  	/**
  	 * 流量银行插入记录
  	 * @param context
  	 * @param phoneNum
  	 */
  	public static void cleanFlowBankHistory(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
		editor.putString("flow_bank", "");
		editor.commit();
  	}
  	
  	public static double getSelf(Context context) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		String temp=sp.getString("flow_self", "");
  		if(temp.equals("")) {
  			return 0;
  		}
  		else {
  			return Double.parseDouble(temp.substring(0, temp.length()-2));
  		}
  	}
  	
  	public static void setSelf(Context context, String flow) {
  		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
		editor.putString("flow_self", flow);
		editor.commit();
  	}
  	
  	//序列化
  	public static byte[] serialize(OutputInfoModel model){ 
          try { 
        	  ByteArrayOutputStream mem_out = new ByteArrayOutputStream(); 
  			  ObjectOutputStream out = new ObjectOutputStream(mem_out);  
              out.writeObject(model); 
              out.close(); 
              mem_out.close();  
              byte[] bytes =  mem_out.toByteArray(); 
              return bytes; 
          } catch (IOException e) { 
              return null; 
          } 
      } 
   
  	//反序列化
  	public static OutputInfoModel deserialize(byte[] bytes){ 
  		try { 
  			ByteArrayInputStream mem_in = new ByteArrayInputStream(bytes); 
  			ObjectInputStream in = new ObjectInputStream(mem_in);  
  			OutputInfoModel model = (OutputInfoModel)in.readObject();  
  			in.close(); 
  			mem_in.close();  
  			return model; 
  		} catch (StreamCorruptedException e) { 
  			return null; 
  		} catch (ClassNotFoundException e) { 
  			return null; 
  		} catch (IOException e) { 
  			return null; 
  		} 
  	} 
  	
  	//获取时间差值
  	public static String getLastTime(String str) {
  		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date=format.parse(str);		
			Date today=new Date();		
			int day=(int) ((date.getTime()-today.getTime())/(1000*60*60*24));
			int hour=(int) ((date.getTime()-today.getTime())/(60*60*1000)-day*24);
			int minute=(int) ((date.getTime()-today.getTime())/(60*1000)-day*24*60-hour*60);
			int second=(int) ((date.getTime()-today.getTime())/1000-day*24*60*60-hour*60*60-minute*60);
	  		return String.valueOf(day+"天"+hour+"小时"+minute+"分"+second+"秒");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
  	}
  	
  	/*
  	 * 首页加载判断是否是新数据
  	 */
  	public static boolean isNewData(OutputInfoModel model, Context context) {
  		//如果更换了sim卡，直接强制刷新
  		if(((GasStationApplication) context.getApplicationContext()).isChangeSim) {
  			((GasStationApplication) context.getApplicationContext()).isChangeSim=false;
  			return true;
  		}
  		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		try {
			Date date=format.parse(model.getData_time());
			GetConnData cData=new GetConnData(context);
			Date date_old=format.parse(cData.getMonitor().getData_time());
			if(date_old.getTime()<=date.getTime()) {
				return true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
  		return false;
  	}
  	
	/**
	 * 上传用户信息
	 */
	public static void uploadClientInfo(final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				LinkedList<String> wholeUrl=Util.getWholeUrl(context);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) context.getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(context).get(0):((GasStationApplication) context.getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) context.getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(context);
						PackageManager manager=context.getPackageManager();
						PackageInfo info=manager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
						CommonManager commonManager=GetWebDate.getHessionFactiory(context).create(CommonManager.class, ((GasStationApplication) context.getApplicationContext()).AreaUrl+"/hessian/commonManager", context.getClassLoader());
						commonManager.saveVersion(Util.getDeviceId(context)+Util.getMacAddress(context),android.os.Build.VERSION.RELEASE, info.versionName);
						System.out.println("abc:"+android.os.Build.BRAND);
						flag=false;
						((GasStationApplication) context.getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
			        } catch(Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							//ip 端口等错误  java.net.SocketTimeoutException
							else {
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									flag=false;
								}
							}							
						}
						else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									flag=false;
								}
							}
						}
						else {
							wholeUrl.remove(currentUsedUrl);
							if(wholeUrl.size()>0) {
								currentUsedUrl=wholeUrl.get(0);
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								flag=false;
							}
						}
					}
				}			
				
			}}).start();
	}
	
	public static void setSave(Context context, boolean flag) {
		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putBoolean("saveSource", flag);
		editor.commit();
	}
	
	public static boolean getSave(Context context) {
		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
		return sp.getBoolean("saveSource", false);
	}
	
	
  	/**
  	  * 时间戳加密
  	  * @param time
  	  * @return
  	  */
  	public static String encodeTime(long time) {
	  	long time1=time/1000000000;
	  	time1=time1%10;
	  	time1=time1*3+2-time1;
	  	return Long.toString((time+time1*1000000000)*9);
  	}
  	
  	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString().toUpperCase();
  	}
  	
  	/**
	 * http post请求
	 * @param map
	 * @param url
	 * @return
	 */
	public static String getWebData(HashMap<String, String> map, String url) {
		ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		Iterator<Entry<String, String>> it=map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, String> entry=it.next();
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		HttpPost post=new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));//设置post参数 并设置编码格式
			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			DefaultHttpClient client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
			HttpResponse resp=client.execute(post);
			if(resp.getStatusLine().getStatusCode()==200) {
				return EntityUtils.toString(resp.getEntity());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return "";
	}
	
	/**
	 * 极光推送通知获取
	 * @param str
	 * @return
	 */
	public static String getJPushResult(String str) {
		try {
			JSONObject obj=new JSONObject(str);
			return obj.getString(JPushReceiver.TuiSong_Flag);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取WebService返回数据
	 * @param method_name 方法名
	 * @param map 参数map
	 * @return
	 */
	public String getWebServiceInfo(HashMap<String, Object> map) {
		String result="";
		SoapObject soaprequest = new SoapObject("http://wsi.huateng.com", "");
		String text="";
		soaprequest.addProperty("in0", text);
		// 之后我们给定义发送数据的信封的封装格式
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		// 发出请求
		envelope.setOutputSoapObject(soaprequest);
		AndroidHttpTransport aht = new AndroidHttpTransport("http://132.228.190.147/Province/services/provice");
		try {
			aht.call(null, envelope);
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return "error";
		}
		// 接着就可以定义一个SoapObject类型的实例去获取我们返回来的数据
		if(!(envelope.bodyIn instanceof SoapFault)){
			SoapObject so = (SoapObject) envelope.bodyIn;
			result=so.getProperty("out").toString();
		}
		else {
			result="error";
		}
		System.out.println(result.toString());
		return result.toString();
	}
	
	public static String getMd5(String plainText) { 
		try { 
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(plainText.getBytes()); 
			byte b[] = md.digest(); 
	
			int i; 
	
			StringBuffer buf = new StringBuffer(""); 
			for (int offset = 0; offset < b.length; offset++) { 
				i = b[offset]; 
				if(i<0) i+= 256; 
				if(i<16) 
				buf.append("0"); 
				buf.append(Integer.toHexString(i)); 
			} 
			return buf.toString();
		} catch (NoSuchAlgorithmException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		}
		return "";
	}
	
	public static HashMap<String, String> getSingInfo(Context context) {
    	HashMap<String, String> map=null;
    	try {
    		PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
    				context.getPackageName(), PackageManager.GET_SIGNATURES);
    		Signature[] signs = packageInfo.signatures;
    		Signature sign = signs[0];
    		map=parseSignature(sign.toByteArray());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return map;
	}

	public static HashMap<String, String> parseSignature(byte[] signature) {
		HashMap<String, String> map=null;
    	try {
    		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    		X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
    		String pubKey = cert.getPublicKey().toString();
    		String signNumber = cert.getSerialNumber().toString();
    		map=new HashMap<String, String>();
    		map.put("pubKey", pubKey);
    		map.put("signNumber", signNumber);
    	} catch (CertificateException e) {
    		e.printStackTrace();
    	}
    	return map;
	}
	
	public static String getVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        String version="";
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return version;
    }
	
	/**
	 * 设置本地提示
	 */
	public static void setThreshold(Context context, boolean showTip) {
		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
		editor.putBoolean("thresholdtip", showTip);
		editor.commit();
	}
	
	public static boolean getThreshold(Context context) {
		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
		return sp.getBoolean("thresholdtip", false);
	}
	
	public static void setDisplay(Context context, float density) {
		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putFloat("density", density);
		editor.commit();
	}
	
	public static float getDisplay(Context context) {
		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
		return sp.getFloat("density", 1.5f);
	}
	
	/**
	 * 获取时间差值
	 * @param str
	 * @return
	 */
	public static String getExtraTime(String str) {
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long day=0;
		try {
			Date date=format.parse(str);
			day=date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			day=System.currentTimeMillis();
		}
		
		Date today=new Date();		
		int day_=0;
		int hour_=0;
		int minute_=0;
		int second_=0;
		day_=(int) ((today.getTime()-day)/(24*60*60*1000));
		hour_=(int)(today.getTime()-day)/(60*60*1000)-day_*24;
		minute_=(int) (today.getTime()-day)/(60*1000)-day_*24*60-hour_*60;
		second_=(int) (today.getTime()-day)/1000-day_*24*60*60-hour_*60*60-minute_*60;	
		
		if(day_>0) {
			return day_+"天前";
		}
		else if(hour_>0) {
			return hour_+"小时前";
		}
		else if(minute_>0) {
			return minute_+"分钟前";
		}
		else if(second_>0) {
			return second_+"秒前";
		}
		else {
			return "刚刚";
		}
	}
	
	public static boolean checkAPKState(Context context, String path) {
		PackageInfo pi=null;
		try {
			PackageManager pm=context.getPackageManager();
			pi=pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
			if(pi==null) {
				File file=new File(path);
				if(file.exists()) {
					file.delete();
				}				
			}
			return pi==null?false:true;
		} catch(Exception e) {
			if(pi==null) {
				File file=new File(path);
				if(file.exists()) {
					file.delete();
				}				
			}
			return false;
		}
	}
  	
	/**
	 * 加载全部联系人信息
	 */
	public static ArrayList<ContactModel> getContactData(Context context) {
		//联系人adapter数据
		ArrayList<ContactModel> model_list=new ArrayList<ContactModel>();
		HashMap<String, ArrayList<ContactModel>> contactsMap=Util.getPhoneContacts(context);
		Object[] obj1=contactsMap.keySet().toArray();
		//首字母排序
		Arrays.sort(obj1);
		for(int i=0;i<obj1.length;i++) {
			ArrayList<ContactModel> model_temp=contactsMap.get(obj1[i]);
			for(int j=0;j<model_temp.size();j++) {
				model_list.add(model_temp.get(j));
			}
		}
		((GasStationApplication) context.getApplicationContext()).setModel_list(model_list);
		return model_list;
	}
	
	/**
	 * 设置号头
	 * @param context
	 * @param header
	 */
	public static void setPhoneHeader(Context context, String header) {
		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putString("header", header);
		editor.commit();
	}
	
	/**
	 * 得到号头
	 * @param context
	 * @return
	 */
	public static String getPhoneHeader(Context context) {
		SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
		return sp.getString("header", "");
	}
	
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
   
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    /**
	 * 上传用户信息
	 */
	public static void shareInfo(final Context context) {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.obj!=null) {
					Map map=(Map) msg.obj;
					if(Integer.parseInt(map.get("result").toString())==1) {
						BaseActivity.showCustomToastWithContext(map.get("comments").toString(), context);
					}
					System.out.println("发出广播");
					Intent intent=new Intent("refreshMember");
	            	intent.setAction("refreshMember");
	            	context.sendBroadcast(intent);
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				LinkedList<String> wholeUrl=Util.getWholeUrl(context);
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) context.getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(context).get(0):((GasStationApplication) context.getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) context.getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(context);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(context).create(StrategyManager.class, ((GasStationApplication) context.getApplicationContext()).AreaUrl+"/hessian/strategyManager", context.getClassLoader());
						System.out.println(Long.parseLong(list.get(0))+" "+list.get(1)+" "+
								((GasStationApplication) context.getApplicationContext()).shareType+" "+
								((GasStationApplication) context.getApplicationContext()).activityId+" "+
								((GasStationApplication) context.getApplicationContext()).content);
						Map map=strategyManager.shareInfo(Long.parseLong(list.get(0)), list.get(1),
								((GasStationApplication) context.getApplicationContext()).shareType,
								((GasStationApplication) context.getApplicationContext()).activityId,
								((GasStationApplication) context.getApplicationContext()).content);
						flag=false;
						Message m=new Message();
						m.obj=map;
						handler.sendMessage(m);
						((GasStationApplication) context.getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
			        } catch(Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							//ip 端口等错误  java.net.SocketTimeoutException
							else {
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									flag=false;
								}
							}							
						}
						else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									flag=false;
								}
							}
						}
						else {
							wholeUrl.remove(currentUsedUrl);
							if(wholeUrl.size()>0) {
								currentUsedUrl=wholeUrl.get(0);
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								flag=false;
							}
						}
					}
				}			
				
			}}).start();
	}
	
	/**
     * 得到屏幕宽度
     * @return 单位:px
     */
    public static int getScreenWidth(Context context) {
        int screenWidth;
        WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth=dm.widthPixels;
        return screenWidth;
    }
     
    /**
     * 得到屏幕高度
     * @return 单位:px
     */
    public static int getScreenHeight(Context context) {
        int screenHeight;
        WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenHeight=dm.heightPixels;
        return screenHeight;
    }
    
    /**
     * 摇一摇时间设置
     * @param context
     */
    public static void setYiyTime(Context context) {
    	Calendar cal=Calendar.getInstance();
    	String time=cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
    	SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
    	SharedPreferences.Editor editor=sp.edit();
    	editor.putString("yiy", time);
    	editor.commit();
    }
    
    /**
     * 判断今天是否参加摇一摇活动
     * @param context
     * @return
     */
    public static boolean compYiyTime(Context context) {
    	//非无锡不判断
    	if(!Util.getUserInfo(context).get(1).equals("0510")) {
    		return true;
    	}
    	Calendar cal=Calendar.getInstance();
    	String time=cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
    	SharedPreferences sp=context.getSharedPreferences("gas", Activity.MODE_PRIVATE);
    	if(sp.getString("yiy", "").equals(time)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    /**
	 * 判断服务是否存在
	 * @param context
	 * @return
	 */
	public static boolean isServiceWorked(Context context, String serviceName) {  
		ActivityManager myManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(100);  
		for(int i = 0 ; i<runningService.size();i++) {  
			System.out.println(runningService.get(i).service.getClassName().toString());
			if(runningService.get(i).service.getClassName().toString().equals(serviceName)) {  
				return true;  
			}  
		}  
		return false;  
	}
}
