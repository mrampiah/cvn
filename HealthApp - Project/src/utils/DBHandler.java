package utils;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import model.*;
import org.apache.derby.jdbc.EmbeddedDataSource;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrampiah on 4/24/16.
 */
public class DBHandler {

    private static PreparedStatement ps;
    private static ResultSet rs;
    private static Connection connection;
    private static DatabaseMetaData metaData;
    private static DBHandler uniqueInstance;
    private boolean success;// monitors if sql interactions result in errors

    private DBHandler() {
        connect();
    }

    private enum UserType{PATIENT, ADMIN, MEDICAL_STAFF, RELATION};

    /**
     * Get the unique database instance
     *
     * @return DatabaseHandler object
     */
    public static DBHandler getUniqueInstance() {
        if (connection != null)
            return uniqueInstance;
        else {
            uniqueInstance = new DBHandler();
            return uniqueInstance;
        }
    }

    public static LocalDate timestampToLocalDate(Timestamp timestamp) {
        return timestamp.toLocalDateTime().toLocalDate();
    }

    public static Timestamp localDateToTimestamp(LocalDate localDate) {
        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public void initDB() {
        if (connect()) {
            createTables();
            populateDatabase(10);
        } else {
            System.out.println("Couldn't connect");
        }
    }

    public void populateDatabase(int number) {
        for (int i = 1; i < number; i++) {
            Patient p = RandomGenerator.getRandomPatient();
            p = RandomGenerator.fillPatient(p);
            insertPatientAlgorithm(p);
        }

        Administrator admin = RandomGenerator.getRandomAdmin();
        System.out.printf("admin username:%s, password:%s\n", admin.getUsername(), admin.getPassword());
        insertAdminAlgorithm(admin);
    }

    public boolean connect() {
        boolean connected = false;
        try {
            // connect method - embedded driver
            EmbeddedDataSource ds = new EmbeddedDataSource();
            ds.setDatabaseName("HealthApp");
            ds.setCreateDatabase("create");

            connection = ds.getConnection();
            if (connection != null) {
                // System.out.println("Connected to database");
                metaData = connection.getMetaData();
                connected = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        }
        return connected;
    }

    public boolean createUserTable() {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement("CREATE TABLE user_account("
                    + "user_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), f" +
                    "irstname VARCHAR(20), lastname VARCHAR(20)," +
                    "username VARCHAR(20), password VARCHAR(50), birthday TIMESTAMP, room VARCHAR(10)," +
                    " picture VARCHAR(200), user_type VARCHAR(20), role VARCHAR(10)," +
                    "isFamily BOOLEAN, isCaregiver BOOLEAN, relationship VARCHAR(20), PRIMARY KEY(user_id) )");
            ps.execute();
            success = true;
            ps.close();
        } catch (SQLException e) {
            MainApp.printError(e);
        }
        return success;
    }

    public boolean createContactTable() {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connect()) {
            try {
                ps = connection.prepareStatement("CREATE TABLE contact( " +
                        "contact_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "user_id BIGINT, value VARCHAR(500), type VARCHAR(20), contact_type VARCHAR(20), " +
                        "FOREIGN KEY(user_id) REFERENCES user_account(user_id), PRIMARY KEY (contact_id) )");
                ps.execute();
                success = true;
                ps.close();
            } catch (SQLException e) {
                MainApp.printError(e);
            }
        }
        return success;
    }

    public boolean createPetTable() {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connect()) {
            try {
                ps = connection.prepareStatement("CREATE TABLE pet("
                        + "pet_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                        " name VARCHAR(10), species VARCHAR(20), allergy_friendly BOOLEAN, user_id BIGINT,"
                        + "FOREIGN KEY(user_id) REFERENCES user_account(user_id))");
                ps.execute();
                success = true;
                ps.close();
            } catch (SQLException e) {
                MainApp.printError(e);
//
            }
        }
        return success;
    }

    public boolean createMealTable() {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connect()) {
            try {
                ps = connection.prepareStatement("CREATE TABLE meal("
                        + "meal_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                        " name VARCHAR(20), calories INT, notes VARCHAR(200), PRIMARY KEY(meal_id) )");
                ps.execute();
                success = true;
                ps.close();
            } catch (SQLException e) {
                MainApp.printError(e);
//
            }
        }
        return success;
    }

    public boolean createEatsTable() {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connect()) {
            try {
                ps = connection.prepareStatement("CREATE TABLE eats(user_id BIGINT, meal_id BIGINT, rating int," +
                        " FOREIGN KEY(user_id) REFERENCES user_account(user_id), " +
                        "FOREIGN KEY(meal_id) REFERENCES meal(meal_id))");
                ps.execute();
                success = true;
                ps.close();
            } catch (SQLException e) {
//
                MainApp.printError(e);
            }
        }
        return success;
    }

    public boolean createEventTable() {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connect()) {
            try {
                ps = connection.prepareStatement("CREATE TABLE event("
                        + "event_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                        " name VARCHAR(20), date TIMESTAMP , category VARCHAR(20), "
                        + "notes VARCHAR(200), PRIMARY KEY(event_id) )");
                ps.execute();
                success = true;
                ps.close();
            } catch (SQLException e) {
                MainApp.printError(e);

            }
        }
        return success;
    }

    public boolean createHealthInfoTable() {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connect()) {
            try {
                ps = connection.prepareStatement("CREATE TABLE health_info("
                        + "health_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                        " user_id BIGINT, date TIMESTAMP, name VARCHAR(20), value VARCHAR(10),"
                        + "FOREIGN KEY(user_id) REFERENCES user_account(user_id), Primary Key(health_id) )");
                ps.execute();
                success = true;
            } catch (SQLException e) {
                MainApp.printError(e);

            }
        }
        return success;
    }

    public boolean createStaffAssignmentTable() {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connect()) {
            try {
                ps = connection.prepareStatement("CREATE TABLE staff_assignment("
                        + "patient_id BIGINT, med_id BIGINT, " +
                        "FOREIGN KEY(patient_id) REFERENCES user_account (user_id),"
                        + "FOREIGN KEY(med_id) REFERENCES user_account(user_id))");
                ps.execute();
                success = true;
            } catch (SQLException e) {
                MainApp.printError(e);

            }
        }
        return success;
    }

    public boolean createRelatedTable() {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connect()) {
            try {
                ps = connection.prepareStatement("CREATE TABLE related("
                        + "patient_id BIGINT, relation_id BIGINT, " +
                        "FOREIGN KEY(patient_id) REFERENCES user_account (user_id),"
                        + "FOREIGN KEY(relation_id) REFERENCES user_account(user_id))");
                ps.execute();
                success = true;
            } catch (SQLException e) {
                MainApp.printError(e);

            }
        }
        return success;
    }

    public void createTables() {
        try {
            connect();
            // check if the tables exist in our schema, create if they dont
            rs = metaData.getTables(null, "APP", "USER_ACCOUNT", null);
            if (!rs.next())
                createUserTable();

            rs = metaData.getTables(null, "APP", "CONTACT", null);
            if (!rs.next())
                createContactTable();

            rs = metaData.getTables(null, "APP", "PET", null);
            if (!rs.next())
                createPetTable();

            rs = metaData.getTables(null, "APP", "MEAL", null);
            if (!rs.next())
                createMealTable();

            rs = metaData.getTables(null, "APP", "EATS", null);
            if (!rs.next())
                createEatsTable();

            rs = metaData.getTables(null, "APP", "EVENT", null);
            if (!rs.next())
                createEventTable();

            rs = metaData.getTables(null, "APP", "HEALTH_INFO", null);
            if (!rs.next())
                createHealthInfoTable();

            rs = metaData.getTables(null, "APP", "RELATED", null);
            if (!rs.next())
                createRelatedTable();

            rs = metaData.getTables(null, "APP", "STAFF_ASSIGNMENT", null);
            if (!rs.next())
                createStaffAssignmentTable();

            if (ps != null) {
                ps.close();
                System.out.println("Tables created");
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        }
    }

    public boolean insertPatientAlgorithm(Patient p) {
        success = true;
        insertPatient(p);
        for (Pet pet : p.getPets())
            insertPet(pet, p.getUserIdValue());
        for (Meal meal : p.getMeals()) {
            insertMeal(meal);
            insertEats(meal, p.getUserIdValue());
        }
        for (AbsRelation relation : p.getRelations()) {
            insertRelation(relation);
            insertRelated(p.getUserIdValue(), relation.getUserIdValue());
        }
        for (MedicalStaff medStaff : p.getAssignedStaff()) {
            insertMedicalStaff(medStaff);
            insertStaffAssignment(p.getUserIdValue(), medStaff.getUserIdValue());
        }
        for (HealthAttribute<?> attribute : p.getHealthProfile().getHealthInfo())
            insertHealthInfo(attribute, p.getUserIdValue());

        for (ContactElement e : p.getContactInfo().getAllContactElements())
            insertContact(e, p.getUserIdValue());

        return success;
    }

    public boolean insertAdminAlgorithm(Administrator admin) {
        success = true;
        insertAdmin(admin);
        for (ContactElement e : admin.getContactInfo().getAllContactElements())
            insertContact(e, admin.getUserIdValue());

        return success;
    }

    public boolean insertPatient(Patient p) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO user_account (firstname, lastname," +
                        "username, password, birthday, room, picture, user_type) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)", new String[]{"USER_ID"});

                int i = 1;
                ps.setString(i++, p.getFirstName());
                ps.setString(i++, p.getLastName());
                ps.setString(i++, p.getUsername());
                ps.setString(i++, p.getPassword());
                ps.setTimestamp(i++, localDateToTimestamp(p.getBirthday()));
                ps.setString(i++, p.getRoom());
                ps.setString(i++, p.getPicture());
                ps.setString(i++, "PATIENT");
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                p.setUserIdValue(rs.getLong(1));
                success = true;
                System.out.printf("patient username: %s, password: %s user type: %s\n", p.getUsername(), p.getPassword(), UserType.PATIENT);
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean insertMedicalStaff(MedicalStaff med) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO user_account (firstname, lastname," +
                        "username, password, birthday, room, picture, user_type) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)", new String[]{"USER_ID"});

                int i = 1;
                ps.setString(i++, med.getFirstName());
                ps.setString(i++, med.getLastName());
                ps.setString(i++, med.getUsername());
                ps.setString(i++, med.getPassword());
                ps.setTimestamp(i++, localDateToTimestamp(med.getBirthday()));
                ps.setString(i++, med.getRoom());
                ps.setString(i++, med.getPicture());
                ps.setString(i++, UserType.MEDICAL_STAFF.name());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                med.setUserIdValue(rs.getLong(1));
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean insertAdmin(Administrator admin) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO user_account (firstname, lastname," +
                        "username, password, birthday, room, picture, user_type) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)", new String[]{"USER_ID"});

                int i = 1;
                ps.setString(i++, admin.getFirstName());
                ps.setString(i++, admin.getLastName());
                ps.setString(i++, admin.getUsername());
                ps.setString(i++, admin.getPassword());
                ps.setTimestamp(i++, localDateToTimestamp(admin.getBirthday()));
                ps.setString(i++, admin.getRoom());
                ps.setString(i++, admin.getPicture());
                ps.setString(i++, UserType.ADMIN.name());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                admin.setUserIdValue(rs.getLong(1));
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean insertRelation(AbsRelation rel) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO user_account (firstname, lastname," +
                        "username, password, birthday, room, picture, user_type) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)", new String[]{"USER_ID"});

                int i = 1;
                ps.setString(i++, rel.getFirstName());
                ps.setString(i++, rel.getLastName());
                ps.setString(i++, rel.getUsername());
                ps.setString(i++, rel.getPassword());
                ps.setTimestamp(i++, localDateToTimestamp(rel.getBirthday()));
                ps.setString(i++, rel.getRoom());
                ps.setString(i++, rel.getPicture());
                ps.setString(i++, UserType.RELATION.name());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                rel.setUserIdValue(rs.getLong(1));
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean insertContact(ContactElement c, long userIdValue) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO contact (user_id, value, type, contact_type) "
                        + "VALUES(?, ?, ?, ?)", new String[]{"CONTACT_ID"});

                ps.setLong(1, userIdValue);
                ps.setString(2, c.getValue());
                ps.setString(3, c.getType());
                ps.setString(4, c.getContactType());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                c.setElementId(rs.getLong(1));
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean insertPet(Pet pet, long userIdValue) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO pet (user_id, name, species, allergy_friendly) "
                        + "VALUES( ?, ?, ?, ?)", new String[]{"PET_ID"});
                int i = 1;
                ps.setLong(i++, userIdValue);
                ps.setString(i++, pet.getName());
                ps.setString(i++, pet.getSpecies());
                ps.setBoolean(i++, pet.isAllergyFriendly());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                pet.setPetId(rs.getLong(1));
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }

    }

    public boolean insertMeal(Meal meal) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO meal (name, calories, notes) "
                        + "VALUES( ?, ?, ?)", new String[]{"MEAL_ID"});
                int i = 1;
                ps.setString(i++, meal.getFood());
                ps.setInt(i++, meal.getCalories());
                ps.setString(i++, meal.getNotes());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                meal.setMealId(rs.getLong(1));
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean insertEats(Meal meal, long userIdValue) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO eats (user_id, meal_id, rating) "
                        + "VALUES(?, ?, ?)");
                int i = 1;
                ps.setLong(i++, userIdValue);
                ps.setLong(i++, meal.getMealId());
                ps.setInt(i++, meal.getRating());
                ps.executeUpdate();
                ps.close();
                success = true;
            }
        } catch (SQLException e) {

            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean insertRelated(long patientId, long relationId) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO related (patient_id, relation_id) "
                        + "VALUES(?, ?)");
                int i = 1;
                ps.setLong(i++, patientId);
                ps.setLong(i++, relationId);
                ps.executeUpdate();
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean insertStaffAssignment(long patientId, long medId) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO staff_assignment (patient_id, med_id) "
                        + "VALUES(?, ?)");
                int i = 1;
                ps.setLong(i++, patientId);
                ps.setLong(i++, medId);
                ps.executeUpdate();
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean insertHealthInfo(HealthAttribute<?> healthInfo, long userIdValue) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("INSERT INTO health_info (user_id, date, name, value) "
                        + "VALUES( ?, ?, ?, ?)", new String[]{"HEALTH_ID"});
                int i = 1;
                ps.setLong(i++, userIdValue);
                ps.setTimestamp(i++, localDateToTimestamp(healthInfo.getDate()));
                ps.setString(i++, healthInfo.getName());
                ps.setString(i++, healthInfo.getStringValue());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();
                healthInfo.setHealthAttributeId(rs.getLong(1));
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public AbsUser getFilledUserByUsername(String username) {
        if (connect()) {
            AbsUser user = getAbsUserByUsername(username);
            List<ContactElement> info = getContactInfo(user.getUserIdValue());
            Contact contactInfo = new Contact(info);
            user.setContactInfo(contactInfo);
            return user;
        }
        return null;
    }

    public AbsUser getFilledUserById(long userId) {
        if (connect()) {
            AbsUser user = getAbsUserById(userId);
            List<ContactElement> info = getContactInfo(user.getUserIdValue());
            if (info.size() > 0) {
                Contact contactInfo = new Contact(info);
                user.setContactInfo(contactInfo);
            }
            return user;
        }
        return null;
    }

    public AbsUser getAbsUserByUsername(String username) {
        AbsUser user = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("SELECT * FROM user_account WHERE username = ? ");
                ps.setString(1, username);
                rs = ps.executeQuery();
                System.out.println("nullies");

                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    System.out.printf("usertype: %s admin usertype: %s patient usertype: %s\n", userType, Administrator.getUserType(),
                            UserType.PATIENT.name());
                    if (userType.equals(UserType.PATIENT.name())) {
                        user = getPatientByUsername(username);
                    } else if (userType.equals(UserType.ADMIN.name())) ;
                    user = getAdministratorByUsername(username);
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return user;
        }
    }

    public AbsUser getAbsUserById(long userId) {
        AbsUser user = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("SELECT * FROM user_account WHERE user_id = ? ");
                ps.setLong(1, userId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    if (userType.equals(UserType.PATIENT.name()))
                        user = getPatientById(userId);
                    else if (userType.equals(UserType.ADMIN.name()))
                        user = getAdministratorById(userId);
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return user;
        }
    }

    public Administrator getAdministratorByUsername(String username) {
        Administrator admin = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT * FROM user_account WHERE username = ? AND user_type = ?");
                ps.setString(1, username);
                ps.setString(2, UserType.ADMIN.name());
                rs = ps.executeQuery();
                if (rs.next()) {
                    admin = new Administrator(rs.getLong("user_id"), rs.getString("firstname"),
                            rs.getString("lastname"), rs.getString("username"), rs.getString("password"),
                            timestampToLocalDate(rs.getTimestamp("birthday")), rs.getString("room"),
                            rs.getString("picture"));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return admin;
        }
    }

    public Administrator getAdministratorById(long userIdValue) {
        Administrator admin = null;
        try {
            if (connect()) {

                ps = connection.prepareStatement(" SELECT * FROM user_account WHERE user_id = ? AND user_type = ?");
                ps.setLong(1, userIdValue);
                ps.setString(2, UserType.ADMIN.name());
                rs = ps.executeQuery();
                if (rs.next()) {
                    admin = new Administrator(rs.getLong("user_id"), rs.getString("firstname"),
                            rs.getString("lastname"), rs.getString("username"), rs.getString("password"),
                            timestampToLocalDate(rs.getTimestamp("birthday")), rs.getString("room"),
                            rs.getString("picture"));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return admin;
        }
    }

    public Patient getPatientByUsername(String username) {
        Patient p = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT user_id, firstname, lastname, username, password, " +
                        "birthday, room, picture FROM  user_account WHERE username = ? AND user_type = ?");
                ps.setString(1, username);
                ps.setString(2, UserType.PATIENT.name());
                p = getPatient(ps.executeQuery());
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return p;
        }
    }

    public Patient getPatientById(long userIdValue) {
        Patient p = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT user_id, firstname, lastname, username, password, " +
                        "birthday, room, picture FROM  user_account WHERE user_id = ? AND user_type = ?");
                ps.setLong(1, userIdValue);
                ps.setString(2, UserType.PATIENT.name());
                p = getPatient(ps.executeQuery());
            }
            return getPatient(ps.executeQuery());
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return p;
        }
    }

    public Patient getFilledPatientByUsername(String username) {
        Patient p = (Patient) getAbsUserByUsername(username);
        p.getHealthProfile().setHealthInfo(getHealthInfo(p.getUserIdValue()));

        return p;
    }

    public Patient getFilledPatientById(long userIdValue) {
        Patient p = (Patient) getAbsUserById(userIdValue);
        p.setPets(getPatientPets(userIdValue));
        p.setMeals(getPatientMeals(userIdValue));
        p.setContactInfo(new Contact(getContactInfo(userIdValue)));
        p.getHealthProfile().setHealthInfo(getHealthInfo(p.getUserIdValue()));
        return p;
    }

    public MedicalStaff getMedicalStaffByUsername(String username) {
        MedicalStaff med = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT user_id, firstname, lastname, username, password, " +
                        "birthday, room, picture FROM  user_account WHERE username = ? AND user_type = ?");
                ps.setString(1, username);
                ps.setString(2, UserType.MEDICAL_STAFF.name());
                rs = ps.executeQuery();
                if (rs.next()) {
                    med = new MedicalStaff(rs.getLong("user_id"), rs.getString("firstname"),
                            rs.getString("lastname"), rs.getString("username"), rs.getString("password"),
                            timestampToLocalDate(rs.getTimestamp("birthday")), rs.getString("room"),
                            rs.getString("picture"));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return med;
        }
    }

    public MedicalStaff getMedicalStaffById(long userIdValue) {
        MedicalStaff med = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT user_id, firstname, lastname, username, password, " +
                        "birthday, room, picture FROM  user_account WHERE user_id = ? AND user_type = ?");
                ps.setLong(1, userIdValue);
                ps.setString(2, UserType.MEDICAL_STAFF.name());
                rs = ps.executeQuery();
                if (rs.next()) {
                    med = new MedicalStaff(rs.getLong("user_id"), rs.getString("firstname"),
                            rs.getString("lastname"), rs.getString("username"), rs.getString("password"),
                            timestampToLocalDate(rs.getTimestamp("birthday")), rs.getString("room"),
                            rs.getString("picture"));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return med;
        }
    }

    public List<MedicalStaff> getPatientMedicalStaff(long userIdValue) {
        List<MedicalStaff> staff = new LinkedList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT med_id FROM staff_assignment WHERE patient_id = ?");
                ps.setLong(1, userIdValue);
                while (rs.next()) {
                    staff.add(getMedicalStaffById(rs.getLong("med_id")));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return staff;
        }
    }

    public AbsRelation getRelationByUsername(String username) {
        AbsRelation relation = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT user_id, firstname, lastname, username, password, " +
                        "birthday, room, picture FROM user_account WHERE username = ? AND user_type = ?");
                ps.setString(1, username);
                ps.setString(2, UserType.RELATION.name());
                relation = getAbsRelation(ps.executeQuery());
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return relation;
        }
    }

    public AbsRelation getRelationById(long userIdValue) {
        AbsRelation relation = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT user_id, firstname, lastname, username, password, " +
                        "birthday, room, picture FROM  user_account WHERE user_id = ?");
                ps.setLong(1, userIdValue);
                relation = getAbsRelation(ps.executeQuery());
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return relation;
        }
    }

    public AbsRelation getAbsRelation(ResultSet rs) {
        try {
            if (rs.next()) {
                AbsRelation relation;
                if (rs.getString("role") == Caregiver.getRole()) {
                    relation = new Caregiver(rs.getLong("user_id"), rs.getString("firstname"),
                            rs.getString("lastname"), rs.getString("username"), rs.getString("password"),
                            timestampToLocalDate(rs.getTimestamp("birthday")), rs.getString("room"),
                            rs.getString("picture"), rs.getString("relationship"), rs.getBoolean("isFamily"));
                } else {
                    relation = new Family(rs.getLong("user_id"), rs.getString("firstname"),
                            rs.getString("lastname"), rs.getString("username"), rs.getString("password"),
                            timestampToLocalDate(rs.getTimestamp("birthday")), rs.getString("room"),
                            rs.getString("picture"), rs.getString("relationship"), rs.getBoolean("isCaregiver"));

                }
                connection.close();
                return relation;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        }
        return null;
    }

    public List<AbsRelation> getPatientRelations(long userIdValue) {
        List<AbsRelation> relations = new LinkedList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT relation_id FROM related WHERE patient_id = ?");
                ps.setLong(1, userIdValue);
                while (rs.next()) {
                    relations.add(getRelationById(rs.getLong("relation_id")));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return relations;
        }
    }

    public Patient getPatient(ResultSet rs) {
        try {
            if (rs.next()) {
                Patient patient = new Patient(rs.getLong("user_id"), rs.getString("firstname"),
                        rs.getString("lastname"), rs.getString("username"), rs.getString("password"),
                        timestampToLocalDate(rs.getTimestamp("birthday")), rs.getString("room"),
                        rs.getString("picture"));
                return patient;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        }
        return null;
    }

    public List<Patient> getAllPatients() {
        LinkedList<Patient> patientList = new LinkedList<Patient>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT * FROM user_account WHERE user_type = ?");
                ps.setString(1, UserType.PATIENT.name());
                rs = ps.executeQuery();
                while (rs.next()) {
                    Patient patient = getPatient(rs);
                    patientList.add(patient);
                }
            }
        } catch (SQLException | NullPointerException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return patientList;
        }
    }

    public List<Patient> getAllFilledPatients() {
        LinkedList<Patient> patientList = new LinkedList<Patient>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT * FROM user_account WHERE user_type = ?");
                ps.setString(1, UserType.PATIENT.name());
                rs = ps.executeQuery();
                while (rs.next()) {
                    Patient patient = getFilledPatientById(rs.getLong("user_id"));
                    patientList.add(patient);
                }
            }
        } catch (SQLException | NullPointerException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return patientList;
        }
    }

    public List<ContactElement> getContactInfo(long userIdValue) {
        List<ContactElement> elements = new LinkedList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT * FROM contact WHERE user_id = ?");
                ps.setLong(1, userIdValue);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ContactElement element = new Phone(rs.getLong("contact_id"),
                            rs.getString("value"), rs.getString("type"), rs.getString("contact_type"));
                    elements.add(element);
                }
            }
        } catch (SQLException | NullPointerException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return elements;
        }
    }

    public Meal getMeal(long mealId) {
        Meal meal = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT name, calories, notes FROM  meal WHERE meal_id = ? ");
                ps.setLong(1, mealId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    meal = new Meal(mealId, rs.getString("name"), rs.getInt("calories"), rs.getString("notes"));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return meal;
        }
    }

    public List<Meal> getPatientMeals(long userIdValue) {
        List<Meal> meals = new LinkedList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("SELECT * FROM eats JOIN user_account ON eats.user_id = user_account.user_id WHERE eats.user_id = ? ");
                ps.setLong(1, userIdValue);
                rs = ps.executeQuery();
                while (rs.next()) {
                    meals.add(new Meal(rs.getLong("meal_id"), rs.getString("name"), rs.getInt("calories"),
                            rs.getInt("rating"), rs.getString("notes")));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return meals;
        }
    }

    public Pet getPet(long petId) {
        Pet p = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT name, species, allergey_friendly FROM  pet WHERE pet_id = ? ");
                ps.setLong(1, petId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    p = new Pet(petId, rs.getString("name"), rs.getString("species"), rs.getBoolean("allergy_friendly"));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return p;
        }
    }

    public List<Pet> getPatientPets(long userIdValue) {
        List<Pet> pets = new LinkedList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("SELECT * FROM pet WHERE user_id = ? ");
                ps.setLong(1, userIdValue);
                rs = ps.executeQuery();
                while (rs.next()) {
                    pets.add(new Pet(rs.getLong("pet_id"), rs.getString("name"), rs.getString("species"),
                            rs.getBoolean("allergy_friendly")));
                    System.out.println("pet name " + pets.get(0).getName() );
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return pets;
        }
    }

    public List<HealthAttribute<?>> getHealthInfo(long userIdValue) {
        List<HealthAttribute<?>> healthInfo = new LinkedList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("SELECT health_id, name, date, value FROM health_info WHERE user_id = ?");
                ps.setLong(1, userIdValue);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (RandomGenerator.isInteger(rs.getString("value")))
                        healthInfo.add(new HealthAttribute<Integer>(rs.getLong("health_id"),
                                timestampToLocalDate(rs.getTimestamp("date")),
                                rs.getString("name"), Integer.parseInt(rs.getString("value"))));
                    else if (RandomGenerator.isDouble(rs.getString("value")))
                        healthInfo.add(new HealthAttribute<Double>(rs.getLong("health_id"),
                                timestampToLocalDate(rs.getTimestamp("date")),
                                rs.getString("name"), Double.parseDouble(rs.getString("value"))));
                    else
                        healthInfo.add(new HealthAttribute<>(rs.getLong("health_id"),
                                timestampToLocalDate(rs.getTimestamp("date")),
                                rs.getString("name"), rs.getString("value")));
                }
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return healthInfo;
        }
    }

    public boolean updatePatientAlgorithm(Patient p) {
        success = true;
        updatePatient(p);
        for (Pet pet : p.getPets())
            updatePet(pet);

        for (Meal meal : p.getMeals()) {
            updateMeal(meal);
            updateMealRating(meal);
        }

        for (AbsRelation relation : p.getRelations())
            updateRelation(relation);

        for (MedicalStaff medStaff : p.getAssignedStaff())
            updateMedicalStaff(medStaff);

        for (HealthAttribute<?> attribute : p.getHealthProfile().getHealthInfo())
            insertHealthInfo(attribute, p.getUserIdValue());

        for (ContactElement e : p.getContactInfo().getAllContactElements())
            updateContact(e);

        return success;
    }

    public boolean updateRelation(AbsRelation relation) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("UPDATE user_account SET firstname = ?, lastname = ?," +
                        "username = ?, password = ?, birthday = ?, room = ?, picture = ? WHERE " +
                        "user_type = ? AND user_id = ?) ");

                setUpdateParameters(relation, UserType.RELATION.name(), ps);
                ps.execute();
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean updatePatient(Patient p) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("UPDATE user_account SET firstname = ?, lastname = ?," +
                        "username = ?, password = ?, birthday = ?, room = ?, picture = ? WHERE " +
                        "user_type = ? AND user_id = ? ");
                setUpdateParameters(p, UserType.PATIENT.name(), ps);
                ps.executeUpdate();
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean updateMedicalStaff(MedicalStaff medStaff) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("UPDATE user_account SET firstname = ?, lastname = ?," +
                        "username = ?, password = ?, birthday = ?, room = ?, picture = ? WHERE " +
                        "user_type = ? AND user_id = ? ");
                setUpdateParameters(medStaff, UserType.MEDICAL_STAFF.name(), ps);
                ps.execute();
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean updatePet(Pet p) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("UPDATE pet SET name = ?, species = ?," +
                        "allergy_friendly = ? WHERE pet_id = ? ");
                int i = 1;
                ps.setString(i++, p.getName());
                ps.setString(i++, p.getSpecies());
                ps.setBoolean(i++, p.isAllergyFriendly());
                ps.setLong(i++, p.getPetId());
                ps.execute();
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean updateMeal(Meal m) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("UPDATE meal SET name = ?, calories = ?," +
                        "notes = ? WHERE meal_id = ? ");
                int i = 1;
                ps.setString(i++, m.getFood());
                ps.setInt(i++, m.getCalories());
                ps.setString(i++, m.getNotes());
                ps.setLong(i++, m.getMealId());
                ps.execute();
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean updateMealRating(Meal m) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("UPDATE eats SET rating = ?," +
                        " WHERE meal_id = ? ");
                int i = 1;
                ps.setInt(i++, m.getRating());
                ps.setLong(i++, m.getMealId());
                ps.execute();
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public boolean updateContact(ContactElement e) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("UPDATE contact SET value = ?, type = ? " +
                        " WHERE contact_id = ? ");
                int i = 1;
                ps.setString(i++, e.getValue());
                ps.setString(i++, e.getType());
                ps.setLong(i++, e.getElementId());
                ps.execute();
                success = true;
            }
        } catch (SQLException ex) {
            MainApp.printError(ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception ex) {
                MainApp.printError(ex);
            }
            return success;
        }
    }

    public boolean updateHealthInfo(HealthAttribute<?> healthAttribute) {
        success = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                ps = connection.prepareStatement("UPDATE health_info SET date = ?, name = ?, value = ?, " +
                        "WHERE health_id = ? ");
                int i = 1;
                ps.setTimestamp(i++, localDateToTimestamp(healthAttribute.getDate()));
                ps.setString(i++, healthAttribute.getName());
                ps.setString(i++, healthAttribute.getStringValue());
                ps.setLong(i++, healthAttribute.getHealthAttributeId());
                ps.execute();
                success = true;
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                MainApp.printError(e);
            }
            return success;
        }
    }

    public void setUpdateParameters(AbsUser user, String userType, PreparedStatement ps) {
        int i = 1;
        try {
            ps.setString(i++, user.getFirstName());
            ps.setString(i++, user.getLastName());
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getPassword());
            ps.setTimestamp(i++, localDateToTimestamp(user.getBirthday()));
            ps.setString(i++, user.getRoom());
            ps.setString(i++, user.getPicture());
            ps.setString(i++, userType);
            ps.setLong(i++, user.getUserIdValue());
        } catch (SQLException e) {
            MainApp.printError(e);
        }
    }

    public NamedParameterStatement setBasicParameters(NamedParameterStatement nps, AbsUser user) {
        //            nps.setString(1, user.getUserId());
//            nps.setString(2, user.getFirstName());
//            nps.setString(3, user.getLastName());
//            nps.setString(4, user.getUsername());
//            nps.setString(5, user.getPassword());
//            nps.setString(6, user.getRoom());
//            nps.setString(7, user.getPicture());
//            nps.setTimestamp(8, localDateToTimestamp(user.getBirthday()));
//            nps.setString(9, user.getUserType());

        return nps;
    }

    public boolean isUniqueId(long userIdValue) {
        try {
            if (connect()) {
                ps = connection.prepareStatement(" SELECT * FROM  user_account WHERE user_id = ? ");
                ps.setLong(1, userIdValue);
                return rs.next();
            }
        } catch (SQLException e) {
            MainApp.printError(e);
        }
        return false;
    }
}
