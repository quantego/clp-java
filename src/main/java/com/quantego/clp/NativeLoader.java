package com.quantego.clp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class NativeLoader {
	
	static String prefix = "CLPExtractedLib";
	static String library = "clp-1.16.10";
	static String pathSep = System.getProperty("file.separator");

	static void load() {
		
		File tempDir = createTempDir(prefix);
		String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("mac")) {
        	String path = library+"/darwin/";
        	loadLibrary(tempDir,path,"libClp.dylib");
            System.setProperty("java.library.path", System.getProperty("java.library.path")+":"+tempDir.getAbsolutePath());
        } else if (osName.startsWith("win")) {
        	if (osArch.contains("64")) {
        		String path = library+"/win64/";
            	String[] libs = {"libgcc_s_seh_64-1.dll","libstdc++_64-6.dll","libCoinUtils-3.dll","Clp.dll",};
            	for (String lib : libs) 
            		loadLibrary(tempDir,path,lib);
                System.setProperty("java.library.path", System.getProperty("java.library.path")+";"+tempDir.getAbsolutePath());
        	}
            else {
                throw new UnsupportedOperationException("Platform " + osName + ":" + osArch + " notsupported");
            }
        } else if (osName.startsWith("linux")) {
        	String path = library+"/linux64/";
        	loadLibrary(tempDir,path,"libClp.so");
            System.setProperty("java.library.path", System.getProperty("java.library.path")+":"+tempDir.getAbsolutePath());

        } else {
            throw new UnsupportedOperationException("Platform " + osName + ":" + osArch + " not supported");
        }
	}
	
	public static File createTempDir(String prefix) {
        String tmpDirName = System.getProperty("java.io.tmpdir");
        if (tmpDirName.endsWith(pathSep))
        	tmpDirName += prefix+System.nanoTime()+pathSep;
        else
        	tmpDirName += pathSep+prefix+System.nanoTime()+pathSep;
        File dir = new File(tmpDirName);
        dir.mkdir();
        dir.deleteOnExit();
        return dir;
	}
	
    public static void loadLibrary(File dir, String path, String name) {
        InputStream in = null;
        OutputStream out = null;
        try {
        	in = NativeLoader.class.getClassLoader().getResourceAsStream(path+name);
            File file = new File(dir, name);
            file.deleteOnExit();
            file.createNewFile();
            out = new FileOutputStream(file);
            int cnt;
            byte buf[] = new byte[16 * 1024];
            while ((cnt = in.read(buf)) >= 1) {
                out.write(buf, 0, cnt);
            }
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