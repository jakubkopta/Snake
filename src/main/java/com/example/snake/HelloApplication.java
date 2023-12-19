package com.example.snake;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class HelloApplication extends Application {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public static final int BLOCK_SIZE = 20;
    public static final int MAX_WIDTH = 30 * BLOCK_SIZE;
    public static final int MAX_HEIGHT = 30 * BLOCK_SIZE;
    public static final int ROWS = MAX_WIDTH/BLOCK_SIZE;
    public static final int COLUMNS = ROWS;

    private Direction direction = Direction.RIGHT;
    private int score = 0;
    private int highScore = 0;
    private Label scoreLabel = new Label();
    private boolean moved = false;
    private boolean running = false;
    private final Timeline timeline = new Timeline();
    private ObservableList<Node> snake;
    private Stage stage;
    private GraphicsContext gc;


    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(MAX_WIDTH, MAX_HEIGHT);
        Canvas canvas = new Canvas(MAX_WIDTH, MAX_HEIGHT);
        root.getChildren().addAll(canvas,scoreLabel);
        gc = canvas.getGraphicsContext2D();
        Group snakeBody = new Group();
        snake = snakeBody.getChildren();
        drawScore();

        Rectangle food = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
        food.setFill(Color.RED);
        food.setTranslateX((int) (Math.random() * (MAX_WIDTH - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);
        food.setTranslateY((int) (Math.random() * (MAX_HEIGHT - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);
        drawBackground(gc);

        KeyFrame frame = new KeyFrame(Duration.seconds(0.15), event -> {
           if (!running)
               return;
           boolean toRemove = snake.size() > 1;
           Node tail = toRemove ? snake.remove(snake.size() - 1) : snake.get(0);

           double tailX = tail.getTranslateX();
           double tailY = tail.getTranslateY();

           switch (direction) {
               case UP:
                       tail.setTranslateX(snake.get(0).getTranslateX());
                       tail.setTranslateY(snake.get(0).getTranslateY() - BLOCK_SIZE);
                       break;
               case DOWN:
                       tail.setTranslateX(snake.get(0).getTranslateX());
                       tail.setTranslateY(snake.get(0).getTranslateY() + BLOCK_SIZE);
                       break;
               case LEFT:
                       tail.setTranslateX(snake.get(0).getTranslateX() - BLOCK_SIZE);
                       tail.setTranslateY(snake.get(0).getTranslateY());
                       break;
               case RIGHT:
                       tail.setTranslateX(snake.get(0).getTranslateX() + BLOCK_SIZE);
                       tail.setTranslateY(snake.get(0).getTranslateY());
                       break;
           }

           moved = true;

           if (toRemove)
               snake.add(0, tail);

           for (Node rect : snake) {
               if (rect != tail && tail.getTranslateX() == rect.getTranslateX()
                                && tail.getTranslateY() == rect.getTranslateY()) {
                   restartGame();
                   break;
               }
           }

           if (tail.getTranslateX() < 0 || tail.getTranslateX() >= MAX_WIDTH
                || tail.getTranslateY() < 0 || tail.getTranslateY() >= MAX_HEIGHT) {
               restartGame();
           }

           if (tail.getTranslateX() == food.getTranslateX() && tail.getTranslateY() == food.getTranslateY()) {
               food.setTranslateX((int)(Math.random() * (MAX_WIDTH - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);
               food.setTranslateY((int)(Math.random() * (MAX_HEIGHT - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);

               Rectangle rect = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
               rect.setFill(Color.GREEN);
               rect.setTranslateX(tailX);
               rect.setTranslateY(tailY);

               snake.add(rect);
               score++;
               drawScore();
           }

        });

        timeline.getKeyFrames().add(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);

        root.getChildren().addAll(food, snakeBody);
        return root;
    }

    private void restartGame() {
        stopGame();

        Stage gameOverStage = new Stage();
        gameOverStage.setTitle("Game Over");

        Label gameOverLabel = new Label("GAME OVER!");
        if (score > highScore) {
            highScore = score;
        }
        Label finalScoreLabel = new Label("High score: " + highScore);
        gameOverLabel.setStyle("-fx-font-size: 24; -fx-text-fill: red;");
        finalScoreLabel.setStyle("-fx-font-size: 14; -fx-text-fill: black;");

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> {
            gameOverStage.close();
            score = 0;
            drawScore();
            startGame();
        });

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            gameOverStage.close();
            stage.close();
        });

        VBox gameOverLayout = new VBox(10);
        gameOverLayout.getChildren().addAll(gameOverLabel, finalScoreLabel, restartButton, exitButton);
        gameOverLayout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene gameOverScene = new Scene(gameOverLayout, 250, 150);
        gameOverStage.setScene(gameOverScene);

        gameOverStage.show();
    }
    private void stopGame() {
        running = false;
        timeline.stop();
        snake.clear();
    }

    private void startGame() {
        direction = Direction.RIGHT;
        Rectangle head = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
        head.setFill(Color.GREEN);
        snake.add(head);
        timeline.play();
        running = true;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed(event -> {
            if (moved) {
                switch (event.getCode()) {
                    case W:
                    case UP:
                        if (direction != Direction.DOWN)
                            direction = Direction.UP;
                        break;
                    case S:
                    case DOWN:
                        if (direction != Direction.UP)
                            direction = Direction.DOWN;
                        break;
                    case A:
                    case LEFT:
                        if (direction != Direction.RIGHT)
                            direction = Direction.LEFT;
                        break;
                    case D:
                    case RIGHT:
                        if (direction != Direction.LEFT)
                            direction = Direction.RIGHT;
                        break;
                }
            }

            moved = false;
        });

        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.show();
        startGame();
    }

    private void drawBackground(GraphicsContext gc) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i + j) % 2 == 0) {
                    gc.setFill(Color.web("AAD751"));
                } else {
                    gc.setFill(Color.web("A2D149"));
                }
                gc.fillRect(i * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            }
        }
    }

    private void drawScore() {
        scoreLabel.setStyle("-fx-font-size: 30");
        scoreLabel.setText("Score: " + score);
    }

    public static void main(String[] args) {
        launch();
    }
}