package edu.monash.it.fit3077.akql.view;

import javafx.scene.Node;

/*
This interface is mainly used to get the root node of a view so it can be attached to the hosted view.
 */
public interface JavaFXView {
    /**
     * Used so parent views can attach nodes of child views to itself.
     * @return JavaFX node
     */
    Node getRootNode();
}
