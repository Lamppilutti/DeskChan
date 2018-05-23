package info.deskchan.gui_javafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FilesManagerDialog extends TemplateBox{

    protected ListView<String> filesList = new ListView<>();

    public List<String> getSelectedFiles(){
        List<String> list = new LinkedList<>();
        for(String file : filesList.getItems())
            list.add(file);
        return list;
    }

    public FilesManagerDialog(Window parent, List<String> files){
        super("file-manager");
        initOwner(parent);

        for (String file : files) {
            if(file != null)
                filesList.getItems().add(file);
        }
        setMultipleSelection(false);

        getDialogPane().setContent(filesList);

        setResizable(true);

        ButtonType addButton = new ButtonType(Main.getString("add"));
        ButtonType removeButton = new ButtonType(Main.getString("remove"));
        getDialogPane().getButtonTypes().add(addButton);
        getDialogPane().getButtonTypes().add(removeButton);
        getDialogPane().lookupButton(addButton).addEventFilter(ActionEvent.ACTION, (event) -> {
            event.consume();
            try {
                Platform.runLater(() -> {
                    FileChooser chooser = new FileChooser();
                    chooser.setInitialDirectory(Main.getInstance().getPluginProxy().getRootDirPath().toFile());
                    File newFile = chooser.showOpenDialog(getDialogPane().getScene().getWindow());
                    if (newFile != null)
                        filesList.getItems().add(newFile.toString());
                });
            } catch(Exception e){ };
        });
        getDialogPane().lookupButton(removeButton).addEventFilter(ActionEvent.ACTION, (event) -> {
            event.consume();
            try {
                Platform.runLater(() -> {
                    filesList.getItems().remove(filesList.getSelectionModel().getSelectedIndex());
                });
            } catch(Exception e){ };
        });
    }

    void setMultipleSelection(boolean value){
        filesList.getSelectionModel().setSelectionMode(value ? SelectionMode.MULTIPLE : SelectionMode.SINGLE);
    }

    interface ItemsListener {
        void change(List<String> newItems);
    }
    private ItemsListener listener = null;
    void setListener(ItemsListener listener){
        this.listener = listener;
    }

    public void showDialog(){
        filesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (listener != null)
                listener.change(getSelectedFiles());
        });
        showAndWait();
    }
}
