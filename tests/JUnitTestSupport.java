package tests;

import java.io.InputStream;
import java.util.Scanner;

public class JUnitTestSupport
{
    public static final String RELATIVE_FOLDER = "/tests/expectedOutput/";
    /**
     * @param input file of expected output
     * @return entire content of the file in a String
     */
    public static String readFile(InputStream input)
    {
        if(input == null)
        {
            return "";
        }
        
        Scanner sc = new Scanner(input);
        StringBuffer str = new StringBuffer();
        
        while(sc.hasNextLine())
        {
            str.append(sc.nextLine());
            str.append(sc.hasNextLine() ? "\n" : "");
        }
        sc.close();
        
        return str.toString();
    }
}
