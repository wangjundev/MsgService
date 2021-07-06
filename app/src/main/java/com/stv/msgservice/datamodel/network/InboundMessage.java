package com.stv.msgservice.datamodel.network;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("inboundMessage")
public class InboundMessage {
    private String destinationAddress;
    private String senderAddress;
//    private String origUser;
    private String imFormat;
    private String messageId;
    private String bodyText;
    private String contentType;
    private String contentEncoding;
    private ServiceCapability serviceCapability;
    private String conversationID;
    private String contributionId;

    public InboundMessage(String destinationAddress, String senderAddress, String imFormat, String messageId, String bodyText, String contentType, String contentEncoding, ServiceCapability serviceCapability, String conversationID, String contributionId) {
        this.destinationAddress = destinationAddress;
        this.senderAddress = senderAddress;
//        this.origUser = senderAddress;
        this.imFormat = imFormat;
        this.messageId = messageId;
        this.bodyText = bodyText;
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
        this.serviceCapability = serviceCapability;
        this.conversationID = conversationID;
        this.contributionId = contributionId;
    }
}
