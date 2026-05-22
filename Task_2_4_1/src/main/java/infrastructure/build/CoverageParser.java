package infrastructure.build;

import core.util.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;

public class CoverageParser {

    public double parseCoverage(Path xmlPath) {
        try {
            if (xmlPath == null || !Files.exists(xmlPath)) {
                return 0.0;
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setFeature("http://xml.org/sax/features/namespaces", false);
            dbf.setFeature("http://xml.org/sax/features/validation", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            Document doc = dbf.newDocumentBuilder().parse(xmlPath.toFile());

            Node reportNode = doc.getDocumentElement();
            NodeList children = reportNode.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Element counter && "counter".equals(node.getNodeName())) {
                    if ("INSTRUCTION".equals(counter.getAttribute("type"))) {
                        double missed = Double.parseDouble(counter.getAttribute("missed"));
                        double covered = Double.parseDouble(counter.getAttribute("covered"));

                        double total = missed + covered;
                        if (total == 0) return 0.0;

                        double result = (covered / total) * 100.0;
                        Logger.debug(String.format("Покрытие инструкций: %.2f%%", result));
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Ошибка парсинга JaCoCo XML (" + xmlPath.getFileName() + "): " + e.getMessage());
        }
        return 0.0;
    }
}