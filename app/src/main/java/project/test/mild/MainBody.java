package project.test.mild;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainBody extends AppCompatActivity {

    Fragment fragment;
    public static int user;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_body);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //Initiate variable dan fragment
        user = getIntent().getIntExtra("user", 1);

        fragment = new Calendar();
        loadFragment(fragment);

        /*mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);*/
    }

    public int getUser(){
        return user;
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
            return true;
        }
        return false;
    }

    //Pilihan berdasarkan BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.calendar:
                    bundle.putInt("user", user);
                    fragment.setArguments(bundle);

                    fragment = new Calendar();
                    break;
                case R.id.diary:
                    bundle.putInt("user", user);
                    fragment.setArguments(bundle);

                    fragment = new Diary();
                    break;
                case R.id.chart:
                    bundle.putInt("user", user);
                    fragment.setArguments(bundle);
                    fragment = new Chart();
                    break;
            }
            return loadFragment(fragment);
        }

    };
}