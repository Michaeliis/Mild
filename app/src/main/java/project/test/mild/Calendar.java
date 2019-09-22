package project.test.mild;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;


public class Calendar extends Fragment {
    Spinner selector;
    int user;
    protected Cursor cursor;
    SqlLiteConf dbcenter;
    SQLiteDatabase db;
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
    TextView titleMonth;
    Button addM, help, setting;
    Date selectedDate = new Date();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        //Initiate variable dan koneksi dengan database
        dbcenter = new SqlLiteConf(getActivity());
        db = dbcenter.getReadableDatabase();
        MainBody obj = (MainBody)getActivity();
        user = obj.user;
        addM = v.findViewById(R.id.addMButton);
        selector = v.findViewById(R.id.spinner);
        titleMonth = v.findViewById(R.id.textMonth);
        help = v.findViewById(R.id.buttonHelpOpen);
        setting = v.findViewById(R.id.buttonSettingOpen);
        final CompactCalendarView compactCalendarView = v.findViewById(R.id.compactcalendar);

        //Memberi value pada spinner
        String[] items = new String[]{"All", "Menstruation Only", "Ovulation Only"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        selector.setAdapter(adapter);

        //Mengisi kalender dengan default setting
        refreshCalendar("all", compactCalendarView);

        //Pilihan dari spinner dan action
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position){
                    case 0: refreshCalendar("all", compactCalendarView); break;
                    case 1: refreshCalendar("menstruation", compactCalendarView); break;
                    case 2: refreshCalendar("ovulation", compactCalendarView); break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // if nothing is selected
            }

        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Help.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Setting.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });

        //Action pada calendar
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate = dateClicked;
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                SimpleDateFormat monthOnly = new SimpleDateFormat("MMMM");
                titleMonth.setText(monthOnly.format(firstDayOfNewMonth));
            }
        });

        //Mengubah tanggal menstruasi terakhir
        addM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] dialogitem = {"Ya", "Tidak"};
                SimpleDateFormat questionDate = new SimpleDateFormat("dd-MMM");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Apakah pada " + questionDate.format(selectedDate)  +" menstruasi anda dimulai?");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                SimpleDateFormat parsingDate = new SimpleDateFormat("yyyy-MM-dd");
                                String newDate = parsingDate.format(selectedDate);
                                if(DateTime.parse(newDate).isBeforeNow()){
                                    String sql = String.format("UPDATE dateTable SET dateLast = '%s' WHERE userId = '%d'", newDate, user);
                                    db.execSQL(sql);
                                    Toast.makeText(getActivity(), "Berhasil diubah", Toast.LENGTH_LONG).show();
                                    refreshCalendar("all", compactCalendarView);
                                    selector.setSelection(0);
                                }else {
                                    Toast.makeText(getActivity(), "Tanggal Melewati Hari Ini!", Toast.LENGTH_LONG).show();
                                }

                                break;

                        }
                    }
                });
                builder.create().show();
            }
        });

        return v;
    }

    public void refreshCalendar(String type, final CompactCalendarView compactCalendarView){
        compactCalendarView.removeAllEvents();

        //Menarik data untuk prediksi tanggal
        DateTime lastTime = DateTime.now();
        int range = 1, span = 1;
        cursor = db.rawQuery("SELECT * FROM dateTable WHERE userId = '"+user+"'", null);
        if(cursor.getCount() > 0 ){
            cursor.moveToPosition(0);
            lastTime = DateTime.parse(cursor.getString(1), format);
            range = Integer.parseInt(cursor.getString(2));
            span = Integer.parseInt(cursor.getString(3));
        }

        DateTime lastCountDays, lastCountMonth = lastTime;

        //Setting hari pertama setiap minggu
        compactCalendarView.setFirstDayOfWeek(java.util.Calendar.SUNDAY);


        //Mengisi event prediksi berdasarkan pilihan
        while (lastCountMonth.isBefore(lastTime.plusYears(1))){

            if(type.equals("all") || type.equals("menstruation")){
                lastCountDays = lastCountMonth;
                while (lastCountDays.isBefore(lastCountMonth.plusDays(span))){
                    DateTime nextTime = lastCountDays;
                    long time = (nextTime.getMillis());
                    Event ev1 = new Event(Color.RED, time, "Menstruation Date");
                    compactCalendarView.addEvent(ev1);
                    lastCountDays = lastCountDays.plusDays(1);
                }
            }

            if(type.equals("all") || type.equals("ovulation")){
                lastCountDays = lastCountMonth.plusDays(range-16);
                for (int i =0; i < 3; i++){
                    DateTime nextTime = lastCountDays;
                    long time = (nextTime.getMillis());
                    Event ev1 = new Event(Color.GREEN, time, "Ovulation Date");
                    compactCalendarView.addEvent(ev1);
                    lastCountDays = lastCountDays.plusDays(1);
                }
            }
            lastCountMonth = lastCountMonth.plusDays(range);
        }
    }
// TODO: Rename method, update argument and hook method into UI event

}
