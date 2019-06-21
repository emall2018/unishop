package com.google.cloud.android.speech;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by World of UI/UX on 01/04/2019.
 */

public class FilterRestaurantAdapter extends RecyclerView.Adapter<FilterRestaurantAdapter.MyViewHolder> {

    Context context;
    private List<Food> OfferList;
    int myPos = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }

    public FilterRestaurantAdapter(Context context, List<Food> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public FilterRestaurantAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_filter_restaurant, parent, false);
        return new FilterRestaurantAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Food lists = OfferList.get(position);
        holder.title.setText(lists.getTitle());

        if (myPos == position){
            holder.title.setTextColor(Color.parseColor("#F73A36"));
            holder.image.setBackgroundResource(R.drawable.tick);
        }else {
            holder.title.setTextColor(Color.parseColor("#7e7e7e"));
            holder.image.setBackgroundColor(Color.parseColor("#00000000"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPos = position;
                notifyDataSetChanged();
            }


        });
    }

    @Override
    public int getItemCount() {
        return OfferList.size();
    }

}


