package project.test.mild;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Setting extends Activity {

    protected Cursor cursor, cursor2;
    SqlLiteConf dbcenter
            ;
    SQLiteDatabase db;
    Button submit, back;
    EditText span, range;
    int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Initiate variable dan koneksi dengan database
        Intent intent = getIntent();
        user = intent.getIntExtra("user", 1);
        submit = (Button) findViewById(R.id.buttonSetting);
        span = (EditText) findViewById(R.id.editSpanSetting);
        range = (EditText) findViewById(R.id.editRangeSetting);
        back = (Button) findViewById(R.id.buttonBackSetting);

        dbcenter = new SqlLiteConf(this);
        db = dbcenter.getReadableDatabase();

        String sql = "SELECT * FROM dateTable WHERE userId = '"+user+"'", textSpan = "", textRange = "" ;
        cursor = db.rawQuery(sql, null);

        if(cursor.getCount() > 0){
            cursor.moveToPosition(0);
            textRange = cursor.getString(2);
            textSpan  = cursor.getString(3);
        }

        span.setText(textSpan);
        range.setText(textRange);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int newSpan, newRange;
                    newSpan = Integer.parseInt(span.getText().toString());
                    newRange = Integer.parseInt(range.getText().toString());
                    if(newRange < (newSpan + 16)) {
                        Toast.makeText(getApplicationContext(), "Tanggal Terlalu berdekatan!", Toast.LENGTH_LONG).show();
                    }else {
                        String sql2 = String.format("UPDATE dateTable SET dateRange = '%d', dateSpan = '%d' WHERE userId = '%d'", newRange, newSpan, user);
                        db.execSQL(sql2);
                        Toast.makeText(getApplicationContext(), "Berhasil diubah", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Masukkan angka!", Toast.LENGTH_LONG).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainBody.class);
                i.putExtra("user", user);
                startActivity(i);
                finish();
            }
        });
    }
}
