package sahil.iiitk_foundationday_app.adapters;
// Made by Tanuj
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.views.DetailedEvent;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private static Context con;
    private static int club_number;
    private static FirebaseStorage storage;

    private static List<String> event_images_url,event_names;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameView;

        public ViewHolder(View v) {
            super(v);
            imageView=(ImageView) v.findViewById(R.id.event_image);
            nameView=v.findViewById(R.id.event_card_name);
            final Intent intent=new Intent(con, DetailedEvent.class);
            v.setOnClickListener(
                    new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            intent.putExtra("club_number",club_number);
                            intent.putExtra("event_number",getAdapterPosition());
                            con.startActivity(intent);
                        }
                    }
            );
        }

        public ImageView getImageView(){
            return imageView;
        }
        public TextView getNameView(){
            return nameView;
        }
    }


    public EventAdapter(Context context, int club_num, List<String> data,List<String> data2) {
        event_images_url=data;
        con=context;
        club_number=club_num;
        event_names=data2;
        storage=FirebaseStorage.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.events_card, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        viewHolder.getNameView().setText(event_names.get(pos));
        String folderName="";
        switch (club_number){
            case 0: folderName="technical/";
            break;
            case 1: folderName="cultural/";
            break;
            case 2: folderName="literary/";
            break;
            case 3: folderName="photography/";
            break;
        }
        StorageReference ref=storage.getReference().child(folderName+event_images_url.get(pos));
        Glide.with(con).using(new FirebaseImageLoader()).load(ref)
                .placeholder(R.drawable.ic_event_placeholder).into(viewHolder.getImageView());

    }

    @Override
    public int getItemCount() {
        if (event_images_url ==null)
            return 0;
        else
        return event_images_url.size();
    }
}
