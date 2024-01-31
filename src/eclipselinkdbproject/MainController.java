//@Author Kayra Cansın Gökmen&Ahmet Emre Çakmak

package eclipselinkdbproject;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;


public class MainController implements Initializable {

    @FXML
    private Label JPASystemLabel;
    @FXML
    private Button ReadDataBT;
    @FXML
    private Button listAllCoursesBT;
    @FXML
    private Button CalculateGPAGraduatesBT;
    @FXML
    private Button ListAllGraduatesBT;
    @FXML
    private Button CalculateAverageOfCourseBT;
    @FXML
    private Button CalculateAverageOfSlotBT;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EclipseLinkDBProjectPU");
    EntityManager entityManager = entityManagerFactory.createEntityManager();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ReadDataBT.setOnAction(b -> {
            try{
                
            
            ReadTables rt = new ReadTables();
            if (rt.readTables()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Başarılı!");
                alert.setHeaderText("Data okuma başarılı!");
                alert.setContentText("Data okuma başarılı!");
            }
            }
            catch(Exception e){
                 Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
            }
        });
        
       CalculateAverageOfSlotBT.setOnAction(a -> {
    try{
        Stage currentStage = (Stage) CalculateAverageOfCourseBT.getScene().getWindow();
    
    Scene scene = currentStage.getScene();
    Parent root = scene.getRoot();
    BorderPane borderPane = (BorderPane) root;
    TextField slotCodeTF = new TextField();
    slotCodeTF.setPromptText("Slot Kodu");

    TextField slotNameTF = new TextField();
    slotNameTF.setPromptText("Slot Adı");

    TextField yearTF = new TextField();
    yearTF.setPromptText("Yıl (Opsiyonel)");

    

    Button btn = new Button();
    btn.setText("Ortalama Hesapla");

    TableView<Map.Entry<String, Double>> notTableView = new TableView<>();

    TableColumn<Map.Entry<String, Double>, String> ortalamaSutunu = new TableColumn<>("Ortalama");
    ortalamaSutunu.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getValue())));

    TableColumn<Map.Entry<String, Double>, String> yilSutunu = new TableColumn<>("Yıl");
    yilSutunu.setCellValueFactory(cellData -> {
        String key = cellData.getValue().getKey();
        String[] parts = key.split("_");
        return new SimpleStringProperty((parts.length > 0) ? parts[0] : "");
    });

    TableColumn<Map.Entry<String, Double>, String> donemSutunu = new TableColumn<>("Dönem");
    donemSutunu.setCellValueFactory(cellData -> {
        String key = cellData.getValue().getKey();
        String[] parts = key.split("_");
        return new SimpleStringProperty((parts.length > 1) ? parts[1] : "");
    });

    notTableView.getColumns().add(ortalamaSutunu);
    notTableView.getColumns().add(yilSutunu);
    notTableView.getColumns().add(donemSutunu);

    btn.setOnAction(c -> {
        if(slotCodeTF.getText().equals("") || slotNameTF.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText("Please enter a valid input");
                    alert.showAndWait();
        }
        
        else{
        borderPane.setCenter(null);

        String selectedSlotCode = slotCodeTF.getText();
        String selectedSlotName = slotNameTF.getText();
        String selectedYear = yearTF.getText();
        

        String query = "SELECT s FROM SlotAndCourse s WHERE s.slot.slotCode = :slotCode AND s.slot.slotName = :slotName AND s.grade IS NOT NULL";
        if (!selectedYear.isEmpty()) {
            query += " AND s.yearTaken = :year";
        }
        

        TypedQuery<SlotAndCourse> queryObject = entityManager.createQuery(query, SlotAndCourse.class)
                .setParameter("slotCode", selectedSlotCode)
                .setParameter("slotName", selectedSlotName);

        if (!selectedYear.isEmpty()) {
            queryObject.setParameter("year", Integer.parseInt(selectedYear));
        }
        

        List<SlotAndCourse> slotAndCourseList = queryObject.getResultList();

        ObservableList<Map.Entry<String, Double>> data = FXCollections.observableArrayList();

        Map<String, List<SlotAndCourse>> groupedByYearAndSemester = slotAndCourseList.stream()
                .collect(Collectors.groupingBy(
                        slotAndCourse -> slotAndCourse.getYearTaken() + "_" + slotAndCourse.getTermTaken()));

        for (Map.Entry<String, List<SlotAndCourse>> entry : groupedByYearAndSemester.entrySet()) {
            String key = entry.getKey();
            List<SlotAndCourse> values = entry.getValue();
            Double value = calculateCourseAverage(values);
            data.add(new AbstractMap.SimpleEntry<>(key, value));
        }

        notTableView.setItems(data);

        borderPane.setCenter(notTableView);
        }
    });

    HBox hbox = new HBox();
    hbox.getChildren().addAll(slotCodeTF, slotNameTF, yearTF,  btn);
    borderPane.setCenter(hbox);
    
    }
    catch(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
    }
    
});

        
       CalculateAverageOfCourseBT.setOnAction(a -> {
           try{
    Stage currentStage = (Stage) CalculateAverageOfCourseBT.getScene().getWindow();
    Scene scene = currentStage.getScene();
    Parent root = scene.getRoot();
    BorderPane borderPane = (BorderPane) root;
    HBox hbox = new HBox();
    borderPane.setCenter(hbox);

    TextField courseCodeTF = new TextField();
    courseCodeTF.setPromptText("Ders Kodu");

    TextField yearTF = new TextField();
    yearTF.setPromptText("Yıl (Opsiyonel)");

    TextField semesterTF = new TextField();
    semesterTF.setPromptText("Dönem (Opsiyonel)");

    Button btn = new Button();
    btn.setText("Ortalama Hesapla");
    hbox.getChildren().addAll(courseCodeTF, yearTF, semesterTF, btn);

    btn.setOnAction(c -> {
    borderPane.setCenter(null);

    if (courseCodeTF.getText().isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Uyarı!");
        alert.setHeaderText("Uyarı");
        alert.setContentText("Lütfen geçerli bir ders kodu giriniz");
        alert.showAndWait();
    } else {
        String secilenDersKodu = courseCodeTF.getText();
        String yil = yearTF.getText();
        String donem = semesterTF.getText();

        String sorgu = "SELECT s FROM SlotAndCourse s WHERE s.course.courseCode = :dersKodu AND s.grade IS NOT NULL";
        if (!yil.isEmpty()) {
            sorgu += " AND s.yearTaken = :yil";
        }
        if (!donem.isEmpty()) {
            sorgu += " AND s.termTaken = :donem";
        }

        TypedQuery<SlotAndCourse> sorguNesnesi = entityManager.createQuery(sorgu, SlotAndCourse.class)
                .setParameter("dersKodu", secilenDersKodu);

        if (!yil.isEmpty()) {
            sorguNesnesi.setParameter("yil", Integer.parseInt(yil));
        }
        if (!donem.isEmpty()) {
            sorguNesnesi.setParameter("donem", donem);
        }

        List<SlotAndCourse> dersNotlari = sorguNesnesi.getResultList();

        TableView<Map.Entry<String, Double>> notTableView = new TableView<>();

        

        TableColumn<Map.Entry<String, Double>, String> ortalamaSutunu = new TableColumn<>("Ortalama");
        ortalamaSutunu.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getValue())));

        TableColumn<Map.Entry<String, Double>, String> yilSutunu = new TableColumn<>("Yıl");
        yilSutunu.setCellValueFactory(cellData -> {
            String key = cellData.getValue().getKey();
    
    if (!key.contains("_")) {
        return new SimpleStringProperty(key);
    }
    
    String[] parts = key.split("_");
    String dersKodu = (parts.length > 0) ? parts[0] : "";
    
    return new SimpleStringProperty(dersKodu);
        });

        TableColumn<Map.Entry<String, Double>, String> donemSutunu = new TableColumn<>("Dönem");
        donemSutunu.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().getKey().split("_");
            return new SimpleStringProperty((parts.length > 1) ? parts[1] : "");
        });

        
        notTableView.getColumns().add(ortalamaSutunu);
        notTableView.getColumns().add(yilSutunu);
        notTableView.getColumns().add(donemSutunu);

        ObservableList<Map.Entry<String, Double>> data = FXCollections.observableArrayList();

        Map<String, List<SlotAndCourse>> groupedByYearAndSemester = dersNotlari.stream()
                .collect(Collectors.groupingBy(
                        slotAndCourse -> slotAndCourse.getYearTaken() + "_" + slotAndCourse.getTermTaken()));

        for (Map.Entry<String, List<SlotAndCourse>> entry : groupedByYearAndSemester.entrySet()) {
            String key = entry.getKey();
            List<SlotAndCourse> values = entry.getValue();
            Double value = calculateCourseAverage(values);
            data.add(new AbstractMap.SimpleEntry<>(key, value));
        }

        notTableView.setItems(data);

        borderPane.setCenter(notTableView);
        
    }
});
           }
    catch(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
    }
});

        ListAllGraduatesBT.setOnAction(a -> {
            try{
            Stage currentStage = (Stage) ListAllGraduatesBT.getScene().getWindow();
            Scene scene = currentStage.getScene();
            Parent root = scene.getRoot();
            BorderPane borderPane = (BorderPane) root;
            HBox hbox = new HBox();
            borderPane.setCenter(hbox);
            TextField yearttf = new TextField();
            yearttf.setPromptText("year");
            Button btn = new Button();
            btn.setText("Get Table");
            hbox.getChildren().addAll(yearttf, btn);
            btn.setOnAction(c -> {
                borderPane.setCenter(null);
                if (yearttf.getText().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText("Please enter a valid input");
                    alert.showAndWait();
                } else {
                    int selectedYear = Integer.parseInt(yearttf.getText());

                    List<Student> graduates = entityManager.createQuery("SELECT s FROM Student s WHERE s.leavingDate IS NOT NULL AND FUNCTION('YEAR', s.leavingDate) = :selectedYear", Student.class)
                            .setParameter("selectedYear", selectedYear)
                            .getResultList();

                    TableColumn<Student, String> studentNumberColumn = new TableColumn<>("Öğrenci Numarası");
                    studentNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentNumber()));

                    TableColumn<Student, String> leavingDateColumn = new TableColumn<>("Mezuniyet Tarihi");
                    leavingDateColumn.setCellValueFactory(cellData -> {
                        Date date = cellData.getValue().getLeavingDate();
                        String formattedDate = (date != null) ? new SimpleDateFormat("yyyy-MM-dd").format(date) : "";
                        return new SimpleStringProperty(formattedDate);
                    });

                    TableColumn<Student, String> minorDegreeColumn = new TableColumn<>("Yan Dal");
                    minorDegreeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(Boolean.toString(cellData.getValue().getMinorDegree())));

                    TableView<Student> graduateTableView = new TableView<>();
                    graduateTableView.getColumns().add(studentNumberColumn);
                    graduateTableView.getColumns().add(leavingDateColumn);
                    graduateTableView.getColumns().add(minorDegreeColumn);

                    ObservableList<Student> observableGraduates = FXCollections.observableArrayList(graduates);
                    graduateTableView.setItems(observableGraduates);

                    borderPane.setCenter(graduateTableView);
                }
            });
            }
            catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
            }
        });
        
        CalculateGPAGraduatesBT.setOnAction(t -> {
            try{
            Stage currentStage = (Stage) ListAllGraduatesBT.getScene().getWindow();
            Scene scene = currentStage.getScene();
            Parent root = scene.getRoot();
            BorderPane borderPane = (BorderPane) root;
            HBox hbox = new HBox();
            borderPane.setCenter(hbox);
            Button btn = new Button();
            btn.setText("Calculate GPAs");
            hbox.getChildren().addAll(btn);
            btn.setOnAction(c -> {
                borderPane.setCenter(null);

                List<Student> graduates = entityManager.createQuery("SELECT s FROM Student s WHERE s.leavingDate IS NOT NULL", Student.class)
                        .getResultList();

                
                TableColumn<Student, String> studentNumberColumn = new TableColumn<>("Öğrenci Numarası");
                studentNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentNumber()));

                TableColumn<Student, String> leavingDateColumn = new TableColumn<>("Mezuniyet Tarihi");
                leavingDateColumn.setCellValueFactory(cellData -> {
                    Date date = cellData.getValue().getLeavingDate();
                    String formattedDate = (date != null) ? date.toString() : "";
                    return new SimpleStringProperty(formattedDate);
                });

                TableColumn<Student, String> minorDegreeColumn = new TableColumn<>("Yan Dal");
                minorDegreeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(Boolean.toString(cellData.getValue().getMinorDegree())));

                TableColumn<Student, String> gpaColumn = new TableColumn<>("GPA");
                gpaColumn.setCellValueFactory(cellData -> {
                    String studentNumber = cellData.getValue().getStudentNumber();
                    double gpa = calculateStudentGPA(studentNumber);
                    return new SimpleStringProperty(String.format("%.2f", gpa));
                });

                TableView<Student> graduateTableView = new TableView<>();
                graduateTableView.getColumns().add(studentNumberColumn);
                graduateTableView.getColumns().add(leavingDateColumn);
                graduateTableView.getColumns().add(minorDegreeColumn);
                graduateTableView.getColumns().add(gpaColumn);

                ObservableList<Student> observableGraduates = FXCollections.observableArrayList(graduates);
                graduateTableView.setItems(observableGraduates);

                borderPane.setCenter(graduateTableView);
            });
            }
            catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
            }
        });
        listAllCoursesBT.setOnAction(a -> {
            try{
            Stage currentStage = (Stage) listAllCoursesBT.getScene().getWindow();
            Scene scene = currentStage.getScene();
            Parent root = scene.getRoot();
            BorderPane borderPane = (BorderPane) root;
            HBox hbox = new HBox();
            borderPane.setCenter(hbox);
            TextField slotCodetf = new TextField();
            slotCodetf.setPromptText("slot code");
            TextField yearttf = new TextField();
            yearttf.setPromptText("year");
            Button btn = new Button();
            btn.setText("Get Table");
            hbox.getChildren().addAll(slotCodetf, yearttf, btn);
            btn.setOnAction(c -> {
                borderPane.setCenter(null);
                if (slotCodetf.getText().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText("Please enter a valid input");
                    alert.showAndWait();

                } else {
                    String selectedSlotCode = slotCodetf.getText();
                    String year = yearttf.getText();
                    String queryString = "SELECT s FROM SlotAndCourse s WHERE s.ck.slotCode = :slotCode";

                    if (!year.isEmpty()) {
                        queryString += " AND s.yearTaken = :yearTaken";
                    }

                    TypedQuery<SlotAndCourse> query = entityManager.createQuery(queryString, SlotAndCourse.class)
                            .setParameter("slotCode", selectedSlotCode);

                    if (!year.isEmpty()) {
                        query.setParameter("yearTaken", Integer.parseInt(year));
                    }

                    List<SlotAndCourse> coursesForSlot = query.getResultList();

                    TableColumn<SlotAndCourse, String> courseCodeColumn = new TableColumn<>("Kurs Kodu");
                    TableColumn<SlotAndCourse, String> yearColumn = new TableColumn<>("Yıl");
                    yearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getYearTaken())));

                    TableColumn<SlotAndCourse, String> termTakenColumn = new TableColumn<>("Term");
                    termTakenColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTermTaken()));

                    courseCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourse().getCourseCode()));

                    TableView<SlotAndCourse> courseTableView = new TableView<>();
                    courseTableView.getColumns().add(courseCodeColumn);
                    courseTableView.getColumns().add(yearColumn);
                    courseTableView.getColumns().add(termTakenColumn);

                    ObservableList<SlotAndCourse> observableCourses = FXCollections.observableArrayList(coursesForSlot);
                    courseTableView.setItems(observableCourses);

                    borderPane.setCenter(courseTableView);
                }
            });
            }
            catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
            }
        });
    }

    private double calculateStudentGPA(String studentNumber) {
        try{
        List<SlotAndCourse> courses = entityManager.createQuery("SELECT s FROM SlotAndCourse s WHERE s.ck.studentNumber = :studentNumber AND s.grade IS NOT NULL", SlotAndCourse.class)
            .setParameter("studentNumber", studentNumber)
            .getResultList();

    double totalGradePoints = 0;
    int totalCredits = 0;

    for (SlotAndCourse course : courses) {
        int credit = course.getSlot().getCredit();
        String grade = course.getGrade();
        double gradePoint = calculateGradePoint(grade);

        totalGradePoints += (gradePoint * credit);
        totalCredits += credit;
    }

    return (totalCredits > 0) ? totalGradePoints / totalCredits : 0.0;
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
                    
        }
        return 0;
    }

    private double calculateGradePoint(String grade) {
        try{
       if (grade == null) {
        return 0.0; 
    }

    
    switch (grade) {
        case "aa":
            return 4.0;
        case "ba":
            return 3.5;
        case "bb":
            return 3.0;
        case "cb":
            return 2.5;
        case "cc":
            return 2.0;
        case "dc":
            return 1.5;
        case "dd":
            return 1.0;
        default:
            return 0.0;
    }
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
        }
        return 0;
    }
    
    private double calculateCourseAverage(List<SlotAndCourse> courseGrades) {
        try{
    double totalGradePoints = 0;
    int numberOfGrades = 0;

    for (SlotAndCourse grade : courseGrades) {
        String gradeString = grade.getGrade();
        double gradePoint = calculateGradePoint(gradeString);

        totalGradePoints += gradePoint;
        numberOfGrades++;
    }

    return (numberOfGrades > 0) ? totalGradePoints / numberOfGrades : 0.0;
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert!");
                    alert.setHeaderText("Alert");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
        }
        return 0;
}
}
