package com.stv.msgservice.ui.conversation.message.search;

import android.view.View;
import android.widget.TextView;

import com.stv.msgservice.R2;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpandViewHolder extends RecyclerView.ViewHolder {
    @BindView(R2.id.expandTextView)
    TextView expandTextView;

    public ExpandViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * @param category
     * @param count    被折叠了的搜索结果数量
     */
    public void onBind(String category, int count) {
        // todo
        expandTextView.setText("点击展开剩余" + count + "项");
    }
}
