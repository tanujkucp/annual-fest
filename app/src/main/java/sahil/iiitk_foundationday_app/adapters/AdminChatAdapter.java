package sahil.iiitk_foundationday_app.adapters;
//Made by Tanuj
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.model.Msg;
import sahil.iiitk_foundationday_app.views.ContactUs;

public class AdminChatAdapter extends RecyclerView.Adapter<AdminChatAdapter.ViewHolder> {
    private static List<String> name,id,numNew;
    private static  List<Msg> lastMessage;
    private static Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView,messageView,avatarView,dateView,unreadView;

        public ViewHolder(View v) {
            super(v);
            nameView=v.findViewById(R.id.dialogName);
            messageView=v.findViewById(R.id.dialogLastMessage);
            avatarView=v.findViewById(R.id.dialogAvatar);
            dateView=v.findViewById(R.id.dialogDate);
            unreadView=v.findViewById(R.id.dialogUnreadBubble);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ContactUs.class);
                    intent.putExtra("isAdmin",true);
                    intent.putExtra("ffid",id.get(getAdapterPosition()));
                    intent.putExtra("name",name.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getMessageView() {
            return messageView;
        }

        public TextView getAvatarView() {
            return avatarView;
        }

        public TextView getDateView() {
            return dateView;
        }

        public TextView getUnreadView() {
            return unreadView;
        }
    }

    public AdminChatAdapter(List<String> name, List<String> id, List<Msg> lastMessage, List<String> numNew, Context context){
        this.name=name;
        this.id=id;
        this.lastMessage=lastMessage;
        this.numNew=numNew;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_dialog, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
       viewHolder.getNameView().setText(name.get(pos));
       viewHolder.getAvatarView().setText(""+name.get(pos).charAt(0));
       viewHolder.getMessageView().setText(lastMessage.get(pos).getMessage());
       viewHolder.getDateView().setText(lastMessage.get(pos).getTime());
       if (!numNew.get(pos).equals("0")){
           viewHolder.getUnreadView().setText(numNew.get(pos));
       }else{
           viewHolder.getUnreadView().setVisibility(View.GONE);
       }

    }

    @Override
    public int getItemCount() {
        return name.size();
    }

}
