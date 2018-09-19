/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package io.adaptivecards.objectmodel;

public class NumberInputParser extends BaseCardElementParser {
  private transient long swigCPtr;
  private transient boolean swigCMemOwnDerived;

  protected NumberInputParser(long cPtr, boolean cMemoryOwn) {
    super(AdaptiveCardObjectModelJNI.NumberInputParser_SWIGSmartPtrUpcast(cPtr), true);
    swigCMemOwnDerived = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(NumberInputParser obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwnDerived) {
        swigCMemOwnDerived = false;
        AdaptiveCardObjectModelJNI.delete_NumberInputParser(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public NumberInputParser() {
    this(AdaptiveCardObjectModelJNI.new_NumberInputParser__SWIG_0(), true);
  }

  public NumberInputParser(NumberInputParser arg0) {
    this(AdaptiveCardObjectModelJNI.new_NumberInputParser__SWIG_1(NumberInputParser.getCPtr(arg0), arg0), true);
  }

  public BaseCardElement Deserialize(ElementParserRegistration elementParserRegistration, ActionParserRegistration actionParserRegistration, AdaptiveCardParseWarningVector warnings, JsonValue root) {
    long cPtr = AdaptiveCardObjectModelJNI.NumberInputParser_Deserialize(swigCPtr, this, ElementParserRegistration.getCPtr(elementParserRegistration), elementParserRegistration, ActionParserRegistration.getCPtr(actionParserRegistration), actionParserRegistration, AdaptiveCardParseWarningVector.getCPtr(warnings), warnings, JsonValue.getCPtr(root), root);
    return (cPtr == 0) ? null : new BaseCardElement(cPtr, true);
  }

  public BaseCardElement DeserializeFromString(ElementParserRegistration elementParserRegistration, ActionParserRegistration actionParserRegistration, AdaptiveCardParseWarningVector warnings, String jsonString) {
    long cPtr = AdaptiveCardObjectModelJNI.NumberInputParser_DeserializeFromString(swigCPtr, this, ElementParserRegistration.getCPtr(elementParserRegistration), elementParserRegistration, ActionParserRegistration.getCPtr(actionParserRegistration), actionParserRegistration, AdaptiveCardParseWarningVector.getCPtr(warnings), warnings, jsonString);
    return (cPtr == 0) ? null : new BaseCardElement(cPtr, true);
  }

}
