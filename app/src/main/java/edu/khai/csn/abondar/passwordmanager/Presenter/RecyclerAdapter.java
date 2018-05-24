package edu.khai.csn.abondar.passwordmanager.Presenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import edu.khai.csn.abondar.passwordmanager.Model.Cryptography;
import edu.khai.csn.abondar.passwordmanager.Model.DBHelper;
import edu.khai.csn.abondar.passwordmanager.Model.Entities.Password;
import edu.khai.csn.abondar.passwordmanager.R;
import edu.khai.csn.abondar.passwordmanager.View.PasswordDetailsActivity;

import static android.content.Context.CLIPBOARD_SERVICE;

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
        imageMap.put("vk", R.drawable.ic_icon_vk);
        imageMap.put("steam", R.drawable.ic_icon_steam);
        imageMap.put("twitch", R.drawable.ic_icon_twitch);
        imageMap.put("github", R.drawable.ic_icon_github);
        imageMap.put("elearn", R.drawable.ic_icon_elearn);
        imageMap.put("dropbox", R.drawable.ic_icon_dropbox);
        //imageMap.put("apple", R.drawable.ic_icon_apple);
        imageMap.put("facebook", R.drawable.ic_icon_facebook);
        imageMap.put("linkedin", R.drawable.ic_icon_linkedin);
        imageMap.put("evernote", R.drawable.ic_icon_evernote);
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

        int clipBoardId = R.drawable.ic_icon_clipboard;
        if (imageMap.containsKey(arrayList.get(position).getServiceName().toLowerCase())) {
            iconId = imageMap.get(arrayList.get(position).getServiceName().toLowerCase());
        }
        holder.icon.setImageResource(iconId);
        holder.serviceName.setText(arrayList.get(position).getServiceName());
        holder.loginName.setText(arrayList.get(position).getUserName());
        holder.clipboard.setImageResource(clipBoardId);
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
        ImageView clipboard;
        ClipboardManager clipboardManager;
        ClipData clipData;
        Cryptography crypto;
        //OnItemClickListener listener;

        public MyViewHolder(View itemView, final OnItemClickListener listener, final Context context, ArrayList<Password> arrayList, final RecyclerAdapter adapter) {
            super(itemView);
            icon = itemView.findViewById(R.id.imgAccount);
            serviceName = itemView.findViewById(R.id.serviceName);
            loginName = itemView.findViewById(R.id.login);
            clipboard = itemView.findViewById(R.id.copyToClipboard);
            clipboardManager=(ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
            crypto = new Cryptography("passwordmanager1");

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
            clipboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String clipText = mArrayList.get(getAdapterPosition()).getPassword();
                    try{clipText = crypto.decrypt(clipText);}catch (Exception e){}
                    clipData = ClipData.newPlainText("password", clipText);
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(context, "Password Copied", Toast.LENGTH_LONG).show();
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