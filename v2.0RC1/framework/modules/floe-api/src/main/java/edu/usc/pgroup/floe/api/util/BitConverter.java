package edu.usc.pgroup.floe.api.util;

import java.io.*;

public class BitConverter {

    private static ClassLoader applicationClassLoader;

    public static <T> byte[] getBytes(T obj) {
        byte[] retBytes = null;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(byteStream);
            outStream.writeObject(obj);
            outStream.flush();
            outStream.close();
            byteStream.close();
            retBytes = byteStream.toByteArray();
        } catch (IOException ex) {
            //TODO: Handle the exception
            ex.printStackTrace();
        }
        return retBytes;
    }

    public static <T> T getObject(byte[] bytes) {
        T obj = null;
        ObjectInputStream ois = null;
        try {

            ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);

            ois = new ObjectInputStream(byteInput);

            obj = (T) ois.readObject();
            return obj;
        } catch (Exception ex) {
             ex.printStackTrace();
//            if (applicationClassLoader == null) {
//                File jarDir = new File(PalletJarDeployer.FILE_DIR_PATH);
//                ClassLoader classLoader;
//                ArrayList<URL> jarUrls = new ArrayList<URL>();
//                if (jarDir.isDirectory()) {
//
//                    File[] jars = jarDir.listFiles();
//
//                    for (File jar : jars) {
//
//                        URL jarFileUrl = null;
//                        try {
//                            jarFileUrl = new URL("file:" + jar.getAbsolutePath());
//                        } catch (MalformedURLException e1) {
//                            e1.printStackTrace();
//                        }
//                        jarUrls.add(jarFileUrl);
//                    }
//                    classLoader = new URLClassLoader(jarUrls.toArray(new URL[jarUrls.size()]));
//
//                    try {
//
//                        ClassLoader b4 = Thread.currentThread().getContextClassLoader();
//                        Thread.currentThread().setContextClassLoader(classLoader);
//                        ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
//                        ois = new ClassLoadingAwareObjectInputStream(byteInput);
//                        obj = (T) ois.readObject();
//                        applicationClassLoader = classLoader;
//                        Thread.currentThread().setContextClassLoader(b4);
//
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//
//                } else {
//                    throw new RuntimeException("Error while Reading from Pallet Jar location :" +
//                            PalletJarDeployer.FILE_DIR_PATH);
//                }
//            }
//                try {
//                    ClassLoader b4 = Thread.currentThread().getContextClassLoader();
//                    Thread.currentThread().setContextClassLoader(applicationClassLoader);
//
//                    ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
//
//                    ois = new ClassLoadingAwareObjectInputStream(byteInput);
//                    obj = (T) ois.readObject();
//
//                    return obj;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//
//
//                    File jarDir = new File(PalletJarDeployer.FILE_DIR_PATH);
//                    ClassLoader classLoader;
//                    ArrayList<URL> jarUrls = new ArrayList<URL>();
//                    if (jarDir.isDirectory()) {
//
//                        File[] jars = jarDir.listFiles();
//
//                        for (File jar : jars) {
//
//                            URL jarFileUrl = null;
//                            try {
//                                jarFileUrl = new URL("file:" + jar.getAbsolutePath());
//                            } catch (MalformedURLException e1) {
//                                e1.printStackTrace();
//                            }
//                            jarUrls.add(jarFileUrl);
//                        }
//                        classLoader = new URLClassLoader(jarUrls.toArray(new URL[jarUrls.size()]));
//
//                        try {
//
//                            ClassLoader b4 = Thread.currentThread().getContextClassLoader();
//                            Thread.currentThread().setContextClassLoader(classLoader);
//                            ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
//                            ois = new ClassLoadingAwareObjectInputStream(byteInput);
//                            obj = (T) ois.readObject();
//                            applicationClassLoader = classLoader;
//                            Thread.currentThread().setContextClassLoader(b4);
//
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                        }
//
//                    } else {
//                        throw new RuntimeException("Error while Reading from Pallet Jar location :" +
//                                PalletJarDeployer.FILE_DIR_PATH);
//                    }
//
//
//                }

        }
        return obj;
    }


    public static final int byteArrayToInt(byte[] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }


}
