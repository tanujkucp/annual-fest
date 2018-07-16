package sahil.iiitk_foundationday_app.adapters;
//Made by Tanuj
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.model.SingleEventPersonal;

public class RegisteredEventAdapter extends RecyclerView.Adapter<RegisteredEventAdapter.ViewHolder> {
    private static List<SingleEventPersonal> events;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name,details;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.card_regEvent_name);
            details =  v.findViewById(R.id.card_regEvent_details);
        }

        public TextView getNameView() {
            return name;
        }
        public TextView getDetailsView(){
            return details;
        }
    }

    public RegisteredEventAdapter(List<SingleEventPersonal> data){
        events=data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_registered_event, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getNameView().setText(events.get(pos).getEvent_name());
        viewHolder.getDetailsView().setText("by "+events.get(pos).getUser_name()+" on "+events.get(pos).getDate()+".");
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

}
