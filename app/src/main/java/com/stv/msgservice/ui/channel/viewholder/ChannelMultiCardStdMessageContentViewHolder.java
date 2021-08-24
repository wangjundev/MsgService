package com.stv.msgservice.ui.channel.viewholder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.database.entity.MessageUserInfoEntity;
import com.stv.msgservice.ui.banner.Banner;
import com.stv.msgservice.ui.channel.ChannelMainFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;

public class ChannelMultiCardStdMessageContentViewHolder extends ChannelMsgItemViewHolder {
    //    @BindView(R2.id.contentTextView)
//    TextView contentTextView;
//    @BindView(R2.id.refTextView)
//    TextView refTextView;
//    @BindView(R2.id.card_image)
//    ImageView cardImage;
//    @BindView(R2.id.card_title)
//    TextView cardTitle;
//    @BindView(R2.id.card_description)
//    TextView cardDescription;
//    @BindView(R2.id.card_layout)
//    LinearLayout cardLayout;
    //    private QuoteInfo quoteInfo;
//    @BindView(R2.id.card_rv)
//    RecyclerView cardRv;
    Fragment mFragment;
    @BindView(R2.id.stv_multicard_banner)
    Banner stv_multicard_banner;

    public ChannelMultiCardStdMessageContentViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
    }

    @Override
    public void onBind(MessageUserInfoEntity message) {
        Log.i("Junwang", "multicardstdMessage content view onBind");
//        String content = message.getContent();
//        ChatbotStdMultiCard multiCard = new Gson().fromJson(content, ChatbotStdMultiCard.class);
//        GeneralPurposeCardCarousel gpcc = multiCard.getMessage().getGeneralPurposeCardCarousel();
//        if(gpcc != null){
//            CardContent[] cardcontents = gpcc.getContent();
//            if((cardcontents != null) && (cardcontents.length>0)){
////                loadChatbotMultiCard(cardcontents);
////                cardRv.setLayoutManager(new LinearLayoutManager(mFragment.getContext(), LinearLayoutManager.HORIZONTAL, false));
////                MultiCardItemViewAdapter listAdapter = new MultiCardItemViewAdapter(Arrays.asList(cardcontents), mFragment.getContext(), R.layout.multi_card_itemview);
////                cardRv.setAdapter(listAdapter);
//            }
//        }
        initMultiCardBanner();
    }

    private void initMultiCardBanner() {
        stv_multicard_banner
//                .setIndicator(stv_indicator.setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_BEZIER)
//                .setIndicatorColor(/*Color.GRAY*/Color.parseColor("#DADADA"))
//                .setIndicatorSelectorColor(Color.WHITE), false)
//                .setPageMargin(UIUtils.dip2Px(40), UIUtils.dip2Px(3))
                .setOuterPageChangeListener(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {

                    }
                })
                .setAdapter(new ChannelMultiCardStdMessageContentViewHolder.ImageAdapter(ChannelMainFragment.images));
    }

    static class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<String> items;

        ImageAdapter(List<String> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_image, parent, false);
            return new ChannelMultiCardStdMessageContentViewHolder.ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChannelMultiCardStdMessageContentViewHolder.ImageViewHolder imageViewHolder = (ChannelMultiCardStdMessageContentViewHolder.ImageViewHolder) holder;
            Glide.with(imageViewHolder.bannerImage)
                    .load(items.get(position))
                    .apply(new RequestOptions()
                            .transform(new RoundedCorners(SizeUtils.dp2px(4))))
                    .into(imageViewHolder.bannerImage);
            imageViewHolder.bannerTextView.setText(ChannelMainFragment.texts.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView bannerImage;
        private final TextView bannerTextView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.multicard_banner_img);
            bannerTextView = itemView.findViewById(R.id.multicard_banner_text);
        }
    }

}