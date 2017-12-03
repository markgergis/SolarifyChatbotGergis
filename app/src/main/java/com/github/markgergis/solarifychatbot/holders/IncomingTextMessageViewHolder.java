package com.github.markgergis.solarifychatbot.holders;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.markgergis.solarifychatbot.R;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.github.markgergis.solarifychatbot.model.Message;

public class IncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    public Button buttonYes;
    public IncomingTextMessageViewHolder(View itemView) {
        super(itemView);
        buttonYes = (Button) itemView.findViewById(R.id.buttonYes);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

    }
}

