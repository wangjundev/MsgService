package com.stv.msgservice.ui.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kyleduo.switchbutton.SwitchButton;
import com.stv.msgservice.AppExecutors;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.UserInfo;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.conversation.message.search.SearchMessageActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleConversationInfoFragment extends Fragment implements ConversationMemberAdapter.OnMemberClickListener, CompoundButton.OnCheckedChangeListener {

    // common
    @BindView(R2.id.memberRecyclerView)
    RecyclerView memberReclerView;
    @BindView(R2.id.stickTopSwitchButton)
    SwitchButton stickTopSwitchButton;
    @BindView(R2.id.silentSwitchButton)
    SwitchButton silentSwitchButton;

//    @BindView(R2.id.fileRecordOptionItemView)
//    OptionItemView fileRecordOptionItem;

    private Conversation conversationInfo;
    private ConversationMemberAdapter conversationMemberAdapter;
//    private ConversationViewModel conversationViewModel;
    private UserInfoViewModel userViewModel;
    private AppExecutors mAppExecutors;


    public static SingleConversationInfoFragment newInstance(ConversationEntity conversationInfo) {
        SingleConversationInfoFragment fragment = new SingleConversationInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("conversationInfo", conversationInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        assert args != null;
        conversationInfo = args.getParcelable("conversationInfo");
        assert conversationInfo != null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation_info_single_fragment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mAppExecutors = new AppExecutors();
//        conversationViewModel = WfcUIKit.getAppScopeViewModel(ConversationViewModel.class);
//        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        Log.i("Junwang", "SingleConversationInfoFragment init");
        UserInfoViewModel.Factory factory1 = new UserInfoViewModel.Factory(
                this.getActivity().getApplication(), null);

        userViewModel = new ViewModelProvider(this, factory1)
                .get(UserInfoViewModel.class);
//        String userId = conversationInfo.getNormalizedDestination();
        String userId = conversationInfo.getSenderAddress();
        conversationMemberAdapter = new ConversationMemberAdapter(conversationInfo, false, false);
        List<UserInfo> members = new ArrayList<>();//Collections.singletonList(userViewModel.getUserInfo(userId));
        userViewModel.getUserInfo(userId).observe(getViewLifecycleOwner(), userInfoEntity -> {
            members.add(userInfoEntity);
            conversationMemberAdapter.setMembers(members);
            conversationMemberAdapter.setOnMemberClickListener(this);
            memberReclerView.setAdapter(conversationMemberAdapter);
            memberReclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        });
        Log.i("Junwang", "conversationInfo.isTop()="+conversationInfo.isTop());
        stickTopSwitchButton.setChecked(conversationInfo.isTop());
//        silentSwitchButton.setChecked(conversationInfo.isSilent);
        stickTopSwitchButton.setOnCheckedChangeListener(this);
        silentSwitchButton.setOnCheckedChangeListener(this);

        observerUserInfoUpdate();
//        if(ChatManager.Instance().isCommercialServer()) {
//            fileRecordOptionItem.setVisibility(View.VISIBLE);
//        } else {
//            fileRecordOptionItem.setVisibility(View.GONE);
//        }
    }

    private void observerUserInfoUpdate() {
        userViewModel.userInfoLiveData().observe(getViewLifecycleOwner(), userInfos -> {
            for (UserInfo userInfo : userInfos) {
                if (userInfo.getUri().equals(this.conversationInfo.getSenderAddress()/*getNormalizedDestination()*/)) {
                    List<UserInfo> members = Collections.singletonList(userInfo);
                    conversationMemberAdapter.setMembers(members);
                    conversationMemberAdapter.notifyDataSetChanged();
                    break;
                }
            }
        });
    }

    public void RemoveConversation(ConversationEntity ce){
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                getActivity().getApplication(), 0);
        MessageViewModel messageViewModel = new ViewModelProvider(this, factory)
                .get(MessageViewModel.class);
        messageViewModel.getMessages(ce.getId()).observe(this, messageEntityList -> {
            mAppExecutors.diskIO().execute(() -> {
                ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                mViewModel.deleteConversation(ce);
                messageViewModel.deleteMessages(messageEntityList);
            });
        });
    }

    @OnClick(R2.id.clearMessagesOptionItemView)
    void clearMessage() {
        new MaterialDialog.Builder(getActivity())
                .items("????????????", "??????")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        if (position == 0) {
                            RemoveConversation((ConversationEntity) conversationInfo);
                        } else {

                        }
                    }
                })
                .show();
    }

    @OnClick(R2.id.searchMessageOptionItemView)
    void searchGroupMessage() {
        Intent intent = new Intent(getActivity(), SearchMessageActivity.class);
        intent.putExtra("conversation", (ConversationEntity)conversationInfo);
//        Intent intent = new Intent(getActivity(), ChatbotSearchActivity.class);
        startActivity(intent);
    }

//    @OnClick(R2.id.fileRecordOptionItemView)
//    void fileRecord(){
//        Intent intent = new Intent(getActivity(), FileRecordActivity.class);
//        intent.putExtra("conversation", conversationInfo.conversation);
//        startActivity(intent);
//    }

    @Override
    public void onUserMemberClick(UserInfo userInfo) {
//        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
//        intent.putExtra("userInfo", userInfo);
//        startActivity(intent);
    }

    @Override
    public void onAddMemberClick() {
//        Intent intent = new Intent(getActivity(), CreateConversationActivity.class);
//        ArrayList<String> participants = new ArrayList<>();
//        participants.add(conversationInfo.conversation.target);
//        intent.putExtra(PickConversationTargetActivity.CURRENT_PARTICIPANTS, participants);
//        startActivity(intent);
    }

    @Override
    public void onRemoveMemberClick() {
        // do nothing
    }

    private void stickTop(boolean top) {
        ((ConversationInfoActivity)getActivity()).setConversationTopStatus(top);
        mAppExecutors.diskIO().execute(() -> {
            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
            Log.i("Junwang", "top="+top);
            conversationInfo.setTop(top);
            if(top){
                conversationInfo.setTopTimestamp(System.currentTimeMillis());
            }else {
                conversationInfo.setTopTimestamp(0);
            }
            mViewModel.updateConversation((ConversationEntity) conversationInfo);
        });
//        ConversationListViewModel conversationListViewModel = ViewModelProviders
//                .of(this, new ConversationListViewModelFactory(Arrays.asList(Conversation.ConversationType.Single, Conversation.ConversationType.Group, Conversation.ConversationType.Channel), Arrays.asList(0)))
//                .get(ConversationListViewModel.class);
//        conversationListViewModel.setConversationTop(conversationInfo, top);
    }

    private void silent(boolean silent) {
//        conversationViewModel.setConversationSilent(conversationInfo.conversation, silent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.stickTopSwitchButton) {
            stickTop(isChecked);
        } else if (id == R.id.silentSwitchButton) {
            silent(isChecked);
        }

    }
}
