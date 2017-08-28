/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule;

import org.mule.transformer.types.TypedValue;

import java.util.HashMap;
import java.util.Map;

/**
 * This is class to propagate attributes that must not be in the
 * inbound properties of a message.
 *
 */
public class DefaultMessageAttributes implements MessageAttributes
{
    private Map<String, TypedValue> attributes = new HashMap<String, TypedValue>();

    public TypedValue getAttribute(String attributeName)
    {
        return attributes.get(attributeName);
    }
    
    public void addAttribute(String attributeName, TypedValue attributeValue)
    {
        attributes.put(attributeName, attributeValue);
    }
}
