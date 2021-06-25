package com.stv.msgservice.datamodel.network;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("deliveryInfoNotification")
public class DeliveryInfoNotification {
    DeliveryInfo deliveryInfo;

    public DeliveryInfoNotification(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }
}
