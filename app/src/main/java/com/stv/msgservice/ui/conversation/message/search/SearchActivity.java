package com.stv.msgservice.ui.conversation.message.search;

import android.os.Bundle;
import android.text.TextUtils;

import com.stv.msgservice.R;
import com.stv.msgservice.R2;

import java.util.ArrayList;
import java.util.List;

//import androidx.appcompat.widget.SearchView;
import butterknife.BindView;
import butterknife.OnClick;

public abstract class SearchActivity extends BaseNoToolbarActivity {
    private SearchFragment searchFragment;
    private List<SearchableModule> modules = new ArrayList<>();

    @BindView(R2.id.search_view)
    SearchView searchView;

    @OnClick(R2.id.cancel)
    public void onCancelClick() {
        finish();
    }

    protected boolean hideSearchDescView(){
        return false;
    }

    /**
     * 子类如果替换布局，它的布局中必须要包含 R.layout.search_bar
     *
     * @return 布局资源id
     */
    protected int contentLayout() {
        return R.layout.search_portal_activity;
    }

    protected void beforeViews() {
        setStatusBarColor(R.color.gray5);
    }

    protected void afterViews() {
        initSearchView();
        initSearchFragment();
        String initialKeyword = getIntent().getStringExtra("keyword");
//        ChatManager.Instance().getMainHandler().post(() -> {
            if (!TextUtils.isEmpty(initialKeyword)) {
                searchView.setQuery(initialKeyword);
            }
//        });

        if(hideSearchDescView()){
            searchView.clearFocus();
            hideInputMethod();
        }
    }

    private void initSearchView() {
        searchView.setOnQueryTextListener(this::search);
    }

    protected void initSearchFragment() {
        searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putBoolean(SearchFragment.HIDE_SEARCH_DESC_VIEW, hideSearchDescView());
        searchFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerFrameLayout, searchFragment)
                .commit();
        initSearchModule(modules);
    }

    void search(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            searchFragment.search(keyword, modules);
        } else {
            searchFragment.reset();
        }
    }

    /**
     * @param modules 是一个输出参数，用来添加希望搜索的{@link SearchableModule}
     */
    protected abstract void initSearchModule(List<SearchableModule> modules);
}
