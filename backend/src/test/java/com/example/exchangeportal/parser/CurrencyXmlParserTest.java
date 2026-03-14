package com.example.exchangeportal.parser;

import com.example.exchangeportal.record.CurrencyClientResponse;
import com.example.exchangeportal.record.ParsedCurrency;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class CurrencyXmlParserTest {

    private final CurrencyXmlParser currencyXmlParser;

    public CurrencyXmlParserTest() {
        currencyXmlParser = new CurrencyXmlParser();
    }

    @Test
    void testParse_Success() throws ParserConfigurationException, SAXException, IOException {
        String validXml = """
                <CcyTbl xmlns="http://www.lb.lt/WebServices/FxRates">
                    <CcyNtry>
                        <Ccy>AED</Ccy>
                        <CcyNm lang="LT">Jungtinių Arabų Emiratų dirhamas</CcyNm>
                        <CcyNm lang="EN">UAE dirham</CcyNm>
                        <CcyNbr>784</CcyNbr>
                        <CcyMnrUnts>2</CcyMnrUnts>
                    </CcyNtry>
                    <CcyNtry>
                        <Ccy>AFN</Ccy>
                        <CcyNm lang="LT">Afganistano afganis</CcyNm>
                        <CcyNm lang="EN">Afghani</CcyNm>
                        <CcyNbr>971</CcyNbr>
                        <CcyMnrUnts>2</CcyMnrUnts>
                    </CcyNtry>
                </CcyTbl>
                """;

        List<ParsedCurrency> expectedCurrencies = List.of(
                new ParsedCurrency("AED", "UAE dirham", 2),
                new ParsedCurrency("AFN", "Afghani", 2));

        List<ParsedCurrency> actualCurrencies = currencyXmlParser.parse(new CurrencyClientResponse(validXml));
        assertEquals(expectedCurrencies, actualCurrencies);
    }
}
