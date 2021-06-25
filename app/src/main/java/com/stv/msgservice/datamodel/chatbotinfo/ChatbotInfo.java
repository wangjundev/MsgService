package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class ChatbotInfo {
    private Botinfo botinfo;
    @JSONField(name="persistent-menu")
    private PersistentMenu persistent_menu;
    @JSONField(name="generic-CSS-template")
    private String generic_CSS_template;
    @JSONField(name="bot-verification")
    private BotVerification bot_verification;
    @JSONField(name="bot-strategy")
    private BotStrategy bot_strategy;

    public Botinfo getBotinfo() {
        return botinfo;
    }

    public void setBotinfo(Botinfo botinfo) {
        this.botinfo = botinfo;
    }

    public PersistentMenu getPersistent_menu() {
        return persistent_menu;
    }

    public void setPersistent_menu(PersistentMenu persistent_menu) {
        this.persistent_menu = persistent_menu;
    }

    public String getGeneric_CSS_template() {
        return generic_CSS_template;
    }

    public void setGeneric_CSS_template(String generic_CSS_template) {
        this.generic_CSS_template = generic_CSS_template;
    }

    public BotVerification getBot_verification() {
        return bot_verification;
    }

    public void setBot_verification(BotVerification bot_verification) {
        this.bot_verification = bot_verification;
    }

    public BotStrategy getBot_strategy() {
        return bot_strategy;
    }

    public void setBot_strategy(BotStrategy bot_strategy) {
        this.bot_strategy = bot_strategy;
    }
}
