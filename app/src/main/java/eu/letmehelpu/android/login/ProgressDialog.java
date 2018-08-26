package eu.letmehelpu.android.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.letmehelpu.android.R;

public class ProgressDialog extends DialogFragment {

    public static final String TAG = ProgressDialog.class.getSimpleName();
    private static final String ARG_MESSAGE_ID = "ARG_MESSAGE_ID";
    private static final String STATE_ERROR = "STATE_ERROR";

    private TextView dialogMessage;
    private View progress;
    private View errorIcon;

    @Nullable
    private String displayedError = null;

    public static ProgressDialog newFragment(@StringRes int messageId) {
        ProgressDialog dialog = new ProgressDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE_ID, messageId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogMessage = view.findViewById(R.id.dialog_message);
        progress = view.findViewById(R.id.progress);
        errorIcon = view.findViewById(R.id.error_icon);

        handleArguments();
        restoreState(savedInstanceState);
    }

    private void handleArguments() {
        Bundle args = getArguments();
        if (args == null) throw new RuntimeException("Create only with newFragment method");
        dialogMessage.setText(args.getInt(ARG_MESSAGE_ID));
    }

    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            displayedError = savedInstanceState.getString(STATE_ERROR);
        }
        if (displayedError != null) {
            displayError(displayedError);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_ERROR, displayedError);
    }

    public void displayError(String error) {
        displayedError = error;
        setCancelable(true);
        dialogMessage.setText(error);
        progress.setVisibility(View.GONE);
        errorIcon.setVisibility(View.VISIBLE);
    }
}
