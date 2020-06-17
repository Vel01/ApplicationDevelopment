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
import kiz.austria.tracker.model.Case;
import kiz.austria.tracker.util.TrackerDiffUtil;

public class CaseRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private static final String TAG = "CaseRecyclerAdapter";

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean mIsNormal = false;

    private ArrayList<Case> mCases = new ArrayList<>();


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                mIsNormal = true;
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_case_item, parent, false), mCases);

            case VIEW_TYPE_EMPTY:
            default:
                mIsNormal = false;
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.shimmer_drop_case_item, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (mIsNormal) {
            holder.onBind(position);
        }

    }

    @Override
    public int getItemCount() {
        return (mCases != null && mCases.size() > 0) ? mCases.size() : 8;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        try {
            return mCases.get(position).getCaseNo();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mCases != null && mCases.size() > 0) {
            return VIEW_TYPE_NORMAL;
        }
        return VIEW_TYPE_EMPTY;
    }

    public void addList(ArrayList<Case> cases) {
        TrackerDiffUtil<Case> util = new TrackerDiffUtil<>(mCases, cases);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(util);

        mCases.clear();
        mCases.addAll(cases);
        result.dispatchUpdatesTo(this);
    }


    public static class ViewHolder extends BaseViewHolder {

        final ArrayList<Case> mCases;
        @BindView(R.id.tv_case_number)
        TextView caseNo;
        @BindView(R.id.tv_case_age)
        TextView age;
        @BindView(R.id.tv_case_gender)
        TextView gender;
        @BindView(R.id.tv_case_nationality)
        TextView nationality;
        @BindView(R.id.tv_case_hospital)
        TextView hospital;
        @BindView(R.id.tv_case_status)
        TextView status;

        ViewHolder(@NonNull View itemView, final ArrayList<Case> cases) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCases = cases;
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            Case current = mCases.get(getAdapterPosition());

            if (current.getCaseNo() != null) caseNo.setText(current.getCaseNo());
            if (current.getAge() != null) age.setText(current.getAge());
            if (current.getGender() != null) gender.setText(current.getGender());
            if (current.getNationality() != null) nationality.setText(current.getNationality());
            if (current.getHospitalAdmittedTo() != null)
                hospital.setText(current.getHospitalAdmittedTo());
            if (current.getHealthStatus() != null) {
                status.setText(current.getHealthStatus());
                String status = current.getHealthStatus().toLowerCase();
                if (status.equals("recovered")) {
                    this.status.setBackground(ResourcesCompat.getDrawable(this.status.getResources(), R.drawable.rounded_status_recovered, null));
                } else if (status.equals("died")) {
                    this.status.setBackground(ResourcesCompat.getDrawable(this.status.getResources(), R.drawable.rounded_status_died, null));
                } else {
                    this.status.setBackground(ResourcesCompat.getDrawable(this.status.getResources(), R.drawable.rounded_status_other, null));
                }
            }
        }

        @Override
        protected void setFadeScaleAnimation() {

        }

        @Override
        protected void setFadeInAnimation() {

        }

        @Override
        protected void clear() {
            caseNo.setText("");
            age.setText("");
            gender.setText("");
            nationality.setText("");
            hospital.setText("");
            status.setText("");
        }

        @Override
        protected String numberFormat(String value) {
            return null;
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {

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
