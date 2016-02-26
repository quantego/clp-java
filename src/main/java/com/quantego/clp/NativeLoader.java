package com.quantego.clp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class NativeLoader {
    public static void loadLibrary(String library) {
    	String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        String name;
        String path;
        if (osName.startsWith("mac")) {
        	name = "lib" + library + ".dylib";
            path = "darwin/";
        } else if (osName.startsWith("win")) {
        	name = "lib" + library + ".dll";
            path = "win/";
        } else if (osName.startsWith("linux")) {
        	name = "lib" + library + ".so";
            path = "linux/";
        } else {
            throw new UnsupportedOperationException("Platform " + osName + ":" + osArch + " not supported");
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = NativeLoader.class.getClassLoader().getResourceAsStream("lib/" + path+name);
            String pathSep = System.getProperty("file.separator");
            String tmpDirName = System.getProperty("java.io.tmpdir");
            if (tmpDirName.endsWith(pathSep))
            	tmpDirName += "CLPExtractedLib"+System.nanoTime()+pathSep;
            else
            	tmpDirName += pathSep+"CLPExtractedLib"+System.nanoTime()+pathSep;
            File dir = new File(tmpDirName);
            dir.mkdir();
            dir.deleteOnExit();
            File file = new File(tmpDirName, name);
            file.deleteOnExit();
            file.createNewFile();
            out = new FileOutputStream(file);
            int cnt;
            byte buf[] = new byte[16 * 1024];
            while ((cnt = in.read(buf)) >= 1) {
                out.write(buf, 0, cnt);
            }
            System.setProperty("java.library.path", System.getProperty("java.library.path")+":"+tmpDirName);
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
    
}