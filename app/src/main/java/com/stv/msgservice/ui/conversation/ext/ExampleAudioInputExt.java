package com.stv.msgservice.ui.conversation.ext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.stv.msgservice.R;
import com.stv.msgservice.annotation.ExtContextMenuItem;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.ui.conversation.ext.core.ConversationExt;

//用来演示类似微信语音输入那种扩展
public class ExampleAudioInputExt extends ConversationExt {

    /**
     * @param containerView 扩展view的container
     * @param conversation
     */
    @ExtContextMenuItem
    public void image(View containerView, Conversation conversation) {
        FrameLayout frameLayout = (FrameLayout) containerView;
        View view = LayoutInflater.from(activity).inflate(R.layout.conversatioin_ext_example_layout, frameLayout, false);
        frameLayout.addView(view);
        extension.disableHideOnScroll();

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extension.reset();
            }
        });
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public int iconResId() {
        return R.mipmap.ic_func_voice;
    }

    @Override
    public String title(Context context) {
        return "Example";
    }

    @Override
    public String contextMenuTitle(Context context, String tag) {
        return title(context);
    }
}