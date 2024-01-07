package com.example.greenstep;

import android.util.Log;

public class EditAvailabilityManager {
    private boolean isEditAvailable = false;

    public void toggleEditAvailability() {
        isEditAvailable = !isEditAvailable;
        Log.d("EditStatusRETURN FROM TOGGLE", "isEditAvailable TOGGLE FROM MANAGER: " + isEditAvailable);

    }

    public boolean isEditAvailable() {
        Log.d("EditStatusRETURN FROM MANAGER", "isEditAvailableRETURN FROM MANAGER: " + isEditAvailable);

        return isEditAvailable;
    }
}
