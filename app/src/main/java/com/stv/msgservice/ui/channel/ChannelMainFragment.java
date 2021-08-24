package com.stv.msgservice.ui.channel;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.stv.msgservice.MainActivity;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.banner.Banner;
import com.stv.msgservice.ui.banner.IndicatorView;
import com.stv.msgservice.utils.UIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChannelMainFragment extends Fragment {
    @BindView(R2.id.stv_banner)
    Banner stv_banner;
    @BindView(R2.id.stv_indicator)
    IndicatorView stv_indicator;
    @BindView(R2.id.recyclerview_latest_chatbot)
    RecyclerView recyclerview_latest_chatbot;
//    @BindView(R2.id.recyclerview_chatbot_item)
//    RecyclerView recyclerview_chatbot_item;
    @BindView(R2.id.message_list)
    RecyclerView message_list;
    @BindView(R2.id.simulate_btn1)
    ImageButton simulate_btn1;
    private ChannelLatestUsedChatbotListAdapter channelLatestUsedChatbotListAdapter;
    private ChannelChatbotMsgListAdapter chatbotMsgListAdapter;

    public static List<String> images = new ArrayList<String>(Arrays.asList(
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2860421298,3956393162&fm=26&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=163638141,898531478&fm=26&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1028426622,4209712325&fm=26&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1462142898,440466184&fm=26&gp=0.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3210855908,3095539181&fm=26&gp=0.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2080505558,2205047574&fm=26&gp=0.jpg"
    ));

    public static List<String> texts = new ArrayList<String>(Arrays.asList(
            "重磅发布《“美国第一”？！美国抗疫真相》研究报告，用严谨的研究、真实的数据、客观的立…",
            "明起，出租汽车驾驶员全员疫苗接种及全行业严格防疫明起，出租汽",
            "习近平“七一”重要讲话中的党史｜土地革命战争篇",
            "动漫 | 增值税、消费税分别与附加税费申报表整合小问答",
            "习近平“七一”重要讲话中的党史｜土地革命战争篇",
            "习近平“七一”重要讲话中的党史｜土地革命战争篇"
    ));

    public static ChannelMainFragment newInstance() {
        return new ChannelMainFragment();
    }
    private void initBanner() {
        stv_banner.setIndicator(stv_indicator.setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_BEZIER)
                .setIndicatorColor(/*Color.GRAY*/Color.parseColor("#DADADA"))
                .setIndicatorSelectorColor(Color.WHITE), false)
                .setPageMargin(UIUtils.dip2Px(40), UIUtils.dip2Px(3))
                .setOuterPageChangeListener(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {

                    }
                })
                .setAdapter(new ChannelMainFragment.ImageAdapter(images));
    }

    private void initLatestUsedChatbotList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerview_latest_chatbot.setLayoutManager(layoutManager);
        recyclerview_latest_chatbot.addItemDecoration(new HorizontalItemDecoration(29,getContext()));//10表示10dp

        UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                getActivity().getApplication(), null);

        final UserInfoViewModel userInfoViewModel = new ViewModelProvider(this, factory)
                .get(UserInfoViewModel.class);
        userInfoViewModel.getLatestUsedChatbotList().observe(getViewLifecycleOwner(), userInfoEntities -> {
            channelLatestUsedChatbotListAdapter.setMembers(userInfoEntities);
            recyclerview_latest_chatbot.setAdapter(channelLatestUsedChatbotListAdapter);
        });

        simulate_btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).simulateReceivedMsg();
            }
        });
    }

    private void initMsgList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        message_list.setLayoutManager(layoutManager);
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                this.getActivity().getApplication(), 0);
        MessageViewModel messageViewModel = new ViewModelProvider(this, factory)
                .get(MessageViewModel.class);

        messageViewModel.getAllMessages().observe(getViewLifecycleOwner(), messageUserInfoEntities -> {
            chatbotMsgListAdapter.setMessageList(messageUserInfoEntities);
            message_list.setAdapter(chatbotMsgListAdapter);
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("Junwang", "ChannelMainFragment onCreate");
        super.onCreate(savedInstanceState);
        channelLatestUsedChatbotListAdapter = new ChannelLatestUsedChatbotListAdapter();
        chatbotMsgListAdapter = new ChannelChatbotMsgListAdapter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("Junwang", "ChannelMainFragment onCreateView");
        View view = inflater.inflate(R.layout.ysyy_fragment, container, false);
        ButterKnife.bind(this, view);
        initBanner();
        initLatestUsedChatbotList();
        initMsgList();
        return view;
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
            return new ChannelMainFragment.ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChannelMainFragment.ImageViewHolder imageViewHolder = (ChannelMainFragment.ImageViewHolder) holder;
            Glide.with(imageViewHolder.bannerImage)
                    .load(items.get(position))
                    .apply(new RequestOptions()
                            .transform(new RoundedCorners(SizeUtils.dp2px(4))))
                    .into(imageViewHolder.bannerImage);
            imageViewHolder.bannerTextView.setText(texts.get(position));
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
            bannerImage = itemView.findViewById(R.id.channel_banner_img);
            bannerTextView = itemView.findViewById(R.id.channel_bannel_text);
        }
    }
}
