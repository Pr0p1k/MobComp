/*
 * $Id$
 */

package ru.ifmo.cs.elements;

/**
 *
 * @author Dmitry Afanasiev <KOT@MATPOCKuH.Ru>
 */
public class DataPart extends DataHandler {
	protected int startbit;

	public DataPart(int startbit, int width, DataStorage ... inputs) {
		super(width, inputs);

		this.startbit = startbit;
	}

	public DataPart(int startbit, DataStorage ... inputs) {
		this(startbit, 1, inputs);
	}

	@Override
	public void setValue(int value) {
		super.setValue(value >> startbit);
	}
}
