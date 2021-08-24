package com.stv.msgservice.ui.channel.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lqr.emoji.EmotionLayout;
import com.lqr.emoji.IEmotionExtClickListener;
import com.lqr.emoji.IEmotionSelectedListener;
import com.lqr.emoji.LQREmotionKit;
import com.lqr.emoji.MoonUtils;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.ui.audio.AudioRecorderPanel;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.widget.InputAwareLayout;
import com.stv.msgservice.ui.widget.KeyboardHeightFrameLayout;
import com.stv.msgservice.ui.widget.ViewPagerFixed;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChannelConversationInputPanel extends FrameLayout implements IEmotionSelectedListener {

    @BindView(R2.id.inputContainerLinearLayout)
    LinearLayout inputContainerLinearLayout;
    @BindView(R2.id.disableInputTipTextView)
    TextView disableInputTipTextView;

    @BindView(R2.id.switchImageView)
    ImageView switchImageView;
    @BindView(R2.id.audioImageView)
    ImageView audioImageView;
    @BindView(R2.id.audioButton)
    Button audioButton;
    @BindView(R2.id.editText)
    EditText editText;
    @BindView(R2.id.emotionImageView)
    ImageView emotionImageView;
    @BindView(R2.id.extImageView)
    ImageView extImageView;
    @BindView(R2.id.sendButton)
    Button sendButton;

    @BindView(R2.id.emotionContainerFrameLayout)
    KeyboardHeightFrameLayout emotionContainerFrameLayout;
    @BindView(R2.id.emotionLayout)
    EmotionLayout emotionLayout;
    @BindView(R2.id.extContainerContainerLayout)
    KeyboardHeightFrameLayout extContainerFrameLayout;

    @BindView(R2.id.conversationExtViewPager)
    ViewPagerFixed extViewPager;

    @BindView(R2.id.refRelativeLayout)
    RelativeLayout refRelativeLayout;
    @BindView(R2.id.refEditText)
    EditText refEditText;

//    ConversationExtension extension;
    private long conversationId;
    private String chatbotId;
    private String sendAddress;
    private String destinationAddress;
    private String conversationUUID;
//    private MessageViewModel messageViewModel;
    private InputAwareLayout rootLinearLayout;
    private Fragment fragment;
    private FragmentActivity activity;
    private AudioRecorderPanel audioRecorderPanel;

    private long lastTypingTime;
    private String draftString;
    private static final int TYPING_INTERVAL_IN_SECOND = 10;
    private static final int MAX_EMOJI_PER_MESSAGE = 50;
    private int messageEmojiCount = 0;
    private SharedPreferences sharedPreferences;

    private ChannelConversationInputPanel.OnConversationInputPanelStateChangeListener onConversationInputPanelStateChangeListener;

    public ChannelConversationInputPanel(@NonNull Context context) {
        super(context);
    }

    public ChannelConversationInputPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public ChannelConversationInputPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChannelConversationInputPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public void setOnConversationInputPanelStateChangeListener(OnConversationInputPanelStateChangeListener onConversationInputPanelStateChangeListener) {
        this.onConversationInputPanelStateChangeListener = onConversationInputPanelStateChangeListener;
    }

    public void bind(FragmentActivity activity, InputAwareLayout rootInputAwareLayout) {

    }

    public void setupConversation(long conversationId, String chatbotId, String sendAddress, String destinationAddress, String conversationUUID) {
        this.conversationId = conversationId;
        this.chatbotId = chatbotId;
        this.sendAddress = sendAddress;
        this.destinationAddress = destinationAddress;
        this.conversationUUID = conversationUUID;
//        this.extension.bind(this.messageViewModel, conversation, chatbotId);
//        if(conversation != null){
//            draftString = conversation.getDraftSnippetText();
//            if(draftString != null){
//                setDraft();
//            }
//        }
    }

    public void disableInput(String tip) {
        closeConversationInputPanel();
        inputContainerLinearLayout.setVisibility(GONE);
        disableInputTipTextView.setVisibility(VISIBLE);
        disableInputTipTextView.setText(tip);
    }

    public void enableInput() {
        inputContainerLinearLayout.setVisibility(VISIBLE);
        disableInputTipTextView.setVisibility(GONE);
    }

//    public void onDestroy() {
//        this.extension.onDestroy();
//    }

    public void init(Fragment fragment, InputAwareLayout rootInputAwareLayout) {
        LayoutInflater.from(getContext()).inflate(R.layout.conversation_input_panel, this, true);
        ButterKnife.bind(this, this);

        this.activity = fragment.getActivity();
        this.fragment = fragment;
        this.rootLinearLayout = rootInputAwareLayout;

//        this.extension = new ConversationExtension(fragment, this, extViewPager);


        sharedPreferences = getContext().getSharedPreferences("sticker", Context.MODE_PRIVATE);

        // emotion
        emotionLayout.setEmotionAddVisiable(true);
        emotionLayout.setEmotionSettingVisiable(true);

        // audio record panel
        audioRecorderPanel = new AudioRecorderPanel(getContext());
        audioRecorderPanel.setRecordListener(new AudioRecorderPanel.OnRecordListener() {
            @Override
            public void onRecordSuccess(String audioFile, int duration) {
                Log.i("Junwang", "onRecordSuccess audioFile="+audioFile);
                //发送文件
                File file = new File(audioFile);
                if (file.exists()) {
                    if(conversationId != 0){
                        ((ConversationActivity)activity).saveMsg(activity, null, destinationAddress, sendAddress, conversationUUID, false, audioFile, null, MessageConstants.CONTENT_TYPE_AUDIO,"audio/mp3");
                    }else if(chatbotId != null){
                        //need to get local phone number
                        ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null, false, audioFile, null, MessageConstants.CONTENT_TYPE_AUDIO, "audio/mp3");
                    }
                }
            }

            @Override
            public void onRecordFail(String reason) {
                Log.i("Junwang", "onRecordFail");
                Toast.makeText(activity, reason, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecordStateChanged(AudioRecorderPanel.RecordState state) {
                if (state == AudioRecorderPanel.RecordState.START) {
                    Log.i("Junwang", "onRecordStateChanged");
                }
            }
        });

        // emotion
        emotionLayout.setEmotionSelectedListener(this);
        emotionLayout.setEmotionExtClickListener(new IEmotionExtClickListener() {
            @Override
            public void onEmotionAddClick(View view) {
                Toast.makeText(activity, "add", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEmotionSettingClick(View view) {
                Toast.makeText(activity, "setting", Toast.LENGTH_SHORT).show();
            }
        });

//        MessageViewModel.Factory factory = new MessageViewModel.Factory(
//                fragment.getActivity().getApplication(), 0);
//
//        messageViewModel = new ViewModelProvider(fragment, factory)
//                .get(MessageViewModel.class);

    }

    @OnClick(R2.id.extImageView)
    void onExtImageViewClick() {
        if (audioButton.getTag() != null) {
            return;
        }
        if (rootLinearLayout.getCurrentInput() == extContainerFrameLayout) {
            hideConversationExtension();
            rootLinearLayout.showSoftkey(editText);
        } else {
            emotionImageView.setImageResource(R.mipmap.ic_cheat_emo);
            showConversationExtension();
        }
    }

    @OnClick(R2.id.emotionImageView)
    void onEmotionImageViewClick() {

        if (audioRecorderPanel.isShowingRecorder()) {
            return;
        }
        if (rootLinearLayout.getCurrentInput() == emotionContainerFrameLayout) {
            hideEmotionLayout();
            rootLinearLayout.showSoftkey(editText);
        } else {
            hideAudioButton();
            showEmotionLayout();
        }
    }

    @OnClick(R2.id.audioImageView)
    public void showRecordPanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                fragment.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                return;
            }
        }

        if (audioButton.isShown()) {
            hideAudioButton();
            editText.requestFocus();
            rootLinearLayout.showSoftkey(editText);
        } else {
//            editText.clearFocus();
            showAudioButton();
            hideEmotionLayout();
            rootLinearLayout.hideSoftkey(editText, null);
            hideConversationExtension();
        }
    }

    @OnClick(R2.id.sendButton)
    void sendMessage() {
        messageEmojiCount = 0;
        Editable content = editText.getText();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if(conversationId != 0){
            ((ConversationActivity)(fragment.getActivity())).saveMsg(fragment.getContext(), content.toString().trim(), destinationAddress, sendAddress, conversationUUID, false, null, null, MessageConstants.CONTENT_TYPE_TEXT, null);
        }else if(chatbotId != null){
            ((ConversationActivity)(fragment.getActivity())).saveMsg(fragment.getContext(), content.toString().trim(), null, chatbotId, null,false, null, null, MessageConstants.CONTENT_TYPE_TEXT, null);
        }

        ((ConversationFragment)fragment).getConversationInputPanel().closeConversationInputPanel();
        editText.setText("");
    }

    public void onKeyboardShown() {
        hideEmotionLayout();
    }

    public void onKeyboardHidden() {
        // do nothing
    }

    public void onDestroy() {
//        this.extension.onDestroy();
    }

    public void onActivityPause() {
//        updateConversationDraft();
    }

    private void setDraft() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(draftString);
        editText.setText(spannableStringBuilder);
//         FIXME: 4/16/21 恢复草稿时，消息列表界面会抖动，且没有滑动到最后
//        editText.requestFocus();
    }

    public void setInputText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        editText.setText(text);
        editText.setSelection(text.length());
        editText.requestFocus();
        rootLinearLayout.showSoftkey(editText);
    }

    private void showAudioButton() {
        audioButton.setVisibility(View.VISIBLE);
        audioRecorderPanel.attach(rootLinearLayout, audioButton);
        editText.setVisibility(View.GONE);
        extImageView.setVisibility(VISIBLE);
        sendButton.setVisibility(View.GONE);
        audioImageView.setImageResource(R.mipmap.ic_cheat_keyboard);
        rootLinearLayout.hideCurrentInput(editText);
        rootLinearLayout.hideAttachedInput(true);
    }


    private void hideAudioButton() {
        audioButton.setVisibility(View.GONE);
        audioRecorderPanel.deattch();
        editText.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(editText.getText())) {
            extImageView.setVisibility(VISIBLE);
            sendButton.setVisibility(View.GONE);
        } else {
            extImageView.setVisibility(GONE);
            sendButton.setVisibility(View.VISIBLE);
        }
        audioImageView.setImageResource(R.mipmap.ic_cheat_voice);
    }

    private void showEmotionLayout() {
        audioButton.setVisibility(View.GONE);
        emotionImageView.setImageResource(R.mipmap.ic_cheat_keyboard);
        rootLinearLayout.show(editText, emotionContainerFrameLayout);
        if (onConversationInputPanelStateChangeListener != null) {
            onConversationInputPanelStateChangeListener.onInputPanelExpanded();
        }
    }

    private void hideEmotionLayout() {
        emotionImageView.setImageResource(R.mipmap.ic_cheat_emo);
        if (onConversationInputPanelStateChangeListener != null) {
            onConversationInputPanelStateChangeListener.onInputPanelCollapsed();
        }
    }

    private void showConversationExtension() {
        rootLinearLayout.show(editText, extContainerFrameLayout);
        if (audioButton.isShown()) {
            hideAudioButton();
        }
        if (onConversationInputPanelStateChangeListener != null) {
            onConversationInputPanelStateChangeListener.onInputPanelExpanded();
        }
    }

    private void hideConversationExtension() {
        if (onConversationInputPanelStateChangeListener != null) {
            onConversationInputPanelStateChangeListener.onInputPanelCollapsed();
        }
    }

    public void closeConversationInputPanel() {
//        extension.reset();
        emotionImageView.setImageResource(R.mipmap.ic_cheat_emo);
        rootLinearLayout.hideAttachedInput(true);
        rootLinearLayout.hideCurrentInput(editText);
    }

    @Override
    public void onEmojiSelected(String key) {
        Editable editable = editText.getText();
        if (key.equals("/DEL")) {
            messageEmojiCount--;
            messageEmojiCount = messageEmojiCount < 0 ? 0 : messageEmojiCount;
            editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            if (messageEmojiCount >= MAX_EMOJI_PER_MESSAGE) {
                Toast.makeText(activity, "最多允许输入" + MAX_EMOJI_PER_MESSAGE + "个表情符号", Toast.LENGTH_SHORT).show();
                return;
            }
            messageEmojiCount++;
            int code = Integer.decode(key);
            char[] chars = Character.toChars(code);
            String value = Character.toString(chars[0]);
            for (int i = 1; i < chars.length; i++) {
                value += Character.toString(chars[i]);
            }

            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            editable.replace(start, end, value);

            int editEnd = editText.getSelectionEnd();
            MoonUtils.replaceEmoticons(LQREmotionKit.getContext(), editable, 0, editable.toString().length());
            editText.setSelection(editEnd);
        }
    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {
//        String remoteUrl = sharedPreferences.getString(stickerBitmapPath, null);
//        messageViewModel.sendStickerMsg(conversation, stickerBitmapPath, remoteUrl);
    }


    public interface OnConversationInputPanelStateChangeListener {
        /**
         * 输入面板展开
         */
        void onInputPanelExpanded();

        /**
         * 输入面板关闭
         */
        void onInputPanelCollapsed();
    }
}
