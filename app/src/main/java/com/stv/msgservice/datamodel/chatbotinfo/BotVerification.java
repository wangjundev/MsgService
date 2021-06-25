package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class BotVerification {
    @JSONField(name="verification-info")
    private VerificationInfo verification_info;

    public VerificationInfo getVerification_info() {
        return verification_info;
    }

    public void setVerification_info(VerificationInfo verification_info) {
        this.verification_info = verification_info;
    }
}
