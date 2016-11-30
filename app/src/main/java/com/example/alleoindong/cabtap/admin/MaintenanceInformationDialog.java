package com.example.alleoindong.cabtap.admin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.Maintenance;

/**
 * Created by alleoindong on 12/1/16.
 */

public class MaintenanceInformationDialog extends DialogFragment {
    public MaintenanceInformationDialog() {

    }

    public static MaintenanceInformationDialog newInstance(Maintenance maintenance) {
        MaintenanceInformationDialog frag = new MaintenanceInformationDialog();
        Bundle args = new Bundle();

        args.putString("plateNumber", maintenance.plateNumber);
        args.putDouble("cost", maintenance.cost);
        args.putString("date", maintenance.maintenanceDate);
        args.putString("maintenance", maintenance.maintenance);

        frag.setArguments(args);

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maintenance_info_dialog, container);

        String plateNumber = getArguments().getString("plateNumber", "No Plate");
        String date = getArguments().getString("date", "No Date");
        double cost = getArguments().getDouble("cost", 0);
        String maintenance = getArguments().getString("maintenance", "No maintenance");

        ((TextView) view.findViewById(R.id.lbl_platenumber)).setText(plateNumber);
        ((TextView) view.findViewById(R.id.lbl_date)).setText(date);
        ((TextView) view.findViewById(R.id.lbl_maintenance)).setText(maintenance);
        ((TextView) view.findViewById(R.id.lbl_cost)).setText(String.valueOf(cost));

        ((Button) view.findViewById(R.id.btn_hide_maintenance_info)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
