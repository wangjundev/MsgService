package com.stv.msgservice.datamodel.network;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("serviceCapability")
public class ServiceCapability {
    private String capabilityId;
    private String version;

    public ServiceCapability(String capabilityId, String version) {
        this.capabilityId = capabilityId;
        this.version = version;
    }
}
