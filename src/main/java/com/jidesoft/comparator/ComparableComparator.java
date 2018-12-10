/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Commons" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package com.jidesoft.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A Comparator that compares Comparable objects. Throws ClassCastExceptions if the objects are not
 * Comparable, or if they are null. Throws ClassCastException if the compareTo of both objects do
 * not provide an inverse result of each other as per the Comparable javadoc.  This Comparator is
 * useful, for example, for enforcing the natural order in custom implementations of SortedSet and
 * SortedMap. <br> If both objects are null, they will be treated as equal. If one is null and the
 * other is not, the null value will be treated as smaller then non-null value.
 *
 * @author bayard@generationjava.com
 * @author JIDE Software
 */
public class ComparableComparator implements Comparator<Object>, Serializable {
    private static final long serialVersionUID = -291439688585137865L;
  
    private static final ComparableComparator instance =
            new ComparableComparator();

    /**
     * Return a shared instance of a ComparableComparator.  Developers are encouraged to use the
     * comparator returned from this method instead of constructing a new instance to reduce
     * allocation and GC overhead when multiple comparable comparators may be used in the same VM.
     *
     * @return an instance of ComparableComparator.
     */
    public static ComparableComparator getInstance() {
        return instance;
    }
    
    /**
     * Constructs a ComparableComparator.
     */
    public ComparableComparator() {
    }

    @SuppressWarnings("unchecked")
    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        else if (o1 == null) {
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }

        if (o1 instanceof Comparable) {
            if (o2 instanceof Comparable) {
                int result1;
                int result2;
                try {
                    result1 = ((Comparable<Object>) o1).compareTo(o2);
                    result2 = ((Comparable<Object>) o2).compareTo(o1);
                }
                catch (ClassCastException e) {
                    return o1.getClass().getName().compareTo(o2.getClass().getName()); // fall back to compare the string
                }

                // enforce comparable contract
                if (result1 == 0 && result2 == 0) {
                    return 0;
                }
                else
                if (result1 < 0 && result2 > 0) { // to make sure the two results are consistent
                    return result1;
                }
                else
                if (result1 > 0 && result2 < 0) { // to make sure the two results are consistent
                    return result1;
                }
                else {
                    // results inconsistent
                    throw new ClassCastException("The two compareTo methods of o1 and o2 returned two inconsistent results. Please make sure sgn(x.compareTo(y)) == -sgn(y.compareTo(x)) for all x and y.");
                }
            }
            else {
                // o2 wasn't comparable
                throw new ClassCastException("The second argument of this method was not a Comparable: " + o2.getClass().getName());
            }
        }
        else if (o2 instanceof Comparable) {
            // o1 wasn't comparable
            throw new ClassCastException("The first argument of this method was not a Comparable: " + o1.getClass().getName());
        }
        else {
            // neither were comparable
            throw new ClassCastException("Both arguments of this method were not Comparables: " + o1.getClass().getName() + " and " + o2.getClass().getName());
        }
    }
}
