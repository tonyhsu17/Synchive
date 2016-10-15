package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * JUnit Test Suite
 * 
 * @author Tony Hsu
 */
@RunWith(Suite.class)

@Suite.SuiteClasses({ 
   UtilitiesJUnit.class,
   DestFileProcJUnit.class,
   SrcFileProcJUnit.class,
})

public class JUnitTestSuite {
}