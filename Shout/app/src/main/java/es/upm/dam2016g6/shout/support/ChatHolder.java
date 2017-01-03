package es.upm.dam2016g6.shout.support;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import es.upm.dam2016g6.shout.R;

/**
 * Created by pthalamy on 3/1/17.
 */

public class ChatHolder extends RecyclerView.ViewHolder {
//    private final TextView mNameField;
    private final TextView mTextField;

    public ChatHolder(View itemView) {
        super(itemView);
//        mNameField = (TextView) itemView.findViewById(android.R.id.text1);
        mTextField = (TextView) itemView.findViewById(R.id.tv_chatText);
    }

//    public void setName(String name) {
//        mNameField.setText(name);
//    }

    public void setText(String text) {
        mTextField.setText(text);
    }
}