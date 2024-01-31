package com.virtuslab.scala3.scalajs.compiler.servlet

import java.io.InputStream
import java.net.URL
import javax.servlet.descriptor.JspConfigDescriptor
import javax.servlet.{Filter, FilterRegistration, RequestDispatcher, Servlet, ServletContext, ServletRegistration, SessionCookieConfig, SessionTrackingMode}
import java.{util => ju}

class TestContext extends ServletContext {
  override def getContext(uripath: String): ServletContext = ???
  override def getServlet(name: String): Servlet = ???
  override def getVirtualServerName(): String = ???
  override def removeAttribute(name: String): Unit = ???
  override def createServlet[T <: Servlet](clazz: Class[T]): T = ???
  override def getServlets(): ju.Enumeration[Servlet] = ???
  override def getMajorVersion(): Int = ???
  override def getServletRegistration(servletName: String): ServletRegistration = ???
  override def getSessionCookieConfig(): SessionCookieConfig = ???
  override def getRequestDispatcher(path: String): RequestDispatcher = ???
  override def getDefaultSessionTrackingModes(): ju.Set[SessionTrackingMode] = ???
  override def getInitParameterNames(): ju.Enumeration[String] = ???
  override def createListener[T <: ju.EventListener](clazz: Class[T]): T = ???
  override def getRealPath(path: String): String = ???
  override def getAttribute(name: String): Object = ???
  override def getJspConfigDescriptor(): JspConfigDescriptor = ???
  override def getInitParameter(name: String): String = ???
  override def getNamedDispatcher(name: String): RequestDispatcher = ???
  override def setAttribute(name: String, `object`: Object): Unit = ???
  override def getResourcePaths(path: String): ju.Set[String] = ???
  override def getFilterRegistration(filterName: String): FilterRegistration = ???
  override def getServletNames(): ju.Enumeration[String] = ???
  override def getEffectiveMajorVersion(): Int = ???
  override def getServerInfo(): String = ???
  override def getEffectiveMinorVersion(): Int = ???
  override def getFilterRegistrations(): ju.Map[String, _ <: FilterRegistration] = ???
  override def getContextPath(): String = ???
  override def addFilter(filterName: String, filterClass: Class[? <: Filter]): FilterRegistration.Dynamic = ???
  override def addFilter(filterName: String, filter: Filter): FilterRegistration.Dynamic = ???
  override def addFilter(filterName: String, className: String): FilterRegistration.Dynamic = ???
  override def getResource(path: String): URL = ???
  override def createFilter[T <: Filter](clazz: Class[T]): T = ???
  override def setInitParameter(name: String, value: String): Boolean = ???
  override def addServlet(servletName: String, servletClass: Class[? <: Servlet]): ServletRegistration.Dynamic = ???
  override def addServlet(servletName: String, servlet: Servlet): ServletRegistration.Dynamic = ???
  override def addServlet(servletName: String, className: String): ServletRegistration.Dynamic = ???
  override def getServletRegistrations(): ju.Map[String, _ <: ServletRegistration] = ???
  override def getMimeType(file: String): String = ???
  override def getServletContextName(): String = ???
  override def declareRoles(roleNames: String*): Unit = ???
  override def setSessionTrackingModes(sessionTrackingModes: ju.Set[SessionTrackingMode]): Unit = ???
  override def addListener(listenerClass: Class[? <: ju.EventListener]): Unit = ???
  override def addListener[T <: ju.EventListener](t: T): Unit = ???
  override def addListener(className: String): Unit = ???
  override def getMinorVersion(): Int = ???
  override def getAttributeNames(): ju.Enumeration[String] = ???
  override def getEffectiveSessionTrackingModes(): ju.Set[SessionTrackingMode] = ???
  override def log(message: String, throwable: Throwable): Unit = ???
  override def log(exception: Exception, msg: String): Unit = ???
  override def log(msg: String): Unit = ???
  override def getClassLoader(): ClassLoader = this.getClass.getClassLoader()
  override def getResourceAsStream(path: String): InputStream =
    getClass().getClassLoader().getResourceAsStream(path)

}
