package com.caringaide.user.interfaces;

import androidx.fragment.app.Fragment;

/**
 * listener to be used for changing fragments from adapter
 */
public interface ChangeFragmentListener {
        void changeToTargetFragment(Fragment fragment);
}
