package kiz.austria.tracker.util;

import android.animation.ValueAnimator;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

public class AnimationContract {

    private AnimationContract() {
        //empty constructor
    }

    public static class Display {

        private Display() {
            //empty constructor
        }

        public static void countNumber(final TextView mTextField, int value) {
            ValueAnimator animator = new ValueAnimator();
            animator.setObjectValues(0, value);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    mTextField.setText(NumberFormat.getNumberInstance(Locale.US).format(animation.getAnimatedValue()));
                }
            });
            animator.setDuration(1000);
            animator.start();
        }
    }

}
