package project.test.mild;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import org.joda.time.DateTime;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;


public class Chart extends Fragment {
    protected Cursor cursor;
    SqlLiteConf dbcenter;
    SQLiteDatabase db;
    int user;
    Button thisMonth, allMonth;
    TextView cHappy, cNormal, cAngry, cSad;
    DateTimeFormatter justMonth = DateTimeFormat.forPattern("yyyy-MM");
    Pie pie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainBody obj = (MainBody)getActivity();
        user = obj.user;
        View v = inflater.inflate(R.layout.fragment_chart, container, false);

        //Initiate variable dan koneksi dengan database
        dbcenter = new SqlLiteConf(getActivity());
        db = dbcenter.getReadableDatabase();
        thisMonth = v.findViewById(R.id.buttonThisMonth);
        allMonth = v.findViewById(R.id.buttonAllMonth);
        cHappy = v.findViewById(R.id.textHappy);
        cNormal = v.findViewById(R.id.textNormal);
        cAngry = v.findViewById(R.id.textAngry);
        cSad = v.findViewById(R.id.textSad);
        pie = AnyChart.pie();
        final AnyChartView anyChartView = v.findViewById(R.id.thisChart);


        //Mengisi Chart sesuai permintaan
        refreshChart(pie, anyChartView, "all");

        thisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshChart(pie, anyChartView, "this");
            }
        });

        allMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshChart(pie, anyChartView, "all");
            }
        });

        return v;
    }

    public void refreshChart(Pie pie, AnyChartView anyChartView, String status){
        String sql = "SELECT * FROM diary WHERE userId = '"+user+"'";

        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int happy = 0, normal = 0 , angry = 0, sad = 0;

        DateTime now = new DateTime();
        LocalDate today = now.toLocalDate();
        now = today.toDateTimeAtCurrentTime();

        if(cursor.getCount() > 0){
            for (int cc = 0; cc < cursor.getCount(); cc++) {
                cursor.moveToPosition(cc);
                String date = cursor.getString(1);
                DateTime dateReal = DateTime.parse(date);

                if(cursor.getString(3).equals("Happy")){
                    if(status.equals("this") && justMonth.print(now).equals(justMonth.print(dateReal))){
                        happy++;
                    }else if (status.equals("all")){
                        happy++;
                    }
                }else if(cursor.getString(3).equals("Normal")){
                    if(status.equals("this") && justMonth.print(now).equals(justMonth.print(dateReal))){
                        normal++;
                    }else if (status.equals("all")){
                        normal++;
                    }
                }else if(cursor.getString(3).equals("Angry")){
                    if(status.equals("this") && justMonth.print(now).equals(justMonth.print(dateReal))){
                        angry++;
                    }else if (status.equals("all")){
                        angry++;
                    }
                }else if(cursor.getString(3).equals("Sad")){
                    if(status.equals("this") && justMonth.print(now).equals(justMonth.print(dateReal))){
                        sad++;
                    }else if (status.equals("all")){
                        sad++;
                    }
                }
            }
        }

        cHappy.setText("Happy : " + happy);
        cNormal.setText("Normal : " + normal);
        cAngry.setText("Angry : " + angry);
        cSad.setText("Sad : " + sad);

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Happy", happy));
        data.add(new ValueDataEntry("Normal", normal));
        data.add(new ValueDataEntry("Angry", angry));
        data.add(new ValueDataEntry("Sad", sad));

        pie.setData(data);
        anyChartView.setChart(pie);
    }

}
