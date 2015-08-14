/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.helpers;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


/**
 * this class provides functions used to generate a relative path
 * from two absolute paths
 * @author David M. Howard
 */
public class RelativePath {
	/**
	 * break a path down into individual elements and add to a list.
	 * example : if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
	 * @param f input file
	 * @return a List collection with the individual elements of the path in
            reverse order
	 */
	private static List getPathList(File f) {
		List l = new ArrayList();
		File r;
		try {
			r = f.getCanonicalFile();
			while(r != null) {
				l.add(r.getName());
				r = r.getParentFile();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			l = null;
		}
		return l;
	}

	/**
	 * figure out a string representing the relative path of
	 * 'f' with respect to 'r'
	 * @param r home path
	 * @param f path of file
	 */
	private static String matchPathLists(List r,List f) {
		int i;
		int j;
		String s;
		// start at the beginning of the lists
		// iterate while both lists are equal
		s = "";
		i = r.size()-1;
		j = f.size()-1;

		// first eliminate common root
		while((i >= 0)&&(j >= 0)&&(r.get(i).equals(f.get(j)))) {
			i--;
			j--;
		}

		// for each remaining level in the home path, add a ..
		for(;i>=0;i--) {
			s += ".." + File.separator;
		}

		// for each level in the file path, add the path
		for(;j>=1;j--) {
			s += f.get(j) + File.separator;
		}

		// file name
		s += f.get(j);
		return s;
	}

	/**
	 * get relative path of File 'f' with respect to 'home' directory
	 * example : home = /a/b/c
	 *           f    = /a/d/e/x.txt
	 *           s = getRelativePath(home,f) = ../../d/e/x.txt
	 * @param home base path, should be a directory, not a file, or it doesn't
            make sense
	 * @param f file to generate path for
	 * @return path from home to f as a string
	 */
	public static String getRelativePath(File home,File f){
                // CHANGED 13-10-2009: if the paths do not have the same roots
                // then it makes no sense to relativize them
                String os = System.getProperty("os.name").substring(0,3);
                boolean sameRoots = (!(os.equalsIgnoreCase("win")))
                        || (home.getAbsolutePath().startsWith(f.getAbsolutePath().substring(0, 2)));
                if (!sameRoots){
                    return f.getAbsolutePath();
                }

                File r;
		List homelist;
		List filelist;
		String s;

		homelist = getPathList(home);
		filelist = getPathList(f);
		s = matchPathLists(homelist,filelist);

		// changed 13-08: need to have slashes in relative filenames
                // not system dependent separator char
                return s.replace(System.getProperty("file.separator").charAt(0), '/');
	}

	/**
       * test the function
       */
	public static void main(String args[]) {
            String path1 = "S:\\TP-Z2\\DATEN\\CHILDES\\RomansLabor\\CHILDES\\Paris\\leonard\\exmaralda\\LEONARD-14-3_02_25.exs";
            String path2 = "S:\\TP-Z2\\DATEN\\CHILDES\\RomansLabor\\CHILDES\\Paris\\leonard\\14.mov";
            System.out.println("home = " + path1);
            System.out.println("file = " + path2);
            System.out.println("path = " + getRelativePath(new File(path1), new File(path2)));
            try {
                System.out.println("URL1 = " + new File(path1).toURI().toURL());
                System.out.println("URL2 = " + new File(getRelativePath(new File(path1), new File(path2))).toURI().toURL());
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }

            String path3 = "Ali_Dimitri/MT_091209_Dimitri_s.exs";
            String path4 = "http://vs.uni-hamburg.de/corpora/z2-hamatac/protected/MAPTASK.coma";
            System.out.println("relative = " + path3);
            System.out.println("base = " + path4);
            System.out.println("resolved = " + resolveRelativePath(path3, path4));

	}

    public static String resolveRelativePath(String relativePath, String base) {
        /*System.out.println("===============");
        System.out.println(relativePath);
        System.out.println(base);*/
        // keep simple cases as they were
        if ((base==null) || (new File(relativePath).isAbsolute())){
            //System.out.println("1");
            return relativePath;
        }
        if (base.startsWith("http")){
            try {
                //System.out.println("2a");
                return new URI(base).resolve(relativePath).toString();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
        if (!(relativePath.startsWith(".."))){
            //System.out.println("2b");
            File parentFile = new File(base).getParentFile();
            if (parentFile==null){
                return relativePath;
            }
            URI uri2 = parentFile.toURI();
            String modRelPath = relativePath.replaceAll(" ", "%20");
            //String modRelPath = relativePath;
            try{
                URI absoluteURI = uri2.resolve(modRelPath);
                return new File(absoluteURI).getAbsolutePath();            
            } catch (IllegalArgumentException use){
                use.printStackTrace();
                return relativePath;
            }
        }
        
        // the relative Path starts with ..
        String UP = ".." + System.getProperty("file.separator");
        String OTHER = "../";
        if (relativePath.startsWith(UP) || relativePath.startsWith(OTHER)){
            String newBase = new File(base).getParent();
            String newRelative = relativePath.substring(3);
            //System.out.println("3");
            return resolveRelativePath(newRelative, newBase);
        }
        // no can do
        //System.out.println("4");
        return relativePath;

    }
}

