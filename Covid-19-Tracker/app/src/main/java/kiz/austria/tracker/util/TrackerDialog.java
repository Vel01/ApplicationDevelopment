package kiz.austria.tracker.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import kiz.austria.tracker.R;

import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_ID;
import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_MESSAGE;
import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_NEGATIVE_RID;
import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_POSITIVE_RID;
import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_TITLE;

public class TrackerDialog extends DialogFragment {


    private OnDialogEventListener mListener;

    public interface OnDialogEventListener {
        void onDialogPositiveEvent(int id, Bundle args);

        void onDialogNegativeEvent(int id, Bundle args);

        void onDialogCancelEvent(int id);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(context instanceof OnDialogEventListener)) {
            throw new ClassCastException(context.toString() + " must implement OnDialogEventListener.");
        }
        mListener = (OnDialogEventListener) getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /*
            Customize dialog depending on the argument passed on the bundle
         */
        final Bundle args = getArguments();
        final int dialog_id;
        String title;
        String message;
        int positive_id;
        int negative_id;

        if (args != null) {


            dialog_id = args.getInt(KEY_DIALOG_ID);
            title = args.getString(KEY_DIALOG_TITLE);
            message = args.getString(KEY_DIALOG_MESSAGE);

            if (dialog_id == 0 || message == null) {
                throw new IllegalArgumentException("must passed KEY_DIALOG_ID and KEY_DIALOG_MESSAGE");
            }

            positive_id = args.getInt(KEY_DIALOG_POSITIVE_RID);
            if (positive_id == 0) {
                positive_id = R.string.label_dialog_ok;
            }

            negative_id = args.getInt(KEY_DIALOG_NEGATIVE_RID);
            if (negative_id == 0) {
                negative_id = R.string.label_dialog_cancel;
            }

        } else {
            throw new IllegalArgumentException("must passed KEY_DIALOG_MESSAGE");
        }

        builder.setMessage(message).setTitle(title)
                .setPositiveButton(positive_id, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onDialogPositiveEvent(dialog_id, args);
                        }
                    }
                })
                .setNegativeButton(negative_id, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onDialogNegativeEvent(dialog_id, args);
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (mListener != null) {
            assert getArguments() != null;
            int dialog_id = getArguments().getInt(KEY_DIALOG_ID);
            mListener.onDialogCancelEvent(dialog_id);
        }
    }
}
