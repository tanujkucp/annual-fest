package sahil.iiitk_foundationday_app.views;
// Made by Tanuj
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nightonke.boommenu.Animation.EaseEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.MyToast;
import sahil.iiitk_foundationday_app.adapters.NotifAdapter;
import sahil.iiitk_foundationday_app.model.AdminIDs;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,MainFragment2.PrimaryDescriptionInterface
,HelplineFragment.PrimaryContactsInterface,ScheduleFragment.PrimaryScheduleInterface{

    private ViewPager mViewPager;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ArrayList<String> titles;
    ImageSwitcher BackGround;
    ArrayList<String> backgrounds;
    private boolean backPressedToExitOnce = false;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    TextView nav_ffid;
    String dialogue_entry;
    FirebaseDatabase db;
    DatabaseReference ref;
    AdminIDs admin = new AdminIDs();
    ValueEventListener listener;
    Button gotoMyactivity;
    BoomMenuButton bmb;
    FirebaseStorage storage;
    int switcherPosition=0;
    Timer timer;
    List<Bitmap> bitmaps=new ArrayList<>();
    File path;
    Boolean isPrimaryFileRead=false;
    String pri_about;
    JSONArray pri_contacts,pri_schedule,pri_images;
    SharedPreferences sharedPreferences;
    SharedPreferences sequencePref;
    ProgressDialog dialog;


    //interface methods to get data from primary file
    @Override
    public String getDescription() {
        if (!isPrimaryFileRead) readFile();
        return pri_about;
    }

    @Override
    public JSONArray getContacts() {
        if (!isPrimaryFileRead) readFile();
        return pri_contacts;
    }

    @Override
    public JSONArray getSchedule() {
        if (!isPrimaryFileRead) readFile();
        return pri_schedule;
    }
    /////interface methods end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bmb = findViewById(R.id.home_bmb);
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        sequencePref=getSharedPreferences("sequence",MODE_PRIVATE);
        storage=FirebaseStorage.getInstance();
        path=getCacheDir();
        new DownloadTask().execute();

        collapsingToolbarLayout =findViewById(R.id.coll);
        collapsingToolbarLayout.setTitleEnabled(true);
        BackGround = findViewById(R.id.BG);
        titles = new ArrayList<>();
        {
            titles.add("About");
            titles.add("Events");
            titles.add("Schedule");
            //todo add sponsors if needed
            //titles.add("Sponsors");
            titles.add("Contacts");
            titles.add("Team");
        }
        //start shuffling images on the home page
        BackGround.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(MainActivity.this);
            }
        });

        collapsingToolbarLayout.setTitle(titles.get(0));
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                //Log.e("page", titles.get(position));
                collapsingToolbarLayout.setTitle(titles.get(position));
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView notif_nav = findViewById(R.id.nav_view_2);
        mRecyclerView = notif_nav.getHeaderView(0).findViewById(R.id.notif_recycler);

        //showing user's FFID if it exists
        nav_ffid = navigationView.getHeaderView(0).findViewById(R.id.nav_ffid);
        gotoMyactivity = navigationView.getHeaderView(0).findViewById(R.id.nav_myactivity_button);
        if (!sharedPreferences.getString("FFID", "").isEmpty()) {
            nav_ffid.setText("Your FFID is " + sharedPreferences.getString("FFID", ""));
        }
        gotoMyactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("FFID", "").isEmpty()) {
                    new MyToast(MainActivity.this, "Please register first!",false).show();
                }else{
                    Intent intent = new Intent(MainActivity.this, UserActivityPage.class);
                    MainActivity.this.startActivity(intent);
                }

            }
        });

        //getting notifications
        getNotifications();

        //setup boom menu
        initBmb();

        //read the primary file data
        readFile();

        dialog=new ProgressDialog(MainActivity.this,R.style.AlertDialogCustom);
        dialog.setCancelable(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer!=null){
            timer.cancel();
            Log.e("switcher","timer stopped");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bitmaps!=null && backgrounds!=null){
            if (bitmaps.size()==backgrounds.size()){
                startAnimation();
                Log.e("switcher","starting animation again after pause");
            }
        }

    }

    public void startImageSwitcher(JSONArray array){
        Log.e("switcher","starting download of images");
        BackGround.setInAnimation(AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left));
        BackGround.setOutAnimation(AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right));
        // add other images which change repeatedly with image switcher
        backgrounds=new ArrayList<>();
        try{
            for (int i=0;i<array.length();i++){
                backgrounds.add(array.getString(i));
            }
            downloadImage(switcherPosition);
        }catch (JSONException e){
            Log.e("switcher",e.getMessage());
        }

    }

    public void downloadImage(final int pos){
        StorageReference ref=storage.getReference("home_images/"+backgrounds.get(pos));
        Glide.with(MainActivity.this).using(new FirebaseImageLoader()).load(ref).asBitmap()
                .listener(new RequestListener<StorageReference, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, StorageReference model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        bitmaps.add(resource);
                        Log.e("switcher","Image: "+pos+" is ready");
                        switcherPosition++;
                        if (switcherPosition==backgrounds.size()) startAnimation();
                        else downloadImage(switcherPosition);
                        return true;
                    }
                }).placeholder(R.drawable.ffposter).centerCrop().into((ImageView)BackGround.getCurrentView());
    }

    public void startAnimation(){
        Log.e("switcher","all images ready. Starting animation");
        if (timer!=null) timer.cancel();
        timer=new Timer();
        switcherPosition=0;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!bitmaps.get(switcherPosition).isRecycled()){
                            BackGround.setImageDrawable(new BitmapDrawable(getResources(),bitmaps.get(switcherPosition)));
                        }
                        switcherPosition++;
                        if (switcherPosition==backgrounds.size()) switcherPosition=0;
                    }
                });
            }
        },0,3000);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MainFragment2(), "ABOUT");
        adapter.addFrag(new ClubsFragment(), "EVENTS");
        adapter.addFrag(new ScheduleFragment(), "SCHEDULE");
        //adapter.addFrag(new SponsorsFragment(), "SPONSORS");
        adapter.addFrag(new HelplineFragment(), "CONTACTS");
        adapter.addFrag(new TeamFragment(), "TEAM");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bell) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.END);
            return true;
        }
        if (id == R.id.logoutInside) {
            final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(MainActivity.this);
            dialog.setCancelable(true);
            dialog.setMessage("Please confirm to logout!");
            dialog.setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
                    GoogleSignInClient client = GoogleSignIn.getClient(MainActivity.this, gso);
                    client.signOut();
                    Intent intent = new Intent(MainActivity.this, Login_Screen.class);
                    MainActivity.this.startActivity(intent);
                    new MyToast(MainActivity.this, "Logged out successfully!").show();
                    MainActivity.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            //double back press logic
            if (backPressedToExitOnce) {
                super.onBackPressed();
            } else {
                this.backPressedToExitOnce = true;
                new MyToast(MainActivity.this, "Press again to exit").show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backPressedToExitOnce = false;
                    }
                }, 2000);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_register) {
            if (!sharedPreferences.getString("FFID", "").isEmpty()) {
                new MyToast(MainActivity.this, "You are already registered!",Toast.LENGTH_SHORT,true).show();
            } else {
                Bundle bundle = new Bundle();
                if (!sharedPreferences.getString("name", "").isEmpty()) {
                    bundle.putString("name", sharedPreferences.getString("name", ""));
                }
                if (!sharedPreferences.getString("email", "").isEmpty()) {
                    bundle.putString("email", sharedPreferences.getString("email", ""));
                }
                if (!sharedPreferences.getString("phone", "").isEmpty()) {
                    bundle.putString("phone", sharedPreferences.getString("phone", ""));
                }
                Intent intent = new Intent(this, Register.class);
                intent.putExtras(bundle);
                this.startActivity(intent);
                finish();
            }

        } else if (id == R.id.nav_reaches) {
            Intent intent = new Intent(this, MapActivity.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_queries) {
            //check if the user has an account in the app or not
            if (sharedPreferences.getString("FFID", "").isEmpty()) {
                new MyToast(this, "Please register first to chat!",false).show();
            }else{
                Intent intent=new Intent(this,ContactUs.class);
                intent.putExtra("isAdmin",false);
                MainActivity.this.startActivity(intent);
            }

        }
        else if (id == R.id.nav_email){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",getString(R.string.contact_email), null));
            startActivity(Intent.createChooser(emailIntent, "Send Email via"));
        } else if (id == R.id.nav_quiz) {
            dialog.setMessage("contacting server...");
            dialog.show();
            //check if the user has an account in the app or not
            if (sharedPreferences.getString("FFID", "").isEmpty()) {
                new MyToast(this, "You have to register in the App to play game.",false).show();
            } else {
                // check if quiz is open or not by using  a value stored on firebase
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("Counters").child("start_quiz");
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.e("home", "start_quiz variable found.");
                            long start_quiz = (long) dataSnapshot.getValue();
                            if (start_quiz == 1) {
                                Log.e("home", "Starting quiz.");
                                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                                MainActivity.this.startActivity(intent);
                            } else {
                                Log.e("home", "Quiz not started yet.");
                                new MyToast(MainActivity.this, "Quiz has not been started yet!\nCome back soon.",false).show();
                            }
                            dialog.dismiss();
                            myRef.removeEventListener(listener);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("home", "Failed to read value: " + databaseError.getDetails());
                    }
                };
                myRef.addListenerForSingleValueEvent(listener);
            }
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Flair-Fiesta 2k18");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Download Flair-Fiesta 2k18 from Play Store. https://play.google.com/store/apps/details?id=sahil.iiitk_foundationday_app ");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }  else if (id == R.id.nav_admin) {
            launchAdmin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Log.e("taptarget","inflating menu");
        //need to fire this method after some time because menu is taking time to inflate
        //otherwise it will crash
        // change the delay here accordingly
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startTapTargets();
            }
        },1000);

        return true;
    }

    public void getNotifications() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Notification");
        Query query = ref.orderByChild("notif_id");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    // collectNotifications((Map<String, Object>) dataSnapshot.getValue());
                    new NotificationTask().execute((Map<String, Object>) dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private class NotificationTask extends AsyncTask<Map<String, Object>,Void,Void>{
        @Override
        protected Void doInBackground(Map<String, Object>... values) {
            Map<String, Object> users=values[0];
            //iterate through each notification
            HashMap<Long, String> details = new HashMap<Long, String>();
            HashMap<Long, String> times = new HashMap<Long, String>();
            HashMap<Long, String> club_names = new HashMap<Long, String>();
            List keys;
            final ArrayList<String> info = new ArrayList<>();
            final ArrayList<String> when = new ArrayList<>();
            final ArrayList<String> which_club = new ArrayList<>();
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                //Get user map
                Map singleUser = (Map) entry.getValue();
                details.put((Long) singleUser.get("notif_id"), (String) singleUser.get("details"));
                times.put((Long) singleUser.get("notif_id"), (String) singleUser.get("time"));
                club_names.put((Long) singleUser.get("notif_id"), (String) singleUser.get("which_club"));
            }
            //sort the notifications
            keys = new ArrayList(details.keySet());
            Collections.sort(keys);
            int max_notif_id = keys.size();
            //Fill array which is sorted and ready
            for (int i = 0; i < keys.size(); i++) {
                info.add(details.get(keys.get(i)));
                when.add(times.get(keys.get(i)));
                which_club.add(club_names.get(keys.get(i)));
            }
            //copy info list for use in alerting by notification
            ArrayList<String> copy = new ArrayList<>();
            for (int i = 0; i < info.size(); i++) {
                String x = info.get(i);
                copy.add(x);
            }
            //reverse all lists so that newest notification comes on top
            Collections.reverse(info);
            Collections.reverse(when);
            Collections.reverse(which_club);
            //showing notifications in cards
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showNotifCard(info, when, which_club);
                }
            });

            //getting last seen notification from sharedpreferences
            //a default number so that we can differentiate between cases when user is logging first time or
            //was already logged in
            int default_seen_notif = -1;
            SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
            int n = pref.getInt("last_notif", default_seen_notif);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("last_notif", max_notif_id);
            editor.apply();
            Log.e("notif", "" + n + ", " + max_notif_id);
            //displaying these notifications in user's notification panel
            if (n != default_seen_notif) {
                for (int i = n; i < max_notif_id; i++) {
                    alertUser(copy.get(i), i);
                }
            }
            return null;
        }
    }

    private void showNotifCard(ArrayList<String> info, ArrayList<String> when, ArrayList<String> which) {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NotifAdapter(info, when, which);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void alertUser(String a, int NOTIFICATION_ID) {
        Log.e("notif", "notifying: " + a);
//        //creating  a notification channel
        //todo setup notifications for newer versions of android >=26 OREO
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//// The id of the channel.
//        String id = "my_channel";
//// The user-visible name of the channel.
//        CharSequence name = "Flair-Fiesta";
//// The user-visible description of the channel.
//        String description ="Flair-Fiesta";
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//// Configure the notification channel.
//        mChannel.setDescription(description);
//        mChannel.enableLights(true);
//// Sets the notification light color for notifications posted to this
//// channel, if the device supports this feature.
//        mChannel.setLightColor(Color.RED);
//        mChannel.enableVibration(true);
//        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//        mNotificationManager.createNotificationChannel(mChannel);

        if (Build.VERSION.SDK_INT < 26) {
            //notification for devices running SDK<26
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher_round_small)
                            .setContentTitle("FlairFiesta 2k18")
                            .setTicker(a)
                            .setAutoCancel(true)
                            .setPriority(1000)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentText(a);
            Intent intent = new Intent(this, Splash_Activity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.notify(NOTIFICATION_ID, builder.build());
        }

    }

    private void launchAdmin() {
        //launch a dialogue to verify Admin ID
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Admin ID");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        //   Set up the buttons
        builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogue_entry = input.getText().toString();
                dialog.cancel();
                checkAdminID();
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

    private void checkAdminID() {
        dialog.setMessage("checking Admin ID");
        dialog.show();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Admin");
        Query query = ref;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.e("adminpage", "Data Snapshot exists!");
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    getAdminIDs(data);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAdminIDs(Map<String, Object> users) {
        ArrayList<String> adminIDs = new ArrayList<>();
        ArrayList<String> adminNames = new ArrayList<>();
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            //Get admin user map
            Map singleUser = (Map) entry.getValue();
            adminIDs.add((String) singleUser.get("id"));
            adminNames.add((String) singleUser.get("name"));
        }

        // Log.e("adminpage",adminIDs.toString());
        if (adminIDs.contains(dialogue_entry)) {
            Log.e("adminpage", "Admin ID confirmed!");
            int index = adminIDs.indexOf(dialogue_entry);
            String club_name = adminNames.get(index);
            new MyToast(MainActivity.this, "Welcome Admin!").show();
            dialog.dismiss();
            //goto Admin page
            Intent intent = new Intent(getApplicationContext(), AdminPage.class);
            intent.putExtra("club_name", club_name);
            this.startActivity(intent);
        } else {
            Log.e("adminpage", "Wrong Admin ID");
            dialog.dismiss();
            new MyToast(this, "Wrong Admin ID!",false).show();
        }
    }

    //use when need to put more adminIDs
    private void putAdminIDs() {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Admin");
        admin.setName("Fine_arts_and_photography_club");
        admin.setId("abc");
        ref.push().setValue(admin);
        Log.e("adminpage", "One Admin ID pushed!");
    }

    public void initBmb() {
        // add facebook button
        TextOutsideCircleButton.Builder builder1 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.icon_facebook)
                .normalText("Facebook")
                .normalColor(Color.parseColor("#9FA8DA"))
                .shadowEffect(true)
                .rotateText(false)
                .textGravity(Gravity.CENTER)
                .textSize(15)
                .rippleEffect(true)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://www.facebook.com/iiitkfd"));
                        MainActivity.this.startActivity(i);
                    }
                });
        bmb.addBuilder(builder1);
        // add instagram button
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.icon_instagram)
                .normalText("Instagram")
                .shadowEffect(true)
                .rotateText(false)
                .normalColor(Color.parseColor("#F48FB1"))
                .textGravity(Gravity.CENTER)
                .textSize(15)
                .rippleEffect(true)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://www.instagram.com/flairfiesta/"));
                        MainActivity.this.startActivity(i);
                    }
                });
        bmb.addBuilder(builder2);
        // add flair fiesta web button
        TextOutsideCircleButton.Builder builder3 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.ic_launcher_round_small)
                .normalText("flairfiesta.com")
                .shadowEffect(true)
                .rotateText(false)
                .textGravity(Gravity.CENTER)
                .textSize(15)
                .normalColor(Color.parseColor("#E0E0E0"))
                .rippleEffect(true)
                .textWidth(Util.dp2px(100))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("http://www.flairfiesta.com/"));
                        MainActivity.this.startActivity(i);
                    }
                });
        bmb.addBuilder(builder3);
        // add iiit kota website button
        TextOutsideCircleButton.Builder builder4 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.icon_iiitkota)
                .normalText("iiitkota.ac.in")
                .shadowEffect(true)
                .rotateText(false)
                .normalColor(Color.parseColor("#FFCC80"))
                .textGravity(Gravity.CENTER)
                .textSize(15)
                .rippleEffect(true)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("http://www.iiitkota.ac.in/"));
                        MainActivity.this.startActivity(i);
                    }
                });
        bmb.addBuilder(builder4);
    }

    private class DownloadTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            //download the json file from firebase
            StorageReference ref=storage.getReference().child("primary.json");
            File file=new File(path,"primary.json");
            if (!file.exists()){
                Log.e("file","Primary data File does not exist ... downloading...!");
                ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.e("file","Primary data File download success!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MyToast(MainActivity.this,"Details have been loaded, Please close the app and restart.",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("file","Primary data File download failed!\nError: "+e.getMessage());
                    }
                });
            }
            return null;
        }
    }

    public void readFile(){
        File file=new File(path,"primary.json");
        int length=(int) file.length();
        byte[] bytes=new byte[length];
        try{
            FileInputStream in=new FileInputStream(file);
            try{
                in.read(bytes);
                in.close();
                String contents=new String(bytes);
                Log.e("file","Primary File found and parsing...");
                // Log.e("file","File read.: \n"+contents);
                parseJSON(contents);
                //to download the file to check for updates
            }catch (IOException e){
                Log.e("file",""+e.getMessage());
                Log.e("file","An error occurred. have to download.");
                new MyToast(this, "An error occurred in fetching details!",false).show();
                //some error occurred in file , download data again
            }
        }catch (FileNotFoundException e){
            Log.e("file","File not found in cache. have to download.");
        }
    }

    public void parseJSON(String text) {
        try{
            JSONObject object=new JSONObject(text);
            //get data out of this object
            pri_about=object.getString("description");
            pri_contacts=object.getJSONArray("contacts");
            pri_schedule=object.getJSONArray("schedule");
            pri_images=object.getJSONArray("home_images");
            startImageSwitcher(pri_images);

            JSONObject emailDetails=object.getJSONObject("email_account");
            String id=emailDetails.getString("email_id");
            String pass=emailDetails.getString("email_password");
            SharedPreferences preferences=getSharedPreferences("email_account",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("email_id",id);
            editor.putString("email_password",pass);
            editor.apply();

            isPrimaryFileRead=true;
            Log.e("file","Primary file parsing done.");
        }catch(JSONException e){
            Log.e("file",e.getMessage());
            new MyToast(this, "An error occurred! Please Reload page.",false).show();
        }
    }

    public void startTapTargets(){
        if (sequencePref.getBoolean("seq_home",false)) return;

        List<TapTarget> targets=new ArrayList<>();
        // customise these targets with different colors and styles
        targets.add(TapTarget.forToolbarNavigationIcon(toolbar,"Explore Menu"
                ,"See your Profile, Venue Map, Contact options, and many more...")
                .outerCircleColor(R.color.orange)
                .outerCircleAlpha(0.9f)
                .descriptionTextAlpha(1f)
                .titleTextSize(25)
                .descriptionTextColor(R.color.white)
                .id(1).cancelable(false).transparentTarget(false));

        Log.e("taptarget","adding tap target on logout");
        targets.add(TapTarget.forToolbarMenuItem(toolbar,R.id.logoutInside,"Logout Button"
                ,"Click to Logout anytime if you want to login with another Flair Fiesta Account!")
                .outerCircleColor(R.color.pink800)
                .outerCircleAlpha(0.9f)
                .descriptionTextAlpha(1f)
                .titleTextSize(25)
                .descriptionTextColor(R.color.white)
                .id(2).cancelable(false).transparentTarget(false));

        targets.add(TapTarget.forToolbarMenuItem(toolbar,R.id.bell,"Broadcasts"
                ,"See general and event related broadcasts from organisers here!")
                .outerCircleColor(R.color.blue800)
                .outerCircleAlpha(0.9f)
                .descriptionTextAlpha(1f)
                .titleTextSize(25)
                .descriptionTextColor(R.color.white)
                .id(3).cancelable(false).transparentTarget(false));

        targets.add(TapTarget.forView(findViewById(R.id.home_bmb),"We are social!"
                ,"Connect with us on facebook, instagram and website too...").id(4)
                .cancelable(false)
                .outerCircleColor(R.color.green)
                .outerCircleAlpha(0.9f)
                .titleTextSize(25)
                .descriptionTextAlpha(1f)
                .descriptionTextColor(R.color.white)
                .transparentTarget(true));

        TapTargetSequence sequence=new TapTargetSequence(MainActivity.this)
                .targets(targets)
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        //save in preferernces so that this is only shown when you open app first time
                        SharedPreferences.Editor editor=sequencePref.edit();
                        editor.putBoolean("seq_home",true);
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
