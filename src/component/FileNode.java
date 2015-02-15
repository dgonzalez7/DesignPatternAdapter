package component;

import java.io.File;
import javax.swing.tree.*;

class FileNode extends DefaultMutableTreeNode 
				   implements ExplorableTreeNode {
	private boolean explored = false;

	public FileNode(File file) { 
		setUserObject(file); 
	}
	public boolean getAllowsChildren() { return isDirectory(); }
	public boolean isLeaf() 	 { return !isDirectory(); }
	public File getFile()		 { return (File)getUserObject(); }
	public boolean isExplored() { return explored; }

	public boolean isDirectory() {
		File file = getFile();
		return file.isDirectory();
	}
	public String toString() {
		File file = (File)getUserObject();
		String filename = file.toString();
		int index = filename.lastIndexOf(File.separator);

		return (index != -1 && index != filename.length()-1) ? 
									filename.substring(index+1) : 
									filename;
	}
	public void explore() {
		if(!isDirectory())
			return;

		if(!isExplored()) {
			File file = getFile();
			File[] children = file.listFiles();

			for(int i=0; i < children.length; ++i) 
				add(new FileNode(children[i]));

			explored = true;
		}
	}
}
