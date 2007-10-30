/*
 * FontFilterField.java
 *
 * Created on 2007-10-15, 21:05:38
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jidesoft.list.GroupableListModel;
import com.jidesoft.list.QuickGroupableListFilterField;
import java.awt.Font;


public class FontFilterField extends QuickGroupableListFilterField {

    public FontFilterField() {
        super();
    }

    public FontFilterField(GroupableListModel listModel) {
        super(listModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String convertElementToString(Object element) {
        if(element instanceof Font) {
            return ((Font)element).getName();
        }
        return super.convertElementToString(element);
    }

}
