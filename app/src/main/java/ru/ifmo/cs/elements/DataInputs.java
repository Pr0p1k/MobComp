/*
 * $Id$
 */

package ru.ifmo.cs.elements;

/**
 *
 * @author Dmitry Afanasiev <KOT@MATPOCKuH.Ru>
 */
public class DataInputs extends DataWidth {
	public DataInputs(String name, int width, DataSource ... inputs) {
		super(name, width);

		for (DataSource input : inputs)
			if (input instanceof DataHandler)
				((DataHandler)input).addDestination((DataDestination)this);
	}
}
