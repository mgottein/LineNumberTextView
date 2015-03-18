# LineNumberTextView
TextView and EditText with line numbers

![Sample Screenshot](/screenshot.png)

##Usage
```Java
LineNumberTextView tv = new LineNumberTextView(getContext());

//By default, line numbers are shown on every line and on the left

//Change line number display properties
tv.setLineNumberColor(Color.BLACK);
tv.setLineNumberSize(20);
tv.setLineNumberTypeface(Typeface.MONOSPACE);

//Make line numbers appear on the right
tv.layoutLineNumbersOnLeft(false);

//Make line numbers as close to the line as possible
tv.doLineNumbersHugLine(true);

//This only shows line numbers on even lines
tv.setLineNumberController(new LineNumberController() {

  @Override
  public String getLineNumberText(boolean layoutOnLeft, int line) {
    return Integer.toString(line);
  }
  
  @Override
  public boolean showLineNumber(int line) {
    return line % 2 == 0;
  }
  
});

//An edit text with line numbers is available as well
LineNumberEditText et = new LineNumberEditText(getContext());
```
