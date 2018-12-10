package com.jidesoft.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This interface is used by several PersistenceUtils classes inside JIDE to register a callback when writing or reading
 * the xml element.
 */
public interface PersistenceUtilsCallback {
    public static interface Save {
        /**
         * This method is called when writing the object to the element.
         *
         * @param document the XML document
         * @param element  the element representing the object
         * @param object   the object to be saved.
         */
        public void save(Document document, Element element, Object object);
    }

    public static interface Load {
        /**
         * This method is called when reading the object from the element.
         *
         * @param document the XML document
         * @param element  the element representing the object
         * @param object   the object to be written.
         */
        public void load(Document document, Element element, Object object);
    }
}
