package com.rackspace.cloud.api.wadl

import java.io.File
import org.json4s._
import org.json4s.jackson.JsonMethods._
import com.typesafe.scalalogging.slf4j.LazyLogging
import com.rackspace.cloud.api.wadl.util.XMLWithLocationLoader
import scala.collection.mutable.ListBuffer

class WADLValidationException(msg: String) extends RuntimeException(msg) {
  def this(msg: String, causes: List[Throwable]) = {
    this(msg + causes.map( c => c.getMessage ).mkString("\n  "))
  }
}

class InvalidProgramListingException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)

class WADLProgramListingValidator() extends LazyLogging {
  def validate(listing : scala.xml.Node) : Boolean = {
    val lineNumber = listing \ "@line"
    val column = listing \ "@column"

    try {
      (listing \ "@language").text match {
        case "json" | "javascript" => parse(listing.text)
        case "xml" => XMLWithLocationLoader.loadString(listing.text)
      }
    } catch {
      case cause: Exception => {
        val msg = cause.getMessage
        val error = new InvalidProgramListingException(s"Invalid programlisting found (line: $lineNumber, column: $column): $msg", cause)
        logger.error(error.getMessage)
        throw error
      }
    }

    true
  }

  def validateAll(xml : scala.xml.Elem) : Boolean = {
    val exceptions = new ListBuffer[InvalidProgramListingException]()

    for (listing <- eachProgramListing(xml)) {
      try {
        validate(listing)
      } catch {
        case e: InvalidProgramListingException => {
          exceptions += e
        }
      }
    }

    if (!exceptions.isEmpty) {
      val error = new WADLValidationException("Invalid progamlistings detected: ", exceptions.toList)
      throw error
    }

    true
  }

  def validateAll(string : String) : Boolean = {
    validateAll(XMLWithLocationLoader.loadString(string))
  }

  def validateAll(file : File) : Boolean = {
    validateAll(XMLWithLocationLoader.loadFile(file))
  }

  def validate(string : String) : Boolean = {
    validate(XMLWithLocationLoader.loadString(string))
  }

  def eachProgramListing(xml : scala.xml.Elem) = {
    for {
      node <- xml \\ "programlisting"
    } yield {node}
  }
}
