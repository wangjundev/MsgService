package com.stv.msgservice.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.stv.msgservice.AppExecutors;
import com.stv.msgservice.core.mmslib.ContentType;
import com.stv.msgservice.core.mmslib.pdu.GenericPdu;
import com.stv.msgservice.core.mmslib.pdu.PduParser;
import com.stv.msgservice.core.sms.MmsUtils;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.disposables.CompositeDisposable;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

/**
 * Class that handles MMS WAP push intent from telephony on pre-KLP Devices.
 */
public class MmsWapPushReceiver extends BroadcastReceiver {
    static final String EXTRA_DATA = "data";
    private String pushData;
    private Context mContext;
    private AppCompatActivity mActivity;
    private AppExecutors mAppExecutors;
    private int mMessageId;
    private ConversationListViewModel mConversationListViewModel;
    private MessageViewModel mMessageViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public MmsWapPushReceiver(AppCompatActivity activity, AppExecutors appExecutors) {
        this.mActivity = activity;
        this.mAppExecutors = appExecutors;
        mConversationListViewModel =
                new ViewModelProvider(mActivity).get(ConversationListViewModel.class);
//        mMessageViewModel =
//                new ViewModelProvider(mActivity).get(MessageViewModel.class);
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                mActivity.getApplication(), 0);

        mMessageViewModel = new ViewModelProvider(mActivity, factory)
                .get(MessageViewModel.class);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        mContext = context;
        if (Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION.equals(intent.getAction())
                && ContentType.MMS_MESSAGE.equals(intent.getType())) {
            final byte[] data = intent.getByteArrayExtra(MmsWapPushReceiver.EXTRA_DATA);
            final android.telephony.SmsMessage[] messages = getMessagesFromIntent(intent);
            if(data != null) {
                try {
                    Log.i("Junwang", "MmsWapPushReceiver original pdu = " + Arrays.toString(data));
                    pushData = parsePdu(context, data, messages);
                    Log.i("Junwang", "MmsWapPushReceiver pushData = " + pushData);
                } catch (Exception e) {
                    Log.e("Junwang", "convert byte[] to String excecption " + e.toString());
                }
            }else{
                Log.e("Junwang", "MmsWapPushReceiver pdu is null!");
            }
        }
    }

    public String getPushData(){
        return pushData;
    }

    public String parsePdu(final Context context, byte[] pdu, SmsMessage[] messages){
        final PduParser parser = new PduParser(pdu,
                true);
        final GenericPdu parsedPdu = parser.parse();
        if(parsedPdu != null){
            final String from = parsedPdu.getFrom().getString();
            mAppExecutors.diskIO().execute(() -> {
                final ContentValues messageValues =
                        MmsUtils.parseReceivedSmsMessage(messages, 0);
                final String text = messageValues.getAsString(Telephony.Sms.BODY);
                final long received = messageValues.getAsLong(Telephony.Sms.DATE);

                mConversationListViewModel.saveMsg(context, text, from, true,  null,1);

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
        byte[] data = new byte[]{-116, -126, -115, -112, -118, -1, -1, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 97, 98, 99, 100, 101, 102, 103, 104, 105, -1, -1};
        if(data != null){
            int len = data.length;
            if(len > 24 && (data[len-1]==-1) && (data[len-2]== -1)
                    && (data[len-23]==-1) && (data[len-24]==-1)){
                byte[] content = Arrays.copyOfRange(data, len-22, len-2);
                String body = new String(content);
                Log.i("Junwang", "pushData="+ Arrays.toString(content)+", body="+body);
                return body;
            }
        }
        return null;
    }
}
