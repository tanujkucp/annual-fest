package sahil.iiitk_foundationday_app.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import sahil.iiitk_foundationday_app.R;

public class Splash_Activity extends AppCompatActivity {
    // Splash Screen
    private TextView tv;
    private ImageView iv;
    FirebaseStorage storage;
    File path;
    Thread loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);
        path=getCacheDir();
        storage=FirebaseStorage.getInstance();

        //set GIF background
        LinearLayout back=findViewById(R.id.splash_back);
        try{
            GifDrawable image=new GifDrawable(getResources(),R.drawable.splash_back);
            back.setBackground(image);
        }catch (IOException e){
            Log.e("splash",e.getMessage());
        }
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status));
        }

        Typeface type = Typeface.createFromAsset(getAssets(), "font/Sofia-Regular.otf");
        tv.setTypeface(type);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        tv.startAnimation(myanim);
        iv.startAnimation(myanim);
        //hold this activity to show splash screen to the user and download necessary files for app
        loading = new Thread(){
            public void run()
            {
                try{
                    // add sleep
                    sleep(4000);
                    Intent i;
                       if (isLoggedIn()){
                           i = new Intent(Splash_Activity.this,MainActivity.class);
                       }else{
                           i=new Intent(Splash_Activity.this,Login_Screen.class);
                       }
                        startActivity(i);
                        finish();
                }
                catch (Exception ex)
                {
                    Log.e("thread",""+ex.getMessage());
                }
            }
        };
        loading.start();

        //create an asynchronous task to download the primary data file for the app
        new DownloadTask().execute();

    }
    public boolean isLoggedIn(){
        SharedPreferences sp=getSharedPreferences("userInfo",this.MODE_PRIVATE);
        if (sp.getString("status","false").equals("true"))
            return true;
        else return false;
    }

    private class DownloadTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            //download the json file from firebase
            Log.e("file","downloading primary data file.");
            StorageReference ref=storage.getReference().child("primary.json");
            File file=new File(path,"primary.json");
            ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("file","Primary data File download success!");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("file","Primary data File download failed!\nError: "+e.getMessage());
                }
            });
            return null;
        }
    }


}
