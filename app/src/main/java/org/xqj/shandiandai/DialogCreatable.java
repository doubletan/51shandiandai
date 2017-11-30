package org.xqj.shandiandai;

import android.app.Dialog;

/**
 * Dialog 接口,用于创建对应的 Dialog
 */
public interface DialogCreatable {

    /**
     * Override to build your own custom Dialog container.
     *
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    Dialog onCreateCustomDialog(int dialogId);
}