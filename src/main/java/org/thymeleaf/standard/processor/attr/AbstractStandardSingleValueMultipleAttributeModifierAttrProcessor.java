/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.standard.processor.attr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardSingleValueMultipleAttributeModifierAttrProcessor 
        extends AbstractAttributeModifierAttrProcessor {





    protected AbstractStandardSingleValueMultipleAttributeModifierAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    protected AbstractStandardSingleValueMultipleAttributeModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    




    @Override
    protected final Map<String, String> getModifiedAttributeValues(
            final Arguments arguments, final Element element, final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);
        
        final Set<String> newAttributeNames = 
                getModifiedAttributeNames(arguments, element, attributeName, attributeValue, expression);

        if (!applyConversion(arguments, element, attributeName)) {

            final Object valueForAttributes = expression.execute(configuration, arguments);

            final Map<String,String> result = new HashMap<String,String>(newAttributeNames.size() + 1, 1.0f);
            for (final String newAttributeName : newAttributeNames) {
                result.put(newAttributeName, (valueForAttributes == null? "" : valueForAttributes.toString()));
            }

            return result;

        }

        final Object valueForAttributes =
                expression.execute(configuration, arguments, StandardExpressionExecutionContext.NORMAL_WITH_TYPE_CONVERSION);

        final IStandardConversionService conversionService = StandardExpressions.getConversionService(configuration);

        final Map<String,String> result = new HashMap<String,String>(newAttributeNames.size() + 1, 1.0f);
        for (final String newAttributeName : newAttributeNames) {
            final String convertedValue =
                    (valueForAttributes == null? null : conversionService.convert(configuration, arguments, valueForAttributes, String.class));
            result.put(newAttributeName, (convertedValue == null? "" : convertedValue));
        }

        return result;

    }


    protected abstract Set<String> getModifiedAttributeNames(final Arguments arguments,
            final Element element, final String attributeName, final String attributeValue, final IStandardExpression expression);





    @Override
    protected boolean recomputeProcessorsAfterExecution(final Arguments arguments,
            final Element element, final String attributeName) {
        return false;
    }



    protected boolean applyConversion(final Arguments arguments, final Element element, final String attributeName) {
        return false;
    }

    
}
