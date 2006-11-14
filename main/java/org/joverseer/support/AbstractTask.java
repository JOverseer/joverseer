package org.joverseer.support;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 14 Íןו 2006
 * Time: 5:07:42 לל
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTask {
    int progress;
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public abstract void run();
}
