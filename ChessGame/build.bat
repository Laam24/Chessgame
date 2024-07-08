@echo off
REM Build the project with Maven
call "F:\apache-maven-3.9.8\bin\mvn" clean package
REM Create the executable
"C:\Program Files\Java\jdk-22\bin\jpackage" ^
--type exe ^
--input target ^
--main-jar chessgame-1.0-SNAPSHOT.jar ^
--main-class org.example.chessgame.HelloApplication ^
--name ChessGame ^
--module-path "PATH_TO_JAVAFX_MODS;PATH_TO_OTHER_MODS" ^
--add-modules javafx.controls,javafx.fxml,javafx.graphics,org.controlsfx.controls,com.dlsc.formsfx,net.synedra.validatorfx,org.kordamp.ikonli.javafx,org.kordamp.bootstrapfx.core,eu.hansolo.tilesfx,com.almasb.fxgl.all