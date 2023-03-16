module com.example.emailclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires mail;
    requires org.jsoup;
    //requires jfxrt;
    requires java.desktop;
    requires  org.apache.commons.io;


    opens com.example.emailclient to javafx.fxml;
    exports com.example.emailclient;
}