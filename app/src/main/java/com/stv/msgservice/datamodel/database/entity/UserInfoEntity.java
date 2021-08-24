package com.stv.msgservice.datamodel.database.entity;

import com.stv.msgservice.datamodel.model.UserInfo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "userinfos", indices = {@Index(value = "uri", unique = true)})
public class UserInfoEntity implements UserInfo {
    @PrimaryKey(autoGenerate = true)
    private long user_id;
    private String domain;
    private String uri;
    @ColumnInfo(name = "expiry_time")
    private String expiryTime;
    private String etag;
    private String json;
    private String name;
    private String menu;
    private String portrait;
    private String category;
    private String description;
    @ColumnInfo(name = "last_used_time")
    private long lastUsedTime;
    @ColumnInfo(name = "pcc_type")
    private String pccType;
    @ColumnInfo(name = "verification_signatures")
    private String verificationSignatures;
    @ColumnInfo(name = "is_attentioned")
    private int isAttentioned;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public String getPccType() {
        return pccType;
    }

    public void setPccType(String pccType) {
        this.pccType = pccType;
    }

    public String getVerificationSignatures() {
        return verificationSignatures;
    }

    public void setVerificationSignatures(String verificationSignatures) {
        this.verificationSignatures = verificationSignatures;
    }

    public int isAttentioned() {
        return isAttentioned;
    }

    public void setIsAttentioned(int isAttentioned) {
        this.isAttentioned = isAttentioned;
    }

    //    public UserInfoEntity(String name, String portrait) {
//        this.name = name;
//        this.portrait = portrait;
//    }
}
