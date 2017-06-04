package uk.co.jakelee.blacksmithslots.components;

import com.popalay.tutors.TutorsBuilder;

public class TutorsBuilderFullscreen extends TutorsBuilder {

    private int textColorRes;
    private int shadowColorRes;
    private int textSizeRes;
    private int completeIconRes;
    private int spacingRes;
    private int lineWidthRes;
    private boolean cancelable;

    public TutorsBuilderFullscreen textColorRes(int textColorRes) {
        this.textColorRes = textColorRes;
        return this;
    }

    public TutorsBuilderFullscreen shadowColorRes(int shadowColorRes) {
        this.shadowColorRes = shadowColorRes;
        return this;
    }

    public TutorsBuilderFullscreen textSizeRes(int textSizeRes) {
        this.textSizeRes = textSizeRes;
        return this;
    }

    public TutorsBuilderFullscreen completeIconRes(int completeIconRes) {
        this.completeIconRes = completeIconRes;
        return this;
    }

    public TutorsBuilderFullscreen spacingRes(int spacingRes) {
        this.spacingRes = spacingRes;
        return this;
    }

    public TutorsBuilderFullscreen lineWidthRes(int lineWidthRes) {
        this.lineWidthRes = lineWidthRes;
        return this;
    }

    public TutorsBuilderFullscreen cancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public int getTextColorRes() {
        return textColorRes;
    }

    public int getShadowColorRes() {
        return shadowColorRes;
    }

    public int getTextSizeRes() {
        return textSizeRes;
    }

    public int getCompleteIconRes() {
        return completeIconRes;
    }

    public int getSpacingRes() {
        return spacingRes;
    }

    public int getLineWidthRes() {
        return lineWidthRes;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public TutorsFullscreen build() {
        return TutorsFullscreen.newInstance(this);
    }
}
