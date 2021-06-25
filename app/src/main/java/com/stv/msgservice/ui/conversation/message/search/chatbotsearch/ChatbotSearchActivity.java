package com.stv.msgservice.ui.conversation.message.search.chatbotsearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.chatbotinfo.ChatbotInfo;
import com.stv.msgservice.datamodel.chatbotinfo.ChatbotSearchResult;
import com.stv.msgservice.datamodel.chatbotinfo.SearchedBot;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.model.UserInfo;
import com.stv.msgservice.datamodel.network.ApiService;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.message.search.BaseNoToolbarActivity;
import com.stv.msgservice.ui.conversation.santilayout.ExpandLayout;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatbotSearchActivity extends BaseNoToolbarActivity implements AdapterView.OnItemClickListener{
    private ListView mChatbotListView;
    private SearchView mSearchView;
    private ChatbotListAdapter mAdapter;
    private ArrayList<SearchedBot> mChatbotList;
    private TextView mCancelTV;
    private UserInfo botEntity;
    private static List<UserInfo> mHistoryDataList;
    private boolean mIsFromHistory;

    private ExpandLayout history_expand_layout;
    private TagFlowLayout history_flowlayout;
    private TagAdapter<UserInfo> mHistoryAdapter;

    private UserInfoViewModel userInfoViewModel;
    private ConversationListViewModel mConversationListViewModel;

//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.arg1){
//                case 1:
//                    if(mChatbotList != null) {
//                        mAdapter.setChatbotListItems(mChatbotList);
//                    }else{
//                        Toast.makeText(getApplicationContext(), "没有找到服务号", Toast.LENGTH_LONG).show();
//                    }
//                    mHistoryAdapter.notifyDataChanged();
//                    break;
//                case 2:
//                    String conversationId = (String)msg.obj;
//                    LogUtil.i("Junwang", "conversationId="+conversationId);
//                    if(conversationId != null) {
//                        Intent intent = UIIntentsImpl.getConversationActivityWithH5MsgInfoIntent(ChatbotSearchActivity.this, conversationId, null, false,
//                                0, null, null, null, null, 0,
//                                null, null, null, null, botEntity != null ? botEntity.getMenu() : null);
//                        startActivity(intent);
//                    }else{
//                        String address = mChatbotList.get(msg.arg2).getId();
//                        Uri uri = Uri.parse("smsto:"+address);
//                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//                        intent.putExtra("sms_body", "");
//                        startActivity(intent);
//                    }
//                    finish();
//                    break;
//                default:
//                    break;
//            }
//
//        }
//    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected int contentLayout() {
        return R.layout.search_chatbot_result;
    }

    @Override
    protected void afterViews() {
//        setStatusBar();
        init();
        initView();
    }

    public void searchChatbotList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MessageConstants.BASE_URL)
//                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        try {
            service.searchChatbotList(MessageViewModel.getHttpsRequestArguments(getApplication().getApplicationContext(), null, null))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ChatbotSearchResult>() {
                        @Override
                        public void accept(ChatbotSearchResult chatbotSearchResult) throws Exception {
                            mChatbotList = (ArrayList<SearchedBot>) Arrays.asList(chatbotSearchResult.getBots());
                            if(mChatbotList != null) {
                                mAdapter.setChatbotListItems(mChatbotList);
                            }else{
                                Toast.makeText(getApplicationContext(), "没有找到服务号", Toast.LENGTH_LONG).show();
                            }
                            mHistoryAdapter.notifyDataChanged();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(getApplicationContext(), "没有找到服务号", Toast.LENGTH_LONG).show();
                        }
                    });
        }catch (Exception e){
            Log.i("Junwang", "SearchChatbotList exception "+e.toString());
            Toast.makeText(getApplicationContext(), "没有找到服务号", Toast.LENGTH_LONG).show();
        }
    }

    private void init(){
        UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                getApplication(), null);
        userInfoViewModel = new ViewModelProvider(this, factory)
                .get(UserInfoViewModel.class);
        mConversationListViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
    }

    private void initView(){
        mChatbotListView = (ListView) findViewById(R.id.searchResultList);
//        lvContacts.setVisibility(View.GONE);
        mSearchView = (SearchView)findViewById(R.id.searchview);
        mSearchView.setIconifiedByDefault(false);
        mChatbotList = new ArrayList();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtil.i("Junwang", "onQueryTextSubmit query="+query);
//                if(mChatbotList != null){
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mChatbotList.clear();
//                            UserInfo entity = new UserInfoEntity();
//                            entity.setName(query);
//                            if(!mIsFromHistory){
//                                if(mHistoryDataList.contains(entity)){
//                                    LogUtil.i("Junwang", "mHistoryDataList contain entity.");
//                                    mHistoryDataList.add(0, entity);
//                                    for(int i=1; i<mHistoryDataList.size(); i++){
//                                        if(mHistoryDataList.get(i).getName().equals(query)){
//                                            mHistoryDataList.remove(i);
////                                            ChatbotFavoriteTableUtils.updateChatbotSearchHistoryTable(entity);
//                                        }
//                                    }
//                                }else {
//                                    mHistoryDataList.add(0, entity);
////                                    ChatbotFavoriteTableUtils.insertChatbotSearchHistoryTable(null, query, null, null, null, 0, null, null, null);
//                                }
////                                mHistoryAdapter.notifyDataChanged();
//                            }else{
////                                mHistoryAdapter.notifyDataChanged();
////                                ChatbotFavoriteTableUtils.updateChatbotSearchHistoryTable(entity);
//                                mIsFromHistory = false;
//                            }
//                            ChatbotInfoQueryHelper.SearchChatbot(query);
//                        }
//                    }).start();
//                    mHistoryAdapter.notifyDataChanged();
//                }

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
                    searchChatbotList();
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
//                    mAdapter.setChatbotListItems(null);
                    mChatbotListView.setVisibility(View.GONE);
                    mAdapter.clear();
                    mAdapter.notifyDataSetChanged();
                }else {
                    mChatbotListView.setVisibility(View.VISIBLE);
                    searchChatbotList();
//                    ChatbotInfoQueryHelper.SearchChatbot(newText);

                }
                return true;
            }
        });
        history_expand_layout = (ExpandLayout)findViewById(R.id.expand_history);
        history_flowlayout = (TagFlowLayout) findViewById(R.id.history_flow_layout);
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
        mCancelTV = (TextView)findViewById(R.id.search_cancel_button);
        mCancelTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAdapter = new ChatbotListAdapter(getApplicationContext());

        mChatbotListView.setAdapter(mAdapter);
    }

    protected boolean useThemestatusBarColor = true;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useStatusBarColor = true;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_background_color));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    class ChatbotListAdapter extends ArrayAdapter<SearchedBot> {
        public ChatbotListAdapter(@NonNull Context context) {
            super(context, R.layout.search_chatbot_itemview);
        }

        public void setChatbotListItems(final List<SearchedBot> newList) {
            clear();
            addAll(newList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView;
            if (convertView != null) {
                itemView = convertView;
            } else {
                final LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = inflater.inflate(
                        R.layout.search_chatbot_itemview, parent, false);
            }
            final TextView textView = (TextView)itemView.findViewById(R.id.text_view);
            final SearchedBot item = mChatbotList.get(position);
            textView.setText(item.getName());
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String chatbotId = mChatbotList.get(position).getId();
                    Log.i("Junwang", "click "+chatbotId+" item");
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(MessageConstants.BASE_URL)
//                .client(httpClient)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    ApiService service = retrofit.create(ApiService.class);
                    try {
                        service.getChatbotInfo(/*chatbotId*/MessageViewModel.getHttpsRequestArguments(getApplication().getApplicationContext(), chatbotId, null)).subscribe(new Consumer<ChatbotInfo>() {
                            @Override
                            public void accept(ChatbotInfo chatbotInfo) throws Exception {
                                UserInfoEntity userInfoEntity = new UserInfoEntity();
                                userInfoEntity.setUri(chatbotId);
                                String menuJson = new Gson().toJson(chatbotInfo.getPersistent_menu());
                                userInfoEntity.setMenu(menuJson);
                                userInfoEntity.setPortrait(chatbotInfo.getBotinfo().getPcc().getOrg_details().getMedia_list().getMedia_entry().get(0).getMedia().getMedia_url());
                                userInfoEntity.setName(chatbotInfo.getBotinfo().getPcc().getOrg_details().getOrg_name().get(0).getDisplay_name());
                                userInfoEntity.setCategory(chatbotInfo.getBotinfo().getPcc().getOrg_details().getCategory_list().getCategory_entry().toString());
                                userInfoEntity.setDescription(chatbotInfo.getBotinfo().getPcc().getOrg_details().getOrg_description());
//                                mRepository.insertUserInfo(userInfoEntity);
                                userInfoViewModel.insertUserInfo(userInfoEntity);
                                mConversationListViewModel.getConversationByChatbotId(chatbotId).observe(ChatbotSearchActivity.this, conversationEntity -> {
                                    if(conversationEntity != null) {
                                        Intent intent = new Intent(getContext(), ConversationActivity.class);
                                        intent.putExtra("conversation", conversationEntity);
                                        getContext().startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(getContext(), ConversationActivity.class);
                                        intent.putExtra("chatbotId", chatbotId);
                                        intent.putExtra("conversationTitle", chatbotInfo.getBotinfo().getPcc().getOrg_details().getOrg_name().get(0).getDisplay_name());
                                        getContext().startActivity(intent);
                                    }
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
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String conversationId = BugleDatabaseOperations.getConversationId(mChatbotList.get(position).getChatbotSipUri());
//                            botEntity = ChatbotInfoTableUtils.queryChatbotInfoTable(mChatbotList.get(position).getChatbotSipUri());
//                            Message msg = new Message();
//                            if(conversationId != null){
//                                msg.obj = conversationId;
//                                Intent intent = new Intent(getContext(), ConversationActivity.class);
//                                intent.putExtra("conversation", (ConversationEntity)conversationInfo);
//                                getContext().startActivity(intent);
//                            }else{
//                                msg.arg2 = position;//mChatbotList.get(position).getChatbotSipUri();
//                            }
//                            msg.arg1 = 2;
//                            mHandler.sendMessage(msg);
//                        }
//                    }).start();
                }
            });
            return itemView;
        }
    }
}
