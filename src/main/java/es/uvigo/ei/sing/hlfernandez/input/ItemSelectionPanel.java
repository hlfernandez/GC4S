package es.uvigo.ei.sing.hlfernandez.input;

import static es.uvigo.ei.sing.hlfernandez.util.Checks.requireStrictPositive;
import static java.util.Objects.requireNonNull;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JComboBox;

import es.uvigo.ei.sing.hlfernandez.combobox.ComboBoxItem;
import es.uvigo.ei.sing.hlfernandez.ui.CenteredJPanel;

/**
 * An {@code ItemSelectionPanel} allows users choosing <i>n</i> items from 
 * different combo boxes.
 * 
 * @author hlfernandez
 *
 * @param <T> the type of the items.
 */
public class ItemSelectionPanel<T> extends CenteredJPanel {
	private static final long serialVersionUID = 1L;

	public static final String PROPERTY_SELECTION = "gc4s.item.selection";

	private List<T> items;
	private int n;
	private InputParameter[] inputParameters;
	private Function<T, String> namingFunction;

	/**
	 * Creates a new {@code ItemSelectionPanel} with {@code n} combo boxes
	 * to select {@code items}. The visible name in the combo box for each item
	 * is its {@code toString} representation. 
	 *  
	 * @param items a list of selectable items.
	 * @param n an strict positive integer specifying the number of items to 
	 * 	select.
	 */
	public ItemSelectionPanel(List<T> items, int n) {
		this(items, n, T::toString);
	}

	/**
	 * Creates a new {@code ItemSelectionPanel} with {@code n} combo boxes
	 * to select {@code items}. The {@code namingFunction} is applied to each
	 * item to obtain its visible name in the combo box. 
	 * 
	 * @param items a list of selectable items.
	 * @param n an strict positive integer specifying the number of items to 
	 * 	select.
	 * @param namingFunction the function to obtain the visible name in the
	 * 	combo boxes.
	 */
	public ItemSelectionPanel(List<T> items, int n,
		Function<T, String> namingFunction
	) {
		this.items = requireNonNull(items);
		this.n = requireStrictPositive(n, "n must be greater than 0");
		this.namingFunction = requireNonNull(namingFunction);
		
		this.initComponent();
	}

	private void initComponent() {
		this.add(createInputParametersPanel());
	}

	private Component createInputParametersPanel() {
		return new InputParametersPanel(getInputParameters());
	}

	private InputParameter[] getInputParameters() {
		this.inputParameters = new InputParameter[n];
		for (int i = 0; i < n; i++) {
			this.inputParameters[i] = createInputParameter(i);
		}
		return this.inputParameters;
	}

	private InputParameter createInputParameter(int i) {
		JComboBox<ComboBoxItem<T>> comboBox = new JComboBox<ComboBoxItem<T>>();
		getComboBoxItems().forEach(comboBox::addItem);
		String itemName = "Item " + (i+1);
		if(i < comboBox.getItemCount()) {
			comboBox.setSelectedIndex(i);
		}
		comboBox.addItemListener(this::itemSelectionChanged);
		
		return new InputParameter(itemName, comboBox, itemName);
	}

	private List<ComboBoxItem<T>> getComboBoxItems() {
		List<ComboBoxItem<T>> cmbItems = new ArrayList<>();
		for (T i : items) {
			cmbItems.add(new ComboBoxItem<T>(i, namingFunction.apply(i)));
		}
		
		return cmbItems;
	}

	private void itemSelectionChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED) {
			this.firePropertyChange(PROPERTY_SELECTION, null, getSelectedItems());
		}
	}

	/**
	 * Returns the list of <i>n</i> selected items.
	 * 
	 * @return the list of selected items.
	 */
	public List<T> getSelectedItems() {
		List<T> selectedItems = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			JComboBox<?> comboBox = (JComboBox<?>) this.inputParameters[i].getInput();
			selectedItems.add(items.get(comboBox.getSelectedIndex()));
		}
		return selectedItems;
	}
}
