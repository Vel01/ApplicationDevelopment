package kiz.austria.tracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kiz.austria.tracker.R;
import kiz.austria.tracker.model.DOHDrop;
import kiz.austria.tracker.util.TrackerDiffUtil;

public class DropRecyclerAdapter extends RecyclerView.Adapter<DropRecyclerAdapter.ViewHolder> {

    private static final String TAG = "DropRecyclerAdapter";

    private ArrayList<DOHDrop> mDropList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_drop_item, parent, false);
        return new ViewHolder(view, mDropList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mDropList.size();
    }

    public void addList(ArrayList<DOHDrop> dropList) {
        TrackerDiffUtil<DOHDrop> util = new TrackerDiffUtil<>(mDropList, dropList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(util);

        mDropList.clear();
        mDropList.addAll(dropList);
        result.dispatchUpdatesTo(this);
    }

    public ArrayList<DOHDrop> getDropList() {
        return mDropList;
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
}
