package project.test.mild;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    protected Cursor cursor;
    SqlLiteConf dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initiate variable dan koneksi dengan database
        dbHelper = new SqlLiteConf(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM user";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        final Handler handler = new Handler();
        //Mengecek apakah sudah ada user
        if (cursor.getCount()>0)
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }, 5000L);
        }else{
            //Apabila user baru pertama kali masuk akan diarahkan ke register
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), Register1.class));
                    finish();
                }
            }, 5000L);
        }

    }
}
