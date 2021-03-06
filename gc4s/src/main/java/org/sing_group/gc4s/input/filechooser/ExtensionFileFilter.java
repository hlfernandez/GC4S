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
package org.sing_group.gc4s.input.filechooser;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.filechooser.FileFilter;

/**
 * A concrete implementation of {@link FileFilter} that provides the possibility
 * of filtering files based on a regular expression. It also allows choosing
 * whether the regular expression must be evaluated as case-sensitive or not.
 * 
 * @author hlfernandez
 *
 */
public class ExtensionFileFilter extends FileFilter {
	public static final boolean DEFAULT_CASE_SENSITIVE = true;
	private final String nameregexp;
	private final String description;
	private boolean caseSensitive;

	/**
	 * Creates a new {@code ExtensionFileFilter} instance with the regular
	 * expression and description specified. In this case, {@code regex} is 
	 * evaluated as case sensitive.
	 *  
	 * 
	 * @param regex the regular expression.
	 * @param description the filter description.
	 */
	public ExtensionFileFilter(String regex, String description) {
		this(regex, description, DEFAULT_CASE_SENSITIVE);
	}

	/**
	 * Creates a new {@code ExtensionFileFilter} instance with the regular
	 * expression and description specified. {@code caseSensitive} indicates
	 * whether the regular expression must be evaluated as case-sensitive or
	 * not.
	 * 
	 * @param regex the regular expression.
	 * @param description the filter description.
	 * @param caseSensitive whether the regular expression must be evaluated as
	 *            case-sensitive or not.
	 */
	public ExtensionFileFilter(String regex, String description,
		boolean caseSensitive
	) {
		this.nameregexp = requestValidRegex(regex);
		this.description = description;
		this.caseSensitive = caseSensitive;
	}

	private String requestValidRegex(String regex) {
		try {
			Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw new IllegalArgumentException(e);
		}
		return regex;
	}

	@Override
	public boolean accept(File pathname) {
		return (pathname.isDirectory()
			|| pathname.getName().matches(getRegex()));
	}

	private String getRegex() {
		return caseSensitive == true ? nameregexp : ("(?i)" + nameregexp);
	}	

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public boolean equals(Object aThat) {
		if (aThat == null) {
			return false;
		}
		if (!(aThat instanceof ExtensionFileFilter)) {
			return false;
		}

		ExtensionFileFilter that = (ExtensionFileFilter) aThat;

		return this.nameregexp.equals(that.nameregexp)
			&& this.description.equals(that.description)
			&& this.caseSensitive == that.caseSensitive;
	}
}