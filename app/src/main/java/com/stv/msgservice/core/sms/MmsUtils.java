package com.stv.msgservice.core.sms;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.telephony.SmsMessage;
import android.util.Log;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.stv.msgservice.core.mmslib.pdu.GenericPdu;
import com.stv.msgservice.core.mmslib.pdu.NotificationInd;
import com.stv.msgservice.core.mmslib.pdu.PduHeaders;
import com.stv.msgservice.core.mmslib.pdu.PduParser;
import com.stv.msgservice.datamodel.network.ParsedMsgBean;

import java.util.Arrays;

/**
 * Utils for sending sms/mms messages.
 */
public class MmsUtils {
    private final static String TAG = "Junwang";
    public static final Uri MMS_PART_CONTENT_URI = Uri.parse("content://mms/part");

    /**
     * Parse values from a received sms message
     *
     * @param msgs The received sms message content
     * @param error The received sms error
     * @return Parsed values from the message
     */
    public static ContentValues parseReceivedSmsMessage(
            final SmsMessage[] msgs, final int error) {
        final SmsMessage sms = msgs[0];
        final ContentValues values = new ContentValues();

        values.put(Sms.ADDRESS, sms.getDisplayOriginatingAddress());
        values.put(Sms.BODY, buildMessageBodyFromPdus(msgs));
//        if (MmsUtils.hasSmsDateSentColumn()) {
//            // TODO:: The boxing here seems unnecessary.
//            values.put(Sms.DATE_SENT, Long.valueOf(sms.getTimestampMillis()));
//        }
        values.put(Sms.PROTOCOL, sms.getProtocolIdentifier());
        if (sms.getPseudoSubject().length() > 0) {
            values.put(Sms.SUBJECT, sms.getPseudoSubject());
        }
        values.put(Sms.REPLY_PATH_PRESENT, sms.isReplyPathPresent() ? 1 : 0);
        values.put(Sms.SERVICE_CENTER, sms.getServiceCenterAddress());
        // Error code
        values.put(Sms.ERROR_CODE, error);

        return values;
    }

    // Some providers send formfeeds in their messages. Convert those formfeeds to newlines.
    private static String replaceFormFeeds(final String s) {
        return s == null ? "" : s.replace('\f', '\n');
    }

    // Parse the message body from message PDUs
    private static String buildMessageBodyFromPdus(final SmsMessage[] msgs) {
        if (msgs.length == 1) {
            // There is only one part, so grab the body directly.
            return replaceFormFeeds(msgs[0].getDisplayMessageBody());
        } else {
            // Build up the body from the parts.
            final StringBuilder body = new StringBuilder();
            for (final SmsMessage msg : msgs) {
                try {
                    // getDisplayMessageBody() can NPE if mWrappedMessage inside is null.
                    body.append(msg.getDisplayMessageBody());
                } catch (final NullPointerException e) {
                    // Nothing to do
                }
            }
            return replaceFormFeeds(body.toString());
        }
    }

    /**
     * Parse the message row id from a message Uri.
     *
     * @param messageUri The input Uri
     * @return The message row id if valid, otherwise -1
     */
    public static long parseRowIdFromMessageUri(final Uri messageUri) {
        try {
            if (messageUri != null) {
                return ContentUris.parseId(messageUri);
            }
        } catch (final UnsupportedOperationException e) {
            // Nothing to do
        } catch (final NumberFormatException e) {
            // Nothing to do
        }
        return -1;
    }

    // Selection for new dedup algorithm:
    // ((m_type<>130) OR (exp>NOW)) AND (date>NOW-7d) AND (date<NOW+7d) AND (ct_l=xxxxxx)
    // i.e. If it is NotificationInd and not expired or not NotificationInd
    //      AND message is received with +/- 7 days from now
    //      AND content location is the input URL
    private static final String DUP_NOTIFICATION_QUERY_SELECTION =
            "((" + Telephony.Mms.MESSAGE_TYPE + "<>?) OR (" + Telephony.Mms.EXPIRY + ">?)) AND ("
                    + Telephony.Mms.DATE + ">?) AND (" + Telephony.Mms.DATE + "<?) AND (" + Telephony.Mms.CONTENT_LOCATION +
                    "=?)";
    // Selection for old behavior: only checks NotificationInd and its content location
    private static final String DUP_NOTIFICATION_QUERY_SELECTION_OLD =
            "(" + Telephony.Mms.MESSAGE_TYPE + "=?) AND (" + Telephony.Mms.CONTENT_LOCATION + "=?)";
    private static final int MAX_RETURN = 32;

    private static String[] getDupNotifications(final Context context, final NotificationInd nInd) {
        final byte[] rawLocation = nInd.getContentLocation();
        if (rawLocation != null) {
            final String location = new String(rawLocation);
            // We can not be sure if the content location of an MMS is globally and historically
            // unique. So we limit the dedup time within the last 7 days
            // (or configured by gservices remotely). If the same content location shows up after
            // that, we will download regardless. Duplicated message is better than no message.
            String selection;
            String[] selectionArgs;
//            final long timeLimit = BugleGservices.get().getLong(
//                    BugleGservicesKeys.MMS_WAP_PUSH_DEDUP_TIME_LIMIT_SECS,
//                    BugleGservicesKeys.MMS_WAP_PUSH_DEDUP_TIME_LIMIT_SECS_DEFAULT);
//            if (timeLimit > 0) {
//                // New dedup algorithm
//                selection = DUP_NOTIFICATION_QUERY_SELECTION;
//                final long nowSecs = System.currentTimeMillis() / 1000;
//                final long timeLowerBoundSecs = nowSecs - timeLimit;
//                // Need upper bound to protect against clock change so that a message has a time
//                // stamp in the future
//                final long timeUpperBoundSecs = nowSecs + timeLimit;
//                selectionArgs = new String[] {
//                        Integer.toString(PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND),
//                        Long.toString(nowSecs),
//                        Long.toString(timeLowerBoundSecs),
//                        Long.toString(timeUpperBoundSecs),
//                        location
//                };
//            } else
                {
                // If time limit is 0, we revert back to old behavior in case the new
                // dedup algorithm behaves badly
                selection = DUP_NOTIFICATION_QUERY_SELECTION_OLD;
                selectionArgs = new String[] {
                        Integer.toString(PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND),
                        location
                };
            }
            Cursor cursor = null;
            try {
                cursor = SqliteWrapper.query(
                        context, context.getContentResolver(),
                        Telephony.Mms.CONTENT_URI, new String[] { Telephony.Mms._ID },
                        selection, selectionArgs, null);
                final int dupCount = cursor.getCount();
                if (dupCount > 0) {
                    // We already received the same notification before.
                    // Don't want to return too many dups. It is only for debugging.
                    final int returnCount = dupCount < MAX_RETURN ? dupCount : MAX_RETURN;
                    final String[] dups = new String[returnCount];
                    for (int i = 0; cursor.moveToNext() && i < returnCount; i++) {
                        dups[i] = cursor.getString(0);
                    }
                    return dups;
                }
            } catch (final SQLiteException e) {
                LogUtil.e(TAG, "query failure: " + e);
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public static void parseMsgPdu(byte[] body, ParsedMsgBean parsedMsgBean){
        boolean domainStart = false;
        int domainStartIndex = 0;
        if(body != null && body.length > 0){
            for(int i=0; i<body.length; i++){
                if(!domainStart){
                    if (body[i] != 0x7C) {
                        continue;
                    }else{
                        byte[] temp = Arrays.copyOfRange(body, 0, i-1);
                        parsedMsgBean.setOrderNo(new String(temp));
                        domainStartIndex = i+1;
                        Log.i("Junwang", "MmsWapPushReceiver orderNo = " + parsedMsgBean.getOrderNo());
                        domainStart = true;
                    }
                }else{
                    byte[] temp = Arrays.copyOfRange(body, domainStartIndex, body.length);
                    if(temp == null || temp.length < 1){
                        parsedMsgBean.setDomain("callback.supermms.cn");
                    }else {
                        parsedMsgBean.setDomain(new String(temp));
                    }
                    Log.i("Junwang", "MmsWapPushReceiver domain = " + parsedMsgBean.getDomain());
                    break;
                }
            }
        }
    }

    public static /*DatabaseMessages.MmsMessage*/byte[] processReceivedPdu(final Context context,
                                                                 final byte[] pushData, final int subId, final String subPhoneNumber) {
        // Parse data

        // Insert placeholder row to telephony and local db
        // Get raw PDU push-data from the message and parse it
        final PduParser parser = new PduParser(pushData,
                true);
        final GenericPdu pdu = parser.parse();
        if(pdu == null){
            return parser.getContentValue();
        }else{
            return null;
        }


//        if (null == pdu) {
//            LogUtil.e(TAG, "Invalid PUSH data");
////            return null;
//            return;
//        }
//
//        final PduPersister p = PduPersister.getPduPersister(context);
//        final int type = pdu.getMessageType();
//
//        Uri messageUri = null;
//        switch (type) {
//            case PduHeaders.MESSAGE_TYPE_DELIVERY_IND:
//            case PduHeaders.MESSAGE_TYPE_READ_ORIG_IND: {
//                // TODO: Should this be commented out?
////                threadId = findThreadId(context, pdu, type);
////                if (threadId == -1) {
////                    // The associated SendReq isn't found, therefore skip
////                    // processing this PDU.
////                    break;
////                }
//
////                Uri uri = p.persist(pdu, Inbox.CONTENT_URI, true,
////                        MessagingPreferenceActivity.getIsGroupMmsEnabled(mContext), null);
////                // Update thread ID for ReadOrigInd & DeliveryInd.
////                ContentValues values = new ContentValues(1);
////                values.put(Mms.THREAD_ID, threadId);
////                SqliteWrapper.update(mContext, cr, uri, values, null, null);
//                LogUtil.i(TAG, "Received unsupported WAP Push, type=" + type);
//                break;
//            }
//            case PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND: {
//                final NotificationInd nInd = (NotificationInd) pdu;
//
//                if (/*MmsConfig.get(subId).getTransIdEnabled()*/true) {
//                    final byte [] contentLocationTemp = nInd.getContentLocation();
//                    if ('=' == contentLocationTemp[contentLocationTemp.length - 1]) {
//                        final byte [] transactionIdTemp = nInd.getTransactionId();
//                        final byte [] contentLocationWithId =
//                                new byte [contentLocationTemp.length
//                                        + transactionIdTemp.length];
//                        System.arraycopy(contentLocationTemp, 0, contentLocationWithId,
//                                0, contentLocationTemp.length);
//                        System.arraycopy(transactionIdTemp, 0, contentLocationWithId,
//                                contentLocationTemp.length, transactionIdTemp.length);
//                        nInd.setContentLocation(contentLocationWithId);
//                    }
//                }
//                final String[] dups = getDupNotifications(context, nInd);
//                if (dups == null) {
//                    // TODO: Do we handle Rfc822 Email Addresses?
//                    //final String contentLocation =
//                    //        MmsUtils.bytesToString(nInd.getContentLocation(), "UTF-8");
//                    //final byte[] transactionId = nInd.getTransactionId();
//                    //final long messageSize = nInd.getMessageSize();
//                    //final long expiry = nInd.getExpiry();
//                    //final String transactionIdString =
//                    //        MmsUtils.bytesToString(transactionId, "UTF-8");
//
//                    //final EncodedStringValue fromEncoded = nInd.getFrom();
//                    // An mms ind received from email address will have from address shown as
//                    // "John Doe <johndoe@foobar.com>" but the actual received message will only
//                    // have the email address. So let's try to parse the RFC822 format to get the
//                    // real email. Otherwise we will create two conversations for the MMS
//                    // notification and the actual MMS message if auto retrieve is disabled.
//                    //final String from = parsePotentialRfc822EmailAddress(
//                    //        fromEncoded != null ? fromEncoded.getString() : null);
//
//                    Uri inboxUri = null;
//                    try {
//                        inboxUri = p.persist(pdu, Telephony.Mms.Inbox.CONTENT_URI, subId, subPhoneNumber,
//                                null);
//                        messageUri = ContentUris.withAppendedId(Telephony.Mms.CONTENT_URI,
//                                ContentUris.parseId(inboxUri));
//                    } catch (final MmsException e) {
//                        LogUtil.e(TAG, "Failed to save the data from PUSH: type=" + type + e.toString());
//                    }
//                } else {
//                    LogUtil.e(TAG, "Received WAP Push is a dup: ");
//                }
//                break;
//            }
//            default:
//                LogUtil.e(TAG, "Received unrecognized WAP Push, type=" + type);
//        }
//
////        DatabaseMessages.MmsMessage mms = null;
//        if (messageUri != null) {
////            mms = MmsUtils.loadMms(messageUri);
//            Log.i("Junwang", "mms uri="+messageUri);
//        }
////        return mms;
    }
}
