package com.stv.msgservice.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.stv.msgservice.AppExecutors;
import com.stv.msgservice.core.mmslib.pdu.GenericPdu;
import com.stv.msgservice.core.mmslib.pdu.PduParser;
import com.stv.msgservice.core.sms.MmsUtils;
import com.stv.msgservice.datamodel.network.NetworkUtil;
import com.stv.msgservice.datamodel.network.ParsedMsgBean;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

import static com.stv.msgservice.core.sms.MmsUtils.parseMsgPdu;

/**
 * Class that handles MMS WAP push intent from telephony on pre-KLP Devices.
 */
public class MmsWapPushReceiver extends BroadcastReceiver {
    static final String EXTRA_DATA = "data";
    private Context mContext;
    private AppCompatActivity mActivity;
    private AppExecutors mAppExecutors;
    private ConversationListViewModel mConversationListViewModel;

    public MmsWapPushReceiver() {
    }

    public MmsWapPushReceiver(AppCompatActivity activity, AppExecutors appExecutors) {
        this.mActivity = activity;
        this.mAppExecutors = appExecutors;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i("Junwang", "MmsWapPushReceiver onReceived action = "+intent.getAction()+", type="+intent.getType());
        mContext = context;
        if (/*Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION.equals(intent.getAction())
                && ContentType.MMS_MESSAGE.equals(intent.getType())*/true) {
            final byte[] data = intent.getByteArrayExtra(MmsWapPushReceiver.EXTRA_DATA);
            if(data != null) {
                try {
                    Log.i("Junwang", "MmsWapPushReceiver original pdu = " + Arrays.toString(data));
                    byte[] body = MmsUtils.processReceivedPdu(
                            context, data, -1, "10086");
                    if(body == null || body.length < 1){
                        return;
                    }
                    ParsedMsgBean msgBean = new ParsedMsgBean();
                    parseMsgPdu(body, msgBean);
                    Log.i("Junwang", "MmsWapPushReceiver parsed orderNo = " + msgBean.getOrderNo()+", parsed domain="+msgBean.getDomain());
//                    mConversationListViewModel =
//                            new ViewModelProvider(mActivity).get(ConversationListViewModel.class);
//                    mConversationListViewModel.getXml(mContext, msgBean.getOrderNo(), msgBean.getDomain());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NetworkUtil.getXml(mContext, msgBean.getOrderNo(), msgBean.getDomain());
                        }
                    }).start();
                } catch (Exception e) {
                    Log.e("Junwang", "convert byte[] to String excecption " + e.toString());
                }
            }else{
                Log.e("Junwang", "MmsWapPushReceiver pdu is null!");
            }
        }
    }

    public String parsePdu(final Context context, byte[] pdu, SmsMessage[] messages) {
        try {
            final PduParser parser = new PduParser(pdu,
                    true);
            final GenericPdu parsedPdu = parser.parse();
            if (parsedPdu != null) {
                final String from = parsedPdu.getFrom().getString();
                mAppExecutors.diskIO().execute(() -> {
                    final ContentValues messageValues =
                            MmsUtils.parseReceivedSmsMessage(messages, 0);
                    final String text = messageValues.getAsString(Telephony.Sms.BODY);
                    final byte[] body = messageValues.getAsByteArray(Telephony.Sms.BODY);
                    final long received = messageValues.getAsLong(Telephony.Sms.DATE);
                    Log.i("Junwang", "MmsWapPushReceiver parsed text = " + text);
                    Log.i("Junwang", "MmsWapPushReceiver parsed body = " + Arrays.toString(body));
                    byte[] buffer = new byte[30];
                    byte[] domainBuffer = new byte[50];
                    String orderNo = null;
                    String domain = null;
                    boolean orderStart = false;
                    boolean domainStart = false;
                    if (body != null) {
                        for (int i = 0, j = 0, k = 0; i < body.length; i++) {
                            if (body[i] == /*0x98*/-124 || orderStart) {
                                orderStart = true;
                                if(orderStart){
                                    if ((body[i+1] != 0x7C)) {
                                        buffer[j++] = body[i + 1];
                                    } else {
                                        buffer[j] = '\0';
                                        byte[] temp = Arrays.copyOfRange(buffer, 0, j);
                                        orderNo = new String(temp);
                                        Log.i("Junwang", "MmsWapPushReceiver orderNo = " + orderNo+", length="+orderNo.length());
                                        orderStart = false;
                                        domainStart = true;
                                    }
                                }
                            } else if (domainStart) {
                                if (body[i] != '\0') {
                                    domainBuffer[k++] = body[i+1];
                                } else {
                                    domainBuffer[k] = '\0';
                                    byte[] temp = Arrays.copyOfRange(domainBuffer, 0, k-1);
                                    domain = new String(temp);
                                    Log.i("Junwang", "MmsWapPushReceiver domain = " + domain+", length="+domain.length());
                                    break;
                                }
                            }
                        }
                    }
//                    mConversationListViewModel.getXml(context, text);
//                mConversationListViewModel.saveMsg(context, text, from, true,  null, null,1);

//                int convId = DataRepository.getInstance(AppDatabase.getInstance(context)).getConversationId(from);
//                Log.i("Junwang", "query conversation Id = "+ convId);
//                ConversationEntity ce = new ConversationEntity();
//                ce.setId(convId);
//                ce.setLatestMessageId(0);
//                ce.setLastTimestamp(received);
//                if(convId == 0){
//                    mConversationListViewModel.insertConversation(ce);
//                }
//
//                MessageEntity me = new MessageEntity();
//                me.setContent(text);
//                me.setReceivedTimeStamp(received);
//                mMessageViewModel.insertMessage(me);
//
//                ce.setLatestMessageId(mMessageId);
//                ce.setSnippetText(me.generateSnippetText());
//                mConversationListViewModel.updateConversation(ce);
//                mConversationListViewModel.getConversations();
                });
            }
//            byte[] data = new byte[]{-116, -126, -115, -112, -118, -1, -1, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 97, 98, 99, 100, 101, 102, 103, 104, 105, -1, -1};
//            if (data != null) {
//                int len = data.length;
//                if (len > 24 && (data[len - 1] == -1) && (data[len - 2] == -1)
//                        && (data[len - 23] == -1) && (data[len - 24] == -1)) {
//                    byte[] content = Arrays.copyOfRange(data, len - 22, len - 2);
//                    String body = new String(content);
//                    Log.i("Junwang", "pushData=" + Arrays.toString(content) + ", body=" + body);
//                    return body;
//                }
//            }

        } catch (Exception e) {
            Log.e("Junwang", "MmsWapPushReceiver parsePdu error " + e.toString());
        }
        return null;
    }
}
