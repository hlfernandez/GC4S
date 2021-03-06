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
package org.sing_group.gc4s.visualization.heatmap;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.Box.createHorizontalGlue;
import static javax.swing.Box.createHorizontalStrut;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.SwingUtilities.getWindowAncestor;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_COLUMN_16;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_EDIT_16;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_FONT_16;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_IMAGE_16;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_PAINT_16;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_RANGE_16;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_ROW_16;
import static org.sing_group.gc4s.visualization.heatmap.JHeatMapOperations.center;
import static org.sing_group.gc4s.visualization.heatmap.JHeatMapOperations.transform;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;

import org.sing_group.gc4s.dialog.ColorsSelectionDialog;
import org.sing_group.gc4s.dialog.FontConfigurationDialog;
import org.sing_group.gc4s.dialog.ListSelectionDialog;
import org.sing_group.gc4s.input.DoubleRange;
import org.sing_group.gc4s.input.DoubleRangeInputDialog;
import org.sing_group.gc4s.ui.ColorListCellRenderer;
import org.sing_group.gc4s.ui.menu.HamburgerMenu;
import org.sing_group.gc4s.utilities.ExtendedAbstractAction;
import org.sing_group.gc4s.visualization.heatmap.JHeatMapOperations.Centering;
import org.sing_group.gc4s.visualization.heatmap.JHeatMapOperations.Transform;

/**
 * A {@code JHeatMapPanel} wraps a {@code JHeatMap}, adding a toolbar with
 * options to manipulate it.
 *
 * @author hlfernandez
 * @see JHeatMap
 */
public class JHeatMapPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	protected static final String DESCRIPTION_RANGE_DIALOG =
		"This dialog allows you to select the minimum and maximum values used "
		+ "to create the color gradient.";
	protected static final String DESCRIPTION_FONT_DIALOG =
		"This dialog allows you to select the style and size of the font used "
		+ "in the heatmap.";
	protected static final String DESCRIPTION_VISIBLE_ROWS =
		"This dialog allows you to configure the visible rows. To do so, move "
		+ "them from one list to another using the controls.";
	protected static final String DESCRIPTION_VISIBLE_COLUMNS =
		"This dialog allows you to configure the visible columns. To do so, move "
			+ "them from one list to another using the controls.";

	private Color[] colors = new Color[] {Color.RED, Color.GREEN, Color.BLUE };
	private JComboBox<Color> lowColorCB;
	private JComboBox<Color> highColorCB;

	private JHeatMap heatmap;
	private Optional<Font> heatmapFont = empty();

	/**
	 * Constructs a new {@code JHeatMapPanel} wrapping {@code heatmap}.
	 *
	 * @param heatmap
	 *            a {@code JHeatMap}.
	 */
	public JHeatMapPanel(JHeatMap heatmap) {
		super(new BorderLayout());

		this.heatmap = heatmap;
		this.initComponent();
	}

	private void initComponent() {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

		JPanel toolbar = new JPanel();
		toolbar.setBorder(createEmptyBorder(2, 2, 2, 2));
		BoxLayout layout = new BoxLayout(toolbar, X_AXIS);
		toolbar.setLayout(layout);

		toolbar.add(getMenu());

		this.checkInitialColors();

		lowColorCB = new JComboBox<Color>(colors);
		lowColorCB.setRenderer(new ColorListCellRenderer());
		fixComboSize(lowColorCB);
		lowColorCB.setSelectedItem(this.heatmap.getLowColor());
		lowColorCB.addItemListener(e -> {
			heatmap.setLowColor(((Color) lowColorCB.getSelectedItem()));
		});

		toolbar.add(createHorizontalGlue());
		toolbar.add(new JLabel("Low: "));
		toolbar.add(lowColorCB);

		highColorCB = new JComboBox<Color>(colors);
		fixComboSize(highColorCB);
		highColorCB.setRenderer(new ColorListCellRenderer());
		highColorCB.setSelectedItem(this.heatmap.getHighColor());
		highColorCB.addItemListener(e -> {
			heatmap.setHighColor(((Color) highColorCB.getSelectedItem()));
		});

		toolbar.add(createHorizontalStrut(10));
		toolbar.add(new JLabel("High: "));
		toolbar.add(highColorCB);

		toolbar.add(createHorizontalStrut(10));

		this.add(toolbar, NORTH);
		this.add(heatmap, CENTER);
	}

	private void checkInitialColors() {
		List<Color> colorsList = new LinkedList<>(asList(this.colors));
		if (!colorsList.contains(this.heatmap.getLowColor())) {
			colorsList.add(this.heatmap.getLowColor());
		}
		if (!colorsList.contains(this.heatmap.getHighColor())) {
			colorsList.add(this.heatmap.getHighColor());
		}
		this.colors = colorsList.toArray(new Color[colorsList.size()]);
	}

	private Component getMenu() {
		HamburgerMenu menu = new HamburgerMenu(HamburgerMenu.Size.SIZE16);

		menu.add(new ExtendedAbstractAction(
			"Set range", ICON_RANGE_16, this::setHeatmapRange
		));

		menu.add(new ExtendedAbstractAction(
			"Transform data", ICON_EDIT_16, this::transformDataMatrix
		));

		menu.add(new ExtendedAbstractAction(
			"Visible rows", ICON_ROW_16, this::editVisibleRows
		));

		menu.add(new ExtendedAbstractAction(
			"Visible columns", ICON_COLUMN_16, this::editVisibleColumns
		));

		menu.add(new ExtendedAbstractAction(
			"Configure font", ICON_FONT_16, this::configureFont
		));
		menu.add(new ExtendedAbstractAction(
			"Edit colors", ICON_PAINT_16, this::editColors
		));

		menu.add(new ExtendedAbstractAction(
			"Export heatmap as image", ICON_IMAGE_16, this::exportAsImage
		));

		return menu;
	}

	protected void editVisibleRows() {
		List<String> visible 	= this.heatmap.getVisibleRowNames();
		List<String> notVisible = this.heatmap.getRowNames();
		notVisible.removeAll(visible);

		ListSelectionDialog<String> dialog;
		try {
			dialog = new ListSelectionDialog<String>(
				getDialogParent(), visible, notVisible, "Visible rows",
				"Not visible rows"
			) {
				private static final long serialVersionUID = 1L;

				public String getDescription() {
					return DESCRIPTION_VISIBLE_ROWS;
				}
			};
			dialog.setVisible(true);

			if (!dialog.isCanceled()) {
				this.heatmap.setVisibleRowNames(dialog.getSelectedItems());
			}
		} catch (InvalidClassException e) {
			throw new RuntimeException(e);
		}
	}

	private Window getDialogParent() {
		return getWindowAncestor(this);
	}

	protected void editVisibleColumns() {
		List<String> visible 	= this.heatmap.getVisibleColumnNames();
		List<String> notVisible = this.heatmap.getColumnNames();
		notVisible.removeAll(visible);

		ListSelectionDialog<String> dialog;
		try {
			dialog = new ListSelectionDialog<String>(
				getDialogParent(), visible, notVisible, "Visible columns",
				"Not visible columns"
			){
				private static final long serialVersionUID = 1L;

				public String getDescription() {
					return DESCRIPTION_VISIBLE_COLUMNS;
				}
			};

			dialog.setVisible(true);

			if (!dialog.isCanceled()) {
				this.heatmap.setVisibleColumnNames(dialog.getSelectedItems());
			}
		} catch (InvalidClassException e) {
			throw new RuntimeException(e);
		}
	}

	protected void setHeatmapRange() {
		DoubleRangeInputDialog dialog = new DoubleRangeInputDialog(
			getDialogParent(),
			new DoubleRange(heatmap.getLowValue(), heatmap.getHighValue())
		) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getDescription() {
				return DESCRIPTION_RANGE_DIALOG;
			}
		};
		dialog.setVisible(true);

		if(!dialog.isCanceled()) {
			heatmap.setValuesRange(dialog.getSelectedRange());
		}
	}

	protected void exportAsImage() {
		JFileChooser fc = new JFileChooser();
		int result = fc.showSaveDialog(JHeatMapPanel.this);
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				heatmap.toPngImage(fc.getSelectedFile());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	protected void configureFont() {
		FontConfigurationDialog dialog = new FontConfigurationDialog(
			getDialogParent(), getHeatmapFont()
		) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getDescription() {
				return DESCRIPTION_FONT_DIALOG;
			}
		};
		dialog.setVisible(true);

		if(!dialog.isCanceled()) {
			this.setHeatmapFont(dialog.getSelectedFont());
		}
	}

	private void setHeatmapFont(Font font) {
		this.heatmapFont = ofNullable(font);
		this.heatmap.setHeatmapFont(getHeatmapFont());
	}

	protected void editColors() {
		ColorsSelectionDialog dialog = new ColorsSelectionDialog(
			getDialogParent(), 2, Integer.MAX_VALUE,
			asList(this.colors)
		);

		dialog.setVisible(true);

		if (!dialog.isCanceled()) {
			this.setColors(dialog.getSelectedColors());
		}
	}

	private void setColors(List<Color> selectedColors) {
		this.colors = selectedColors.toArray(new Color[selectedColors.size()]);
		Color previousLowColor = (Color) lowColorCB.getSelectedItem();
		Color previousHighColor = (Color) highColorCB.getSelectedItem();

		lowColorCB.setModel(new DefaultComboBoxModel<>(colors));
		highColorCB.setModel(new DefaultComboBoxModel<>(colors));

		boolean changeColors = false;
		if (selectedColors.contains(previousLowColor)) {
			lowColorCB.setSelectedItem(previousLowColor);
		} else {
			changeColors = true;
		}

		if (selectedColors.contains(previousHighColor)) {
			highColorCB.setSelectedItem(previousHighColor);
		} else {
			changeColors = true;
		}

		if (changeColors) {
			this.heatmap.setColors(
				(Color) lowColorCB.getSelectedItem(),
				(Color) highColorCB.getSelectedItem()
			);
		}
	}

	/**
	 * Returns the heat map font.
	 *
	 * @return the heat map font
	 */
	public Font getHeatmapFont() {
		return this.heatmapFont.orElse(this.getFont());
	}

	private void fixComboSize(JComboBox<Color> combobox) {
		Dimension d = new Dimension(120, 20);
		combobox.setSize(d);
		combobox.setMaximumSize(d);
		combobox.setPreferredSize(d);
	}

	private void transformDataMatrix() {
		JHeatMapDataOperationsDialog dialog =
			new JHeatMapDataOperationsDialog(getDialogParent());
		dialog.setVisible(true);

		if(!dialog.isCanceled()) {
			applyTransformations(dialog.getTransform(), dialog.getCentering());
		}
	}

	private void applyTransformations(Transform transform, Centering centering) {
		this.heatmap.setData(
			center(
				transform(this.heatmap.getData(), transform), centering, true)
		);
	}
}
