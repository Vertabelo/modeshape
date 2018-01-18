package org.modeshape.sequencer.ddl.dialect.mysql;

import org.modeshape.sequencer.ddl.datatype.DataType;

/**
 * Definicja typu danych dla bazy MySQL.
 *  
 * @author Agnieszka Kozubek
 */
public class MySqlDataType extends DataType {
    
    public MySqlDataType() {
        super();
    }
    public MySqlDataType(String name, int precision, int scale) {
        super(name, precision, scale);
    }
    public MySqlDataType(String name, int length) {
        super(name, length);
    }
    public MySqlDataType(String theName) {
        super(theName);
    }

    private String characterSet = null;
    private String collate = null;
    private boolean onUpdateCurrentTimestamp;
    
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
    public void setOnUpdateCurrentTimestamp(boolean onUpdateCurrentTimestamp) {
        this.onUpdateCurrentTimestamp = onUpdateCurrentTimestamp;
    }
    public boolean isOnUpdateCurrentTimestamp() {
        return onUpdateCurrentTimestamp;
    }
}
