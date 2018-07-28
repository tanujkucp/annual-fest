package sahil.iiitk_foundationday_app.adapters;
//Made by Tanuj
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static ArrayList<User> users;
    private static Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name,contact;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.user_name);
            contact =  v.findViewById(R.id.user_contact);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //display detailed data in a dialog
                    User currentUser=users.get(getAdapterPosition());
                    final Dialog myDialog =  new Dialog(context);
                    myDialog.requestWindowFeature(DialogInterface.BUTTON_NEGATIVE);
                    myDialog.setContentView(R.layout.card_user_profile);
                    myDialog.setCancelable(true);
                    ((TextView)myDialog.findViewById(R.id.profile_name)).setText(currentUser.getName());
                    ((TextView)myDialog.findViewById(R.id.profile_ffid)).setText(currentUser.getUser_id());
                    ((TextView)myDialog.findViewById(R.id.profile_collageID)).setText(currentUser.getCollegeid());
                    ((TextView)myDialog.findViewById(R.id.profile_emailID)).setText(currentUser.getEmail());
                    ((TextView)myDialog.findViewById(R.id.profile_mobile)).setText(currentUser.getPhone());
                    ((TextView)myDialog.findViewById(R.id.profile_collage)).setText(currentUser.getCollege());
                    ((TextView)myDialog.findViewById(R.id.profile_branch)).setText
                            (currentUser.getYear()+" year, "+currentUser.getDepartment());
                    ((TextView)myDialog.findViewById(R.id.profile_gender)).setText
                            (currentUser.getGender()+", "+currentUser.getMos());

                    myDialog.show();
                }
            });
        }

        public TextView getNameView() {
            return name;
        }
        public TextView getContactView(){
            return contact;
        }
    }

    public UserAdapter(ArrayList<User> data, Context con){
        users=data;
        context=con;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_user, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getNameView().setText(users.get(pos).getUser_id()+" | "+""+users.get(pos).getName());
        viewHolder.getContactView().setText(""+users.get(pos).getEmail());
    }


    @Override
    public int getItemCount() {
        return users.size();
    }
}
