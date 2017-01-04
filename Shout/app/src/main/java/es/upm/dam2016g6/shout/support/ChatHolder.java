package es.upm.dam2016g6.shout.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import es.upm.dam2016g6.shout.R;

/**
 * Created by pthalamy on 3/1/17.
 */

public class ChatHolder extends RecyclerView.ViewHolder {
    private final TextView mNameField;
    private final TextView mTextField;
    private final TextView mDateField;
    private final CircleImageView mUserPic;

    public ChatHolder(View itemView) {
        super(itemView);

        mNameField = (TextView) itemView.findViewById(R.id.chat_tv_user);
        mTextField = (TextView) itemView.findViewById(R.id.chat_tv_text);
        mDateField = (TextView) itemView.findViewById(R.id.chat_tv_date);
        mUserPic = (CircleImageView) itemView.findViewById(R.id.chat_iv_pic);
    }

    public void setName(String name) {
        mNameField.setText(name);
    }

    public void setDate(long date) {
        mDateField.setText(Utils.getDateStringFromTimestamp(date));
    }

    public void setText(String text) {
        mTextField.setText(text);
    }

    public void setUserPic(String imageUrl, Context context) {
        Glide.with(context)
                .load(imageUrl)
                .into(this.mUserPic);
    }
}