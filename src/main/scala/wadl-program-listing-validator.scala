package com.rackspace.cloud.api.wadl

import java.io.File
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.xml._
import com.typesafe.scalalogging.slf4j.LazyLogging

class InvalidProgramListingException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)

class WADLProgramListingValidator() extends LazyLogging {
  def validate(xml : scala.xml.Elem) : Boolean = {
    for (listing <- each_program_listing(xml)) {
      try {
        (listing \ "@language").text match {
          case "json" | "javascript" => parse(listing.text)
          case "xml" => XML.loadString(listing.text)
        }
      } catch {
        case e: Exception => throw new InvalidProgramListingException("Invalid programlisting found (location unknown)", e)
      }
    }
    true
  }

  def validate(xml : File) : Boolean = {
    validate(scala.xml.XML.loadFile(xml))
  }

  def each_program_listing(xml : scala.xml.Elem) = {
    for {
      node <- xml \\ "programlisting"
    } yield {node}
  }
}
