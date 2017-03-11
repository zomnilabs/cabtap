package com.alleoindong.cabtap.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alleoindong.cabtap.R;

/**
 * Created by alleoindong on 12/1/16.
 */

public class BookingAcceptedDialog extends DialogFragment {
    public BookingAcceptedDialog() {

    }

    public static BookingAcceptedDialog newInstance(String title, String plateNumber) {
        BookingAcceptedDialog bookingAcceptedDialog = new BookingAcceptedDialog();
        Bundle args = new Bundle();

        // Set Arguments
        args.putString("title", title);
        args.putString("plateNumber", plateNumber);
        bookingAcceptedDialog.setArguments(args);

        return bookingAcceptedDialog;
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_accepted_dialog, container);

        String plateNumber = getArguments().getString("plateNumber", "No Plate");

        ((TextView) view.findViewById(R.id.lbl_platenumber)).setText(plateNumber);

        ((Button) view.findViewById(R.id.btn_hide_booking_info)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
