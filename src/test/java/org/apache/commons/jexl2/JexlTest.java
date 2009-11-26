/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jexl2;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jexl2.parser.ParseException;
import org.apache.commons.jexl2.parser.Parser;

/**
 *  Simple testcases
 *
 *  @since 1.0
 *  @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 *  @version $Id$
 */
public class JexlTest extends JexlTestCase
{
    protected static final String METHOD_STRING = "Method string";
    protected static final String GET_METHOD_STRING = "GetMethod string";

    public JexlTest(String testName)
    {
        super(testName);
    }

    /**
      *  test a simple property expression
      */
    public void testProperty()
         throws Exception
    {
        /*
         *  tests a simple property expression
         */

        Expression e = JEXL.createExpression("foo.bar");
        JexlContext jc = new JexlContext.Mapped();

        jc.setJexlVariable("foo", new Foo() );
        Object o = e.evaluate(jc);

        assertTrue("o not instanceof String", o instanceof String);
        assertEquals("o incorrect", GET_METHOD_STRING, o);
    }

    public void testBoolean()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo", new Foo() );
        jc.setJexlVariable("a", Boolean.TRUE);
        jc.setJexlVariable("b", Boolean.FALSE);

        assertExpression(jc, "foo.convertBoolean(a==b)", "Boolean : false");
        assertExpression(jc, "foo.convertBoolean(a==true)", "Boolean : true");
        assertExpression(jc, "foo.convertBoolean(a==false)", "Boolean : false");
        assertExpression(jc, "foo.convertBoolean(true==false)", "Boolean : false");
        assertExpression(jc, "true eq false", Boolean.FALSE);
        assertExpression(jc, "true ne false", Boolean.TRUE);
    }

    public void testStringLit()
         throws Exception
    {
        /*
         *  tests a simple property expression
         */
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo", new Foo() );
        assertExpression(jc, "foo.get(\"woogie\")", "Repeat : woogie");
    }

    public void testExpression()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo", new Foo() );
        jc.setJexlVariable("a", Boolean.TRUE);
        jc.setJexlVariable("b", Boolean.FALSE);
        jc.setJexlVariable("num", new Integer(5));
        jc.setJexlVariable("now", Calendar.getInstance().getTime());
        GregorianCalendar gc = new GregorianCalendar(5000, 11, 20);
        jc.setJexlVariable("now2", gc.getTime());
        jc.setJexlVariable("bdec", new BigDecimal("7"));
        jc.setJexlVariable("bint", new BigInteger("7"));

        assertExpression(jc, "a == b", Boolean.FALSE);
        assertExpression(jc, "a==true", Boolean.TRUE);
        assertExpression(jc, "a==false", Boolean.FALSE);
        assertExpression(jc, "true==false", Boolean.FALSE);
        
        assertExpression(jc, "2 < 3", Boolean.TRUE);
        assertExpression(jc, "num < 5", Boolean.FALSE);
        assertExpression(jc, "num < num", Boolean.FALSE);
        assertExpression(jc, "num < null", Boolean.FALSE);
        assertExpression(jc, "num < 2.5", Boolean.FALSE);
        assertExpression(jc, "now2 < now", Boolean.FALSE); // test comparable
//
        assertExpression(jc, "'6' <= '5'", Boolean.FALSE);
        assertExpression(jc, "num <= 5", Boolean.TRUE);
        assertExpression(jc, "num <= num", Boolean.TRUE);
        assertExpression(jc, "num <= null", Boolean.FALSE);
        assertExpression(jc, "num <= 2.5", Boolean.FALSE);
        assertExpression(jc, "now2 <= now", Boolean.FALSE); // test comparable

//        
        assertExpression(jc, "'6' >= '5'", Boolean.TRUE);
        assertExpression(jc, "num >= 5", Boolean.TRUE);
        assertExpression(jc, "num >= num", Boolean.TRUE);
        assertExpression(jc, "num >= null", Boolean.FALSE);
        assertExpression(jc, "num >= 2.5", Boolean.TRUE);
        assertExpression(jc, "now2 >= now", Boolean.TRUE); // test comparable

        assertExpression(jc, "'6' > '5'", Boolean.TRUE);
        assertExpression(jc, "num > 4", Boolean.TRUE);
        assertExpression(jc, "num > num", Boolean.FALSE);
        assertExpression(jc, "num > null", Boolean.FALSE);
        assertExpression(jc, "num > 2.5", Boolean.TRUE);
        assertExpression(jc, "now2 > now", Boolean.TRUE); // test comparable

        assertExpression(jc, "\"foo\" + \"bar\" == \"foobar\"", Boolean.TRUE);

        assertExpression(jc, "bdec > num", Boolean.TRUE);
        assertExpression(jc, "bdec >= num", Boolean.TRUE);
        assertExpression(jc, "num <= bdec", Boolean.TRUE);
        assertExpression(jc, "num < bdec", Boolean.TRUE);
        assertExpression(jc, "bint > num", Boolean.TRUE);
        assertExpression(jc, "bint == bdec", Boolean.TRUE);
        assertExpression(jc, "bint >= num", Boolean.TRUE);
        assertExpression(jc, "num <= bint", Boolean.TRUE);
        assertExpression(jc, "num < bint", Boolean.TRUE);
    }

    public void testEmpty()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("string", "");
        jc.setJexlVariable("array", new Object[0]);
        jc.setJexlVariable("map", new HashMap<Object, Object>());
        jc.setJexlVariable("list", new ArrayList<Object>());
        jc.setJexlVariable("set", (new HashMap<Object, Object>()).keySet());
        jc.setJexlVariable("longstring", "thingthing");

        /*
         *  I can't believe anyone thinks this is a syntax.. :)
         */
        assertExpression(jc, "empty nullthing", Boolean.TRUE);
        assertExpression(jc, "empty string", Boolean.TRUE);
        assertExpression(jc, "empty array", Boolean.TRUE);
        assertExpression(jc, "empty map", Boolean.TRUE);
        assertExpression(jc, "empty set", Boolean.TRUE);
        assertExpression(jc, "empty list", Boolean.TRUE);
        assertExpression(jc, "empty longstring", Boolean.FALSE);
        assertExpression(jc, "not empty longstring", Boolean.TRUE);
    }

    public void testSize()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("s", "five!");
        jc.setJexlVariable("array", new Object[5]);

        Map<String, Integer> map = new HashMap<String, Integer>();

        map.put("1", new Integer(1));
        map.put("2", new Integer(2));
        map.put("3", new Integer(3));
        map.put("4", new Integer(4));
        map.put("5", new Integer(5));

        jc.setJexlVariable("map", map);

        List<String> list = new ArrayList<String>();

        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        jc.setJexlVariable("list", list);

        // 30652 - support for set
        Set<String> set = new HashSet<String>();
        set.addAll(list);
        set.add("1");
        
        jc.setJexlVariable("set", set);
        
        // support generic int size() method
        BitSet bitset = new BitSet(5);
        jc.setJexlVariable("bitset", bitset);

        assertExpression(jc, "size(s)", new Integer(5));
        assertExpression(jc, "size(array)", new Integer(5));
        assertExpression(jc, "size(list)", new Integer(5));
        assertExpression(jc, "size(map)", new Integer(5));
        assertExpression(jc, "size(set)", new Integer(5));
        assertExpression(jc, "size(bitset)", new Integer(64));
        assertExpression(jc, "list.size()", new Integer(5));
        assertExpression(jc, "map.size()", new Integer(5));
        assertExpression(jc, "set.size()", new Integer(5));
        assertExpression(jc, "bitset.size()", new Integer(64));

        assertExpression(jc, "list.get(size(list) - 1)", "5");
        assertExpression(jc, "list[size(list) - 1]", "5");
        assertExpression(jc, "list.get(list.size() - 1)", "5");
    }

    public void testSizeAsProperty() throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("size", "cheese");
        jc.setJexlVariable("map", map);
        jc.setJexlVariable("foo", new Foo());

        assertExpression(jc, "map['size']", "cheese");
// PR - unsure whether or not we should support map.size or force usage of the above 'escaped' version        
//        assertExpression(jc, "map.size", "cheese");
        assertExpression(jc, "foo.getSize()", new Integer(22));
        // failing assertion for size property
        //assertExpression(jc, "foo.size", new Integer(22));
    }

    /**
      *  test the new function e.g constructor invocation
      */
    public void testNew() throws Exception {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("double", Double.class);
        jc.setJexlVariable("foo", "org.apache.commons.jexl2.Foo");
        Expression expr;
        Object value;
        expr = JEXL.createExpression("new(double, 1)");
        value = expr.evaluate(jc);
        assertEquals(expr.toString(), new Double(1.0), value);
        expr = JEXL.createExpression("new('java.lang.Float', 100)");
        value = expr.evaluate(jc);
        assertEquals(expr.toString(), new Float(100.0), value);
        expr = JEXL.createExpression("new(foo).quux");
        value = expr.evaluate(jc);
        assertEquals(expr.toString(), "Repeat : quux", value);
    }

    /**
      *  test some simple mathematical calculations
      */
    public void testCalculations()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();

        /*
         * test to ensure new string cat works
         */
        jc.setJexlVariable("stringy", "thingy" );
        assertExpression(jc, "stringy + 2", "thingy2");

        /*
         * test new null coersion
         */
        jc.setJexlVariable("imanull", null );
        assertExpression(jc, "imanull + 2", new Integer(2));
        assertExpression(jc, "imanull + imanull", new Integer(0));
        
        /* test for bugzilla 31577 */
        jc.setJexlVariable("n", new Integer(0));
        assertExpression(jc, "n != null && n != 0", Boolean.FALSE);
    }

    /**
      *  test some simple conditions
      */
    public void testConditions()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo", new Integer(2) );
        jc.setJexlVariable("aFloat", new Float(1));
        jc.setJexlVariable("aDouble", new Double(2));
        jc.setJexlVariable("aChar", new Character('A'));
        jc.setJexlVariable("aBool", Boolean.TRUE);
        StringBuffer buffer = new StringBuffer("abc");
        List<Object> list = new ArrayList<Object>();
        List<Object> list2 = new LinkedList<Object>();
        jc.setJexlVariable("aBuffer", buffer);
        jc.setJexlVariable("aList", list);
        jc.setJexlVariable("bList", list2);
        
        assertExpression(jc, "foo == 2", Boolean.TRUE);
        assertExpression(jc, "2 == 3", Boolean.FALSE);
        assertExpression(jc, "3 == foo", Boolean.FALSE);
        assertExpression(jc, "3 != foo", Boolean.TRUE);
        assertExpression(jc, "foo != 2", Boolean.FALSE);
        // test float and double equality
        assertExpression(jc, "aFloat eq aDouble", Boolean.FALSE);
        assertExpression(jc, "aFloat ne aDouble", Boolean.TRUE);
        assertExpression(jc, "aFloat == aDouble", Boolean.FALSE);
        assertExpression(jc, "aFloat != aDouble", Boolean.TRUE);
        // test number and character equality
        assertExpression(jc, "foo == aChar", Boolean.FALSE);
        assertExpression(jc, "foo != aChar", Boolean.TRUE);
        // test string and boolean
        assertExpression(jc, "aBool == 'true'", Boolean.TRUE);
        assertExpression(jc, "aBool == 'false'", Boolean.FALSE);
        assertExpression(jc, "aBool != 'false'", Boolean.TRUE);
        // test null and boolean
        assertExpression(jc, "aBool == notThere", Boolean.FALSE);
        assertExpression(jc, "aBool != notThere", Boolean.TRUE);
        // anything and string as a string comparison
        assertExpression(jc, "aBuffer == 'abc'", Boolean.TRUE);
        assertExpression(jc, "aBuffer != 'abc'", Boolean.FALSE);
        // arbitrary equals
        assertExpression(jc, "aList == bList", Boolean.TRUE);
        assertExpression(jc, "aList != bList", Boolean.FALSE);
    }

    /**
      *  test some simple conditions
      */
    public void testNotConditions()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();

        Foo foo = new Foo();
        jc.setJexlVariable("x", Boolean.TRUE );
        jc.setJexlVariable("foo", foo );
        jc.setJexlVariable("bar", "true" );

        assertExpression(jc, "!x", Boolean.FALSE);
        assertExpression(jc, "x", Boolean.TRUE);
        assertExpression(jc, "!bar", Boolean.FALSE);
        assertExpression(jc, "!foo.isSimple()", Boolean.FALSE);
        assertExpression(jc, "foo.isSimple()", Boolean.TRUE);
        assertExpression(jc, "!foo.simple", Boolean.FALSE);
        assertExpression(jc, "foo.simple", Boolean.TRUE);
        assertExpression(jc, "foo.getCheeseList().size() == 3", Boolean.TRUE);
        assertExpression(jc, "foo.cheeseList.size() == 3", Boolean.TRUE);

        jc.setJexlVariable("string", "");
        assertExpression(jc, "not empty string", Boolean.FALSE);
        assertExpression(jc, "not(empty string)", Boolean.FALSE);
        assertExpression(jc, "not empty(string)", Boolean.FALSE);
        assertExpression(jc, "! empty string", Boolean.FALSE);
        assertExpression(jc, "!(empty string)", Boolean.FALSE);
        assertExpression(jc, "!empty(string)", Boolean.FALSE);

    }


    /**
      *  test some simple conditions
      */
    public void testNotConditionsWithDots()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();

        jc.setJexlVariable("x.a", Boolean.TRUE );
        jc.setJexlVariable("x.b", Boolean.FALSE );

        assertExpression(jc, "x.a", Boolean.TRUE);
        assertExpression(jc, "!x.a", Boolean.FALSE);
        assertExpression(jc, "!x.b", Boolean.TRUE);
    }

    /**
      *  test some simple conditions
      */
    public void testComparisons()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo", "the quick and lazy fox" );

        assertExpression(jc, "foo.indexOf('quick') > 0", Boolean.TRUE);
        assertExpression(jc, "foo.indexOf('bar') >= 0", Boolean.FALSE);
        assertExpression(jc, "foo.indexOf('bar') < 0", Boolean.TRUE);
    }

    /**
      *  test some null conditions
      */
    public void testNull()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("bar", new Integer(2) );

        assertExpression(jc, "empty foo", Boolean.TRUE);
        assertExpression(jc, "bar == null", Boolean.FALSE);
        assertExpression(jc, "foo == null", Boolean.TRUE);
        assertExpression(jc, "bar != null", Boolean.TRUE);
        assertExpression(jc, "foo != null", Boolean.FALSE);
        assertExpression(jc, "empty(bar)", Boolean.FALSE);
        assertExpression(jc, "empty(foo)", Boolean.TRUE);
    }

    /** 
     * test quoting in strings 
     */
    public void testStringQuoting() throws Exception {
        JexlContext jc = new JexlContext.Mapped();
        assertExpression(jc, "'\"Hello\"'", "\"Hello\"");
        assertExpression(jc, "\"I'm testing\"", "I'm testing");
    }
    
    /**
      *  test some blank strings
      */
    public void testBlankStrings()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("bar", "" );

        assertExpression(jc, "foo == ''", Boolean.FALSE);
        assertExpression(jc, "bar == ''", Boolean.TRUE);
        assertExpression(jc, "barnotexist == ''", Boolean.FALSE);
        assertExpression(jc, "empty bar", Boolean.TRUE);
        assertExpression(jc, "bar.length() == 0", Boolean.TRUE);
        assertExpression(jc, "size(bar) == 0", Boolean.TRUE);
    }

    /**
      *  test some blank strings
      */
    public void testLogicExpressions()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo", "abc" );
        jc.setJexlVariable("bar", "def" );

        assertExpression(jc, "foo == 'abc' || bar == 'abc'", Boolean.TRUE);
        assertExpression(jc, "foo == 'abc' or bar == 'abc'", Boolean.TRUE);
        assertExpression(jc, "foo == 'abc' && bar == 'abc'", Boolean.FALSE);
        assertExpression(jc, "foo == 'abc' and bar == 'abc'", Boolean.FALSE);

        assertExpression(jc, "foo == 'def' || bar == 'abc'", Boolean.FALSE);
        assertExpression(jc, "foo == 'def' or bar == 'abc'", Boolean.FALSE);
        assertExpression(jc, "foo == 'abc' && bar == 'def'", Boolean.TRUE);
        assertExpression(jc, "foo == 'abc' and bar == 'def'", Boolean.TRUE);
    }


    /**
      *  test variables with underscore names
      */
    public void testVariableNames()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo_bar", "123" );
        
        assertExpression(jc, "foo_bar", "123");
    }

    /**
      *  test the use of dot notation to lookup map entries
      */
    public void testMapDot()
         throws Exception
    {
        Map<String, String> foo = new HashMap<String, String>();
        foo.put( "bar", "123" );

        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo", foo );
        
        assertExpression(jc, "foo.bar", "123");
    }

    /**
     *  Tests string literals
     */
    public void testStringLiterals()
        throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo", "bar" );

        assertExpression(jc, "foo == \"bar\"", Boolean.TRUE);
        assertExpression(jc, "foo == 'bar'", Boolean.TRUE);
    }

    /**
      *  test the use of an int based property
      */
    public void testIntProperty()
         throws Exception
    {
        Foo foo = new Foo();

        // lets check the square function first..
        assertEquals(4, foo.square(2));
        assertEquals(4, foo.square(-2));

        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("foo", foo );

        assertExpression(jc, "foo.count", new Integer(5));
        assertExpression(jc, "foo.square(2)", new Integer(4));
        assertExpression(jc, "foo.square(-2)", new Integer(4));
    }

    /**
      *  test the -1 comparison bug
      */
    public void testNegativeIntComparison()
         throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        Foo foo = new Foo();
        jc.setJexlVariable("foo", foo );

        assertExpression(jc, "foo.count != -1", Boolean.TRUE);
        assertExpression(jc, "foo.count == 5", Boolean.TRUE);
        assertExpression(jc, "foo.count == -1", Boolean.FALSE);
    }

    /**
     * Attempts to recreate bug http://jira.werken.com/ViewIssue.jspa?key=JELLY-8
     */
    public void testCharAtBug()
        throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();

        jc.setJexlVariable("foo", "abcdef");

        assertExpression(jc, "foo.substring(2,4)", "cd");
        assertExpression(jc, "foo.charAt(2)", new Character('c'));
        assertExpression(jc, "foo.charAt(-2)", null);

    }

    public void testEmptyDottedVariableName() throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();

        jc.setJexlVariable( "this.is.a.test", "");

        assertExpression(jc, "empty(this.is.a.test)", Boolean.TRUE);
    }

    public void testEmptySubListOfMap() throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        Map<String, ArrayList<?>> m = new HashMap<String, ArrayList<?>>();
        m.put("aList", new ArrayList<Object>());

        jc.setJexlVariable( "aMap", m );

        assertExpression( jc, "empty( aMap.aList )", Boolean.TRUE );
    }

    public void testCoercionWithComparisionOperators()
        throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();

        assertExpression(jc, "'2' > 1", Boolean.TRUE);
        assertExpression(jc, "'2' >= 1", Boolean.TRUE);
        assertExpression(jc, "'2' >= 2", Boolean.TRUE);
        assertExpression(jc, "'2' < 1", Boolean.FALSE);
        assertExpression(jc, "'2' <= 1", Boolean.FALSE);
        assertExpression(jc, "'2' <= 2", Boolean.TRUE);

        assertExpression(jc, "2 > '1'", Boolean.TRUE);
        assertExpression(jc, "2 >= '1'", Boolean.TRUE);
        assertExpression(jc, "2 >= '2'", Boolean.TRUE);
        assertExpression(jc, "2 < '1'", Boolean.FALSE);
        assertExpression(jc, "2 <= '1'", Boolean.FALSE);
        assertExpression(jc, "2 <= '2'", Boolean.TRUE);
    }

    /**
     * Test that 'and' only evaluates the second item if needed
     * @throws Exception if there are errors
     */
    public void testBooleanShortCircuitAnd() throws Exception
    {
        // handle false for the left arg of 'and'
        Foo tester = new Foo();
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("first", Boolean.FALSE);
        jc.setJexlVariable("foo", tester);
        Expression expr = JEXL.createExpression("first and foo.trueAndModify");
        expr.evaluate(jc);
        assertTrue("Short circuit failure: rhs evaluated when lhs FALSE", !tester.getModified());
        // handle true for the left arg of 'and' 
        tester = new Foo();
        jc.setJexlVariable("first", Boolean.TRUE);
        jc.setJexlVariable("foo", tester);
        expr.evaluate(jc);
        assertTrue("Short circuit failure: rhs not evaluated when lhs TRUE", tester.getModified());
    }
    
    /**
     * Test that 'or' only evaluates the second item if needed
     * @throws Exception if there are errors
     */
    public void testBooleanShortCircuitOr() throws Exception
    {
        // handle false for the left arg of 'or'
        Foo tester = new Foo();
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("first", Boolean.FALSE);
        jc.setJexlVariable("foo", tester);
        Expression expr = JEXL.createExpression("first or foo.trueAndModify");
        expr.evaluate(jc);
        assertTrue("Short circuit failure: rhs not evaluated when lhs FALSE", tester.getModified());
        // handle true for the left arg of 'or' 
        tester = new Foo();
        jc.setJexlVariable("first", Boolean.TRUE);
        jc.setJexlVariable("foo", tester);
        expr.evaluate(jc);
        assertTrue("Short circuit failure: rhs evaluated when lhs TRUE", !tester.getModified());
    }

    /**
     * Simple test of '+' as a string concatenation operator
     * @throws Exception
     */
    public void testStringConcatenation() throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("first", "Hello");
        jc.setJexlVariable("second", "World");
        assertExpression(jc, "first + ' ' + second", "Hello World");
    }

    public void testToString() throws Exception {
        String code = "abcd";
        Expression expr = JEXL.createExpression(code);
        assertEquals("Bad expression value", code, expr.toString());
    }
    
    /**
     * Make sure bad syntax throws ParseException
     * @throws Exception on errors
     */
    public void testBadParse() throws Exception
    {
        try
        {
            assertExpression(new JexlContext.Mapped(), "empty()", null);
            fail("Bad expression didn't throw ParseException");
        }
        catch (JexlException pe)
        {
            // expected behaviour
        }
    }

    /**
     * Test the ## comment in a string
     * @throws Exception
     */
    public void testComment() throws Exception
    {
        assertExpression(new JexlContext.Mapped(), "## double or nothing\n 1 + 1", Integer.valueOf("2"));
    }
    
    /**
     * Test assignment.
     * @throws Exception
     */
    public void testAssignment() throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("aString", "Hello");
        Foo foo = new Foo();
        jc.setJexlVariable("foo", foo);
        Parser parser = new Parser(new StringReader(";"));
        parser.parse(new StringReader("aString = 'World';"), null);
        
        assertExpression(jc, "hello = 'world'", "world");
        assertEquals("hello variable not changed", "world", jc.getJexlVariable("hello"));
        assertExpression(jc, "result = 1 + 1", new Integer(2));
        assertEquals("result variable not changed", new Integer(2), jc.getJexlVariable("result"));
        // todo: make sure properties can be assigned to, fall back to flat var if no property
        // assertExpression(jc, "foo.property1 = '99'", "99");
        // assertEquals("property not set", "99", foo.getProperty1());
    }
    
    public void testAntPropertiesWithMethods() throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        String value = "Stinky Cheese";
        jc.setJexlVariable("maven.bob.food", value);
        assertExpression(jc, "maven.bob.food.length()", new Integer(value.length()));
        assertExpression(jc, "empty(maven.bob.food)", Boolean.FALSE);
        assertExpression(jc, "size(maven.bob.food)", new Integer(value.length()));
        assertExpression(jc, "maven.bob.food + ' is good'", value + " is good");

        // DG: Note the following ant properties don't work
//        String version = "1.0.3";
//        jc.setJexlVariable("commons-logging", version);
//        assertExpression(jc, "commons-logging", version);
    }

    public void testUnicodeSupport() throws Exception
    {
        JexlContext jc = new JexlContext.Mapped();
        assertExpression(jc, "myvar == 'Użytkownik'", Boolean.FALSE);
        assertExpression(jc, "'c:\\some\\windows\\path'", "c:\\some\\windows\\path");
        assertExpression(jc, "'foo\\u0020bar'", "foo\u0020bar");
        assertExpression(jc, "'foo\\u0020\\u0020bar'", "foo\u0020\u0020bar");
        assertExpression(jc, "'\\u0020foobar\\u0020'", "\u0020foobar\u0020");
    }

    public static final class Duck {
        int user = 10;
        @SuppressWarnings("boxing")
        public Integer get(String val) {
            if ("zero".equals(val))
                return 0;
            if ("one".equals(val))
                return 1;
            if ("user".equals(val))
                return user;
            return -1;
        }
        @SuppressWarnings("boxing")
        public void set(String val, Object value) {
            if ("user".equals(val)) {
                if ("zero".equals(value))
                    user = 0;
                else if ("one".equals(value))
                    user = 1;
                else
                    user = value instanceof Integer? (Integer) value : -1;
            }
        }
    }

    @SuppressWarnings("boxing")
    public void testDuck() throws Exception {
        JexlEngine jexl = JEXL;
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("duck", new Duck());
        Expression expr;
        Object result;
        expr = jexl.createExpression("duck.zero");
        result = expr.evaluate(jc);
        assertEquals(expr.toString(), 0, result);
        expr = jexl.createExpression("duck.one");
        result = expr.evaluate(jc);
        assertEquals(expr.toString(), 1, result);
        expr = jexl.createExpression("duck.user = 20");
        result = expr.evaluate(jc);
        assertEquals(expr.toString(), 20, result);
        expr = jexl.createExpression("duck.user");
        result = expr.evaluate(jc);
        assertEquals(expr.toString(), 20, result);
        expr = jexl.createExpression("duck.user = 'zero'");
        result = expr.evaluate(jc);
        expr = jexl.createExpression("duck.user");
        result = expr.evaluate(jc);
        assertEquals(expr.toString(), 0, result);
    }

    @SuppressWarnings("boxing")
    public void testArray() throws Exception {
        int[] array = { 100, 101 , 102 };
        JexlEngine jexl = JEXL;
        JexlContext jc = new JexlContext.Mapped();
        jc.setJexlVariable("array", array);
        Expression expr;
        Object result;
        expr = jexl.createExpression("array.1");
        result = expr.evaluate(jc);
        assertEquals(expr.toString(), 101, result);
        expr = jexl.createExpression("array[1] = 1010");
        result = expr.evaluate(jc);
        assertEquals(expr.toString(), 1010, result);
        expr = jexl.createExpression("array.0");
        result = expr.evaluate(jc);
        assertEquals(expr.toString(), 100, result);
    }

    /**
     * Asserts that the given expression returns the given value when applied to the
     * given context
     */
    protected void assertExpression(JexlContext jc, String expression, Object expected) throws Exception
    {
        Expression e = JEXL.createExpression(expression);
        Object actual = e.evaluate(jc);
        assertEquals(expression, expected, actual);
    }

}