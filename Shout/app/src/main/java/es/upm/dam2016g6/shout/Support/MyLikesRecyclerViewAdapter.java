package es.upm.dam2016g6.shout.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.FacebookLike;

/**
 * Created by pthalamy on 22/11/16.
 */

public class MyLikesRecyclerViewAdapter extends
        RecyclerView.Adapter
                <MyLikesRecyclerViewAdapter.MyLikesViewHolder> {

    private List<FacebookLike> items;
    private Context context;

    public MyLikesRecyclerViewAdapter(List<FacebookLike> likes) {
        if (likes == null) {
            throw new IllegalArgumentException(
                    "List of likes must not be null");
        }

        this.items = likes;
    }

    @Override
    public MyLikesViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        View itemView = LayoutInflater.
                from(context).
                inflate(R.layout.like_item_layout,
                        viewGroup,
                        false);
        return new MyLikesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(
            final MyLikesViewHolder viewHolder, final int position) {
        FacebookLike like= items.get(position);
        viewHolder.label.setText(like.getName());

        if (like.getPictureUrl() != null) {
            Glide.with(context)
                    .load(like.getPictureUrl())
                    .placeholder(R.drawable.com_facebook_button_icon_blue)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.imageView);
        } else {
            // make sure Glide doesn't load anything into this view until told otherwise
            Glide.clear(viewHolder.imageView);
//            // remove the placeholder (optional); read comments below
//            viewHolder.imageView.setImageDrawable(null);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public final static class MyLikesViewHolder
            extends RecyclerView.ViewHolder {
        TextView label;
        ImageView imageView;

        public MyLikesViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.tv_like_name);
            imageView = (ImageView) itemView.findViewById(R.id.img_pagepic);
        }
    }
}
