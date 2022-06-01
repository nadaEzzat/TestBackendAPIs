package com.project.backendapis.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.backendapis.R;
import com.project.backendapis.data.Data;

import java.util.ArrayList;

public class ParamsAdapter extends RecyclerView.Adapter<ParamsAdapter.ViewHolder> {

    ArrayList<Data> data;
    Context context;
    Callbacks callbacks;
    int selected_position;

    public ParamsAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    public void insertNewParam(Data data) {
        this.data.add(data);
        notifyItemInserted(this.data.size());

        updateUrl();

    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void editParam(Data data) {
        this.data.set(selected_position, data);
        notifyItemChanged(selected_position);

        updateUrl();

    }

    private void removeData(int position) {

        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());

        updateUrl();
    }

    private void updateUrl() {
        String newParams = "";
        for (Data data : data
        ) {
            if (newParams.isEmpty())
                newParams = data.getKey() + "=" + data.getValue();
            else
                newParams = newParams.concat("&" + data.getKey() + "=" + data.getValue());
        }
        Log.i("APIResponse", "newParams : " + newParams);
        callbacks.updateUrl(newParams);

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_header, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.key.setText(data.get(position).getKey());
        holder.value.setText(data.get(position).getValue());
        holder.removeHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeData(position);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_position = position;
                callbacks.editParam(data.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addParams(ArrayList<Data> newParams) {
        data.clear();
        data = newParams;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView key;
        TextView value;
        ImageButton removeHeader;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            key = itemView.findViewById(R.id.key);
            value = itemView.findViewById(R.id.value);
            removeHeader = itemView.findViewById(R.id.remove_header);
        }
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public interface Callbacks {
        public void editParam(Data data);

        public void updateUrl(String param);
    }
}
