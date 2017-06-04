package uk.co.jakelee.blacksmithslots.components;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.popalay.tutors.Tutors;

public class TutorsFullscreen extends Tutors {
    private static final String ARG_TEXT_COLOR = "TEXT_COLOR";
    private static final String ARG_TEXT_SIZE = "TEXT_SIZE";
    private static final String ARG_SHADOW_COLOR = "SHADOW_COLOR";
    private static final String ARG_COMPLETE_ICON = "COMPLETE_ICON";
    private static final String ARG_SPACING = "SPACING";
    private static final String ARG_LINE_WIDTH = "LINE_WIDTH";
    private static final String ARG_CANCELABLE = "CANCELABLE";

    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /*@Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }*/

    @Override
    public void show(FragmentManager manager, String tag) {
        for (Fragment fragment : manager.getFragments()) {
            fragment.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
        super.show(manager, tag);
        for (Fragment fragment : manager.getFragments()) {
            fragment.getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            fragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        } else {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        }
        super.onCreate(savedInstanceState);

    }

    static TutorsFullscreen newInstance(TutorsBuilderFullscreen builder) {
        final Bundle args = new Bundle();
        final TutorsFullscreen fragment = new TutorsFullscreen();

        args.putInt(ARG_TEXT_COLOR, builder.getTextColorRes());
        args.putInt(ARG_SHADOW_COLOR, builder.getShadowColorRes());
        args.putInt(ARG_TEXT_SIZE, builder.getTextSizeRes());
        args.putInt(ARG_COMPLETE_ICON, builder.getCompleteIconRes());
        args.putInt(ARG_SPACING, builder.getSpacingRes());
        args.putInt(ARG_LINE_WIDTH, builder.getLineWidthRes());
        args.putBoolean(ARG_CANCELABLE, builder.isCancelable());

        fragment.setArguments(args);
        return fragment;
    }

}
