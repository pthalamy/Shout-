package es.upm.dam2016g6.shout.support;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import es.upm.dam2016g6.shout.R;

/**
 * Created by pthalamy on 11/12/16.
 */

public class ChatRoomViewHolder extends RecyclerView.ViewHolder {
    TextView tv_title;
    TextView tv_created;
    TextView tv_expires;
    TextView tv_description;
    TextView tv_category;
    Button bt_share;
    Button bt_join;
    ImageView imageView;

    public ChatRoomViewHolder(View itemView) {
        super(itemView);

        tv_title = (TextView) itemView.findViewById(R.id.cicl_tv_title);
    }
}
