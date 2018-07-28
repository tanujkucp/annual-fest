package sahil.iiitk_foundationday_app.views;
// Made by Tanuj
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.mail.GMailSender;
import sahil.iiitk_foundationday_app.model.EventReg;
import sahil.iiitk_foundationday_app.model.MyPersonalRegistrations;
import sahil.iiitk_foundationday_app.model.SingleEventPersonal;

public class EventRegActivity extends AppCompatActivity {
    LinearLayout lv1, lv2;
    Spinner s1;
    int min, max, club_number, check_number,event_number;
    String event_name, body,formattedDate,eventID;
    EditText ed, edd;
    Button btnl;
    List<EditText> allEds = new ArrayList<>();
    List<Integer> lst = new ArrayList<>();
    List<String> IDs = new ArrayList<>();
    EventReg registration = new EventReg();
    FirebaseDatabase db;
    SharedPreferences savedData;
    ProgressDialog dialog;
    ActionBar bar;
    Boolean isRegistrationProcessRunning=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_reg);
        bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Event Registration");

        db = FirebaseDatabase.getInstance();
        savedData = getSharedPreferences("userInfo", MODE_PRIVATE);

        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);

        //get values passed by intent
        Bundle bundle = getIntent().getExtras();
        event_name = bundle.getString("event_name");
        club_number = bundle.getInt("club_number");
        event_number=bundle.getInt("event_number");
        eventID="event"+club_number+""+event_number;
        min = bundle.getInt("min");
        max = bundle.getInt("max");

        try {
            for (int f = 0; f <= (max - min); f++) {
                lst.add(f + min);
            }
            s1 = findViewById(R.id.s1);
            lv1 = findViewById(R.id.lv1);
            lv2 = findViewById(R.id.lv2);
            btnl = findViewById(R.id.go);
            ArrayAdapter<Integer> aa1 = new ArrayAdapter<>(this, R.layout.spinner_register, lst);
            s1.setAdapter(aa1);
            aa1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            s1.getBackground().setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_ATOP);
            int id = 90;
            if (max >= 2) {
                edd = new EditText(getApplicationContext());
                edd.setId(id);
                edd.getBackground().setColorFilter(getResources().getColor(R.color.hh), PorterDuff.Mode.SRC_ATOP);
                edd.setHint("Team Name");
                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter.LengthFilter(20); //Filter to 20 characters
                edd.setFilters(filters);
                edd.setHintTextColor(getResources().getColor(R.color.hh));
                edd.setTextColor(getResources().getColor(R.color.black));
                edd.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
                        AbsListView.LayoutParams.WRAP_CONTENT));
                lv2.addView(edd);
            }
            s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    lv1.removeAllViews();
                    allEds.clear();
                    InputFilter[] filters = new InputFilter[1];
                    filters[0] = new InputFilter.LengthFilter(6); //Filter to 6 characters
                    for (int t = 0; t < (i + min); t++) {
                        ed = new EditText(getApplicationContext());
                        allEds.add(ed);
                        ed.setId(t + 1);
                        ed.setFilters(filters);
                        ed.getBackground().setColorFilter(getResources().getColor(R.color.hh), PorterDuff.Mode.SRC_ATOP);
                        ed.setHint("Member " + (t + 1) + " FFID");
                        ed.setHintTextColor(getResources().getColor(R.color.hh));
                        ed.setTextColor(getResources().getColor(R.color.black));
                        ed.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
                                AbsListView.LayoutParams.WRAP_CONTENT));
                        lv1.addView(ed);
                    }
                    allEds.get(0).setText("" + savedData.getString("FFID", ""));
                    allEds.get(0).setEnabled(false);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Problem", Toast.LENGTH_SHORT).show();
        }
        btnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IDs.clear();
                check_number = 0;
                int flag = 0;
                String[] strings = new String[allEds.size()];
                for (int i = 0; i < allEds.size(); i++) {
                    strings[i] = allEds.get(i).getText().toString();
                    if (strings[i].trim().equals("")) {
                        allEds.get(i).setError("Enter FFID");
                        flag = 1;
                    } else {
                        IDs.add(strings[i]);
                    }
                }
                if (max >= 2) {
                    if (edd.getText().toString().trim().equals("")) {
                        edd.setError("Empty");
                        flag = 1;
                    }
                }

                if (flag == 0) {
                    btnl.setEnabled(false);
                    //check if entered FFIDs have the registered user's FFID or not
                    //so that user can't register for other people unless he is in the team
                    if (IDs.contains(savedData.getString("FFID", ""))) {
                        Log.e("registration", "Going to check FFIDs");
                        String college_name = savedData.getString("college", "");
                        if (college_name.equals("IIIT KOTA") || college_name.equals("MNIT JAIPUR")) {
                            checkFFID(IDs.get(0));
                        } else {
                            DatabaseReference databaseReference = db.getReference("Users");
                            Query query = databaseReference.orderByChild("user_id").equalTo(IDs.get(0));
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        if (max == 1) {
                                            i.setData(Uri.parse("https://www.townscript.com/e/solo-event-flairfiesta-iiitk-334121/"));
                                        } else {
                                            i.setData(Uri.parse("https://www.townscript.com/e/team-event-flairfiesta-iiitk-334121/"));
                                        }
                                        EventRegActivity.this.startActivity(i);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Provided FFID does not exist!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                        }
                    } else {
                        btnl.setEnabled(true);
                        Log.e("registration", "User's FFID is not present in the list!");
                        Toast.makeText(getApplicationContext(), "You can't register for others unless you have a team!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    btnl.setEnabled(true);
                    IDs.clear();
                }

            }
        });
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

    @Override
    public void onBackPressed() {
        if (!isRegistrationProcessRunning) super.onBackPressed();
    }

    //registration ,validation and confirmation work done by
    //Tanuj
    public void checkFFID(final String a) {
        dialog.setMessage("Checking your FFIDs...");
        dialog.show();
        isRegistrationProcessRunning=true;
        DatabaseReference databaseReference = db.getReference("Users");
        final Query query = databaseReference.orderByChild("user_id").equalTo(a);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    dialog.dismiss();
                    isRegistrationProcessRunning=false;
                    allEds.get(check_number).setError("This FFID does not exist!");
                    Toast.makeText(getApplicationContext(), "Member with FFID: " + a + " does not exist!", Toast.LENGTH_SHORT).show();
                } else {
                    check_number++;
                    if (check_number < IDs.size()) {
                        checkFFID(IDs.get(check_number));
                    } else if (check_number == IDs.size()) {
                        check_number = 0;
                        reg_secondStage();
                    }
                }
                query.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                isRegistrationProcessRunning=false;
            }
        });
    }

    public void reg_secondStage() {
        dialog.setMessage("Checking Registrations...");
        Log.e("registration", "Second stage called!");
        DatabaseReference ref = db.getReference("Registrations");
        switch (club_number) {
            case 0:
                ref = ref.child("Technical_club");
                break;
            case 1:
                ref = ref.child("Cultural_club");
                break;
            case 2:
                ref = ref.child("Literary_and_dramatics_club");
                break;
            case 3:
                ref = ref.child("Fine_arts_and_photography_club");
                break;
        }
        ref = ref.child(eventID);
        final DatabaseReference secref=ref;
        secref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.e("registration", "Snapshot exists!");
                    collectRegistrations((Map<String, Object>) dataSnapshot.getValue());
                } else {
                    Log.e("registration", "Snapshot does not exists!");
                    reg_lastStage();
                }
                secref.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("registration", databaseError.getMessage());
                dialog.dismiss();
                isRegistrationProcessRunning=false;
            }
        });
    }

    private void collectRegistrations(Map<String, Object> users) {
        //iterate through each registration
        Log.e("registration", "Collecting all registrations!");
        boolean goodToGo = true;
        HashMap<Long, ArrayList<String>> ffids = new HashMap<Long, ArrayList<String>>();
        long i = 0;
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            //Get registration map
            Map singleUser = (Map) entry.getValue();
            ffids.put(i, (ArrayList<String>) singleUser.get("ffids"));
            i++;
        }
        ArrayList<Long> keys = new ArrayList<>(ffids.keySet());
        for (int j = 0; j < ffids.keySet().size(); j++) {
            Log.e("registration", "outer call no. " + j);
            ArrayList<String> single_reg = ffids.get(keys.get(j));
            Log.e("registration", single_reg.toString());
            Log.e("registration", IDs.toString());
            for (String a : IDs) {
                Log.e("registration", "inner call no. " + j);
                if (single_reg.contains(a)) {
                    goodToGo = false;
                    Log.e("registration", "Found match for FFID!");
                    break;
                }
            }
        }
        if (goodToGo) {
            reg_lastStage();
        } else {
            dialog.dismiss();
            isRegistrationProcessRunning=false;
            Toast.makeText(getApplicationContext(), "One or more of these FFIDs are already registered for this event!", Toast.LENGTH_LONG).show();
        }
    }

    public void reg_lastStage() {
        dialog.setMessage("Registering your team...");
        Log.e("registration", "Last stage called!");
        DatabaseReference ref = db.getReference("Registrations");
        switch (club_number) {
            case 0:
                ref = ref.child("Technical_club");
                break;
            case 1:
                ref = ref.child("Cultural_club");
                break;
            case 2:
                ref = ref.child("Literary_and_dramatics_club");
                break;
            case 3:
                ref = ref.child("Fine_arts_and_photography_club");
                break;
        }
        ref = ref.child(eventID);
        registration.setFFIDs(IDs);
        //get current time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM HH:mm");
        formattedDate = df.format(c.getTime());
        registration.setReg_time(formattedDate);
        String team_name;
        if (max >= 2) {
            team_name = edd.getText().toString();
        } else {
            team_name = savedData.getString("name", "");
        }
        registration.setTeam_name(team_name);
        registration.setEmail(savedData.getString("email", ""));
        registration.setPhone(savedData.getString("phone", ""));
        ref.push().setValue(registration);

        //send confirmation mail
        sendEmail(IDs, savedData.getString("name", ""), savedData.getString("email", ""), event_name, team_name);

        //increase the counter for the specific club's registrations
        increaseClubRegistrationCounter();

        //add this event to all the team members' personal registered events list
        addToRegisteredEvents();

        //todo it now goes back to home activity but i want to display a nice message to user of confirmation with details
        this.finish();
    }

    //sending emails automatically
    public void sendEmail(List<String> ffids, String name, String email, final String event_name, final String team_name) {
        dialog.setMessage("Sending confirmation email...");
        body = "Hi,\n" + name + " ( " + email + " )\nYour registration for " + event_name + " in Flair Fiesta 2k18 is now confirmed."
                + "\nTeam Name: " + team_name
                + "\nYour team members are - ";
        for (int i = 0; i < ffids.size(); i++) {
            body = body + "\n" + (i + 1) + ". " + ffids.get(i);
        }
        body = body + "\n\nWe at Organzing Team of Flair Fiesta 2k18 will be glad to have you with us on 23Mar 18 and 24Mar 18."
                + "\n\nThanks and Regards"
                + "\nAdmin";
        final String recepient = email;
        SharedPreferences email_pref=getSharedPreferences("email_account",MODE_PRIVATE);
        final String username=email_pref.getString("email_id","error"),
                password=email_pref.getString("email_password","error");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(username, password);
                    sender.sendMail("Your Registration for " + event_name + " in Flair Fiesta 2k18 is Confirmed.",
                            body,
                            username,
                            recepient);
                    Log.e("registration", "Sending confirmation email to: " + recepient);
                } catch (Exception e) {
                    Log.e("registration", e.getMessage(), e);
                }
            }
        }).start();

    }

    public void increaseClubRegistrationCounter() {
        dialog.setMessage("Completing registration...");
        DatabaseReference ref = db.getReference("Counters");
        switch (club_number) {
            case 0:
                ref = ref.child("technical_reg");
                break;
            case 1:
                ref = ref.child("cultural_reg");
                break;
            case 2:
                ref = ref.child("literary_reg");
                break;
            case 3:
                ref = ref.child("photography_reg");
                break;
        }
        //increase counter for individual club
        final DatabaseReference ref1=ref;
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = (long) dataSnapshot.getValue();
                dataSnapshot.getRef().setValue(count + 1);
                ref1.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("registration", databaseError.getMessage());
            }
        });
        //increase overall total registrations counter
        final DatabaseReference ref2 = db.getReference("Counters").child("total_reg");
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = (long) dataSnapshot.getValue();
                dataSnapshot.getRef().setValue(count + 1);
                ref2.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("registration", databaseError.getMessage());
            }
        });
        //increase counter for individual specific event
        final DatabaseReference ref3=db.getReference("Counters").child("Event"+club_number+""+event_number);
        ref3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count=(long) dataSnapshot.getValue();
                dataSnapshot.getRef().setValue(count+1);
                ref3.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("registration", databaseError.getMessage());
            }
        });
    }

    public void addToRegisteredEvents(){
        final MyPersonalRegistrations reg=new MyPersonalRegistrations();
        final SingleEventPersonal event=new SingleEventPersonal();
        event.setEvent_name(event_name);
        event.setUser_name(savedData.getString("name","Error"));
        event.setDate(formattedDate);
        //now update the data on firebase for each registered FFID
        for (final String ffid : IDs){
            final DatabaseReference ref=db.getReference("UserPersonalRegistrationLists");
            final Query query=ref.orderByChild("ffid").equalTo(ffid);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        updatePersonalList(ffid,event);
                    }else{
                        List<SingleEventPersonal> emptyEventList=new ArrayList<>();
                        event.setId(1);
                        emptyEventList.add(event);
                        reg.setFfid(ffid);
                        reg.setEventList(emptyEventList);
                        ref.push().setValue(reg);
                        query.removeEventListener(this);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        //notify user
        dialog.dismiss();
        isRegistrationProcessRunning=false;
        Toast.makeText(this, "Your Team is registered for " + event_name, Toast.LENGTH_LONG).show();
    }

    public void updatePersonalList(String ffid, final SingleEventPersonal event){
         DatabaseReference ref=db.getReference("UserPersonalRegistrationLists");
        final Query query=ref.orderByChild("ffid").equalTo(ffid);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyPersonalRegistrations reg=dataSnapshot.getValue(MyPersonalRegistrations.class);
                List<SingleEventPersonal> eventList=reg.getEventList();
                long id=eventList.size()+1;
                event.setId(id);
                eventList.add(event);
                reg.setEventList(eventList);
                dataSnapshot.getRef().setValue(reg);
                query.removeEventListener(this);
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

}

