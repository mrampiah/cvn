package view;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.MainApp;
import model.Patient;

public class AdminDashController {

	@FXML
	private TabPane tabPane;

	@FXML
	private Tab scheduleTab = new Tab();

	@FXML
	private Tab searchTab = new Tab();

	@FXML
	private Tab addPatientTab = new Tab();
	@FXML
	private Tab editPatientTab = new Tab();
	@FXML
	private ImageView scheduleImage = new ImageView();

	@FXML
	private ImageView searchImage = new ImageView();

	@FXML
	private AnchorPane adminDash;

	// Reference to the main application.
	private MainApp mainApp;

	private AnchorPane editPatient;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public AdminDashController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
				if (t1 == addPatientTab)
					loadAddPatientTab();
			}
		});
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// get the list of profiles from another class
		// personTable.setItems(mainApp.getPersonData());
	}

	/**
	 * Fills all text fields to show details about the person. If the specified
	 * person is null, all text fields are cleared.
	 * 
	 * @param person
	 *            the person or null
	 */
	// private void showProfileDetails(Profile profile) {
	// fills the profile space in the admin dash
	/*
	 * if (profile != null) { // Fill the labels with info from the person
	 * object. firstNameLabel.setText(person.getFirstName());
	 * lastNameLabel.setText(person.getLastName());
	 * streetLabel.setText(person.getStreet());
	 * postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
	 * cityLabel.setText(person.getCity());
	 * birthdayLabel.setText(DateUtil.format(person.getBirthday()));
	 * 
	 * } else { // Profile is null, remove all the text.
	 * firstNameLabel.setText(""); lastNameLabel.setText("");
	 * streetLabel.setText(""); postalCodeLabel.setText("");
	 * cityLabel.setText(""); birthdayLabel.setText(""); } }
	 */

	/**
	 * Loads and sets content of the schedule tab.
	 */
	@FXML
	private void loadScheduleTab() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("../view/MySchedule.fxml"));
			AnchorPane mySchedule = (AnchorPane) loader.load();

			scheduleTab.setContent(mySchedule);

			MyScheduleController controller = loader.getController();
			controller.setMainApp(mainApp);

		} catch (IOException e) {
			MainApp.printError(e);
		}

	}

	/**
	 * Loads and sets the contents of the search tab.
	 */
	private void loadSearchTab() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("../view/GlobalSearch.fxml"));
			AnchorPane globalSearch = (AnchorPane) loader.load();

			SearchTabController controller = loader.getController();
			controller.setMain(mainApp);
			searchTab.setContent(globalSearch);
		} catch (IOException e) {
			MainApp.printError(e);
		}
	}

	/**
	 * Loads and sets content of the add patient tab.
	 */
	@FXML
	private void loadAddPatientTab() {
		try {
			addPatientTab.setContent(addPatient);

			EditPatientController controller = loader.getController();
			controller.setMainApp(mainApp);

			tabPane.getSelectionModel().select(addPatientTab);
		} catch (IOException e) {
			MainApp.printError(e);
		}

	}

	/**
	 * Loads and sets content of the edit patient tab.
	 */

	public void loadEditPatientTab(Patient patient) {
		try {
			editPatientTab.setContent(editPatient);

			controller.loadFields();

			tabPane.getSelectionModel().select(editPatientTab);

		} catch (IOException e) {
			MainApp.printError(e);
		}
	}

	private AnchorPane getEditPatient() {
		return editPatient;
	}

	private void loadEditPatient() {

		try {
			FXMLLoader editPatientLoader = new FXMLLoader();
			editPatientLoader.setLocation(MainApp.class.getResource("../view/EditPatient.fxml"));
			editPatient = (AnchorPane) editPatientLoader.load();
			EditPatientController controller = editPatientLoader.getController();
			controller.setMainApp(mainApp);
			controller.setPatient(new Patient());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Clicking search image opens the search tab.
	 */
	@FXML
	private void handleSearchImage() {
		loadSearchTab();
		tabPane.getSelectionModel().select(searchTab);
	}

	/**
	 * Clicking schedule image opens the schedule tab.
	 */
	@FXML
	private void handleScheduleImage() {

		tabPane.getSelectionModel().select(scheduleTab);
	}

}
