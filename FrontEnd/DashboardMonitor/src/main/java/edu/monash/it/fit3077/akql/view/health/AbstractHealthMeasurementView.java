package edu.monash.it.fit3077.akql.view.health;

import edu.monash.it.fit3077.akql.model.health.AbstractHealthMeasurementModel;
import edu.monash.it.fit3077.akql.observer.Observer;
import edu.monash.it.fit3077.akql.view.JavaFXView;
import javafx.scene.Node;

/*
This abstract class holds the common code and workflow needed for measurement views (subclasses). It registers the view
as observers to the model and initializes the view itself.
 */
public abstract class AbstractHealthMeasurementView implements JavaFXView, Observer {
    protected AbstractHealthMeasurementModel model;
    Node rootNode;

    /**
     * Register as observer to the model and initialize the view.
     * @param hm: the health measurement model.
     */
    AbstractHealthMeasurementView(AbstractHealthMeasurementModel hm) {
        this.model = hm;
        this.model.attach(this);
        this.setUpView();
    }

    /**
     * Method to initialize the view.
     */
    protected abstract void setUpView();

    /**
     * Used so parent views can attach nodes of child views to itself.
     * @return JavaFX node
     */
    @Override
    public Node getRootNode() {
        return this.rootNode;
    }
}
