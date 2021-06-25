package com.stv.msgservice.ui.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.stv.msgservice.R;
import com.stv.msgservice.ui.videoplayer.ijk.IjkPlayer;
import com.stv.msgservice.ui.videoplayer.listener.OnVideoViewStateChangeListener;
import com.stv.msgservice.ui.videoplayer.player.DanmuVideoView;
import com.stv.msgservice.ui.videoplayer.player.PlayerFactory;
import com.stv.msgservice.ui.videoplayer.player.SantiVideoView;
import com.stv.msgservice.ui.videoplayer.ui.StandardVideoController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayVideoActivity extends AppCompatActivity{
    public static final String URL = "url";
    public static final String TITLE= "title";
    public static final String DETAILS = "details";
    private ImageView mIVBack;
    private TextView mTVTitle;
    private String mUrl;
    private String mTitle;
    SantiVideoView mVideoView;
    private TextView mTVDetails;
    private String mDetails;
    private ImageView mCloseImage;
    private ImageView mCoverImage;
    private ImageView mShareVideo;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    public static void start(Context context, String url, String title, String details) {
        Intent intent = new Intent(context, PlayVideoActivity.class);
        if(url != null) {
            intent.putExtra(URL, url);
        }
        if(title != null) {
            intent.putExtra(TITLE, title);
        }
        if(details !=null){
            intent.putExtra(DETAILS, details);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        mUrl = getIntent().getStringExtra(URL);
        mTitle = getIntent().getStringExtra(TITLE);
        mDetails = getIntent().getStringExtra(DETAILS);
        initView();
//        ImmersionBar.with(this)
//                .titleBar(R.id.toolbar, false)
//                .transparentBar()
//                .init();
    }

    protected void initView(){
//        StatusBarUtil.setStatusBarColor(this, /*R.color.color_BDBDBD*/Color.parseColor("#FFFFFF"));
        mTVTitle = (TextView)findViewById(R.id.video_title);
        mTVDetails = (TextView)findViewById(R.id.video_details);
        mTVDetails.setMovementMethod(ScrollingMovementMethod.getInstance());
        if(mTitle != null) {
            mTVTitle.setText(mTitle);
        }
        if(mDetails != null){
            mTVDetails.setText(mDetails);
        }
        mCloseImage = (ImageView)findViewById(R.id.close_img);
        mCloseImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mShareVideo = (ImageView)findViewById(R.id.share_video);
        mShareVideo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
                shareIntent.setType("text/plain");
                final CharSequence title = getResources().getText(R.string.action_share);
                startActivity(Intent.createChooser(shareIntent, title));
            }
        });
        initVideoView();
    }

    private void initVideoView(){
        mVideoView = (SantiVideoView)findViewById(R.id.vv_video);
        StandardVideoController standardVideoController = new StandardVideoController(this);
        if(mTitle != null) {
            standardVideoController.setTitle(mTitle);
        }
        mVideoView.setVideoController(standardVideoController);
        mVideoView.setUrl(/*"rtmp://58.200.131.2:1935/livetv/hunantv"*/mUrl);

        mVideoView.setPlayerFactory(new PlayerFactory<IjkPlayer>() {
            @Override
            public IjkPlayer createPlayer() {
                return new IjkPlayer() {
                    @Override
                    public void setOptions() {
                        //精准seek
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
//                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
//                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
//                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
//                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024 * 1024 * 20); //20M 缓存
//                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 30L); //30帧每秒
//                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 10);
//                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1L);//设置播放前的探测时间 1,达到首屏秒开效果
                    }
                };
            }
        });
        //播放器配置，注意：此为全局配置，按需开启
//        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
//                .setLogEnabled(false)
//                .setPlayerFactory(IjkPlayerFactory.create())
//                .setPlayerFactory(IjkPlayerFactory.create())
//                .setEnableOrientation(true)
//                .setEnableMediaCodec(true)
//                .setUsingSurfaceView(true)
//                .setEnableParallelPlay(true)
//                .setEnableAudioFocus(false)
//                .setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
//                .build());
        mVideoView.start();

        mVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == DanmuVideoView.STATE_PREPARED) {
                    if(!mVideoView.isReplay()) {
                    }
                } else if (playState == DanmuVideoView.STATE_PLAYBACK_COMPLETED) {
                    mHandler.removeCallbacksAndMessages(null);
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        LogUtil.i("Junwang", "DanmuVideoPlayActivity onDestroy()");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    public void onBackPressed() {
        if (mVideoView == null || !mVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
