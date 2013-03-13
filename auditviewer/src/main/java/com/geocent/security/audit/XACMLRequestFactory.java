/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geocent.security.audit;

import com.sun.xacml.Indenter;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bpriest
 */
public class XACMLRequestFactory {

    public static String createXacmlRequest(PEPAuditEvent event) {
        RequestCtx requestContext = new RequestCtx(
                setupSubjects(event.getSubjectAttributes()),
                setupAttributes(event.getResourceAttributes()),
                setupAttributes(event.getActionAttributes()),
                setupAttributes(event.getEnvironmentAttributes()));
        ByteArrayOutputStream request = new ByteArrayOutputStream();
        requestContext.encode(request, new Indenter());
        return new String(request.toByteArray());
    }

    private static Set<Attribute> setupAttributes(Map<URI, AttributeValue> map) {
        Set<Attribute> attrSet = new HashSet<Attribute>();

        if (map == null || map.isEmpty()) {
            return attrSet;
        }

        for (URI uri : map.keySet()) {
            attrSet.add(new Attribute(uri, null, null, map.get(uri)));
        }

        return attrSet;
    }

    private static Set<Subject> setupSubjects(Map<URI, AttributeValue> subjMap) {
        Set<Subject> subjects = new HashSet<Subject>();

        if (subjMap == null || subjMap.isEmpty()) {
            subjects.add(new Subject(new HashSet<Attribute>()));
            return subjects;
        }

        Set<Attribute> attributes = new HashSet<Attribute>();

        for (URI uri : subjMap.keySet()) {
            attributes.add(new Attribute(uri, null, null, subjMap.get(uri)));
        }

        subjects.add(new Subject(attributes));

        return subjects;
    }
}
