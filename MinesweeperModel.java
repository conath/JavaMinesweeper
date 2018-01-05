
import java.util.Random;

/**
 * MinesweeperModel hält das Spielfeld im semantischen Sinn im Speicher
 * und sorgt für den korrekten Spielablauf.<br>
 * Zugehörigkeit: Model. Mutable.<br>
 * Die Methoden, die Koordinaten als Parameter haben, erwarten, dass diese
 * innerhalb des Spielfelds liegen (sonst treten IndexOutOfBoundsException ein).
 * @version 1.0
 * @author CONATH
 */
public class MinesweeperModel {
  // Settings (MSS = MineSweeperSettings)
  static final int MSS_COLS = 20;
  static final int MSS_ROWS = 10;
  static final int MSS_MAX_MINES = 10;
  static final int MSS_MIN_MINES = 10;

  // Field states
  private static final int FIELD_MINE = -1;
  private static final int FIELD_BLANK = 0;
  // Number of adjacent mines is stored simply as any fieldContent > 0.

  // Debug output
  private static final char FIELD_MINE_C = '%';
  private static final char FIELD_BLANK_C = ' ';

  // Variables
  private int mines = 0;
  // Data
  private int[][] fieldContent = new int[MSS_COLS][MSS_ROWS];
  private boolean[][] isVisible = new boolean[MSS_COLS][MSS_ROWS];
  private boolean[][] isFlagged = new boolean[MSS_COLS][MSS_ROWS];

  /**
   * Generate randomly placed mines according to the constants.
   */
  public MinesweeperModel() {
    /* The initial value of variables of type boolean is false, so no need to
     * set isVisible or isFlagged to false. Moreover the initial value of int
     * is 0, which meets my specification above for blank fields.
     * So if the arrays are left as they were initialized above, the mine field
     * is initially blank, as required.
     */
    // Generate number of mines + random extra mines at random locations
    Random random = new Random();
    int extra = MSS_MAX_MINES - MSS_MIN_MINES;
    if (extra>0)
      extra = random.nextInt(extra);
    mines = MSS_MIN_MINES + extra;
    for (int i = 0; i<mines; i ++) {
      boolean success = false;
      do {
        int c = random.nextInt(MSS_COLS);
        int r = random.nextInt(MSS_ROWS);
        success = updateFieldSetNewMine(c, r);
      } while (!success);
    }
  }

  /**
   * Update entire field for new mine at c,r.
   * @param c Column of new mine to place
   * @param r Row of new mine to place
   * @return true if the mine was placed and fields were updated
   *          false if there is already a mine at c,r
   */
  private boolean updateFieldSetNewMine(int c, int r) {
    // Is there already a mine at c,r? Then a new one can't be placed there.
    if (fieldContent[c][r] == FIELD_MINE)
      return false;
    for (int x = c-1; x<=c+1; x ++)
      for (int y = r-1; y<=r+1; y ++)
        if (isWithinBounds(x, y))
          if (fieldContent[x][y]>=0)
            fieldContent[x][y] += 1;
    fieldContent[c][r] = FIELD_MINE;
    return true;
  }

  /**
   * Mark a single field as visible.
   * @param c Column of field
   * @param r Row of field
   */
  private void reveal(int c, int r) {
    isVisible[c][r] = true;
    isFlagged[c][r] = false;
  }

  /**
   * Determine whether there is a mine at field c,r.
   * @param c Column of field
   * @param r Row of field
   * @return Whether there is a mine there
   */
  private boolean isNotAMine(int c, int r) {
    return !(fieldContent[c][r]<0);
  }

  /**
   * Determine whether the field at c,r is blank (no neighboring mines).
   * @param c Column of field
   * @param r Row of field
   * @return Whether it is blank
   */
  private boolean isBlank(int c, int r) {
    return fieldContent[c][r]==FIELD_BLANK;
  }

  /**
   * Return whether the field at c,r is within grid bounds.
   * @param c Column of field
   * @param r Row of field
   * @return Whether it is within bounds
   */
  private boolean isWithinBounds(int c, int r) {
    return (c>=0 && r>=0 && c<MSS_COLS && r<MSS_ROWS);
  }

  /**
   * Mark all connected blank fields as visible.
   * @param c Column of initial field
   * @param r Row of initial field
   * @return All connected fields, e. g. for c==1,r==1 possibly {{1,2},{2,2}}.
   */
  private void revealAllConnectedBlank(int c, int r) {
    // strategy: mark neighbors as visible & recursively find neighboring zeroes
    for (int x = c-1; x<=c+1; x ++)
      for (int y = r-1; y<=r+1; y ++)
        if (isWithinBounds(x, y) && !(x==c && y==r) && !isVisible[x][y]) {
          if (isNotAMine(x, y))
            reveal(x, y);
          if (isBlank(x, y))
            revealAllConnectedBlank(x, y);
        }
  }

  /**
   * Reveal all fields.
   */
  private void revealAll() {
    for (int c = 0; c<MSS_COLS; c ++)
      for (int r = 0; r<MSS_ROWS; r ++)
        reveal(c, r);
  }

  /**
   * For accessing field content type at a certain grid location.
   * @param c Column of field
   * @param r Row of field
   * @return the field content type at c,r
   */
  public int getFieldContentAt(int c, int r) {
    return fieldContent[c][r];
  }

  /**
   * Map field content type to user-facing char.
   * @param f Field content type
   * @return the char for field content type f.
   */
  public char mapFieldContentToChar(int f) {
    switch (f) {
    case -1: return FIELD_MINE_C;
    case 0: return FIELD_BLANK_C;
    default: return (""+f).charAt(0);
    }
  }

  /**
   * Determine whether the game objective has been reached.
   * @return Whether all fields that do not contain mines have been revealed
   */
  private boolean didGameFinish() {
    for (int c = 0; c<MSS_COLS; c ++)
      for (int r = 0; r<MSS_ROWS; r ++) {
        boolean didFinish = (isNotAMine(c, r) && isVisible(c, r))
                    || (!isNotAMine(c, r) && !isVisible(c, r));
        if (!didFinish) return false;
      }
    return true;
  }

  /**
   * Process that player has selected field at c,r to be revealed.
   * @param c Column of field
   * @param r Row of field
   * @return Game state (0 = continue, 1 = finish, 2 = fail)
   */
  public int userSelectedField(int c, int r) {
    reveal(c, r);
    if (isNotAMine(c, r)) {
      revealAllConnectedBlank(c, r);
      return didGameFinish() ? 1 : 0;
    } else {
      revealAll();
      return 2;
    }
  }

  /**
   * Toggle whether the field at c,r is flagged.
   * @param c Column of field
   * @param r Row of field
   */
  public void flag(int c, int r) {
    isFlagged[c][r] = !isFlagged[c][r];
  }

  /**
   * Return whether the field at c,r is flagged.
   * @param c Column of field
   * @param r Row of field
   * @return Whether the field at c,r is flagged
   */
  public boolean isFlagged(int c, int r) {
    return isFlagged[c][r];
  }

  /**
   * Return whether the field at c,r is visible.
   * @param c Column of field
   * @param r Row of field
   * @return Whether it is visible
   */
  public boolean isVisible(int c, int r) {
    return isVisible[c][r];
  }

  /**
   * Return the width of the grid.
   * @return Width of the grid
   */
  public int getWidth() {
    return MSS_COLS;
  }

  /**
   * Return the height of the grid.
   * @return Height of the grid
   */
  public int getHeight() {
    return MSS_ROWS;
  }

  /**
   * Serialize field in human readable table-like String, useful for debugging.
   * @return a table representation of the field contents mapped to chars.
   */
  public String toString() {
    String ret = " ";
    for (int r = 0; r<MSS_ROWS; r ++) {
      for (int c = 0; c<MSS_COLS; c ++) {
        if (c!=0)
          ret += "| ";
        String s = ""+mapFieldContentToChar(fieldContent[c][r]);
        ret += s + " ";
      }
      if (r!=MSS_ROWS-1) {
        ret += "\n---";
        for (int i = 1; i<MSS_COLS; i ++)
          ret += "+---";
      }
      ret += "\n ";
    }
    return ret;
  }

}
