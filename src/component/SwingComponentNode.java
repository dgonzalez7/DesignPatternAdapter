package component;

import java.awt.Component;
import java.awt.Container;
import javax.swing.*;
import javax.swing.tree.*;

class SwingComponentNode extends DefaultMutableTreeNode 
				     implements ExplorableTreeNode {
	
	private boolean explored = false;

	public SwingComponentNode(Component swingComponent) { 
		setUserObject(swingComponent); 
	}
	
	public boolean getAllowsChildren() { return isContainer(); }
	
	public boolean isLeaf() 	        { return !isContainer(); }
	
	public JComponent getComponent()	  { return (JComponent)getUserObject(); }

	public boolean isContainer() {
		return getComponent().getComponentCount() > 0;
	}
	
	public String toString() {
		return getComponent().toString();
	}
	
	public boolean isExplored() { 
		 return explored; 
	}
	
	public void explore() {
		if(!isContainer())
			return;

		if(!isExplored()) {
			Component[] children = getComponent().getComponents();

			for(int i=0; i < children.length; ++i) 
				add(new SwingComponentNode((JComponent)children[i]));

			explored = true;
		}
	}
}
