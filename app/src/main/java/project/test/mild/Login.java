package project.test.mild;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    protected Cursor cursor;
    SqlLiteConf dbHelper;
    Button submit, newUser;
    EditText password;
    int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initiate variable dan koneksi dengan database
        dbHelper = new SqlLiteConf(this);
        submit = (Button) findViewById(R.id.buttonLogin);
        newUser = (Button) findViewById(R.id.buttonNewUser);
        password = (EditText) findViewById(R.id.textPassword);

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Register1.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void check(View v){
        //Mengecek apakah user ada dalam database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM user WHERE userPass = '" +
                password.getText().toString() + "'",null);
        cursor.moveToFirst();

        if (cursor.getCount()>0)
        {
            cursor.moveToPosition(0);
            user = Integer.parseInt(cursor.getString(0));
            Intent i = new Intent(getApplicationContext(), MainBody.class);
            i.putExtra("user", user);
            startActivity(i);
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Password salah", Toast.LENGTH_LONG).show();
        }
    }
}
