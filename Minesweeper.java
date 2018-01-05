
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Eine Implementierung von Minesweeper mit JavaFX.<br>
 * Der Code und enthaltene Dokumentation sowie Kommentare sind in Englisch
 * verfasst.<br>
 * Zugehörigkeit: Controller<br>
 * Es wird das Entwurfsmuster MVC verwendet. Zur Durchreichung der
 * Benutzereingaben von View zu Controller wird das Delegate-Muster verwendet.
 * Auf die Verwendung des Observer-Muster zur Aktualisierung der View wurde
 * verzichtet, da dies in diesem einfachen Programm nicht notwendig ist.
 * @author CONATH
 * @version 1.0
 */
public class Minesweeper extends Application
                          implements MinesweeperViewDelegate {
  private MinesweeperModel model;
  private MinesweeperView view;

  /**
   * Start the JavaFX application.
   * @param args Is ignored
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Starting point of the application. Loads model and view.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    model = new MinesweeperModel();
    view = new MinesweeperView(model.getWidth(), model.getHeight(), this);
    updateView();
    Scene scene = new Scene(view);
    primaryStage.setTitle(view.getPreferredTitle());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Called by the view when the user clicks the primary mouse button.
   * @param x Horizontal position of click on game board
   * @param y Vertival position of click on game board
   */
  @Override
  public void primaryInteractionOccurred(int x, int y) {
    // gameState 0 = continue, 1 = finish, 2 = fail
    int gameState = model.userSelectedField(x, y);
    updateView();
    if (gameState == 1)
      view.showEndDialog(true);
    else if (gameState == 2)
      view.showEndDialog(false);
  }

  /**
   * Called by the view when the user clicks the secondary mouse button.
   * @param x Horizontal position of click on game board
   * @param y Vertical position of click on game board
   */
  @Override
  public void secondaryInteractionOccurred(int x, int y) {
    model.flag(x, y);
    updateView();
  }

  /**
   * Called by the view when the user dismisses the game over
   * dialog by clicking OK. (restart game)
   */
  @Override
  public void endDialogDidSelectOk() {
    model = new MinesweeperModel();
    updateView();
  }

  /**
   * Called by the view when the user dismisses the game over
   * dialog by clicking cancel. (quit game)
   */
  @Override
  public void endDialogDidSelectCancel() {
    System.exit(0);
  }

  /**
   * Updates the view with contents of the model. This is the part where the
   * observer pattern could have been used (that would also allow atomic view
   * updates). However since all changes to the model are initiated by the
   * controller and completely re-setting the grid doesn't take too long,
   * this lazy implementation suffices for this limited game.
   */
  private void updateView() {
    for (int x = 0; x<model.getWidth(); x ++) {
      for (int y = 0; y<model.getHeight(); y ++) {
        if (model.isFlagged(x, y)) {
          view.setLabel(x, y, "P");
        } else if (model.isVisible(x, y)) {
          int field = model.getFieldContentAt(x, y);
          String label = "" + model.mapFieldContentToChar(field);
          view.setLabel(x, y, label);
        } else { // Contents have not yet been revealed
          view.setLabel(x, y, " ");
        }
        view.setDisabled(x, y, model.isVisible(x, y));
      }
    }
  }

}
