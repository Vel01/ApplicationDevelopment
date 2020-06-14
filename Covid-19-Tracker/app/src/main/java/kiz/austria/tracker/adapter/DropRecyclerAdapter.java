package kiz.austria.tracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kiz.austria.tracker.R;
import kiz.austria.tracker.model.DOHDrop;
import kiz.austria.tracker.util.TrackerDiffUtil;

public class DropRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private static final String TAG = "DropRecyclerAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean mIsNormal = false;

    private ArrayList<DOHDrop> mDropList = new ArrayList<>();

    private OnAdapterClickListener mListener;

    public void setOnAdapterClickListener(final OnAdapterClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                mIsNormal = true;
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_drop_item, parent, false), mDropList);

            case VIEW_TYPE_EMPTY:
            default:
                mIsNormal = false;
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.shimmer_drop_item, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (mIsNormal) {
            holder.onBind(position);
            holder.itemView.setOnClickListener(v -> mListener.onItemClick(holder.getAdapterPosition()));
        }
    }

    public interface OnAdapterClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return (mDropList != null && mDropList.size() > 0) ? mDropList.size() : 8;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDropList != null && mDropList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        }
        return VIEW_TYPE_EMPTY;
    }

    public void addList(ArrayList<DOHDrop> dropList) {
        TrackerDiffUtil<DOHDrop> util = new TrackerDiffUtil<>(mDropList, dropList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(util);

        mDropList.clear();
        mDropList.addAll(dropList);
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        try {
            return mDropList.get(position).getCasesCode();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    public static class ViewHolder extends BaseViewHolder {

        final ArrayList<DOHDrop> mDropList;
        @BindView(R.id.tv_drop_code)
        TextView caseCode;
        @BindView(R.id.tv_drop_age)
        TextView age;
        @BindView(R.id.tv_drop_gender)
        TextView gender;
        @BindView(R.id.tv_drop_region)
        TextView region;
        @BindView(R.id.tv_drop_province)
        TextView province;
        @BindView(R.id.tv_drop_status)
        TextView status;
        @BindView(R.id.v_drop_divider)
        View divider;


        ViewHolder(@NonNull View itemView, final ArrayList<DOHDrop> dropList) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mDropList = dropList;
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            DOHDrop drop = mDropList.get(getAdapterPosition());

            if (drop.getCasesCode() != null)
                caseCode.setText(drop.getCasesCode());
            if (drop.getAge() != null)
                age.setText(drop.getAge());
            if (drop.getSex() != null)
                gender.setText(drop.getSex());
            if (drop.getRegionRes() != null)
                region.setText(drop.getRegionRes());
            if (drop.getProvCityRes() != null)
                province.setText(drop.getProvCityRes());

            if (!drop.getRecoveredOn().isEmpty()) {
                status.setText("Recovered");
                status.setBackground(ResourcesCompat.getDrawable(caseCode.getResources(), R.drawable.rounded_status_recovered, null));
            } else if (!drop.getDateDied().isEmpty()) {
                status.setText("Died");
                status.setBackground(ResourcesCompat.getDrawable(caseCode.getResources(), R.drawable.rounded_status_died, null));
            } else {
                status.setText("Active");
                status.setBackground(ResourcesCompat.getDrawable(caseCode.getResources(), R.drawable.rounded_status_active, null));
            }

            if (getAdapterPosition() == mDropList.size() - 1) divider.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void setFadeScaleAnimation() {
        }

        @Override
        protected void setFadeInAnimation() {
        }

        @Override
        protected void clear() {
            caseCode.setText("");
            age.setText("");
            gender.setText("");
            region.setText("");
            province.setText("");
            status.setText("");
        }

        @Override
        protected String numberFormat(String value) {
            return null;
        }
    }


    static class EmptyViewHolder extends BaseViewHolder {

        EmptyViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        @Override
        protected void setFadeScaleAnimation() {

        }

        @Override
        protected void setFadeInAnimation() {

        }

        @Override
        protected void clear() {

        }

        @Override
        protected String numberFormat(String value) {
            return null;
        }
    }
}
