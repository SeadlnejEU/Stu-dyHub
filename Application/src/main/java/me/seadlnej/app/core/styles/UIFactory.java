package me.seadlnej.app.core.styles;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import me.seadlnej.app.Main;
import me.seadlnej.app.core.styles.theme.Colors;
import me.seadlnej.app.core.styles.theme.ThemeManager;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.managers.Account;
import me.seadlnej.app.utilities.JSON;
import me.seadlnej.app.utilities.WsClient;

import java.util.*;
import java.util.function.Consumer;

public class UIFactory {

    // Creates universal app button
    public static Button create(String display) {

        Button button = new Button(display);

        barStyle(button);

        return button;

    }

    public static void barStyle(Button button) {

        button.setFont(Font.font("Ariel", 20));

        button.setPrefSize(60,50);
        button.setTextFill(Paint.valueOf(Colors.Header()));

        ThemeManager.onChange(() -> button.setTextFill(Paint.valueOf(Colors.Header())));

        button.setStyle(
                "-fx-background-color: " + "none" + ";" +
                        "-fx-background-radius: 0;"
        );

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ff2cdf, #0014ff);"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: none;"));

    }

    // Title Bar button styles (Func, Profile...)
    public static Button barFuncBtn(String display) {

        Button button = new Button(display);
        button.setPrefSize(40, 40);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        button.setStyle("-fx-background-color: " + "none" + ";"+
                "-fx-text-fill: " + Colors.Header() + ";" +
                "-fx-background-radius: 0;"
        );

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: " + Colors.LinearGradient() + ";"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: none;"));

        ThemeManager.onChange(() -> button.setStyle(button.getStyle() +
                "-fx-text-fill: " + Colors.Header() + ";"));

        return button;
    }




    public static Button barStatusBtn(String display) {

        Button button = new Button(display);
        button.setPrefSize(40, 40);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        button.setStyle("-fx-background-color: " + "none" + ";"+
                "-fx-text-fill: " + Colors.Header() + ";" +
                "-fx-background-radius: 0;"
        );

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: " + Colors.LinearGradient() + ";"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: none;"));

        ThemeManager.onChange(() -> button.setStyle(button.getStyle() +
                "-fx-text-fill: " + Colors.Header() + ";"));

        return button;
    }













    public static Button authBtn(String display) {

        // Creates new button
        Button button = new Button(display);
        button.prefWidth(120);

        button.setStyle("-fx-background-color: " + Colors.Primary() + ";" +
                "-fx-text-fill: " + Colors.Header() + "; -fx-background-radius: 15; " +
                "-fx-pref-height: 40px; -fx-padding: 5 25 5 25;");
        button.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        button.setOnMouseEntered(e -> button.setStyle(
                button.getStyle() + "-fx-background-color: " + Colors.GradientPrimary() + ";"));
        button.setOnMouseExited(e -> button.setStyle(
                button.getStyle() + "-fx-background-color: " + Colors.Primary() + ";"));

        return button;
    }

    public static Button authBtn(Region parent, String display) {

        // Creates new button
        Button button = new Button(display);

        button.prefWidthProperty().bind(parent.widthProperty().multiply(0.8));

        button.setStyle("-fx-background-color: " + Colors.Primary() + ";" +
                "-fx-text-fill: " + Colors.Header() + "; -fx-background-radius: 15; " +
                "-fx-pref-height: 40px; -fx-padding: 5 25 5 25;");
        button.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        button.setOnMouseEntered(e -> button.setStyle(
                button.getStyle() + "-fx-background-color: " + Colors.GradientPrimary() + ";"));
        button.setOnMouseExited(e -> button.setStyle(
                button.getStyle() + "-fx-background-color: " + Colors.Primary() + ";"));

        return button;
    }

    public static Button authSwitch(String display) {

        // Creates new button
        Button button = new Button(display);

        button.setStyle("-fx-background-color: none;" +
                "-fx-text-fill: " + Colors.Header() + ";");
        button.setFont(Font.font("Inter", 14));

        button.setOnMouseEntered(e -> button.setStyle(
                button.getStyle() + "-fx-text-fill: " + Colors.GradientPrimary() + ";" +
                        "-fx-font-weight: bold;"));
        button.setOnMouseExited(e -> button.setStyle(
                button.getStyle() + "-fx-text-fill: " + Colors.Header() + ";" +
                        "-fx-font-weight: normal;"));

        return button;
    }

    public static HBox authFieldWithIcon(TextField field, String display, String iconPath) {

        // Default styling of box
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 5, 0));
        box.setStyle("-fx-border-width: 0 0 2 0; -fx-background-color: transparent;" +
                "-fx-border-color: transparent transparent " + Colors.Description() + " transparent;"
        );


        // Trying to load image
        Image image = null;
        if (iconPath != null) {
            try {
                image = new Image(UIFactory.class.getResourceAsStream(iconPath));
                if (image.isError()) image = null;
            } catch (Exception ignored) {}
        }

        ImageView icon = null;
        if (image != null) {
            icon = new ImageView(image);
            icon.setFitWidth(20);
            icon.setFitHeight(20);
            icon.setPreserveRatio(true);
        } else {
            box.setSpacing(0);
        }

        // Designing text field
        field.setPrefHeight(35);
        field.setPromptText(display);
        field.setFont(Font.font("Inter", 14));

        // padding závisí od toho, či existuje ikona
        int leftPadding = (icon != null ? 12 : 4);

        field.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-width: 0;" +
                        "-fx-text-fill: " + Colors.Header() + ";" +
                        "-fx-prompt-text-fill: " + Colors.Description() + ";" +
                        "-fx-padding: 4 12 4 " + leftPadding + ";"
        );

        HBox.setHgrow(field, Priority.ALWAYS);

        // ---- Kompozícia ----
        if (icon != null) {
            box.getChildren().addAll(icon, field);
        } else {
            box.getChildren().add(field);
        }

        return box;
    }


    public TextField authField(String display) {

        TextField field = new TextField();

        field.setPromptText(display);
        field.setFont(Font.font("Inter", 16));
        field.setPrefHeight(40);


        field.setStyle("-fx-background-color: none; -fx-border-width: 0 0 3 0;" +
                "-fx-text-fill: " + Colors.Header() + "; " +
                "-fx-prompt-text-fill: " + Colors.Description() + "; " +
                "-fx-border-color: transparent transparent " + Colors.Description() + " transparent;" +
                "-fx-padding: 4 12"
        );

        return field;
    }

    public PasswordField authPass(String display) {

        PasswordField field = new PasswordField();

        field.setPromptText(display);
        field.setFont(Font.font("Inter", 16));
        field.setPrefHeight(40);


        field.setStyle("-fx-background-color: none; -fx-border-width: 0 0 3 0;" +
                "-fx-text-fill: " + Colors.Description() + "; " +
                "-fx-prompt-text-fill: " + Colors.Header() + "; " +
                "-fx-border-color: transparent transparent " + Colors.Primary() + " transparent;" +
                "-fx-padding: 4 12"
        );

        return field;
    }



    public Button profileBtn(Stage region, String diplay) {

        Image img = new Image("");

        Circle circle = new Circle(25);
        circle.setFill(new ImagePattern(img));

        Button button = new Button(diplay);
        button.setShape(circle);

        button.setMinSize(50, 50);
        button.setPrefSize(50, 50);
        button.setMaxSize(50, 50);

        Popup popup = new Popup();

        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 8; -fx-background-radius: 8;");
        box.setAlignment(Pos.CENTER);

        box.getChildren().addAll(
                new Button("Nastaviť profil"),
                new Button("Odhlásiť sa"),
                new Button("Zavrieť")
        );

        popup.getContent().add(box);

        // 5) Kliknutie na tlačidlo → otvorí popup
        button.setOnAction(e -> {
            if (!popup.isShowing()) {
                popup.show(region);
            } else {
                popup.hide();
            }
        });

        return button;
    }












    // Configuration section bosex, buttons, field etc:
        //Dropdown button for selection of options
    public static VBox dropdownBtn(String initial, HashMap<String, String> options, Consumer<String> onSelect) {

        VBox container = new VBox(5);
        container.setPadding(new Insets(5));

        Button mainBtn = new Button(initial);
        mainBtn.setPrefWidth(100);
        mainBtn.setAlignment(Pos.CENTER_LEFT);
        mainBtn.setPadding(new Insets(5, 10, 5, 10));
        mainBtn.setFont(Font.font("Inter", FontWeight.BOLD, 14));

        VBox optionsBox = new VBox(2);
        optionsBox.setVisible(false);
        optionsBox.setManaged(false);

        for (Map.Entry<String, String> entry : options.entrySet()) {
            Button optionBtn = new Button(entry.getValue());
            optionBtn.setPrefWidth(Double.MAX_VALUE);
            optionBtn.setAlignment(Pos.CENTER_LEFT);
            optionBtn.setPadding(new Insets(5, 10, 5, 10));
            optionBtn.setFont(Font.font("Inter", 13));

            optionBtn.setOnAction(e -> {
                mainBtn.setText(entry.getValue());
                onSelect.accept(entry.getKey());
                optionsBox.setVisible(false);
                optionsBox.setManaged(false);
            });

            optionsBox.getChildren().add(optionBtn);

        }

        mainBtn.setOnAction(e -> {
            boolean showing = optionsBox.isVisible();
            optionsBox.setVisible(!showing);
            optionsBox.setManaged(!showing);
        });

        container.getChildren().addAll(mainBtn, optionsBox);
        return container;
    }

        // Account main header box, for image and details
    public static VBox detail(String header, String field) {

        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(header);
        label.setTextFill(Paint.valueOf(Colors.Header()));
        label.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 12));

        TextField textField = new TextField(field);
        textField.setPrefWidth(240);
        textField.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: transparent transparent " + Colors.Description() + " transparent;" +
                "-fx-border-width: 0 0 2 0; -fx-text-fill: " + Colors.Description() + ";");

        ThemeManager.onChange(() -> {
            label.setTextFill(Paint.valueOf(Colors.Header()));
            textField.setStyle(textField.getStyle() + "-fx-border-color: transparent transparent " +
                    Colors.Description() + " transparent;" + "-fx-text-fill: " + Colors.Description() + ";");
        });

        content.getChildren().addAll(label, textField);

        return content;
    }















    // --------------------------------------------------
    // | Authentification scene styling                 |
    // --------------------------------------------------

    // | Auth Title Bar | Function button style
    public static Button authBarFunc(String text) {

        Button button = new Button(text);
        button.setMinSize(35, 35);
        button.setPrefSize(35, 35);
        button.setMaxSize(35, 35);

        button.setTextFill(Paint.valueOf("#ffffff")); // Text color
        button.setFont(Font.font("Inter", FontWeight.BOLD, 14)); // Font style

        // Removing default style
        button.setStyle("-fx-background-color: transparent; -fx-background-border: none; -fx-padding: 0;");

        // On hover effect
        button.setOnMouseEntered(e -> button.setTextFill(Paint.valueOf("#CFFAFE")));
        button.setOnMouseExited(e -> button.setTextFill(Paint.valueOf("#ffffff")));

        return button;
    }

    // | Auth Title Bar | Close function button
    public static Button authBarClose(String text) {

        Button button = new Button(text);
        button.setMinSize(35, 35);
        button.setPrefSize(35, 35);
        button.setMaxSize(35, 35);

        button.setTextFill(Paint.valueOf("#d9d9d9")); // Text color
        button.setFont(Font.font("Inter", FontWeight.BOLD, 14)); // Font style

        // Removing default style
        button.setStyle("-fx-background-color: transparent; -fx-background-border: none; -fx-padding: 0;");

        // On hover effect
        button.setOnMouseEntered(e -> button.setTextFill(Paint.valueOf("#b54747")));
        button.setOnMouseExited(e -> button.setTextFill(Paint.valueOf("#d9d9d9")));

        return button;
    }

    // | Auth Accounts Panel | Accounts button style
    public static HBox authAccoButn(Account account) {

        // Default styling of box
        HBox box = new HBox(5);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setOnMouseEntered(e -> box.setStyle("-fx-background-radius: 4;" +
                "-fx-background-color: " + Colors.Primary() + ";"));
        box.setOnMouseExited(e -> box.setStyle("-fx-background-radius: 4;" +
                "-fx-background-color: transparent;"));

        // Profile picture box
        Circle avatar = new Circle(20);

        if (account.getImage() != null && !account.getImage().isError()) {
            avatar.setFill(new ImagePattern(account.getImage()));
        } else {
            // Fallback color
            avatar.setFill(Color.web("#444"));
        }

        // Account details box
        VBox details = new VBox();
        details.setSpacing(3);
        HBox.setHgrow(details, Priority.ALWAYS);

        // Full name, Top box
        Text name = new Text(account.getFirstname() + " " + account.getLastname());
        name.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        name.setFill(Paint.valueOf(Colors.Header())); // Color and Theme
        ThemeManager.onChange(() -> name.setFill(Paint.valueOf(Colors.Header())));

        // Bottom line, username and default state
        HBox bottom = new HBox();

        // Username text
        Text username = new Text("@" + account.getUsername());
        username.setFont(Font.font("Inter", 12));
        username.setFill(Paint.valueOf(Colors.Description())); // Color and Theme
        ThemeManager.onChange(() -> username.setFill(Paint.valueOf(Colors.Description())));

        // Spacer between username and default state
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label defaultTag = new Label("Default");
        defaultTag.setFont(Font.font("Inter", 10));
        defaultTag.setTextFill(Paint.valueOf(Colors.Description())); // Color and Theme
        ThemeManager.onChange(() -> defaultTag.setTextFill(Paint.valueOf(Colors.Description())));
        defaultTag.setVisible(account.isDefault()); // Visibility
        defaultTag.setManaged(account.isDefault()); // If it has effect on parent

        // Adding everything into boxes
        bottom.getChildren().addAll(username, spacer, defaultTag);
        details.getChildren().addAll(name, bottom);
        box.getChildren().addAll(avatar, details);

        // Returning
        return box;
    }

    public static void updateAuthAccoButn(HBox box, Account account) {

        // 0 - avatar (Circle)
        Circle avatar = (Circle) box.getChildren().get(0);
        if (account.getImage() != null && !account.getImage().isError()) {
            avatar.setFill(new ImagePattern(account.getImage()));
        }

        // 1 - details VBox
        VBox details = (VBox) box.getChildren().get(1);

        // details.getChildren():
        // 0 = name
        // 1 = bottom HBox

        Text name = (Text) details.getChildren().get(0);
        name.setText(account.getFirstname() + " " + account.getLastname());

        HBox bottom = (HBox) details.getChildren().get(1);

        // bottom.getChildren():
        // 0 = username
        // 1 = spacer
        // 2 = default label

        Text username = (Text) bottom.getChildren().get(0);
        username.setText("@" + account.getUsername());

        Label defaultTag = (Label) bottom.getChildren().get(2);
        defaultTag.setVisible(account.isDefault());
        defaultTag.setManaged(account.isDefault());
    }

    // | Auth Accounts Panel | Accounts list bottom button to switch add account
    public static Button authSwitchForms(String text) {

        // Default styling of button
        Button button = new Button(text);
        button.setTextFill(Paint.valueOf(Colors.Description())); // Text color
        button.setFont(Font.font("Inter",12)); // Font style

        // Removing default style
        button.setStyle("-fx-background-color: transparent; -fx-background-border: none; -fx-padding: 0;");

        // On hover effect


        // Returning
        return button;
    }

    // | Auth Accounts Panel | Back button to return accounts list
    public static Button authBackList(String text) {

        // Default styling of button
        Button button = new Button(text);
        button.setTextFill(Paint.valueOf(Colors.Description())); // Text color
        button.setFont(Font.font("Inter",28)); // Font style

        // Removing default style
        button.setStyle("-fx-background-color: transparent; -fx-background-border: none; -fx-padding: 0;");

        // On hover effect


        // Returning
        return button;
    }

    public static CheckBox authCheckbox(String text) {

        CheckBox box = new CheckBox(text);

        box.setFont(Font.font("Inter", 12));
        box.setTextFill(Paint.valueOf(Colors.Description()));
        box.setFocusTraversable(false);

        box.setStyle("-fx-cursor: hand;" + "-fx-background-color: transparent;" +
                "-fx-padding: 2 0 2 0;"
        );


        return box;
    }























        public static Button sendRequ(String title, String color) {

            Button button = new Button(title);

            button.setStyle("-fx-background-color: transparent; -fx-background-radius: none;" +
                    "-fx-border-radius: none;");
            button.setTextFill(Paint.valueOf(color));
            button.setFont(Font.font("Inter", 12));

            return button;
        }

















    // --------------------------------------------------
    // | Communication scene styling                    |
    // --------------------------------------------------

    // | Chats section | header profile button
    public static HBox profStat(String status, String color) {

        // Main box
        HBox box = new HBox(10);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(Double.MAX_VALUE); box.setPrefHeight(30);
        box.setStyle("-fx-background-radius: 5;");

        Circle circle = new Circle(6, Color.valueOf(color));
        circle.setTranslateY(-1);

        VBox labels = new VBox(0);
        Label name = new Label(status);
        name.setTextFill(Paint.valueOf(Colors.Header()));
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        labels.getChildren().addAll(name);
        box.getChildren().addAll(circle, labels);

        box.setOnMouseEntered(e -> box.setStyle(box.getStyle() + "-fx-background-color: " + Colors.Primary() + ";"));
        box.setOnMouseExited(e -> box.setStyle(box.getStyle() + "-fx-background-color: transparent;"));

        return box;
    }

    // | Chats section | Content switch buttons
    public static Button commChatSwit(Region parent, String display) {

        // Button object
        Button button = new Button(display);

        // Size styling
        button.setPrefHeight(18);
        button.setMinWidth(60);
        button.setAlignment(Pos.CENTER);
        HBox.setMargin(button, new Insets(0, 10, 0, 10));
        button.setFont(Font.font("Inter", FontWeight.BOLD, 12));

        // Runnable style
        Runnable applyStyle = () -> {
            String bg = "none";
            String text = Colors.Header();
            button.setStyle("-fx-background-color: " + bg + "; " + "-fx-text-fill: " + text + "; ");
        };
        applyStyle.run();

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + Colors.Primary() + "; -fx-text-fill: " + Colors.Description() + ";"));
        button.setOnMouseExited(e -> applyStyle.run());

        // Theme change
        ThemeManager.onChange(applyStyle);

        return button;
    }

    public static Node circleAvatar(String imagePath, double size) {

        Circle circle = new Circle(size / 2);

        try {
            if (imagePath != null) {
                Image img = new Image(Objects.requireNonNull(UIFactory.class.getResourceAsStream("/images/" + imagePath)),
                        size, size, false, true);

                if (!img.isError()) {
                    circle.setFill(new ImagePattern(img));
                    return circle;
                }
            }
        } catch (Exception ignored) {}

        // Ak obrázok chýba → transparentný kruh s borderom
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.GRAY);  // jemná linka, ak nechceš → odstráň
        circle.setStrokeWidth(1);

        return circle;
    }

    // | Message manager | Manager header buttons
    public static Button meMaDel() {

        Button button = new Button("X");
        button.setPrefSize(36, 36);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        button.setStyle("-fx-background-color: transparent;");
        button.setTextFill(Paint.valueOf(Colors.Header()));
        ThemeManager.onChange(() -> button.setTextFill(Paint.valueOf(Colors.Header())));

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: " + Colors.DnD() + ";"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: transparent;"));

        return button;
    }

    public static Button meMaUs() {

        Button button = new Button("+");
        button.setPrefSize(36, 36);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        button.setStyle("-fx-background-color: transparent;");
        button.setTextFill(Paint.valueOf(Colors.Header()));
        ThemeManager.onChange(() -> button.setTextFill(Paint.valueOf(Colors.Header())));

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: " + Colors.Primary() + ";"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle() +
                "-fx-background-color: transparent;"));


        return button;
    }

    public static Button toolBarBtn(String imagePath) {

        ImageView imageView = new ImageView();
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setPreserveRatio(true);

        Circle circle = new Circle(15, 15, 15);
        imageView.setClip(circle);

        Button btn = new Button();
        btn.setShape(circle);
        btn.setGraphic(imageView);
        btn.setPrefSize(30, 30);
        btn.setMinSize(30, 30);
        btn.setMaxSize(30, 30);
        HBox.setMargin(btn, new Insets(5));

        btn.setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: transparent;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
            """);

        // obrázok
        Image image = new Image(Objects.requireNonNull(
                UIFactory.class.getResourceAsStream(imagePath)
        ));
        imageView.setImage(image);

        return btn;
    }


    // --------------------------------------------------
    // | Configuration scene styling                    |
    // --------------------------------------------------

    public static Button settingsButton() {

        ImageView imageView = new ImageView();
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setPreserveRatio(true);

        Circle circle = new Circle(15, 15, 15);
        imageView.setClip(circle);

        Button button = new Button();
        button.setShape(circle);
        button.setGraphic(imageView);
        button.setPrefSize(30, 30);
        button.setMinSize(30, 30);
        button.setMaxSize(30, 30);
        HBox.setMargin(button, new Insets(5));

        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;" +
                "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        );

        Runnable updateImage = () -> {
            String theme = Colors.getTheme().toString();
            String base = "/images/";

            String normal = base + "gear_" + theme.toLowerCase() + ".png";
            String path = base + "gear_" + theme.toLowerCase() + "_hover.png";

            Image normalImg = new Image(UIFactory.class.getResourceAsStream(normal));
            Image hoverImg = new Image(UIFactory.class.getResourceAsStream(path));

            imageView.setImage(normalImg);

            button.setOnMouseEntered(ev -> imageView.setImage(hoverImg));
            button.setOnMouseExited(ev -> imageView.setImage(normalImg));
        };

        // Spusti hneď pri vytvorení tlačidla
        updateImage.run();

        // Dynamická zmena theme
        ThemeManager.onChange(updateImage);

        return button;
    }

    // Configuration category button style
    public static Button confCategory(Region parent, String display) {

        // Button object
        Button button = new Button(display);

        // Size styling
        button.setPrefHeight(30);
        HBox.setMargin(button, new Insets(5));
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefWidth(Double.MAX_VALUE);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 16));

        // Runnable style
        Runnable applyStyle = () -> {
            String bg = "none";
            String text = Colors.Header();
            button.setStyle("-fx-background-color: " + bg + "; " + "-fx-text-fill: " + text + "; ");
        };
        applyStyle.run();

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + Colors.Secondary() + "; -fx-text-fill: " + Colors.Description() + ";"));
        button.setOnMouseExited(e -> applyStyle.run());

        // Theme change
        ThemeManager.onChange(applyStyle);

        return button;
    }

    // Log out button
    public static Button confLogOut(Region parent, String display) {

        // Button object
        Button button = new Button(display);

        // Size styling
        button.setPrefHeight(40);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefWidth(Double.MAX_VALUE);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 16));

        // Runnable style
        Runnable applyStyle = () -> {
            String bg = "none";
            String text = Colors.Header();
            button.setStyle("-fx-background-color: " + bg + "; " + "-fx-text-fill: #ff0000; ");
        };
        applyStyle.run();

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-background-color: " + Colors.Secondary() + ";"));
        button.setOnMouseExited(e -> applyStyle.run());

        return button;
    }

    // | Contacts section | Send contact request
    public static Button contSendRequest() {
        Button btn = new Button("Save changes");
        btn.setPrefSize(160, 40);
        btn.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-border-color: transparent; -fx-background-radius: 0;" +
                "-fx-text-fill: " + Colors.Header() + ";");

        ThemeManager.onChange(() -> btn.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-text-fill: " + Colors.Header() + ";"));

        return btn;
    }

    public static HBox contRequestTile(Image image, String firstname, String lastname,
                                         String username, String createdAt, Long requestId) {

        // Default box styling
        HBox box = new HBox(15);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: " + Colors.Primary() + "; -fx-background-radius: 10;");
        ThemeManager.onChange(() -> box.setStyle(box.getStyle() + "-fx-background-color: " + Colors.Primary() + ";"));

        // Profile picture
        Circle circle = new Circle(20);
        circle.setFill(new ImagePattern(image));

        // Details box
        VBox details = new VBox(2);

        // Name and username box
        HBox nameBox = new HBox(5);

        // Label for name
        Label name = new Label(firstname + " " + lastname + " (@" + username + ")");
        extracted(name);

        // Label for time of create
        Label time = new Label(createdAt);
        time.setStyle("-fx-text-fill: " + Colors.Description() + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        ThemeManager.onChange(() -> time.setStyle(time.getStyle() + "-fx-text-fill: " + Colors.Description() + ";"));

        nameBox.getChildren().addAll(name, time);

        // Accept and reject buttons
        HBox buttons = new HBox(10);

        Button acceptBtn = new Button("Accept");
        Button rejectBtn = new Button("Reject");

        contSmallAction(acceptBtn, "#00ff00");
        contSmallAction(rejectBtn, "#ff0000");

        acceptBtn.setOnAction(e -> contHandleAction(requestId, true, box));
        rejectBtn.setOnAction(e -> contHandleAction(requestId, false, box));

        buttons.getChildren().addAll(acceptBtn, rejectBtn);
        details.getChildren().addAll(nameBox, buttons);

        box.getChildren().addAll(circle, details);
        return box;
    }

    private static void extracted(Label name) {
        name.setStyle("-fx-text-fill: " + Colors.Header() + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        ThemeManager.onChange(() -> name.setStyle(name.getStyle() + "-fx-text-fill: " + Colors.Header() + ";"));
    }

    // | Contacts section | Small action button style (accept, reject)
    public static void contSmallAction(Button button, String color) {

        button.setMinWidth(60);
        button.setPrefHeight(20);
        button.setFont(Font.font("Inter", FontWeight.BOLD, 10));
        button.setStyle("-fx-background-color: " + Colors.Secondary() + "; -fx-text-fill: " + Colors.Header() +
                "; -fx-background-radius: 10;");
        ThemeManager.onChange(() -> button.setStyle(button.getStyle() +  "-fx-background-color: " + Colors.Secondary() + ";"));

        button.setOnMouseEntered(e ->
                button.setStyle(button.getStyle() + "-fx-text-fill: " + color + ";"));
        button.setOnMouseExited(e ->
                button.setStyle(button.getStyle() + "-fx-text-fill: " + Colors.Header() + ";"));
    }

    // | Contacts section | Sending request response
    private static void contHandleAction(Long requestId, boolean accept, HBox tile) {

        ApiHandler api = new ApiHandler(); // Api handler

        api.put("requestId", requestId);
        api.put("accepted", accept);
        JSON response = api.post("request/respond", SessionHandler.getInstance().getToken());

        if(response.isSuccess() && accept) {
            ((VBox) tile.getParent()).getChildren().remove(tile);
        }
    }



}