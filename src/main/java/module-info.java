module com.example.emailclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires mail;
    requires org.jsoup;
    //requires jfxrt;


    opens com.example.emailclient to javafx.fxml;
    exports com.example.emailclient;
}