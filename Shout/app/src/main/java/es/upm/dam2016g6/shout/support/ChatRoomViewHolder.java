package es.upm.dam2016g6.shout.support;

import android.content.Context;
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
    Context context;

    TextView tv_title;
    TextView tv_created;
    TextView tv_expires;
    TextView tv_range;
    TextView tv_category;
    TextView tv_distance;
    Button bt_share;
    Button bt_join;
    ImageView imageView;


    public ChatRoomViewHolder(View itemView) {
        super(itemView);

        context = itemView.getContext();
        tv_title = (TextView) itemView.findViewById(R.id.cicl_tv_title);
        tv_distance = (TextView) itemView.findViewById(R.id.cicl_tv_dist);
        tv_created = (TextView) itemView.findViewById(R.id.cicl_tv_created);
        tv_expires = (TextView) itemView.findViewById(R.id.cicl_tv_expires);
        tv_range = (TextView) itemView.findViewById(R.id.cicl_tv_range);
        tv_category = (TextView) itemView.findViewById(R.id.cicl_tv_category);
        bt_join = (Button) itemView.findViewById(R.id.cicl_bt_join);
        bt_share = (Button) itemView.findViewById(R.id.cicl_bt_share);
        imageView = (ImageView) itemView.findViewById(R.id.cicl_image);
    }
}
