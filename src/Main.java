import com.drcosu.utils.UProperties;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.zip.Adler32;

import static com.drcosu.utils.UByte.intToByte;
import static com.drcosu.utils.UByte.readFileBytes;
import static com.drcosu.utils.UDex.fixCheckSumHeader;
import static com.drcosu.utils.UDex.fixFileSizeHeader;
import static com.drcosu.utils.UDex.fixSHA1Header;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

        Map<String,String> config = UProperties.readProperties();
        // TODO Auto-generated method stub
        try {
            File payloadSrcFile = new File(config.get("shellOffApk"));   //需要加壳的程序
            System.out.println("apk size:"+payloadSrcFile.length());
            File unShellDexFile = new File(config.get("shellDex"));    //解客dex
            byte[] payloadArray = encrpt(readFileBytes(payloadSrcFile));//以二进制形式读出apk，并进行加密处理//对源Apk进行加密操作
            byte[] unShellDexArray = readFileBytes(unShellDexFile);//以二进制形式读出dex
            int payloadLen = payloadArray.length;
            int unShellDexLen = unShellDexArray.length;
            int totalLen = payloadLen + unShellDexLen +4;//多出4字节是存放长度的。
            byte[] newdex = new byte[totalLen]; // 申请了新的长度
            //添加解壳代码
            System.arraycopy(unShellDexArray, 0, newdex, 0, unShellDexLen);//先拷贝dex内容
            //添加加密后的解壳数据
            System.arraycopy(payloadArray, 0, newdex, unShellDexLen, payloadLen);//再在dex内容后面拷贝apk的内容
            //添加解壳数据长度
            System.arraycopy(intToByte(payloadLen), 0, newdex, totalLen-4, 4);//最后4为长度
            //修改DEX file size文件头
            fixFileSizeHeader(newdex);
            //修改DEX SHA1 文件头
            fixSHA1Header(newdex);
            //修改DEX CheckSum文件头
            fixCheckSumHeader(newdex);

            String str = config.get("shellApk");
            File file = new File(str);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream localFileOutputStream = new FileOutputStream(str);
            localFileOutputStream.write(newdex);
            localFileOutputStream.flush();
            localFileOutputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //直接返回数据，读者可以添加自己加密方法
    private static byte[] encrpt(byte[] srcdata){
        for(int i = 0;i<srcdata.length;i++){
            srcdata[i] = (byte)(0xFF ^ srcdata[i]);
        }
        return srcdata;
    }

}