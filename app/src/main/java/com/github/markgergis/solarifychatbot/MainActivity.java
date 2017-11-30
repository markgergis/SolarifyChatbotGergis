//package com.github.markgergis.solarifychatbot;
//
//import android.content.Context;
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import com.stfalcon.chatkit.messages.MessageHolders;
//import com.stfalcon.chatkit.messages.MessageInput;
//import com.stfalcon.chatkit.messages.MessagesList;
//import com.stfalcon.chatkit.messages.MessagesListAdapter;
//import com.stfalcon.chatkit.sample.R;
//import com.stfalcon.chatkit.sample.common.data.fixtures.MessagesFixtures;
//import com.stfalcon.chatkit.sample.common.data.model.Message;
//import com.stfalcon.chatkit.sample.features.demo.DemoMessagesActivity;
//import com.stfalcon.chatkit.sample.utils.AppUtils;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//}

package com.github.markgergis.solarifychatbot;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.sample.R;
import com.stfalcon.chatkit.sample.common.data.fixtures.MessagesFixtures;
import com.github.markgergis.solarifychatbot.model.Message;
import com.github.markgergis.solarifychatbot.DemoMessagesActivity;
import com.github.markgergis.solarifychatbot.model.User;
import com.stfalcon.chatkit.sample.utils.AppUtils;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends DemoMessagesActivity
        implements MessagesListAdapter.OnMessageLongClickListener<Message>,
        MessageInput.InputListener,
        MessageInput.AttachmentsListener {

    public static void open(Context context) {
        context.startActivity(new Intent(context, com.github.markgergis.solarifychatbot.MainActivity.class));
    }

    private MessagesList messagesList;
    Message m = new Message(Long.toString(UUID.randomUUID().getLeastSignificantBits()),
            (new User(senderId,"user",null,true)),
            "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_layout_messages);

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        m.setText(input.toString());
        final String in = input.toString();
        messagesAdapter.addToStart(
                m,
                true);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String post = "";
                try {
                    post = server.post(new Gson().toJson( "{\n\t\"message\": " + in + "\n}"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Log.d("markr",post);
                JsonObject jobj =gson.fromJson(post, JsonObject.class);
                return jobj.get("message").getAsString();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Message mes = new Message(server.uuid,new User(server.uuid,"solarify",null,true),server.message);
                messagesAdapter.addToStart(mes, true);
            }
        }.execute();

        return true;
    }

    @Override
    public void onAddAttachments() {
        messagesAdapter.addToStart(m, true);
    }

    @Override
    public void onMessageLongClick(Message message) {
        AppUtils.showToast(this, R.string.on_log_click_message, false);
    }

    private void initAdapter() {
        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextLayout(R.layout.item_custom_incoming_text_message)
                .setOutcomingTextLayout(R.layout.item_custom_outcoming_text_message)
                .setIncomingImageLayout(R.layout.item_custom_incoming_image_message)
                .setOutcomingImageLayout(R.layout.item_custom_outcoming_image_message);

        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, holdersConfig, super.imageLoader);
        super.messagesAdapter.setOnMessageLongClickListener(this);
        super.messagesAdapter.setLoadMoreListener(this);
        messagesList.setAdapter(super.messagesAdapter);
    }
}
