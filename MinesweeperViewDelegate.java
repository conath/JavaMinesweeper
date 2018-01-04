
/**
 * MinesweeperViewDelegate wird von MinesweeperView über Eingaben des Spielers
 * benachrichtigt.<br>
 * Zugehörigkeit: View
 * @version 1.0
 * @author CONATH
 */
 public interface MinesweeperViewDelegate {
   public void primaryInteractionOccurred(int x, int y);
   public void secondaryInteractionOccurred(int x, int y);
   public void endDialogDidSelectOk();
   public void endDialogDidSelectCancel();
 }
