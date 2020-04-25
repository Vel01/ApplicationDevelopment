package kiz.austria.tracker.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kiz.austria.tracker.model.Countries;

public class CountriesRecyclerAdapter extends RecyclerView.Adapter<CountriesRecyclerAdapter.ViewHolder> {
    //TODO: Complete Adapter Implementation.

    private ArrayList<Countries> mCountries;
    private Context mContext;

    public CountriesRecyclerAdapter(Context context, ArrayList<Countries> countries) {
        mContext = context;
        mCountries = countries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
