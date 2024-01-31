//@Author Kayra Cansın Gökmen

package eclipselinkdbproject;
import javax.persistence.Embeddable;

@Embeddable
public class slotAndCourseCK {
    private String slotCode;
    private String courseCode;
    private String studentNumber;

    public slotAndCourseCK() {
    }

    public slotAndCourseCK(String slotCode, String courseCode) {
        this.slotCode = slotCode;
        this.courseCode = courseCode;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode(); 
    }

    @Override
    public String toString() {
        return super.toString();
    }
    

}
