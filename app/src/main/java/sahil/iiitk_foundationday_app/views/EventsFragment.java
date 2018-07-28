package sahil.iiitk_foundationday_app.views;
// Made by tanuj
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.EventAdapter;

public class EventsFragment extends Fragment {
    int club_number;
    Bundle bundle;
    List<String> image_names,names;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    FirebaseStorage storage;
    File path,temp;
    int version=0;
    String club_name;
    ProgressDialog dialog;
    ClubNameInterface provider;

    public interface ClubNameInterface{
        public void setClubName(String x);
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
        bundle=getArguments();
        club_number=bundle.getInt("club_number");
        View view= inflater.inflate(R.layout.fragment_events, container, false);
        mRecyclerView =view.findViewById(R.id.recyclerViewEvents);
        mLayoutManager =new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        storage=FirebaseStorage.getInstance();
        path=getActivity().getCacheDir();
        names=new ArrayList<>();
        image_names=new ArrayList<>();
        dialog=new ProgressDialog(getActivity(),R.style.AlertDialogCustom);
        dialog.setMessage("fetching fun events...");
        dialog.setCancelable(false);
        dialog.show();
        //check if the json file for this club data is available offline or to download
        readFile(false);

        return view;
    }

    public void getClubData(final Boolean isCached){
        Log.e("file","downloading , cache available: "+isCached.toString());
        //download the json file of this club from firebase
        Log.e("file","downloading file of this club.");
        StorageReference ref=storage.getReference().child("clubs_json/club"+club_number+".json");
        File file;
        temp=new File(path,"temp_club.json");
        //create an empty file to store download
        if (isCached) file=temp;
        else file=new File(path,"club"+club_number+".json");
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
                Toast.makeText(getActivity(), "Network error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void readFile(Boolean readTemp){
        Log.e("file","Reading from temp: "+readTemp.toString());
        File file;
        if (readTemp) file=temp;
        else file=new File(path,"club"+club_number+".json");

        int length=(int) file.length();
        byte[] bytes=new byte[length];
        try{
            FileInputStream in=new FileInputStream(file);
            try{
                in.read(bytes);
                in.close();
                String contents=new String(bytes);
                Log.e("file","File found and parsing...");
               // Log.e("file","File read.: \n"+contents);
                parseJSON(contents,readTemp);
                //to download the file to check for updates
                if (!readTemp) getClubData(true);
            }catch (IOException e){
                Log.e("file",""+e.getMessage());
                dialog.cancel();
                Log.e("file","An error occurred. have to download.");
                Toast.makeText(getActivity(), "An error occurred!", Toast.LENGTH_SHORT).show();
                //some error occurred in file , download data again
                getClubData(false);
            }
        }catch (FileNotFoundException e){
            Log.e("file","File not found in cache. have to download.");
            //no cache found , download data now
            getClubData(false);
        }
    }

    public void parseJSON(String text,Boolean checkUpdate) {
        try{
            JSONObject object=new JSONObject(text);
            if (checkUpdate){
                if (version<object.getInt("version")){
                    Log.e("file","Update found for this club.");
                    temp.delete();
                    File toUpdate=new File(path,"club"+club_number+".json");
                    try{
                        FileOutputStream stream=new FileOutputStream(toUpdate);
                        try{
                            stream.write(text.getBytes());
                            stream.close();
                            Log.e("file","Club file updated.\nShowing new data to user");
                            getDataFromJSONObject(object);
                            Toast.makeText(getActivity(), "CLub details have been updated!", Toast.LENGTH_LONG).show();
                        }catch (IOException e){
                            Log.e("file","During file update :\n"+e.getMessage());
                        }
                    }catch (FileNotFoundException e){
                        Log.e("file","During file update :\n"+e.getMessage());
                    }
                } else Log.e("file","No update found for this event.");
            }else{
                getDataFromJSONObject(object);
            }

        }catch(JSONException e){
            Log.e("file",e.getMessage());
            dialog.dismiss();
            Toast.makeText(getActivity(), "An error occurred! Please Reload page.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getDataFromJSONObject(JSONObject object){
        try{
            version=object.getInt("version");
            club_name=object.getString("name");
            JSONArray array=object.getJSONArray("events");
            names.clear();
            image_names.clear();
            for (int i=0;i<array.length();i++){
                try{
                    JSONObject oneEvent=array.getJSONObject(i);
                    //pull all items from this object
                    names.add(oneEvent.getString("name"));
                    image_names.add(oneEvent.getString("image_name"));
                }catch (JSONException e){
                    Log.e("file",e.getMessage());
                }
            }
            Log.e("file","JSON parsing complete.\nEvents names: "+names.toString());
            populateAdapter();
        }catch (JSONException e){
            Log.e("file",e.getMessage());
            dialog.dismiss();
            Toast.makeText(getActivity(), "An error occurred! Please Reload page.", Toast.LENGTH_SHORT).show();

        }
    }

    public void populateAdapter(){
        Log.e("file","populating adapter");
        //set cluyb name in ACTIVITY
        provider.setClubName(club_name);
       EventAdapter adapter=new EventAdapter(getActivity(),club_number,image_names,names);
       mRecyclerView.setAdapter(adapter);
       dialog.dismiss();
    }
}
