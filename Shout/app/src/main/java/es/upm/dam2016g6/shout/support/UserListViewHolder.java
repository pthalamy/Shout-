package es.upm.dam2016g6.shout.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import es.upm.dam2016g6.shout.R;

/**
 * Created by pthalamy on 3/1/17.
 */
public class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public Context mContext;
    public CircleImageView civ_userPic;
    public TextView tv_userName;
    private UserListViewHolderClickListener mListener;

    public UserListViewHolder(View itemView, UserListViewHolderClickListener listener) {
        super(itemView);

        tv_userName = (TextView) itemView.findViewById(R.id.ul_tv_userName);
        civ_userPic = (CircleImageView) itemView.findViewById(R.id.ul_civ_profilePic);
        mListener = listener;
        mContext = itemView.getContext();

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mListener.onUserSelected(view, this.getAdapterPosition());
    }

    public static interface UserListViewHolderClickListener {
        public void onUserSelected(View caller, int position);
    }
}
