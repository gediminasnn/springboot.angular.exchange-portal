package com.example.exchangeportal.parser;

import static org.junit.jupiter.api.Assertions.*;

import com.example.exchangeportal.record.ParsedExchangeRate;
import com.example.exchangeportal.record.RatesClientResponse;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExchangeRateXmlParserTest {

    private final ExchangeRateXmlParser parser = new ExchangeRateXmlParser();

    @Test
    void testParse_Success() throws ParserConfigurationException, SAXException, IOException {
        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<FxRates xmlns:xsi=\"http://www.w3.org/2021/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"FxRatesSchema.xsd\">\n"
                +
                "  <FxRate>\n" +
                "    <Tp>LT</Tp>\n" +
                "    <Dt>2024-05-10</Dt>\n" +
                "    <CcyAmt>\n" +
                "      <Ccy>EUR</Ccy>\n" +
                "      <Amt>1.0000</Amt>\n" +
                "    </CcyAmt>\n" +
                "    <CcyAmt>\n" +
                "      <Ccy>USD</Ccy>\n" +
                "      <Amt>1.0926</Amt>\n" +
                "    </CcyAmt>\n" +
                "  </FxRate>\n" +
                "  <FxRate>\n" +
                "    <Tp>LT</Tp>\n" +
                "    <Dt>2024-05-10</Dt>\n" +
                "    <CcyAmt>\n" +
                "      <Ccy>EUR</Ccy>\n" +
                "      <Amt>1.0000</Amt>\n" +
                "    </CcyAmt>\n" +
                "    <CcyAmt>\n" +
                "      <Ccy>JPY</Ccy>\n" +
                "      <Amt>142.75</Amt>\n" +
                "    </CcyAmt>\n" +
                "  </FxRate>\n" +
                "</FxRates>";
        LocalDate date = LocalDate.parse("2024-05-10");

        List<ParsedExchangeRate> expected = List.of(
                new ParsedExchangeRate("USD", 1.0926, date),
                new ParsedExchangeRate("JPY", 142.75, date));

        List<ParsedExchangeRate> actual = parser.parse(new RatesClientResponse(xmlData));

        assertEquals(expected, actual);
    }

    @Test
    void testParse_InvalidXml() {
        String invalidXml = "not valid xml";
        assertThrows(SAXException.class, () -> parser.parse(new RatesClientResponse(invalidXml)));
    }
}
