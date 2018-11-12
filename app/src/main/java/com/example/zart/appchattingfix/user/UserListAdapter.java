package com.example.zart.appchattingfix.user;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zart.appchattingfix.ChatRoomActivity;
import com.example.zart.appchattingfix.MainActivity;
import com.example.zart.appchattingfix.R;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {


    ArrayList<UserObject> userlist;
    private Context mcontext;

    public UserListAdapter(Context mcontext, ArrayList<UserObject> userlist) {
        this.userlist = userlist;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutview);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, int position) {

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mcontext,ChatRoomActivity.class);
                intent.putExtra("nama",holder.mName.getText());
                intent.putExtra("phone",holder.mPhone.getText());
                mcontext.startActivity(intent);
            }
        });
        holder.mName.setText(userlist.get(position).getName());
        holder.mPhone.setText(userlist.get(position).getPhone());

    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mPhone;
        LinearLayout linearLayout;

        public UserListViewHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.parent_layout);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
        }
    }
}
