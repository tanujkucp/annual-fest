package sahil.iiitk_foundationday_app.views;
//Made by Tanuj
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.messagesAdapter;
import sahil.iiitk_foundationday_app.model.ChatsList;
import sahil.iiitk_foundationday_app.model.Msg;

public class ContactUs extends AppCompatActivity {

    RecyclerView chatRecycler;
    EditText textBox;
    ProgressBar progressBar;
    FirebaseDatabase db;
    SharedPreferences pref;
    String ffid,user_name,adminSideName="",adminSideFFID;
    ChatsList listToUpdate;
    List<Msg> messages;
    DataSnapshot snapshotToSendMessage;
    messagesAdapter adapter;
    Boolean chatBoxAvailable=false;
    long numOfNew=0;
    Boolean isAdmin=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        chatRecycler=findViewById(R.id.chat_messages_recycler);
        textBox=findViewById(R.id.chat_textBox);
        progressBar=findViewById(R.id.chat_progressBar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //determine if admin has opened this chat or a user
        isAdmin=getIntent().getBooleanExtra("isAdmin",false);
        if (isAdmin){
            adminSideFFID=getIntent().getStringExtra("ffid");
            adminSideName=getIntent().getStringExtra("name");
            actionBar.setTitle(adminSideName);
        }else{
            actionBar.setTitle("Chat with Admin");
        }

        db= FirebaseDatabase.getInstance();
        pref=getSharedPreferences("userInfo",MODE_PRIVATE);
        ffid=pref.getString("FFID","");
        user_name=pref.getString("name","Unknown");

        RecyclerView.LayoutManager manager=new LinearLayoutManager(ContactUs.this);
        chatRecycler.setLayoutManager(manager);

        checkIfChatBoxExists();

    }

    public void checkIfChatBoxExists(){
        DatabaseReference ref=db.getReference("Chatforum");
        Query query=ref.orderByChild("id").equalTo(ffid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.e("chat","your Chat Box exists");
                    Log.e("chat",dataSnapshot.toString());
                    downloadMessages();
                }else{
                    progressBar.setVisibility(View.GONE);
                    Log.e("chat","no chatBox found");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void downloadMessages(){
        Log.e("chat","Downloading messages!");
        DatabaseReference ref=db.getReference("Chatforum");
        Query query=ref.orderByChild("id").equalTo(ffid);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null){
                    chatBoxAvailable=true;
                    Log.e("chat",dataSnapshot.toString());
                    snapshotToSendMessage=dataSnapshot;
                    listToUpdate=dataSnapshot.getValue(ChatsList.class);
                    messages=listToUpdate.getMessages();
                    numOfNew=listToUpdate.getNumOfNew();
                    if (messages==null){
                        messages=new ArrayList<>();
                    }
                    Log.e("chat","Messages downloaded: "+messages.size());
                    progressBar.setVisibility(View.GONE);
                    if (messages.size()>0){
                        // populate the message adapter
                        adapter=new messagesAdapter(messages,isAdmin,ContactUs.this);
                        chatRecycler.setAdapter(adapter);
                        chatRecycler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Call smooth scroll
                                chatRecycler.smoothScrollToPosition(adapter.getItemCount());
                            }
                        });
                    }

                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void sendMessage(View view){
        // send message
        Log.e("chat","Sending message");
        Msg newMessage=new Msg();
        String text=textBox.getText().toString().trim();
        if (!text.isEmpty()){
            textBox.setText("");
            newMessage.setMessage(text);
            //get current time
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM HH:mm");
            final String formattedDate = df.format(c.getTime());
            newMessage.setTime(formattedDate);
            //check if the message should be sent as user or admin
            if (isAdmin){
                newMessage.setId(messages.size());
                newMessage.setSender("admin");
                messages.add(newMessage);
                listToUpdate.setMessages(messages);
                listToUpdate.setNumOfNew(0);
                snapshotToSendMessage.getRef().setValue(listToUpdate);
                Log.e("chat","Admin :Message added to the chat thread. ");
            }else {
                if (chatBoxAvailable){
                    newMessage.setId(messages.size());
                    newMessage.setSender("user");
                    messages.add(newMessage);
                    listToUpdate.setMessages(messages);
                    listToUpdate.setNumOfNew(numOfNew+1);
                    snapshotToSendMessage.getRef().setValue(listToUpdate);
                    Log.e("chat","User :Message added to the chat thread. ");
                }else{
                    ChatsList newList=new ChatsList();
                    newList.setId(ffid);
                    newList.setNumOfNew(1);
                    List<Msg> messages=new ArrayList<>();
                    newMessage.setSender("user");
                    messages.add(newMessage);
                    newList.setMessages(messages);
                    newList.setName(user_name);
                    DatabaseReference ref=db.getReference("Chatforum");
                    ref.push().setValue(newList);
                    Log.e("chat","User :New chat box created and message added to the chat thread.");
                }
            }


        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
