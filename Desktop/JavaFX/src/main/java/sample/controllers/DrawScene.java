package sample.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import sample.Main;
import sample.model.Lesson;
import sample.providers.StepImagesProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Max on 07.03.2015.
 */
public class DrawScene {
    public ColorPicker colorPicker;
    public Canvas canvas;
    public static Lesson currentLesson;
    public ImageView stepImage;
    public static int currentStep;
    public static final int STARTING_STEP = 0;
    public Button btnSavePicture;

    public void onBtnMainSceneAction(ActionEvent event) {
        ((Node)event.getSource()).getScene().setRoot(Main.mainRoot.getRoot());
    }

    public void onBtnCurrentLessonAction(ActionEvent event) {
        ((Node) event.getSource()).getScene().setRoot(Main.drawRoot.getRoot());
    }

    public void onCanvasMouseMoved(Event event) {

    }

    public void onCanvasMousePressed(Event event) {
        MouseEvent me = (MouseEvent)event;
        canvas.getGraphicsContext2D().setFill(colorPicker.getValue());
        canvas.getGraphicsContext2D().setStroke(colorPicker.getValue());
        canvas.getGraphicsContext2D().setLineWidth(2.0);
        canvas.getGraphicsContext2D().beginPath();
        canvas.getGraphicsContext2D().moveTo(me.getX(), me.getY());
        canvas.getGraphicsContext2D().stroke();
    }

    public void onCanvasMouseReleased(Event event) {
        canvas.getGraphicsContext2D().closePath();
    }

    public void onCanvasMouseDragged(Event event) {
        MouseEvent me = (MouseEvent)event;
        canvas.getGraphicsContext2D().lineTo(me.getX(), me.getY());
        canvas.getGraphicsContext2D().stroke();
    }

    public void onBtnPrevStepAction(ActionEvent event) {
        prevStep();
    }

    private void prevStep() {
        if (currentStep < STARTING_STEP || currentLesson == null)
            return;

        if (currentStep > STARTING_STEP)
            currentStep--;

        updateCurrentStep();
    }

    private void nextStep() {
        if (currentLesson == null)
            return;

        if (currentStep < currentLesson.getSteps()) {
            currentStep++;
        }

        updateCurrentStep();
    }

    private void updateCurrentStep() {
        Image img = StepImagesProvider.getImage(
                currentLesson.getLocalId() + "_" + currentLesson.getId(), currentStep);

        if (img == null)
            return;

        stepImage.setImage(img);
    }

    public void onBtnNextStepAction(ActionEvent event) {
        nextStep();
    }

    public void onBtnSavePictureAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = fileChooser.showSaveDialog(((Node)event.getSource()).getScene().getWindow());

        if (file == null)
            return;

        WritableImage image = canvas.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onLoadLesson(final Lesson lesson) {
        currentLesson = lesson;
        currentStep = STARTING_STEP;
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        updateCurrentStep();
    }
}
