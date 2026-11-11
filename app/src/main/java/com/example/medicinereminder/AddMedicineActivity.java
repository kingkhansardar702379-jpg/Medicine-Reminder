package com.example.medicinereminder;

import android.app.*;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMedicineActivity extends AppCompatActivity {

    EditText name, dose, time;
    Button pickTimeBtn, saveBtn;

    int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        name = findViewById(R.id.name);
        dose = findViewById(R.id.dose);
        time = findViewById(R.id.time);
        pickTimeBtn = findViewById(R.id.pickTimeBtn);
        saveBtn = findViewById(R.id.saveBtn);

        DBHelper db = new DBHelper(this);

        // 🕒 TIME PICKER
        pickTimeBtn.setOnClickListener(v -> {

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, h, m) -> {
                        hour = h;
                        minute = m;
                        time.setText(String.format("%02d:%02d", h, m));
                    },
                    12, 0, false
            );

            dialog.show();
        });

        // 💾 SAVE + ALARM
        saveBtn.setOnClickListener(v -> {

            String medName = name.getText().toString();
            String medTime = time.getText().toString();
            String medDose = dose.getText().toString();

            // 📅 AUTO DATE (NO ERROR WAY)
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date());

            db.addMedicine(medName, medTime, medDose, date);

            setAlarm(medName);

            Toast.makeText(this, "Saved + Alarm Set 🔔", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // 🔔 ALARM FUNCTION
    private void setAlarm(String name) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // ⚠️ agar time past ho gaya ho → next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medName", name);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                if (am.canScheduleExactAlarms()) {
                    am.setExact(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pi);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pi);
                }

            } else {
                am.setExact(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pi);
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}