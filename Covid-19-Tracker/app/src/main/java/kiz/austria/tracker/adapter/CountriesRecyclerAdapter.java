package kiz.austria.tracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kiz.austria.tracker.R;
import kiz.austria.tracker.model.Nation;

public class CountriesRecyclerAdapter extends RecyclerView.Adapter<CountriesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "CountriesRecyclerAdapte";

    private ArrayList<Nation> mNations;

    public CountriesRecyclerAdapter(ArrayList<Nation> nations) {
        mNations = nations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_countries_item_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Nation nation = mNations.get(position);
        holder.mCountry.setText(nation.getCountry());
        holder.mCases.setText(nation.getCases());
        holder.mDeaths.setText(nation.getDeaths());
        holder.mRecovered.setText(nation.getRecovered());
        holder.mTodayCases.setText(nation.getTodayCases());
        holder.mTodayDeaths.setText(nation.getTodayDeaths());
        holder.mCritical.setText(nation.getCritical());
        holder.mActive.setText(nation.getActive());

    }

    @Override
    public int getItemCount() {
        return mNations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mCountry;
        private TextView mCases;
        private TextView mDeaths;
        private TextView mRecovered;
        private TextView mTodayCases;
        private TextView mTodayDeaths;
        private TextView mCritical;
        private TextView mActive;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCountry = itemView.findViewById(R.id.tv_countries_head_country);
            mCases = itemView.findViewById(R.id.tv_countries_head_cases);
            mDeaths = itemView.findViewById(R.id.tv_countries_head_deaths);
            mRecovered = itemView.findViewById(R.id.tv_countries_head_recovered);
            mTodayCases = itemView.findViewById(R.id.tv_countries_content_today_cases);
            mTodayDeaths = itemView.findViewById(R.id.tv_countries_content_today_deaths);
            mCritical = itemView.findViewById(R.id.tv_countries_content_critical);
            mActive = itemView.findViewById(R.id.tv_countries_content_active);

        }
    }
}
