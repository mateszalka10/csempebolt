package com.example.csempebolt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<ShoppingItem> mShoppingItemsData;
    private ArrayList<ShoppingItem> mShoppingItemsDataAll;
    private int LastPositon = -1;

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemsData){
        this.mShoppingItemsData = itemsData;
        this.mShoppingItemsDataAll = itemsData;
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.termek_lista, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = mShoppingItemsData.get(position);

        holder.bindTo(currentItem);
        if (holder.getAdapterPosition() > LastPositon){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            LastPositon = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShoppingItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }
    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0){
                results.count = mShoppingItemsDataAll.size();
                results.values = mShoppingItemsDataAll;
            } else {
                String filterPatter = charSequence.toString().toLowerCase().trim();
                for (ShoppingItem item : mShoppingItemsDataAll){
                    if (item.getName().toLowerCase().contains(filterPatter)){
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mShoppingItemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleText = itemView.findViewById(R.id.termekNev);
            mInfoText = itemView.findViewById(R.id.leiras);
            mPriceText= itemView.findViewById(R.id.ar);
            mItemImage= itemView.findViewById(R.id.termekKep);


        }

        public void bindTo(ShoppingItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());

            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
            itemView.findViewById(R.id.kosar_gomb).setOnClickListener(v -> {((CsempeListaActivity)mContext).updateAlertIcon(currentItem);});
            itemView.findViewById(R.id.torles).setOnClickListener(view -> ((CsempeListaActivity)mContext).DeleteItem(currentItem));
        }
    };

}

