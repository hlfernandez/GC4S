/*
 * #%L
 * GC4S components
 * %%
 * Copyright (C) 2014 - 2018 Hugo López-Fernández, Daniel Glez-Peña, Miguel Reboiro-Jato, 
 * 			Florentino Fdez-Riverola, Rosalía Laza-Fidalgo, Reyes Pavón-Rial
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.sing_group.gc4s.input.list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.InvalidClassException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTextField;
import org.sing_group.gc4s.utilities.builder.JButtonBuilder;
import org.sing_group.gc4s.utilities.builder.JToggleButtonBuilder;

/**
 * This class encloses a {@link JList} based on a
 * {@link ExtendedDefaultListModel} to provide common list functionalities
 * (moving elements, select all, clear selection, filtering elements).
 *
 * @author hlfernandez
 *
 */
public class JListPanel<E> extends JPanel {
	private static final long serialVersionUID = 1L;

	protected static final ImageIcon ICON_ARROW_DOWN = new ImageIcon(
		JListPanel.class.getResource("icons/down.png"));
	protected static final ImageIcon ICON_ARROW_UP = new ImageIcon(
		JListPanel.class.getResource("icons/up.png"));
	protected static final ImageIcon ICON_REMOVE = new ImageIcon(
		JListPanel.class.getResource("icons/remove.png"));
	protected static final ImageIcon ICON_CLEAR = new ImageIcon(
		JListPanel.class.getResource("icons/clear.png"));
	protected static final ImageIcon ICON_SELECT_ALL = new ImageIcon(
		JListPanel.class.getResource("icons/check.png"));

	private JList<E> list;
	private ExtendedDefaultListModel<E> listModel;
	private FilteredListModel<E> filteredListModel;
	private JButton btnMoveDown;
	private JButton btnMoveUp;
	private JButton btnRemove;
	private JButton btnClearSelection;
	private JButton btnSelectAll;
	private JToggleButton regexButton;
	private JPanel northPanel;
	private JPanel buttonsPanel;
	private JPanel filterPanel;
	private JTextField filterTextField;
	private AbstractAction actionMoveDown;
	private AbstractAction actionMoveUp;
	private AbstractAction actionRemoveElements;
	private AbstractAction actionSelectAll;
	private AbstractAction actionClearSelection;
	private boolean buttons;
	private boolean filter;

  private JPanel userActions;

	/**
	 * Constructs a {@code JListPanel} within the button actions and the
	 * filter visible.
	 *
	 * @param list
	 *            a JList that uses a {@link ExtendedDefaultListModel}.
	 *
	 * @throws InvalidClassException
	 *             if list's model is not an {@code ExtendedDefaultModel}.
	 */
	public JListPanel(JList<E> list) throws InvalidClassException{
		this(list, true, true);
	}

	/**
	 * Constructs a {@code JListPanel}.
	 *
	 * @param list
	 *            a JList that uses a {@link ExtendedDefaultListModel}.
	 * @param buttons
	 *            if true, the action buttons are showed.
	 * @param filter
	 *            if true, the list filter is shown.
	 *
	 * @throws InvalidClassException
	 *             if list's model is not an {@code ExtendedDefaultModel}.
	 */
	public JListPanel(JList<E> list, boolean buttons, boolean filter)
		throws InvalidClassException
	{
		super();
		this.list = list;
		if(!(list.getModel() instanceof ExtendedDefaultListModel)){
			throw new InvalidClassException("List should have a ExtendedDefaultModel");
		}
		this.listModel = (ExtendedDefaultListModel<E>) list.getModel();
		this.filteredListModel = new FilteredListModel<E>(this.listModel);
		this.list.setModel(this.filteredListModel);
		this.buttons = buttons;
		this.filter = filter;
		initComponent();
	}

	private void initComponent() {
		this.setLayout(new BorderLayout());
		this.add(this.getNorthPane(), BorderLayout.NORTH);
		this.add(new JScrollPane(list), BorderLayout.CENTER);
		this.list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				checkActionButtons();
			}
		});
		this.listModel.addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
				checkActionButtons();
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				checkActionButtons();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				checkActionButtons();
			}
		});
	}

	private Component getNorthPane() {
		if(this.northPanel == null){
			this.northPanel = new JPanel();
			this.northPanel.setLayout(new GridLayout(0,1));
			getButtonsPane();
			if (buttons) {
				this.northPanel.add(getButtonsPane());
			}
			if (filter) {
				this.northPanel.add(getFilterPane());
			}
		}
		return this.northPanel;
	}

	private Component getFilterPane() {
		if(this.filterPanel == null){
			this.filterPanel = new JPanel(new BorderLayout());

			filterTextField = new JXTextField("Filter", Color.LIGHT_GRAY);
			this.filterPanel.add(filterTextField, BorderLayout.CENTER);
			filterTextField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent arg0) {
				}

				@Override
				public void insertUpdate(DocumentEvent arg0) {
					filterChanged();
				}

				@Override
				public void removeUpdate(DocumentEvent arg0) {
					filterChanged();
				}
			});

			regexButton = JToggleButtonBuilder
				.newJToggleButton()
					.withLabel("(.*)")
					.withSelectedIcon(null)
					.withUnselectedIcon(null)
					.setSelected(false)
					.withTooltip("Select this option to apply the filter as a regular expression")
					.thatDoes((e) -> this.filterChanged())
				.build();

			this.filterPanel.add(regexButton, BorderLayout.EAST);

		}
		return this.filterPanel;
	}

	protected void filterChanged() {
		filteredListModel.setFilter(new FilteredListModel.Filter() {
			public boolean accept(Object element) {

				String string = element.toString();
				String filter = filterTextField.getText();

				if (!regexButton.isSelected()) {
					if (string.contains(filter))
						return true;
				} else {
					try {
						if (string.matches(".*" + filter + ".*"))
							return true;
					} catch (java.util.regex.PatternSyntaxException ex) {
						return false;
					}
				}

				return false;
			}
		});
	}

	private Component getButtonsPane() {
		if (this.buttonsPanel == null) {
			this.buttonsPanel = new JPanel();
			BoxLayout layout = new BoxLayout(this.buttonsPanel, BoxLayout.X_AXIS);
			this.buttonsPanel.setLayout(layout);

			actionMoveDown = new AbstractAction("Move down", ICON_ARROW_DOWN) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					moveDownSelectedElement();
				}
			};
			actionMoveUp = new AbstractAction("Move up", ICON_ARROW_UP) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					moveUpSelectedElement();
				}
			};
			actionRemoveElements = new AbstractAction("Remove", ICON_REMOVE) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					removeSelectedElements();
				}
			};
			actionClearSelection = new AbstractAction("Clear selection", ICON_CLEAR) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					clearSelection();
				}
			};
			actionSelectAll = new AbstractAction("Select all", ICON_SELECT_ALL) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					selectAll();
				}
			};

			btnMoveDown = JButtonBuilder.newJButtonBuilder()
				.thatDoes(actionMoveDown).withText("")
				.withTooltip("Moves down the selected element")
				.disabled()
				.build();
			
			btnMoveUp = JButtonBuilder.newJButtonBuilder()
				.thatDoes(actionMoveUp).withText("")
				.withTooltip("Moves up the selected element")
				.disabled()
				.build();

			btnRemove = JButtonBuilder.newJButtonBuilder()
				.thatDoes(actionRemoveElements).withText("")
				.withTooltip("Removes the selected elements")
				.disabled()
				.build();

			btnSelectAll = JButtonBuilder.newJButtonBuilder()
				.thatDoes(actionSelectAll).withText("")
				.withTooltip("Select all the elements")
				.build();
			
			btnSelectAll.setEnabled(this.listModel.getSize() > 0);

			btnClearSelection = JButtonBuilder.newJButtonBuilder()
				.thatDoes(actionClearSelection).withText("")
				.withTooltip("Clear the current selection")
				.disabled()
				.build();

			buttonsPanel.add(Box.createHorizontalGlue());
			buttonsPanel.add(btnMoveDown);
			buttonsPanel.add(btnMoveUp);
			buttonsPanel.add(btnRemove);
			buttonsPanel.add(btnSelectAll);
			buttonsPanel.add(btnClearSelection);
			buttonsPanel.add(getUserActionsPanel());
			buttonsPanel.add(Box.createHorizontalGlue());
		}
		return buttonsPanel;
	}

	private JPanel getUserActionsPanel() {
		userActions = new JPanel();
		BoxLayout layout = new BoxLayout(userActions, BoxLayout.X_AXIS);
		userActions.setLayout(layout);

		return userActions;
	}

	private void moveDownSelectedElement() {
		if (listModel.moveDown(list.getSelectedIndex())) {
			list.setSelectedIndex(list.getSelectedIndex() + 1);
		}
	}

	private void moveUpSelectedElement() {
		if (listModel.moveUp(list.getSelectedIndex())) {
			list.setSelectedIndex(list.getSelectedIndex() - 1);
		}
	}

	private void removeSelectedElements() {
		list.getSelectedValuesList().forEach(e -> {
			this.listModel.removeElement(e);
		});
		this.list.clearSelection();
		this.list.updateUI();
	}

	private void checkActionButtons() {
		int selectedIndexesCount = this.list.getSelectedIndices().length;
		boolean moveEnabled = selectedIndexesCount == 1
				&& this.listModel.getSize() > 1;
		btnMoveUp.setEnabled(moveEnabled);
		btnMoveDown.setEnabled(moveEnabled);
		btnClearSelection.setEnabled(selectedIndexesCount > 0);
		btnRemove.setEnabled(selectedIndexesCount > 0);
		btnSelectAll.setEnabled(
			selectedIndexesCount < this.listModel.getSize() &&
			this.listModel.getSize() > 0
		);
	}

	private void clearSelection() {
		this.list.clearSelection();
	}

	private void selectAll() {
		int size = this.listModel.getSize();
		int[] selectedIndexes = new int[size];
		for (int i = 0; i < size; i++) {
			selectedIndexes[i] = i;
		}
		this.list.setSelectedIndices(selectedIndexes);
	}

	/**
	 * Returns the move up button.
	 *
	 * @return the move up button.
	 */
	public JButton getBtnMoveUp() {
		return btnMoveUp;
	}

	/**
	 * Returns the move down button.
	 *
	 * @return the move down button.
	 */
	public JButton getBtnMoveDown() {
		return btnMoveDown;
	}

	/**
	 * Returns the remove elements button.
	 *
	 * @return the remove elements button.
	 */
	public JButton getBtnRemoveElements() {
		return btnRemove;
	}

	/**
	 * Returns the clear selection button.
	 *
	 * @return the clear selection button.
	 */
	public JButton getBtnClearSelection() {
		return btnClearSelection;
	}

	/**
	 * Returns the select all button.
	 *
	 * @return the select all button.
	 */
	public JButton getBtnSelectAll() {
		return btnSelectAll;
	}

	/**
	 * Returns the move down action.
	 *
	 * @return the move down action.
	 */
	public AbstractAction getActionMoveUp() {
		return actionMoveUp;
	}

	/**
	 * Returns the move down action.
	 *
	 * @return the move down action.
	 */
	public AbstractAction getActionMoveDown() {
		return actionMoveDown;
	}

	/**
	 * Returns the remove elements action.
	 *
	 * @return the remove elements action.
	 */
	public AbstractAction getActionRemoveElements() {
		return actionRemoveElements;
	}

	/**
	 * Returns the clear selection action.
	 *
	 * @return the clear selection action.
	 */
	public AbstractAction getActionClearSelection() {
		return actionClearSelection;
	}

	/**
	 * Returns the select all action.
	 *
	 * @return the select all action.
	 */
	public AbstractAction getActionSelectAll() {
		return actionSelectAll;
	}

	/**
	 * Returns the wrapped {@code JList}.
	 *
	 * @return the wrapped {@code JList}.
	 */
	public JList<E> getList() {
		return list;
	}

	/**
	 * Adds a button with the specified action.
	 *
	 * @param a the button action
	 */
	public void addAction(Action a) {
		addAction(a, null);
	}

	/**
	 * Adds a button with the specified action.
	 *
	 * @param a the button action
	 * @param tooltip the button tooltip
	 */
	public void addAction(Action a, String tooltip) {
		userActions.add(
			JButtonBuilder.newJButtonBuilder()
				.thatDoes(a).withText("").withTooltip(tooltip)
			.build()
		);
	}
}
