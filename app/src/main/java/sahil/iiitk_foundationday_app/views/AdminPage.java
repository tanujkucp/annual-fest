package sahil.iiitk_foundationday_app.views;
// Made by Tanuj
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sahil.iiitk_foundationday_app.R;

public class AdminPage extends AppCompatActivity
        implements AdminRegistrationsFragment.EventNamesInterface,AdminInsightsFragment.EventsNamesInterface2
            ,AdminActionsFragment.ClubNameInterface{

    ViewPager mViewPager;
    File path;
    Boolean isPrimaryFileRead=false;
    String club_name;
    JSONArray pri_clubs;
    ViewPagerAdapter adapter;

////////interface methods for use of fragments
    @Override
    public String[] getEventNames(int club_number) {
        if (!isPrimaryFileRead) readFile();
        String[] finalArray=null;
        try{
            JSONObject wholeClubData=pri_clubs.getJSONObject(club_number);
            JSONArray arrayOfNames=wholeClubData.getJSONArray("event_names");
            finalArray=new String[arrayOfNames.length()];
            for (int i=0;i<arrayOfNames.length();i++){
                finalArray[i]=arrayOfNames.getString(i);
            }

        }catch (JSONException e){
            Log.e("interface",e.getMessage());
        }
        return finalArray;
    }

    @Override
    public int getClubNumber() {
        //determine club number and get all event  names
        if (club_name.equals("Technical_club")){
            return 0;
        }
        else if (club_name.equals("Cultural_club")){
            return 1;
        }
        else if (club_name.equals("Literary_and_dramatics_club")){
            return 2;
        }
        else if (club_name.equals("Fine_arts_and_photography_club")){
            return 3;
        }
        return 0;
    }

    @Override
    public String getSelectedClubName() {
        return club_name;
    }

    //////////////end of interface methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        club_name=getIntent().getStringExtra("club_name");

        //setup viewpager
        mViewPager =findViewById(R.id.admin_viewpager);
        setupViewPager(mViewPager);
        TabLayout tabLayout =findViewById(R.id.admin_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        path=getCacheDir();
        readFile();

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AdminRegistrationsFragment(), "Registrations");
        adapter.addFrag(new AdminInsightsFragment(),"Insights");
        adapter.addFrag(new AdminActionsFragment(),"Actions");
        adapter.addFrag(new AdminChatFragment(),"Queries");
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
    public void onBackPressed() {
        int pos=mViewPager.getCurrentItem()-1;
        if (pos>=0) mViewPager.setCurrentItem(pos,true);
        else this.finish();
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
                Toast.makeText(this, "An error occurred in fetching details!", Toast.LENGTH_SHORT).show();
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
            pri_clubs=object.getJSONArray("clubs");
            isPrimaryFileRead=true;
            Log.e("file","Primary file parsing done.");
        }catch(JSONException e){
            Log.e("file",e.getMessage());
            Toast.makeText(this, "An error occurred! Please Reload page.", Toast.LENGTH_SHORT).show();
        }
    }

}
