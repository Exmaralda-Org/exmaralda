/**
 * wlfxb - a library for creating and processing of TCF data streams.
 *
 * Copyright (C) University of Tübingen.
 *
 * This file is part of wlfxb.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.clarin.weblicht.wlfxb.tc.api;

import eu.clarin.weblicht.wlfxb.io.WLFormatException;
import java.util.List;

/**
 * The <tt>TextStructureLayer</tt> preserves the original structure of a written 
 * text. Within the layer, a token sequence can be annotated as belonging to 
 * some text structure element, such as to a page, a paragraph, a line, a title, 
 * etc. textspan element represents text structure annotation on a token sequence. 
 * The token sequence is specified by start token id and end token id. The type 
 * of the text structure element is specified by the corresponding attribute. 
 * 
 * @author Yana Panchenko
 */
public interface TextStructureLayer extends TextCorpusLayer {

    public TextSpan getSpan(int index);

    public List<TextSpan> getSpans(Token token);

    //public TextSpan getSpan(Token token, TextSpanType type);
    public TextSpan getSpan(Token token, String type);

    //public List<TextSpan> getSpans(TextSpanType type);
    public List<TextSpan> getSpans(String type);
    
    public Token[] getTokens(TextSpan span);

    //public TextSpan addSpan(Token spanStart, Token spanEnd, TextSpanType type);
    
    public TextSpan addSpan(Token spanStart, Token spanEnd, String type);
    
    public TextSpan addSpan(Token spanStart, Token spanEnd, String type, int startChar, int endChar);
    
    public TextSpan addSpan(Token spanStart, Token spanEnd, String type, String value);
    
    public TextSpan addSpan(TextSpan parentSpan, Token spanStart, Token spanEnd, String type) throws WLFormatException;
    
    public TextSpan addSpan(TextSpan parentSpan, Token spanStart, Token spanEnd, String type, int startChar, int endChar) throws WLFormatException;
    
    public TextSpan addSpan(TextSpan parentSpan, Token spanStart, Token spanEnd, String type, String value) throws WLFormatException;
    
}
