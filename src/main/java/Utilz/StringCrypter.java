package Utilz;
// Файл StringCrypter.java

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Класс для шифрования и дешифрования строк
 * Использует библиотеку Apache Codec http://commons.apache.org/codec/
 * @author Рудницкий Валентин
 */
public class StringCrypter {
    public static void main(String[] args) {
        StringCrypter crypter=new StringCrypter(new byte[]{1,4,5,6,8,9,7,8});
        String testStr = "01/03/2019";
        // шифрование
        System.out.println( crypter.encrypt(testStr));
        System.out.println(crypter.decrypt(crypter.encrypt(testStr)));

    }

    /**
     * Упрощенный конструктор. Создает StringCrypter с ключом DESSecretKey со значением по умолчанию (не рекомендуется)
     */
    public StringCrypter() {
        this(new byte[]{1,4,5,6,8,9,7,8});
    }

    /**
     * Упрощенный конструктор. Создает StringCrypter с ключом
     * DESSecretKey (алгоритм шифрования DES) со значением key.
     * Ключ key должен иметь длину 8 байт
     */
    public StringCrypter(byte[] key) {
        try {
            updateSecretKey(new DESSecretKey(key));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    public StringCrypter(SecretKey key) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException {
        updateSecretKey(key);
    }

    private void updateSecretKey(SecretKey key) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException {
        ecipher = Cipher.getInstance(key.getAlgorithm());
        dcipher = Cipher.getInstance(key.getAlgorithm());
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        dcipher.init(Cipher.DECRYPT_MODE, key);
    }

    public static class DESSecretKey implements SecretKey {

        private final byte[] key;

        /**
         * ключ должен иметь длину 8 байт
         */
        public DESSecretKey(byte[] key) {
            this.key = key;
        }

        @Override
        public String getAlgorithm() {
            return "DES";
        }

        @Override
        public String getFormat() {
            return "RAW";
        }

        @Override
        public byte[] getEncoded() {
            return key;
        }
    }

    private Cipher ecipher;
    private Cipher dcipher;

    /**
     * Функция шифрования
     *
     * @param str строка открытого текста
     * @return зашифрованная строка в формате Base64
     */
    public String encrypt(String str) {
        try {
            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            return Base64.encodeBase64String(enc);
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
            Logger.getLogger(StringCrypter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Функция дешифрования
     *
     * @param str зашифрованная строка в формате Base64
     * @return расшифрованная строка
     */
    public String decrypt(String str) {
        try {
            byte[] dec = Base64.decodeBase64(str);
            byte[] utf8 = dcipher.doFinal(dec);
            return new String(utf8, "UTF8");
        } catch (IllegalBlockSizeException | BadPaddingException | IOException ex) {
            Logger.getLogger(StringCrypter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
