package edu.khai.csn.abondar.passwordmanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    ArrayList<Service>  arrayList = new ArrayList<>();
    public RecyclerAdapter(ArrayList<Service> arrayList){
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.icon.setImageResource(arrayList.get(position).getIconId());
        holder.serviceName.setText(arrayList.get(position).getServiceName());
        holder.loginName.setText(arrayList.get(position).getLoginName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView serviceName;
        TextView loginName;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imgAccount);
            serviceName = itemView.findViewById(R.id.serviceName);
            loginName = itemView.findViewById(R.id.login);
        }
    }

    public void setFilter(ArrayList<Service> newList){
        arrayList = new ArrayList<>();
        arrayList.addAll(newList);
        notifyDataSetChanged();
    }
}