package lui.editor;

import java.awt.Dimension;
import java.util.ArrayList;

import lui.container.LContainer;
import lui.container.LPanel;
import lui.container.LScrollPanel;
import lui.base.data.LDataList;
import lui.base.event.LControlEvent;
import lui.base.event.listener.LControlListener;
import lui.widget.LControlWidget;
import lui.widget.LLabel;

public abstract class LGridForm<T> extends LObjectEditor<LDataList<T>> {

	protected LDataList<T> values;
	protected ArrayList<LControlWidget<T>> controls;
	protected LScrollPanel scroll;
	protected LPanel content;
	
	public LGridForm(LContainer parent, int columns) {
		super(parent, false);
		setFillLayout(true);
		controls = new ArrayList<>();
		scroll = new LScrollPanel(this, true);
		content = new LPanel(scroll);
		content.setGridLayout(columns * 2);
	}
	
	public void setObject(Object obj) {
		super.setObject(obj);
		if (obj != null) {
			for(int i = 0; i < controls.size(); i++) {
				if (i < currentObject.size()) {
					controls.get(i).setValue(currentObject.get(i));
				} else {
					T value = getDefaultValue();
					currentObject.add(value);
					controls.get(i).setValue(value);
				}
			}
		} else {
			for(LControlWidget<T> control : controls) {
				control.setValue(null);
				control.setEnabled(false);
			}
		}
	}
	
	public void saveObjectValues() {
		super.saveObjectValues();
		for(int i = 0; i < controls.size(); i++) {
			if (i < currentObject.size()) {
				currentObject.set(i, controls.get(i).getValue());
			} else {
				T value = getDefaultValue();
				currentObject.add(value);
				controls.get(i).setValue(value);
			}
		}
	}

	public void onVisible() {
		ArrayList<Object> data = getList();
		// Update children
		int nLabels = getChildCount() / 2;
		controls.clear();
		for (int i = 0; i < nLabels; i++)	{
			LLabel label = (LLabel) getChild(i * 2);
			label.setText(getLabelText(i, data.get(i)));
			@SuppressWarnings("unchecked")
			LControlWidget<T> control = (LControlWidget<T>) getChild(i * 2 + 1);
			controls.add(control);
		}
		// Add missing controls for exceeding attributes
		for(int i = nLabels; i < data.size(); i ++) {
			new LLabel(content, getLabelText(i, data.get(i)));
			LControlWidget<T> control = createControl(i, data.get(i));
			final int k = i;
			control.addModifyListener(new LControlListener<T>() {
				@Override
				public void onModify(LControlEvent<T> event) {
					if (currentObject != null) {
						currentObject.set(k, event.newValue);
					}
				}
			});
			control.setMenuInterface(getMenuInterface());
			controls.add(control);
		}
		// Remove exceeding controls
		nLabels = data.size() * 2;
		while (getChildCount() > nLabels) {
			LPanel label = (LPanel) getChild(nLabels);
			label.dispose();
		}
		Dimension size = content.getPreferredSize();
		scroll.setContentSize(size.width, size.height);
	}
	
	protected abstract T getDefaultValue();
	protected abstract LDataList<Object> getList();
	protected abstract LControlWidget<T> createControl(final int i, final Object obj);
	protected abstract String getLabelText(final int i, final Object obj);
	
}
