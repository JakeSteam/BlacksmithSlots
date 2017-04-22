package uk.co.jakelee.blacksmithslots.constructs;

public class DialogAction {
    private String text;
    private Runnable runnable;

    public DialogAction(String text, Runnable runnable) {
        this.text = text;
        this.runnable = runnable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
