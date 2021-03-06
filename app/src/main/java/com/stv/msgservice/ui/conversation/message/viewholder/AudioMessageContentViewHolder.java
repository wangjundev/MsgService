package com.stv.msgservice.ui.conversation.message.viewholder;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.datamodel.constants.Config;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.ui.audio.AudioPlayManager;
import com.stv.msgservice.ui.audio.IAudioPlayListener;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.message.SoundMessageContent;
import com.stv.msgservice.utils.UIUtils;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

@MessageContentType(SoundMessageContent.class)
@EnableContextMenu
public class AudioMessageContentViewHolder extends MediaMessageContentViewHolder {
    @BindView(R2.id.audioImageView)
    ImageView ivAudio;
    @BindView(R2.id.durationTextView)
    TextView durationTextView;
    @BindView(R2.id.audioContentLayout)
    RelativeLayout contentLayout;
    @Nullable
    @BindView(R2.id.playStatusIndicator)
    View playStatusIndicator;
    private Fragment mFragment;
    private Message toPlayAudioMessage;
    private boolean isPlaying;
    AnimationDrawable animation;

    public AudioMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
    }

    public int getAudioDuration(String audioPath){
        int duration;
        try{
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(audioPath);
            duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))/1000;
        }catch (Exception e){
            duration = 0;
        }
        Log.i("Junwang", "getAudioDuration="+duration);
        return duration;
    }

    @Override
    protected void setSendStatus(Message item) {
        super.setSendStatus(item);
    }

    @Override
    public void onBind(Message message) {
//        super.onBind(message);
//        SoundMessageContent voiceMessage = (SoundMessageContent) message.message.content;
        int increment = UIUtils.getDisplayWidth(fragment.getContext()) / 3 / Config.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND * getAudioDuration(message.getAttachmentPath());
        Log.i("Junwang", "audio onBind increment="+increment);
//        durationTextView.setText(voiceMessage.getDuration() + "''");
        int duration = getAudioDuration(message.getAttachmentPath());
        if(duration <= 0){
            durationTextView.setVisibility(View.GONE);
        }else{
            durationTextView.setText(duration+"''");
        }
//        ViewGroup.LayoutParams params = contentLayout.getLayoutParams();
//        params.width = UIUtils.dip2Px(65) + increment;
//        contentLayout.setLayoutParams(params);
        if (message.getDirection() == MessageConstants.DIRECTION_IN) {
//            if (message.message.status != MessageStatus.Played) {
//                playStatusIndicator.setVisibility(View.VISIBLE);
//            } else {
//                playStatusIndicator.setVisibility(View.GONE);
//            }
            playStatusIndicator.setVisibility(View.VISIBLE);
        }
//        if (/*message.isPlaying*/false) {
//            animation = (AnimationDrawable) ivAudio.getBackground();
//            if (!animation.isRunning()) {
//                animation.start();
//            }
//        } else {
//            // TODO ?????????????????????????????????????????????????????????, ????????????????????????
//            ivAudio.setBackground(null);
//            if (message.getDirection() == MessageConstants.DIRECTION_OUT) {
//                ivAudio.setBackgroundResource(R.drawable.audio_animation_right_list);
//            } else {
//                ivAudio.setBackgroundResource(R.drawable.audio_animation_left_list);
//            }
//        }
        animation = (AnimationDrawable) ivAudio.getBackground();
        // ???????????????????????????
//        if (message.progress == 100) {
//            message.progress = 0;
//            itemView.post(() -> {
//                messageViewModel.playAudioMessage(message);
//            });
//        }
    }

    @Override
    public void onViewRecycled() {
        // TODO ??????????????????????????????????????????????????????
    }

    @OnClick(R2.id.audioContentLayout)
    public void onClick(View view) {
//        File file = messageViewModel.mediaMessageContentFile(message.message);
        Log.i("Junwang", "audio path = "+message.getAttachmentPath());
        File file = new File(message.getAttachmentPath());
        if (file == null) {
            return;
        }
        if (file.exists()) {
            playAudioMessage(message);
        } else {
//            if (message.isDownloading) {
//                return;
//            }
//            messageViewModel.downloadMedia(message, file);
        }
    }

    public void playAudioMessage(Message message) {
        if (toPlayAudioMessage != null && toPlayAudioMessage.equals(message)) {
            AudioPlayManager.getInstance().stopPlay();
            toPlayAudioMessage = null;
            return;
        }

        toPlayAudioMessage = message;
//        if (message.message.direction == MessageDirection.Receive && message.message.status != MessageStatus.Played) {
//            message.message.status = MessageStatus.Played;
//        }

//        File file = mediaMessageContentFile(message.message);
        File file = new File(message.getAttachmentPath());

        if (file == null) {
            return;
        }
        if (file.exists()) {
            playAudio(message, file);
        } else {
            Log.e("ConversationViewHolder", "audio not exist");
        }
    }

    private void playAudio(Message message, File file) {
        Uri uri = Uri.fromFile(file);
        AudioPlayManager.getInstance().startPlay(mFragment.getContext(), uri, new IAudioPlayListener() {
            @Override
            public void onStart(Uri var1) {
                if (uri.equals(var1)) {
                    Log.i("Junwang", "audio play animation start.");
//                    message.isPlaying = true;
                    isPlaying = true;
                    postMessageUpdate(message);
                }
            }

            @Override
            public void onStop(Uri var1) {
                if (uri.equals(var1)) {
                    Log.i("Junwang", "audio play animation stop.");
//                    message.isPlaying = false;
                    isPlaying = false;
                    toPlayAudioMessage = null;
//                    postMessageUpdate(message);
                    if(animation != null){
                        animation.stop();
                    }
                    if (message.getDirection() == MessageConstants.DIRECTION_OUT) {
                        ivAudio.setBackgroundResource(R.drawable.audio_animation_right_list);
                    } else {
                        ivAudio.setBackgroundResource(R.drawable.audio_animation_left_list);
                    }
                }
            }



            @Override
            public void onComplete(Uri var1) {
                if (uri.equals(var1)) {
                    Log.i("Junwang", "audio play animation complete.");
//                    message.isPlaying = false;
                    isPlaying = false;
                    toPlayAudioMessage = null;
                    animation = (AnimationDrawable) ivAudio.getBackground();
                    if(animation != null){
                        animation.stop();
                    }
                    if (message.getDirection() == MessageConstants.DIRECTION_OUT) {
                        ivAudio.setBackgroundResource(R.drawable.audio_animation_right_list);
                    } else {
                        ivAudio.setBackgroundResource(R.drawable.audio_animation_left_list);
                    }
//                    postMessageUpdate(message);
                }
            }
        });
    }

    private void postMessageUpdate(Message message) {
        AnimationDrawable animation = null;
        if (isPlaying) {
            animation = (AnimationDrawable) ivAudio.getBackground();
            if (!animation.isRunning()) {
                animation.start();
            }
        }else{
            if(animation != null){
                animation.stop();
            }
        }
//        if (message == null) {
//            return;
//        }
//        if (messageUpdateLiveData != null) {
//            UIUtils.postTaskSafely(() -> messageUpdateLiveData.setValue(message));
//        }
    }

}
