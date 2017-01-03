package es.upm.dam2016g6.shout.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import es.upm.dam2016g6.shout.R;

/**
 * Created by pthalamy on 11/12/16.
 */

public class ListChatRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
    Context context;

    TextView tv_title;
    TextView tv_category;
    TextView tv_createdOn;
    TextView tv_expiresIn;
    TextView tv_latestText;
    TextView tv_latestTextTime;
    TextView tv_latestTextAuthor;
    CircleImageView iv_thumbnail;
    ImageButton ib_star;
    MyViewHolderClickListener mListener;
    // TODO: 3/1/17 Add distance to chat room

    public ListChatRoomViewHolder(View itemView, MyViewHolderClickListener listener) {
        super(itemView);

        context = itemView.getContext();
        tv_title = (TextView) itemView.findViewById(R.id.lcic_tv_title);
        tv_createdOn = (TextView) itemView.findViewById(R.id.lcic_tv_createdOn);
        tv_expiresIn = (TextView) itemView.findViewById(R.id.lcic_tv_expiresIn);
        tv_category = (TextView) itemView.findViewById(R.id.lcic_tv_category);
        iv_thumbnail = (CircleImageView) itemView.findViewById(R.id.lcic_iv_thumbnail);
        tv_latestText = (TextView) itemView.findViewById(R.id.lcic_tv_lastText);
        tv_latestTextAuthor = (TextView) itemView.findViewById(R.id.lcic_tv_lastAuthor);
        tv_latestTextTime = (TextView) itemView.findViewById(R.id.lcic_tv_lastTextTime);
        ib_star = (ImageButton) itemView.findViewById(R.id.lcic_ib_star);

        mListener = listener;

        ib_star.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view instanceof ImageButton){
            mListener.onStar((ImageButton) view, this.getAdapterPosition());
        } else {
            mListener.onChatroomSelected(view, this.getAdapterPosition());
        }
    }

    public static interface MyViewHolderClickListener {
        public void onStar(ImageButton callerImageButton, int position);
        public void onChatroomSelected(View caller, int position);
    }
}
