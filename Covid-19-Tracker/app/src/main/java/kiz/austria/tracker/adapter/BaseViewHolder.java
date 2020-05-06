package kiz.austria.tracker.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    private int mCurrentPosition;

    BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected abstract void setFadeScaleAnimation();

    protected abstract void setFadeInAnimation();

    protected abstract void clear();

    protected abstract String numberFormat(String value);

    public void onBind(int position) {
        mCurrentPosition = position;
        clear();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
