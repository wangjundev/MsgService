package com.stv.msgservice.ui.channel.viewholder;

import android.text.Html;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lqr.emoji.MoonUtils;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.database.entity.MessageUserInfoEntity;
import com.stv.msgservice.ui.WfcWebViewActivity;
import com.stv.msgservice.ui.widget.LinkClickListener;
import com.stv.msgservice.ui.widget.LinkTextViewMovementMethod;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ChannelTextMessageContentViewHolder  extends ChannelMsgItemViewHolder {
    TextView channelContentTextView;
    Fragment mFragment;

    public ChannelTextMessageContentViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView, View viewStubInflator) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
        channelContentTextView = viewStubInflator.findViewById(R.id.channelContentTextView);
    }

    @Override
    public void onBind(MessageUserInfoEntity message) {
        String content = "央视网消息：今天（12日）上午，郑州市召开疫情防控新闻发布会，介绍新增确诊病例和疫情防控情况。郑州市政府副秘书长李慧芳在发布会上通报，为切实堵塞防疫漏洞，即日起郑州…";//message.getContent();
        if(channelContentTextView != null){
            Log.i("Junwang", "ChannelTextMessageContentViewHolder content view onBind content="+content);
            if (content.startsWith("<") && content.endsWith(">")) {
                channelContentTextView.setText(Html.fromHtml(content));
            } else {
                MoonUtils.identifyFaceExpression(fragment.getContext(), channelContentTextView, content, ImageSpan.ALIGN_BOTTOM);
            }
            channelContentTextView.setMovementMethod(new LinkTextViewMovementMethod(new LinkClickListener() {
                @Override
                public boolean onLinkClick(String link) {
                    WfcWebViewActivity.loadUrl(fragment.getContext(), "", link);
                    return true;
                }
            }));
        }

    }
}