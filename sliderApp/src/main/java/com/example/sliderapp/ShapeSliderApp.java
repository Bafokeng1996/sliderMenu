package com.example.sliderapp;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ShapeSliderApp extends Application {

    private BorderPane root;
    private Pane canvas;
    private Shape[] shapes;
    private int currentIndex;
    private double mouseOffsetX;
    private double mouseOffsetY;
    private Label shapeInfoLabel; // To display shape information
    private ColorPicker colorPicker; // For selecting shape color

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        canvas = new Pane();
        canvas.setStyle("-fx-background-color: white;");
        root.setCenter(canvas);

        // Create shapes
        shapes = new Shape[3];
        shapes[0] = new Triangle(100, 100, 150);
        shapes[1] = new MyRectangle(100, 100, 100, 100);
        shapes[2] = new MyCircle(150, 150, 50);

        // Display initial shape
        currentIndex = 0;
        canvas.getChildren().add(shapes[currentIndex]);

        // Shape information label
        shapeInfoLabel = new Label();
        shapeInfoLabel.setPadding(new Insets(10));
        root.setTop(shapeInfoLabel);

        // Color picker
        colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setOnAction(e -> applyShapeColor(colorPicker.getValue()));

        // Left region (Shape Selection and Color Picker)
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        for (Shape shape : shapes) {
            Button shapeBtn = new Button(shape.getClass().getSimpleName());
            shapeBtn.setOnAction(e -> selectShape(shape));
            leftPane.getChildren().add(shapeBtn);
        }
        leftPane.getChildren().addAll(new Label("Choose Color:"), colorPicker);
        root.setLeft(leftPane);

        // Right region (Shape Details or Tools)
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        Label shapeDetailsLabel = new Label("Shape Details:");
        Text shapeDetailsText = new Text();

        // Example: Display area and perimeter of the selected shape
        shapeDetailsText.textProperty().bind(Bindings.createStringBinding(() -> {
            Shape selectedShape = shapes[currentIndex];
            if (selectedShape instanceof Triangle) {
                Triangle triangle = (Triangle) selectedShape;
                double area = calculateTriangleArea(triangle);
                double perimeter = calculateTrianglePerimeter(triangle);
                return String.format("Area: %.2f\nPerimeter: %.2f", area, perimeter);
            } else if (selectedShape instanceof MyRectangle) {
                MyRectangle rectangle = (MyRectangle) selectedShape;
                return String.format("Width: %.2f\nHeight: %.2f", rectangle.getRectWidth(), rectangle.getRectHeight());
            } else if (selectedShape instanceof MyCircle) {
                MyCircle circle = (MyCircle) selectedShape;
                return String.format("Radius: %.2f", circle.getRadius());
            }
            return "";
        }, shapes[currentIndex].boundsInParentProperty()));

        rightPane.getChildren().addAll(shapeDetailsLabel, shapeDetailsText);
        root.setRight(rightPane);

        // Add mouse event handlers for dragging shapes
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);

        // Previous and Next buttons
        Button previousBtn = new Button("Previous");
        previousBtn.setOnAction(e -> showPreviousShape());
        Button nextBtn = new Button("Next");
        nextBtn.setOnAction(e -> showNextShape());

        // Zoom buttons
        Button zoomInBtn = new Button("Zoom In");
        zoomInBtn.setOnAction(e -> zoom(1.1)); // Increase scale by 10%
        Button zoomOutBtn = new Button("Zoom Out");
        zoomOutBtn.setOnAction(e -> zoom(0.9)); // Decrease scale by 10%

        // Change background button
        Button changeBgBtn = new Button("Change Background");
        changeBgBtn.setOnAction(e -> changeBackground());

        // Layout buttons in a control pane
        HBox buttonPane = new HBox(10, previousBtn, nextBtn, zoomInBtn, zoomOutBtn, changeBgBtn);
        buttonPane.setPadding(new Insets(10));
        root.setBottom(buttonPane);

        // Set up the scene
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); // Apply external CSS
        primaryStage.setTitle("Shape Slider");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Display initial shape information
        updateShapeInfo();
    }

    private void handleMousePressed(MouseEvent event) {
        Shape currentShape = shapes[currentIndex];
        if (currentShape != null) {
            mouseOffsetX = event.getX() - currentShape.getLayoutX();
            mouseOffsetY = event.getY() - currentShape.getLayoutY();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        Shape currentShape = shapes[currentIndex];
        if (currentShape != null) {
            double newX = event.getX() - mouseOffsetX;
            double newY = event.getY() - mouseOffsetY;

            // Restrict dragging within canvas bounds
            if (newX >= 0 && newX <= canvas.getWidth() - currentShape.getBoundsInLocal().getWidth()) {
                currentShape.setLayoutX(newX);
            }

            if (newY >= 0 && newY <= canvas.getHeight() - currentShape.getBoundsInLocal().getHeight()) {
                currentShape.setLayoutY(newY);
            }
        }
    }

    private void showPreviousShape() {
        if (currentIndex > 0) {
            canvas.getChildren().remove(shapes[currentIndex]);
            currentIndex--;
            canvas.getChildren().add(shapes[currentIndex]);
            updateShapeInfo();
        }
    }

    private void showNextShape() {
        if (currentIndex < shapes.length - 1) {
            canvas.getChildren().remove(shapes[currentIndex]);
            currentIndex++;
            canvas.getChildren().add(shapes[currentIndex]);
            updateShapeInfo();
        }
    }

    private void zoom(double scaleFactor) {
        Shape currentShape = shapes[currentIndex];
        if (currentShape != null) {
            currentShape.setScaleX(currentShape.getScaleX() * scaleFactor);
            currentShape.setScaleY(currentShape.getScaleY() * scaleFactor);
        }
    }

    private void changeBackground() {
        Color newColor = Color.rgb(
                (int) (Math.random() * 256),
                (int) (Math.random() * 256),
                (int) (Math.random() * 256));
        canvas.setStyle("-fx-background-color: " + toHexString(newColor) + ";");
    }

    private void updateShapeInfo() {
        String shapeName = shapes[currentIndex].getClass().getSimpleName();
        shapeInfoLabel.setText("Selected Shape: " + shapeName);
    }

    private void selectShape(Shape selectedShape) {
        canvas.getChildren().remove(shapes[currentIndex]);
        currentIndex = indexOfShape(selectedShape);
        canvas.getChildren().add(shapes[currentIndex]);
        updateShapeInfo();
    }

    private int indexOfShape(Shape shape) {
        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i] == shape) {
                return i;
            }
        }
        return -1;
    }

    private void applyShapeColor(Color color) {
        Shape currentShape = shapes[currentIndex];
        if (currentShape != null) {
            currentShape.setFill(color);
        }
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    // Helper methods for calculating shape properties (e.g., area, perimeter)
    private double calculateTriangleArea(Triangle triangle) {
        return 0.5 * triangle.getBase() * triangle.getTriangleHeight();
    }

    private double calculateTrianglePerimeter(Triangle triangle) {
        double a = triangle.getSideA();
        double b = triangle.getSideB();
        double c = triangle.getSideC();
        return a + b + c;
    }

    public static void main(String[] args) {
        launch(args);
    }

    abstract class Shape extends Pane {
        public Shape(double x, double y) {
            setLayoutX(x);
            setLayoutY(y);
        }

        public abstract void setFill(Color color);
    }

    class Triangle extends Shape {
        private Polygon triangle;
        private double base;
        private double height;

        public Triangle(double x, double y, double size) {
            super(x, y);
            base = size;
            height = size;

            triangle = new Polygon();
            triangle.getPoints().addAll(
                    0.0, 0.0,
                    base, 0.0,
                    base / 2, height);
            triangle.setFill(Color.GREEN);

            getChildren().add(triangle);
        }

        public double getBase() {
            return base;
        }

        public double getTriangleHeight() {
            return height;
        }

        public double getSideA() {
            return base;
        }

        public double getSideB() {
            return height;
        }

        public double getSideC() {
            return Math.sqrt(base * base + height * height);
        }

        @Override
        public void setFill(Color color) {
            triangle.setFill(color);
        }
    }

    class MyRectangle extends Shape {
        private Rectangle rectangle;

        public MyRectangle(double x, double y, double width, double height) {
            super(x, y);

            rectangle = new Rectangle(width, height);
            rectangle.setFill(Color.BLUE);

            getChildren().add(rectangle);
        }

        @Override
        public void setFill(Color color) {
            rectangle.setFill(color);
        }

        // Use different method names to avoid conflicts with Region's final methods
        public double getRectWidth() {
            return rectangle.getWidth();
        }

        public double getRectHeight() {
            return rectangle.getHeight();
        }
    }

    class MyCircle extends Shape {
        private Circle circle;

        public MyCircle(double x, double y, double radius) {
            super(x, y);

            circle = new Circle(radius);
            circle.setFill(Color.RED);

            getChildren().add(circle);
        }

        @Override
        public void setFill(Color color) {
            circle.setFill(color);
        }

        public double getRadius() {
            return circle.getRadius();
        }
    }
}
