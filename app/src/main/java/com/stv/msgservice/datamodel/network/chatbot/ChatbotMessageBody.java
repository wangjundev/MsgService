package com.stv.msgservice.datamodel.network.chatbot;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="msg", strict = false)
public class ChatbotMessageBody {
    @Element(name="address", required = false)
    private String address;
    @Element(name="destinationAddress", required = false)
    private String destinationAddress;
    @Element(name="senderAddress", required = false)
    private String senderAddress;
    @Element(name="senderName", required = false)
    private String senderName;
    @Element(name = "outboundIMMessage", required = false)
    private OutboundIMMessage outboundIMMessage;

    @Root(name ="outboundIMMessage", strict = false)
    public static class OutboundIMMessage{
        @Element(name="subject", required = false)
        private String subject;
        @Element(name = "contentType", required = false)
        private String contentType;
        @Element(name = "conversationID", required = false)
        private String conversationID;
        @Element(name = "contributionID", required = false)
        private String contributionID;
//        @Element(name = "serviceCapability", required = false)
//        private ServiceCapability serviceCapability;
//        @Root(name = "serviceCapability", strict = false)
//        static class ServiceCapability{
//            @Element(name = "capabilityId", required = false)
//            private String capabilityId;
//            @Element(name = "version", required = false)
//            private String version;
//
//            public String getCapabilityId() {
//                return capabilityId;
//            }
//
//            public String getVersion() {
//                return version;
//            }
//        }

        @Element(name = "messageId", required = false)
        private String messageId;
        @Element(name = "bodyText", required = false)
        private String bodyText;

        public String getSubject() {
            return subject;
        }

        public String getContentType() {
            return contentType;
        }

        public String getConversationID() {
            return conversationID;
        }

        public String getContributionID() {
            return contributionID;
        }

//        public ServiceCapability getServiceCapability() {
//            return serviceCapability;
//        }

        public String getMessageId() {
            return messageId;
        }

        public String getBodyText() {
            return bodyText;
        }
    }

    @Element(name = "clientCorrelator", required = false)
    private long clientCorrelator;

    public String getAddress() {
        return address;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getSenderName() {
        return senderName;
    }

    public OutboundIMMessage getOutboundIMMessage() {
        return outboundIMMessage;
    }

    public long getClientCorrelator() {
        return clientCorrelator;
    }
}
