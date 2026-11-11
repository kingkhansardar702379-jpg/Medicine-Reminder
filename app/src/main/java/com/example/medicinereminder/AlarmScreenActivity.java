package com.example.medicinereminder;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmScreenActivity extends AppCompatActivity {

    static Ringtone ringtone;
    Vibrator vibrator;
    CameraManager cameraManager;
    String cameraId;

    boolean isFlashOn = false;
    Handler handler = new Handler();

    Runnable flashRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                isFlashOn = !isFlashOn;
                cameraManager.setTorchMode(cameraId, isFlashOn);
                handler.postDelayed(this, 500); // blink speed
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setShowWhenLocked(true);
        setTurnScreenOn(true);

        setContentView(R.layout.activity_alarm);

        Button stopBtn = findViewById(R.id.stopBtn);


        // 🔊 SOUND
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, sound);
        ringtone.play();

        // 📳 VIBRATION
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(
                    new long[]{0, 500, 500}, 0)); // repeat
        } else {
            vibrator.vibrate(new long[]{0, 500, 500}, 0);
        }

        // 🔦 FLASHLIGHT
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
            handler.post(flashRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 🛑 STOP BUTTON
        stopBtn.setOnClickListener(v -> {

            // stop sound
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }

            // stop vibration
            if (vibrator != null) {
                vibrator.cancel();
            }

            // stop flashlight
            try {
                handler.removeCallbacks(flashRunnable);
                cameraManager.setTorchMode(cameraId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        });
    }
}