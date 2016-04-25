package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Administrator;
import model.MainApp;
import model.MedicalStaff;
import model.Patient;
import utils.ObjectNotFoundException;

import javax.imageio.ImageIO;

public class MiniPatientProfileController extends AbsController {
	
	private Patient patient;

	// Integer will be replaced with Profile model
	@FXML
	private TableView<MedicalStaff> assignedStaffTable;
	@FXML
	private TableColumn<MedicalStaff, String> positionColumn;
	@FXML
	private TableColumn<MedicalStaff, String> firstNameColumn;
	@FXML
	private TableColumn<MedicalStaff, String> lastNameColumn;
	@FXML
	private Button viewPatientProfileButton = new Button();
	@FXML
	private Button viewMedicalStaffProfileButton = new Button();
	@FXML
	private Button editProfileButton = new Button();
	@FXML
	private Button removeProfileButton = new Button();
	@FXML
	private ImageView profilePic;
	@FXML
	private Label nameLabel;
	@FXML
	private Label idLabel;
	@FXML
	private Label phoneLabel;
	@FXML
	private Label emailLabel;
	@FXML
	private Label assignedStaffLabel;
	

	private int userId;
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
	}
	
	@Override
	public FXMLLoader getLoader(){
		loader.setLocation(MainApp.class.getResource("../view/MiniPatientProfileView.fxml"));
		return loader;
	}
	
	public MiniPatientProfileController() {
	}

	/**
	 * Method that displays med staff mini profile
	 * @param
	 */
	private void showMedStaffDetails(MedicalStaff staff) {	
	}
	
	public void setPatient(Patient patient){
		this.patient = patient;
		loadPatientDetails();
	}
	
	public void loadPatientDetails(){
//		profilePic.setImage(patient.getPicture());
		nameLabel.setText(patient.getFirstName() + " " + patient.getLastName());
		idLabel.setText(String.valueOf(patient.getUserId()));

		try {
			phoneLabel.setText(patient.getContactInfo().getPrimaryPhone().getValue());
			emailLabel.setText(patient.getContactInfo().getPrimaryEmail().getValue());
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}

	}
	

	/**
	 * Called when the user clicks edit profile button.
	 */
	@FXML
	private void handleEditProfile() {
		AbsDashController controller = MainApp.getDashController() ;
		((AdminDashController) controller).loadEditPatientTab(patient);

	}
	
	/**
	 *
	 * 
	 * This function will open a patient to view their information
	 * 
	 */
	@FXML
	public void viewPatientProfile() {
		//Will open a new view to look at a given patient
		
	        Parent root;
	        try {
	            root = FXMLLoader.load(getClass().getClassLoader().getResource("../view/PatientProfile.fxml"));
	            Stage stage = new Stage();
	            stage.setTitle("My New Stage Title");
	            stage.setScene(new Scene(root, 450, 450));
	            stage.show();

	            //hide this current window (if this is whant you want
	            //((Node)(event.getSource())).getScene().getWindow().hide();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	   
	}
	
	/**
	 *
	 * 
	 * This function will open a patient to view their information
	 * 
	 */
	@FXML
	public void viewMedicalStaffProfile() {
		//Will open a new view to look at a given patient
	}
	
	/**
	 * TODO removePatient
	 * 
	 * This function will remove a patient from the database
	 * 
	 */
	@FXML
	public void removePatient() {
		//Database will be called

	}
	
}