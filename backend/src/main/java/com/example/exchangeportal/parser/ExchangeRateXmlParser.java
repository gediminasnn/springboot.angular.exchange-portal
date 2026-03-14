package com.example.exchangeportal.parser;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.exchangeportal.record.ParsedExchangeRate;
import com.example.exchangeportal.record.RatesClientResponse;

@Component
public class ExchangeRateXmlParser {

    public List<ParsedExchangeRate> parse(RatesClientResponse response)
            throws ParserConfigurationException, SAXException, IOException {
        Document document = parse(response.xmlData());
        NodeList nodeList = document.getElementsByTagName("FxRate");
        List<ParsedExchangeRate> parsedRates = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            parsedRates.add(parse(element));
        }
        return parsedRates;
    }

    private Document parse(String xmlData) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new java.io.ByteArrayInputStream(xmlData.getBytes()));
    }

    private ParsedExchangeRate parse(Element element) {
        String dateString = element.getElementsByTagName("Dt").item(0).getTextContent();
        LocalDate date = LocalDate.parse(dateString);

        NodeList ccyAmtList = element.getElementsByTagName("CcyAmt");
        String currencyCode = ((Element) ccyAmtList.item(1)).getElementsByTagName("Ccy").item(0).getTextContent();
        double rateAmount = Double
                .parseDouble(((Element) ccyAmtList.item(1)).getElementsByTagName("Amt").item(0).getTextContent());

        return new ParsedExchangeRate(currencyCode, rateAmount, date);
    }
}
