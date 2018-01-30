package org.sing_group.gc4s.dialog.wizard.event;


/**
 * An abstract adapter class for receiving wizard step events. The methods in
 * this class are empty. This class exists as convenience for creating
 * {@code WizardStepListener} objects.
 *
 * @author hlfernandez
 *
 */
public class WizardStepAdapter implements WizardStepListener {

	@Override
	public void wizardStepCompleted(WizardStepEvent event) {
	}

	@Override
	public void wizardStepUncompleted(WizardStepEvent event) {
	}

	@Override
	public void wizardStepNextButtonTooltipChanged(String nextButtonTooltip) {
	}
}