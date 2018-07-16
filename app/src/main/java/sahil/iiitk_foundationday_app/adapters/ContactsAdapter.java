package sahil.iiitk_foundationday_app.adapters;
//Made by Tanuj
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

