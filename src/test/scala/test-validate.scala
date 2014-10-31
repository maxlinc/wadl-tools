package com.rackspace.cloud.api.wadl.test

import scala.xml._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._

import com.rackspace.cloud.api.wadl.WADLFormat._
import com.rackspace.cloud.api.wadl.XSDVersion._
import com.rackspace.cloud.api.wadl.RType._
import com.rackspace.cloud.api.wadl.Converters._

import org.xml.sax.SAXParseException

import com.typesafe.scalalogging.slf4j.LazyLogging

import java.io.File
import java.io.ByteArrayOutputStream

import javax.xml.transform.stream.StreamSource
import javax.xml.transform.stream.StreamResult
import com.rackspace.cloud.api.wadl.InvalidProgramListingException

@RunWith(classOf[JUnitRunner])
class BadProgramListingSpec extends BaseProgramListingSpec with LazyLogging {
  //
  //  Register some common prefixes, you'll need the for XPath
  //  assertions.
  //
  register ("xsd", "http://www.w3.org/2001/XMLSchema")
  register ("wadl","http://wadl.dev.java.net/2009/02")

  feature ("The WADLProgramListingValidator should validate program listing syntax for the specified language.") {
    scenario ("A WADL with an invalid json program listing should not be validated") {
     Given("a WADL with an invalid json program listing")
     val inWADL =
        <application xmlns="http://wadl.dev.java.net/2009/02"
                     xmlns:xsd="http://www.w3.org/2001/XMLSchema">
          <resources base="https://test.api.openstack.com">
            <resource path="a/b">
              <resource path="c">
                <method name="POST" rax:id="createNotification">
                  <request>
                    <representation mediaType="application/xml" xml:lang="EN" element="csapi">
                      <wadl:doc xmlns="http://docbook.org/ns/docbook" title="Create webhook notification: XML request">
                        <xsdxt:code>
                          <programlisting language="json">
                          {raw"""{"label": "my webhook #1", "type": "webhook""details":{"url": "https://systems.example.org/alert"} } """}
                          </programlisting>
                        </xsdxt:code>
                      </wadl:doc>
                    </representation>
                  </request>
                </method>
              </resource>
            </resource>
          </resources>
        </application>
      When("the WADL is validated")
      val thrown = intercept[InvalidProgramListingException] {
        val result = wadl.validate(inWADL)
      }
      Then("The result should be foo")
      assert(thrown.getMessage().contains("Invalid programlisting found"))
    }

    scenario ("A WADL with an valid json program listing should be validated") {
     Given("a WADL with an valid json program listing")
     val inWADL =
        <application xmlns="http://wadl.dev.java.net/2009/02"
                     xmlns:xsd="http://www.w3.org/2001/XMLSchema">
          <resources base="https://test.api.openstack.com">
            <resource path="a/b">
              <resource path="c">
                <method name="POST" rax:id="createNotification">
                  <request>
                    <representation mediaType="application/xml" xml:lang="EN" element="csapi">
                      <wadl:doc xmlns="http://docbook.org/ns/docbook" title="Create webhook notification: XML request">
                        <xsdxt:code>
                          <programlisting language="json">
                          {raw"""{"label": "my webhook #1", "type": "webhook", "details":{"url": "https://systems.example.org/alert"} } """}
                          </programlisting>
                        </xsdxt:code>
                      </wadl:doc>
                    </representation>
                  </request>
                </method>
              </resource>
            </resource>
          </resources>
        </application>
      When("the WADL is validated")
      val result = wadl.validate(inWADL)
      Then("The result should be true")
      assert(result == true)
    }

    scenario ("A WADL with an invalid xml program listing should not be validated") {
     Given("a WADL with an invalid json program listing")
     val inWADL =
        <application xmlns="http://wadl.dev.java.net/2009/02"
                     xmlns:xsd="http://www.w3.org/2001/XMLSchema">
          <resources base="https://test.api.openstack.com">
            <resource path="a/b">
              <resource path="c">
                <method name="POST" rax:id="createNotification">
                  <request>
                    <representation mediaType="application/xml" xml:lang="EN" element="csapi">
                      <wadl:doc xmlns="http://docbook.org/ns/docbook" title="Create webhook notification: XML request">
                        <xsdxt:code>
                          <programlisting language="xml">&lt;?xml version="1.0" encoding="utf-8"?&gt;
                          &lt;notification&gt;
                            &lt;label&gt;my webhook #1&lt;/label&gt;
                            &lt;type&gt;webhook&lt;/type&gt;
                            &lt;details&gt;
                              &lt;url&gt;https://systems.example.org/alert&lt;/url&gt;
                            &lt;details&gt;
                          &lt;/notification&gt;
                          </programlisting>
                        </xsdxt:code>
                      </wadl:doc>
                    </representation>
                  </request>
                </method>
              </resource>
            </resource>
          </resources>
        </application>
      When("the WADL is validated")
      val thrown = intercept[InvalidProgramListingException] {
        val result = wadl.validate(inWADL)
      }
      Then("The result should be foo")
      assert(thrown.getMessage().contains("Invalid programlisting found"))
    }

    scenario ("A WADL with an valid xml program listing should be validated") {
     Given("a WADL with an valid xml program listing")
     val inWADL =
        <application xmlns="http://wadl.dev.java.net/2009/02"
                     xmlns:xsd="http://www.w3.org/2001/XMLSchema">
          <resources base="https://test.api.openstack.com">
            <resource path="a/b">
              <resource path="c">
                <method name="POST" rax:id="createNotification">
                  <request>
                    <representation mediaType="application/xml" xml:lang="EN" element="csapi">
                      <wadl:doc xmlns="http://docbook.org/ns/docbook" title="Create webhook notification: XML request">
                        <xsdxt:code>
                          <programlisting language="xml">&lt;?xml version="1.0" encoding="utf-8"?&gt;
                          &lt;notification&gt;
                            &lt;label&gt;my webhook #1&lt;/label&gt;
                            &lt;type&gt;webhook&lt;/type&gt;
                            &lt;details&gt;
                              &lt;url&gt;https://systems.example.org/alert&lt;/url&gt;
                            &lt;/details&gt;
                          &lt;/notification&gt;
                          </programlisting>
                        </xsdxt:code>
                      </wadl:doc>
                    </representation>
                  </request>
                </method>
              </resource>
            </resource>
          </resources>
        </application>
      When("the WADL is validated")
      val result = wadl.validate(inWADL)
      Then("The result should be true")
      assert(result == true)
    }
  }
}
