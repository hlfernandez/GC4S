package org.sing_group.gc4s.demo;

import static java.util.Arrays.asList;
import static org.sing_group.gc4s.demo.DemoUtils.showComponent;

import javax.swing.event.ChangeEvent;

import org.sing_group.gc4s.filechooser.ExtensionFileFilter;
import org.sing_group.gc4s.filechooser.JMultipleFileChooserPanel;
import org.sing_group.gc4s.filechooser.JMultipleFileChooserPanelBuilder;
import org.sing_group.gc4s.filechooser.MultipleFileChooserListener;
import org.sing_group.gc4s.filechooser.SelectionMode;

/**
 * An example showing the use of {@link JMultipleFileChooserPanelBuilder} to
 * create a {@code JMultipleFileChooserPanel}.
 * 
 * @author hlfernandez
 *
 */
public class JMultipleFileChooserPanelBuilderDemo {
	public static void main(String[] args) {
		JMultipleFileChooserPanel fileChooserPanel = 
			JMultipleFileChooserPanelBuilder.createOpenJMultipleFileChooserPanel()
				.withFileChooserSelectionMode(SelectionMode.FILES)
				.withFileFilters(asList(
					new ExtensionFileFilter(".*\\.csv", "CSV files", false),
					new ExtensionFileFilter(".*\\.txt", "TXT files")
				))
				.withAllowAllFilter(true)
			.build();
		
		fileChooserPanel
			.addFileChooserListener(new MultipleFileChooserListener() {

				@Override
				public void onFileChoosed(ChangeEvent event) {
					fileSelectionChanged();
				}

				@Override
				public void onFileRemoved(ChangeEvent event) {
					fileSelectionChanged();
				}

				private void fileSelectionChanged() {
					System.err.println("\nSelected files list:");
					fileChooserPanel.getSelectedFiles().forEach(f -> {
						System.err.println("\t - " + f);
					});
				}
			});
			
		showComponent(fileChooserPanel);
	}
}
