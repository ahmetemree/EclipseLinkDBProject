//@Author Ahmet Emre Çakmak
package eclipselinkdbproject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ReadTables {

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EclipseLinkDBProjectPU");
    EntityManager EntityManager = entityManagerFactory.createEntityManager();

    public boolean readTables() {
        List<String> fileNames = new ArrayList<>();
        String dataFolderLocation = "Data/";

        for (int i = 1; i <= 693; i++) {
            fileNames.add(dataFolderLocation + i + ".txt.txt.mxt");
        }
        fileNames.add(dataFolderLocation+"2126.txt.txt.mxt");
        try {
            for (String fileName : fileNames) {
                readFile(fileName);
            }

            if (!EntityManager.isJoinedToTransaction()) {
                EntityManager.getTransaction().begin();
            }
            EntityManager.getTransaction().commit();
        } catch (Exception e) {
            if (EntityManager.getTransaction().isActive()) {
                EntityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            EntityManager.close();
        }
        return true;
    }

    private void readFile(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {

            Student student = new Student();
            Slot currentSlot = new Slot();
            Course currentCourse = new Course();
            CCR ccr = new CCR();
            SlotAndCourse slotAndCourse = new SlotAndCourse();
            int currentSemester = 1;
            int countAttempts = 0;
            boolean isStillStudent = true;

            while (scanner.hasNext()) {
                String[] parts = scanner.nextLine().split("\\s+");

                if (isStillStudent) {
                    switch (parts[0]) {
                        case "majorleavingdate":
                            if (student != null) {
                                Date leavingDate = new SimpleDateFormat("dd MM yyyy").parse(parts[1] + " " + parts[2] + " " + parts[3]);
                                student.setLeavingDate(leavingDate);
                            }
                            break;

                        case "studentnumber":
                            student.setStudentNumber(parts[1]);
                            break;

                        case "minor":
                            if (student != null) {
                                student.setMinorDegree(Boolean.parseBoolean(parts[1]));
                            }
                            break;
                        case "semester":
                            currentSemester = Integer.parseInt(parts[1]);
                            isStillStudent = false;
                            break;
                        default:
                            break;
                    }
                } else {
                    if (parts[0].equals("numberofattempts")) {
                        countAttempts = Integer.parseInt(parts[1]);
                        continue;
                    }

                    if (parts[0].equals("semester")) {
                        currentSemester = Integer.parseInt(parts[1]);
                        continue;
                    }

                    if (countAttempts == 0) {
                        EntityManager.getTransaction().begin();
                        currentCourse = new Course(parts[0]);
                        currentSlot = new Slot(parts[0], parts[1]);
                        currentSlot.setCredit(Integer.parseInt(parts[2]));
                        currentSlot.setSemester(currentSemester);
                        EntityManager.merge(currentSlot);
                        EntityManager.merge(currentCourse);
                        EntityManager.getTransaction().commit();
                        continue;
                    }

                    if (countAttempts > 0) {
                        EntityManager.getTransaction().begin();
                        slotAndCourse.setYearTaken(Integer.parseInt(parts[0]));
                        slotAndCourse.setTermTaken(parts[1]);
                        slotAndCourse.setGrade(parts[2]);
                        slotAndCourse.setSlot(currentSlot);
                        slotAndCourse.setCourse(currentCourse);

                        CCRId ccrid = new CCRId();
                        ccrid.setStudentNumber(student.getStudentNumber());
                        ccr.setStudent(student);
                        ccr.setCCRId(ccrid);

                        slotAndCourseCK ck = new slotAndCourseCK();
                        ck.setSlotCode(currentSlot.getSlotCode());
                        ck.setCourseCode(currentCourse.getCourseCode());
                        ck.setStudentNumber(student.getStudentNumber());
                        slotAndCourse.setCk(ck);
                        slotAndCourse.setCcr(ccr);
                        slotAndCourse.getCcr().setStudent(student);
                        EntityManager.merge(slotAndCourse);
                        EntityManager.getTransaction().commit();
                        countAttempts--;
                        continue;
                    }
                }
            }

            EntityManager.getTransaction().begin();
            EntityManager.persist(student);
            EntityManager.persist(ccr);
            EntityManager.getTransaction().commit();

        } catch (Exception e) {
            
        }
    }
}
