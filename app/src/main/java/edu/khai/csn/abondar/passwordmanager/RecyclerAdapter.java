package edu.khai.csn.abondar.passwordmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.khai.csn.abondar.passwordmanager.Model.Entities.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {//implements View.OnClickListener {
    private ArrayList<Password> arrayList = new ArrayList<>();
    private Map<String, Integer> imageMap = new HashMap<>();
    private OnItemClickListener mListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecyclerAdapter(ArrayList<Password> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        //TODO add icons of services
        imageMap.put("instagram", R.drawable.ic_icon_instagram);
        imageMap.put("twitter", R.drawable.ic_icon_twitter);
        imageMap.put("google", R.drawable.ic_icon_google);
        ///imageMap.put("vk", 3);
        ///imageMap.put("steam", 4);
        ///imageMap.put("twitch", 5);
        ///imageMap.put("github", 6);
        ///imageMap.put("elearn", 7);
        ///imageMap.put("dropbox", 8);
        ///imageMap.put("appleid", 9);
        ///imageMap.put("facebook", 10);
        ///imageMap.put("linkedin", 11);
        ///imageMap.put("evernote", 12);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);

        return new MyViewHolder(view, mListener, context, arrayList, this);
    }
    //public int pos;
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        int iconId = R.mipmap.ic_account_circle_black_48dp;
        if (imageMap.containsKey(arrayList.get(position).getServiceName().toLowerCase())) {
            iconId = imageMap.get(arrayList.get(position).getServiceName().toLowerCase());
        }
        holder.icon.setImageResource(iconId);
        holder.serviceName.setText(arrayList.get(position).getServiceName());
        holder.loginName.setText(arrayList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView serviceName;
        TextView loginName;
        Context context;
        ArrayList<Password> mArrayList;
        //OnItemClickListener listener;

        public MyViewHolder(View itemView, final OnItemClickListener listener, final Context context, ArrayList<Password> arrayList, final RecyclerAdapter adapter) {
            super(itemView);
            icon = itemView.findViewById(R.id.imgAccount);
            serviceName = itemView.findViewById(R.id.serviceName);
            loginName = itemView.findViewById(R.id.login);
            mArrayList = arrayList;
            this.context = context;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Password password = mArrayList.get(getAdapterPosition());
                    //String mServiceName =
                    Bundle extras = new Bundle();
                    extras.putSerializable("pass", password);
                    Intent intent = new Intent(context, PasswordDetailsActivity.class);
                    intent.putExtras(extras);
                    context.startActivity(intent);
                    //if (listener != null) {
                    //    int position = getAdapterPosition();
                    //    if (position != RecyclerView.NO_POSITION) {
                    //        listener.onItemClick(position);
                    //    }
                    //}
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete item")
                            .setMessage("Do you really want to delete this password?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DBHelper db = new DBHelper(context);
                                    db.deletePassword(mArrayList.get(getAdapterPosition()).getId());
                                    mArrayList.remove(getAdapterPosition());
                                    adapter.notifyItemRemoved(getAdapterPosition());
                                }
                            }).create().show();

                    return false;
                }
            });
        }
    }

    public void setFilter(ArrayList<Password> newList) {
        arrayList = new ArrayList<>();
        arrayList.addAll(newList);
        notifyDataSetChanged();
    }
}