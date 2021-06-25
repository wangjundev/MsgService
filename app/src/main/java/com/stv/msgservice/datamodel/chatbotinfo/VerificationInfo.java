package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class VerificationInfo {
    @JSONField(name = "verification-signatures")
    private VerificationSignatures verification_signatures;
    public void setVerificationSignatures(VerificationSignatures verification_signatures) {
        this.verification_signatures = verification_signatures;
    }
    public VerificationSignatures getVerificationSignatures() {
        return verification_signatures;
    }
}
