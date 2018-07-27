package sahil.iiitk_foundationday_app.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.mail.GMailSender;
import sahil.iiitk_foundationday_app.model.User;

public class Register extends AppCompatActivity
{
    Button btn;
    TextView reg_later;
    public FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public SharedPreferences userdetails;
    public EditText name, college_id, department, phone, email, myEditText;
    public RadioGroup radioGroup, mocGroup;
    public RadioButton radioButton, mocButton;
    RelativeLayout mRlayout;
    Spinner col,s;
    public int selectedId, mocID, otherstrue = 0;
    public String regphone = "^[6789]\\d{9}$";
    public String gender = "Female", body = "", bundle_name = "", bundle_email = "",
            bundle_phone = "", year = "", college = "", mos = "Hosteller",username="",password="";
    User user = new User();
    String[] arraySpinner = new String[] {
            "First", "Second", "Third", "Fourth","Fifth","Faculty"
    };
    String[] arraySpinner2 = new String[]{
            "IIIT KOTA", "MNIT JAIPUR", "Others"
    };
    File path;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        path=getCacheDir();
        new DownloadTask().execute();

        btn = (Button)findViewById(R.id.submit);
        name = (EditText)findViewById(R.id.name_input);
        college_id = (EditText)findViewById(R.id.college_id_input);
        department = (EditText)findViewById(R.id.branch_input);
        phone = (EditText)findViewById(R.id.mobile_input);
        email = (EditText)findViewById(R.id.email_input);
        reg_later=(TextView)findViewById(R.id.reg_later);
        col=findViewById(R.id.collegespin);
        s=findViewById(R.id.Year);
        mRlayout = (RelativeLayout) findViewById(R.id.rel);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.getString("name") != null) bundle_name=extras.getString("name");
            if(extras.getString("email") != null){
                bundle_email=extras.getString("email");
                email.setText(bundle_email);
                email.setEnabled(false);
            }
            if(extras.getString("phone") != null){
                bundle_phone=extras.getString("phone");
                phone.setText(bundle_phone);
                phone.setEnabled(false);
            }
            name.setText(bundle_name);
        }
        reg_later.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences userdetails = getSharedPreferences("userInfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor=userdetails.edit();
                        if (!bundle_email.isEmpty()){
                            editor.putString("email",bundle_email);
                        }
                        if(!bundle_name.isEmpty()){
                            editor.putString("name",bundle_name);
                        }
                        if (!bundle_phone.isEmpty()){
                            editor.putString("phone",bundle_phone);
                        }
                        editor.putString("status","true");
                        editor.apply();
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = adapterView.getSelectedItemPosition();
                year = arraySpinner[pos];
            }
            public void onNothingSelected(AdapterView<?> adapterView){
                TextView errorText = (TextView)s.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText("Please enter the Year");//changes the selected item text to this
            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, arraySpinner2);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        col.setAdapter(adapter1);
        col.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = adapterView.getSelectedItemPosition();
                college = arraySpinner2[pos];
                if(college.equals("Others")){
                    spin();
                }else{
                    if (otherstrue==1){
                        mRlayout.setVisibility(View.GONE);
                        // hide the other college name field
                    }
                    otherstrue=0;
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView){
                TextView errorText = (TextView)col.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText("Please enter your College");//changes the selected item text to this
            }
        });

        //read the primary data for the app to get confirmation email account details
        readFile();
    }

    public void spin(){
        otherstrue = 1;
        mRlayout.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams mRparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myEditText = new EditText(this);
        myEditText.setLayoutParams(mRparams);
        myEditText.setHint("Enter your College Name");
        myEditText.setTextColor(ContextCompat.getColor(this, R.color.black));
        myEditText.setHintTextColor(ContextCompat.getColor(this, R.color.grey700));
        ViewCompat.setBackgroundTintList(myEditText, ColorStateList.valueOf(Color.parseColor("#dddddd")));
        mRlayout.addView(myEditText);
    }

    public void onRadioButtonClicked(View view){
        radioGroup = (RadioGroup) findViewById(R.id.rad);
        selectedId = radioGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radioButton = (RadioButton) findViewById(selectedId);
        gender = radioButton.getText().toString();
    }

    public void onMocButtonClicked(View view){
        mocGroup = (RadioGroup)findViewById(R.id.moc);
        mocID = mocGroup.getCheckedRadioButtonId();

        mocButton = (RadioButton)findViewById(mocID);
        mos = mocButton.getText().toString();
    }

    //this method was coded by tanuj
    public void getFFid(){
        // Write a message to the database
        final DatabaseReference myRef = database.getReference("Counters").child("FFID");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long uid=(long)dataSnapshot.getValue();
                //to generate random IDs so that user's can never guess what will be new FFID
                Random rand=new Random();
                int n=rand.nextInt(3)+1;
                uid=uid+n;
                dataSnapshot.getRef().setValue(uid);
                myRef.removeEventListener(this);
                //increase the total user counters by one
                increaseTotalUserCounter(uid);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void increaseTotalUserCounter(final long uid){
        final DatabaseReference ref = database.getReference("Counters").child("total_users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count=(long)dataSnapshot.getValue();
                dataSnapshot.getRef().setValue(count+1);
                ref.removeEventListener(this);
                //now send a confirmation email
                sendEmail(uid);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }

    //this method was coded by tanuj
    //sending emails automatically
    public void sendEmail(long ffid){
        regSecondStage(ffid);
        body="Hi,\n"+name.getText().toString()+" ( "+email.getText().toString()+" )\nYour registration ID for Flair Fiesta 2k18 is FF"+ffid+" . \nYou have successfully registered for Flair Fiesta 2k18, annual cultural-technical fest of" +
                " IIIT Kota.\nWe would be glad to see you on 23rd and 24th March,2018.\n\nRegards\nAdmin";
        final String recepient;
        if (email.getText().toString().isEmpty()){
            Toast.makeText(this,"Email is not provided!",Toast.LENGTH_SHORT).show();
            return;
        }else{
            recepient=email.getText().toString();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(username, password);
                    sender.sendMail("Your Registration for FlairFiesta 2k18 is Confirmed.",
                            body,
                            username,
                            recepient);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        }).start();

    }

    public boolean validateInputs(){
        int flag = 0;
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.setError("Enter correct Email Address");
            flag = 1;
        }
        if((!phone.getText().toString().matches(regphone)) || (phone.getText().toString().length() == 0)){
            phone.setError("Enter correct 10 digit Mobile Number");
            flag = 1;
        }

        if( name.getText().toString().length() == 0 ){
            name.setError( "Name is required!" );
            flag = 1;
        }

        if (otherstrue==1) college=myEditText.getText().toString();
        if(college.length() == 0){
            Toast.makeText(this, "Enter appropriate College", Toast.LENGTH_SHORT).show();
            flag = 1;
        }

        if( college_id.getText().toString().length() == 0 ) {
            college_id.setError("College ID is required!");
            flag = 1;
        }

        if( department.getText().toString().length() == 0 ) {
            department.setError("Department is required!");
            flag = 1;
        }

        if(selectedId < 0){
            radioButton.setError("Gender is required!");
            flag = 1;
        }

        if(year.length() == 0){
            Toast.makeText(this, "Enter correct year", Toast.LENGTH_SHORT).show();
            flag = 1;
        }

        if(mocID < 0){
            mocButton.setError("Mode of Stay is required!");
            flag = 1;
        }

        if(flag == 0)
            return true;
        else
            return false;
    }

    //this method was coded by tanuj
    public void sendMessage(View view) {
        btn.setEnabled(false);
        database = FirebaseDatabase.getInstance();
        boolean value = validateInputs();
        if(value){
            user.setName(name.getText().toString().trim());
            user.setDepartment(department.getText().toString().trim());
            user.setPhone(phone.getText().toString().trim());
            user.setEmail(email.getText().toString().trim());
            if(otherstrue == 1){
                user.setCollege(myEditText.getText().toString().trim());
            }
            else{
                user.setCollege(college);
            }
            user.setCollegeid(college_id.getText().toString().trim());
            user.setGender(gender);
            user.setYear(year);
            user.setMos(mos);
            getFFid();
        }
        else{
            Toast.makeText(this, "Enter correct details", Toast.LENGTH_SHORT).show();
            btn.setEnabled(true);
        }
    }

    //this method was coded by tanuj
    public void regSecondStage(long id){
        user.setUser_id("FF"+id);
        List<Long> empty_list=new ArrayList<>();
        user.setDone_questions(empty_list);
        user.setQuiz_lives(5);
        user.setQuiz_correct(0);
        DatabaseReference mRef = database.getReference().child("Users");
        mRef.push().setValue(user);
        Toast.makeText(getApplicationContext(),"Your FFID is : FF"+id,Toast.LENGTH_LONG).show();

        userdetails = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor=userdetails.edit();
        editor.putString("name",name.getText().toString());
        editor.putString("department",department.getText().toString());
        editor.putString("phone",phone.getText().toString());
        editor.putString("email",email.getText().toString());
        if(otherstrue == 1){
            editor.putString("college", myEditText.getText().toString());
        }
        else{
            editor.putString("college",college);
        }
        editor.putString("collegeid",college_id.getText().toString());
        editor.putString("gender", gender);
        editor.putString("Year", year);
        editor.putString("MOS", mos);
        editor.putString("FFID", "FF"+id);
        editor.putString("status","true");
        editor.apply();
        Intent intent=new Intent(this,MainActivity.class);
        this.startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent i = new Intent(getApplicationContext(),Login_Screen.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("file","Primary data File download failed!\nError: "+e.getMessage());
                        Toast.makeText(Register.this,"Network error! Please reopen app with Data connection ON, otherwise you may not receive confirmation email.",Toast.LENGTH_LONG).show();
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
            JSONObject emailDetails=object.getJSONObject("email_account");
            username=emailDetails.getString("email_id");
            password=emailDetails.getString("email_password");

            Log.e("file","Primary file parsing done.");
        }catch(JSONException e){
            Log.e("file",e.getMessage());
            Toast.makeText(this, "Network error occurred! Please Reload page.", Toast.LENGTH_SHORT).show();
        }
    }
}
