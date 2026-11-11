package com.example.medicinereminder;

import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.*;
import android.content.*;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    DBHelper db;
    ArrayList<String> list;
    MedicineAdapter adapter;

    Button addBtn, calendarBtn;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔔 Notification Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }

        // 📌 Views
        listView = findViewById(R.id.listView);
        addBtn = findViewById(R.id.addBtn);
        fabAdd = findViewById(R.id.fabAdd);
        calendarBtn = findViewById(R.id.calendarBtn);

        db = new DBHelper(this);
        list = new ArrayList<>();

        // 📊 LOAD DATA FIRST
        loadData();

        // 💊 CLICK = MARK AS TAKEN (PROGRESS TRACKING)
        listView.setOnItemClickListener((parent, view, position, id) -> {

            Cursor c = db.getAllMedicines();

            if (c.moveToPosition(position)) {
                int medId = c.getInt(0);

                db.markTaken(medId);

                Toast.makeText(this, "✅ Taken Marked", Toast.LENGTH_SHORT).show();
                loadData();
            }
        });

        // 🗑️ LONG PRESS = DELETE
        listView.setOnItemLongClickListener((parent, view, position, id) -> {

            Cursor c = db.getAllMedicines();

            if (c.moveToPosition(position)) {
                int medId = c.getInt(0);

                db.deleteMedicine(medId);

                Toast.makeText(this, "🗑️ Deleted", Toast.LENGTH_SHORT).show();
                loadData();
            }

            return true;
        });

        // ➕ ADD BUTTON
        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AddMedicineActivity.class));
        });

        // 💎 FLOATING BUTTON (ANIMATION)
        fabAdd.setOnClickListener(v -> {

            Animation anim = AnimationUtils.loadAnimation(this, R.anim.bounce);
            v.startAnimation(anim);

            startActivity(new Intent(this, AddMedicineActivity.class));
        });

        // 📅 CALENDAR BUTTON
        if (calendarBtn != null) {
            calendarBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, CalendarActivity.class));
            });
        }
    }

    // 📊 LOAD DATA FUNCTION
    private void loadData() {

        list.clear();

        Cursor c = db.getAllMedicines();

        if (c.getCount() == 0) {
            list.add("No Medicines | -- | --");
        } else {
            while (c.moveToNext()) {

                String status = (c.getInt(4) == 1) ? "✔ Taken" : "⏰ Pending";

                list.add(
                        "💊 " + c.getString(1) +
                                " | ⏰ " + c.getString(2) +
                                " | 💉 " + c.getString(3) +
                                " | " + status
                );
            }
        }

        adapter = new MedicineAdapter(this, list);
        listView.setAdapter(adapter);

        // 💎 ANIMATION
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.item_animation);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        listView.setLayoutAnimation(controller);
        listView.scheduleLayoutAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}