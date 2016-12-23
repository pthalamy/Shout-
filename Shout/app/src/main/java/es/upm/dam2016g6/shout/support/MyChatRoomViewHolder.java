package es.upm.dam2016g6.shout.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by pthalamy on 11/12/16.
 */

public class MyChatRoomViewHolder extends RecyclerView.ViewHolder {
    Context context;

    TextView tv_title;
    TextView tv_created;
    TextView tv_expires;
    TextView tv_category;
    TextView tv_latest;
    ImageButton ib_star;

    public MyChatRoomViewHolder(View itemView) {
        super(itemView);
    }
}
