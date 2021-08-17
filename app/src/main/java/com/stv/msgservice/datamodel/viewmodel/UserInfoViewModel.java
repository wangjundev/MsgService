package com.stv.msgservice.datamodel.viewmodel;

import android.app.Application;

import com.stv.msgservice.datamodel.database.AppDatabase;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.datarepository.DataRepository;
import com.stv.msgservice.datamodel.model.UserInfo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UserInfoViewModel extends AndroidViewModel {
    private String mUri;
    private final DataRepository mRepository;
    private final LiveData<UserInfoEntity> mUserInfo;
    private MutableLiveData<List<UserInfo>> userInfoLiveData;

    public UserInfoViewModel(@NonNull Application application, DataRepository repository,
                             final String uri) {
        super(application);
        mUri = uri;
        mRepository = DataRepository.getInstance(AppDatabase.getInstance(application.getBaseContext()));
        mUserInfo = repository.getUser(mUri);
    }

    public MutableLiveData<List<UserInfo>> userInfoLiveData() {
        if (userInfoLiveData == null) {
            userInfoLiveData = new MutableLiveData<>();
        }
        return userInfoLiveData;
    }

    public LiveData<List<UserInfoEntity>> getUsers() {
        return mRepository.getUsers();
    }

    public LiveData<List<UserInfoEntity>> getLatestUsedChatbotList(){
        return mRepository.getLatestUsedChatbotList();
    }
//    public void SearchChatbotList() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(MessageConstants.BASE_URL)
////                .client(httpClient)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        ApiService service = retrofit.create(ApiService.class);
//        try {
//            service.searchChatbotList(/*chatbotId*/MessageViewModel.getHttpsRequestArguments(getApplication().getApplicationContext(), null, null))
//                    .subscribe(new Consumer<ChatbotSearchResult>() {
//                        @Override
//                        public void accept(ChatbotSearchResult chatbotSearchResult) throws Exception {
//                            chatbotSearchResult.getBots();
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//
//                        }
//                    });
//        }catch (Exception e){
//            Log.i("Junwang", "SearchChatbotList exception "+e.toString());
//        }
//    }

    public LiveData<UserInfoEntity> getUserInfo(String uri){
        return mRepository.getUser(uri);
    }

    public LiveData<UserInfoEntity> getUserInfoByConversationId(final long conversationId){
        return mRepository.getUserInfoByConversationId(conversationId);
    }

    public void insertUserInfo(UserInfoEntity userInfo){
        mRepository.insertUserInfo(userInfo);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final String mUri;

        private final DataRepository mRepository;

        public Factory(@NonNull Application application, String uri) {
            mApplication = application;
            mUri = uri;
            mRepository = DataRepository.getInstance(AppDatabase.getInstance(application.getApplicationContext()));
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserInfoViewModel(mApplication, mRepository, mUri);
        }
    }
}
