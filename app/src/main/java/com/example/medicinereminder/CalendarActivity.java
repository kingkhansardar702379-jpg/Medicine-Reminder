package com.example.medicinereminder;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    ListView listView;
    DBHelper db;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        listView = findViewById(R.id.listView);

        db = new DBHelper(this);
        list = new ArrayList<>();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

            loadMedicines(selectedDate);
        });
    }

    private void loadMedicines(String date) {

        list.clear();

        Cursor c = db.getAllMedicines();

        while (c.moveToNext()) {

            String dbDate = c.getString(4); // date column

            if (dbDate != null && dbDate.equals(date)) {

                list.add(
                        "💊 " + c.getString(1) +
                                " | ⏰ " + c.getString(2) +
                                " | 💉 " + c.getString(3)
                );
            }
        }

        if (list.isEmpty()) {
            list.add("No medicines for this date 😴");
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                list);

        listView.setAdapter(adapter);
    }
}