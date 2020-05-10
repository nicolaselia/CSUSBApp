package com.theupswing.csusbapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ExampleViewHolder> {

    private ArrayList<LocationItem> mExampleList;
    private OnItemClickListener mListener; // make sure you import the one that you implemented below (heed the package name)

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView checkbox;
        public TextView location;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox_image);
            location = itemView.findViewById(R.id.location_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public LocationAdapter(ArrayList<LocationItem> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        LocationItem currentItem = mExampleList.get(position);

        holder.checkbox.setImageResource(currentItem.getCheckboxResource());
        holder.location.setText(currentItem.getLocation());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
