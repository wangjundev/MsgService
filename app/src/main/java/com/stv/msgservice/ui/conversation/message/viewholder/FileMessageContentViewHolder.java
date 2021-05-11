package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.ext.FileUtils;
import com.stv.msgservice.ui.conversation.message.FileMessageContent;

import java.io.File;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

@MessageContentType(FileMessageContent.class)
@EnableContextMenu
public class FileMessageContentViewHolder extends MediaMessageContentViewHolder {
    @BindView(R2.id.fileIconImageView)
    ImageView fileIconImageView;
    @BindView(R2.id.fileNameTextView)
    TextView nameTextView;
    @BindView(R2.id.fileSizeTextView)
    TextView sizeTextView;

    private FileMessageContent fileMessageContent;

    public FileMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    @Override
    public void onBind(Message message) {
//        super.onBind(message);
//        fileMessageContent = (FileMessageContent) message.message.content;
//        nameTextView.setText(fileMessageContent.getName());
//        sizeTextView.setText(FileUtils.getReadableFileSize(fileMessageContent.getSize()));
//        fileIconImageView.setImageResource(FileUtils.getFileTypeImageResId(fileMessageContent.getName()));
    }

    @OnClick(R2.id.imageView)
    public void onClick(View view) {
//        if (message.isDownloading) {
//            return;
//        }
//        File file = messageViewModel.mediaMessageContentFile(message.message);
//        if (file == null) {
//            return;
//        }
//
//        if (file.exists()) {
//            Intent intent = FileUtils.getViewIntent(fragment.getContext(), file);
//            ComponentName cn = intent.resolveActivity(fragment.getContext().getPackageManager());
//            if (cn == null) {
//                Toast.makeText(fragment.getContext(), "找不到能打开此文件的应用", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            fragment.startActivity(intent);
//        } else {
//            messageViewModel.downloadMedia(message, file);
//        }
    }
}
