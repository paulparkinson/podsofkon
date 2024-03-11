package podsofkon.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "SUMMARIES")
@Data
@NoArgsConstructor
public class Summary {
    public Summary(String summaryText, byte[] imageField) {
        this.summaryText = summaryText;
        this.imageField = imageField;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summary_text")
    private String summaryText;


    @Lob
    @Column(name = "image_field")
    private byte[] imageField;


    /**


     public static void main(String[] args) {
     String url = "jdbc:oracle:thin:@localhost:1521:xe"; // Replace with your database connection URL
     String username = "your_username"; // Replace with your database username
     String password = "your_password"; // Replace with your database password

     String sql = "INSERT INTO your_table_name (text_field1, text_field2, image_field) VALUES (?, ?, ?)";

     try (Connection conn = DriverManager.getConnection(url, username, password);
     PreparedStatement stmt = conn.prepareStatement(sql)) {
     stmt.setString(1, "Example Text 1"); // Set the value for text_field1
     stmt.setString(2, "Example Text 2"); // Set the value for text_field2

     // Set the value for image_field
     FileInputStream imageStream = new FileInputStream("path_to_your_image.jpg");
     stmt.setBinaryStream(3, imageStream);

     int rowsInserted = stmt.executeUpdate();
     if (rowsInserted > 0) {
     System.out.println("A new row has been inserted successfully.");
     }
     } catch (SQLException | FileNotFoundException e) {
     e.printStackTrace();
     }
     }
     }





     */

}
