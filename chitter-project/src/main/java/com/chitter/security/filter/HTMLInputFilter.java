package com.chitter.security.filter;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 15/8/12
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 *
 * HTML filtering utility for protecting against XSS (Cross Site Scripting).
 *
 */
public class HTMLInputFilter
{
    protected boolean vDebug;

    public HTMLInputFilter()
    {
        this(false);
    }

    public HTMLInputFilter( boolean debug )
    {
        vDebug = debug;
    }

    protected void debug( String msg )
    {
        if (vDebug)
            System.out.println( msg );
    }

    public static String htmlSpecialChars( String s )
    {
        return s;
    }

    //---------------------------------------------------------------

    /**
     * given a user submitted input String, filter out any invalid or restricted
     * html.
     *
     * @param input text (i.e. submitted by a user) than may contain html
     * @return "clean" version of input.
     */
    public synchronized String filter( String input )
    {
        String s = input;

        debug( "************************************************" );
        debug( "              INPUT: " + input );

        s = escapeReservedChars(s);
        debug("       escapeReservedChars: "+s);


        debug( "************************************************\n\n" );
        return s;
    }

    private String escapeReservedChars(String s) {
        s = s.replaceAll( "&", "&amp;" );
        s = s.replaceAll( "\"", "&quot;" );
        s = s.replaceAll( "<", "&lt;" );
        s = s.replaceAll( ">", "&gt;" );
        s = s.replaceAll("'","&apos;");
        return s;
    }


}