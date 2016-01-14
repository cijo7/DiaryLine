package com.solidskulls.diaryline;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by cijo-saju on 13/1/16.
 * Class used as a Settings TimePicker
 * //todo implement 12 hour Clock also
 */
public class TimePickerPreference extends DialogPreference{
    static int MAX_HOUR =12;
    static int MIN_HOUR=1;
    static int MIN_MIN=0;
    static int MAX_MINUTES=59;
    static String TIME="5:0 AM";
    /**
     * flagBind possible values
     * -1   :   Indicates Not Bind
     *  0   :   Indicate Bind
     *  1   :   Indicate From OnSavedInstance
     */
    private short flagBind=-1;


    private String mTime;
    private int mHour=0;
    private int mMinutes=0;
    private boolean mAM =true;

    private NumberPicker mNumberPickerHour, mNumberPickerMinutes, mMeridian;
    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_timepicker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view){
        super.onBindDialogView(view);
        if(flagBind!=1)
            flagBind=0;

        mNumberPickerHour =(NumberPicker)view.findViewById(R.id.preference_numberPicker_hour);
        mNumberPickerHour.setMinValue(MIN_HOUR);
        mNumberPickerHour.setMaxValue(MAX_HOUR);
        mNumberPickerHour.setWrapSelectorWheel(true);
        mNumberPickerHour.setValue(mHour);
        mNumberPickerHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mHour = picker.getValue();
            }
        });
        mNumberPickerHour.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });

        mNumberPickerMinutes =(NumberPicker)view.findViewById(R.id.preference_numberPicker_minute);
        mNumberPickerMinutes.setMinValue(MIN_MIN);
        mNumberPickerMinutes.setMaxValue(MAX_MINUTES);
        mNumberPickerMinutes.setWrapSelectorWheel(true);
        mNumberPickerMinutes.setValue(mMinutes);
        mNumberPickerMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mMinutes = picker.getValue();
            }
        });
        mNumberPickerMinutes.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });

        mMeridian =(NumberPicker)view.findViewById(R.id.preference_numberPicker_meridian);
        mMeridian.setMinValue(0);
        mMeridian.setMaxValue(1);
        mMeridian.setWrapSelectorWheel(false);
        mMeridian.setValue(mAM?0:1);
        mMeridian.setDisplayedValues(new String[]{"AM", "PM"});
        mMeridian.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mAM= (picker.getValue() == 0);
            }
        });
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            mNumberPickerHour.clearFocus();
            mNumberPickerMinutes.clearFocus();
            mHour= mNumberPickerHour.getValue();
            mMinutes= mNumberPickerMinutes.getValue();
            mAM=mMeridian.getValue()==0;
            mTime=String.format("%02d",mHour) + ":" + String.format("%02d",mMinutes)+" "+(mAM?"AM":"PM");
            callChangeListener(mTime);
            persistString(mTime);
        }else{
            mHour=getHour(mTime);
            mMinutes=getMinutes(mTime);
            mAM= getMeridian(mTime);
        }

        if(flagBind==0)//From Binding
            flagBind=-1;
        else            //From onSavedInstance
            flagBind=0;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mTime = this.getPersistedString(TIME);
        } else {
            mTime=(String )defaultValue;
            persistString(mTime);
        }
        mHour=getHour(mTime);
        mMinutes=getMinutes(mTime);
        mAM= getMeridian(mTime);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /**
     *
     * @param string String to Manipulate Hour from
     * @return Hours
     */
    private int getHour(String string){
        return Integer.parseInt(string.split(":")[0]);
    }

    /**
     *
     * @param string String to Manipulate Minutes from
     * @return Minutes
     */
    private int getMinutes(String string){
        try {
            return Integer.parseInt(string.split("[: ]")[1]);
        }catch (Exception e){
            Log.d("Timer","Unable to parse int.",e);
        }
        return 0;
    }

    private boolean getMeridian(String string){
        try {
            String str= string.split("[: ]")[2];
            return str.equals("AM")||str.equals("am");
        }catch (NullPointerException e){
            Log.d("Timer","No Meridian Given. We got Null.");
        }
        Log.d("Timer","Invalid Meridian. Returning dummy");
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
                    if (isPersistent()&&flagBind==-1) {
                        // No need to save instance state since it's persistent,
                        // use superclass state
                        return superState;
                    }
        flagBind=1;
        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.hour = mHour;
        myState.min=mMinutes;
        myState.am=mAM;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
                if (state == null || !state.getClass().equals(SavedState.class)) {
                    // Didn't save the state, so call superclass
                    super.onRestoreInstanceState(state);
                    return;
                }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        mHour=myState.hour;
        mMinutes=myState.min;
        mAM=myState.am;
        mNumberPickerHour.setValue(mHour);
        mNumberPickerMinutes.setValue(mMinutes);
        mMeridian.setValue(mAM?0:1);
    }

    /**
     * Subclass to provide method to SaveState.
     */
    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        int hour;
        int min;
        boolean am;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            hour = source.readInt();  // Change this to read the appropriate data type
            min=source.readInt();
            am=source.readByte()==1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeInt(hour);  // Change this to write the appropriate data type
            dest.writeInt(min);
            dest.writeByte((byte)( am ? 1 : 0));
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
