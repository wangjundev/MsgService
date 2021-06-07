package com.stv.msgservice.ui.conversation;

import android.widget.Toast;

import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.ui.WfcBaseActivity;

import androidx.fragment.app.Fragment;

public class ConversationInfoActivity extends WfcBaseActivity {

    private Conversation conversationInfo;
    private String activityName;

    @Override
    protected int contentLayout() {
        return R.layout.fragment_container_activity;
    }

    @Override
    protected void afterViews() {
        conversationInfo = getIntent().getParcelableExtra("conversation");
        activityName = getIntent().getStringExtra("activityname");
        Fragment fragment = SingleConversationInfoFragment.newInstance((ConversationEntity) conversationInfo);
        if (fragment == null) {
            Toast.makeText(this, "todo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerFrameLayout, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setConversationTopStatus(boolean isTop){
        ConversationActivity.conversation.setTop(isTop);
//        Class activityThreadClass = null;
//        try {
//            activityThreadClass = Class.forName(activityName);
//            Method m = activityThreadClass.getMethod("setConversationTopStatus", boolean.class);
//            m.invoke(activityThreadClass.newInstance(), isTop);
//        } catch (Exception e) {
//            Log.i("Junwang", "setConversationTopStatus exception "+e.toString());
//            e.printStackTrace();
//        }
    }

}
