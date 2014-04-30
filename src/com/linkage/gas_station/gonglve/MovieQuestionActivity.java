package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.oil_treasure.TreasureMainActivity;
import com.linkage.gas_station.qqapi.QQActivity;
import com.linkage.gas_station.sinaweiboapi.WBMainActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;
import com.linkage.gas_station.wxapi.WXEntryActivity;
import com.linkage.gas_station.yxapi.YX_SendActivity;

public class MovieQuestionActivity extends BaseActivity implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

	String[] answers={"A", "B", "C", "D", "E", "F", "G", "H"};
	private static final String TAG = "MediaPlayerDemo";
	private int mVideoWidth;
	private int mVideoHeight;
	private MediaPlayer mMediaPlayer;
	private SurfaceView mPreview;
	private SurfaceHolder holder;
	private String path="";
	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;
	
	ImageView title_refresh=null;
	TextView title_name=null;
	ImageView title_back=null;
	ProgressBar title_refresh_progress=null;
	
	LinearLayout movie_question_start_layout=null;
	TextView movie_question_desp=null;
	ImageView movie_question_start=null;
	RelativeLayout movie_question_award_layout=null;
	TextView surface_title=null;
	TextView movie_question_answer_que=null;
	TextView movie_question_answer_next=null;
	LinearLayout movie_question_answer_layout=null;
	
	TextView movie_question_award_title=null;
	TextView movie_question_award_right_num=null;
	TextView movie_question_award_coin=null;
	TextView movie_question_award_coin_watch=null;
	ImageView yixin_pengyou_share=null;
	ImageView weixin_pengyou_share=null;
	ImageView qqkj_logo_share=null;
	ImageView sinaweibo_logo_share=null;
	
	RelativeLayout surface_conver=null;
	ArrayList<View> view_question_answer_list=null;
	ImageView media_video_start=null;
	private SeekBar mediacontroller_seekbar=null;
	long duration=0;
	TextView mediacontroller_time_total=null;
	TextView mediacontroller_time_current=null;
	ImageView mediacontroller_play_pause=null;
	boolean isPause=false;
	
	int answer=-1;
	long questionId=-1;
	//判断是否正在加载
	boolean isLoading=false;

	/**
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.activity_moviequestion);
		
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("一猜到底");
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setVisibility(View.GONE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isLoading) {
					getUserGuessInfo();
				}
				else {
					showCustomToast("活动正在加载中，请稍后");
				}
			}});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);
		
		movie_question_start_layout=(LinearLayout) findViewById(R.id.movie_question_start_layout);
		movie_question_start=(ImageView) findViewById(R.id.movie_question_start);
		movie_question_start.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getUserGuessInfo();
			}});
		movie_question_desp=(TextView) findViewById(R.id.movie_question_desp);
		movie_question_desp.setText(getIntent().getExtras().getString("desp"));
		movie_question_award_layout=(RelativeLayout) findViewById(R.id.movie_question_award_layout);
		surface_title=(TextView) findViewById(R.id.surface_title);		
		movie_question_answer_que=(TextView) findViewById(R.id.movie_question_answer_que);
		movie_question_answer_next=(TextView) findViewById(R.id.movie_question_answer_next);
		movie_question_answer_next.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(answer==-1) {
					showCustomToast("请您选择一个答案");
				}
				else if(answer>=view_question_answer_list.size()) {
					showCustomToast("获取一猜到底相关信息出错，请刷新再试");
				}
				else {
					if(questionId==-1) {
						showCustomToast("获取一猜到底相关信息出错，请刷新再试");
					}
					else {
						guessQuestion(questionId, answers[answer]);
					}
				}
			}});
		view_question_answer_list=new ArrayList<View>();
		movie_question_answer_layout=(LinearLayout) findViewById(R.id.movie_question_answer_layout);
		surface_conver=(RelativeLayout) findViewById(R.id.surface_conver);
		
		mPreview = (SurfaceView) findViewById(R.id.surface);
		holder = mPreview.getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.RGBA_8888); 

		media_video_start=(ImageView) findViewById(R.id.media_video_start);
		media_video_start.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				media_video_start.setVisibility(View.GONE);
				surface_conver.setVisibility(View.VISIBLE);
				if(mMediaPlayer!=null) {
					mMediaPlayer.stop();
				}
				releaseMediaPlayer();
				doCleanUp();
				playVideo();
			}});
		mediacontroller_play_pause=(ImageView) findViewById(R.id.mediacontroller_play_pause);
		mediacontroller_play_pause.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isPause) {
					mediacontroller_play_pause.setImageResource(R.drawable.mediacontroller_pause);
					mMediaPlayer.start();
					isPause=false;
				}
				else {
					mediacontroller_play_pause.setImageResource(R.drawable.mediacontroller_play);
					mMediaPlayer.pause();
					isPause=true;
				}
				
			}});
		mediacontroller_time_current=(TextView) findViewById(R.id.mediacontroller_time_current);
		mediacontroller_time_total=(TextView) findViewById(R.id.mediacontroller_time_total);
		mediacontroller_seekbar=(SeekBar) findViewById(R.id.mediacontroller_seekbar);
		mediacontroller_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				handler_voice_bar.sendEmptyMessageDelayed(1, 1000);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				handler_voice_bar.removeMessages(1);
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if(fromUser) {
					mMediaPlayer.seekTo((int) ((float) progress/1000*duration));
				}
			}
		});
		
		movie_question_award_title=(TextView) findViewById(R.id.movie_question_award_title);
		movie_question_award_right_num=(TextView) findViewById(R.id.movie_question_award_right_num);
		movie_question_award_coin=(TextView) findViewById(R.id.movie_question_award_coin);
		movie_question_award_coin_watch=(TextView) findViewById(R.id.movie_question_award_coin_watch);
		movie_question_award_coin_watch.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent=new Intent(MovieQuestionActivity.this, TreasureMainActivity.class);
				startActivity(intent);
			}});
		yixin_pengyou_share=(ImageView) findViewById(R.id.yixin_pengyou_share);
		yixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MovieQuestionActivity.this, YX_SendActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", "一猜到底(无锡电信用户专享)");
				bundle.putString("url", "http://202.102.116.115:7001/lljyz/index.html");
				bundle.putString("text", movie_question_award_right_num.getText()+","+movie_question_award_coin.getText());
				bundle.putBoolean("isFriend", true);
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		weixin_pengyou_share=(ImageView) findViewById(R.id.weixin_pengyou_share);
		weixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MovieQuestionActivity.this, WXEntryActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", "一猜到底(无锡电信用户专享)"+"\n"+movie_question_award_right_num.getText()+","+movie_question_award_coin.getText());
				bundle.putString("url", "http://202.102.116.115:7001/lljyz/index.html");
				bundle.putString("text", movie_question_award_right_num.getText()+","+movie_question_award_coin.getText());
				bundle.putBoolean("isFriend", true);
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		qqkj_logo_share=(ImageView) findViewById(R.id.qqkj_logo_share);
		qqkj_logo_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MovieQuestionActivity.this, QQActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", "一猜到底(无锡电信用户专享)");
				bundle.putString("url", "http://202.102.116.115:7001/lljyz/index.html");
				bundle.putString("text", movie_question_award_right_num.getText()+","+movie_question_award_coin.getText());
				bundle.putString("send_imageUrl", "http://a2.mzstatic.com/us/r30/Purple6/v4/98/a8/48/98a84887-be7a-9402-24ce-59284e6bf0f8/mzl.rwwplqzr.175x175-75.jpg");
				bundle.putString("type", "qqkj");
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		sinaweibo_logo_share=(ImageView) findViewById(R.id.sinaweibo_logo_share);
		sinaweibo_logo_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MovieQuestionActivity.this, WBMainActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", "一猜到底(无锡电信用户专享)");
				bundle.putString("url", "http://202.102.116.115:7001/lljyz/index.html");
				bundle.putString("text", movie_question_award_right_num.getText()+","+movie_question_award_coin.getText());
				bundle.putString("defaultText", "流量加油站");
				intent.putExtras(bundle);
				startActivity(intent);
			}});
	}

	private void playVideo() {
		doCleanUp();
		try {
			// Create a new media player and set the listeners
			mMediaPlayer=new MediaPlayer(this);
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.setDisplay(holder);
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnVideoSizeChangedListener(this);
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
		}
	}

	public void onBufferingUpdate(MediaPlayer arg0, int percent) {
		// Log.d(TAG, "onBufferingUpdate percent:" + percent);

	}

	public void onCompletion(MediaPlayer arg0) {
		Log.d(TAG, "onCompletion called");
		mMediaPlayer.seekTo(0);
		mMediaPlayer.pause();
		mediacontroller_play_pause.setImageResource(R.drawable.mediacontroller_play);
		isPause=true;
		
		media_video_start.setVisibility(View.VISIBLE);
		movie_question_answer_que.setVisibility(View.VISIBLE);
		movie_question_answer_layout.setVisibility(View.VISIBLE);
		movie_question_answer_next.setVisibility(View.VISIBLE);
	}

	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		Log.v(TAG, "onVideoSizeChanged called");
		surface_conver.setVisibility(View.INVISIBLE);
		media_video_start.setVisibility(View.GONE);
		if (width == 0 || height == 0) {
			Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
			return;
		}
		mIsVideoSizeKnown = true;
		mVideoWidth = width;
		mVideoHeight = height;
	}

	public void onPrepared(MediaPlayer mediaplayer) {
		duration=mMediaPlayer.getDuration();
		mediacontroller_time_total.setText(""+toTime((int) duration));
		handler_voice_bar.sendEmptyMessage(1);
		mMediaPlayer.getMetadata();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mIsVideoReadyToBePlayed=true;
		if(mIsVideoReadyToBePlayed&&mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}
	
	Handler handler_voice_bar=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1) {
				if(mMediaPlayer!=null) {
					mediacontroller_seekbar.setProgress((int) (((float) mMediaPlayer.getCurrentPosition()/duration)*1000));
					mediacontroller_time_current.setText(""+toTime((int) mMediaPlayer.getCurrentPosition()));
				}
				handler_voice_bar.sendEmptyMessageDelayed(1, 1000);
			}
		}
	};

	public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
		Log.d(TAG, "surfaceChanged called");

	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		Log.d(TAG, "surfaceDestroyed called");
		releaseMediaPlayer();
		doCleanUp();
		mediacontroller_play_pause.setImageResource(R.drawable.mediacontroller_pause);
		mediacontroller_time_current.setText("00:00");
		mediacontroller_seekbar.setProgress(0);
		isPause=false;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated called");
		//playVideo();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(!path.equals("")&&!isLoading) {
			media_video_start.setVisibility(View.VISIBLE);
		}		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseMediaPlayer();
		doCleanUp();
	}

	private void releaseMediaPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if(handler_voice_bar!=null) {
			handler_voice_bar.removeMessages(1);
		}
	}

	private void doCleanUp() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		mIsVideoReadyToBePlayed = false;
		mIsVideoSizeKnown = false;
	}

	private void startVideoPlayback() {
		Log.v(TAG, "startVideoPlayback");
		mediacontroller_play_pause.setImageResource(R.drawable.mediacontroller_pause);
		holder.setFixedSize(mVideoWidth, mVideoHeight);
		mMediaPlayer.start();
	}
	
	public static String toTime(int time) {
		time/=1000;
		int minute=time/60;
		int second=time%60;
		minute%=60;
		return String.format("%02d:%02d", minute, second);
	}
	
	/**
	 * 判断网络条件以控制
	 * @return
	 */
	private boolean isPermit() {
		ConnectivityManager cManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo networkInfo=cManager.getActiveNetworkInfo();  
        if (null==networkInfo) {  
        	return false;
        }
        else {
        	switch (networkInfo.getType()) {  
	            case ConnectivityManager.TYPE_WIFI:  
	            	return false;
	            case ConnectivityManager.TYPE_MOBILE:
	            	return true;
	        	default:
	        		return false;
        	}
        }
	}
	
	private void getUserGuessInfo() {
		movie_question_answer_layout.removeAllViews();
		movie_question_answer_layout.setVisibility(View.INVISIBLE);
		movie_question_answer_que.setVisibility(View.INVISIBLE);
		movie_question_answer_next.setVisibility(View.INVISIBLE);
		surface_title.setVisibility(View.INVISIBLE);
		media_video_start.setVisibility(View.GONE);
		
		isLoading=true;
		showProgressDialog(R.string.tishi_loading);
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		if(mMediaPlayer!=null) {
			mMediaPlayer.stop();
		}
		releaseMediaPlayer();
		doCleanUp();
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
				movie_question_start_layout.setVisibility(View.GONE);
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					switch(Integer.parseInt(map.get("result").toString())) {
					case -1:
						title_refresh.setVisibility(View.INVISIBLE);
						title_refresh_progress.setVisibility(View.INVISIBLE);
						movie_question_start_layout.setVisibility(View.VISIBLE);
						break;
					case 0:
						surface_title.setText("第"+(Integer.parseInt(map.get("guess_total").toString())+1)+"关");
						Map map_question=(Map) map.get("question");
						path=map_question.get("video_url").toString();
						String num_str=map_question.get("question_options").toString();
						int num=num_str.split("#").length;
						for(int i=0;i<num;i++) {
							final int pos=i;
							View view=LayoutInflater.from(MovieQuestionActivity.this).inflate(R.layout.view_movie_question_radiobutton, null);
							final ImageView movie_question_image=(ImageView) view.findViewById(R.id.movie_question_image);
							view.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									for(int j=0;j<view_question_answer_list.size();j++) {
										((ImageView) (view_question_answer_list.get(j).findViewById(R.id.movie_question_image))).setImageResource(R.drawable.movie_question_nor);
									}
									movie_question_image.setImageResource(R.drawable.movie_question_right);
									answer=pos;
								}});
							final TextView movie_question_title=(TextView) view.findViewById(R.id.movie_question_title);
							movie_question_title.setText(num_str.split("#")[i]);
							movie_question_answer_layout.addView(view);
							view_question_answer_list.add(view);
						}
						movie_question_answer_que.setText(map_question.get("question_content").toString());
						questionId=Long.parseLong(map_question.get("question_id").toString());
						
						surface_title.setVisibility(View.VISIBLE);
						surface_conver.setVisibility(View.VISIBLE);
						
						playVideo();
						break;
					case 1:
						movie_question_award_layout.setVisibility(View.VISIBLE);
						movie_question_award_title.setText(map.get("comments").toString());
						movie_question_award_right_num.setText("您答对了"+map.get("guess_right").toString()+"道题目");
						movie_question_award_coin.setText("您赢得了"+map.get("guess_coin").toString()+"枚金币");
						title_refresh.setVisibility(View.GONE);
						break;
					}
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==-3) {
					showCustomToast("请您在3G网络下参与本活动");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				isLoading=false;
				
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				if(!isPermit()) {
					m.what=-3;
					handler.sendMessage(m);
					return;
				}
				LinkedList<String> wholeUrl=Util.getWholeUrl(MovieQuestionActivity.this);				
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MovieQuestionActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MovieQuestionActivity.this);						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MovieQuestionActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map result=strategyManager.getUserGuessInfo(Long.parseLong(list.get(0)), list.get(1));
						m.what=1;
						m.obj=result;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-2;
			        } catch (Exception e) {
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
						m.what=-1;
					}
				}
				handler.sendMessage(m);				
			}
		}).start();
	}
	
	private void guessQuestion(final Long questionId, final String userAnswer) {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					new AlertDialog.Builder(MovieQuestionActivity.this).setTitle("答案信息").setMessage(map.get("comments").toString()).setNegativeButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub							
							getUserGuessInfo();
						}
					}).show();
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(MovieQuestionActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MovieQuestionActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MovieQuestionActivity.this);						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MovieQuestionActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map result=strategyManager.guessQuestion(Long.parseLong(list.get(0)), list.get(1), questionId, userAnswer);
						m.what=1;
						m.obj=result;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-2;
			        } catch (Exception e) {
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
						m.what=-1;
					}
				}
				handler.sendMessage(m);				
			}
		}).start();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!path.equals("")) {
			surface_conver.setVisibility(View.INVISIBLE);
			media_video_start.setVisibility(View.VISIBLE);
		}
	}
	
}