
import java.io.Serializable;

public class Student implements Serializable {
    private static final long serialVersionUID = 20151107;

    private int id;
    private String code;
    private float gpa;
    private String gpaLetter;

    public Student(int id, String code, float gpa) {
        this.id = id;
        this.code = code;
        this.gpa = gpa;
        this.gpaLetter = convertGpaToLetter(gpa);
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public float getGpa() {
        return gpa;
    }

    public String getGpaLetter() {
        return gpaLetter;
    }

    private String convertGpaToLetter(float gpa) {
        if (gpa >= 3.7 && gpa <= 4.0) {
            return "A";
        } else if (gpa >= 3.0 && gpa < 3.7) {
            return "B";
        } else if (gpa >= 2.0 && gpa < 3.0) {
            return "C";
        } else if (gpa >= 1.0 && gpa < 2.0) {
            return "D";
        } else {
            return "F";
        }
    }
     public void setGpaLetter(String gpaLetter) {
        this.gpaLetter = gpaLetter;
    }
}
