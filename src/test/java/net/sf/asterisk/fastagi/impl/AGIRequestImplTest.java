/*
 * Copyright  2004-2005 Stefan Reuter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package net.sf.asterisk.fastagi.impl;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import net.sf.asterisk.fastagi.AGIRequest;

public class AGIRequestImplTest extends TestCase
{
    protected void setUp()
    {
    }

    public void testBuildRequest()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_network: yes");
        lines.add("agi_network_script: myscript.agi");
        lines.add("agi_request: agi://host/myscript.agi");
        lines.add("agi_channel: SIP/1234-d715");
        lines.add("agi_language: en");
        lines.add("agi_type: SIP");
        lines.add("agi_uniqueid: 1110023416.6");
        lines.add("agi_callerid: John Doe<1234>");
        lines.add("agi_dnid: 8870");
        lines.add("agi_rdnis: 9876");
        lines.add("agi_context: local");
        lines.add("agi_extension: 8870");
        lines.add("agi_priority: 1");
        lines.add("agi_enhanced: 0.0");
        lines.add("agi_accountcode: ");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect script", "myscript.agi", request.getScript());
        assertEquals("incorrect requestURL", "agi://host/myscript.agi", request.getRequestURL());
        assertEquals("incorrect channel", "SIP/1234-d715", request.getChannel());
        assertEquals("incorrect uniqueId", "SIP/1234-d715", request.getChannel());
        assertEquals("incorrect type", "SIP", request.getType());
        assertEquals("incorrect uniqueId", "1110023416.6", request.getUniqueId());
        assertEquals("incorrect language", "en", request.getLanguage());
        assertEquals("incorrect callerId", "1234", request.getCallerId());
        assertEquals("incorrect callerIdName", "John Doe", request.getCallerIdName());
        assertEquals("incorrect dnid", "8870", request.getDnid());
        assertEquals("incorrect rdnis", "9876", request.getRdnis());
        assertEquals("incorrect context", "local", request.getContext());
        assertEquals("incorrect extension", "8870", request.getExtension());
        assertEquals("incorrect priority", new Integer(1), request.getPriority());
        assertEquals("incorrect enhanced", Boolean.FALSE, request.getEnhanced());
        assertNull("incorrect accountCode must not be set", request.getAccountCode());
    }

    public void testBuildRequestWithoutCallerIdName()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_callerid: 1234");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect callerId", "1234", request.getCallerId());
        assertNull("callerIdName must not be set", request.getCallerIdName());
    }

    public void testBuildRequestWithoutCallerIdNameButBracket()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_callerid: <1234>");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect callerId", "1234", request.getCallerId());
        assertNull("callerIdName must not be set", request.getCallerIdName());
    }

    public void testBuildRequestWithoutCallerIdNameButBracketAndQuotesAndSpace()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_callerid: \"\" <1234>");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect callerId", "1234", request.getCallerId());
        assertNull("callerIdName must not be set", request.getCallerIdName());
    }

    public void testBuildRequestWithQuotedCallerIdName()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_callerid: \"John Doe\"<1234>");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect callerId", "1234", request.getCallerId());
        assertEquals("incorrect callerIdName", "John Doe", request.getCallerIdName());
    }

    public void testBuildRequestWithQuotedCallerIdNameAndSpace()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_callerid: \"John Doe\" <1234>");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect callerId", "1234", request.getCallerId());
        assertEquals("incorrect callerIdName", "John Doe", request.getCallerIdName());
    }

    public void testBuildRequestWithoutCallerId()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_callerid: ");

        request = new AGIRequestImpl(lines);

        assertNull("callerId must not be set", request.getCallerId());
        assertNull("callerIdName must not be set", request.getCallerIdName());
    }

    /*
     * Asterisk 1.2 now uses agi_callerid and agi_calleridname so we don't need to process
     * it ourselves.
     */
    public void testBuildRequestCallerIdAsterisk12()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_callerid: 1234");
        lines.add("agi_calleridname: John Doe");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect callerId", "1234", request.getCallerId());
        assertEquals("incorrect callerIdName", "John Doe", request.getCallerIdName());
    }

    public void testBuildRequestCallerIdAsterisk12WithUnknownCallerId()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_callerid: unknown");
        lines.add("agi_calleridname: John Doe");

        request = new AGIRequestImpl(lines);

        assertNull("callerId must not be set if \"unknown\"", request.getCallerId());
        assertEquals("incorrect callerIdName", "John Doe", request.getCallerIdName());
    }

    public void testBuildRequestCallerIdAsterisk12WithUnknownCallerIdName()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_callerid: 1234");
        lines.add("agi_calleridname: unknown");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect callerId", "1234", request.getCallerId());
        assertNull("callerIdName must not be set if \"unknown\"", request.getCallerIdName());
    }

    public void testBuildRequestCallerIdWithUnknownDnid()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_dnid: unknown");

        request = new AGIRequestImpl(lines);

        assertNull("dnid must not be set if \"unknown\"", request.getDnid());
    }

    public void testBuildRequestCallerIdWithUnknownRdnis()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_rdnis: unknown");

        request = new AGIRequestImpl(lines);

        assertNull("rdnis must not be set if \"unknown\"", request.getRdnis());
    }

    public void testBuildRequestWithNullEnvironment()
    {
        try
        {
            new AGIRequestImpl(null);
            fail("No IllegalArgumentException thrown.");
        }
        catch (IllegalArgumentException e)
        {
        }
    }

    public void testBuildRequestWithUnusualInput()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("var without agi prefix: a value");
        lines.add("agi_without_colon another value");
        lines.add("agi_without_space_after_colon:");
        lines.add("agi_channel: SIP/1234-a892");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect channel", "SIP/1234-a892", request.getChannel());
    }

    public void testBuildRequestWithoutParameters()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_network_script: myscript.agi");
        lines.add("agi_request: agi://host/myscript.agi");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect script", "myscript.agi", request.getScript());
        assertEquals("incorrect requestURL", "agi://host/myscript.agi", request.getRequestURL());
        assertEquals("incorrect value for unset parameter 'param1'", null, request.getParameter("param1"));
        assertEquals("incorrect values for unset parameter 'param1'", null, request.getParameterValues("param1"));
        assertNotNull("getParameterMap() must not return null", request.getParameterMap());
        assertEquals("incorrect size of getParameterMap()", 0, request.getParameterMap().size());
    }

    public void testBuildRequestWithSingleValueParameters()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_network_script: myscript.agi?param1=value1&param2=value2");
        lines.add("agi_request: agi://host/myscript.agi?param1=value1&param2=value2");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect script", "myscript.agi", request.getScript());
        assertEquals("incorrect requestURL", "agi://host/myscript.agi?param1=value1&param2=value2", request.getRequestURL());
        assertEquals("incorrect value for parameter 'param1'", "value1", request.getParameter("param1"));
        assertEquals("incorrect value for parameter 'param2'", "value2", request.getParameter("param2"));
        assertEquals("incorrect value for unset parameter 'param3'", null, request.getParameter("param3"));
        assertEquals("incorrect size of getParameterMap()", 2, request.getParameterMap().size());
        assertEquals("incorrect value for parameter 'param1' when obtained from map", "value1", ((String[]) request.getParameterMap().get("param1"))[0]);
    }

    public void testBuildRequestWithMultiValueParameter()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_network_script: myscript.agi?param1=value1&param1=value2");
        lines.add("agi_request: agi://host/myscript.agi?param1=value1&param1=value2");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect script", "myscript.agi", request.getScript());
        assertEquals("incorrect requestURL", 
                "agi://host/myscript.agi?param1=value1&param1=value2", request.getRequestURL());
        assertEquals("incorrect number of values for parameter 'param1'", 2, request.getParameterValues("param1").length);
        assertEquals("incorrect value[0] for parameter 'param1'", "value1", request.getParameterValues("param1")[0]);
        assertEquals("incorrect value[1] for parameter 'param1'", "value2", request.getParameterValues("param1")[1]);
    }

    public void testBuildRequestWithEmptyValueParameter()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_network_script: myscript.agi?param1");
        lines.add("agi_request: agi://host/myscript.agi?param1");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect script", "myscript.agi", request.getScript());
        assertEquals("incorrect requestURL", "agi://host/myscript.agi?param1", request.getRequestURL());
        assertEquals("incorrect value for parameter 'param1'", "", request.getParameter("param1"));
        assertEquals("incorrect number of values for parameter 'param1'", 1, request.getParameterValues("param1").length);
        assertEquals("incorrect value[0] for parameter 'param1'", "", request.getParameterValues("param1")[0]);
    }

    public void testBuildRequestWithUrlEncodedParameter()
    {
        Collection lines;
        AGIRequest request;

        lines = new ArrayList();

        lines.add("agi_network_script: myscript.agi?param1=my%20value");
        lines.add("agi_request: agi://host/myscript.agi?param1=my%20value");

        request = new AGIRequestImpl(lines);

        assertEquals("incorrect script", "myscript.agi", request.getScript());
        assertEquals("incorrect requestURL", "agi://host/myscript.agi?param1=my%20value", request.getRequestURL());
        assertEquals("incorrect value for parameter 'param1'", "my value", request.getParameter("param1"));
    }
}