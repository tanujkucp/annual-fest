package sahil.iiitk_foundationday_app.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
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

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private static Context con;
    private static JSONArray array;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final RecyclerView recyclerView;

        public ViewHolder(View v) {
            super(v);
            nameView =v.findViewById(R.id.card_schedule_title);
            recyclerView=v.findViewById(R.id.card_schedule_recycler);
            RecyclerView.LayoutManager manager=new LinearLayoutManager(con);
            recyclerView.setLayoutManager(manager);

        }

        public TextView getNameView() {
            return nameView;
        }

        public RecyclerView getRecyclerView() {
            return recyclerView;
        }
    }

    public ScheduleAdapter(Context context, JSONArray arr) {
        con=context;
        array=arr;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_schedule, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        //get data from JSON array
        try{
            JSONObject object=array.getJSONObject(pos);
            JSONArray items=object.getJSONArray("items");
            viewHolder.getNameView().setText(""+object.getString("title"));
            //set adapter to the item recycler
            ScheduleItemsAdapter adapter=new ScheduleItemsAdapter(con,items);
            viewHolder.getRecyclerView().setAdapter(adapter);

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

