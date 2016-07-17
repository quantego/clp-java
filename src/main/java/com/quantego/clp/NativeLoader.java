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
	
	public static void main(String... args) {
		load();
	}

	static void load() {
		File tempDir = createTempDir(prefix);
        BridJ.addLibraryPath(tempDir.getAbsolutePath());

		String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        String path;
        String[] libs;
        if (osName.startsWith("mac")) {
        	path = library+"/darwin/";
        	libs = new String[]{"libCoinUtils.3.dylib","libClp.dylib"};
        } else if (osName.startsWith("win") && osArch.contains("64")) {
        	path = library+"/win64/";
            libs = new String[]{"libgcc_s_seh_64-1.dll","libstdc++_64-6.dll","libCoinUtils-3.dll","Clp.dll",};
        } else if (osName.startsWith("linux")) {
        	path = library+"/linux64/";
        	libs = new String[]{"libCoinUtils.so.3","libClp.so"};
            BridJ.addNativeLibraryDependencies("Clp", "CoinUtils");
        } else {
            throw new UnsupportedOperationException("Platform " + osName + ":" + osArch + " not supported");
        }
        for (String lib : libs) 
    		loadLibrary(tempDir,path,lib);
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