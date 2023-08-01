package userQueries;

public class UserQuery {
    String user_CNIC,user_name,message_date,message_content;


    public UserQuery(String user_CNIC, String user_name, String message_date, String message_content) {
        this.user_CNIC = user_CNIC;
        this.user_name = user_name;
        this.message_date = message_date;
        this.message_content = message_content;
    }

    public UserQuery()
    {

    }

    public String getUser_CNIC() {
        return user_CNIC;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getMessage_date() {
        return message_date;
    }

    public String getMessage_content() {
        return message_content;
    }
}
