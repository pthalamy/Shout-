package es.upm.dam2016g6.shout.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import es.upm.dam2016g6.shout.R;

/**
 * Created by pthalamy on 4/1/17.
 */

public class PrivateConversationViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    Context context;

    TextView tv_title;
    TextView tv_latestText;
    TextView tv_latestTextTime;
    TextView tv_latestTextAuthor;
    CircleImageView civ_profilePic;
    PCViewHolderClickListener mListener;

    public PrivateConversationViewHolder(View itemView, PCViewHolderClickListener listener) {
        super(itemView);

        context = itemView.getContext();
        tv_title = (TextView) itemView.findViewById(R.id.lpc_tv_title);
        civ_profilePic = (CircleImageView) itemView.findViewById(R.id.lpc_civ_profilePic);
        tv_latestText = (TextView) itemView.findViewById(R.id.lpc_tv_lastText);
        tv_latestTextAuthor = (TextView) itemView.findViewById(R.id.lpc_tv_lastAuthor);
        tv_latestTextTime = (TextView) itemView.findViewById(R.id.lpc_tv_lastTextTime);

        mListener = listener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mListener.onPCSelected(view, this.getAdapterPosition());
    }

    public static interface PCViewHolderClickListener {
        public void onPCSelected(View caller, int position);
    }
}
