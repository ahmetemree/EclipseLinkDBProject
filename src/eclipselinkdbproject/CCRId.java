//@Author Ahmet Emre Çakmak

package eclipselinkdbproject;

import java.io.Serializable;
import javax.persistence.Embeddable;


@Embeddable
public class CCRId implements Serializable {

    private String studentNumber;

    public CCRId() {
    
    }

    public CCRId(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString(); 
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

}
