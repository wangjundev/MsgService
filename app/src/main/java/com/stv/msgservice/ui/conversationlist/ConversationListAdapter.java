package com.stv.msgservice.ui.conversationlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.stv.msgservice.R;
import com.stv.msgservice.annotation.ConversationContextMenuItem;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.ui.conversationlist.viewholder.ConversationViewHolder;
import com.stv.msgservice.ui.conversationlist.viewholder.SingleConversationViewHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ConversationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Fragment fragment;

    public List<ConversationEntity> conversationInfos; //= new ArrayList<>();
//    private List<StatusNotification> statusNotifications;

    public ConversationListAdapter(Fragment context) {
        super();
        this.fragment = context;
    }

    private boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

//    public void updateStatusNotification(List<StatusNotification> statusNotifications) {
//        submit(statusNotifications, this.conversationInfos);
//    }

    private int headerCount() {
        return /*isEmpty(this.statusNotifications) ? 0 : 1*/0;
    }

    public void setConversationInfos(List<ConversationEntity> converInfos) {
//        submit(this.statusNotifications, conversationInfos);
//        if(converInfos != null || (converInfos.size() == 0)){
//            if (conversationInfos == null) {
//                Log.i("Junwang", "conversationInfos == null");
//                this.conversationInfos = converInfos;
////                notifyDataSetChanged();
//                notifyItemRangeInserted(0, conversationInfos.size());
//            } else {
//                DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
//                    @Override
//                    public int getOldListSize() {
//                        return conversationInfos.size();
//                    }
//
//                    @Override
//                    public int getNewListSize() {
//                        return conversationInfos.size();
//                    }
//
//                    @Override
//                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//                        return (converInfos.get(oldItemPosition).getId() ==
//                                conversationInfos.get(newItemPosition).getId());
////                                && (uiMessages.get(oldItemPosition).message.getMessageStatus() == uiMessageList.get(newItemPosition).message.getMessageStatus());
//                    }
//
//                    @Override
//                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//                        ConversationEntity newConversation = converInfos.get(newItemPosition);
//                        ConversationEntity oldConversation = conversationInfos.get(oldItemPosition);
//                        return newConversation.getId() == oldConversation.getId()
//                                && newConversation.getLatestMessageId() == oldConversation.getLatestMessageId()
//                                && newConversation.getLatestMessageStatus() == oldConversation.getLatestMessageStatus()
//                                && newConversation.getSnippetText() == oldConversation.getSnippetText()
//                                && newConversation.getDraftSnippetText() == oldConversation.getDraftSnippetText();
//                    }
//                });
//                this.conversationInfos = converInfos;
//                result.dispatchUpdatesTo(this);
//            }
//        }
        this.conversationInfos = converInfos;
        notifyDataSetChanged();
    }

//    private void submit(List<StatusNotification> notifications, List<ConversationInfo> conversationInfos) {
//        this.statusNotifications = notifications;
//        this.conversationInfos = conversationInfos;
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == R.layout.conversationlist_item_notification_container) {
//            View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.conversationlist_item_notification_container, parent, false);
//            return new StatusNotificationContainerViewHolder(view);
//        }
//        Class<? extends ConversationViewHolder> viewHolderClazz = ConversationViewHolderManager.getInstance().getConversationContentViewHolder(viewType);

//        try {
//            Constructor constructor = viewHolderClazz.getConstructor(Fragment.class, RecyclerView.Adapter.class, View.class);
//            ConversationViewHolder viewHolder = (ConversationViewHolder) constructor.newInstance(fragment, this, itemView);
//            processConversationClick(viewHolder, itemView);
//            processConversationLongClick(viewHolderClazz, viewHolder, itemView);
//            return viewHolder;
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
        View itemView;
        itemView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.conversationlist_item_conversation, parent, false);
        SingleConversationViewHolder viewHolder = new SingleConversationViewHolder(fragment, this, itemView);
        processConversationClick(viewHolder, itemView);
//        processConversationLongClick(SingleConversationViewHolder.class, viewHolder, itemView);
        return viewHolder;
    }

    private void processConversationClick(ConversationViewHolder viewHolder, View itemView) {
        itemView.setOnClickListener(viewHolder::onClick);
    }

    private static class ContextMenuItemWrapper {
        ConversationContextMenuItem contextMenuItem;
        Method method;

        public ContextMenuItemWrapper(ConversationContextMenuItem contextMenuItem, Method method) {
            this.contextMenuItem = contextMenuItem;
            this.method = method;
        }
    }


    private void processConversationLongClick(Class<? extends ConversationViewHolder> viewHolderClazz, ConversationViewHolder viewHolder, View itemView) {
        if (!viewHolderClazz.isAnnotationPresent(EnableContextMenu.class)) {
            return;
        }
        View.OnLongClickListener listener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Method[] allMethods = viewHolderClazz.getDeclaredMethods();
                List<ContextMenuItemWrapper> contextMenus = new ArrayList<>();
                for (final Method method : allMethods) {
                    if (method.isAnnotationPresent(ConversationContextMenuItem.class)) {
                        contextMenus.add(new ContextMenuItemWrapper(method.getAnnotation(ConversationContextMenuItem.class), method));
                    }
                }
                // handle annotated method in ConversationViewHolder
                allMethods = ConversationViewHolder.class.getDeclaredMethods();
                for (final Method method : allMethods) {
                    if (method.isAnnotationPresent(ConversationContextMenuItem.class)) {
                        contextMenus.add(new ContextMenuItemWrapper(method.getAnnotation(ConversationContextMenuItem.class), method));
                    }
                }

                if (contextMenus.isEmpty()) {
                    return false;
                }

                int position = viewHolder.getAdapterPosition();
                Conversation conversationInfo = conversationInfos.get(position - headerCount());
                Iterator<ContextMenuItemWrapper> iterator = contextMenus.iterator();
                ConversationContextMenuItem item;
                while (iterator.hasNext()) {
                    item = iterator.next().contextMenuItem;
                    if (viewHolder.contextMenuItemFilter(conversationInfo, item.tag())) {
                        iterator.remove();
                    }
                }

                if (contextMenus.isEmpty()) {
                    return false;
                }
                Collections.sort(contextMenus, (o1, o2) -> o1.contextMenuItem.priority() - o2.contextMenuItem.priority());
                List<String> titles = new ArrayList<>(contextMenus.size());
                for (ContextMenuItemWrapper itemWrapper : contextMenus) {
                    titles.add(viewHolder.contextMenuTitle(fragment.getContext(), itemWrapper.contextMenuItem.tag()));
                }
                new MaterialDialog.Builder(fragment.getContext()).items(titles).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View v, int position, CharSequence text) {
                        try {
                            ContextMenuItemWrapper menuItem = contextMenus.get(position);
                            if (menuItem.contextMenuItem.confirm()) {
                                String content;
                                content = viewHolder.contextConfirmPrompt(fragment.getContext(), menuItem.contextMenuItem.tag());
                                new MaterialDialog.Builder(fragment.getActivity())
                                        .content(content)
                                        .negativeText("??????")
                                        .positiveText("??????")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                try {
                                                    menuItem.method.invoke(viewHolder, itemView, conversationInfo);
                                                } catch (IllegalAccessException e) {
                                                    e.printStackTrace();
                                                } catch (InvocationTargetException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .build()
                                        .show();

                            } else {
                                contextMenus.get(position).method.invoke(viewHolder, itemView, conversationInfo);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    }
                }).show();
                return true;
            }
        };
        itemView.setOnLongClickListener(listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (isStatusNotificationHeader(position)) {
//            ((StatusNotificationContainerViewHolder) holder).onBind(fragment, holder.itemView, statusNotifications);
//            return;
//        }
        ((ConversationViewHolder) holder).onBind(conversationInfos.get(position - headerCount()), position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
//        if (isStatusNotificationHeader(position)) {
//            ((StatusNotificationContainerViewHolder) holder).onBind(fragment, holder.itemView, statusNotifications);
//            return;
//        }
        super.onBindViewHolder(holder, position, payloads);
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (isStatusNotificationHeader(position)) {
//            return R.layout.conversationlist_item_notification_container;
//        }
//        Conversation conversation = conversationInfos.get(position - headerCount()).conversation;
//        return conversation.type.getValue() << 24 | conversation.line;
//    }

    @Override
    public int getItemCount() {
        return headerCount() + (conversationInfos == null ? 0 : conversationInfos.size());
    }

    private boolean isStatusNotificationHeader(int position) {
        return position < headerCount();
    }
}
