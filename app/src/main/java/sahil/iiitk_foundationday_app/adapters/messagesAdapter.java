package sahil.iiitk_foundationday_app.adapters;
//Made by Tanuj
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.model.Msg;

public class messagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static List<Msg> messages;
    private static CardView card;
    private static Context context;
    private static Boolean isAdmin;

    public static class ViewHolderIn extends RecyclerView.ViewHolder {
        private final TextView msgView,time;
        public ViewHolderIn(View v) {
            super(v);
            msgView=v.findViewById(R.id.message_msg_in);
            time=v.findViewById(R.id.message_time_in);
        }
        public TextView getMsgView() {
            return msgView;
        }
        public TextView getTime() {
            return time;
        }
    }

    public static class ViewHolderOut extends RecyclerView.ViewHolder{
        private final TextView messageText,messageTime;
        public ViewHolderOut(View v){
            super(v);
            messageText=v.findViewById(R.id.message_msg_out);
            messageTime=v.findViewById(R.id.message_time_out);
            card=v.findViewById(R.id.message_card);
        }
        public TextView getMessageText() {
            return messageText;
        }
        public TextView getMessageTime() {
            return messageTime;
        }
    }

    public messagesAdapter(List<Msg> data,Boolean isAdmin,Context context){
        messages=data;
        this.isAdmin=isAdmin;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v0 = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_message_out, viewGroup, false);
        View v1 = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_message_in, viewGroup, false);
        if (viewType==0) return new ViewHolderOut(v0);
        else return new ViewHolderIn(v1);
    }

    @Override
    public int getItemViewType(int position) {
        //determine whether message is incoming or outgoing
        if (isAdmin){
            if (messages.get(position).getSender().equals("admin")){
                return 0;
            }else return 1;
        }else{
            if (messages.get(position).getSender().equals("user")){
                return 0;
            }else return 1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int pos) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        if (viewHolder.getItemViewType()==0){
            ViewHolderOut outView=(ViewHolderOut) viewHolder;
            outView.getMessageText().setText(messages.get(pos).getMessage());
            outView.getMessageTime().setText(messages.get(pos).getTime());
            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams
                            (RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    Resources r = context.getResources();
                    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());
                    layoutParams.setMargins((int)px,0,0,0);
                    card.setLayoutParams(layoutParams);
        }else{
            ViewHolderIn inView=(ViewHolderIn) viewHolder;
            inView.getMsgView().setText(messages.get(pos).getMessage());
            inView.getTime().setText(messages.get(pos).getTime());
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
