package mgottein.me.sample;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

/**
 * Created by mgottein on 3/17/15.
 */
public class SettingsFragment extends DialogFragment {

    public interface SettingsListener {
        public void setGravity(int gravity);
        public void setShouldLineNumbersHugLine(boolean hugLine);
        public void layoutLineLumbersOnLeft(boolean layoutOnLeft);
    }

    private static final String ARG_GRAVITY = "gravity";
    private static final String ARG_HUG_LINE = "hugLine";
    private static final String ARG_LAYOUT_ON_LEFT = "layoutOnLeft";

    public static SettingsFragment newInstance(int gravity, boolean hugLine, boolean layoutOnLeft) {
        Bundle args = new Bundle();
        args.putInt(ARG_GRAVITY, gravity);
        args.putBoolean(ARG_HUG_LINE, hugLine);
        args.putBoolean(ARG_LAYOUT_ON_LEFT, layoutOnLeft);
        SettingsFragment frag = new SettingsFragment();
        frag.setArguments(args);
        return frag;
    }

    private int mGravity;
    private boolean mHugLine;
    private boolean mLayoutOnLeft;
    private Spinner mVerticalGravityOptions;
    private Spinner mHorizontalGravityOptions;
    private RadioGroup mHugLineOptions;
    private RadioGroup mLayoutOnLeftOptions;
    private Button mApplyButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(savedInstanceState != null) {
            args = savedInstanceState;
        }
        mGravity = args.getInt(ARG_GRAVITY);
        mHugLine = args.getBoolean(ARG_HUG_LINE);
        mLayoutOnLeft = args.getBoolean(ARG_LAYOUT_ON_LEFT);
    }

    private int getSelectedGravity() {
        int verticalGravity = 0, horizontalGravity = 0;
        switch(mHorizontalGravityOptions.getSelectedItemPosition()) {
            case 0:
                verticalGravity = Gravity.LEFT;
                break;
            case 1:
                verticalGravity = Gravity.CENTER_HORIZONTAL;
                break;
            case 2:
                verticalGravity = Gravity.RIGHT;
                break;
        }
        switch(mVerticalGravityOptions.getSelectedItemPosition()) {
            case 0:
                horizontalGravity = Gravity.TOP;
                break;
            case 1:
                horizontalGravity = Gravity.CENTER_VERTICAL;
                break;
            case 2:
                horizontalGravity = Gravity.BOTTOM;
                break;
        }
        return verticalGravity | horizontalGravity;
    }

    private boolean getSelectedHugLine() {
        return mHugLineOptions.getCheckedRadioButtonId() == R.id.hug_true ? true : false;
    }

    private boolean getSelectedLayoutOnLeft() {
        return mLayoutOnLeftOptions.getCheckedRadioButtonId() == R.id.layout_on_left_true ? true : false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_GRAVITY, getSelectedGravity());
        outState.putBoolean(ARG_HUG_LINE, getSelectedHugLine());
        outState.putBoolean(ARG_LAYOUT_ON_LEFT, getSelectedLayoutOnLeft());
    }

    private int getGravityHorizontalIndex() {
        switch(mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                return 0;
            case Gravity.CENTER_HORIZONTAL:
                return 1;
            case Gravity.RIGHT:
                return 2;
            default:
                throw new IllegalStateException("invalid horizontal gravity");
        }
    }

    private int getGravityVerticalIndex() {
        switch(mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                return 0;
            case Gravity.CENTER_VERTICAL:
                return 1;
            case Gravity.BOTTOM:
                return 2;
            default:
                throw new IllegalStateException("invalid vertical gravity");
        }
    }

    private void applySettings() {
        SettingsListener settingsListener = (SettingsListener) getActivity();
        settingsListener.setGravity(getSelectedGravity());
        settingsListener.setShouldLineNumbersHugLine(getSelectedHugLine());
        settingsListener.layoutLineLumbersOnLeft(getSelectedLayoutOnLeft());
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.settings_fragment, container, false);
        mVerticalGravityOptions = (Spinner) root.findViewById(R.id.vertical_gravity_spinner);
        mHorizontalGravityOptions = (Spinner) root.findViewById(R.id.horizontal_gravity_spinner);
        mHugLineOptions = (RadioGroup) root.findViewById(R.id.hug_options);
        mLayoutOnLeftOptions = (RadioGroup) root.findViewById(R.id.layout_on_left_options);
        mApplyButton = (Button) root.findViewById(R.id.apply_settings);
        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applySettings();
            }
        });
        mVerticalGravityOptions.setSelection(getGravityVerticalIndex());
        mHorizontalGravityOptions.setSelection(getGravityHorizontalIndex());
        mHugLineOptions.check(mHugLine ? R.id.hug_true : R.id.hug_false);
        mLayoutOnLeftOptions.check(mLayoutOnLeft ? R.id.layout_on_left_true : R.id.layout_on_left_false);
        return root;
    }
}
