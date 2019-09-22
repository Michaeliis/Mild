package project.test.mild;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Diary extends Fragment {
    protected Cursor cursor, cursor2;
    SqlLiteConf dbcenter;
    String[] daftar;
    ListView listview;
    TextView textdate;
    SQLiteDatabase db;
    Button submit;
    EditText textdiary;
    RadioGroup radiogroup;
    int user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_diary, container, false);

        //Initiate variable dan koneksi dengan database
        dbcenter = new SqlLiteConf(getActivity());
        db = dbcenter.getReadableDatabase();
        MainBody obj = (MainBody)getActivity();
        user = obj.user;

        textdate = v.findViewById(R.id.textDate);
        listview = v.findViewById(R.id.listdate);
        textdiary = v.findViewById(R.id.textDiary);
        submit = v.findViewById(R.id.buttonSubmit);
        radiogroup = v.findViewById(R.id.radioGroup);

        cursor = db.rawQuery("SELECT * FROM diary WHERE userId = '"+user+"'", null);
        daftar = new String[cursor.getCount() + 1];
        cursor.moveToFirst();
        daftar[0] = "Today";
        for (int cc = 1; cc <= cursor.getCount(); cc++) {
            cursor.moveToPosition(cc-1);
            daftar[cc] = cursor.getString(1);
        }

        listview.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, daftar));
        listview.setSelected(true);

        //Mengisi diary dengan catatan pada hari pilihan
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                final String selection = daftar[arg2];
                final CharSequence[] dialogitem = {"Lihat Diary"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                String diaryDay = "", feeling = "";

                                if(selection.equals("Today")){
                                    textdate.setText("Today");
                                    String date = getToday();
                                    cursor2 = db.rawQuery("SELECT * FROM diary WHERE diaryDate = '" +
                                            date + "'",null);
                                    cursor2.moveToFirst();

                                    if (cursor2.getCount()>0)
                                    {
                                        diaryDay = cursor2.getString(2);
                                        feeling = cursor2.getString(3);
                                        textdiary.setText(diaryDay);
                                        if(feeling.equals("Happy")){
                                            radiogroup.check(R.id.radioHappy);
                                        }else if(feeling.equals("Normal")){
                                            radiogroup.check(R.id.radioNormal);
                                        }else if(feeling.equals("Angry")){
                                            radiogroup.check(R.id.radioAngry);
                                        }else if(feeling.equals("Sad")){
                                            radiogroup.check(R.id.radioSad);
                                        }
                                    }else {
                                        textdiary.setText("");
                                        radiogroup.clearCheck();
                                    }

                                }else{
                                    cursor2 = db.rawQuery("SELECT * FROM diary WHERE diaryDate = '" +
                                            selection + "'",null);
                                    cursor2.moveToFirst();

                                    if (cursor2.getCount()>0)
                                    {
                                        diaryDay = cursor2.getString(2);
                                        feeling = cursor2.getString(3);
                                    }
                                    textdiary.setText(diaryDay);
                                    textdate.setText(selection);
                                    if(feeling.equals("Happy")){
                                        radiogroup.check(R.id.radioHappy);
                                    }else if(feeling.equals("Normal")){
                                        radiogroup.check(R.id.radioNormal);
                                    }else if(feeling.equals("Angry")){
                                        radiogroup.check(R.id.radioAngry);
                                    }else if(feeling.equals("Sad")){
                                        radiogroup.check(R.id.radioSad);
                                    }
                                }
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
        ((ArrayAdapter) listview.getAdapter()).notifyDataSetInvalidated();


        //Memasukkan diary baru atau mengubah diary lama
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                String date, diary, feeling;

                if(textdate.getText().equals("Today")){
                    date = getToday();
                }else{
                    date = textdate.getText().toString();
                }

                diary = getRes(textdiary);
                int happy, normal, sad, angry, selected;
                happy = R.id.radioHappy;
                normal = R.id.radioNormal;
                sad = R.id.radioSad;
                angry = R.id.radioAngry;

                feeling = "";
                selected = radiogroup.getCheckedRadioButtonId();
                if(selected == happy){
                    feeling = "Happy";
                } else if(selected == normal){
                    feeling = "Normal";
                } else if(selected == sad){
                    feeling = "Sad";
                } else if(selected == angry){
                    feeling = "Angry";
                }

                if(feeling.equals("") || diary.equals("")) {
                    Toast.makeText(getActivity(), "Data harus diisi!", Toast.LENGTH_LONG).show();
                }else{
                    if(textdate.getText().equals("Today")){
                        cursor2 = db.rawQuery("SELECT * FROM diary WHERE diaryDate = '" +
                                date + "'",null);
                        cursor2.moveToFirst();

                        if (cursor2.getCount()>0)
                        {
                            String sql = String.format("UPDATE diary SET diaryText = '%s', diaryFeel = '%s' WHERE diaryDate = '%s' AND userId = '%d'",diary, feeling, date,  user);
                            db.execSQL(sql);
                            Toast.makeText(getActivity(), "Berhasil Diubah", Toast.LENGTH_LONG).show();
                        }else {
                            String sql = String.format("INSERT INTO diary(userId, diaryDate, diaryText, diaryFeel) VALUES ('%d', '%s', '%s', '%s')", user, date, diary, feeling);
                            db.execSQL(sql);
                            Toast.makeText(getActivity(), "Berhasil Masuk", Toast.LENGTH_LONG).show();
                        }

                    }else{
                        String sql = String.format("UPDATE diary SET diaryText = '%s', diaryFeel = '%s' WHERE diaryDate = '%s' AND userId = '%d'",diary, feeling, date,  user);
                        db.execSQL(sql);
                        Toast.makeText(getActivity(), "Berhasil Diubah", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        return v;
    }

    public String getRes(EditText text){
        return text.getText().toString();
    }

    public String getToday(){
        DateTime now = new DateTime();
        LocalDate today = now.toLocalDate();
        now = today.toDateTimeAtCurrentTime();

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        String date = format.print(now);
        return date;
    }

}
