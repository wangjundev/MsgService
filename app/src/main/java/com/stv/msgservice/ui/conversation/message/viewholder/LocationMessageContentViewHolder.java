package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.third.activity.LocationData;
import com.stv.msgservice.third.activity.ShowLocationActivity;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.message.LocationMessageContent;
import com.stv.msgservice.utils.UIUtils;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

@MessageContentType(LocationMessageContent.class)
@EnableContextMenu
public class LocationMessageContentViewHolder extends NormalMessageContentViewHolder {

    @BindView(R2.id.locationTitleTextView)
    TextView locationTitleTextView;
    @BindView(R2.id.locationDescription)
    TextView locationDescription;
    @BindView(R2.id.locationImageView)
    ImageView locationImageView;

    public LocationMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    @Override
    public void onBind(Message message) {
        LocationData locationData = message.getLocationData();
        locationTitleTextView.setText(locationData.getTitle());
        locationDescription.setText(locationData.getDescription());
//        LocationMessageContent locationMessage = (LocationMessageContent) message.message.content;
//        locationTitleTextView.setText(locationMessage.getTitle());

        if (locationData.getThumbnail() != null && locationData.getThumbnail().getWidth() > 0) {
            int width = locationData.getThumbnail().getWidth();
            int height = locationData.getThumbnail().getHeight();
            locationImageView.getLayoutParams().width = UIUtils.dip2Px(width > 200 ? 200 : width);
            locationImageView.getLayoutParams().height = UIUtils.dip2Px(height > 200 ? 200 : height);
            locationImageView.setImageBitmap(locationData.getThumbnail());
        } else {
            Glide.with(fragment).load(R.mipmap.default_location)
                    .apply(new RequestOptions().override(UIUtils.dip2Px(200), UIUtils.dip2Px(200)).centerCrop()).into(locationImageView);
        }
    }

    @OnClick(R2.id.locationLinearLayout)
    public void onClick(View view) {
        LocationData locationData = message.getLocationData();
        Intent intent = new Intent(fragment.getContext(), ShowLocationActivity.class);
//        LocationMessageContent content = (LocationMessageContent) message.message.content;
        intent.putExtra("Lat", locationData.getLat());
        intent.putExtra("Long", locationData.getLng());
        intent.putExtra("title", locationData.getTitle());
        fragment.startActivity(intent);
    }
}
