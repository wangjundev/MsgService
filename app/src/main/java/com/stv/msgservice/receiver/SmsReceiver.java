package com.stv.msgservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.stv.msgservice.AppExecutors;
import com.stv.msgservice.datamodel.network.ParsedMsgBean;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.disposables.CompositeDisposable;

import static com.stv.msgservice.core.sms.MmsUtils.parseMsgPdu;

public class SmsReceiver extends BroadcastReceiver {
    static final String EXTRA_DATA = "data";
    private String pushData;
    private Context mContext;
    private AppCompatActivity mActivity;
    private AppExecutors mAppExecutors;
    private int mMessageId;
    private ConversationListViewModel mConversationListViewModel;
    private MessageViewModel mMessageViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public SmsReceiver() {
    }

    public SmsReceiver(AppCompatActivity activity, AppExecutors appExecutors) {
        this.mActivity = activity;
        this.mAppExecutors = appExecutors;
        mConversationListViewModel =
                new ViewModelProvider(mActivity).get(ConversationListViewModel.class);
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                mActivity.getApplication(), 0);

        mMessageViewModel = new ViewModelProvider(mActivity, factory)
                .get(MessageViewModel.class);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i("Junwang", "SmsReceiver onReceived");
        mContext = context;
//        parsePdu(intent, context, null, null);
//        final byte[] data = intent.getByteArrayExtra(MmsWapPushReceiver.EXTRA_DATA);
//        final android.telephony.SmsMessage[] messages = getMessagesFromIntent(intent);
//        if(data != null) {
//            try {
//                Log.i("Junwang", "SmsReceiver original pdu = " + Arrays.toString(data));
//                pushData = parsePdu(intent, context, data, messages);
//                Log.i("Junwang", "SmsReceiver pushData = " + pushData);
//            } catch (Exception e) {
//                Log.e("Junwang", "convert byte[] to String excecption " + e.toString());
//            }
//        }else{
//            Log.e("Junwang", "SmsReceiver pdu is null!");
//        }
    }

    public String getPushData(){
        return pushData;
    }

    public void parsePdu(final Intent intent, final Context context, byte[] pdu, SmsMessage[] msg){
        // ????????????????????????
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVER")){
            Log.i("Junwang", "SmsReceiver action is android.provider.Telephony.SMS_RECEIVER ");
            //????????????(??????????????????????????????????????????)
//            abortBroadcast();
            StringBuilder sb = new StringBuilder();
            //?????????SMS??????????????????
            Bundle bundle = intent.getExtras();
            //?????????????????????
            if(bundle != null){
                //??????pdus??????????????????????????????????????????
                Object[] pdus = (Object[])bundle.get("pdus");
                //??????????????????array??????????????????????????????????????????array?????????
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i = 0 ; i <pdus.length ; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }
                //??????????????????????????????????????????StringBuilder??????
                for(SmsMessage message : messages){
                    sb.append("????????????:");
                    //?????????????????????????????????
                    sb.append(message.getDisplayOriginatingAddress());
                    sb.append("\n-----????????????-----\n");
                    //?????????????????????
                    sb.append(message.getDisplayMessageBody());
                    byte[] content = message.getDisplayMessageBody().getBytes();
                    ParsedMsgBean msgBean = new ParsedMsgBean();
                    parseMsgPdu(content, msgBean);
                    Log.i("Junwang", "SmsReceiver parsed orderNo = " + msgBean.getOrderNo()+", parsed domain="+msgBean.getDomain());
                    mConversationListViewModel.getXml(mContext, msgBean.getOrderNo(), msgBean.getDomain());
                }

                if(pdus != null) {
                    try {
                        Log.i("Junwang", "SmsReceiver original pdu = " + Arrays.toString(pdus));
                        Log.i("Junwang", "SmsReceiver parsed content = " + sb);
                    } catch (Exception e) {
                        Log.e("Junwang", "convert byte[] to String excecption " + e.toString());
                    }
                }else{
                    Log.e("Junwang", "SmsReceiver pdu is null!");
                }
            }
            Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
