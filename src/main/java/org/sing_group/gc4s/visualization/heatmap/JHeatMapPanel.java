package org.sing_group.gc4s.visualization.heatmap;

import static javax.swing.SwingUtilities.getWindowAncestor;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_COLUMN_16;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_EDIT_16;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_FONT_16;
import static org.sing_group.gc4s.ui.icons.Icons.ICON_IMAGE_16;
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
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;

import org.sing_group.gc4s.dialog.FontConfigurationDialog;
import org.sing_group.gc4s.dialog.ListSelectionDialog;
import org.sing_group.gc4s.input.DoubleRange;
import org.sing_group.gc4s.input.DoubleRangeInputDialog;
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

	private enum ComboColor {
		RED 	(Color.RED, 	"Red"),
		GREEN 	(Color.GREEN, 	"Green"),
		BLUE 	(Color.BLUE, 	"Blue");

		private Color color;
		private String name;

		ComboColor(Color color, String name) {
			this.color = color;
			this.name = name;
		}

		public Color getColor() {
			return color;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private JHeatMap heatmap;
	private Optional<Font> heatmapFont = Optional.empty();

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
		toolbar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		BoxLayout layout = new BoxLayout(toolbar, BoxLayout.X_AXIS);
		toolbar.setLayout(layout);

		toolbar.add(getMenu());

		JComboBox<ComboColor> lowColorCB =
			new JComboBox<ComboColor>(ComboColor.values());
		fixComboSize(lowColorCB);
		lowColorCB.setSelectedItem(ComboColor.GREEN);
		lowColorCB.addItemListener(e -> {
			heatmap.setLowColor(
				((ComboColor)lowColorCB.getSelectedItem()).getColor()
			);
		});

		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(new JLabel("Low: "));
		toolbar.add(lowColorCB);

		JComboBox<ComboColor> highColorCB =
			new JComboBox<ComboColor>(ComboColor.values());
		fixComboSize(highColorCB);
		highColorCB.setSelectedItem(ComboColor.RED);
		highColorCB.addItemListener(e -> {
			heatmap.setHighColor(
				((ComboColor)highColorCB.getSelectedItem()).getColor()
			);
		});

		toolbar.add(Box.createHorizontalStrut(10));
		toolbar.add(new JLabel("High: "));
		toolbar.add(highColorCB);

		toolbar.add(Box.createHorizontalStrut(10));

		this.add(toolbar, BorderLayout.NORTH);
		this.add(heatmap, BorderLayout.CENTER);
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
		this.heatmapFont = Optional.ofNullable(font);
		this.heatmap.setHeatmapFont(getHeatmapFont());
	}

	/**
	 * Returns the heat map font.
	 *
	 * @return the heat map font.
	 */
	public Font getHeatmapFont() {
		return this.heatmapFont.orElse(this.getFont());
	}

	private void fixComboSize(JComboBox<ComboColor> lowColorCB) {
		Dimension d = new Dimension(120, 20);
		lowColorCB.setSize(d);
		lowColorCB.setMaximumSize(d);
		lowColorCB.setPreferredSize(d);
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