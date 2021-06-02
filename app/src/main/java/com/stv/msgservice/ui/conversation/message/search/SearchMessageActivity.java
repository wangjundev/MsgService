package com.stv.msgservice.ui.conversation.message.search;

import com.stv.msgservice.datamodel.model.Conversation;

import java.util.List;

public class SearchMessageActivity extends SearchActivity {
    private Conversation conversation;

    @Override
    protected void beforeViews() {
        conversation = getIntent().getParcelableExtra("conversation");
    }

    @Override
    protected void initSearchModule(List<SearchableModule> modules) {
        modules.add(new ConversationMessageSearchModule(this, conversation));
    }
}
