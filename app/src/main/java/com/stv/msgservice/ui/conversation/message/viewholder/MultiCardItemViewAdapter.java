package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.network.chatbot.CardContent;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionAction;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionActionWrapper;
import com.stv.msgservice.ui.WfcWebViewActivity;
import com.stv.msgservice.utils.NativeFunctionUtil;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MultiCardItemViewAdapter extends RecyclerView.Adapter {
    private List<CardContent> lists;
    private Context context;
    private int resource;
    private View itemView;

    public MultiCardItemViewAdapter(List<CardContent> lists, Context context, int resource) {
        this.lists = lists;
        this.context = context;
        this.resource = resource;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private ImageView iv;
        private TextView tvDescription;
        private TextView tvTitle;
        private LinearLayout layout;
        public MyHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.image);
            tvDescription = (TextView) itemView.findViewById(R.id.product_description);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
            layout = (LinearLayout)itemView.findViewById(R.id.multicard_layout);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(/*R.layout.chatbot_multicard_itemview*/resource,parent,false);
        MyHolder holder =new MyHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String title = lists.get(position).getTitle();
        String description = lists.get(position).getDescription();
        Log.i("Junwang", "multicard title="+title+", description="+description);
        Glide.with(context).load(lists.get(position).getMedia().getThumbnailUrl())
                .centerCrop()
                .into(((MyHolder)holder).iv);
        ((MyHolder)holder).iv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                WebViewNewsActivity.start(context,lists.get(position).getSuggestionActionWrapper()[position].getReply().getPostback().getData()/*getButtonAction()*/);
            }
        });
        ((MyHolder)holder).tvTitle.setText(title);
        ((MyHolder)holder).tvDescription.setText(description);
        if(lists.get(position).getSuggestionActionWrapper() != null
                && lists.get(position).getSuggestionActionWrapper().length > 0){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
//            View text_suggestion_view = layoutInflater.inflate(R.layout.item_chatbot_text_suggestions_layout, null);
            setSuggestionsView(context, lists.get(position), ((MyHolder)holder).layout);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            if(true){
//                ((MyHolder)holder).layout.addView(text_suggestion_view,3, lp);
//            }else{
//                ((MyHolder)holder).layout.addView(text_suggestion_view,0, lp);
//            }
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private void setSuggestionsView(Context context, CardContent cardcontent, LinearLayout layout){
        SuggestionActionWrapper[] saw = cardcontent.getSuggestionActionWrapper();
        if(saw != null && saw.length>0){
            if(layout.getChildCount() > 3){
                return;
            }
            int i = 0;
            TextView tv1;
            Log.i("Junwang", "setSuggestionsView length="+saw.length);
            for(; i<saw.length; i++){
                if(saw[i].action != null) {
                    tv1 = new TextView(context);
                    tv1.setText(saw[i].action.displayText);
                    tv1.setBackgroundResource(R.drawable.border_textview);
                    tv1.setGravity(Gravity.CENTER);
                    tv1.setPadding(0, 15, 0, 15);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(100,12,100,12);
//                    cardLayout.addView(tv1, j, lp);
                    Log.i("Junwang", "add view i="+i);
                    layout.addView(tv1, lp);
                    SuggestionAction sa = saw[i].action;
                    if ((sa != null) && (sa.urlAction != null)) {
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                WfcWebViewActivity.loadUrl(context, "", sa.urlAction.openUrl.url);
//                                WebViewNewsActivity.start(context, sa.urlAction.openUrl.url);
                            }
                        });
                    }else if((sa != null) && (sa.dialerAction != null)){
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                NativeFunctionUtil.callNativeFunction(MessageConstants.NativeActionType.PHONE_CALL, context,
                                        null, null, sa.dialerAction.dialPhoneNumber.phoneNumber);
                            }
                        });
                    }else if((sa != null) && (sa.mapAction != null)){
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                NativeFunctionUtil.openLocation(sa.mapAction.showLocation.location.label, sa.mapAction.showLocation.location.latitude,
                                        sa.mapAction.showLocation.location.longitude, context);
                            }
                        });
                    }
                }
                if(saw[i].reply != null){
                    tv1 = new TextView(context);
                    tv1.setText(saw[i].reply.displayText);
                    tv1.setBackgroundResource(R.drawable.border_textview);
                    tv1.setGravity(Gravity.CENTER);
                    tv1.setPadding(0, 15, 0, 15);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(100,12,100,12);
//                    cardLayout.addView(tv1, j, lp);
                    Log.i("Junwang", "add view1 i="+i);
                    layout.addView(tv1, lp);
                }
            }
        }
    }

//    private void setSuggestionsView(Context context, CardContent cardcontent, View text_suggestion_view){
//        SuggestionActionWrapper[] saw = cardcontent.getSuggestionActionWrapper();
//        if(saw != null && saw.length>0){
//            text_suggestion_view.findViewById(R.id.hor_title).setVisibility(View.GONE);
//            text_suggestion_view.findViewById(R.id.hor_description).setVisibility(View.GONE);
//            LinearLayout ll1 = (LinearLayout)text_suggestion_view.findViewById(R.id.text_suggestion_layout);
//            int i = 0;
//            int j = 2;
//            TextView tv1;
//            for(; i<saw.length; i++, j++){
//                if(saw[i].action != null) {
//                    tv1 = new TextView(context);
//                    tv1.setText(saw[i].action.displayText);
//                    tv1.setBackgroundResource(R.drawable.border_textview);
//                    tv1.setGravity(Gravity.CENTER);
//                    tv1.setPadding(0, 15, 0, 15);
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    lp.setMargins(100,12,100,12);
//                    ll1.addView(tv1, j, lp);
//                    SuggestionAction sa = saw[i].action;
//                    if ((sa != null) && (sa.urlAction != null)) {
//                        tv1.setOnClickListener(new View.OnClickListener(){
//                            @Override
//                            public void onClick(View v) {
//                                WebViewNewsActivity.start(context, sa.urlAction.openUrl.url);
//                            }
//                        });
//                    }else if((sa != null) && (sa.dialerAction != null)){
//                        tv1.setOnClickListener(new View.OnClickListener(){
//                            @Override
//                            public void onClick(View v) {
//                                NativeFunctionUtil.callNativeFunction(MessageConstants.NativeActionType.PHONE_CALL, context,
//                                        null, null, sa.dialerAction.dialPhoneNumber.phoneNumber);
//                            }
//                        });
//                    }else if((sa != null) && (sa.mapAction != null)){
//                        tv1.setOnClickListener(new View.OnClickListener(){
//                            @Override
//                            public void onClick(View v) {
//                                NativeFunctionUtil.openLocation(sa.mapAction.showLocation.location.label, sa.mapAction.showLocation.location.latitude,
//                                        sa.mapAction.showLocation.location.longitude, context);
//                            }
//                        });
//                    }
//                }
//                if(saw[i].reply != null){
//                    tv1 = new TextView(context);
//                    tv1.setText(saw[i].reply.displayText);
//                    tv1.setBackgroundResource(R.drawable.border_textview);
//                    tv1.setGravity(Gravity.CENTER);
//                    tv1.setPadding(0, 15, 0, 15);
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    lp.setMargins(100,12,100,12);
//                    ll1.addView(tv1, j, lp);
//                }
//            }
//        }
//    }
}
