package com.github.markgergis.solarifychatbot.holders;

import android.view.View;

import com.github.markgergis.solarifychatbot.model.Message;
import com.stfalcon.chatkit.messages.MessageHolders;

public class OutcomingTextMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    public OutcomingTextMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        time.setText(message.getStatus() + " " + time.getText());
    }
}
