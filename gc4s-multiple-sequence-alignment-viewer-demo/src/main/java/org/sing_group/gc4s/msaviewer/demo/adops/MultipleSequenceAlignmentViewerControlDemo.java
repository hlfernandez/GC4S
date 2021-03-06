// This class show the usage of the MultipleSequenceAlignmentViewerControl 
// component to reproduce the aligned sequences viewer used in ADOPS 
// (http://sing-group.org/ADOPS/) to highlight bases where signals of positive 
// selection have been identified using CodeML.
/*
 * #%L
 * GC4S multiple sequence alignment viewer demo
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
package org.sing_group.gc4s.msaviewer.demo.adops;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.sing_group.gc4s.msaviewer.demo.Data.getRandomScores;
import static org.sing_group.gc4s.msaviewer.demo.Data.getRandomSequences;
import static org.sing_group.gc4s.visualization.VisualizationUtils.showComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;

import org.sing_group.gc4s.msaviewer.MultipleSequenceAlignmentTracksModel;
import org.sing_group.gc4s.msaviewer.MultipleSequenceAlignmentViewerConfiguration;
import org.sing_group.gc4s.msaviewer.MultipleSequenceAlignmentViewerControl;
import org.sing_group.gc4s.msaviewer.Sequence;
import org.sing_group.gc4s.msaviewer.SequenceAlignmentRenderer;
import org.sing_group.gc4s.msaviewer.Track;

public class MultipleSequenceAlignmentViewerControlDemo {
	public static void main(String[] args) {
		int sequenceLength = 120;

		// First, the list of Sequence objects that will be viewed with the
		// MultipleSequenceAlignmentViewerControl is created. In this case, an
		// auxiliary function is used to generate 6 sequences of the defined 
		// length.
		List<Sequence> sequences = getRandomSequences(6, sequenceLength);
		
		// Then, it is created a MultipleSequenceAlignmentTracksModel that will
		// be set to the viewer panel to show only one bottom track that
		// simulates scores (from 0 to 9) for each position. In ADOPS, these
		// scores are obtained from the aligned.score_ascii file generated using
		// T-COFFEE.
		MultipleSequenceAlignmentTracksModel model1 = new MultipleSequenceAlignmentTracksModel() {

			@Override
			public String getName() {
				return "PSS Model 1";
			}
			
			@Override
			public List<Track> getUpperTracks() {
				return emptyList();
			}

			@Override
			public List<Track> getBottomTracks() {
				return asList(new Track() {
					
					@Override
					public String getName() {
						return "Scores";
					}
					
					@Override
					public String getContent() {
						return getRandomScores(sequenceLength);
					}
				});
			}
		};

		// After that, it is created a map that assigns a Confidence object to
		// some bases (sequence positions) to use later in a
		// SequenceAlignmentRenderer. In ADOPS, these probabilities are
		// calculated using CodeML.
		final Map<Integer, Confidence> confidences1 = new TreeMap<>();
		confidences1.put(4, new Confidence(0.99d, 0.99d));
		confidences1.put(14, new Confidence(0.99d, 0.99d));
		confidences1.put(8, new Confidence(0.99d, 0.92d));
		confidences1.put(12, new Confidence(0.92d, 0.99d));
		confidences1.put(16, new Confidence(0.92d, 0.92d));

		// Now, a CodeMlPositiveSelectionSequenceAlignmentRenderer for the
		// previous map is created. This component highlights the bases with
		// signals of positive selection with different colors depending on the
		// values of the probabilities.
		SequenceAlignmentRenderer renderer1 = 
			new CodeMlPositiveSelectionSequenceAlignmentRenderer(confidences1);
		
		// Again, it is created another MultipleSequenceAlignmentTracksModel
		// that will be set to the viewer panel to show only one bottom track
		// that simulates scores (from 0 to 9) for each position. In ADOPS,
		// these scores are obtained from the aligned.score_ascii file generated
		// using T-COFFEE.
		MultipleSequenceAlignmentTracksModel model2 = new MultipleSequenceAlignmentTracksModel() {

			@Override
			public String getName() {
				return "PSS Model 2";
			}

			@Override
			public List<Track> getUpperTracks() {
				return emptyList();
			}

			@Override
			public List<Track> getBottomTracks() {
				return asList(new Track() {

					@Override
					public String getName() {
						return "Scores";
					}

					@Override
					public String getContent() {
						return getRandomScores(sequenceLength);
					}
				});
			}
		};

		// After that, it is created a map that assigns a Confidence object to
		// some bases (sequence positions) to use later in a
		// SequenceAlignmentRenderer.
		final Map<Integer, Confidence> confidences2 = new TreeMap<>();
		confidences2.put(5, new Confidence(0.99d, 0.99d));
		confidences2.put(14, new Confidence(0.99d, 0.99d));
		confidences2.put(9, new Confidence(0.99d, 0.92d));
		confidences2.put(12, new Confidence(0.92d, 0.99d));
		confidences2.put(16, new Confidence(0.92d, 0.92d));
		confidences2.put(17, new Confidence(0.92d, 0.96d));
		confidences2.put(26, new Confidence(0.92d, 0.92d));
		
		// Now, another CodeMlPositiveSelectionSequenceAlignmentRenderer for the
		// previous map is created.
		SequenceAlignmentRenderer renderer2 = 
			new CodeMlPositiveSelectionSequenceAlignmentRenderer(confidences2);

		// The following map is used to associate SequenceAlignmentRenderers to
		// MultipleSequenceAlignmentTracksModels.
		Map<MultipleSequenceAlignmentTracksModel, SequenceAlignmentRenderer> renderersMap = new HashMap<>();
		renderersMap.put(model1, renderer1);
		renderersMap.put(model2, renderer2);

		// Then, a MultipleSequenceAlignmentViewerConfiguration is created to
		// set the initial configuration of the viewer. This is optional since
		// the viewer panel can create a configuration with default values if it
		// is not provided.
		MultipleSequenceAlignmentViewerConfiguration configuration = 
			new MultipleSequenceAlignmentViewerConfiguration(
				10, 	// The length of the sequence label
				5,	// The number of tabs after the sequence label
				10, 	// The length of each block
				4, 	// The number of blocks per line
				16, 	// The font size
				true, 	// Whether position indexes must be shown or not
				true, 	// Whether upper tracks must be shown or not
				true 	// Whether bottom tracks must be shown or not
		);

		// And finally, the MultipleSequenceAlignmentViewerControl is
		// instantiated using the data structures previously created.
		MultipleSequenceAlignmentViewerControl viewerControl = 
			new MultipleSequenceAlignmentViewerControl(
				sequences, asList(model1, model2), renderersMap, configuration);

		showComponent(viewerControl, JFrame.MAXIMIZED_BOTH);
	}
}
