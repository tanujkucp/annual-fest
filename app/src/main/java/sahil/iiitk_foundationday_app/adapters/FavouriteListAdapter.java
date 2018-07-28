package sahil.iiitk_foundationday_app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sahil.iiitk_foundationday_app.R;

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.ViewHolder> {
    private static List<String> events;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final View separator;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.fav_list_name);
            separator=v.findViewById(R.id.fav_list_item_separator);
        }

        public TextView getNameView() {
            return name;
        }
        public View getSeparator(){return separator;}
    }

    public FavouriteListAdapter(List<String> data){
        events=data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.favourite_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getNameView().setText(events.get(pos));
        if (pos==getItemCount()-1) viewHolder.getSeparator().setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

}
