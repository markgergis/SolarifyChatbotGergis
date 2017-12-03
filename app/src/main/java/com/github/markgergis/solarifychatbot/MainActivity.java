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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.github.markgergis.solarifychatbot.holders.CustomIncomingTextMessageViewHolder;
import com.github.markgergis.solarifychatbot.holders.CustomOutcomingTextMessageViewHolder;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        messagesAdapter.addToStart(
                m,
                true);
        sendPostRequest(input);

        return true;
    }

    void sendPostRequest(CharSequence input){
        final String in = input.toString();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String post = "";
                try {
                    post = server.post("{\n\t\"message\": \"" + in + "\"\n}");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Log.d("markr",post);
                if(post.startsWith("Response"))
                    return "Incorrect entry, please try again :)";
                JsonObject jobj =gson.fromJson(post, JsonObject.class);
                Log.d("jobj", jobj.get("message").getAsString());

                return jobj.get("message").getAsString();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Message mes = new Message(server.uuid,new User(server.uuid,"solarify",null,true),s);
                messagesAdapter.addToStart(mes, true);
                final Handler handler = new Handler();
                if(s.toLowerCase().contains("i need your location")) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.locationLayout).setVisibility(View.VISIBLE);

                        }
                    },1500);

                }

            }
        }.execute();
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
                .setIncomingTextConfig(CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message)
                .setOutcomingTextConfig(CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message);


        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, holdersConfig, super.imageLoader);
        super.messagesAdapter.setOnMessageLongClickListener(this);
        super.messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.registerViewClickListener(
                R.id.buttonYes, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        getLocationOnPress();
                        final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    String s = (lat+"#"+lon);
                                    Log.d("markr", s);
                                    sendPostRequest(s);
                                }
                            },500);


                    }
                }
        );
        messagesList.setAdapter(super.messagesAdapter);

    }
}
