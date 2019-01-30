/*
 * $Id$
 */

package ru.ifmo.cs.elements;

/**
 *
 * @author Dmitry Afanasiev <KOT@MATPOCKuH.Ru>
 */
public class DummyValve extends DataHandler {
	private DataSource input;
	private int startbit;

	public DummyValve(DataSource input, int startbit, int width, DataSource ... ctrls) {
		super(width, ctrls);

		this.input = input;
		this.startbit = startbit;
	}

	public DummyValve(DataSource input, DataSource ... ctrls) {
		this(input, 0, input.getWidth(), ctrls);
	}

	@Override
	public void setValue(int ctrl) {
		super.setValue(input.getValue() >> startbit);
	}
}
