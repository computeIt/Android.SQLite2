package com.example.addy.dblitecountries;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final String LOG_TAG = "myLog";
    String[] name = {"china", "usa", "brasilia", "russia", "japan", "germany", "egypt", "italy", "france", "canada"};
    int[] population = {1400, 311, 195, 142, 128, 82, 80, 60, 66, 35};
    String[] region = {"asia", "america", "america", "europe", "asia", "europe", "africa", "europe", "europe", "america"};

    Button btnGetAll, btnPopulation, btnFunction, btnAreaPopulation, btnSort, btnGroup;
    EditText inputFunction, inputPopulation, inputAreaPopulation;
    RadioGroup radioGroup;
    DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetAll = findViewById(R.id.btnGetAll);
        btnGetAll.setOnClickListener(this);

        btnFunction = findViewById(R.id.btnFunction);
        btnFunction.setOnClickListener(this);

        btnPopulation = findViewById(R.id.btnPopulation);
        btnPopulation.setOnClickListener(this);

        btnAreaPopulation = findViewById(R.id.btnAreaPoputation);
        btnAreaPopulation.setOnClickListener(this);

        btnSort = findViewById(R.id.btnSort);
        btnSort.setOnClickListener(this);

        btnGroup = findViewById(R.id.btnGroup);
        btnGroup.setOnClickListener(this);

        inputFunction = findViewById(R.id.inputFunction);
        inputPopulation = findViewById(R.id.inputPopulation);
        inputAreaPopulation = findViewById(R.id.inputAreaPopulation);

        radioGroup = findViewById(R.id.radioGroup);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query("mytable", null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < 10; i++) {
                contentValues.put("name", name[i]);
                contentValues.put("population", population[i]);
                contentValues.put("region", region[i]);
                Log.d(LOG_TAG, "id = " + database.insert("mytable", null, contentValues));
            }
        }
        cursor.close();
        dbHelper.close();
        onClick(btnGetAll);

    }

    @Override
    public void onClick(View v) {
        database = dbHelper.getWritableDatabase();

        String inputFunc = inputFunction.getText().toString();
        String inputPop = inputPopulation.getText().toString();
        String inputAreaPop = inputAreaPopulation.getText().toString();

        String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor cursor = null;

        switch (v.getId()) {
            case R.id.btnGetAll:
                Log.d(LOG_TAG, "все записи");
                cursor = database.query("mytable", null, null, null, null, null, null);
                break;
            case R.id.btnFunction:
                Log.d(LOG_TAG, "функция = " + inputFunc);
                columns = new String[]{inputFunc};
                cursor = database.query("mytable", columns, null, null, null, null, null);
                break;
            case R.id.btnPopulation:
                Log.d(LOG_TAG, "население больше " + inputPop);
                selection = "population > ?";
                selectionArgs = new String[]{inputPop};
                cursor = database.query("mytable", null, selection, selectionArgs, null, null, null);
                break;
            case R.id.btnGroup:
                Log.d(LOG_TAG, "население по региону ");
                columns = new String[]{"region", "sum(population) as population"};
                groupBy = "region";
                cursor = database.query("mytable", columns, null, null, groupBy, null, null);
                break;
            case R.id.btnAreaPoputation:
                Log.d(LOG_TAG, "население по региону больше чем " + inputAreaPop);
                columns = new String[]{"region", "sum(population) as population"};
                groupBy = "region";
                having = "sum(population) > " + inputAreaPop;
                cursor = database.query("mytable", columns, null, null, groupBy, having, null);
                break;
            case R.id.btnSort:
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.radioName:
                        Log.d(LOG_TAG, "сортировка по имени");
                        orderBy = "name";
                        break;
                    case R.id.radioPopulation:
                        Log.d(LOG_TAG, "сортировка по населению");
                        orderBy = "population";
                        break;
                    case R.id.radioRegion:
                        Log.d(LOG_TAG, "сортировка по региону");
                        orderBy = "region";
                        break;
                    default:
                        break;
                }
                cursor = database.query("mytable", null, null, null, null, null, orderBy);
                break;
            default:
                break;
        }

        if(cursor != null){
            if(cursor.moveToFirst()){
                String str;
                do{
                    str = "";
                    for(String cn : cursor.getColumnNames()){
                        str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);
                }while(cursor.moveToNext());
            }
            cursor.close();
        } else{
            Log.d(LOG_TAG, "cursor is null");
        }
        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "mytable", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "onCreate database");
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "population integer,"
                    + "region text" + ");");
        }

//        db.execSQL("create table mytable ("
//                + "id integer primary key autoincrement," + "name text,"
//                + "people integer," + "region text" + ");");

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
