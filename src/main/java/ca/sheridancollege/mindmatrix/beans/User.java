package ca.sheridancollege.mindmatrix.beans;

public class User {

    private String email;
    private String name;
    private String birth;
    private String points;

    // Constructor with parameters
    public User(String email, String name, String birth, String points ) {
        this.email = email;
        this.name = name;
        this.birth = birth;
        this.points = points;  // Now an int
    }

    // Default constructor
    public User() {
        // Empty constructor
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoints() {
        return points;  // Getter returns int
    }

    public void setPoints(String points) {  // Setter accepts int
        this.points = points;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    // Optional: Override toString for better readability
    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", birth=" + birth +  // Now an int
                ", points='" + points + '\'' +
                '}';
    }
}

