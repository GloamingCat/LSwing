package lui.editor;

import java.awt.*;
import java.util.ArrayList;

import lui.base.LPrefs;
import lui.base.data.LPoint;
import lui.container.LContainer;
import lui.container.LPanel;
import lui.container.LScrollPanel;
import lui.base.data.LDataList;
import lui.widget.LControlWidget;
import lui.widget.LLabel;

import javax.swing.*;

public abstract class LGridForm<T> extends LObjectEditor<LDataList<T>> {

	protected LDataList<T> values;
	protected ArrayList<LControlWidget<T>> controls;
	protected LScrollPanel scroll;
	protected LPanel content;
	protected LLabel filler;

	protected int labelWidth = LPrefs.LABELWIDTH;
	protected int controlWidth = LPrefs.BUTTONWIDTH;

	protected int columns;

	//////////////////////////////////////////////////
	//region Constructor

	private class FormScrollPanel extends LScrollPanel {
		public FormScrollPanel() {
			super(LGridForm.this.getTopComposite(), false);
		}
	}

	public LGridForm(LContainer parent, int columns) {
		super(parent, columns, false);
	}

	@Override
	protected void createContent(int columns) {
		this.columns = columns;
		controls = new ArrayList<>();
		scroll = new FormScrollPanel();
		content = new LPanel(scroll);
		content.setGridLayout(columns * 2);
		scroll.setBorder(null);
		setFillLayout(true);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Value

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

	protected abstract T getDefaultValue();
	protected abstract LDataList<Object> getList();

	//endregion

	//////////////////////////////////////////////////
	//region Widgets

	public void setLabelWidth(int w) {
		labelWidth = w;
	}

	public void setControlWidth(int w) {
		controlWidth = w;
	}

	public void onVisible() {
		if (filler != null)
			filler.dispose();
		ArrayList<Object> data = getList();
		// Update children
		int nLabels = content.getComponentCount() / 2;
		controls.clear();
		for (int i = 0; i < nLabels; i++)	{
			LLabel label = (LLabel) getChild(i * 2);
			label.setText(getLabelText(i, data.get(i)));
			@SuppressWarnings("unchecked")
			LControlWidget<T> control = (LControlWidget<T>) getChild(i * 2 + 1);
			controls.add(control);
		}
		// Add missing controls for exceeding attributes
		for (int i = nLabels; i < data.size(); i ++) {
			LLabel label = new LLabel(content, getLabelText(i, data.get(i)));
			label.getCellData().setRequiredSize(labelWidth, LPrefs.WIDGETHEIGHT);
			LControlWidget<T> control = createControl(i, data.get(i));
			final int k = i;
			control.addModifyListener(event -> {
                if (currentObject != null)
                    currentObject.set(k, event.newValue);
            });
			control.getCellData().setRequiredSize(controlWidth, LPrefs.WIDGETHEIGHT);
			control.getCellData().setExpand(true, false);
			control.setMenuInterface(getMenuInterface());
			controls.add(control);
		}
		// Remove exceeding controls
		boolean refresh = nLabels != data.size();
		nLabels = data.size() * 2;
		while (content.getComponentCount() > nLabels) {
			Component c = content.getComponent(nLabels);
			remove(c);
			if (c instanceof LContainer cc)
				cc.onDispose();
		}
		filler = new LLabel(content, columns * 2, 1);
		filler.getCellData().setExpand(true, true);
		filler.getCellData().setTargetSize(0, 0);
		filler.getCellData().setRequiredSize(-1, -1);
		if (refresh)
			refreshLayout();
		super.onVisible();
	}

	protected abstract LControlWidget<T> createControl(final int i, final Object obj);
	protected abstract String getLabelText(final int i, final Object obj);

	//endregion

	//////////////////////////////////////////////////
	//region LContainer

	@Override
	public JComponent getTopComposite() {
		return this;
	}

	@Override
	public JComponent getContentComposite() {
		return content;
	}

	@Override
	public LPoint getSpacing() {
		return content.getSpacing();
	}

	@Override
	public void setSpacing(int h, int v) {
		content.setMargins(h, v);
	}

	@Override
	public LPoint getMargins() {
		return content.getMargins();
	}

	@Override
	public void setMargins(int h, int v) {
		content.setMargins(h, v);
	}

	//endregion

}
