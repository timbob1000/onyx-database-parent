package embedded.diskmap;

import category.EmbeddedDatabaseTests;
import com.onyx.diskmap.DefaultMapBuilder;
import com.onyx.diskmap.MapBuilder;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.Externalizable;
import java.io.File;
import java.util.Map;

/**
 * Created by timothy.osborn on 3/21/15.
 */
@Category({ EmbeddedDatabaseTests.class })
public class AbstractTest
{

    public static final String TEST_DATABASE = "C:/Sandbox/Onyx/Tests/hiya.db";


    @BeforeClass
    public static void beforeTest()
    {
        File testDataBase = new File(TEST_DATABASE);
        if(testDataBase.exists())
        {
            testDataBase.delete();
        }
    }

}
