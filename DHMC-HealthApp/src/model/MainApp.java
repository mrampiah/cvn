package model;

import java.io.IOException;

import controller.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import view.AdminDashController;
import view.LoginController;

public class MainApp extends Application {
	private DatabaseHandler dbHandler;
	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		dbHandler = new DatabaseHandler();
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("DHMC - Health App 3.0");

		initRootLayout();

		showLogin();
//		showAdminDash();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public DatabaseHandler getDatabseHandler(){
		return dbHandler;
	}

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("../view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);

			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showLogin() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("../view/Login.fxml"));
			AnchorPane login = (AnchorPane) loader.load();
			
			primaryStage.setScene(new Scene(login));

			LoginController controller = loader.getController();
			controller.setMainApp(this);

//			primaryStage.setWidth(400);
//			primaryStage.setHeight(300);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showAdminDash() {
        try {
        	System.out.println("Login success, loading Admin Dash");
            // Load admin overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("../view/AdminDash.fxml"));
            AnchorPane adminDash = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            primaryStage.setScene(new Scene(adminDash));

            // Give the controller access to the main.
            AdminDashController controller = loader.getController();
            controller.setMain(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}