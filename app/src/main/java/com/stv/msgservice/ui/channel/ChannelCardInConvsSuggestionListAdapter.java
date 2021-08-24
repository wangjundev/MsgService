package com.stv.msgservice.ui.channel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionAction;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionActionWrapper;
import com.stv.msgservice.ui.channel.activity.ChannelWebViewActivity;
import com.stv.msgservice.utils.NativeFunctionUtil;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChannelCardInConvsSuggestionListAdapter  extends RecyclerView.Adapter<ChannelCardInConvsSuggestionListAdapter.SuggestionViewHolder>{
    private SuggestionActionWrapper[] suggestions;
    private Fragment fragment;
    private String chatbotPortrait;
    private String chatbotName;
    private String chatbotId;
    private int chatbotIsAttentioned;

    public void setSuggestions(Fragment fragment, SuggestionActionWrapper[] suggestions,
                               String chatbotPortrait, String chatbotName, String chatbotId, int chatbotIsAttentioned){
        this.fragment = fragment;
        this.suggestions = suggestions;
        this.chatbotPortrait = chatbotPortrait;
        this.chatbotName = chatbotName;
        this.chatbotId = chatbotId;
        this.chatbotIsAttentioned = chatbotIsAttentioned;
    }

    @NonNull
    @Override
    public ChannelCardInConvsSuggestionListAdapter.SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.channel_suggestion_item_view, parent, false);
        return new ChannelCardInConvsSuggestionListAdapter.SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelCardInConvsSuggestionListAdapter.SuggestionViewHolder holder, int position) {
        if (position < suggestions.length) {
            holder.bindSuggestion(suggestions[position]);
        }
    }

    @Override
    public int getItemCount() {
        if (suggestions == null || suggestions.length == 0) {
            return 0;
        }
        int count = suggestions.length;
        return count;
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.channel_suggestion_text)
        TextView channel_suggestion_text;
        SuggestionActionWrapper suggestion;

        private void doSuggestionAction(){
            SuggestionAction sa = suggestion.getAction();
            if ((suggestion != null) && (sa.urlAction != null)) {
//                WebViewNewsActivity.start(fragment.getContext(), sa.urlAction.openUrl.url);
                ChannelWebViewActivity.start(fragment.getContext(), sa.urlAction.openUrl.url,
                        chatbotPortrait, chatbotName,
                        chatbotId, chatbotIsAttentioned);
            }else if((sa != null) && (sa.dialerAction != null)){
                NativeFunctionUtil.callNativeFunction(MessageConstants.NativeActionType.PHONE_CALL, fragment.getContext(),
                        null, null, sa.dialerAction.dialPhoneNumber.phoneNumber);
            }else if((sa != null) && (sa.mapAction != null)) {
                NativeFunctionUtil.openLocation(sa.mapAction.showLocation.location.label, sa.mapAction.showLocation.location.latitude,
                        sa.mapAction.showLocation.location.longitude, fragment.getContext());
            }
        }

        @OnClick(R2.id.channel_suggestion_text)
        void onClick() {
            doSuggestionAction();
        }

        public SuggestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindSuggestion(SuggestionActionWrapper suggestion) {
            this.suggestion = suggestion;
            Log.i("Junwang", "bindSuggestion "+suggestion.getAction().displayText);
            channel_suggestion_text.setText(suggestion.getAction().displayText);
        }
    }
}
