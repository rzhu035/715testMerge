package de.tadris.fitness.ui.dialog;

import android.app.Activity;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;

/**
 * Build a dialog to choose the auto workout timeout after which the workout is automatically stopped
 */
public class ChooseAutoTimeoutDialog extends NumberPickerDialog<Integer> {
    private static final int STEP_WIDTH = 5;
    private static final int NO_TIMEOUT = 0;
    private static final int MAX_VALUE = 60;

    private final ChooseAutoTimeoutDialog.AutoTimeoutSelectListener listener;
    private final int initialTimeoutM;

    /**
     * @param context           The context this dialog should be shown in
     * @param listener          The listener that is called when the user selects a delay
     * @param initialTimeoutM   Initially selected timeout in minutes
     */
    public ChooseAutoTimeoutDialog(Activity context, ChooseAutoTimeoutDialog.AutoTimeoutSelectListener listener, int initialTimeoutM) {
        super(context, context.getString(R.string.pref_auto_timeout_title));
        this.listener = listener;
        this.initialTimeoutM = initialTimeoutM;
    }


    /**
     * Initialize dialog with default timeout from preferences.
     * @param context           The context this dialog should be shown in
     * @param listener          The listener that is called when the user selects a delay
     */
    public ChooseAutoTimeoutDialog(Activity context, ChooseAutoTimeoutDialog.AutoTimeoutSelectListener listener) {
        super(context, context.getString(R.string.pref_auto_timeout_title));
        this.listener = listener;
        this.initialTimeoutM = Instance.getInstance(context).userPreferences.getAutoTimeout();
    }

    @Override
    protected int getOptionCount() {
        return (MAX_VALUE - NO_TIMEOUT) / STEP_WIDTH + 1;
    }

    @Override
    protected Integer getInitOption() {
        return initialTimeoutM;
    }

    @Override
    protected String format(Integer delayM) {
        return delayM == NO_TIMEOUT
                ? context.getText(R.string.notimeout).toString()
                : delayM + " " + context.getText(R.string.timeMinuteShort);
    }

    @Override
    protected int toOptionNum(Integer delayM) {
        int res = delayM / STEP_WIDTH;
        if (res < 0) {
            return 0;
        } else if (res >= getOptionCount()) {
            return getOptionCount() - 1;
        } else {
            return res;
        }
    }

    @Override
    protected Integer fromOptionNum(int optionNum) {
        return optionNum * STEP_WIDTH;
    }

    @Override
    protected void onSelectOption(Integer delayM) {
        listener.onSelectAutoTimeout(delayM);
    }

    public interface AutoTimeoutSelectListener {
        /**
         * @param delayM Selected auto workout timeout in minutes
         */
        void onSelectAutoTimeout(int delayM);
    }
}
