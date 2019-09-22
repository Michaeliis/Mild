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

import org.joda.time.DateTime;

public class Register1 extends AppCompatActivity {
    protected Cursor cursor;
    SqlLiteConf dbHelper;
    EditText pass, pass2;
    Button submit;
    int user;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        //Initiate variable dan koneksi dengan database
        dbHelper = new SqlLiteConf(this);
        db = dbHelper.getWritableDatabase();

        user = countUser();
        pass = (EditText) findViewById(R.id.editPass);
        pass2 = (EditText) findViewById(R.id.editPass2);
        submit = (Button) findViewById(R.id.buttonRegister1);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if(isEmp(pass) || isEmp(pass2)){
                    Toast.makeText(getApplicationContext(), "Tidak Lengkap!", Toast.LENGTH_LONG).show();
                }else{
                    if(!getRes(pass).equals(getRes(pass2))){
                        Toast.makeText(getApplicationContext(), "Password Tidak Cocok!", Toast.LENGTH_LONG).show();
                        pass.setText("");
                        pass2.setText("");
                    }else{
                        if(checkPass(getRes(pass))){
                            try{
                                user = countUser() + 1;
                                String sql = String.format("INSERT INTO user(userId, userPass) values('%d', '%s')", user, getRes(pass));
                                db.execSQL(sql);
                                Toast.makeText(getApplicationContext(), "Berhasil Masuk", Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), Register.class);
                                i.putExtra("user", user);
                                startActivity(i);
                                finish();
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });
    }

    public int countUser(){
        cursor = db.rawQuery("SELECT * FROM user", null);
        int jumlah = cursor.getCount();
        return jumlah;
    }

    //Mengecek apakah password yang sama sudah ada di database
    public boolean checkPass(String newPass){
        boolean flag = true;
        cursor = db.rawQuery("SELECT * FROM user", null);
        int jumlah = cursor.getCount();
        for (int cc = 0; cc < jumlah; cc++) {
            cursor.moveToPosition(cc);
            if(cursor.getString(1).equals(newPass)){
                flag = false;
                break;
            }
        }
        return flag;
    }

    public String getRes(EditText text){
        return text.getText().toString();
    }


    public Boolean isEmp(EditText text){
        return (text.getText().toString() == "");
    }
}
