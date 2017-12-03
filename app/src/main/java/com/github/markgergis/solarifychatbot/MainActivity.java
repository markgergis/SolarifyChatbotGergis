package com.github.markgergis.solarifychatbot;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.markgergis.solarifychatbot.holders.IncomingTextMessageViewHolder;
import com.github.markgergis.solarifychatbot.holders.OutcomingTextMessageViewHolder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.github.markgergis.solarifychatbot.model.Message;
import com.github.markgergis.solarifychatbot.model.User;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends MessagesActivity
        implements MessagesListAdapter.OnMessageLongClickListener<Message>,
        MessageInput.InputListener,
        MessageInput.AttachmentsListener {

    public static void open(Context context) {
        context.startActivity(new Intent(context, com.github.markgergis.solarifychatbot.MainActivity.class));
    }

    private MessagesList messagesList;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_messages);

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        Message m = new Message(Long.toString(UUID.randomUUID().getLeastSignificantBits()),
                (new User(senderId,"user",null,true)),
                input.toString());
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
                if(post.startsWith("Response"))
                    return "Incorrect entry, please try again :)";
                JsonObject jobj =gson.fromJson(post, JsonObject.class);

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

    }

    @Override
    public void onMessageLongClick(Message message) {
    }
    private void initAdapter() {
        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(IncomingTextMessageViewHolder.class,
                        R.layout.item_incoming_text_message)
                .setOutcomingTextConfig(OutcomingTextMessageViewHolder.class,
                        R.layout.item_outcoming_text_message);


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
                                    sendPostRequest(s);
                                }
                            },500);
                    }
                }
        );
        messagesAdapter.registerViewClickListener(
                R.id.buttonNO, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        new MaterialDialog.Builder(MainActivity.this)
                                .title("Permission denied")
                                .content("Solarify can't calculate your daily consumption without accessing your GPS to get your location" +
                                        "\nDo you want to continue using Solarify Chatbot?")
                                .positiveText("Continue").onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                MainActivity.this.finishAffinity();
                            }
                        })
                                .negativeText("Exit")
                                .show();
                    }
                }
        );
        messagesList.setAdapter(super.messagesAdapter);

    }
}
