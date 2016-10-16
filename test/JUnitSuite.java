

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * JUnit Test Suite
 * 
 * @author Tony Hsu
 */
@RunWith(Suite.class)

@Suite.SuiteClasses({ 
   UtilitiesJUnitTest.class,
   DestFileProcJUnitTest.class,
   SrcFileProcJUnitTest.class,
})

public class JUnitSuite {
}
