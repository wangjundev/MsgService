package com.stv.msgservice.ui.videoplayer.ijk;

import com.stv.msgservice.ui.videoplayer.player.PlayerFactory;

public class IjkPlayerFactory extends PlayerFactory<IjkPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkPlayer createPlayer() {
        return new IjkPlayer();
    }
}
