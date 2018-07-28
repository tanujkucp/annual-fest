package sahil.iiitk_foundationday_app.adapters;
//Made by Tanuj
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sahil.iiitk_foundationday_app.R;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private static Context con;
    private static JSONArray array;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView,titleView,PhoneView;

        public ViewHolder(View v) {
            super(v);
            nameView =v.findViewById(R.id.card_contact_name);
            titleView=v.findViewById(R.id.card_contact_title);
            PhoneView=v.findViewById(R.id.card_contact_phone);
            final PopupMenu popup = new PopupMenu(con, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.contact_menu, popup.getMenu());

            PopupMenu.OnMenuItemClickListener listener=new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.contact_call : Intent intent = new Intent(Intent.ACTION_DIAL);
                                                    intent.setData(Uri.parse("tel:"+getPhoneView().getText().toString()));
                                                    con.startActivity(intent);
                                                    return true;
                        case R.id.contact_save : Intent intent2=new Intent(Intent.ACTION_INSERT);
                            intent2.setType(ContactsContract.Contacts.CONTENT_TYPE)
                                    .putExtra(ContactsContract.Intents.Insert.NAME,getNameView().getText().toString())
                                    .putExtra(ContactsContract.Intents.Insert.COMPANY,con.getString(R.string.app_name))
                                    .putExtra(ContactsContract.Intents.Insert.EMAIL,con.getString(R.string.contact_email))
                                    .putExtra(ContactsContract.Intents.Insert.JOB_TITLE,getTitleView().getText().toString())
                                    .putExtra(ContactsContract.Intents.Insert.PHONE,getPhoneView().getText().toString());
                            con.startActivity(intent2);
                            return true;
                    }
                    return false;
                }
            };
            popup.setOnMenuItemClickListener(listener);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();

                }
            });

        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getTitleView() {
            return titleView;
        }

        public TextView getPhoneView() {
            return PhoneView;
        }
    }

    public ContactsAdapter(Context context, JSONArray arr) {
        con=context;
        array=arr;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_contact, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        //get data from JSON array
        try{
            JSONObject object=array.getJSONObject(pos);
            viewHolder.getNameView().setText(""+object.getString("name"));
            viewHolder.getTitleView().setText(""+object.getString("title"));
            viewHolder.getPhoneView().setText(""+object.getString("phone"));
        }catch (JSONException e){
            Log.e("contacts",e.getMessage());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return array.length();
    }
}

