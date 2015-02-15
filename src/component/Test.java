package component;

import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

abstract class TreePanel extends JPanel {
	abstract TreeModel createTreeModel();

	public void createTree() {
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
	private JSplitPane sp = new JSplitPane();
	private JPanel leftPanel = new JPanel();
	private JPanel rightPanel = new JPanel();

	public Test() {
		JButton updateButton = new JButton("show component tree");

		final JPanel rightCenter = new JPanel(), rightSouth = new JPanel();


		rightCenter.setLayout(new BorderLayout());
		rightCenter.add(new JButton("NORTH"), BorderLayout.NORTH);
		rightCenter.add(new JButton("SOUTH"), BorderLayout.SOUTH);
		rightCenter.add(new JButton("EAST"),  BorderLayout.EAST);
		rightCenter.add(new JButton("WEST"),  BorderLayout.WEST);
		rightCenter.add(new JButton("CENTER"),  BorderLayout.CENTER);

		JPanel left = new JPanel(), right = new JPanel(), bottom = new JPanel();
		left.setLayout(new GridLayout(2,1));
		right.setLayout(new GridLayout(2,1));

		left.add(new JLabel("Name:"));
		left.add(new JLabel("Password:"));

		right.add(new JTextField(20));
		right.add(new JTextField(10));

		bottom.add(new JButton("Log In"));

		JPanel form = new JPanel();
		form.add(left);
		form.add(right);
		form.add(bottom);

		rightCenter.setLayout(new BorderLayout());
		rightCenter.add(form);

		rightSouth.setLayout(new BorderLayout());
		rightSouth.add(new JSeparator(JSeparator.HORIZONTAL),
							BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(updateButton);
		rightSouth.add(buttonPanel, BorderLayout.CENTER);

		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(rightCenter, BorderLayout.CENTER);
		rightPanel.add(rightSouth, BorderLayout.SOUTH);


		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(rightCenter, BorderLayout.CENTER);
		rightPanel.add(rightSouth, BorderLayout.SOUTH);

		sp.setLeftComponent(leftPanel);
		sp.setRightComponent(rightPanel);
		sp.setDividerLocation(250);
		getContentPane().add(sp);

		updateButton.addActionListener(new ActionListener() {
			private boolean treeCreated = false;

			public void actionPerformed(ActionEvent event) {
				if(!treeCreated) {
					TreePanel treePanel = new TreePanel() {
						public TreeModel createTreeModel() {
							SwingComponentNode rootNode = new SwingComponentNode(rightCenter);
							rootNode.explore();
			 				return new DefaultTreeModel(rootNode);
						}
					};
					treePanel.createTree();
					leftPanel.setLayout(new BorderLayout());
					leftPanel.add(treePanel, BorderLayout.CENTER);
					getContentPane().validate();
					treeCreated = true;
				}
			}
		});
	}
	public static void main(String args[]) {
		GJApp.launch(new Test(),"UI Builder Prototype", 300,300,600,175);
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
class ThreeDPanel extends Panel {
	public void paint(Graphics g) {
		Dimension sz = getSize();
		g.setColor(Color.lightGray);
		g.draw3DRect(0, 0, sz.width-1, sz.height-1, true);
	}
}
