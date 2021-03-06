package com.ra1ph.MTProtoClient.crypto;

import android.util.Log;

import com.ra1ph.MTProtoClient.tl.TLUtility;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ra1ph
 * Date: 23.07.13
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class CryptoUtility {

    static final int AES_BLOCK_SIZE = 16;
    static final int DATA_WITH_HASH_SIZE = 255;
    static final String RSA_PUBLIC_SERVER_KEY = "MIIBCgKCAQEAwVACPi9w23mF3tBkdZz+zwrzKOaaQdr01vAbU4E1pvkfj4sqDsm6" +
            "lyDONS789sVoD/xCS9Y0hkkC3gtL1tSfTlgCMOOul9lcixlEKzwKENj1Yz/s7daS" +
            "an9tqw3bfUV/nqgbhGX81v/+7RFAEd+RwFnK7a+XYl9sluzHRyVVaTTveB2GazTw" +
            "Efzk2DWgkBluml8OREmvfraX3bkHZJTKX4EQSjBbbdJ2ZXIsRrYOXfaA+xayEGB+" +
            "8hdlLmAjbCVfaigxX0CDqWeR1yFL9kwd9P0NsZRPsmoqVwMbMu7mStFai6aIhc3n" +
            "Slv8kg9qv1m6XHVQY3PnEw+QQtqSIXklHwIDAQAB";


    public static byte[] getSHA1hash(byte[] dataByte) {
        MessageDigest md = null;
        byte[] sha1hash = new byte[20];
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(dataByte, 0, dataByte.length);
            sha1hash = md.digest();
            return sha1hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public static byte[] getDataWithHash(byte[] hash, byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(DATA_WITH_HASH_SIZE);
        buffer.put(hash);
        buffer.put(data);
        byte[] rand = new byte[DATA_WITH_HASH_SIZE - hash.length - data.length];
        new Random().nextBytes(rand);
        buffer.put(rand);
        return buffer.array();
    }

    public static byte[] getDHdataWithHash(byte[] hash, byte[] data) {
        int ost = (data.length + 20) % 16 > 0 ? 16 : 0;
        int size = ((20 + data.length)/16)*16 + ost;

        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(hash);
        buffer.put(data);

        byte[] rand = new byte[size - hash.length - data.length];
        new Random().nextBytes(rand);
        buffer.put(rand);
        return buffer.array();
    }

    public static byte[] getRSAEncryptedData(byte[] dataWithHash) {
        BigInteger modulus = new BigInteger("C150023E2F70DB7985DED064759CFECF0AF328E69A41DAF4D6F01B538135A6F91F8F8B2A0EC9BA9720CE352EFCF6C5680FFC424BD634864902DE0B4BD6D49F4E580230E3AE97D95C8B19442B3C0A10D8F5633FECEDD6926A7F6DAB0DDB7D457F9EA81B8465FCD6FFFEED114011DF91C059CAEDAF97625F6C96ECC74725556934EF781D866B34F011FCE4D835A090196E9A5F0E4449AF7EB697DDB9076494CA5F81104A305B6DD27665722C46B60E5DF680FB16B210607EF217652E60236C255F6A28315F4083A96791D7214BF64C1DF4FD0DB1944FB26A2A57031B32EEE64AD15A8BA68885CDE74A5BFC920F6ABF59BA5C75506373E7130F9042DA922179251F", 16);
        BigInteger pubExp = new BigInteger("010001", 16);

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
            RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherData = cipher.doFinal(dataWithHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BadPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public static byte[] getRSAHackdData(byte[] dataWithHash) {
        BigInteger modulus = new BigInteger("C150023E2F70DB7985DED064759CFECF0AF328E69A41DAF4D6F01B538135A6F91F8F8B2A0EC9BA9720CE352EFCF6C5680FFC424BD634864902DE0B4BD6D49F4E580230E3AE97D95C8B19442B3C0A10D8F5633FECEDD6926A7F6DAB0DDB7D457F9EA81B8465FCD6FFFEED114011DF91C059CAEDAF97625F6C96ECC74725556934EF781D866B34F011FCE4D835A090196E9A5F0E4449AF7EB697DDB9076494CA5F81104A305B6DD27665722C46B60E5DF680FB16B210607EF217652E60236C255F6A28315F4083A96791D7214BF64C1DF4FD0DB1944FB26A2A57031B32EEE64AD15A8BA68885CDE74A5BFC920F6ABF59BA5C75506373E7130F9042DA922179251F", 16);
        BigInteger pubExp = new BigInteger("010001", 16);

        BigInteger r = new BigInteger(dataWithHash);

        BigInteger s = r.modPow(pubExp, modulus);
        byte[] temp = s.toByteArray();
        return Arrays.copyOfRange(temp, temp.length - 256, temp.length);
    }

    public static byte[] getTmpAESKey(byte[] serverNonce, byte[] newNonce) {
        ByteBuffer buffer = ByteBuffer.allocate(serverNonce.length + newNonce.length);
        buffer.put(newNonce);
        buffer.put(serverNonce);
        byte[] tmp1 = getSHA1hash(buffer.array());

        buffer = ByteBuffer.allocate(serverNonce.length + newNonce.length);
        buffer.put(serverNonce);
        buffer.put(newNonce);
        byte[] tmp2 = getSHA1hash(buffer.array());

        buffer = ByteBuffer.allocate(tmp1.length + 12);
        buffer.put(tmp1);
        buffer.put(Arrays.copyOfRange(tmp2, 0, 12));
        return buffer.array();
    }

    public static byte[] getTmpAESiv(byte[] serverNonce, byte[] newNonce) {
        ByteBuffer buffer = ByteBuffer.allocate(serverNonce.length + newNonce.length);
        buffer.put(serverNonce);
        buffer.put(newNonce);
        byte[] tmp1 = getSHA1hash(buffer.array());

        buffer = ByteBuffer.allocate(newNonce.length * 2);
        buffer.put(newNonce);
        buffer.put(newNonce);
        byte[] tmp2 = getSHA1hash(buffer.array());

        buffer = ByteBuffer.allocate(8 + tmp2.length + 4);
        buffer.put(Arrays.copyOfRange(tmp1, 12, 20));
        buffer.put(tmp2);
        buffer.put(Arrays.copyOfRange(newNonce, 0, 4));
        return buffer.array();
    }

    public static byte[] getAESkey(byte[] authKey, byte[] msgKey, boolean isOut){
        int x=0;
        if(!isOut)x = 8;

        ByteBuffer tempA = ByteBuffer.allocate(msgKey.length + 32 );
        tempA.put(msgKey);
        tempA.put(authKey,x,32);
        byte[] a = getSHA1hash(tempA.array());

        ByteBuffer tempB = ByteBuffer.allocate(msgKey.length + 32 );
        tempB.put(authKey,32 + x,16);
        tempB.put(msgKey);
        tempB.put(authKey,48 + x,16);
        byte[] b = getSHA1hash(tempB.array());

        ByteBuffer tempC = ByteBuffer.allocate(msgKey.length + 32 );
        tempC.put(authKey,64 + x,32);
        tempC.put(msgKey);
        byte[] c = getSHA1hash(tempC.array());

        ByteBuffer key = ByteBuffer.allocate(8 + 12 + 12);
        key.put(a,0,8);
        key.put(b,8,12);
        key.put(c,4,12);

        return key.array();
    }

    public static byte[] getAESiv(byte[] authKey, byte[] msgKey, boolean isOut){
        int x=0;
        if(!isOut)x = 8;

        ByteBuffer tempA = ByteBuffer.allocate(msgKey.length + 32 );
        tempA.put(msgKey);
        tempA.put(authKey,x,32);
        byte[] a = getSHA1hash(tempA.array());

        ByteBuffer tempB = ByteBuffer.allocate(msgKey.length + 32 );
        tempB.put(authKey,32 + x,16);
        tempB.put(msgKey);
        tempB.put(authKey,48 + x,16);
        byte[] b = getSHA1hash(tempB.array());

        ByteBuffer tempC = ByteBuffer.allocate(msgKey.length + 32 );
        tempC.put(authKey,64 + x,32);
        tempC.put(msgKey);
        byte[] c = getSHA1hash(tempC.array());

        ByteBuffer tempD = ByteBuffer.allocate(msgKey.length + 32);
        tempD.put(msgKey);
        tempD.put(authKey,96 + x,32);
        byte[] d = getSHA1hash(tempD.array());

        ByteBuffer iv = ByteBuffer.allocate(12 + 8 + 4 + 8);
        iv.put(a,8,12);
        iv.put(b,0,8);
        iv.put(c,16,4);
        iv.put(d,0,8);

        return iv.array();
    }

    public static byte[] messageEncrypt(byte[] authKey, byte[] msgKey, byte[] data,  boolean isOut){
        byte[] key = getAESkey(authKey, msgKey, isOut);
        byte[] iv = getAESiv(authKey, msgKey, isOut);

        return aesIGEencrypt(iv,key,data);
    }

    public static byte[] messageDecrypt(byte[] authKey, byte[] msgKey, byte[] data,  boolean isOut){
        byte[] key = getAESkey(authKey, msgKey, isOut);
        byte[] iv = getAESiv(authKey, msgKey, isOut);

        return aesIGEdecrypt(iv, key, data);
    }

    public static byte[] aesIGEdecrypt(byte[] tmpAESiv, byte[] tmpAesKey, byte[] data) {
        try {

            ByteBuffer out = ByteBuffer.allocate(data.length);

            byte[] iv2p = Arrays.copyOfRange(tmpAESiv, 0, tmpAESiv.length / 2);
            byte[] ivp = Arrays.copyOfRange(tmpAESiv, tmpAESiv.length / 2, tmpAESiv.length);

            int len = data.length / AES_BLOCK_SIZE;

            byte[] xorInput = null;
            byte[] xorOutput = null;

            SecretKeySpec keySpec = null;
            keySpec = new SecretKeySpec(tmpAesKey, "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] input = null;
            byte[] output = null;

            for(int i=0;i < len;i++) {
                input = Arrays.copyOfRange(data,i*AES_BLOCK_SIZE,(i+1)*AES_BLOCK_SIZE);
                xorInput = xor(input, ivp);
                output = cipher.doFinal(xorInput);
                xorOutput = xor(output,iv2p);
                out.put(xorOutput);

                ivp = xorOutput;
                iv2p = input;
            }
            return out.array();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] xor(byte[] a, byte[] b){
        if(a.length == b.length){
            byte[] res = new byte[a.length];
            for(int i=0;i<a.length;i++){
                res[i] = (byte) (a[i]^b[i]);
            }
            return res;
        }else return null;
    }

    public static byte[] aesIGEencrypt(byte[] tmpAESiv, byte[] tmpAesKey, byte[] data) {
        try {

            ByteBuffer out = ByteBuffer.allocate(data.length);

            byte[] ivp = Arrays.copyOfRange(tmpAESiv, 0, tmpAESiv.length / 2);
            byte[] iv2p = Arrays.copyOfRange(tmpAESiv, tmpAESiv.length / 2, tmpAESiv.length);

            int len = data.length / AES_BLOCK_SIZE;

            byte[] xorInput = null;
            byte[] xorOutput = null;

            SecretKeySpec keySpec = null;
            keySpec = new SecretKeySpec(tmpAesKey, "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] input = null;
            byte[] output = null;

            for(int i=0;i < len;i++) {

                input = Arrays.copyOfRange(data,i*AES_BLOCK_SIZE,(i+1)*AES_BLOCK_SIZE);
                xorInput = xor(input, ivp);
                output = cipher.doFinal(xorInput);
                xorOutput = xor(output,iv2p);
                out.put(xorOutput);

                ivp = xorOutput;
                iv2p = input;
            }
            return out.array();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getAuthKeyHash(byte[] key) {
        byte[] authKey = Arrays.copyOfRange(getSHA1hash(key),12,20);
        return authKey;
    }

    public static byte[] getMsgKeyHash(byte[] data){
        byte[] msgKey = Arrays.copyOfRange(getSHA1hash(data),4,20);
        return msgKey;
    }
}
