/*
 * // Copyright 2019 The OpenSDS Authors.
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 * // not use this file except in compliance with the License. You may obtain
 * // a copy of the License at
 * //
 * //     http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * // License for the specific language governing permissions and limitations
 * // under the License.
 *
 */

package org.opensds.vasa.vasa.util;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 提取公共的Xml操作
 *
 * @author l00102779
 * @version [版本号V001R010C00, 2011-12-14]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class XmlUtils {
    private XmlUtils() {
    }

    /**
     * 解析<code>parent</code>节点下的<code>childTagName</code>
     * 内容。
     * <p>
     * 如<parent><child>test</child></parent>
     * 调用改方法则返回test。
     *
     * @param parent       ：父节点
     * @param childTagName 方法参数：childTagName
     * @return String 返回结果
     */
    public static String getTextContentOfChild(Element parent,
                                               String childTagName) {
        String result = null;
        Node node = parent.getFirstChild();
        Element element = null;
        String tagName = null;
        String content = null;
        while (null != node) {
            if (!(node instanceof Element)) {
                node = node.getNextSibling();
                continue;
            }

            element = (Element) node;
            tagName = element.getTagName();
            content = element.getTextContent();
            if (childTagName.equals(tagName)) {
                result = content.trim();
                break;
            }

            node = node.getNextSibling();
        }

        return result;
    }

    /**
     * 获取<code>parent</code>节点下的所有
     * 节点名为childTagName的节点。
     *
     * @param parent       方法参数：parent
     * @param childTagName 方法参数：childTagName
     * @return Collection<Element> ：
     */
    public static Collection<Element> getChildren(Element parent,
                                                  String childTagName) {
        Collection<Element> children = new ArrayList<Element>(0);
        if (null == parent) {
            return children;
        }
        Node child = parent.getFirstChild();

        //遍历parent节点的所有子节点
        Element element = null;
        String tagName = null;
        while (null != child) {
            if (child instanceof Element) {
                element = (Element) child;
                tagName = element.getTagName();

                /*childTagName不为null，则取标签为childTagName的子节点
                                               否则取parent的所有子节点*/
                if (null != childTagName) {
                    if (childTagName.equals(tagName)) {
                        children.add(element);
                    }
                } else {
                    children.add(element);
                }
            }

            child = child.getNextSibling();
        }

        return children;
    }

    /**
     * 获取<code>parent</code>节点下的
     * 节点名为childTagName的节点。
     *
     * @param parent       方法参数：parent
     * @param childTagName 方法参数：childTagName
     * @return Element 返回结果
     */
    public static Element getChild(Element parent, String childTagName) {
        Element result = null;
        Node child = parent.getFirstChild();

        //遍历parent的所有子节点
        Element element = null;
        String tagName = null;
        while (null != child) {
            if (child instanceof Element) {
                element = (Element) child;
                tagName = element.getTagName();
                if (tagName.equals(childTagName)) {
                    result = element;
                    break;
                }
            }

            child = child.getNextSibling();
        }

        return result;
    }

    /**
     * 返回<code>parent</code>节点下的所有的子节点。
     *
     * @param parent 方法参数：parent
     * @return Collection<Element> 返回结果
     */
    public static Collection<Element> getChildren(Element parent) {
        return getChildren(parent, null);
    }


    /**
     * 获取属性
     *
     * @param element   方法参数：element
     * @param attribute 方法参数：attribute
     * @return String 返回结果
     * @see
     */
    public static String getAttribute(Element element, String attribute) {
        String result = element.getAttribute(attribute);
        if (null != result) {
            return result.trim();
        }

        return null;

    }
}
