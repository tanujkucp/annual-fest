package sahil.iiitk_foundationday_app.views;
// Made by tanuj

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.ContactsAdapter;

public class HelplineFragment extends Fragment {

    PrimaryContactsInterface provider;

    public interface PrimaryContactsInterface{
        public JSONArray getContacts();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            provider=(PrimaryContactsInterface) activity;
        }catch (ClassCastException e){
            Log.e("interface",e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_helpline, container, false);
        RecyclerView recyclerView=view.findViewById(R.id.grid);
        RecyclerView.LayoutManager manager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(manager);

        JSONArray array=provider.getContacts();
        //pass this array to the adapter
        if (array!=null){
            ContactsAdapter adapter=new ContactsAdapter(getActivity(),array);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

}
