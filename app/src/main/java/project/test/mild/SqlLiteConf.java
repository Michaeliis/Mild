package project.test.mild;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class SqlLiteConf extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "mild.db";
    private static final int DATABASE_VERSION = 5;

    public SqlLiteConf(Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

}
