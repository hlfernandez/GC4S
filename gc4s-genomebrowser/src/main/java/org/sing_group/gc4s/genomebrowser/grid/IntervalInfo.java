/*
 * #%L
 * GC4S genome browser
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
package org.sing_group.gc4s.genomebrowser.grid;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * A {@code GenericInfo} implementation for providing interval information.
 * 
 * @author hlfernandez
 *
 */
public class IntervalInfo implements GenericInfo {
	private String data;
	private String start;
	private String stop;
	private Color color;

	/**
	 * Creates a new {@code IntervalInfo} started with the specified values.
	 * 
	 * @param data the interval data
	 * @param start the interval start
	 * @param end the interval end
	 */
	public IntervalInfo(String data, String start, String end) {
		this.data = data;
		this.start = start;
		this.stop = end;
		this.color = Color.LIGHT_GRAY;
	}

	/**
	 * Creates a new {@code IntervalInfo} started with the specified values.
	 * 
	 * @param data the interval data
	 * @param start the interval start
	 * @param end the interval end
	 * @param color the tooltip color
	 */
	public IntervalInfo(String data, String start, String end, Color color) {
		this.data = data;
		this.start = start;
		this.stop = end;
		this.color = color;
	}

	@Override
	public String toString() {
		return ("(" + this.start + "," + this.stop + ")=" + this.data);
	}

	@Override
	public Color getToolTipColor() {
		return this.color;
	}

	@Override
	public List<String> getLines() {
		return Arrays.asList(this.toString());
	}
}
