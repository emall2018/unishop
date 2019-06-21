package com.google.cloud.android.speech;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by World of UI/UX on 01/04/2019.
 */

public class BookOrderAdapter extends RecyclerView.Adapter<BookOrderAdapter.MyViewHolder> {

    Context context;
    private List<BookOrder> OfferList;
    int myPos = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        LinearLayout linear;

        public MyViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.image);
            title = (TextView) view.findViewById(R.id.title);
            linear = (LinearLayout) view.findViewById(R.id.linear);
        }
    }

    public BookOrderAdapter(Context context, List<BookOrder> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public BookOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_book_order, parent, false);
        return new BookOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final BookOrder lists = OfferList.get(position);
        holder.image.setImageResource(lists.getImage());
        holder.title.setText(lists.getTitle());

        if (myPos == position){
            holder.title.setTextColor(Color.parseColor("#000000"));
            holder.linear.setBackgroundResource(R.drawable.ic_selector_a);
        }else {
            holder.title.setTextColor(Color.parseColor("#484646"));
            holder.linear.setBackgroundResource(R.drawable.ic_selector_b);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPos = position;
                notifyDataSetChanged();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Intent intent = new Intent(activity, BookOrderActivity.class);

                view.getContext().startActivity(intent);

            }


        });

    }

    @Override
    public int getItemCount() {
        return OfferList.size();
    }

}


