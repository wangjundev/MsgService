package com.stv.msgservice.ui.videoplayer.player;

//import tv.danmaku.ijk.media.player.AndroidMediaPlayer;

public class AndroidMediaPlayerFactory extends com.stv.msgservice.ui.videoplayer.player.PlayerFactory<AndroidMediaPlayer> {

    public static AndroidMediaPlayerFactory create() {
        return new AndroidMediaPlayerFactory();
    }

    @Override
    public AndroidMediaPlayer createPlayer() {
        return new AndroidMediaPlayer();
    }
}
