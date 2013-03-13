
package com.geocent.security.audit;

import com.sun.xacml.ParsingException;
import com.sun.xacml.attr.*;
import java.text.ParseException;

/**
 *
 * @author bpriest
 */
public class AttributeFactory {
    
    public static AttributeValue getAttribute(String dataType, String value) throws ParsingException, ParseException{
        
        if(dataType.equals(StringAttribute.identifier))
            return StringAttribute.getInstance(value);
        if(dataType.equals(DateAttribute.identifier))
            return DateAttribute.getInstance(value);
        if(dataType.equals(Base64BinaryAttribute.identifier))
            return Base64BinaryAttribute.getInstance(value);
        if(dataType.equals(BooleanAttribute.identifier))
            return BooleanAttribute.getInstance(value);
        if(dataType.equals(DateTimeAttribute.identifier))
            return DateTimeAttribute.getInstance(value);
        if(dataType.equals(DayTimeDurationAttribute.identifier))
            return DayTimeDurationAttribute.getInstance(value);
        if(dataType.equals(DoubleAttribute.identifier))
            return DoubleAttribute.getInstance(value);
        if(dataType.equals(HexBinaryAttribute.identifier))
            return HexBinaryAttribute.getInstance(value);
        if(dataType.equals(IntegerAttribute.identifier))
            return IntegerAttribute.getInstance(value);
        if(dataType.equals(TimeAttribute.identifier))
            return TimeAttribute.getInstance(value);
        if(dataType.equals(X500NameAttribute.identifier))
            return X500NameAttribute.getInstance(value);
        if(dataType.equals(YearMonthDurationAttribute.identifier))
            return YearMonthDurationAttribute.getInstance(value);
        return null;
    }
    
    public static String getStringValue(AttributeValue attr){
        if(attr instanceof StringAttribute)
            return ((StringAttribute) attr).getValue();
        if(attr instanceof BooleanAttribute)
            return String.valueOf(((BooleanAttribute) attr).getValue());
        if(attr instanceof DateTimeAttribute)
            return String.valueOf(((DateTimeAttribute) attr).getValue());
        if(attr instanceof DateAttribute)
            return ((DateAttribute) attr).toString();
        if(attr instanceof Base64BinaryAttribute)
            return String.valueOf(((Base64BinaryAttribute) attr).getValue());
        if(attr instanceof DayTimeDurationAttribute)
            return ((DayTimeDurationAttribute) attr).toString();
        if(attr instanceof DoubleAttribute)
            return String.valueOf(((DoubleAttribute) attr).getValue());
        if(attr instanceof IntegerAttribute)
            return String.valueOf(((IntegerAttribute) attr).getValue());
        return attr.toString();
    }
}
