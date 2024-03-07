package com.quantego.clp;

import jnr.ffi.LibraryLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class NativeLoader {

	static String prefix = "CLPExtractedLib";
	static String library = "clp-1.16.15";
	static String pathSep = System.getProperty("file.separator");

	public static void main(String... args) {
		CLP clp = new CLP();
		CLPVariable x1 = clp.addVariable();
		clp.createExpression().add(4).add(-2, x1).asObjective();
		clp.createExpression().add(x1).leq(2);
		CLPVariable x2 = clp.addVariable();
		clp.createExpression().add(6).add(-2, x2).asObjective();
		clp.createExpression().add(x2).leq(3);
		clp.minimize();
		System.out.println(clp.toString());
	}

	static CLPNative load() {
		File tempDir = createTempDir(prefix);
		String osArch = System.getProperty("os.arch").toLowerCase();
	    String osName = System.getProperty("os.name").toLowerCase();
	    String path;
	    String[] libs;
	    String selectedArch = null;
		String coinUtils = null;
		if (osName.startsWith("mac")) {
			if (osArch.equals("aarch64")) {
				path = library+"/darwin-aarch64/";
				libs = new String[]{"libCoinUtils.3.dylib","libClp.dylib"};
				coinUtils = "CoinUtils.3";
				selectedArch = "mac-aarch64";
			}
			else {
				path = library+"/darwin-x86/";
				libs = new String[]{"libCoinUtils.3.dylib","libClp.dylib"};
				coinUtils = "CoinUtils.3";
				selectedArch = "mac-x86_64";
			}
	    } else if (osName.startsWith("win") && osArch.contains("64")) {
	    	path = library+"/win64/";
	        libs = new String[]{"libgcc_s_seh-1.dll","libstdc++-6.dll","libCoinUtils-3.dll","Clp.dll"};
			coinUtils = "libCoinUtils-3.dll";
	        selectedArch = "win-x86_64";
	    } else if (osName.startsWith("linux")) {
			if (osArch.equals("aarch64")) {
				path = library + "/linux-aarch64/";
				libs = new String[]{"libCoinUtils.so.3", "libClp.so"};
				coinUtils = "CoinUtils";
				//BridJ.addNativeLibraryDependencies("Clp", new String[]{"CoinUtils"});
				selectedArch = "linux-aarch64";
			} else {
				path = library+"/linux-x86/";
				libs = new String[]{"libCoinUtils.so.3","libClp.so"};
				coinUtils = "CoinUtils";
				//BridJ.addNativeLibraryDependencies("Clp", new String[]{"CoinUtils"});
				selectedArch = "linux-x86_64";
			}
	    } else {
	        throw new UnsupportedOperationException("Platform " + osName + ":" + osArch + " not supported");
	    }
	    for (String lib : libs)
			loadLibrary(tempDir,path,lib);
		return LibraryLoader
				.create(CLPNative.class)
				.search(tempDir.getAbsolutePath())
				.library(coinUtils)
				.load("Clp");
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
