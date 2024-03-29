package uk.co.jakelee.blacksmithslots.components;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.SystemClock;

public class TransitionDrawable extends LayerDrawable implements
        Drawable.Callback {

    /**
     * A transition is about to start.
     */
    private static final int TRANSITION_STARTING = 0;

    /**
     * The transition has started and the animation is in progress
     */
    private static final int TRANSITION_RUNNING = 1;

    /**
     * No transition will be applied
     */
    private static final int TRANSITION_NONE = 2;

    private static final int TRANSITION_RATE = 3;

    /**
     * The current state of the transition. One of {@link #TRANSITION_STARTING},
     * {@link #TRANSITION_RUNNING} and {@link #TRANSITION_NONE}
     */
    private int mTransitionState = TRANSITION_NONE;

    private boolean mReverse;
    private long mStartTimeMillis;
    private int mFrom;
    private int mTo;
    private long mDuration;
    private long mOriginalDuration;
    private int mAlpha = 0;
    private boolean mCrossFade;

    private float rate;

    private final int layer_count;

    private int present;

    /**
     * Create a new transition drawable with the specified list of layers. At
     * least 2 layers are required for this drawable to work properly.
     */
    public TransitionDrawable(Drawable... layers) {
        super(layers);
        layer_count = layers.length;
        present = 0;
    }

    /**
     * Begin the second layer on top of the first layer.
     *
     * @param durationMillis
     *            The length of the transition in milliseconds
     */
    public void startTransition(long durationMillis) {
        mFrom = 0;
        mTo = 255;
        mAlpha = 0;
        mDuration = mOriginalDuration = durationMillis;
        mReverse = false;
        mTransitionState = TRANSITION_STARTING;
        invalidateSelf();
    }


    /**
     * Show only the first layer.
     */
    public void resetTransition() {
        mAlpha = 0;
        mTransitionState = TRANSITION_NONE;
        invalidateSelf();
    }

    /**
     * Reverses the transition, picking up where the transition currently is. If
     * the transition is not currently running, this will start the transition
     * with the specified duration. If the transition is already running, the
     * last known duration will be used.
     *
     * @param duration
     *            The duration to use if no transition is running.
     */
    public void reverseTransition(long duration) {
        final long time = SystemClock.uptimeMillis();
        // Animation is over
        if (time - mStartTimeMillis > mDuration) {
            if (mTo == 0) {
                mFrom = 0;
                mTo = 255;
                mAlpha = 0;
                mReverse = false;
            } else {
                mFrom = 255;
                mTo = 0;
                mAlpha = 255;
                mReverse = true;
            }
            mDuration = mOriginalDuration = duration;
            mTransitionState = TRANSITION_STARTING;
            invalidateSelf();
            return;
        }

        mReverse = !mReverse;
        mFrom = mAlpha;
        mTo = mReverse ? 0 : 255;
        mDuration = (int) (mReverse ? time - mStartTimeMillis
                : mOriginalDuration - (time - mStartTimeMillis));
        mTransitionState = TRANSITION_STARTING;
    }

    public void reverse(long duration){
        mTo = 0;
        mFrom=mAlpha=((rate!=0)?(int) (255 * rate): 255);
        mDuration = mOriginalDuration = duration;
        mTransitionState = TRANSITION_STARTING;
        invalidateSelf();
    }
    private int getNext(){
        return (present + 1== layer_count) ? 0: (present+1);
    }
    int getPrevious(){
        return (present-1==-1) ? (layer_count-1) : (present-1);
    }

    @Override
    public void draw(Canvas canvas) {
        boolean done = true;

        switch (mTransitionState) {
            case TRANSITION_STARTING:
                mStartTimeMillis = SystemClock.uptimeMillis();
                done = false;
                mTransitionState = TRANSITION_RUNNING;
                break;

            case TRANSITION_RUNNING:
                if (mStartTimeMillis >= 0) {
                    float normalized = (float) (SystemClock.uptimeMillis() - mStartTimeMillis)
                            / mDuration;
                    done = normalized >= 1.0f;
                    normalized = Math.min(normalized, 1.0f);
                    mAlpha = (int) (mFrom + (mTo - mFrom) * normalized);
                }
                break;
            case TRANSITION_RATE:
                float min = Math.min(rate, 1);
                mAlpha = (int) (mFrom + (mTo - mFrom) * min);
                done = false;
                break;
        }

        final int alpha = mAlpha;
        final boolean crossFade = mCrossFade;

        Drawable org = getDrawable(present);

        Drawable drawable = getDrawable(getNext());
        if (done) {
            if(mTransitionState==TRANSITION_RUNNING )
                present=getNext();
            // the setAlpha() calls below trigger invalidation and redraw. If
            // we're done, just draw
            // the appropriate drawable[s] and return
            if (!crossFade || alpha == 0) {
                org.draw(canvas);
            }
            if (alpha == 0xFF) {
                drawable.draw(canvas);
            }
            return;
        }

        Drawable d;
        d = org;
        if (crossFade) {
            d.setAlpha(255 - alpha);
        }
        d.draw(canvas);
        if (crossFade) {
            d.setAlpha(0xFF);
        }

        if (alpha > 0) {
            d = drawable;
            d.setAlpha(alpha);
            d.draw(canvas);
            d.setAlpha(0xFF);
        }

        if (!done) {
            invalidateSelf();
        }
    }

    /**
     * Enables or disables the cross fade of the drawables. When cross fade is
     * disabled, the first drawable is always drawn opaque. With cross fade
     * enabled, the first drawable is drawn with the opposite alpha of the
     * second drawable. Cross fade is disabled by default.
     *
     * @param enabled
     *            True to enable cross fading, false otherwise.
     */
    public void setCrossFadeEnabled(boolean enabled) {
        mCrossFade = enabled;
    }

    /**
     * Indicates whether the cross fade is enabled for this transition.
     *
     * @return True if cross fading is enabled, false otherwise.
     */
    public boolean isCrossFadeEnabled() {
        return mCrossFade;
    }

    public void setRate(float rate) {
        this.rate = rate;
        mFrom = 0;
        mTo = 255;
        mAlpha = 0;
        mTransitionState = TRANSITION_RATE;
        invalidateSelf();
    }

}
