package com.sap.dsc.aas.lib.ua.transform;

import com.sap.dsc.aas.lib.exceptions.TransformationException;
import com.sap.dsc.aas.lib.mapping.MappingSpecificationParser;
import com.sap.dsc.aas.lib.mapping.model.MappingSpecification;
import com.sap.dsc.aas.lib.transform.MappingSpecificationDocumentTransformer;
import com.sap.dsc.aas.lib.transform.XPathHelper;
import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BrowsepathXPathBuilderTest {
    private BrowsepathXPathBuilder pathBuilder;
    private MappingSpecificationParser parser;
    private Logger LOGGER = Logger.getLogger("ParserTest.class");
    private String nodesetInputFileName = "src/test/resources/ua/minimal-nodeset.xml";
    private Document xmlDoc;
    private MappingSpecificationDocumentTransformer transformer;

    @BeforeEach
    void setUp() throws TransformationException, IOException {
        parser = new MappingSpecificationParser();
        transformer = new MappingSpecificationDocumentTransformer();
        try (InputStream nodesetStream = Files.newInputStream(Paths.get(nodesetInputFileName))) {
            UANodeSetTransformer transformer = new UANodeSetTransformer();
            xmlDoc = transformer.readXmlDocument(nodesetStream);
            pathBuilder =  new BrowsepathXPathBuilder(xmlDoc);
        }
    }

    @Test
    void simpleTest() {
        String[] browsePath = {"1:ExampleObject", "1:ExampleIntegerVariable"};
        String nodeId = pathBuilder.getNodeIdFromBrowsePath(browsePath);
        assertEquals("ns=1;i=1010", nodeId);
        String exp = pathBuilder.pathExpression(browsePath);
        List<Node> nodeFromExp =XPathHelper.getInstance().getNodes(xmlDoc, exp);
        assertNotNull(nodeFromExp);
        assertEquals(nodeFromExp.size(), 1);
        assertThat(nodeFromExp.get(0)).isInstanceOf(Element.class);
        assertEquals(nodeId, ((Element)nodeFromExp.get(0)).attribute("NodeId").getValue() );
        assertEquals("http://iwu.fraunhofer.de/c32/arno", pathBuilder.getNamespace(browsePath[0]));


    }

    @Test
    void testUaBrowsePathExpr() throws IOException, TransformationException {
        MappingSpecification spec = parser
                .loadMappingSpecification("src/test/resources/mappings/generic/browsepathTest.json");
        AssetAdministrationShellEnvironment aas = transformer.createShellEnv(xmlDoc, spec);
        assertNotNull(aas);
        assertEquals(1, aas.getSubmodels().size());
        assertEquals(aas.getSubmodels().get(0).getIdShort(), "ns=1;i=1010");
    }

    @Test
    void testInvalidConfigFile() throws IOException {

        MappingSpecification spec = parser
                .loadMappingSpecification("src/test/resources/mappings/generic/invalidBrowsepathTest.json");
        RuntimeException e = assertThrows( RuntimeException.class, () -> transformer.createShellEnv(xmlDoc, spec));
        Throwable cause = e.getCause();
        assertThat(cause instanceof IllegalArgumentException);
    }

}
