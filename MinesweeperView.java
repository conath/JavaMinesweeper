
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseButton;

/**
 * Diese Klasse ist für die Anzeige des Spielfelds und Weiterleitung von
 * Benutzereingaben zuständig.
 * Zugehörigkeit: View
 * @version 1.0
 * @author CONATH
 */
public class MinesweeperView extends GridPane {

  // Constants
  private static final int MV_ITEM_WIDTH = 30;
  private static final int MV_ITEM_HEIGHT = 30;

  private GridPane board;
  private static MinesweeperViewDelegate delegate;
  private Button[][] buttons;

  /**
   * Constructor; initializes grid according to parameters
   * @param x Width of grid
   * @param y Height of grid
   * @param delegate MinesweeperViewDelegate gets notified about user input
   */
  public MinesweeperView(int x, int y, MinesweeperViewDelegate delegate) {
    this.delegate = delegate;
    board = new GridPane();
    buttons = new Button[x][y];
    for (int c = 0; c<x; c ++) {
      for (int r = 0; r<y; r ++) {
        buttons[c][r] = makeButton(c, r);
        board.add(buttons[c][r], c, r);
      }
    }
    this.add(board, 0, 0);
  }

  /**
   * Factory method for a new button that represents a cell on the game board.
   * @param x Width of grid
   * @param y Height of grid
   * @return A new Button that sends x and y to the delegate on mouse clicks
   */
  private static Button makeButton(int x, int y) {
    Button button = new Button();
    button.setMinWidth(MV_ITEM_WIDTH);
    button.setMinHeight(MV_ITEM_HEIGHT);
    button.setOnMouseReleased(event -> {
      if (event.getButton() == MouseButton.PRIMARY)
        delegate.primaryInteractionOccurred(x, y);
      else if (event.getButton() == MouseButton.SECONDARY)
        delegate.secondaryInteractionOccurred(x, y);
    });
    return button;
  }

  /**
   * Update the button at x,y in the grid with new text.
   * @param x Horizontal position of button to be updated
   * @param y Vertical position of button to be updated
   * @param label New label to set for the button at x,y
   */
  public void setLabel(int x, int y, String label) {
    buttons[x][y].setText(label);
  }

  /**
   * Update the button at x,y in the grid with new disabled state.
   * @param x Horizontal position of button to be updated
   * @param y Vertical position of button to be updated
   * @param disable New disabled state to set for the button at x,y
   */
  public void setDisabled(int x, int y, boolean disable) {
    buttons[x][y].setDisable(disable);
  }

  /**
   * @return The current preferred title of the game window.
   */
  public String getPreferredTitle() {
    return "Minesweeper";
  }

  /**
   * Display a game over dialog box with an OK button.
   * The delegate (MinesweeperViewDelegate) gets notified when the player
   * dismisses the dialog.
   * @param win Whether the game ended with a win or a fail
   */
  public void showEndDialog(boolean win) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Beenden mit Abbrechen\nNeustart mit OK");
    alert.setTitle("Spielende");
    String msg = win ? "Gewonnen!" : "Verloren!";
    alert.setHeaderText(msg);
    alert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK)
        delegate.endDialogDidSelectOk();
      else
        delegate.endDialogDidSelectCancel();
    });
  }
}
