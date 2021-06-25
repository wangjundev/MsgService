package com.stv.msgservice.datamodel.chatbotinfo;

public class Botinfo {
    private Pcc pcc;
    private String version;
    private String provider;
    private String email;
    private String website;
    private String TCPage;
    private String colour;
    private String backgroundImage;
    private Address address;

    public Pcc getPcc() {
        return pcc;
    }

    public void setPcc(Pcc pcc) {
        this.pcc = pcc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTCPage() {
        return TCPage;
    }

    public void setTCPage(String TCPage) {
        this.TCPage = TCPage;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
