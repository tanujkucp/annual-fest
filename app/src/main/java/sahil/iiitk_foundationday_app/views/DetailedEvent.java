package sahil.iiitk_foundationday_app.views;
// Made by Tanuj
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sahil.iiitk_foundationday_app.R;

public class DetailedEvent extends AppCompatActivity {

    TextView event_detail, event_date_time, event_venue, event_last_date, event_contact,
            event_prize, event_type,event_regfee;
    String e_name,e_time,e_venue,e_contact,e_details,e_prize;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView event_picture;
    int i,min,max,club_number,event_number,version=0;
    FirebaseStorage storage;
    FirebaseDatabase db;
    BoomMenuButton bmb;
    SharedPreferences pref,favourites,sequencePref;
    Boolean isFavourite,isRegistrationStarted=false;
    TextOutsideCircleButton.Builder builder5;
    File path,temp;
    ProgressDialog dialog;
    TextOutsideCircleButton.Builder b1,b2,b3,b4,b5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_event);
        storage=FirebaseStorage.getInstance();
        db=FirebaseDatabase.getInstance();
        path=getCacheDir();
        dialog=new ProgressDialog(DetailedEvent.this);
        dialog.setMessage("fetching interesting details...");
        dialog.setCancelable(false);
        dialog.show();

        //setting the views
        Toolbar toolbar = findViewById(R.id.event_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = findViewById(R.id.event_collapsing);
        collapsingToolbarLayout.setTitleEnabled(true);

        event_picture = findViewById(R.id.event_picture);
        bmb=findViewById(R.id.event_bmb);
        event_detail = findViewById(R.id.event_more_details);
        event_type = findViewById(R.id.event_type);
        event_date_time = findViewById(R.id.event_date_time);
        event_last_date = findViewById(R.id.event_last_date);
        event_venue = findViewById(R.id.event_venue);
        event_contact = findViewById(R.id.event_contact_details);
        event_prize = findViewById(R.id.event_prize);
        event_regfee=findViewById(R.id.event_regfee);

        //getting chosen club and its event
        club_number = getIntent().getIntExtra("club_number", 0);
        event_number = getIntent().getIntExtra("event_number", 0);

        pref=getSharedPreferences("userInfo",MODE_PRIVATE);
        sequencePref=getSharedPreferences("sequence",MODE_PRIVATE);
        String ffid=pref.getString("FFID","");
        favourites =getSharedPreferences("fav"+ffid,MODE_PRIVATE);

        //add a placeholder builder because it crashes without it
        b1=new TextOutsideCircleButton.Builder().normalText("loading");
        b2=new TextOutsideCircleButton.Builder().normalText("loading");
        b3=new TextOutsideCircleButton.Builder().normalText("loading");
        b4=new TextOutsideCircleButton.Builder().normalText("loading");
        b5=new TextOutsideCircleButton.Builder().normalText("loading");
        bmb.addBuilder(b1);
        bmb.addBuilder(b2);
        bmb.addBuilder(b3);
        bmb.addBuilder(b4);
        bmb.addBuilder(b5);

        //start the loading of the event data process
        readFile(false);

        //check whether registrations has started or not
        // if not then don't let user register for the events
        DatabaseReference ref=db.getReference("Counters").child("start_registrations");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    long value=(long) dataSnapshot.getValue();
                    if (value==1) isRegistrationStarted=true;
                    //todo show somewhere on the screen the live status whether the registrations have started or not and update that too

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("database",databaseError.getMessage());
            }
        });
        //start the tap targets
        startTapTargets();
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

    public void initBmb(){
        bmb.clearBuilders();
        // add a calender event
        TextOutsideCircleButton.Builder builder1 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.ic_calendar)
                .normalText("Save in Calendar")
                .shadowEffect(true)
                .textGravity(Gravity.CENTER)
                .textSize(15)
                .rippleEffect(true)
                .textWidth(Util.dp2px(120))
                .normalColor(Color.parseColor("#FFC107"))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intent= new Intent(Intent.ACTION_INSERT);
                        intent.setType("vnd.android.cursor.item/event")
                                .putExtra(CalendarContract.Events.TITLE,e_name)
                                .putExtra(CalendarContract.Events.EVENT_LOCATION,e_venue)
                                .putExtra(CalendarContract.Events.DESCRIPTION,e_time);
                        DetailedEvent.this.startActivity(intent);
                    }
                });

       bmb.addBuilder(builder1);
        // add contact
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.ic_phone)
                .normalText("Save Event Contact")
                .shadowEffect(true)
                .textGravity(Gravity.CENTER)
                .textSize(15)
                .rippleEffect(true)
                .textWidth(Util.dp2px(120))
                .normalColor(Color.parseColor("#4CAF50"))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        String[] numbers=e_contact.split(",");
                        Intent intent=new Intent(Intent.ACTION_INSERT);
                        intent.setType(ContactsContract.Contacts.CONTENT_TYPE)
                                .putExtra(ContactsContract.Intents.Insert.NAME,e_name+" Contact")
                                .putExtra(ContactsContract.Intents.Insert.NOTES,e_time)
                                .putExtra(ContactsContract.Intents.Insert.COMPANY,"Flair-Fiesta 2k19")
                                .putExtra(ContactsContract.Intents.Insert.EMAIL,"contactus@flairfiesta.com")
                                .putExtra(ContactsContract.Intents.Insert.PHONE,numbers[0]);
                        if (numbers.length>1) intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE,numbers[1]);
                        DetailedEvent.this.startActivity(intent);
                    }
                });
       bmb.addBuilder(builder2);
        //share
        TextOutsideCircleButton.Builder builder3 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.icon_share)
                .normalText("Share")
                .shadowEffect(true)
                .textGravity(Gravity.CENTER)
                .textSize(15)
                .rippleEffect(true)
                .normalColor(Color.parseColor("#FFEB3B"))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        String body="IIIT Kota presents its Annual Techno-Cultural Fest 'Flair-Fiesta 2k19'.\n" +
                                "This fest has this event that i would like to share with you-\n\n" +
                                e_name+"\nWith prizes worth \u20B9 " + e_prize + "/-\n\n" +
                                "For all other details download our Android App from Playstore: https://play.google.com/store/apps/details?id=sahil.iiitk_foundationday_app \n\n" +
                                "Description of the event:\n\n" +
                                e_details;
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Flair-Fiesta 2k18");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT,body);
                        DetailedEvent.this.startActivity(Intent.createChooser(sharingIntent, "Share event via"));
                    }
                });
       bmb.addBuilder(builder3);

        //register
        TextOutsideCircleButton.Builder builder4 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.icon_reg)
                .normalText("Register")
                .shadowEffect(true)
                .textGravity(Gravity.CENTER)
                .textSize(15)
                .rippleEffect(true)
                .normalColor(Color.parseColor("#03A9F4"))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        registerAction();
                    }
                });
        bmb.addBuilder(builder4);
        //add to favourites
         builder5 = new TextOutsideCircleButton.Builder()
                .shadowEffect(true)
                .textGravity(Gravity.CENTER)
                .textSize(15)
                .rippleEffect(true)
                .textWidth(Util.dp2px(120))
                .normalColor(Color.parseColor("#EEEEEE"))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        addToFavourites();
                    }
                });
        if (isFavourite){
            builder5.normalText("Remove favourite")
                    .normalImageRes(R.drawable.ic_favorite);
        }else{
            builder5.normalText("Add as favourite")
                    .normalImageRes(R.drawable.ic_favorite_border);
        }
        bmb.addBuilder(builder5);

    }

    public void registerAction(){
        //check if user has registered in the app or not
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo",MODE_PRIVATE);
        if (!sharedPreferences.getString("FFID","").isEmpty()){
            //open registration page if registration has started
            if (!isRegistrationStarted){
                Toast.makeText(DetailedEvent.this,"Registrations are closed for now!",Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent=new Intent(getApplicationContext(),EventRegActivity.class);
            Bundle bundle=new Bundle();
            bundle.putInt("club_number",club_number);
            bundle.putString("event_name",e_name);
            bundle.putInt("min",min);
            bundle.putInt("max",max);
            bundle.putInt("event_number",event_number);
            intent.putExtras(bundle);
            DetailedEvent.this.startActivity(intent);
        }else{
            Bundle bundle=new Bundle();
            if (!sharedPreferences.getString("name","").isEmpty()){
                bundle.putString("name",sharedPreferences.getString("name",""));
            }
            if (!sharedPreferences.getString("email","").isEmpty()){
                bundle.putString("email",sharedPreferences.getString("email",""));
            }
            if (!sharedPreferences.getString("phone","").isEmpty()){
                bundle.putString("phone",sharedPreferences.getString("phone",""));
            }
            Intent intent=new Intent(getApplicationContext(),Register.class);
            intent.putExtras(bundle);
            DetailedEvent.this.startActivity(intent);
        }
    }

    public Boolean checkIfFavourite(){
        Boolean isTrue=false;
        int num=favourites.getInt("count",0);
        for (int i=1;i<=num;i++){
             if (favourites.getString("event"+i,"").equals(e_name)){
                 isTrue=true;
                 break;
             }
        }
        return isTrue;
    }

    public void addToFavourites(){
        // add favourites functionality by using shared preferences
        int num=favourites.getInt("count",0);
        SharedPreferences.Editor editor=favourites.edit();
        if (isFavourite){
            //remove from favourites
            for (int i=1;i<=num;i++){
                if (favourites.getString("event"+i,"").equals(e_name)){
                    editor.remove("event"+i);
                    editor.apply();
                    builder5.normalText("Add as favourite")
                            .normalImageRes(R.drawable.ic_favorite_border);
                    bmb.setBuilder(4,builder5);
                    Toast.makeText(this,e_name+" removed from favourites!",Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }else{
            editor.putString("event"+(num+1),e_name);
            editor.putInt("count",num+1);
            editor.apply();
            builder5.normalText("Remove favourite")
                    .normalImageRes(R.drawable.ic_favorite);
            bmb.setBuilder(4,builder5);
            Toast.makeText(this,e_name+" added as favourite!",Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadEventData(final Boolean isCached){
        Log.e("file","downloading , cache available: "+isCached.toString());
        //download the json file of this event from firebase
        Log.e("file","downloading file of this event.");
        StorageReference ref=storage.getReference().child("events_json/event"+club_number+""+event_number+".json");
        File file;
        temp=new File(path,"temp.json");
        //create an empty file to store download
        if (isCached) file=temp;
        else file=new File(path,"event"+club_number+""+event_number+".json");

        ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("file","File download success!");
                readFile(isCached);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("file","File download failed!\nError: "+e.getMessage());
                dialog.dismiss();
                Toast.makeText(DetailedEvent.this, "Network error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void readFile(Boolean readTemp){
        Log.e("file","Reading from temp: "+readTemp.toString());
        File file;
        if (readTemp) file=temp;
        else file=new File(path,"event"+club_number+""+event_number+".json");

        int length=(int) file.length();
        byte[] bytes=new byte[length];
        try{
            FileInputStream in=new FileInputStream(file);
            try{
                in.read(bytes);
                in.close();
                String contents=new String(bytes);
                Log.e("file","File found and parsing ...");
                //Log.e("file","File read.: \n"+contents);
                //to show the saved data to the user
                parseJSON(contents,readTemp);
                //to download the file to check for updates
                if (!readTemp) downloadEventData(true);
            }catch (IOException e){
                Log.e("file",""+e.getMessage());
                dialog.cancel();
                Toast.makeText(DetailedEvent.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                //some error occurred in file , download data again
                Log.e("file","an error occurred in event file. have to download.");
                downloadEventData(false);
            }
        }catch (FileNotFoundException e){
            Log.e("file","Event File not found in cache. have to download.");
            //no cache found , download data now
            downloadEventData(false);
        }
    }

    public void parseJSON(String text,Boolean checkUpdate){
        try{
            JSONObject object=new JSONObject(text);
            if (checkUpdate){
                if (version<object.getInt("version")){
                    Log.e("file","Update found for this event.");
                    temp.delete();
                    File toUpdate=new File(path,"event"+club_number+""+event_number+".json");
                    try{
                        FileOutputStream stream=new FileOutputStream(toUpdate);
                        try{
                            stream.write(text.getBytes());
                            stream.close();
                            Log.e("file","Event file updated.\nShowing new data to user");
                            showData(object);
                            Toast.makeText(this, "Event details have been updated!", Toast.LENGTH_LONG).show();
                        }catch (IOException e){
                            Log.e("file","During file update :\n"+e.getMessage());
                        }
                    }catch (FileNotFoundException e){
                        Log.e("file","During file update :\n"+e.getMessage());
                    }
                } else Log.e("file","No update found for this event.");
            }else{
                showData(object);
                // check if this event is already in favourites
                isFavourite=checkIfFavourite();
                //initialise boom menu
                initBmb();
            }

            dialog.dismiss();
            Log.e("file","JSON parsing complete.\n ");
        }catch(JSONException e){
            Log.e("file",e.getMessage());
            dialog.dismiss();
            Toast.makeText(this, "An error occurred! Please Reload page.", Toast.LENGTH_SHORT).show();
        }
    }

    public void showData(JSONObject object){
        Log.e("file","Showing data for this event.");
        //get the data and show in the views
        try{
            version=object.getInt("version");
            e_name=object.getString("name");
            e_time=object.getString("time");
            e_contact=object.getString("contact");
            e_venue=object.getString("venue");
            e_details=object.getString("details");
            e_prize=object.getString("prize");
            min=object.getInt("min_members");
            max=object.getInt("max_members");

            collapsingToolbarLayout.setTitle(e_name);
            event_date_time.setText(e_time);
            event_contact.setText("Contact: "+e_contact);
            event_venue.setText(e_venue);
            event_detail.setText(e_details);
            event_prize.setText("Prizes worth \u20B9 "+e_prize+"/-");
            event_regfee.setText("Registration Fee: \u20B9 "+object.getString("fee")+"/-");
            event_last_date.setText("Last date to pay registration fee: "+object.getString("last_date"));
            event_type.setText(object.getString("event_type"));

            String folderName="";
            //default value
            int placeholder=R.drawable.ic_club_placeholder;
            switch (club_number){
                case 0: folderName="technical/";
                    placeholder=R.drawable.detail_technical;
                    break;
                case 1: folderName="cultural/";
                    placeholder=R.drawable.detail_cultural;
                    break;
                case 2: folderName="literary/";
                    placeholder=R.drawable.detail_literary;
                    break;
                case 3: folderName="photography/";
                    placeholder=R.drawable.detail_photography;
                    break;
            }
            StorageReference ref=storage.getReference().child(folderName+object.getString("image_name"));
            Glide.with(DetailedEvent.this).using(new FirebaseImageLoader())
                    .load(ref).placeholder(placeholder).into(event_picture);
        }catch(JSONException e){
            Log.e("file",e.getMessage());
            dialog.dismiss();
            Toast.makeText(this, "An error occurred! Please Reload page.", Toast.LENGTH_SHORT).show();
        }
    }

    public void startTapTargets(){
        if (sequencePref.getBoolean("seq_reg",false)) return;

        TapTargetView.showFor(DetailedEvent.this,
                TapTarget.forView(bmb,"Register here","Click here to register for the event and other" +
                        " cool options!").transparentTarget(true).cancelable(false)
                ,new TapTargetView.Listener(){
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        //save in preferernces so that this is only shown when you open app first time
                        SharedPreferences.Editor editor=sequencePref.edit();
                        editor.putBoolean("seq_reg",true);
                        editor.apply();
                    }
                }
                );
    }


}
