package com.crossbowffs.nekosms.app;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;

public class MainFragment extends Fragment {
    public MainActivity getMainActivity() {
        return (MainActivity)getActivity();
    }

    @Override
    public Context getContext() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getContext();
        } else {
            return getActivity();
        }
    }

    private Snackbar makeSnackbar(int textId) {
        return getMainActivity().makeSnackbar(textId);
    }

    private Snackbar makeSnackbar(int textId, int actionTextId, View.OnClickListener listener) {
        return makeSnackbar(textId).setAction(actionTextId, listener);
    }

    public void showSnackbar(int textId, int actionTextId, View.OnClickListener listener) {
        makeSnackbar(textId, actionTextId, listener).show();
    }

    public void showSnackbar(int textId) {
        makeSnackbar(textId).show();
    }

    public void setTitle(int titleId) {
        getMainActivity().setTitle(titleId);
    }

    public void enableFab(int iconId, View.OnClickListener listener) {
        getMainActivity().enableFab(iconId, listener);
    }

    public void disableFab() {
        getMainActivity().disableFab();
    }

    public void onNewArguments(Bundle args) {

    }
}
