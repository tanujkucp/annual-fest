package sahil.iiitk_foundationday_app.views;
// Made by tanuj
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import sahil.iiitk_foundationday_app.R;

public class ParentForFrag extends AppCompatActivity implements EventsFragment.ClubNameInterface{

    @Override
    public void setClubName(String x) {
        getSupportActionBar().setTitle(x);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_for_frag);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //show fragment
        int club_number=getIntent().getIntExtra("club_number",0);
        Fragment fragment=new EventsFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("club_number",club_number);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(android.R.id.content,fragment).commit();
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

}
