package com.stv.msgservice.datamodel.network;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("deliveryInfo")
public class DeliveryInfo {
    String address;
    String messageId;
    String deliveryStatus;

    public DeliveryInfo(String address, String messageId, String deliveryStatus) {
        this.address = address;
        this.messageId = messageId;
        this.deliveryStatus = deliveryStatus;
    }
}
