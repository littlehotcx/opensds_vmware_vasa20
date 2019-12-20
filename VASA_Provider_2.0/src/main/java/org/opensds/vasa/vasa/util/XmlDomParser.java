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
import java.net.URL;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Dom类型xml解析器类
 *
 * @author f00102803
 * @version [版本号V001R010C00, 2011-12-14]
 * @since ISM Server:fileUtils
 */
public class XmlDomParser extends XmlParser {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(XmlDomParser.class);

    private static DocumentBuilder documentBuilder; // xml文件构建工厂

    private Document doc = null; // xml文档对象

    private ClassLoader loader = Thread.currentThread()
            .getContextClassLoader();

    /**
     * Dom类型的xml解析器
     *
     * @param filePathRelativeToJar 方法参数：filePathRelativeToJar
     * @throws SAXException 异常：SAXException
     * @throws IOException  异常：IOException
     */
    public XmlDomParser(String filePathRelativeToJar) throws SAXException,
            IOException {
        synchronized (documentBuilder) {
            InputStream is = null;
            try {
                is = loader.getResourceAsStream(filePathRelativeToJar);
                doc = documentBuilder.parse(is);
                doc.getDocumentElement().normalize();
            } catch (Exception e) {
                LOGGER.error("parser xml document failed. " + e);
            } finally {
                if (is != null) {
                    is.close();
                }
            }

        }
    }

    /**
     * Dom类型的xml解析器
     *
     * @param cl                    指定类加载器
     * @param filePathRelativeToJar 方法参数：filePathRelativeToJar
     * @throws SAXException 异常：SAXException
     * @throws IOException  异常：IOException
     */
    public XmlDomParser(String filePathRelativeToJar, ClassLoader cl)
            throws SAXException, IOException {
        synchronized (documentBuilder) {
            URL url = cl.getResource(filePathRelativeToJar);

            // 修改coverity
            if (url != null) {
                doc = documentBuilder.parse(url.toString());
                doc.getDocumentElement().normalize();
            }
            setClassLoader(cl);
        }
    }

    static {
        // 创建加载xml文档的工厂
        DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();

        docBuilderFac.setIgnoringElementContentWhitespace(true); // 忽略空白结点

        try {
            documentBuilder = docBuilderFac.newDocumentBuilder();

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * 方法 ： XmlDomParser
     *
     * @param is 方法参数：is
     * @throws SAXException 异常：SAXException
     * @throws IOException  异常：IOException
     */
    public XmlDomParser(InputStream is) throws SAXException, IOException {
        synchronized (documentBuilder) {
            doc = documentBuilder.parse(is);
            doc.getDocumentElement().normalize();
        }
    }

    /**
     * Dom类型的xml解析器
     *
     * @param file 方法参数：file
     * @throws SAXException 异常：SAXException
     * @throws IOException  异常：IOException
     */
    public XmlDomParser(File file) throws SAXException, IOException {
        synchronized (documentBuilder) {
            doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize();
        }
    }

    /*
     * 设置类加载器
     *
     * @param cl 类加载器
     */
    private void setClassLoader(ClassLoader cl) {
        this.loader = cl;
    }

    /**
     * 获取类加载器
     *
     * @return ClassLoader 类加载器
     */
    @Override
    public ClassLoader getClassLoader() {
        return this.loader;
    }

    /**
     * 方法 ： getAttributeValueOfElementNode
     *
     * @param node          某个结点
     * @param attributeName 结点的属性名
     * @return String 结点的属性值
     */
    @Override
    public String getAttributeValueOfElementNode(Node node, String attributeName) {
        if (attributeName != null) {
            Node attributeNode = node.getAttributes()
                    .getNamedItem(attributeName);
            return attributeNode.getTextContent();
        }
        return "";
    }

    /**
     * 方法 ： getTextContentOfChild
     *
     * @param parent       ：父节点
     * @param childTagName 方法参数：childTagName
     * @return String 返回结果
     */
    @Override
    public String getTextContentOfChild(Element parent, String childTagName) {
        return XmlUtils.getTextContentOfChild(parent, childTagName);
    }

    /**
     * 方法 ： treeWalkOfOneElementNode
     *
     * @param node    某个结点
     * @param tagName 标签名
     * @return Element 返回结果
     */
    @Override
    public Element treeWalkOfOneElementNode(Element node, String tagName) {
        return treeWalkOfOneElementNode(node, tagName, null, null);
    }

    /**
     * 方法 ： treeWalkOfOneElementNode
     *
     * @param parentNode     给定的Node
     * @param tagName        标签名
     * @param attributeName  属性名
     * @param attributeValue 属性值
     * @return Element 某个元素结点
     */
    @Override
    public Element treeWalkOfOneElementNode(Element parentNode, String tagName,
                                            String attributeName, String attributeValue) {
        // 在给定的newNode中获取所有tagName标签对应的结点
        NodeList nodeList = parentNode.getElementsByTagName(tagName);
        return treeWalkOfOneElementFromNodeList(nodeList,
                attributeName,
                attributeValue);
    }

    /**
     * 方法 ： treeWalkOfOneElementNode
     *
     * @param tagName 方法参数：tagName
     * @return Element 某个元素结点
     */
    @Override
    public Element treeWalkOfOneElementNode(String tagName) {
        return treeWalkOfOneElementNode(tagName, null, null);
    }

    /**
     * 方法 ： treeWalkOfOneElementNode
     *
     * @param tagName        标签名
     * @param attributeName  属性名
     * @param attributeValue 属性值
     * @return Element 某个元素结点
     */
    @Override
    public Element treeWalkOfOneElementNode(String tagName,
                                            String attributeName, String attributeValue) {
        // 获取所有tagName标签对应的结点
        NodeList nodeList = this.doc.getElementsByTagName(tagName);
        return treeWalkOfOneElementFromNodeList(nodeList,
                attributeName,
                attributeValue);
    }

    /**
     * 方法 ： getChildren
     *
     * @param parent       方法参数：parent
     * @param childTagName 方法参数：childTagName
     * @return Collection<Element> 返回结果
     */
    @Override
    public Collection<Element> getChildren(Element parent, String childTagName) {
        return XmlUtils.getChildren(parent, childTagName);
    }

    /**
     * 方法 ： getChild
     *
     * @param parent       方法参数：parent
     * @param childTagName 方法参数：childTagName
     * @return Element 返回结果
     */
    @Override
    public Element getChild(Element parent, String childTagName) {
        return XmlUtils.getChild(parent, childTagName);
    }

    /**
     * 方法 ： getChildren
     *
     * @param parent 方法参数：parent
     * @return Collection<Element> 返回结果
     */
    @Override
    public Collection<Element> getChildren(Element parent) {
        return getChildren(parent, null);
    }

    /*
     * 从node集合中找到满足属性名和属性值要求的唯一结点
     * 此方法适合能定位到唯一的Node的情况
     * 如果不唯一，不会抛出异常，只会记录日志
     *
     * @return Element 某个元素结点
     */
    private Element treeWalkOfOneElementFromNodeList(NodeList nodeList,
                                                     String attributeName, String attributeValue) {
        Element findNode = null; // 根据形参找到的结点
//        int findNum = 0; // 找到了满足条件的结点的个数
        int nodeSize = nodeList.getLength();

        // 遍历这些结点，找出属性名等于attributeName，属性值等于attributeValue的结点
        for (int i = 0; i < nodeSize; i++) {
            Element node = (Element) nodeList.item(i);

            // 如果形参的属性名或属性值为空，则认为形参中的每个结点都是要找的结点
            if (null == attributeName || null == attributeValue) {
//                findNum++; // 找到了一个结点
                findNode = node;
                continue;
            }

            // 形参的属性值和属性名都不为空，比较结点的属性名和属性值，判断是否和形参相等
            Node attributeNode = node.getAttributes()
                    .getNamedItem(attributeName);

            // 1、找到了形参属性名对应的属性结点
            if (null != attributeNode) {
                // 2、如果此属性结点的属性值和形参的属性值相等，就找到了结点
                if (attributeValue.equalsIgnoreCase(attributeNode.getTextContent())) {
//                    findNum++; // 找到了一个结点
                    findNode = node;
                }
            }
        }
        return findNode;
    }

}
