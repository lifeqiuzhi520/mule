/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.functional.api.component;


import static java.lang.System.lineSeparator;
import static org.mule.runtime.core.api.util.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

public class StacktraceLogChecker extends AbstractLogChecker {

  private List<MethodCall> expectedCalls = new ArrayList<>();
  private List<ExceptionCause> expectedExceptionCauses = new ArrayList<>();

  @Override
  public void check(String logMessage) {
    StringBuilder errors = new StringBuilder();
    List<String> stackTraceLines = getStacktraceLinesFromLogLines(splitLines(logMessage));
    List<MethodCall> actualStackCalls = new ArrayList<>();
    List<ExceptionCause> actualExceptionCauses = new ArrayList<>();
    for (String line : stackTraceLines) {
      saveLineAsMatchingPojo(line, actualStackCalls, actualExceptionCauses);
    }
    validateCalls(actualStackCalls, errors);
    validateCauses(actualExceptionCauses, errors);
    String errorMessage = errors.toString();
    if (!StringUtils.isBlank(errorMessage)) {
      throw new AssertionError(lineSeparator() + errorMessage);
    }
  }

  private void validateCalls(List<MethodCall> actualCalls, StringBuilder errors) {
    for (MethodCall call : expectedCalls) {
      if (!actualCalls.contains(call)) {
        errors.append(String.format("Expected method call not found in stacktrace: %s", call.toString()));
        errors.append(lineSeparator());
      }
    }
  }

  private void validateCauses(List<ExceptionCause> actualCauses, StringBuilder errors) {
    for (ExceptionCause cause : expectedExceptionCauses) {
      if (!actualCauses.contains(cause)) {
        errors.append(String.format("Expected exception cause not found in stacktrace: %s", cause.toString()));
        errors.append(lineSeparator());
      }
    }
  }

  private void saveLineAsMatchingPojo(String line, List<MethodCall> actualCalls, List<ExceptionCause> actualCauses) {
    Matcher stackTraceMatcher = STACKTRACE_METHOD_CALL_REGEX_PATTERN.matcher(line);
    if (stackTraceMatcher.matches()) {
      actualCalls.add(createMethodCallFromMatcher(stackTraceMatcher));
    } else {
      Matcher exceptionCauseMatcher = STACKTRACE_EXCEPTION_CAUSE_REGEX_PATTERN.matcher(line);
      if (exceptionCauseMatcher.matches()) {
        actualCauses.add(createExceptionCauseFromMatcher(exceptionCauseMatcher));
      }
    }
  }

  private MethodCall createMethodCallFromMatcher(Matcher matcher) {
    //If no line number found, probably due to native method
    if (matcher.group(4).equals(StringUtils.EMPTY)) {
      return new MethodCall(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    return new MethodCall(matcher.group(1), matcher.group(2), matcher.group(3), Integer.parseInt(matcher.group(4)));
  }

  private ExceptionCause createExceptionCauseFromMatcher(Matcher matcher) {
    return new ExceptionCause(matcher.group(2));
  }

  public void setExpectedCalls(List<MethodCall> expectedCalls) {
    this.expectedCalls = expectedCalls;
  }

  public List<MethodCall> getExpectedCalls() {
    return this.expectedCalls;
  }

  public List<ExceptionCause> getExpectedExceptionCauses() {
    return expectedExceptionCauses;
  }

  public void setExpectedExceptionCauses(List<ExceptionCause> expectedExceptionCauses) {
    this.expectedExceptionCauses = expectedExceptionCauses;
  }

  public static class MethodCall {

    private String packageName = null;
    private String clazz = null;
    private String method = null;
    Integer lineNumber = null;

    private void setFields(String packageName, String clazz, String method) {
      this.packageName = packageName;
      this.clazz = clazz;
      this.method = method;
    }

    public MethodCall() {}

    public MethodCall(String packageName, String clazz, String method) {
      setFields(packageName, clazz, method);
    }

    public MethodCall(String packageName, String clazz, String method, Integer lineNumber) {
      setFields(packageName, clazz, method);
      this.lineNumber = lineNumber;
    }

    public boolean isLineNumberSet() {
      return this.lineNumber != null;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public String getClazz() {
      return clazz;
    }

    public void setClazz(String clazz) {
      this.clazz = clazz;
    }

    public String getMethod() {
      return method;
    }

    public void setMethod(String method) {
      this.method = method;
    }

    public Integer getLineNumber() {
      return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
      this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
      String packageNameString = packageName != null ? packageName : "(any)";
      String classString = clazz != null ? clazz : "(any)";
      String methodString = method != null ? method : "(any)";
      String lineString = lineNumber != null ? Integer.toString(lineNumber) : "(any)";
      return String.format("%s.%s.%s:%s", packageNameString, classString, methodString, lineString);
    }

    @Override
    public int hashCode() {
      int methodHashCode = method != null ? method.hashCode() : 7;
      int packageHashCode = packageName != null ? packageName.hashCode() : 7;
      int classHashCode = clazz != null ? clazz.hashCode() : 7;
      int lineHashCode = lineNumber != null ? lineNumber.hashCode() : 7;
      return methodHashCode + packageHashCode + classHashCode + lineHashCode;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof MethodCall)) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      if (method != null && !method.equals(((MethodCall) obj).method)) {
        return false;
      }
      if (clazz != null && !clazz.equals(((MethodCall) obj).clazz)) {
        return false;
      }
      if (packageName != null && !packageName.equals(((MethodCall) obj).packageName)) {
        return false;
      }
      if (lineNumber != null && !lineNumber.equals(((MethodCall) obj).lineNumber)) {
        return false;
      }
      return true;
    }
  }

  public static class ExceptionCause {

    private String exception = EMPTY;

    public ExceptionCause(String exception) {
      this.exception = exception;
    }

    public ExceptionCause() {}

    public String getException() {
      return exception;
    }

    public void setException(String exception) {
      this.exception = exception;
    }

    @Override
    public String toString() {
      return this.exception;
    }

    @Override
    public int hashCode() {
      return exception.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ExceptionCause)) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      return exception.equals(((ExceptionCause) obj).exception);
    }


  }

}
