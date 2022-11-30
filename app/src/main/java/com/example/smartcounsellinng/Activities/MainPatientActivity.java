package com.example.smartcounsellinng.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TabHost;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.smartcounsellinng.Adapters.ViewPagerAdapter;
import com.example.smartcounsellinng.Fragments.FriendsFragment;
import com.example.smartcounsellinng.Fragments.FriendsPatientFragment;
import com.example.smartcounsellinng.Fragments.MessagesFragment;
import com.example.smartcounsellinng.Fragments.MessagesPatientFragment;
import com.example.smartcounsellinng.Fragments.PersonalFragment;
import com.example.smartcounsellinng.Fragments.SettingsFragment;
import com.example.smartcounsellinng.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Vector;

public class MainPatientActivity extends AppCompatActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener{

    private TabHost tabHost;
    private ViewPager viewPager;
    private ViewPagerAdapter myViewPagerAdapter;
    private int i = 0;
    private Bundle bundle;
    private FirebaseAnalytics mFirebaseAnalytics;

    // fake content for tabhost
    class FakeContent implements TabHost.TabContentFactory {
        private final Context mContext;

        public FakeContent(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumHeight(0);
            v.setMinimumWidth(0);
            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_patient);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        i++;

        // init tabhost
        this.initializeTabHost(savedInstanceState);

        // init ViewPager
        this.initializeViewPager();

        bundle = getIntent().getExtras();
        if(bundle != null) {
            int tab = bundle.getInt("ReturnTab");
            tabHost.setCurrentTab(tab);
        }
    }

    private void initializeViewPager() {
        List<Fragment> fragments = new Vector<Fragment>();

        bundle = getIntent().getExtras();
        String uid = bundle.getString("UID");

        Bundle info = new Bundle();
        info.putString("UID",uid);
        PersonalFragment personalFragment = new PersonalFragment();
        personalFragment.setArguments(info); // info la thang bundle gui qua personal

        fragments.add(new MessagesPatientFragment());
        fragments.add(new FriendsPatientFragment());
        fragments.add(personalFragment);
        fragments.add(new SettingsFragment());

        this.myViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        this.viewPager = (ViewPager) super.findViewById(R.id.viewPager);
        this.viewPager.setAdapter(this.myViewPagerAdapter);
        this.viewPager.setOnPageChangeListener(this);
        onRestart();

    }

    private void initializeTabHost(Bundle args) {

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tabMessages = tabHost.newTabSpec("Message");
        tabMessages.setIndicator("",getResources().getDrawable(R.drawable.icon_messages_selector));
        tabMessages.setContent(new FakeContent(MainPatientActivity.this));
        tabHost.addTab(tabMessages);

        TabHost.TabSpec tabFriends = tabHost.newTabSpec("Friend");
        tabFriends.setIndicator("",getResources().getDrawable(R.drawable.icon_friends_selector));
        tabFriends.setContent(new FakeContent(MainPatientActivity.this));
        tabHost.addTab(tabFriends);

        TabHost.TabSpec tabPersonal = tabHost.newTabSpec("I");
        tabPersonal.setIndicator("",getResources().getDrawable(R.drawable.icon_personal_selector));
        tabPersonal.setContent(new FakeContent(MainPatientActivity.this));
        tabHost.addTab(tabPersonal);

        TabHost.TabSpec tabSettings = tabHost.newTabSpec("Setting");
        tabSettings.setIndicator("",getResources().getDrawable(R.drawable.icon_settings_selector));
        tabSettings.setContent(new FakeContent(MainPatientActivity.this));
        tabHost.addTab(tabSettings);


        tabHost.setOnTabChangedListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.tabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabChanged(String tabId) {
        int pos = this.tabHost.getCurrentTab();
        this.viewPager.setCurrentItem(pos);
    }
}
