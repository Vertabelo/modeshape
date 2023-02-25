package org.modeshape.sequencer.ddl.dialect.mysql8;

import org.modeshape.sequencer.ddl.datatype.DataType;

/**
 * Definicja typu danych dla bazy MySQL.
 *  
 * @author Agnieszka Kozubek
 */
public class MySql8DataType extends DataType {
    
    public MySql8DataType() {
        super();
    }
    public MySql8DataType(String name, int precision, int scale) {
        super(name, precision, scale);
    }
    public MySql8DataType(String name, int length) {
        super(name, length);
    }
    public MySql8DataType(String theName) {
        super(theName);
    }

    private String characterSet;
    private String collate;
    private String onUpdate;

    public String getCharacterSet() {
        return characterSet;
    }
    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }
    public String getCollate() {
        return collate;
    }
    public void setCollate(String collate) {
        this.collate = collate;
    }
    public void setOnUpdate(String onUpdate) {
        this.onUpdate = onUpdate;
    }
    public String getOnUpdate() {
        return onUpdate;
    }
}
