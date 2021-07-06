package com.stv.msgservice.datamodel.network;

//@Namespace(reference = "urn:oma:xml:rest:netapi:messaging:1", prefix = "xmlns:msg")
//@XStreamAlias("deliveryInfoNotification")
public class DeliveryInfoNotification {
    DeliveryInfo deliveryInfo;

    public DeliveryInfoNotification(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }
}
