package com.example.zart.appchattingfix;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zart.appchattingfix.helper.ViewPagerAdapterFragment;
import com.example.zart.appchattingfix.model.MessageAdapter;
import com.example.zart.appchattingfix.model.Messages;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapterFragment viewPagerAdapter;
    private FirebaseListAdapter<Messages> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // getPermissions();
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        viewPagerAdapter = new ViewPagerAdapterFragment(getSupportFragmentManager());

        //nambah fragmen

        viewPagerAdapter.AddFragment(new FragmenChatting(),"Chatting");
        viewPagerAdapter.AddFragment(new FragmenKontak(),"Kontak");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);

        displasychatmessages();


    }

    private void displasychatmessages() {

        ListView listmessages = (ListView)findViewById(R.id.messages_list);

        adapter = new FirebaseListAdapter<Messages>(this, Messages.class,
        R.layout.activity_main, FirebaseDatabase.getInstance().getReference()){


            protected void populateView(View v, Messages Model, int position){
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);

                messageText.setText(Model.getMessage());
                messageUser.setText(Model.getFrom());
            }
        };
        listmessages.setAdapter((ListAdapter) adapter);

    }

    private class FirebaseListAdapter<T> {
        public FirebaseListAdapter(MainActivity mainActivity, Class<Messages> messagesClass, int activity_main, DatabaseReference reference) {

        }
    }
}
