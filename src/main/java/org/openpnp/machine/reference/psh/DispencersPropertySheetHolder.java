package org.openpnp.machine.reference.psh;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.openpnp.Translations;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.components.ClassSelectionDialog;
import org.openpnp.gui.support.Icons;
import org.openpnp.gui.support.MessageBoxes;
import org.openpnp.model.Configuration;
import org.openpnp.spi.Dispenser;
import org.openpnp.spi.Head;
import org.openpnp.spi.PropertySheetHolder;
import org.openpnp.spi.base.SimplePropertySheetHolder;

public class DispencersPropertySheetHolder extends SimplePropertySheetHolder {
    final Head head;
    
    public DispencersPropertySheetHolder(Head head, String title, List<? extends PropertySheetHolder> children,
            Icon icon) {
        super(title, children, icon);
        this.head = head;
    }

    @Override
    public Action[] getPropertySheetHolderActions() {
        return new Action[] {newAction};
    }
    
    public Action newAction = new AbstractAction() {
        {
            putValue(SMALL_ICON, Icons.nozzleAdd);
            putValue(NAME, "New Dispencer...");
            putValue(SHORT_DESCRIPTION, Translations.getString(
                    "DispencersPropertySheetHolder.Action.NewDispencer")); //$NON-NLS-1$
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            Configuration configuration = Configuration.get();
            ClassSelectionDialog<Dispenser> dialog = new ClassSelectionDialog<>(MainFrame.get(),
                    Translations.getString(
                            "NozzlesPropertySheetHolder.SelectNozzleDialog.title"), //$NON-NLS-1$
                    Translations.getString("NozzlesPropertySheetHolder.SelectNozzleDialog.description"), //$NON-NLS-1$
                    configuration.getMachine().getCompatibleDispencerClasses());
            dialog.setVisible(true);
            Class<? extends Dispenser> cls = dialog.getSelectedClass();
            if (cls == null) {
                return;
            }
            try {
              Dispenser dispencer = cls.newInstance();

                head.addDispencer(dispencer);
            }
            catch (Exception e) {
                MessageBoxes.errorBox(MainFrame.get(), "Error", e);
            }
        }
    };
}
