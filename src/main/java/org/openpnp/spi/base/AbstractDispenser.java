package org.openpnp.spi.base;

import javax.swing.Icon;

import org.openpnp.gui.support.Icons;
import org.openpnp.model.Configuration;
import org.openpnp.spi.Dispenser;
import org.openpnp.spi.Head;
import org.simpleframework.xml.Attribute;

public abstract class AbstractDispenser extends AbstractHeadMountable implements Dispenser {
    @Attribute
    protected String id;

    @Attribute(required = false)
    protected String name;

    protected Head head;


    public AbstractDispenser() {
        this.id = Configuration.createId("DIS");
        this.name = getClass().getSimpleName();
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public Head getHead() {
        return head;
    }

    @Override
    public void setHead(Head head) {
        this.head = head;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        firePropertyChange("name", null, name);
    }

    @Override
    public Icon getPropertySheetHolderIcon() {
        return Icons.captureTool;
    }
}
