package com.example.utils;

import com.example.enums.ContentType;
import com.example.enums.VersionType;
import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class WordUtils {

    public static File export(List<Element> data) {
        HashSet<Element> nodes = new HashSet<>();
        for (Element element : data) {
            JsonDataGenerator.collectAllNodes(element, nodes);
        }
        List<Element> nodeList = nodes.stream().sorted(Comparator.comparing(Element::getSortIndex)).collect(Collectors.toList());
        String fileName = RandomFileNameGenerator.generateRandomFileName(false, true, true, ".docx");

        File file = new File(System.getProperty("user.dir")+"/"+fileName);
        log.info("export file path: {}", file.getAbsolutePath());
        try {
            // First, create/save a docx containing a formula.
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();

            for (Element element : nodeList) {
                if (element.getType().equals(VersionType.PARAGRAPH.getCode())) {
                    // paragraph && text
                    String content = "";
                    if (element.getContentType().equals(ContentType.TEXT.getType())) {
                        ObjectFactory factory = Context.getWmlObjectFactory();
                        P paragraph = factory.createP();
                        R run = factory.createR();
                        Text t = factory.createText();
                        t.setValue(element.getContent());//填写内容
                        run.getContent().add(t);
                        paragraph.getContent().add(run);

                        mdp.getContent().add(paragraph);
                    } else if (element.getContentType().equals(ContentType.IMAGE.getType())) {

                    } else if (element.getContentType().equals(ContentType.TABLE.getType())) {

                    } else if (element.getContentType().equals(ContentType.FORMULA.getType())) {
                        File formulaeDocx = FormulaeUtils.getDocxFromLatexFormulae(element.getContent(), false, false);
                        String xml = FormulaeUtils.getFormulaeXmlFromDocx(formulaeDocx);

                        P paragraph = new P();
                        JAXBElement omathpara = (JAXBElement) XmlUtils.unmarshalString(xml); // or could have used generated code which uses JAXB factory approach
                        paragraph.getContent().add(omathpara);

                        mdp.getContent().add(paragraph);
                    }
                } else if (element.getType().equals(VersionType.SECTION.getCode())) {
                    ObjectFactory factory = Context.getWmlObjectFactory();
                    P paragraph = factory.createP();
                    R run = factory.createR();
                    Text t = factory.createText();
                    t.setValue(element.getContent());//填写内容
                    run.getContent().add(t);
                    paragraph.getContent().add(run);

                    mdp.getContent().add(paragraph);
                }

            }
            Docx4J.save(wordMLPackage, file, Docx4J.FLAG_SAVE_ZIP_FILE);
            log.info("Saved " + fileName);
        } catch (Exception ex) {
            log.error("Error: {}" + ex.getMessage());
        } finally {
            return file;
        }
    }
}
