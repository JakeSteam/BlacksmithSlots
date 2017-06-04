package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.tourguide.ChainTourGuide;
import uk.co.jakelee.blacksmithslots.tourguide.Overlay;
import uk.co.jakelee.blacksmithslots.tourguide.Sequence;
import uk.co.jakelee.blacksmithslots.tourguide.ToolTip;
import uk.co.jakelee.blacksmithslots.tourguide.TourGuide;


public class TutorialHelper {
    public static boolean currentlyInTutorial = false;
    public static ChainTourGuide chainTourGuide;
    public static int currentStage;
    private final List<ChainTourGuide> tourGuides = new ArrayList<>();
    private final Animation enterAnimation = new AlphaAnimation(0f, 1f);
    private Activity activity;

    public TutorialHelper(Activity activity, int stage) {
        currentStage = stage;
        this.activity = activity;
        tourGuides.clear();

        enterAnimation.setDuration(300);
        enterAnimation.setFillAfter(false);
    }

    public void addTutorial(View view, int bodyID, boolean clickable) {
        addTutorial(view, activity.getString(bodyID), Overlay.Style.CIRCLE, clickable, Gravity.CENTER);
    }

    public void addTutorial(View view, int bodyID, boolean clickable, int gravity) {
        addTutorial(view, activity.getString(bodyID), Overlay.Style.CIRCLE, clickable, gravity);
    }

    public void addTutorialNoOverlay(View view, int bodyID, boolean clickable) {
        addTutorial(view, activity.getString(bodyID), Overlay.Style.NO_HOLE, clickable, Gravity.CENTER);
    }

    public void addTutorialNoOverlay(View view, int bodyID, boolean clickable, int gravity) {
        addTutorial(view, activity.getString(bodyID), Overlay.Style.NO_HOLE, clickable, gravity);
    }

    public void addTutorialRectangle(View view, int bodyID, boolean clickable, int gravity) {
        addTutorial(view, activity.getString(bodyID), Overlay.Style.RECTANGLE, clickable, gravity);
    }

    public void addTutorialRectangle(View view, int bodyID, boolean clickable) {
        addTutorial(view, activity.getString(bodyID), Overlay.Style.RECTANGLE, clickable, Gravity.CENTER);
    }

    private void addTutorial(View view, String body, Overlay.Style style, boolean clickable, int gravity) {
        if (view == null) {
            return;
        }
        tourGuides.add(ChainTourGuide.init(activity)
                .with(TourGuide.Technique.CLICK)
                .setToolTip(new ToolTip()
                        .setDescription(body)
                        .setGravity(gravity)
                        .setBackgroundColor(Color.parseColor("#AAae6c37"))
                        .setShadow(true)
                        .setEnterAnimation(enterAnimation))
                .setOverlay(new Overlay()
                        .disableClick(true)
                        .disableClickThroughHole(!clickable)
                        .setStyle(style))
                .playLater(view));
    }

    public void start() {
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuides.toArray(new ChainTourGuide[tourGuides.size()]))
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.OVERLAY)
                .build();

        chainTourGuide = ChainTourGuide
                .init(activity)
                .playInSequence(sequence);
    }

    public void next() {
        chainTourGuide.next();
    }
}