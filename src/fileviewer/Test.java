package fileviewer;

import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

abstract class TreePanel extends JPanel {
	abstract TreeModel createTreeModel();

	public TreePanel() {
		final JTree tree = new JTree(createTreeModel());
		JScrollPane scrollPane = new JScrollPane(tree);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		tree.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent e) {
				// don't care about collapse events
			}
			public void treeExpanded(TreeExpansionEvent e) {
				UpdateStatus updateThread;
				TreePath path = e.getPath();
				ExplorableTreeNode node = (ExplorableTreeNode)
								   path.getLastPathComponent();

				if( ! node.isExplored()) {
					DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
					GJApp.updateStatus("exploring ...");

					UpdateStatus us = new UpdateStatus();
					us.start();

					node.explore();
					model.nodeStructureChanged(node);
				}
			}
			class UpdateStatus extends Thread {
				public void run() {
					try { Thread.currentThread().sleep(450); }
					catch(InterruptedException e) { }

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							GJApp.updateStatus(" ");
						}
					});
				}
			}
		});
	}
}
public class Test extends JFrame {
	public Test() {
		TreePanel centerPanel = new TreePanel() {
			public TreeModel createTreeModel() {
				FileNode rootNode = new FileNode(
												new File("/Users/david"));
				rootNode.explore();
			 	return new DefaultTreeModel(rootNode);
			}
		};
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(GJApp.getStatusArea(), 
										 BorderLayout.SOUTH);

	}
	public static void main(String args[]) {
		GJApp.launch(new Test(),"JTree File Explorer",
								 300,300,450,400);
	}
}
class GJApp extends WindowAdapter {
	static private JPanel statusArea = new JPanel();
	static private JLabel status = new JLabel(" ");

	public static void launch(final JFrame f, String title,
							  final int x, final int y, 
							  final int w, int h) {
		f.setTitle(title);
		f.setBounds(x,y,w,h);
		f.setVisible(true);

		statusArea.setBorder(BorderFactory.createEtchedBorder());
		statusArea.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		statusArea.add(status);
		status.setHorizontalAlignment(JLabel.LEFT);

		f.setDefaultCloseOperation(
							WindowConstants.DISPOSE_ON_CLOSE);

		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	static public JPanel getStatusArea() {
		return statusArea;
	}
	static public void updateStatus(String s) {
		status.setText(s);
	}
}
