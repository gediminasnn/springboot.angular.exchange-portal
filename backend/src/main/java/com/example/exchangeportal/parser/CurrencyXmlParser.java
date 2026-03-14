package com.example.exchangeportal.parser;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;

import com.example.exchangeportal.record.CurrencyClientResponse;
import com.example.exchangeportal.record.ParsedCurrency;

@Component
public class CurrencyXmlParser {

    public List<ParsedCurrency> parse(CurrencyClientResponse response)
            throws ParserConfigurationException, SAXException, IOException {
        Document document = parse(response.xmlData());
        List<ParsedCurrency> currencies = new ArrayList<>();
        NodeList nodeList = document.getElementsByTagName("CcyNtry");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            currencies.add(parse(element));
        }
        return currencies;
    }

    private Document parse(String xmlData) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));
        document.getDocumentElement().normalize();
        return document;
    }

    private ParsedCurrency parse(Element element) {
        String code = element.getElementsByTagName("Ccy").item(0).getTextContent();
        String name = element.getElementsByTagName("CcyNm").item(1).getTextContent();
        int minorUnits = Integer.parseInt(element.getElementsByTagName("CcyMnrUnts").item(0).getTextContent());

        return new ParsedCurrency(code, name, minorUnits);
    }
}
