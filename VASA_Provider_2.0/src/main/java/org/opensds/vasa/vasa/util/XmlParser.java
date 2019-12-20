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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * xml解析器的抽象类,通过此类选择不同类型的解析器
 *
 * @author f00102803
 * @version [版本号V001R010C00, 2011-12-14]
 * @see XmlDomParser
 * @since ISM Server:fileUtils
 */
public abstract class XmlParser {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(XmlParser.class);

    /**
     * 方法 ： getXmlDomParser
     *
     * @param is 方法参数：is
     * @return XmlParser 返回结果
     * @throws SAXException 异常：SAXException
     * @throws IOException  异常：IOException
     */
    public static XmlParser getXmlDomParser(InputStream is)
            throws SAXException, IOException {
        return new XmlDomParser(is);
    }

    /**
     * 获取Dom类型的xml解析器
     *
     * @param fileRelativePath xml文件的相对路径
     * @return XmlParser 返回Dom类型xml解析器
     * @see XmlDomParser#XmlDomParser(String)
     */
    public static XmlParser getXmlDomParser(String fileRelativePath) {

        try {
            return new XmlDomParser(fileRelativePath);
        } catch (Exception e) {
            LOGGER.error(fileRelativePath
                    + "\nnot found,return null", e);
            return null;
        }
    }

    /**
     * 获取Dom类型的xml解析器
     *
     * @param fileRelativePath xml文件的相对路径
     * @param cl               指定类加载器
     * @return XmlParser 返回Dom类型xml解析器
     * @see XmlDomParser#XmlDomParser(String)
     */
    public static XmlParser getXmlDomParser(String fileRelativePath,
                                            ClassLoader cl) {
        try {
            return new XmlDomParser(fileRelativePath, cl);
        } catch (Exception e) {
            LOGGER.error(fileRelativePath
                    + "not found,return null", e);
            return null;
        }
    }

    /**
     * 获取Dom类型的xml解析器
     *
     * @param file 方法参数：file
     * @return XmlParser 返回Dom类型xml解析器
     * @see XmlDomParser#XmlDomParser(String)
     */
    public static XmlParser getXmlDomParser(File file) {
        try {
            return new XmlDomParser(file);
        } catch (Exception e) {
            LOGGER.error(file
                    + "not found,return null", e);
            return null;
        }
    }

    /**
     * 在给定的Node中获取某个属性对应的属性值
     *
     * @param node          给定的Node
     * @param attributeName 属性名
     * @return String 属性值
     * @see XmlDomParser#getAttributeValueOfElementNode(Node, String)
     */
    public abstract String getAttributeValueOfElementNode(Node node,
                                                          String attributeName);

    /**
     * 获取类加载器
     *
     * @return ClassLoader 类加载器
     */
    public abstract ClassLoader getClassLoader();

    /**
     * 获取子节点的值
     *
     * @param parent       父节点
     * @param childTagName 子节点标签名
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public abstract String getTextContentOfChild(Element parent,
                                                 String childTagName);

    /**
     * 在给定的Node中查找满足条件的子孙结点
     * 通过标签返回一个Node
     * 此方法适合能定位到唯一的Node的情况
     *
     * @param node    给定的Node
     * @param tagName 标签名
     * @return Element 某个元素结点
     * @see XmlDomParser#treeWalkOfOneElementNode(Node, String)
     */
    public abstract Element treeWalkOfOneElementNode(Element node,
                                                     String tagName);

    /**
     * 在给定的Node中查找满足条件的子孙结点
     * 通过标签、某个属性名和对应的属性值返回一个Node
     * 此方法适合能定位到唯一的Node的情况
     *
     * @param node           给定的Node
     * @param tagName        标签名
     * @param attributeName  属性名
     * @param attributeValue 属性值
     * @return Element 某个元素结点
     * @see XmlDomParser#treeWalkOfOneElementNode(Node, String, String, String)
     */
    public abstract Element treeWalkOfOneElementNode(Element node,
                                                     String tagName, String attributeName, String attributeValue);

    /**
     * 通过标签返回一个Node
     * 此方法适合能定位到唯一的Node的情况
     *
     * @param tagName 标签名
     * @return Element 某个元素结点
     * @see XmlDomParser#treeWalkOfOneElementNode(String)
     */
    public abstract Element treeWalkOfOneElementNode(String tagName);

    /**
     * 通过标签、某个属性名和对应的属性值返回一个Node
     * 此方法适合能定位到唯一的Node的情况
     *
     * @param tagName        标签名
     * @param attributeName  属性名
     * @param attributeValue 属性值
     * @return Element 某个元素结点
     * @see XmlDomParser#treeWalkOfOneElementNode(String, String, String)
     */
    public abstract Element treeWalkOfOneElementNode(String tagName,
                                                     String attributeName, String attributeValue);

    /**
     * 获取<code>parent</code>节点下所以标签为<code>childTagName<code>的节点
     *
     * @param parent       方法参数：parent
     * @param childTagName 方法参数：childTagName
     * @return Collection<Element> [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public abstract Collection<Element> getChildren(Element parent,
                                                    String childTagName);

    /**
     * 获取<code>parent</code>节点下面的所有的子节点
     * <功能详细描述>
     *
     * @param parent 方法参数：parent
     * @return Collection<Element> [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public abstract Collection<Element> getChildren(Element parent);

    /**
     * 获取<code>parent</code>节点下面的所有的子节点
     *
     * @param parent       方法参数：parent
     * @param childTagName 方法参数：childTagName
     * @return Element [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public abstract Element getChild(Element parent, String childTagName);

}
