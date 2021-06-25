package com.stv.msgservice.ui.videoplayer.player;

public abstract class PlayerFactory<P extends AbstractPlayer> {
    public abstract P createPlayer();
}
