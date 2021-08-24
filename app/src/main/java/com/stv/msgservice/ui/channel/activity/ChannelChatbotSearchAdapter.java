package com.stv.msgservice.ui.channel.activity;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baronzhang.retrofit2.converter.FastJsonConverterFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.chatbotinfo.Botinfo;
import com.stv.msgservice.datamodel.chatbotinfo.CategoryList;
import com.stv.msgservice.datamodel.chatbotinfo.ChatbotInfo;
import com.stv.msgservice.datamodel.chatbotinfo.Media;
import com.stv.msgservice.datamodel.chatbotinfo.MediaEntry;
import com.stv.msgservice.datamodel.chatbotinfo.MediaList;
import com.stv.msgservice.datamodel.chatbotinfo.OrgDetails;
import com.stv.msgservice.datamodel.chatbotinfo.OrgName;
import com.stv.msgservice.datamodel.chatbotinfo.Pcc;
import com.stv.msgservice.datamodel.chatbotinfo.PersistentMenu;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.network.ApiService;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.GlideCircleWithBorder;
import com.stv.msgservice.ui.conversation.ConversationActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class ChannelChatbotSearchAdapter extends RecyclerView.Adapter<ChannelChatbotSearchAdapter.ViewHolder>{
    private AppCompatActivity activity;
    private List<UserInfoEntity> userInfos;
    private UserInfoViewModel userInfoViewModel;
    private ConversationListViewModel mConversationListViewModel;

    public ChannelChatbotSearchAdapter(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setUserInfos(List<UserInfoEntity> userInfos){
        this.userInfos = userInfos;
    }

    @Override
    public int getItemCount() {
        if(userInfos != null){
            return userInfos.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public ChannelChatbotSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.channel_search_chatbot_item, parent, false);
        ViewHolder viewHolder = new ChannelChatbotSearchAdapter.ViewHolder(itemView);
        processChatbotonClick(viewHolder, itemView);
        return viewHolder;
    }

    private void processChatbotonClick(ViewHolder viewHolder, View itemView) {
        itemView.setOnClickListener(viewHolder::onClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelChatbotSearchAdapter.ViewHolder holder, int position) {
        holder.bindUserInfo(userInfos.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R2.id.portraitImageView)
        ImageView portraitImageView;
        @BindView(R2.id.nameTextView)
        TextView nameTextView;
        @BindView(R2.id.chatbot_description)
        TextView chatbot_description;
        @BindView(R2.id.chatbot_organize)
        TextView chatbot_organize;
        private UserInfoEntity userInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onClick(View itemView) {
            String chatbotId = userInfo.getUri();
            Log.i("Junwang", "click "+chatbotId+" item");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MessageConstants.BASE_URL)
//                .client(httpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                            .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .build();
            String postBody = ConversationListViewModel.getPostBodyJson(activity, null, null, chatbotId);
            Log.i("Junwang", "post body json is "+postBody);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), postBody);

            ApiService service = retrofit.create(ApiService.class);
            try {
                service.getChatbotInfo(body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ChatbotInfo>() {
                            @Override
                            public void accept(ChatbotInfo chatbotInfo) throws Exception {
                                UserInfoEntity userInfoEntity = new UserInfoEntity();
                                userInfoEntity.setUri(chatbotId);
                                PersistentMenu menu = chatbotInfo.getPersistent_menu();
                                if(menu != null){
                                    Log.i("Junwang", "menu = "+menu.getMenu().toString());
                                    String menuJson = new Gson().toJson(menu);
                                    userInfoEntity.setMenu(menuJson);
                                }else{
                                    userInfoEntity.setMenu(null);
                                }

                                Botinfo botinfo = chatbotInfo.getBotinfo();
                                if(botinfo != null){
                                    Pcc pcc = botinfo.getPcc();
                                    if(pcc != null){
                                        userInfoEntity.setPccType(pcc.getPcc_type());
                                        OrgDetails orgDetails = pcc.getOrg_details();
                                        if(orgDetails != null){
                                            MediaList mediaList = orgDetails.getMedia_list();
                                            if(mediaList != null){
                                                List<MediaEntry> mediaEntries = mediaList.getMedia_entry();
                                                if(mediaEntries != null && mediaEntries.size() > 0){
                                                    MediaEntry mediaEntry = mediaEntries.get(0);
                                                    if(mediaEntry != null){
                                                        Media media = mediaEntry.getMedia();
                                                        if(media != null){
                                                            userInfoEntity.setPortrait(media.getMedia_url());
                                                        }
                                                    }
                                                }
                                            }

                                            List<OrgName> orgNameList = orgDetails.getOrg_name();
                                            if(orgNameList != null && orgNameList.size() > 0){
                                                OrgName orgName = orgNameList.get(0);
                                                if(orgName != null)
                                                    userInfoEntity.setName(orgName.getDisplay_name());
                                                Log.i("Junwang", "search chatbotInfo id="+chatbotId+", name="+orgName.getDisplay_name());
                                            }

                                            CategoryList categoryList = orgDetails.getCategory_list();
                                            if(categoryList != null){
                                                List<String> entry = categoryList.getCategory_entry();
                                                if(entry != null && entry.size() > 0){
                                                    userInfoEntity.setCategory(entry.get(0));
                                                }
                                            }

                                            userInfoEntity.setDescription(orgDetails.getOrg_description());
                                        }
                                    }
                                }
                                new Thread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                userInfoViewModel.insertUserInfo(userInfoEntity);
                                            }
                                        }
                                ).start();
                                UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                                        activity.getApplication(), null);
                                userInfoViewModel = new ViewModelProvider(activity, factory)
                                        .get(UserInfoViewModel.class);
                                mConversationListViewModel = new ViewModelProvider(activity).get(ConversationListViewModel.class);
                                LiveData<ConversationEntity> liveData = mConversationListViewModel.getConversationByChatbotId(chatbotId);
                                liveData.observe(activity, conversationEntity -> {
                                    if(conversationEntity != null) {
                                        Intent intent = new Intent(activity, ConversationActivity.class);
                                        intent.putExtra("conversation", conversationEntity);
                                        intent.putExtra("toFocusMessageId", -1);
                                        intent.putExtra("fromSearch", false);
                                        activity.startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(activity, ConversationActivity.class);
                                        intent.putExtra("chatbotId", chatbotId);
                                        intent.putExtra("fromSearch", false);
                                        intent.putExtra("conversationTitle", chatbotInfo.getBotinfo().getPcc().getOrg_details().getOrg_name().get(0).getDisplay_name());
                                        activity.startActivity(intent);
                                    }
                                    liveData.removeObservers(activity);
                                });
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("Junwang", "get chatbot info exception "+throwable.toString());
                            }
                        });
            }catch(Exception e){
                Log.e("Junwang", "get chatbot info exception "+e.toString());
            }
        }

        public void bindUserInfo(UserInfoEntity userInfo) {
            if (userInfo == null) {
                nameTextView.setText("");
                portraitImageView.setImageResource(R.mipmap.avatar_def);
                return;
            }
            this.userInfo = userInfo;
            nameTextView.setVisibility(View.VISIBLE);
            nameTextView.setText(userInfo.getName());
            chatbot_description.setText(userInfo.getDescription());
//            chatbot_organize.setText(userInfo.getPccType());
            Glide.with(portraitImageView).load(userInfo.getPortrait()).apply(new RequestOptions().placeholder(R.mipmap.avatar_def)).transform(new CenterCrop(),new GlideCircleWithBorder()).into(portraitImageView);
        }
    }
}
