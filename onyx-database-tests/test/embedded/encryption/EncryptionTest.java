package embedded.encryption;

import category.EmbeddedDatabaseTests;
import com.onyx.util.EncryptionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

/**
 *
 * @author cosbor11
 */
@Category({ EmbeddedDatabaseTests.class })
public class EncryptionTest {
    
    protected static final String FILE_LOCATION = "C:/Sandbox/Onyx/Tests/encryption";

    @Test
    public void shouldEncrypt() throws GeneralSecurityException, UnsupportedEncodingException {
        String encryptedText = EncryptionUtil.encrypt("adminadmin");      
        Assert.assertTrue(encryptedText != null);
    }

    @Test
    public void shouldDycrypt() throws GeneralSecurityException, IOException {
        String str = "adminadmin";
        String encryptedText = EncryptionUtil.encrypt(str); 
        String decryptedText = EncryptionUtil.decrypt(encryptedText);
        
        Assert.assertTrue(str.equals(decryptedText));
       
    }
    
    @Test
    public void shouldFail() throws GeneralSecurityException, IOException {
        String str = "adminadmin";
        String str2 = "adminpassword"; 
        String decryptedText = EncryptionUtil.decrypt(EncryptionUtil.encrypt(str2));
        
        Assert.assertFalse(str.equals(decryptedText));   
    }
    
    
    @Test
    public void shouldWriteToFile() throws GeneralSecurityException, IOException {
       String textToSave = "asdfasdf";
       
       //Create a temporary file
       final File file = new File(FILE_LOCATION + "/tmp");
       file.getParentFile().mkdirs();
       file.createNewFile();
       //Next, write the encrypted bytes to the file.
       
        try (FileOutputStream fileStream = new FileOutputStream(file)) {
            fileStream.write(EncryptionUtil.encrypt(textToSave).getBytes(StandardCharsets.UTF_8));
            fileStream.close();
        }

        String savedText = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

        //make assertions
        Assert.assertEquals(savedText,EncryptionUtil.encrypt(textToSave));
        
        //cleanup file
        boolean delete = file.delete();
        //Now lets read from the file
        
    }
  

}
