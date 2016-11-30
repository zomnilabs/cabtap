package com.example.alleoindong.cabtap.user;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.alleoindong.cabtap.BaseActivity;
import com.example.alleoindong.cabtap.DividerItemDecoration;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.admin.AddDriverActivity;
import com.example.alleoindong.cabtap.admin.DriverMaintenanceActivity;
import com.example.alleoindong.cabtap.models.Booking;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingHistoryActivity extends AppCompatActivity {

    @BindView(R.id.rcv_passenger_history) RecyclerView nRcvHistory;

    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        setTitle("Booking History");

        // Handle search intent
        handleIntent(getIntent());

        mDatabase = FirebaseDatabase.getInstance().getReference("bookings")
                .child(BaseActivity.uid);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nRcvHistory.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        nRcvHistory.setLayoutManager(mLayoutManager);
        nRcvHistory.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));

        mAdapter = new FirebaseRecyclerAdapter<Booking, BookingHistoryActivity.BookingHistory>(Booking.class,
                R.layout.passenger_history_item, BookingHistoryActivity.BookingHistory.class, mDatabase) {

            @Override
            protected void populateViewHolder(BookingHistoryActivity.BookingHistory viewHolder, Booking model, int position) {
                viewHolder.setPlateNumber(model.plateNumber);
                viewHolder.setStatus(model.status);
                viewHolder.setFareEstimate(model.fareEstimate);
                viewHolder.setFrom(model.passengerLocationName);
                viewHolder.setTo(model.destinationName);
            }

            @Override
            public void cleanup() {
                super.cleanup();
            }
        };

        nRcvHistory.setAdapter(mAdapter);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class BookingHistory extends RecyclerView.ViewHolder {
        View mView;

        public BookingHistory(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setPlateNumber(String name) {
            TextView field = (TextView) mView.findViewById(R.id.plate_number);
            field.setText(name);
        }

        public void setStatus(String text) {
            TextView field = (TextView) mView.findViewById(R.id.status);
            field.setText(text);
        }

        public void setFrom(String text) {
            TextView field = (TextView) mView.findViewById(R.id.from);
            field.setText(text);
        }

        public void setTo(String text) {
            TextView field = (TextView) mView.findViewById(R.id.to);
            field.setText(text);
        }

        public void setFareEstimate(Double fare) {
            TextView field = (TextView) mView.findViewById(R.id.fare_estimate);
            field.setText(String.format("%.2f",fare));
        }

    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }
}
