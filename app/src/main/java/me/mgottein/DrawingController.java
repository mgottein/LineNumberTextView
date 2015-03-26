package me.mgottein;

/**
 * Delegate interface that controls display of line numbers
 */
public interface DrawingController {
  /**
   * @param layoutOnLeft is the line number displayed in the left column
   * @param line line number
   * @return formatted line number string to be displayed
   */
  public String getLineNumberText(boolean layoutOnLeft, int line);

  /**
   * @param line line number
   * @return if this line number will be shown
   */
  public boolean showLineNumber(int line);
}