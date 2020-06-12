package kiz.austria.tracker.util;

import android.os.Parcelable;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class TrackerDiffUtil<T extends Parcelable> extends DiffUtil.Callback {

    private List<T> mOldList;
    private List<T> mNewList;

    public TrackerDiffUtil(List<T> oldList, List<T> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition) == mNewList.get(newItemPosition);

    }
}
