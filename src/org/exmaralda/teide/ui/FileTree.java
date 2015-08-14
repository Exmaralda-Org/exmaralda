/**
 * 
 */
package org.exmaralda.teide.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

public class FileTree extends JTree {

	private static class DirectoryFilter implements FileFilter {
		public boolean accept(File f) {
			return f.isDirectory();
		}
	}

	private static class AcceptAllFilter implements FileFilter {
		public boolean accept(File f) {
			return true;
		}
	}

	private class FileTreeRenderer extends DefaultTreeCellRenderer {

		private FileSystemView fsv = FileSystemView.getFileSystemView();

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			Object user = ((DefaultMutableTreeNode) value).getUserObject();
			if (user instanceof File) {
				File f = (File) user;
				String name = f.getName();
				String ext = name.substring(name.lastIndexOf('.') + 1);
				if (icons.containsKey(ext))
					setIcon(icons.get(ext));
				else
					setIcon(fsv.getSystemIcon(f));
				setText(fsv.getSystemDisplayName(f));
			}
			return this;
		}
	}

	protected FileFilter filter;

	protected DefaultTreeModel model;

	public static final FileFilter DIRECTORY_FILTER = new DirectoryFilter();

	public static final FileFilter ACCEPT_ALL_FILTER = new AcceptAllFilter();

	private Map<String, Icon> icons = new HashMap<String, Icon>();

	private File homeDir;

	public FileTree() {
		this(FileSystemView.getFileSystemView().getHomeDirectory().getPath());
	}

	public FileTree(String fileName) {

		File f = new File(fileName);
		if (!f.exists()) {
			f = FileSystemView.getFileSystemView().getHomeDirectory();
		}

		homeDir = f;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(homeDir);
		DefaultMutableTreeNode home = new DefaultMutableTreeNode(homeDir);
		root.add(home);
		model = new DefaultTreeModel(root);
		setShowsRootHandles(true);
		setCellRenderer(new FileTreeRenderer());
		setModel(model);
		expandPath(home);
		addTreeWillExpandListener(new TreeWillExpandListener() {
			public void treeWillCollapse(TreeExpansionEvent e) {
				((DefaultMutableTreeNode) (e.getPath().getLastPathComponent()))
						.removeAllChildren();
				((DefaultMutableTreeNode) (e.getPath().getLastPathComponent()))
						.add(new DefaultMutableTreeNode(null));
			}

			public void treeWillExpand(TreeExpansionEvent e) {
				expandPath((DefaultMutableTreeNode) (e.getPath()
						.getLastPathComponent()));
			}
		});
	}

	public void setHome(File h) {
		this.removeAll();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(h);
		//		DefaultMutableTreeNode home = new DefaultMutableTreeNode(h);
		//		root.add(home);
		model = new DefaultTreeModel(root);
		setModel(model);
		expandPath(root);

	}

	private void expandPath(final DefaultMutableTreeNode d) {
		d.removeAllChildren();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		File[] tempf = ((File) d.getUserObject()).listFiles();
		Vector<File> elem = new Vector<File>();
		Vector<File> dirs = new Vector<File>();
		Vector<File> files = new Vector<File>();
		for (int i = 0; i < tempf.length; i++) {
			if (tempf[i].isDirectory())
				dirs.add(tempf[i]);
			else
				files.add(tempf[i]);
		}

		File[] sortedDirs = dirs.toArray(new File[0]);

		Arrays.sort(sortedDirs);

		File[] sortedFiles = files.toArray(new File[0]);
		Arrays.sort(sortedFiles);

		elem.addAll(Arrays.asList(sortedDirs));
		elem.addAll(Arrays.asList(sortedFiles));

		DefaultMutableTreeNode tempd = null;
		for (int i = 0; i < elem.size(); i++) {
			if (getFileFilter() == null || getFileFilter().accept(elem.get(i))) {
				tempd = new DefaultMutableTreeNode(elem.get(i));
				if (elem.get(i).isDirectory())
					tempd.add(new DefaultMutableTreeNode(null));
				d.add(tempd);
			}
		}
		((DefaultTreeModel) getModel()).reload(d);
		//         }
		//      });
		setCursor(Cursor.getDefaultCursor());
	}

	public void setFileFilter(FileFilter f) {
		filter = f;
		expandPath((DefaultMutableTreeNode) model.getRoot());
	}

	public FileFilter getFileFilter() {
		return filter;
	}

	public void setFileIcon(String ext, Icon icon) {
		icons.put(ext, icon);
	}

	public void removeFileIcon(String ext) {
		icons.remove(ext);
	}

	public void updateUI() {
		super.updateUI();
		setCellRenderer(new FileTreeRenderer());
	}

	/**
	 * @return
	 */
	public File getRootDir() {
		return homeDir;
	}
}

// got that from http://www.java-forum.org/de/viewtopic.php?p=114713
