package com.stv.msgservice.core.sms;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Telephony.Sms;
import android.telephony.SmsMessage;

/**
 * Utils for sending sms/mms messages.
 */
public class MmsUtils {
    private final static String TAG = "Junwang";
    public static final Uri MMS_PART_CONTENT_URI = Uri.parse("content://mms/part");

    /**
     * Parse values from a received sms message
     *
     * @param context
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
}
