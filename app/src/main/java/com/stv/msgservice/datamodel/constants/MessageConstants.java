package com.stv.msgservice.datamodel.constants;

public class MessageConstants {
    public static final int DIRECTION_OUT = 0;
    public static final int DIRECTION_IN = 1;
    // Bugle STATUS Values
    public static final int BUGLE_STATUS_UNKNOWN = 0;

    // Outgoing
    public static final int BUGLE_STATUS_OUTGOING_COMPLETE                = 1;
    public static final int BUGLE_STATUS_OUTGOING_DELIVERED               = 2;
    // Transitions to either YET_TO_SEND or SEND_AFTER_PROCESSING depending attachments.
    public static final int BUGLE_STATUS_OUTGOING_DRAFT                   = 3;
    public static final int BUGLE_STATUS_OUTGOING_YET_TO_SEND             = 4;
    public static final int BUGLE_STATUS_OUTGOING_SENDING                 = 5;
    public static final int BUGLE_STATUS_OUTGOING_RESENDING               = 6;
    public static final int BUGLE_STATUS_OUTGOING_AWAITING_RETRY          = 7;
    public static final int BUGLE_STATUS_OUTGOING_FAILED                  = 8;
    public static final int BUGLE_STATUS_OUTGOING_FAILED_EMERGENCY_NUMBER = 9;

    // Incoming
    public static final int BUGLE_STATUS_INCOMING_COMPLETE                   = 100;
    public static final int BUGLE_STATUS_INCOMING_DOWNLOAD_FAILED            = 101;
    public static final int BUGLE_STATUS_INCOMING_DOWNLOADING                = 102;

    public static final int CONTENT_TYPE_TEXT                   = 200;
    public static final int CONTENT_TYPE_IMAGE                  = 201;
    public static final int CONTENT_TYPE_VIDEO                  = 202;
    public static final int CONTENT_TYPE_AUDIO                  = 203;
    public static final int CONTENT_TYPE_LOCATION               = 204;
    public static final int CONTENT_TYPE_FILE                   = 205;
    public static final int CONTENT_TYPE_AVCHAT                 = 206;
    public static final int CONTENT_TYPE_NOTIFICATION           = 207;
    public static final int CONTENT_TYPE_SINGLE_CARD            = 208;
    public static final int CONTENT_TYPE_MULTI_CARD             = 209;
    public static final int CONTENT_TYPE_TEXT_WITH_SUGGESTION   = 210;
    public static final int CONTENT_TYPE_IMAGE_WITH_SUGGESTION  = 211;
    public static final int CONTENT_TYPE_VIDEO_WITH_SUGGESTION  = 212;
    public static final int CONTENT_TYPE_AUDIO_WITH_SUGGESTION  = 213;
    public static final int CONTENT_TYPE_LOCATION_WITH_SUGGESTION = 214;
    public static final int CONTENT_TYPE_FILE_WITH_SUGGESTION   = 215;
    public static final int CONTENT_TYPE_AVCHAT_WITH_SUGGESTION = 216;
    public static final int CONTENT_TYPE_NOTIFICATION_WITH_SUGGESTION = 217;
    public static final int CONTENT_TYPE_SINGLE_CARD_WITH_SUGGESTION  = 218;
    public static final int CONTENT_TYPE_MULTI_CARD_WITH_SUGGESTION   = 219;
    public static final int CONTENT_TYPE_UNKNOWN                = 220;

    public static final String BASE_URL = "http://callback.supermms.cn/";//"http://47.103.127.125:8890";//http://192.168.5.127:3000/";
    public static final String UPLOAD_URL = "/upload/file";    //上传单文件
    public static final String UPLOADS_URL = "/upload/files";

    public static final String DELIVEREDTOTERMINAL = "DeliveredToTerminal";
    public static final String DELIVEREDFAILED = "DeliveryImpossible";//"DeliveredFailed";
    public static final String MESSAGEDISPLAYED = "MessageDisplayed";
    public static final String MESSAGEREADREPORTFAILED = "MessageReadReportFailed";

    //原生action类型
    public static class NativeActionType{
        public static final int PHONE_CALL              = 1;
        public static final int SEND_MSG                = 2;
        public static final int TAKE_PICTURE            = 3;
        public static final int TAKE_VIDEO              = 4;
        public static final int COPY                    = 5;
        public static final int OPEN_LOCATION           = 6;
        public static final int CALENDAR                = 7;
        public static final int READ_CONTACT            = 8;
    }
}
