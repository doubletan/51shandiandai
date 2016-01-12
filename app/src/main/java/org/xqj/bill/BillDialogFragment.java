package org.xqj.bill;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * @author Chaos
 *         2016/01/12.
 */
public class BillDialogFragment extends DialogFragment {
    private static final String KEY_DIALOG_ID = "key_dialog_id";

    private int mDialogId;

    private DialogCreatable mCreatable;

    public BillDialogFragment() {
        /* do nothing */
    }

    @SuppressLint("ValidFragment")
    public BillDialogFragment(DialogCreatable creatable, int dialogId) {
        mCreatable = creatable;
        mDialogId = dialogId;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCreatable != null) {
            outState.putInt(KEY_DIALOG_ID, mDialogId);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mDialogId = savedInstanceState.getInt(KEY_DIALOG_ID, 0);
        }
        return mCreatable.onCreateCustomDialog(mDialogId);
    }

    public int getDialogId() {
        return mDialogId;
    }
}
