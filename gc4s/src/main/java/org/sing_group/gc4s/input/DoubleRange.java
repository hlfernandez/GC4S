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
package org.sing_group.gc4s.input;

/**
 * This class represents a range (i.e. a pair of a minimun and a maximum) of
 * {@code double}.
 * 
 * @author hlfernandez
 *
 */
public class DoubleRange {

	private double min;
	private double max;

	/**
	 * Creates a new {@code DoubleRange}.
	 * 
	 * @param min the minimun value.
	 * @param max the maximum value.
	 */
	public DoubleRange(double min, double max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Return the minimum value of the range.
	 * @return the minimum value of the range.
	 */
	public double getMin() {
		return min;
	}

	/**
	 * Return the maximum value of the range.
	 * @return the maximum value of the range.
	 */
	public double getMax() {
		return max;
	}
	
	@Override
	public String toString() {
		return "[" + getMin() + ", " + getMax() + "]";
	}
}
