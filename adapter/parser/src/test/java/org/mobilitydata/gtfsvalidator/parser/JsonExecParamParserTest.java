package org.mobilitydata.gtfsvalidator.parser;

// todo: implement this test. We could not mock the .forEachRemaining method used in JsonExecParamParser.parse
class JsonExecParamParserTest {
//    private final static String HELP_KEY = "help";
//    private final static String INPUT_KEY = "input";
//    private final static String OUTPUT_KEY = "output";
//    private final static String JSON_EXEC_PARAM_FILE = "{\"HELP\": null, \"extract\": \"input\", " +
//            "\"output\": \"output\"}\n";
//
//    @Test
//    public void jsonFileShouldMapToExecutionParameterMap() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//
//        final ExecParam mockHelpExecParam = spy(ExecParam.class);
//        mockHelpExecParam.setKey(HELP_KEY);
//
//        final ExecParam mockInputExecParam = spy(ExecParam.class);
//        mockInputExecParam.setKey(INPUT_KEY);
//        mockInputExecParam.setValue("input");
//
//        final ExecParam mockOutputExecParam = spy(ExecParam.class);
//        mockOutputExecParam.setKey(OUTPUT_KEY);
//        mockOutputExecParam.setValue("output");
//
//        final List<Object> objectCollection = new ArrayList<>();
//
//        objectCollection.add(mockHelpExecParam);
//        objectCollection.add(mockInputExecParam);
//        objectCollection.add(mockOutputExecParam);
//
//        @SuppressWarnings("rawtypes")
//        final Iterator mockIterator = mock(MappingIterator.class);
//        final JsonNode mockJsonNode = mock(JsonNode.class);
//
//        when(mockObjectReader.readTree(anyString())).thenReturn(mockJsonNode);
//        when(mockJsonNode.fields()).thenReturn(mockIterator);
//        when(mockIterator.hasNext()).thenReturn(true, true, true, false);
//        when(mockIterator.next()).thenReturn(mockJsonNode);
//
//        Consumer mockConsumer = mock(Consumer.class);
//
//        when(mockIterator.forEachRemaining(Consumer.class)).thenCallRealMethod();
//
//        Iterator<Map.Entry<String, JsonNode>> mockEntryMap = mock(Iterator.class);
//
//        Map.Entry<String, JsonNode> mockEntry = mock(Map.Entry.class);
//        when(mockEntry.getKey()).thenReturn("help", "input", "output");
//
//        JsonNode mockJsonNode0 = mock(JsonNode.class);
//        when(mockJsonNode0.asText()).thenReturn("null");
//
//        JsonNode mockJsonNode1 = mock(JsonNode.class);
//        when(mockJsonNode0.asText()).thenReturn("input");
//
//        JsonNode mockJsonNode2 = mock(JsonNode.class);
//        when(mockJsonNode0.asText()).thenReturn("output");
//
//        when(mockEntry.getValue()).thenReturn(mockJsonNode0, mockJsonNode1, mockJsonNode2);
//
//        when(mockEntryMap.next()).thenReturn(mockEntry);
//
//        Logger mockLogger = mock(Logger.class);
//
//        final JsonExecParamParser underTest = new JsonExecParamParser(JSON_EXEC_PARAM_FILE, mockObjectReader,
//                mockLogger);
//        final Map<String, ExecParam> toCheck = underTest.parse();
//
//        assertEquals(3, toCheck.size());
//        assertTrue(toCheck.containsKey(HELP_KEY));
//        assertTrue(toCheck.containsKey(INPUT_KEY));
//        assertTrue(toCheck.containsKey(OUTPUT_KEY));
//
//        ExecParam toTest = toCheck.get("help");
//        assertEquals(HELP_KEY, toTest.getKey());
//        assertNull(toTest.getValue());
//
//        toTest = toCheck.get("input");
//        assertEquals(INPUT_KEY, toTest.getKey());
//        assertEquals("input", toTest.getValue());
//
//        toTest = toCheck.get("output");
//        assertEquals(OUTPUT_KEY, toTest.getKey());
//        assertEquals(toTest.getValue(), "output");
//    }
}