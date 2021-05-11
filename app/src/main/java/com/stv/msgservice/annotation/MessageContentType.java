package com.stv.msgservice.annotation;

import com.stv.msgservice.ui.conversation.message.MessageContent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于设置消息UI({@link cn.wildfire.chat.kit.conversation.message.viewholder.MessageContentViewHolder})和消息体({@link MessageContent})对应关系
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageContentType {
    Class<? extends MessageContent>[] value();
}
