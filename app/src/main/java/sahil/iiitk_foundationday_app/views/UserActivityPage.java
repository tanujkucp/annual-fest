package sahil.iiitk_foundationday_app.views;
//Made by Tanuj
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.FavouriteListAdapter;
import sahil.iiitk_foundationday_app.adapters.RegisteredEventAdapter;
import sahil.iiitk_foundationday_app.model.MyPersonalRegistrations;
import sahil.iiitk_foundationday_app.model.SingleEventPersonal;

public class UserActivityPage extends AppCompatActivity {

    String ffid;
    SharedPreferences pref,favourites;
    FirebaseDatabase db;
    List<SingleEventPersonal> listOfRegistrations;
    RecyclerView reg_recycler,fav_recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        reg_recycler=findViewById(R.id.myactivity_reg_recycler);
        fav_recycler=findViewById(R.id.myactivity_fav_recycler);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("My Profile");

        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        reg_recycler.setLayoutManager(manager);
        RecyclerView.LayoutManager manager2=new LinearLayoutManager(this);
        fav_recycler.setLayoutManager(manager2);

        db=FirebaseDatabase.getInstance();
        pref=getSharedPreferences("userInfo",MODE_PRIVATE);
        ffid=pref.getString("FFID","");
        favourites=getSharedPreferences("fav"+ffid,MODE_PRIVATE);

        //show user profile details
        showProfile();

        //get user's registered events' data
        downloadPersonalRegistrations();

        //get user's favourite events
        getFavourites();
    }

    public void showProfile(){
        View profile=findViewById(R.id.include_profile);
        ((TextView)profile.findViewById(R.id.profile_name)).setText(pref.getString("name",""));
        ((TextView)profile.findViewById(R.id.profile_ffid)).setText(pref.getString("FFID",""));
        ((TextView)profile.findViewById(R.id.profile_collageID)).setText(pref.getString("collegeid",""));
        ((TextView)profile.findViewById(R.id.profile_emailID)).setText(pref.getString("email",""));
        ((TextView)profile.findViewById(R.id.profile_mobile)).setText(pref.getString("phone",""));
        ((TextView)profile.findViewById(R.id.profile_collage)).setText(pref.getString("college",""));

        ((TextView)profile.findViewById(R.id.profile_branch)).setText
                (pref.getString("Year","")+" year, "+pref.getString("department",""));
        ((TextView)profile.findViewById(R.id.profile_gender)).setText
                (pref.getString("gender","")+", "+pref.getString("MOS",""));

    }

    public void downloadPersonalRegistrations(){
        DatabaseReference ref=db.getReference("UserPersonalRegistrationLists");
        final Query query=ref.orderByChild("ffid").equalTo(ffid);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null){
                    MyPersonalRegistrations reg=dataSnapshot.getValue(MyPersonalRegistrations.class);
                    listOfRegistrations=reg.getEventList();
                    // now feed this data to the adapter
                    RegisteredEventAdapter adapter=new RegisteredEventAdapter(listOfRegistrations);
                    reg_recycler.setAdapter(adapter);
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

    public void getFavourites(){
        int num=favourites.getInt("count",0);
        List<String> favEventList=new ArrayList<>();
        for (int i=1;i<=num;i++){
            String name=favourites.getString("event"+i,"");
            if (!name.equals(""))
            favEventList.add(name);
        }
        // now feed this data to the adapter
        FavouriteListAdapter adapter=new FavouriteListAdapter(favEventList);
        fav_recycler.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
