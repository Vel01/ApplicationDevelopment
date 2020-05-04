package kiz.austria.tracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_countries_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.get(0) instanceof Integer) {
                bind(holder);
            }
            return;
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setFadeScaleAnimation(holder.mLayoutToFadeScale);
        bind(holder);
    }


    private void bind(ViewHolder holder) {
        Nation nation = mNations.get(holder.getAdapterPosition());

        holder.mCountry.setText(nation.getCountry());
        holder.mConfirmed.setText(nation.getConfirmed());
        holder.mDeaths.setText(nation.getDeaths());
        holder.mRecovered.setText(nation.getRecovered());
        holder.mTodayDeaths.setText(nation.getTodayDeaths());
        holder.mTodayCases.setText(nation.getTodayCases());
        holder.mCritical.setText(nation.getCritical());
        holder.mActive.setText(nation.getActive());

        setFadeInAnimation(holder.mLayoutToFadeIn);

        boolean isExpanded = nation.isExpanded();
        holder.mLayoutToExpand.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.mCollapse.setImageResource(isExpanded ? R.drawable.ic_expand_more : R.drawable.ic_expand_less);

    }

    private void setFadeScaleAnimation(View view) {
        view.setAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_scale_animation));
    }

    private void setFadeInAnimation(View view) {
        view.setAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in_animation));
    }

    @Override
    public int getItemCount() {
        return mNations.size();
    }

    public void addFilter(ArrayList<Nation> nations) {
        mNations = nations;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //widgets
        private TextView mCountry;
        private TextView mConfirmed;
        private TextView mDeaths;
        private TextView mRecovered;
        private TextView mTodayDeaths;
        private TextView mTodayCases;
        private TextView mCritical;
        private TextView mActive;
        private ImageView mCollapse;

        //layouts
        private LinearLayout mLayoutToFadeScale;
        private LinearLayout mLayoutToExpand;
        private ConstraintLayout mLayoutToFadeIn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            //widgets
            mCountry = itemView.findViewById(R.id.tv_countries_country);
            mConfirmed = itemView.findViewById(R.id.tv_countries_confirmed);
            mDeaths = itemView.findViewById(R.id.tv_countries_deaths);
            mRecovered = itemView.findViewById(R.id.tv_countries_recovered);
            mTodayDeaths = itemView.findViewById(R.id.tv_countries_deaths_today);
            mTodayCases = itemView.findViewById(R.id.tv_countries_cases_today);
            mCritical = itemView.findViewById(R.id.tv_countries_critical);
            mActive = itemView.findViewById(R.id.tv_countries_active);
            mCollapse = itemView.findViewById(R.id.imb_countries_collapse);

            //layouts
            mLayoutToFadeScale = itemView.findViewById(R.id.layout_countries_fade_scale);
            mLayoutToExpand = itemView.findViewById(R.id.layout_countries_expand);
            mLayoutToFadeIn = itemView.findViewById(R.id.constraint_countries_to_fade);

            //event
            CardView update = itemView.findViewById(R.id.card_countries_event);

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Nation nation = mNations.get(getAdapterPosition());
                    nation.setExpanded(!nation.isExpanded());
                    notifyItemChanged(getAdapterPosition(), getAdapterPosition());
                }
            });

        }
    }
}
