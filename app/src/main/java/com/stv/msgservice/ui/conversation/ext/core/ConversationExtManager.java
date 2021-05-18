package com.stv.msgservice.ui.conversation.ext.core;

import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.ui.conversation.ext.ExampleAudioInputExt;
import com.stv.msgservice.ui.conversation.ext.FileExt;
import com.stv.msgservice.ui.conversation.ext.ImageExt;
import com.stv.msgservice.ui.conversation.ext.LocationExt;
import com.stv.msgservice.ui.conversation.ext.ShootExt;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ConversationExtManager {
    private static ConversationExtManager instance;
    private List<ConversationExt> conversationExts;

    private ConversationExtManager() {
        conversationExts = new ArrayList<>();
        init();
    }

    public static synchronized ConversationExtManager getInstance() {
        if (instance == null) {
            instance = new ConversationExtManager();
        }
        return instance;
    }

    private void init() {
        registerExt(ImageExt.class);
//        registerExt(VoipExt.class);
        registerExt(ShootExt.class);
        registerExt(FileExt.class);
        registerExt(LocationExt.class);
        registerExt(ExampleAudioInputExt.class);
//        registerExt(UserCardExt.class);
    }

    public void registerExt(Class<? extends ConversationExt> clazz) {
        Constructor constructor;
        try {
            constructor = clazz.getConstructor();
            ConversationExt ext = (ConversationExt) constructor.newInstance();
            conversationExts.add(ext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterExt(Class<? extends ConversationExt> clazz) {
        // TODO
    }

    public List<ConversationExt> getConversationExts(Conversation conversation) {
        List<ConversationExt> currentExts = new ArrayList<>();
        for (ConversationExt ext : this.conversationExts) {
            if (!ext.filter(conversation)) {
                currentExts.add(ext);
            }
        }
        return currentExts;
    }
}
