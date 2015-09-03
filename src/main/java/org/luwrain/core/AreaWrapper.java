
package org.luwrain.core;

/**
 * The interface to mark area wrapping classes. This interface is empty,
 * there are no any methods. It is used for easy checking that particular
 * area object is an area wrapper. (not a natural area).  Natural areas
 * are provided by applications, but usually they are wrapped by one or
 * more wrapping areas. One of them is used due to security reasons,
 * other may be used, for instance, for text search in the area.
 *
 * @see SecurityAreaWrapper SearchAreaWrapper
 */
interface AreaWrapper
{
}
