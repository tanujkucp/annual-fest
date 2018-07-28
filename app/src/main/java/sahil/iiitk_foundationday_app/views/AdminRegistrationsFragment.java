package sahil.iiitk_foundationday_app.views;
//Made by Tanuj
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sahil.iiitk_foundationday_app.R;

import static android.content.Context.MODE_PRIVATE;

public class AdminRegistrationsFragment extends Fragment {

    String club_name;
    TextView admin_club_name;
    String[]  event_names;
    Spinner event_spinner;
    int club_number,i;
    ListView list;
    FirebaseDatabase db;
    HashMap<Long,ArrayList<String>> ffids=new HashMap<>();
    ArrayList<String> team_contact_email=new ArrayList<>();
    ArrayList<String> team_contact_phone=new ArrayList<>();
    ArrayList<String> team_time=new ArrayList<>();
    ArrayList<String> team_names=new ArrayList<>();
    EventNamesInterface provider;
    SharedPreferences sequencePref;
    ProgressDialog dialog;

    public interface EventNamesInterface{
        public String[] getEventNames(int club_number);
        public int getClubNumber();
        public String getSelectedClubName();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            provider=(EventNamesInterface) activity;
        }catch (ClassCastException e){
            Log.e("interface",e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_admin_registrations, container, false);
        admin_club_name=view.findViewById(R.id.admin_club_name);
        event_spinner=view.findViewById(R.id.admin_spinner);
        list=view.findViewById(R.id.admin_registrations);
        sequencePref=getActivity().getSharedPreferences("sequence",MODE_PRIVATE);

        club_name=provider.getSelectedClubName();
        String s=club_name.replace('_',' ');
        admin_club_name.setText(s);
        club_number=provider.getClubNumber();

        event_names=provider.getEventNames(club_number);
        if (event_names==null){
                Toast.makeText(getActivity(), "Reload this page after connecting to internet.", Toast.LENGTH_LONG).show();
                getActivity().finish();
        }
        dialog=new ProgressDialog(getActivity(),R.style.AlertDialogCustom);
        dialog.setMessage("fetching registered teams...");
        dialog.setCancelable(false);

        //show event names in spinner
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,event_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        event_spinner.setAdapter(adapter);

        //listen for item clicks on spinner
        event_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dialog.show();
                getRegisteredTeams(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //listen for item clicks on list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle(team_names.get(position));
                String details="";
                ArrayList<Long> keys=new ArrayList<>(ffids.keySet());
                Collections.sort(keys);
                ArrayList<String> members=ffids.get(keys.get(position));
                Log.e("adminpage",members.toString());
                for (int i=0;i<members.size();i++){
                    details+=""+(i+1)+". "+members.get(i)+"\n";
                }
                details+="\n@ "+team_time.get(position)
                        +"\nContact: "+team_contact_phone.get(position)+"\n"+team_contact_email.get(position)+"\n";
                builder.setMessage(details);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });

        //start tap target
        startTapTargets();

        return view;
    }

    private void getRegisteredTeams(int position){
        db=FirebaseDatabase.getInstance();
        DatabaseReference ref=db.getReference("Registrations");
        ref=ref.child(club_name);
       // ref=ref.child(event_names[position]);
        ref=ref.child("event"+club_number+""+position);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.e("registration","Registrations found!");
                    collectRegistrations((Map<String,Object>) dataSnapshot.getValue());
                }else{
                    Log.e("registration","No registrations found for this event!");
                    team_names.clear();
                    ArrayAdapter adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,team_names);
                    list.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void collectRegistrations(Map<String,Object> users) {
        //iterate through each registration
        Log.e("registration","Collecting all registrations!");
        long i=0;
        team_names.clear();
        team_contact_phone.clear();
        team_time.clear();
        team_contact_email.clear();
        for (Map.Entry<String, Object> entry : users.entrySet()){
            //Get registration map
            Map singleUser = (Map) entry.getValue();
            ffids.put(i, (ArrayList<String>) singleUser.get("ffids"));
            team_names.add((String)singleUser.get("team_name"));
            team_time.add((String) singleUser.get("reg_time"));
            team_contact_email.add((String) singleUser.get("email"));
            team_contact_phone.add((String) singleUser.get("phone"));
            i++;
        }
        Log.e("adminpage","Teams found: "+team_names.size());
        //Log.e("adminpage",ffids.toString());
        ArrayAdapter adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,team_names);
        list.setAdapter(adapter);
        dialog.dismiss();
    }

    public void startTapTargets(){
        if (sequencePref.getBoolean("seq_admin_reg",false)) return;

        TapTargetView.showFor(getActivity(),
                TapTarget.forView(event_spinner,"Events","Select an event to see its registrations!")
                        .transparentTarget(false).cancelable(false).outerCircleColor(R.color.yellow)
                        .outerCircleAlpha(0.9f)
                        .descriptionTextAlpha(1f)
                        .titleTextSize(25)
                        .descriptionTextColor(R.color.white)
                ,new TapTargetView.Listener(){
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        //save in preferernces so that this is only shown when you open app first time
                        SharedPreferences.Editor editor=sequencePref.edit();
                        editor.putBoolean("seq_admin_reg",true);
                        editor.apply();
                    }
                }
        );
    }

}
