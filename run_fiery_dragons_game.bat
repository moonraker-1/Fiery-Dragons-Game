echo "Run the Game\n"

REM java --module-path %PATH_TO_FX% --add-modules javafx.controls -jar Fiery_Dragons.jar 

java --module-path . --add-modules javafx.controls,javafx.graphics,javafx.base -jar Fiery_Dragons.jar 
