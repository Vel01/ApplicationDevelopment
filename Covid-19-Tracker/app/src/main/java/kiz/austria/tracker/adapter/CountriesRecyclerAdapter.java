package kiz.austria.tracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import kiz.austria.tracker.R;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerDiffUtil;

public class CountriesRecyclerAdapter extends RecyclerView.Adapter<CountriesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "CountriesRecyclerAdapte";

    private List<Nation> mNationsAdapterList = new ArrayList<>();

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
                holder.setFadeInAnimation();
                holder.onBind(position);
            }
            return;
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setFadeInAnimation();
        holder.mExpand.setOnClickListener(v -> {
            Nation nation = mNationsAdapterList.get(position);
            nation.setExpanded(!nation.isExpanded());
            notifyItemChanged(position, position);
        });
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mNationsAdapterList.size();
    }

    public void addList(ArrayList<Nation> nations) {
        TrackerDiffUtil util = new TrackerDiffUtil(mNationsAdapterList, nations);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(util);

        mNationsAdapterList.clear();
        mNationsAdapterList.addAll(nations);
        result.dispatchUpdatesTo(this);
    }

    class ViewHolder extends BaseViewHolder {

        //widgets
        @BindView(R.id.tv_countries_country)
        protected TextView mCountry;
        @BindView(R.id.tv_countries_confirmed)
        protected TextView mConfirmed;
        @BindView(R.id.tv_countries_deaths)
        protected TextView mDeaths;
        @BindView(R.id.tv_countries_recovered)
        protected TextView mRecovered;
        @BindView(R.id.tv_countries_deaths_today)
        protected TextView mTodayDeaths;
        @BindView(R.id.tv_countries_cases_today)
        protected TextView mTodayCases;
        @BindView(R.id.tv_countries_critical)
        protected TextView mCritical;
        @BindView(R.id.tv_countries_active)
        protected TextView mActive;
        @BindView(R.id.imb_countries_collapse)
        protected ImageView mCollapse;

        //layouts
        @BindView(R.id.layout_countries_content)
        protected ConstraintLayout mLayoutToExpand;
        @BindView(R.id.layout_countries_animated)
        protected ConstraintLayout mLayoutToAnimate;
        @BindView(R.id.constraint_countries_to_fade)
        protected ConstraintLayout mLayoutToFadeIn;

        @BindView(R.id.card_countries_click)
        protected LinearLayout mExpand;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            final Nation nation = mNationsAdapterList.get(position);
            if (nation.getCountry() != null)
                mCountry.setText(nation.getCountry());
            if (nation.getConfirmed() != null)
                mConfirmed.setText(numberFormat(nation.getConfirmed()));
            if (nation.getDeaths() != null)
                mDeaths.setText(numberFormat(nation.getDeaths()));
            if (nation.getRecovered() != null)
                mRecovered.setText(numberFormat(nation.getRecovered()));
            if (nation.getTodayDeaths() != null)
                mTodayDeaths.setText(numberFormat(nation.getTodayDeaths()));
            if (nation.getTodayCases() != null)
                mTodayCases.setText(numberFormat(nation.getTodayCases()));
            if (nation.getCritical() != null)
                mCritical.setText(numberFormat(nation.getCritical()));
            if (nation.getActive() != null)
                mActive.setText(numberFormat(nation.getActive()));

            setFadeInAnimation();

            boolean isExpanded = nation.isExpanded();
            mLayoutToFadeIn.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            mCollapse.setImageResource(isExpanded ? R.drawable.ic_expand_more : R.drawable.ic_expand_less);
        }

        @Override
        protected void setFadeScaleAnimation() {
            mLayoutToAnimate.setAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_scale_animation));
        }

        @Override
        protected void setFadeInAnimation() {
            mLayoutToFadeIn.setAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_in_animation));
        }

        @Override
        protected void clear() {
            mCountry.setText("");
            mConfirmed.setText("");
            mDeaths.setText("");
            mRecovered.setText("");
            mTodayDeaths.setText("");
            mTodayCases.setText("");
            mCritical.setText("");
            mActive.setText("");
            mLayoutToFadeIn.setVisibility(View.GONE);
            mCollapse.setImageResource(R.drawable.ic_expand_less);
        }

        @Override
        protected String numberFormat(String value) {
            return NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(value));
        }
    }
}
