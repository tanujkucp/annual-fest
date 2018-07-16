package sahil.iiitk_foundationday_app.views;
// Made by tanuj
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.ScheduleAdapter;

public class ScheduleFragment extends Fragment {

    ImageView schedule1,schedule2;
    PrimaryScheduleInterface provider;
    public interface PrimaryScheduleInterface{
        public JSONArray getSchedule();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            provider=(PrimaryScheduleInterface) activity;
        }catch (ClassCastException e){
            Log.e("interface",e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_schedule, container, false);
        RecyclerView recyclerView= view.findViewById(R.id.schedule_recycler);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        JSONArray array=provider.getSchedule();
        if(array!=null){
            ScheduleAdapter adapter=new ScheduleAdapter(getActivity(),array);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

}
