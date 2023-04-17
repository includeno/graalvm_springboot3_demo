package com.example.utils;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import lombok.Data;
import org.docx4j.Docx4J;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.finders.ClassFinder;
import org.docx4j.jaxb.Context;
import org.docx4j.math.CTOMathPara;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FormulaeUtils {

    /**
     * 从latex格式的公式中获取docx文件
     *
     * @param latexFormulae latex格式的公式
     * @return the docx file
     */
    public static File getDocxFromLatexFormulae(String latexFormulae, boolean includeTimestamp, boolean includeUUID) {
        String userPath = System.getProperty("user.dir");
        String fileName = RandomFileNameGenerator.generateRandomFileName("formulae_latex", includeTimestamp, includeUUID, ".docx");
        Path path = Path.of(userPath, fileName);
        return PandocUtils.stringToWordFile(latexFormulae, path.toString());
    }

    public static File getDocxFromOOXMLFormulae(String ooxmlFormulae, boolean includeTimestamp, boolean includeUUID) {
        String userPath = System.getProperty("user.dir");
        String fileName = RandomFileNameGenerator.generateRandomFileName("formulae_ooxml", includeTimestamp, includeUUID, ".docx");
        Path path = Path.of(userPath, fileName);

        // First, create/save a docx containing a formula.
        WordprocessingMLPackage wordMLPackage = null;
        try {
            wordMLPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
            // 创建公式段落
            P p = new P();
            // 添加公式
            JAXBElement<CTOMathPara> omathpara = (JAXBElement) XmlUtils.unmarshalString(ooxmlFormulae);
            p.getContent().add(omathpara);
            mdp.getContent().add(p);
            Docx4J.save(wordMLPackage, new File(fileName), Docx4J.FLAG_SAVE_ZIP_FILE);
            System.out.println("Saved " + fileName);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static String getFormulaeXmlFromDocx(File wordDocxFile) {
        String result = "";
        // First, create/save a docx containing a formula.
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(wordDocxFile);
            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
            ClassFinder finder = new ClassFinder(CTOMathPara.class);
            new TraversalUtil(mdp.getContent(), finder);
            // Get the first result
            Object o = finder.results.get(0);
            System.out.println(o.getClass().getName());
            // Can't use XmlUtils.marshaltoString(o) because
            // CTOMathPara is missing an @XmlRootElement annotation,
            // so be explicit
            result = XmlUtils.marshaltoString(o, true, true,
                    Context.jc,
                    "http://schemas.openxmlformats.org/officeDocument/2006/math", "oMathPara", CTOMathPara.class);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        } finally {
            return result;
        }
    }

    @Data
    static class MarkAppenderResult {
        String ooxml;
        List<Exception> exceptions = new ArrayList<>();
    }


    static class MarkAppender {
        /*
         * 转化公式的xml格式，添加标号
         * */
        public static MarkAppenderResult appendMark(String formulaeXml, Integer sectionNumber, Integer indexNumber) {
            MarkAppenderResult result = new MarkAppenderResult();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(new StringReader(formulaeXml)));
                doc.getDocumentElement().normalize();

                removeStyleTags(doc.getDocumentElement());
                appendMarker(doc.getDocumentElement(), doc, sectionNumber, indexNumber);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StringWriter writer = new StringWriter();
                transformer.transform(source, new StreamResult(writer));
                String simplifiedXmlString = writer.toString();
                result.setOoxml(simplifiedXmlString);
                return result;
            } catch (Exception e) {
                result.setExceptions(List.of(e));
            } finally {
                return result;
            }
        }

        private static void removeStyleTags(Node node) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String nodeName = element.getTagName();

                // 删除样式相关标签
                if (nodeName.equals("w:rPr") || nodeName.equals("w:rFonts") || nodeName.equals("m:ctrlPr")) {
                    node.getParentNode().removeChild(node);
                    return;
                }
            }

            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                removeStyleTags(children.item(i));
            }
        }

        private static void appendMarker(Node node, Document doc, Integer section, Integer index) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String nodeName = element.getTagName();

                if (nodeName.equals("m:e")) {
                    Element marker = doc.createElement("m:r");
                    Element markerText = doc.createElement("m:t");
                    markerText.setTextContent("#(" + section + "-" + index + ")");
                    marker.appendChild(markerText);
                    element.appendChild(marker);
                    return;
                }
            }

            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                appendMarker(children.item(i), doc, section, index);
            }
        }
    }

    public static void main(String[] args) {
        //System.out.println(getDocxFromLatexFormulae("$$\\frac{1}{2}$$", false, false));

        String formulae = getFormulaeXmlFromDocx(getDocxFromLatexFormulae("$$\\frac{1}{2}$$", false, false));

        System.out.println("formulae:" + formulae);

//        String formulaeWithNumber = getFormulaeXmlFromDocx(new File("/Users/includeno/Documents/GitHub/includeno/MultiVerThesis/formulaeWithNumber.docx"));
//        System.out.println("formulaeWithNumber:"+formulaeWithNumber);

        String formulae1 = getFormulaeXmlFromDocx(new File("/Users/includeno/Documents/GitHub/includeno/MultiVerThesis/formulae1.docx"));
        System.out.println("formulae1:" + formulae1);

    }
}
