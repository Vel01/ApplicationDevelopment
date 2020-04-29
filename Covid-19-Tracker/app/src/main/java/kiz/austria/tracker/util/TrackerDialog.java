package kiz.austria.tracker.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import kiz.austria.tracker.R;

import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_ID;
import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_MESSAGE;
import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_NEGATIVE_RID;
import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_POSITIVE_RID;
import static kiz.austria.tracker.util.TrackerKeys.KEY_DIALOG_TITLE;
import static kiz.austria.tracker.util.TrackerKeys.KEY_STYLE;
import static kiz.austria.tracker.util.TrackerKeys.STYLE_DIALOG_CUSTOM;
import static kiz.austria.tracker.util.TrackerKeys.STYLE_DIALOG_NORMAL;

public class TrackerDialog extends DialogFragment {


    //variable
    private OnDialogEventListener mListener;
    private View mView;

    //primitives and text
    private String mTitle;
    private String mMessage;
    private int mDialogId;
    private int mPositiveId;
    private int mNegativeId;


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
        Bundle args = getArguments();

        String dialog_style;

        if (args != null) {
            dialog_style = args.getString(KEY_STYLE);
            mDialogId = args.getInt(KEY_DIALOG_ID);
            mTitle = args.getString(KEY_DIALOG_TITLE);
            mMessage = args.getString(KEY_DIALOG_MESSAGE);

            if (mDialogId == 0 || mMessage == null || dialog_style == null) {
                throw new IllegalArgumentException("must passed KEY_DIALOG_ID, KEY_STYLE and KEY_DIALOG_MESSAGE");
            }

            mPositiveId = args.getInt(KEY_DIALOG_POSITIVE_RID);
            if (mPositiveId == 0) {
                mPositiveId = R.string.label_dialog_ok;
            }

            mNegativeId = args.getInt(KEY_DIALOG_NEGATIVE_RID);
            if (mNegativeId == 0) {
                mNegativeId = R.string.label_dialog_cancel;
            }

        } else {
            throw new IllegalArgumentException("must passed KEY_DIALOG_MESSAGE");
        }

        setStyle(dialog_style, builder, args);

        return builder.create();
    }

    private void setStyle(String style, AlertDialog.Builder builder, final Bundle args) {
        switch (style) {
            case STYLE_DIALOG_NORMAL:
                builder.setMessage(mMessage).setTitle(mTitle)
                        .setPositiveButton(mPositiveId, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mListener != null) {
                                    mListener.onDialogPositiveEvent(mDialogId, args);
                                }
                            }
                        })
                        .setNegativeButton(mNegativeId, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mListener != null) {
                                    mListener.onDialogNegativeEvent(mDialogId, args);
                                }
                            }
                        });
                break;

            case STYLE_DIALOG_CUSTOM:
                builder.setView(mView);
                break;
        }
    }

    public void setView(View view) {
        mView = view;
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
