package gui;

import data.DataLoader;
import evaluation.Precision;
import evaluation.Recall;
import models.KNNClassifier;
import models.NaiveBayesClassifier;
import models.PerceptronClassifier;
import models.Instance;
import evaluation.Accuracy;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;

import java.io.File;
import java.util.List;

public class MainGUI extends Application {
    private KNNClassifier knn;
    private NaiveBayesClassifier nb;
    private PerceptronClassifier perceptron;
    private List<Instance<List<Double>, String>> data;
    private List<Instance<List<Double>, String>> trainingData;
    private List<Instance<List<Double>, String>> testData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ML Library");

        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(20));

        Label label = new Label("Choose a Classifier:");
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("KNN", "Naive Bayes", "Perceptron");
        choiceBox.setValue("KNN");

        Label hyperparameterLabel = new Label("Enter Hyperparameter (K for KNN, Learning Rate for Perceptron):");
        TextField hyperparameterField = new TextField();

        Button loadFileButton = new Button("Load Data File");
        loadFileButton.setOnAction(e -> loadData(primaryStage));

        Label splitLabel = new Label("Choose Train-Test Split (%):");
        Slider splitSlider = new Slider(50, 90, 80);
        splitSlider.setShowTickLabels(true);
        splitSlider.setShowTickMarks(true);

        Button trainButton = new Button("Train Classifier");
        trainButton.setOnAction(e -> trainModel(choiceBox.getValue(), hyperparameterField.getText(), (int) splitSlider.getValue()));

        Button testButton = new Button("Test Classifier");
        testButton.setOnAction(e -> testModel(choiceBox.getValue()));

        layout.getChildren().addAll(label, choiceBox, hyperparameterLabel, hyperparameterField, loadFileButton, splitLabel, splitSlider, trainButton, testButton);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadData(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Data File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                data = DataLoader.loadFromCSV(file.getAbsolutePath());
                showAlert("Data Loaded Successfully!");
            } catch (Exception e) {
                showAlert("Error Loading Data: " + e.getMessage());
            }
        }
    }

    private void trainModel(String classifier, String hyperparameter, int trainPercentage) {
        if (data == null) {
            showAlert("Please load data first!");
            return;
        }

        int splitIndex = (int) (data.size() * trainPercentage / 100.0);
        trainingData = data.subList(0, splitIndex);
        testData = data.subList(splitIndex, data.size());

        try {
            switch (classifier) {
                case "KNN":
                    int k = Integer.parseInt(hyperparameter);
                    knn = new KNNClassifier(k);
                    knn.train(trainingData);
                    break;
                case "Naive Bayes":
                    nb = new NaiveBayesClassifier();
                    nb.train(trainingData);
                    break;
                case "Perceptron":
                    double learningRate = Double.parseDouble(hyperparameter);
                    perceptron = new PerceptronClassifier(learningRate);
                    perceptron.train(trainingData);
                    break;
            }
            showAlert("Model Trained Successfully!");
        } catch (NumberFormatException e) {
            showAlert("Invalid Hyperparameter Value!");
        }
    }

    private void testModel(String classifier) {
        if (testData == null) {
            showAlert("Please train the model first!");
            return;
        }

        List<String> predictions;
        double accuracy;
        double precision;
        double recall;

        switch (classifier) {
            case "KNN":
                predictions = knn.test(testData);
                accuracy = new Accuracy<List<Double>, String>().evaluate(testData, predictions);
                precision = new Precision<List<Double>, String>().evaluate(testData, predictions);
                recall = new Recall<List<Double>, String>().evaluate(testData, predictions);
                break;
            case "Naive Bayes":
                predictions = nb.test(testData);
                accuracy = new Accuracy<List<Double>, String>().evaluate(testData, predictions);
                precision = new Precision<List<Double>, String>().evaluate(testData, predictions);
                recall = new Recall<List<Double>, String>().evaluate(testData, predictions);
                break;
            case "Perceptron":
                predictions = perceptron.test(testData);
                accuracy = new Accuracy<List<Double>, String>().evaluate(testData, predictions);
                precision = new Precision<List<Double>, String>().evaluate(testData, predictions);
                recall = new Recall<List<Double>, String>().evaluate(testData, predictions);
                break;
            default:
                showAlert("Invalid Classifier Selected!");
                return;
        }

        showAlert("Results:\nAccuracy: " + accuracy + "\nPrecision: " + precision + "\nRecall: " + recall);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}