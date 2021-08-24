package com.stv.msgservice.datamodel.model;

public interface UserInfo {
    String getName();
    String getPortrait();
    String getUri();
    String getMenu();
    void setName(String name);
    void setUri(String uri);
    long getLastUsedTime();
    void setLastUsedTime(long lastUsedTime);
    void setDescription(String description);
    String getDescription();
    String getPccType();
    void setPccType(String pccType);
    int isAttentioned();
    void setIsAttentioned(int attentioned);
    String getVerificationSignatures();
    void setVerificationSignatures(String verificationSignatures);
}
