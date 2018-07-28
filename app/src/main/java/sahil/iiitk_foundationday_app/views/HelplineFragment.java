package sahil.iiitk_foundationday_app.views;
// Made by tanuj

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.ContactsAdapter;

public class HelplineFragment extends Fragment {

    PrimaryContactsInterface provider;
    RelativeLayout relativeLayout;
    TextView query_email;

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
        relativeLayout=view.findViewById(R.id.relative);
        query_email=view.findViewById(R.id.tv_query);

        RecyclerView recyclerView=view.findViewById(R.id.grid);
        RecyclerView.LayoutManager manager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(manager);

        JSONArray array=provider.getContacts();
        //pass this array to the adapter
        if (array!=null){
            ContactsAdapter adapter=new ContactsAdapter(getActivity(),array);
            recyclerView.setAdapter(adapter);
        }

        final PopupMenu popup = new PopupMenu(getActivity(), relativeLayout);
        MenuInflater inflater2 = popup.getMenuInflater();
        inflater2.inflate(R.menu.contact_menu, popup.getMenu());
        PopupMenu.OnMenuItemClickListener listener=new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.contact_call : Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"
                                +((TextView)relativeLayout.findViewById(R.id.tv_number5)).getText().toString()));
                        getActivity().startActivity(intent);
                        return true;
                    case R.id.contact_save : Intent intent2=new Intent(Intent.ACTION_INSERT);
                        intent2.setType(ContactsContract.Contacts.CONTENT_TYPE)
                                .putExtra(ContactsContract.Intents.Insert.NAME,
                                        ((TextView)relativeLayout.findViewById(R.id.tv_name5)).getText().toString())
                                .putExtra(ContactsContract.Intents.Insert.COMPANY,getActivity().getString(R.string.app_name))
                                .putExtra(ContactsContract.Intents.Insert.EMAIL,"tanujm241@gmail.com")
                                .putExtra(ContactsContract.Intents.Insert.PHONE,((TextView)relativeLayout.findViewById(R.id.tv_number5)).getText().toString());
                        getActivity().startActivity(intent2);
                        return true;
                }
                return false;
            }
        };
        popup.setOnMenuItemClickListener(listener);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });

        query_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",getActivity().getString(R.string.contact_email), null));
                getActivity().startActivity(Intent.createChooser(emailIntent, "Send Email via"));
            }
        });
        return view;
    }

}
