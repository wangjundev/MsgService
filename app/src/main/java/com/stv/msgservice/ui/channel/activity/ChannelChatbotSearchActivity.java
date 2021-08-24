package com.stv.msgservice.ui.channel.activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.chatbotinfo.ChatbotSearchResult;
import com.stv.msgservice.datamodel.chatbotinfo.SearchedBot;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.model.UserInfo;
import com.stv.msgservice.datamodel.network.ApiService;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.ui.conversation.message.search.BaseNoToolbarActivity;
import com.stv.msgservice.ui.conversation.santilayout.ExpandLayout;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChannelChatbotSearchActivity extends BaseNoToolbarActivity {
    private RecyclerView mSerchChatbotResult;
    private SearchView mSearchView;
    private List<SearchedBot> mChatbotList;
    private boolean mIsFromHistory;
    private static List<UserInfo> mHistoryDataList;
    private ExpandLayout history_expand_layout;
    private TagFlowLayout history_flowlayout;
    private TagAdapter<UserInfo> mHistoryAdapter;
    private TextView mCancelTV;
    private ImageView mChannelBackImage;
    private ChannelChatbotSearchAdapter mAdapter;

    @Override
    protected int contentLayout() {
        return R.layout.channel_search_chatbot_activity;
    }

    @Override
    protected void afterViews() {
//        setStatusBar();
        initView();
    }

    public void searchChatbotList(String keyWord, boolean isNeedPopupDlg) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MessageConstants.BASE_URL)
//                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(LenientGsonConverterFactory.create())
                .build();

        String postBody = ConversationListViewModel.getPostBodyJson(this, keyWord, null, null);
        Log.i("Junwang", "post body json is "+postBody);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), postBody);

        ApiService service = retrofit.create(ApiService.class);
        try {
            service.searchChatbotList(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ChatbotSearchResult>() {
                        @Override
                        public void accept(ChatbotSearchResult chatbotSearchResult) throws Exception {
//                            mChatbotList = (ArrayList<SearchedBot>) Arrays.asList(chatbotSearchResult.getBots());
                            mChatbotList = chatbotSearchResult.getBots();
                            if(mChatbotList != null) {
                                List<UserInfoEntity> botList = new ArrayList<>();
                                UserInfoEntity user = new UserInfoEntity();
                                SearchedBot bot;
                                for(int i=0; i<mChatbotList.size(); i++){
                                    bot = mChatbotList.get(i);
                                    user.setUri(bot.getId());
                                    user.setName(bot.getName());
                                    user.setPortrait(bot.getIcon());
                                    //need server add
                                    user.setDescription("chatbot服务描述，可以为服务、功能、职能等，最多显示两行");
                                    user.setPccType("服务机构、组织、公司名称");
                                    botList.add(user);
                                }
                                mAdapter.setUserInfos(botList);
                            }else{
                                if(isNeedPopupDlg){
                                    Toast.makeText(getApplicationContext(), "没有找到服务号", Toast.LENGTH_LONG).show();
                                }
                            }
                            mHistoryAdapter.notifyDataChanged();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.i("Junwang", "SearchChatbotList throwable "+throwable.toString());
                            if(isNeedPopupDlg){
                                Toast.makeText(getApplicationContext(), "没有找到服务号", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch (Exception e){
            Log.i("Junwang", "SearchChatbotList exception "+e.toString());
            if(isNeedPopupDlg){
                Toast.makeText(getApplicationContext(), "没有找到服务号", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initView(){
        mSerchChatbotResult = (RecyclerView) findViewById(R.id.channel_search_result_recyclerview);
//        lvContacts.setVisibility(View.GONE);
        mSearchView = (SearchView)findViewById(R.id.channel_searchview);
        mSearchView.setIconifiedByDefault(false);
        mChatbotList = new ArrayList();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtil.i("Junwang", "onQueryTextSubmit query="+query);
                if(mChatbotList != null){
                    mChatbotList.clear();
                    UserInfo entity = new UserInfoEntity();
                    entity.setName(query);
                    if(!mIsFromHistory){
                        if(mHistoryDataList.contains(entity)){
                            LogUtil.i("Junwang", "mHistoryDataList contain entity.");
                            mHistoryDataList.add(0, entity);
                            for(int i=1; i<mHistoryDataList.size(); i++){
                                if(mHistoryDataList.get(i).getName().equals(query)){
                                    mHistoryDataList.remove(i);
                                }
                            }
                        }else {
                            mHistoryDataList.add(0, entity);
                        }
                    }else{
                        mIsFromHistory = false;
                    }
                    searchChatbotList(query, true);
                    mHistoryAdapter.notifyDataChanged();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LogUtil.i("Junwang", "onQueryTextChange newText="+newText);
                if(mChatbotList != null){
                    mChatbotList.clear();
                }
                if(newText == null || newText.length() == 0){
                    mSerchChatbotResult.setVisibility(View.GONE);
                    mAdapter.setUserInfos(null);
                    mAdapter.notifyDataSetChanged();
                }else {
                    mSerchChatbotResult.setVisibility(View.VISIBLE);
                    if(mChatbotList != null){
                        mChatbotList.clear();
                    }
                    if(mAdapter != null){
                        mAdapter.setUserInfos(null);
                    }
                    searchChatbotList(newText, false);
//                    ChatbotInfoQueryHelper.SearchChatbot(newText);

                }
                return true;
            }
        });
        history_expand_layout = (ExpandLayout)findViewById(R.id.channel_expand_history);
        history_flowlayout = (TagFlowLayout) findViewById(R.id.channel_history_flow_layout);
        history_flowlayout.setMaxSelectCount(1);
//        queryChatbotSearchHistory();
        mHistoryDataList = new ArrayList<UserInfo>();
        mHistoryAdapter = new TagAdapter<UserInfo>(mHistoryDataList) {
            @Override
            public View getView(FlowLayout parent, int position, UserInfo entity) {
                final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
                TextView tv = (TextView) mInflater.inflate(R.layout.tv, history_flowlayout, false);
                tv.setText(entity.getName());
                return tv;
            }
        };
        history_flowlayout.setAdapter(mHistoryAdapter);
        history_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if(mHistoryDataList != null) {
                    mHistoryDataList.add(0, mHistoryDataList.get(position));
                    mHistoryDataList.remove(position+1);
                    mIsFromHistory = true;
                    mSearchView.setQuery(mHistoryDataList.get(0).getName(), true);
                }
                return false;
            }
        });
        mCancelTV = (TextView)findViewById(R.id.channel_search_cancel_button);
        mCancelTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mChannelBackImage = (ImageView)findViewById(R.id.channel_back_image);
        mChannelBackImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAdapter = new ChannelChatbotSearchAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSerchChatbotResult.setLayoutManager(layoutManager);
        mSerchChatbotResult.setAdapter(mAdapter);
    }
}
