package sahil.iiitk_foundationday_app.views;
//Made by Tanuj
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.AdminChatAdapter;
import sahil.iiitk_foundationday_app.model.ChatsList;
import sahil.iiitk_foundationday_app.model.Msg;

public class AdminChatFragment extends Fragment {

    FirebaseDatabase db;
    RecyclerView recyclerView;
    List<String> names,ids,numNews;
    List<Msg> lastMessages;
    AdminChatAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_admin_chat, container, false);
        recyclerView=view.findViewById(R.id.admin_chat_recycler);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        names=new ArrayList<>();
        ids=new ArrayList<>();
        numNews=new ArrayList<>();
        lastMessages=new ArrayList<>();

        db=FirebaseDatabase.getInstance();
        return view;
    }

    public void getChats(){
        names.clear();
        ids.clear();
        numNews.clear();
        lastMessages.clear();
        //initialize the adapter
        adapter=new AdminChatAdapter(names,ids,lastMessages,numNews,getActivity());
        recyclerView.setAdapter(adapter);
        DatabaseReference ref=db.getReference("Chatforum");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null) {
                    //Log.e("chat", dataSnapshot.toString());
                    ChatsList chatsList = dataSnapshot.getValue(ChatsList.class);
                     String name=chatsList.getName();
                     String id=chatsList.getId();
                     Msg lastMessage=chatsList.getMessages().get(chatsList.getMessages().size()-1);
                     long numNew=chatsList.getNumOfNew();
                     names.add(name);
                     ids.add(id);
                     numNews.add(""+numNew);
                     lastMessages.add(lastMessage);
                     //setup dialog adapter
//                    AdminChatAdapter adapter=new AdminChatAdapter(names,ids,lastMessages,numNews,getActivity());
//                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(names.size()-1);

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

    @Override
    public void onResume() {
        super.onResume();
        //download all chats
        getChats();
    }
}
