// An example to show the usage of the StatisticsTestTable with a Dataset of
// numerical values with three conditions and forty two samples. In this case, a
// One-way ANOVA test (implemented by the OneWayAnovaTest class) is used to
// compute the p-values.
/*
 * #%L
 * GC4S statistics tests table demo
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
package org.sing_group.org.gc4s.statistics.table;

import static org.sing_group.gc4s.visualization.VisualizationUtils.showComponent;
import static org.sing_group.org.gc4s.statistics.data.util.StatisticsTestsDataUtils.randomValues;
import static org.sing_group.org.gc4s.statistics.table.TableDemoUtils.PROGRESS_EVENT_LISTENER;
import static org.sing_group.org.gc4s.statistics.table.TableDemoUtils.conditionNames;
import static org.sing_group.org.gc4s.statistics.table.TableDemoUtils.features;
import static org.sing_group.org.gc4s.statistics.table.TableDemoUtils.samples;

import java.awt.Color;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.sing_group.org.gc4s.statistics.data.Dataset;
import org.sing_group.org.gc4s.statistics.data.DefaultDataset;
import org.sing_group.org.gc4s.statistics.data.tests.FdrCorrection;
import org.sing_group.org.gc4s.statistics.data.tests.OneWayAnovaTest;
import org.sing_group.org.gc4s.statistics.data.tests.PValuesCorrection;
import org.sing_group.org.gc4s.statistics.data.tests.Test;
import org.sing_group.org.gc4s.statistics.table.ui.ConditionsSeparatorHighlighter;
import org.sing_group.org.gc4s.statistics.table.ui.NumberHighlighter;
import org.sing_group.org.gc4s.statistics.table.ui.StatisticsTestTableHeaderRenderer;

public class ThreeConditionsMeasurementsTableDemo {
	public static void main(String[] args) {
		// First, the data structures needed to create a Dataset of numbers are
		// created. These structures are: feature names, sample names and
		// conditions and a random matrix of values.
		int nFeatures = 40;
		int nSamples = 42;
		int nConditions = 3;

		String[] features = features(nFeatures);
		String[] samples = samples(nSamples);

		final Integer[][] data = new Integer[nFeatures][nSamples];
		for (int i = 0; i < nFeatures; i++) {
			List<Integer> randomValues = randomValues(nSamples, i);
			data[i] = randomValues.toArray(new Integer[nSamples]);
		}

		String[] conditionNames = conditionNames(nSamples, nConditions);

		// Then, the Dataset of Number required by the table is instantiated
		// using the data created before.
		Dataset<Number> dataset = new DefaultDataset<Number>(features, samples,
			data, conditionNames);

		// After that, it is created the statistical test of Number which is
		// also required by the table. GC4S provides the One-way ANOVA test for
		// multiple conditions, but it is possible to provide custom tests by
		// implementing Test<Number> (or Test<Integer>, Test<Double>, and so
		// on).
		Test<Number> test = new OneWayAnovaTest();
		PValuesCorrection correction = new FdrCorrection();

		// And now, the table itself is instantiated. Note that the correction
		// object is optional, if it is not provided, then the q-values column
		// is not shown.
		StatisticsTestTable<Number> table =
			new StatisticsTestTable<>(dataset, test, correction);

		// And finally, the table is configured to set some options and add
		// renderers and highlighters.
		table.disableColumVisibilityActions();

		// A ProgressEventListener is added to be notified about the progress of
		// the computation of the p-values.
		table.addProgressEventListener(PROGRESS_EVENT_LISTENER);

		// A StatisticsTestTableHeaderRenderer is set in the header in order to
		// draw the names of the samples in different colors depending on their
		// conditions.
		table.getTableHeader().setDefaultRenderer(
			new StatisticsTestTableHeaderRenderer(
				table.getTableHeader().getDefaultRenderer(), 1)
		);

		// Two highlighters are set. The ConditionsSeparatorHighlighter
		// draws a left border at the first sample of each condition to enhance
		// the distinction between conditions. The NumberHighlighter assigns
		// colors to table values by creating a gradient (like in heat map
		// representations). Note that it is possible to specify the colors of
		// the gradient or let the highlighter use the default ones.
		table.setHighlighters(
			new ConditionsSeparatorHighlighter<>(table),
			new NumberHighlighter(table, Color.RED, Color.GREEN)
		);

		// After applying the necessary configurations, the table is shown as a
		// part of a JScrollPane to make the header visible.
		showComponent(new JScrollPane(table), JFrame.MAXIMIZED_BOTH);
	}
}
