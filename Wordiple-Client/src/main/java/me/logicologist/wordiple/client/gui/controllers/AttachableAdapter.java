package me.logicologist.wordiple.client.gui.controllers;

import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

public abstract class AttachableAdapter implements Initializable {

    private Pane parent;
    private Pane attachment;

    public void setAttachment(Pane pane) {
        if (attachment != null) return;
        this.attachment = pane;
    }

    public void setParent(Pane pane) {
        if (parent != null) return;
        this.parent = pane;
    }

    public void attach() {
        if (parent == null || attachment == null) return;
        this.parent.getChildren().add(attachment);
    }

    public void detach() {
        if (parent == null || attachment == null) return;
        this.parent.getChildren().remove(attachment);
    }
}
