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
import com.rackspace.cloud.api.wadl.WADLValidationException

@RunWith(classOf[JUnitRunner])
class BadProgramListingSpec extends BaseProgramListingSpec with LazyLogging {
  //
  //  Register some common prefixes, you'll need the for XPath
  //  assertions.
  //
  register ("xsd", "http://www.w3.org/2001/XMLSchema")
  register ("wadl","http://wadl.dev.java.net/2009/02")

  feature ("The WADLProgramListingValidator should validate program listing syntax for the specified language.") {
    scenario ("an invalid json program listing should not be validated") {
     Given("an invalid json program listing")
     val listing =
        <programlisting language="json">
        {raw"""{"label": "my webhook #1", "type": "webhook""details":{"url": "https://systems.example.org/alert"} } """}
        </programlisting>
      When("the WADL is validated")
      val thrown = intercept[InvalidProgramListingException] {
        val result = wadl.validate(listing.toString)
      }
      Then("The result should be foo")
      assert(thrown.getMessage().contains("Invalid programlisting found (line: 3, column: 26)"))
    }

    scenario ("a valid json program listing should be validated") {
     Given("a valid json program listing")
     val listing =
        <programlisting language="json">
        {raw"""{"label": "my webhook #1", "type": "webhook", "details":{"url": "https://systems.example.org/alert"} } """}
        </programlisting>
      When("the WADL is validated")
      val result = wadl.validate(listing.toString)
      Then("The result should be true")
      assert(result == true)
    }

    scenario ("an invalid xml program listing should not be validated") {
     Given("an invalid json program listing")
     val listing =
        <programlisting language="xml">&lt;?xml version="1.0" encoding="utf-8"?&gt;
        &lt;notification&gt;
          &lt;label&gt;my webhook #1&lt;/label&gt;
          &lt;type&gt;webhook&lt;/type&gt;
          &lt;details&gt;
            &lt;url&gt;https://systems.example.org/alert&lt;/url&gt;
          &lt;details&gt;
        &lt;/notification&gt;
        </programlisting>
      When("the WADL is validated")
      val thrown = intercept[InvalidProgramListingException] {
        val result = wadl.validate(listing.toString)
      }
      Then("The result should be foo")
      assert(thrown.getMessage().contains("Invalid programlisting found (line: 9, column: 26):"))
    }

    scenario ("a valid xml program listing should be validated") {
     Given("a valid xml program listing")
     val listing =
        <programlisting language="xml">&lt;?xml version="1.0" encoding="utf-8"?&gt;
        &lt;notification&gt;
          &lt;label&gt;my webhook #1&lt;/label&gt;
          &lt;type&gt;webhook&lt;/type&gt;
          &lt;details&gt;
            &lt;url&gt;https://systems.example.org/alert&lt;/url&gt;
          &lt;/details&gt;
        &lt;/notification&gt;
        </programlisting>
      When("the WADL is validated")
      val result = wadl.validate(listing.toString)
      Then("The result should be true")
      assert(result == true)
    }
  }

  feature ("The WADLProgramListingValidator validateAll should report about all exceptions.") {
    scenario ("a WADL file with no invalid programlistings") {
      Given("a WADL file with all valid programlisting")
      val inWADL = new File("src/test/resources/fixtures/valid_listings.wadl")

      When("the WADL is fully validated")
      val result = wadl.validateAll(inWADL)
      Then("The result should be true")
      assert(result == true)
    }

    scenario ("a WADL file with some invalid programlistings") {
      Given("a WADL file with some invalid programlistings")
      val inWADL = new File("src/test/resources/fixtures/invalid_listings.wadl")
      When("the WADL is fully validated")
      val thrown = intercept[WADLValidationException] {
        val result = wadl.validateAll(inWADL)
      }
      Then("The result should be true")
      val errorMsg = thrown.getMessage()
      assert(errorMsg.contains("Invalid progamlistings detected"))
      assert(errorMsg.contains("Invalid programlisting found (line:"))
    }
  }
}
