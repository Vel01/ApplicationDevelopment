package kiz.austria.tracker.adapter;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterClickListener extends RecyclerView.SimpleOnItemTouchListener {

    private static final String TAG = "AdapterClickListener";

    private GestureDetectorCompat mDetectorCompat;
    private OnAdapterClickListener mListener;

    public interface OnAdapterClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

    }

    public AdapterClickListener(Context context, final RecyclerView recyclerView) {
        mDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }

    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        if (mDetectorCompat != null) {
            return mDetectorCompat.onTouchEvent(e);
        }
        return false;
    }
}
