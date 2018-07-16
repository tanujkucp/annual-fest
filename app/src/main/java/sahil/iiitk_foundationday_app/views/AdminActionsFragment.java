package sahil.iiitk_foundationday_app.views;
//Made by Tanuj
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.UserAdapter;
import sahil.iiitk_foundationday_app.model.Notif;
import sahil.iiitk_foundationday_app.model.User;

import static android.content.Context.MODE_PRIVATE;

public class AdminActionsFragment extends Fragment {

    BoomMenuButton bmb;
    SearchView searchView;
    TextView recyclerLabel;
    Spinner spinner;
    RecyclerView recyclerView;
    FirebaseDatabase db;
    ArrayList<User> users;
    Boolean isViewCreated=false;
    RecyclerView.LayoutManager layoutManager;
    UserAdapter adapter,searchAdapter;
    String[] spinner_values = new String[]{
            "user_id", "name", "phone", "email", "collegeid"
    };
    ClubNameInterface provider;
    SharedPreferences sequencePref;

    public interface ClubNameInterface{
        public String getSelectedClubName();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            provider=(ClubNameInterface) activity;
        }catch (ClassCastException e){
            Log.e("interface",e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_actions, container, false);
        searchView = view.findViewById(R.id.admin_actions_search);
        spinner = view.findViewById(R.id.admin_actions_spinner);
        recyclerView = view.findViewById(R.id.user_recycler);
        recyclerLabel=view.findViewById(R.id.user_recycler_label);
        bmb=view.findViewById(R.id.admin_bmb);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        sequencePref=getActivity().getSharedPreferences("sequence",MODE_PRIVATE);
        isViewCreated=true;

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        //initialize the adapter
        adapter = new UserAdapter(users, getActivity());
        recyclerView.setAdapter(adapter);

        //to get all data of users to be shown in recycler
        getUsers();

        //initialize boom menu
        initBmb();

        //populate spinner with values
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, spinner_values);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        //handle search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchField = spinner_values[spinner.getSelectedItemPosition()];
                searchUser(query,searchField);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()){
                    adapter = new UserAdapter(users, getActivity());
                    recyclerView.setAdapter(adapter);
                    recyclerLabel.setText("All Users");
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            //start the tap targets
            if (isViewCreated) startTapTargets();

        }
    }

    public void getUsers() {
        DatabaseReference ref = db.getReference("Users");
        Query query = ref.orderByChild("user_id");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                    //now pass on this data to the adapter
//                    adapter = new UserAdapter(users, getActivity());
//                    recyclerView.setAdapter(adapter);
                    adapter.notifyItemInserted(users.size()-1);
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

    public void searchUser(String searchText, String field){
        final ArrayList<User> searchResults=new ArrayList<>();
        DatabaseReference ref=db.getReference("Users");
        Query query=ref.orderByChild(field).equalTo(searchText);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    User user=dataSnapshot.getValue(User.class);
                    searchResults.add(user);
                    //now pass on this data to the adapter
                    searchAdapter = new UserAdapter(searchResults, getActivity());
                    recyclerView.setAdapter(searchAdapter);
                    recyclerLabel.setText("Search Results");
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

    public void initBmb(){
        //first button
        HamButton.Builder builder1= new HamButton.Builder()
                .normalImageRes(R.drawable.icon_notif)
                .normalText("Broadcast")
                .subNormalText("Send a notification to Users.")
                .shadowEffect(true)
                .textGravity(Gravity.CENTER)
                .subTextGravity(Gravity.CENTER)
                .rippleEffect(true)
                .normalColor(Color.parseColor("#E91E63"))
                .buttonWidth(Util.dp2px(300))
                .buttonHeight(Util.dp2px(60))
                .textSize(20)
                .subTextSize(15)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        notifAction();
                    }
                });
        bmb.addBuilder(builder1);

        //second button
        HamButton.Builder builder2= new HamButton.Builder()
                .normalImageRes(R.drawable.icon_addquestion)
                .normalText("Add Question")
                .subNormalText("Add a question to the Quiz.")
                .shadowEffect(true)
                .textGravity(Gravity.CENTER)
                .subTextGravity(Gravity.CENTER)
                .rippleEffect(true)
                .normalColor(Color.parseColor("#43A047"))
                .buttonWidth(Util.dp2px(300))
                .buttonHeight(Util.dp2px(60))
                .textSize(20)
                .subTextSize(15)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intent=new Intent(getActivity(),UploadQuestionActivity.class);
                        getActivity().startActivity(intent);
                    }
                });
        bmb.addBuilder(builder2);

        //third button
        HamButton.Builder builder3= new HamButton.Builder()
                .normalImageRes(R.drawable.ic_reset)
                .normalText("Reset")
                .subNormalText("Reset lives of all users for the Quiz.")
                .shadowEffect(true)
                .textGravity(Gravity.CENTER)
                .subTextGravity(Gravity.CENTER)
                .rippleEffect(true)
                .normalColor(Color.parseColor("#5E35B1"))
                .buttonWidth(Util.dp2px(300))
                .buttonHeight(Util.dp2px(60))
                .textSize(20)
                .subTextSize(15)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        livesReset();
                    }
                });
        bmb.addBuilder(builder3);
        bmb.setBackgroundEffect(true);
        bmb.setBackPressListened(true);
        bmb.setBoomInWholeScreen(true);
    }

    public void notifAction(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter content of notification-");
                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setSingleLine(false);
                input.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                builder.setView(input);

                //   Set up the buttons
                builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dialogue_entry = input.getText().toString();
                        if (dialogue_entry.isEmpty()){
                            Toast.makeText(getActivity(),"Empty Message!",Toast.LENGTH_SHORT).show();
                        }else{
                            dialog.cancel();
                            //post notification
                            generateNotifID(dialogue_entry);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
    }
        private void generateNotifID(final String dialogueentry){
            // get the value stored on the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("Counters").child("last_notif");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long id=(long)dataSnapshot.getValue();
                    id=id+1;
                    dataSnapshot.getRef().setValue(id);
                    postNotification(id,dialogueentry);
                    myRef.removeEventListener(this);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("firebase", "Failed to read value.", databaseError.toException());
                }
            });
    }

    public void postNotification(long notif_id,String dialogue_entry){
        String club_name=provider.getSelectedClubName();
        Notif notification=new Notif();
        notification.setDetails(dialogue_entry.trim());
        notification.setNotif_id(notif_id);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM HH:mm");
        String formattedDate = df.format(c.getTime());
        notification.setTime(formattedDate);
        String a=club_name.replace('_',' ');
        notification.setWhich_club(a);
        DatabaseReference mRef = db.getReference().child("Notification");
        mRef.push().setValue(notification);
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getActivity(), "Notification push Successful!", Toast.LENGTH_LONG).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity(), "Notification push failed. Try again!", Toast.LENGTH_LONG).show();
//            }
//        });

    }

    public void livesReset(){
        final DatabaseReference ref=db.getReference("Users");
        final AlertDialog.Builder mydia  = new AlertDialog.Builder(getActivity());
                mydia.setMessage("Are you sure, you want to reset the lives and scores of all the users?");
                mydia.setCancelable(true);
                mydia.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
//                         Query query=ref.orderByChild("quiz_lives");
                         Query query=ref.orderByChild("quiz_correct");
                         query.addChildEventListener(new ChildEventListener() {
                             @Override
                             public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                 if (dataSnapshot!=null){
                                     User user = dataSnapshot.getValue(User.class);
                                     user.setQuiz_correct(0);
                                     user.setQuiz_lives(5);
                                     dataSnapshot.getRef().setValue(user);
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
                         Toast.makeText(getActivity(), "Lives Reset Successful!", Toast.LENGTH_SHORT).show();
                     }
                });

                mydia.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = mydia.create();
                alertDialog.show();
    }

    public void startTapTargets(){
        if (sequencePref.getBoolean("seq_admin_actions",false)) return;

        List<TapTarget> targets=new ArrayList<>();
        //todo customise these targets with different colors and styles
        targets.add(TapTarget.forView(searchView,"Search here!"
                ,"Type text here to search specific users.").id(1)
                .cancelable(false)
                .transparentTarget(true));
        targets.add(TapTarget.forView(spinner,"Search Property"
                ,"Choose the property to search for like name etc.").id(2)
                .cancelable(false)
                .transparentTarget(true));
        targets.add(TapTarget.forView(bmb,"Actions"
                ,"Click here for quick actions like Broadcasts etc.").id(3)
                .cancelable(false)
                .transparentTarget(true));

        TapTargetSequence sequence=new TapTargetSequence(getActivity())
                .targets(targets)
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        //save in preferernces so that this is only shown when you open app first time
                        SharedPreferences.Editor editor=sequencePref.edit();
                        editor.putBoolean("seq_admin_actions",true);
                        editor.apply();

                    }
                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }
                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                    }
                });
        sequence.start();
    }

}
