package kiz.austria.tracker.util;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import kiz.austria.tracker.model.Nation;

public class TrackerDiffUtil extends DiffUtil.Callback {

    private List<Nation> mOldList;
    private List<Nation> mNewList;

    public TrackerDiffUtil(List<Nation> oldList, List<Nation> newList) {
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
