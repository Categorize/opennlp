///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 OpenNlp
// 
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
// 
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
// 
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.tools.tokenize;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * Tests for the {@link TokenizerME} class.
 * 
 * This test trains the tokenizer with a few sample tokens
 * and then predicts a token. This test checks if the
 * tokenizer code can be executed.
 * 
 * @see TokenizerME
 */
public class TokenizerMETest extends TestCase {

  public void testTokenizer() throws IOException {
    
    TokenizerModel model = TokenizerTestUtil.createMaxentTokenModel();
    
    TokenizerME tokenizer = new TokenizerME(model);
    
    String tokens[] = tokenizer.tokenize("test,");
    
    assertEquals(2, tokens.length);
    assertEquals("test", tokens[0]);
    assertEquals(",", tokens[1]);
  }
}