package utilWindows.wizard.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Photo;

//DONE Vorschau �berspringbar machen
/**
 * A Class that constructs a Pane for the Import Photos Wizard. It manages the
 * View that lets the user preview and choose the images they wish to import.
 * 
 * @author Basti
 *
 */
public class WizardPreviewWindow {

	private List<Photo> importList;
	private Pane mainContainer;
	private List<File> listOfFiles;

	private Insets defaultPadding;
	private DropShadow ds;

	public WizardPreviewWindow(Pane mainContainer, List<File> listOfFiles) {

		this.listOfFiles = listOfFiles;
		this.mainContainer = mainContainer;

		defaultPadding = new Insets(15);
		ds = new DropShadow(5, Color.DARKGREEN);
		ds.setSpread(0.75);
	}

	/**
	 * Create the Pane that displays all previously chosen images as thumbnails.
	 * Clicking the images will mark and unmark them.
	 * 
	 * @return
	 */
	public Pane getPreviewWindow() {

		List<Photo> listOfPhotos = convertFilesToPhotos();
		importList = FXCollections.observableArrayList();

		TilePane tiles = new TilePane();
		tiles.setPadding(defaultPadding);
		tiles.setHgap(15);
		tiles.setVgap(15);

		for (Photo photo : listOfPhotos) {
			ImageView imageView = createImageView(photo);
			tiles.getChildren().add(imageView);
		}

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setFitToWidth(true);

		scrollPane.setContent(tiles);

		Button markAllBtn = new Button("Alle Markieren");
		markAllBtn.setOnAction(event -> {

			if (markAllBtn.getText().equals("Alle Markieren")) {
				importList.clear();
				tiles.getChildren().forEach(x -> {
					importList.add(((Photo) x.getUserData()));
					x.setEffect(ds);
				});
				markAllBtn.setText("Markierung aufheben");

			} else {
				markAllBtn.setText("Alle Markieren");
				tiles.getChildren().forEach(x -> {
					x.setEffect(null);
				});
				importList.clear();
			}

		});

		ToolBar menuBar = new ToolBar(markAllBtn);
		menuBar.setPadding(new Insets(5, 0, 5, 15));

		Label label = new Label(
				"Markieren sie alle Bilder die sie importieren m�chten durch einfachen Klick.\nDurch einen weiteren Klick deaktivieren sie die Auswahl wieder.");
		label.setPadding(defaultPadding);

		VBox root = new VBox(label, menuBar, scrollPane);
		root.setId("secondPane");
		root.prefHeightProperty().bind(mainContainer.heightProperty().subtract(40));

		return root;

	}

	/**
	 * Utility method that creates an ImageView and adds a Photo to it. A
	 * ClickHandler adds the Photo to a List of to be imported Photos or removes
	 * the Photo from the List if its already in the List.
	 * 
	 * @param photo The Photo to be added to the ImageView
	 * @return the created ImageView
	 */
	private ImageView createImageView(Photo photo) {
		ImageView imageView = new ImageView(photo.getThumbNail());
		imageView.setUserData(photo);

		imageView.setOnMouseClicked(me -> {

			if (me.getClickCount() == 1) {
				ImageView tmp = (ImageView) me.getTarget();

				if (importList.contains(photo)) {
					importList.remove(photo);
					tmp.setEffect(null);

				} else {
					importList.add(photo);
					tmp.setEffect(ds);
				}
			}
		});

		imageView.setFitHeight(150);

		return imageView;
	}

	private List<Photo> convertFilesToPhotos() {
		List<Photo> listOfPhotos = new ArrayList<>();

		for (File x : listOfFiles) {
			listOfPhotos.add(new Photo(x.toPath()));
		}

		return listOfPhotos;
	}

	public List<Photo> getImportedList() {
		return importList;
	}

}