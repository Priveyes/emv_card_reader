package com.example.sebastianczuma.czytnikkart.LayoutClassesEmvInfo;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sebastianczuma.czytnikkart.EmvDetails;
import com.example.sebastianczuma.czytnikkart.MainActivity;
import com.example.sebastianczuma.czytnikkart.R;

import java.util.List;

public class RecyclerViewAdapterHospital extends RecyclerView.Adapter<RecyclerViewHoldersHospital> {
    private MainActivity context;
    private List<EmvDetails> itemList;

    public RecyclerViewAdapterHospital(List<EmvDetails> itemList, MainActivity context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHoldersHospital onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.decoded_all_details, null);
        return new RecyclerViewHoldersHospital(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHoldersHospital holder, int position) {
        holder.ask.setText(itemList.get(position).getAsk());
        holder.answear.setText(itemList.get(position).getAnswear());
        holder.decoded.setText(itemList.get(position).getDecoded());

        holder.context = context;

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}