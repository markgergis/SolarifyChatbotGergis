package com.github.markgergis.solarifychatbot.holders;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.sample.R;
import com.github.markgergis.solarifychatbot.model.Message;

public class CustomIncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    public Button buttonYes;
    public CustomIncomingTextMessageViewHolder(View itemView) {
        super(itemView);
        buttonYes = (Button) itemView.findViewById(R.id.buttonYes);
        Log.d("markr", "initialize");

    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

    }
}

