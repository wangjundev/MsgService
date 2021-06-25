package com.stv.msgservice.ui.conversationlist.viewholder;

import android.util.Log;
import android.view.View;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.stv.msgservice.MainActivity;
import com.stv.msgservice.R;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.GlideApp;
import com.stv.msgservice.ui.GlideCircleWithBorder;
import com.stv.msgservice.ui.conversation.ChatbotIntroduceActivity;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

//import com.stv.msgservice.annotation.ConversationInfoType;

//@ConversationInfoType(type = Conversation.ConversationType.Single, line = 0)
@EnableContextMenu
public class SingleConversationViewHolder extends ConversationViewHolder {
    private Fragment mFragment;
    public SingleConversationViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
    }

    @Override
    public void sendTextMsg(String destination, String text) {
        ((MainActivity)(mFragment.getActivity())).saveMsg(fragment.getContext(), text, destination, false, null, null, MessageConstants.CONTENT_TYPE_TEXT);
    }

    @Override
    protected void onBindConversationInfo(Conversation conversationInfo) {
        UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                fragment.getActivity().getApplication(), /*conversationInfo.getNormalizedDestination()*/conversationInfo.getSenderAddress());

        final UserInfoViewModel userInfoViewModel = new ViewModelProvider(fragment, factory)
                .get(UserInfoViewModel.class);
        Log.i("Junwang", "senderaddress="+conversationInfo.getSenderAddress());
        userInfoViewModel.getUserInfo(/*conversationInfo.getNormalizedDestination()*/conversationInfo.getSenderAddress()).observe(fragment, userInfo -> {
            if(userInfo != null){
                String name = userInfo.getName();
                String portrait = userInfo.getPortrait();
                GlideApp.with(fragment)
                        .load(portrait)
                        .placeholder(R.mipmap.avatar_def)
                        .transform(new CenterCrop(),new GlideCircleWithBorder())
//                        .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dip2Px(fragment.getContext(), 4)))
                        .into(portraitImageView);
                nameTextView.setText(name);
                portraitImageView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        ChatbotIntroduceActivity.start(mFragment.getContext(), userInfo.getUri(), null);
                    }
                });
            }
        });
    }

}
