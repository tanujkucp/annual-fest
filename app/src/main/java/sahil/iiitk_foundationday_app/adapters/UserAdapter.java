package sahil.iiitk_foundationday_app.adapters;
//Made by Tanuj
import android.app.AlertDialog;
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
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle(""+currentUser.getName());
                    String details=""+currentUser.getUser_id()+", "+currentUser.getCollegeid();
                    details+="\nContact: "+currentUser.getEmail()+", "+currentUser.getPhone();
                    details+="\n"+currentUser.getDepartment()+", "+currentUser.getCollege();
                    details+="\n"+currentUser.getGender()+", "+currentUser.getYear()+" Year, "+currentUser.getMos();
                    details+="\nQuiz: Correct= "+currentUser.getQuiz_correct()+", Lives Left= "+currentUser.getQuiz_lives();

                    builder.setMessage(details);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog=builder.create();
                    dialog.show();
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
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_user, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getNameView().setText(users.get(pos).getUser_id()+" | "+users.get(pos).getName());
        viewHolder.getContactView().setText(""+users.get(pos).getEmail()+", "+users.get(pos).getPhone());
    }


    @Override
    public int getItemCount() {
        return users.size();
    }
}
