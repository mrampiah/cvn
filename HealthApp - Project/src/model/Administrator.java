package model;

import java.time.LocalDate;

public class Administrator extends AbsUser {

    public Administrator() {

    }

    public Administrator(String userId, String firstName, String lastName, String username, String password,
                         LocalDate birthday, String room, String picture) {
        super(userId, firstName, lastName, username, password, birthday, room, picture);
    }
    public Administrator(String userId, String firstName, String lastName, String username, String password,
                         LocalDate birthday, String room, String picture, Contact contactInfo) {
        super(userId, firstName, lastName, username, password, birthday, room, picture, contactInfo);
    }

    public static String getUserType() {
        return "ADMIN";
    }

    @Override
    public AbsUser fromXMLString() {
        return null;
    }

    @Override
    public AbsUser fromCSVString() {
        return null;
    }

    @Override
    public AbsUser fromTSVString() {
        return null;
    }

    @Override
    public AbsUser fromSVString(String delimiter) {
        return null;
    }

    public static String getRole(){
        return "ADMIN";
    }
}
