package de.tadris.fitness.ui.dialog;

import android.app.AlertDialog;

/**
 * This interface is intended to be used by FitoTracks dialogs as a common way of showing (and
 * accessing) them.
 */
public interface AlertDialogWrapper {

    /**
     * Creates and shows the dialog.
     */
    void show();

    /**
     * Get the underlying {@link AlertDialog} object.
     * @return Underlying dialog
     * @apiNote This function will return null before the dialog was {@link #show() created}.
     */
    AlertDialog getDialog();
}
