package schooldata;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

// @formatter:off
/**
 * @author Chuck Eastus
 * Reworked from Joe Prasanna Kumar's sample with suggestions and notes made by Kevin W. Wall.
 * This class demonstrates encrypting and decrypting a string message using the AES algorithm.
 * Steps:
 * 1. Generate an AES key of the desired length (in bits) using an AES KeyGenerator.
 * 2. Get a Cipher instance of the desired algorithm, mode, and padding.
 * 3. Generate an initialization vector for our message of the same size as the Cipher's blocksize.
 * 4. Initialize the Cipher instance for encryption using the key and initialization vector.
 * 5. Use the Cipher to encrypt the message (after encoding it to a byte[] using the named Charset), and then append
 * the encrypted data to the IV and Base64-encode the result.
 * 6. Get a new Cipher instance of the same algorithm, mode, and padding used for encryption.
 * 7. Base64-decode and split the data into the IV and the encrypted data, and then initialize the cipher for
 * decryption with the same key used for encryption (symmetric), the IV, and the encrypted data.
 * 8. Use the Cipher to decrypt the data, convert it to a String using the named Charset, and display the message.
 *
 * Notes on padding:
 * PKCS7 padding is actually technically the correct padding name, but Java blew it and called it PKCS5PADDING.
 * Technically, PKCS5 padding only applies to ciphers with a cipher block size of 64-bits, not 128-bits, but both PKCS5
 * and PKCS7 padding act identically for block sizes <= 255 bits.
 * Be sure to specify the mode explicitly as most JCE providers default to ECB mode, which is not secure!
 * For this example, we are use CFB mode with no padding in order to avoid padding attacks.
 *
 * Notes on initialization vectors (IVs):
 * The IV must be saved for later decryption and should not be reused for other encryption operations. It can be stored
 * separately or sent along with the encrypted data. Usually, the encrypted data is appended to the IV and the result
 * is encoded then stored or transmitted.
 */
// @formatter:on

public class CoreClass {

  public static final int keyLength = 128;
  public static final String charEnc = "UTF-8";
  public static final String transformationString = "AES/CFB/NoPadding";

  public static void main(String[] args) {

    String message = "Hello World of Encryption using AES";
    String cipherText;

    try {
      // Step 1
      KeyGenerator keyGen = KeyGenerator.getInstance("AES");
      keyGen.init(keyLength);
      SecretKey secretKey = keyGen.generateKey();
      //String secretKey = "This is my key";
      //// // System.out.println(secretKey.getEncoded());
      // Step 2
      Cipher aesCipherForEncryption = Cipher.getInstance(transformationString);

      // Step 3
      byte[] iv = new byte[aesCipherForEncryption.getBlockSize()];
      SecureRandom prng = new SecureRandom();
      prng.nextBytes(iv);

      // Step 4
      aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

      // Step 5
      byte[] encrypted = aesCipherForEncryption.doFinal(message.getBytes(charEnc));
      ByteBuffer cipherData = ByteBuffer.allocate(iv.length + encrypted.length);
      cipherData.put(iv);
      cipherData.put(encrypted);
      cipherText = new String(Base64.getEncoder().encode(cipherData.array()), charEnc);
       // System.out.println("Encrypted and encoded message is: " + new String(Base64.getEncoder().encode(encrypted), charEnc));
      //// // System.out.println(cipherText);
      //// // System.out.println("\nThe receiver will now initialize the cipher using the IV and decrypt the ciphertext");

      // Step 6
      Cipher aesCipherForDecryption = Cipher.getInstance(transformationString);

      // Step 7
      cipherData = ByteBuffer.wrap(Base64.getDecoder().decode(cipherText.getBytes(charEnc)));
      iv = new byte[aesCipherForDecryption.getBlockSize()];
      cipherData.get(iv);
      encrypted = new byte[cipherData.remaining()];
      cipherData.get(encrypted);
      aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

      // Step 8
      byte[] decrypted = aesCipherForDecryption.doFinal(encrypted);
      //// // System.out.println("Decrypted text message is: " + new String(decrypted, charEnc));
    } catch(NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | UnsupportedEncodingException ex) {
      System.err.println(ex);
    }
  }

}