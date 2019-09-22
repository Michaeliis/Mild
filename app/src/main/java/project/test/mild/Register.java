package project.test.mild;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Register extends AppCompatActivity {

    SqlLiteConf dbHelper;
    EditText range, span, lastt;
    Button submit;
    int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initiate variable dan koneksi dengan database
        dbHelper = new SqlLiteConf(this);
        Intent intent = getIntent();
        user = intent.getIntExtra("user", 1);
        range = (EditText) findViewById(R.id.editRange);
        span = (EditText) findViewById(R.id.editSpan);
        lastt = (EditText) findViewById(R.id.editLast);
        submit = (Button) findViewById(R.id.buttonRegister);

        //Pengecekkan dari data yang telah dimasukkan
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try{
                    if(isEmp(range) || isEmp(span) || isEmp(lastt)){
                        Toast.makeText(getApplicationContext(), "Tidak Lengkap!", Toast.LENGTH_LONG).show();
                    }else if(Integer.parseInt(getRes(range)) < (Integer.parseInt(getRes(span)) + 16)) {
                        Toast.makeText(getApplicationContext(), "Tanggal Terlalu berdekatan!", Toast.LENGTH_LONG).show();
                    }else{
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        DateTime compareDate = new DateTime(getRes(lastt));
                        int rangeNum = Integer.parseInt(getRes(range));
                        int spanNum = Integer.parseInt(getRes(span));
                        if(compareDate.isBeforeNow()){
                            String sql = String.format("INSERT INTO dateTable(userId, dateLast, dateRange, dateSpan) values('%d', '%s', '%d', '%d')", user, getRes(lastt), rangeNum, spanNum);
                            db.execSQL(sql);
                            Toast.makeText(getApplicationContext(), "Berhasil Masuk", Toast.LENGTH_LONG).show();

                            Intent i = new Intent(getApplicationContext(), MainBody.class);
                            i.putExtra("user", user);
                            startActivity(i);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Tanggal Melewati Hari ini", Toast.LENGTH_LONG).show();
                        }

                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Ada masukkan yang salah!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public String getRes(EditText text){
        return text.getText().toString();
    }


    public Boolean isEmp(EditText text){
        return (text.getText().toString() == "");
    }

}
