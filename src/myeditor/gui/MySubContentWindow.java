package myeditor.gui;

import lui.dialog.LObjectDialog;
import lui.dialog.LWindow;
import myeditor.data.MySubContent;

public class MySubContentWindow extends LObjectDialog<MySubContent> {

    private final MySubContentEditor editor;
    public MySubContentWindow(LWindow parent) {
        super(parent, "Sub-content");
        editor = new MySubContentEditor(content);
    }

    @Override
    public void open(MySubContent initial) {
        super.open(initial);
        editor.setObject(initial.clone());
    }

    @Override
    protected MySubContent createResult(MySubContent initial) {
        MySubContent result = editor.getObject();
        if (result.equals(initial))
            return null;
        return result;
    }

}
