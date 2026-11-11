package com.example.medicinereminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import java.util.ArrayList;

public class MedicineAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> list;

    public MedicineAdapter(Context context, ArrayList<String> list) {
        super(context, R.layout.medicine_item, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.medicine_item, parent, false);

        TextView name = view.findViewById(R.id.medName);
        TextView time = view.findViewById(R.id.medTime);
        TextView dose = view.findViewById(R.id.medDose);

        String data = list.get(position);
        String[] parts = data.split("\\|");

        // safety check (crash avoid)
        if (parts.length >= 3) {
            name.setText("💊 " + parts[0]);
            time.setText("⏰ " + parts[1]);
            dose.setText("💉 " + parts[2]);
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.bounce);
            view.startAnimation(anim);

            return view;
        }

        return view;
    }
}