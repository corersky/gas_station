package com.linkage.gas_station.gonglve;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.R;

public class MovieQuestionActivity extends BaseActivity implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

	private static final String TAG = "MediaPlayerDemo";
	private int mVideoWidth;
	private int mVideoHeight;
	private MediaPlayer mMediaPlayer;
	private SurfaceView mPreview;
	private SurfaceHolder holder;
	private String path;
	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;
	
	ImageView ic_launcher=null;
	private SeekBar mediacontroller_seekbar=null;
	long duration=0;
	TextView mediacontroller_time_total=null;
	TextView mediacontroller_time_current=null;
	ImageView mediacontroller_play_pause=null;
	boolean isPause=false;

	/**
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.activity_moviequestion);
		mPreview = (SurfaceView) findViewById(R.id.surface);
		holder = mPreview.getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.RGBA_8888); 

		ic_launcher=(ImageView) findViewById(R.id.ic_launcher);
		ic_launcher.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
	}

	private void playVideo() {
		doCleanUp();
		try {
			path = "http://morningtel.qiniudn.com/%E8%B6%85%E7%BA%A7%E7%BB%8F%E7%BA%AA%E4%BA%BA-%E7%89%87%E8%8A%B1.mp4";
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
	}

	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		Log.v(TAG, "onVideoSizeChanged called");
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

					System.out.println(mMediaPlayer.getCurrentPosition()+" "+duration);
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
		playVideo();
	}

	@Override
	protected void onPause() {
		System.out.println("onPause");
		super.onPause();
		
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
		System.out.println(mVideoWidth+" "+mVideoHeight );
		mMediaPlayer.start();
	}
	
	public static String toTime(int time) {
		time/=1000;
		int minute=time/60;
		int second=time%60;
		minute%=60;
		return String.format("%02d:%02d", minute, second);
	}
	
}