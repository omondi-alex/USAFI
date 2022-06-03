package com.ramgdeveloper.taka;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllWastesRecyclerAdapter extends FirebaseRecyclerAdapter<Waste, AllWastesRecyclerAdapter.ViewHolder> {

    public AllWastesRecyclerAdapter(@NonNull FirebaseRecyclerOptions<Waste> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Waste model) {
        holder.wasteAndLocation.setText(model.getWasteType() + " waste to be collected in " + model.getLocation());
        holder.clientAndDate.setText("Requested by " + model.getClientName() + " on " + model.getDateRequested());
        holder.phoneNumber.setText(model.getPhoneNum());

        holder.deleteWaste.setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("all_wastes");
            DatabaseReference itemRef = getRef(position);
            String key = itemRef.getKey();
            assert key != null;
            databaseReference.child(key).removeValue();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.all_wastes_row, parent, false));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView wasteAndLocation, clientAndDate, phoneNumber;
        ImageView deleteWaste;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wasteAndLocation = itemView.findViewById(R.id.type_location);
            clientAndDate = itemView.findViewById(R.id.client_date);
            phoneNumber = itemView.findViewById(R.id.phoneNumb);
            deleteWaste = itemView.findViewById(R.id.deleteWaste);
        }
    }
}