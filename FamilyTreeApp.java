import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Family Tree Visualization System
 * Java Swing + General Tree
 */
public class FamilyTreeApp extends JFrame {
    private final FamilyTree tree = new FamilyTree();
    private final FamilyTreePanel treePanel;
    private final JTextField nameField = new JTextField(14);
    private final JComboBox<FamilyMember> parentBox = new JComboBox<>();
    private final JTextField searchField = new JTextField(12);
    private final JLabel statusLabel = new JLabel("Ready");

    public FamilyTreeApp() {
        super("Family Tree Visualization System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Sample family members across three generations
        FamilyMember grandpa = tree.addMember("Robert", null);
        FamilyMember grandma = tree.addMember("Margaret", null);
        FamilyMember father = tree.addMember("James", grandpa);
        tree.addMember("Linda", father);
        FamilyMember mother = tree.addMember("Susan", null);
        FamilyMember child1 = tree.addMember("Michael", father);
        tree.addMember("Emily", child1);
        tree.addMember("Sophia", child1);
        tree.addMember("Daniel", father);
        tree.addMember("Olivia", father);

        treePanel = new FamilyTreePanel(tree);
        buildUi();
        refreshParentBox();
    }

    private void buildUi() {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBorder(BorderFactory.createTitledBorder("Family Member Management"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        controls.add(new JLabel("Member Name:"), c);
        c.gridx = 1;
        controls.add(nameField, c);
        c.gridx = 2;
        controls.add(new JLabel("Parent:"), c);
        c.gridx = 3;
        parentBox.setPreferredSize(new Dimension(150, 27));
        parentBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "None (Root)" : value.toString());
                return this;
            }
        });
        controls.add(parentBox, c);
        c.gridx = 4;
        JButton addButton = new JButton("Add Member");
        controls.add(addButton, c);

        c.gridx = 0; c.gridy = 1;
        controls.add(new JLabel("Search Name:"), c);
        c.gridx = 1;
        controls.add(searchField, c);
        c.gridx = 2;
        JButton searchButton = new JButton("Search");
        controls.add(searchButton, c);
        c.gridx = 3;
        JButton resetButton = new JButton("Reset Example");
        controls.add(resetButton, c);
        c.gridx = 4;
        JButton allButton = new JButton("Show All");
        controls.add(allButton, c);

        addButton.addActionListener(e -> addMember());
        searchButton.addActionListener(e -> searchMember());
        resetButton.addActionListener(e -> resetExample());
        allButton.addActionListener(e -> {
            treePanel.clearHighlight();
            statusLabel.setText("แสดงสมาชิกทั้งหมด");
            treePanel.repaint();
        });
        nameField.addActionListener(e -> addMember());
        searchField.addActionListener(e -> searchMember());

        treePanel.setPreferredSize(new Dimension(950, 480));
        JScrollPane scroll = new JScrollPane(treePanel);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        statusLabel.setForeground(new Color(40, 90, 60));

        add(controls, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void addMember() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a member name.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FamilyMember parent = (FamilyMember) parentBox.getSelectedItem();
        if (tree.findByName(name) != null) {
            JOptionPane.showMessageDialog(this, "This name already exists. Please use another name.", "Duplicate Name", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tree.addMember(name, parent);
        nameField.setText("");
        refreshParentBox();
        treePanel.clearHighlight();
        treePanel.revalidate();
        treePanel.repaint();
        statusLabel.setText("Added member: \"" + name + "\"");
    }

    private void searchMember() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search for.");
            return;
        }
        FamilyMember member = tree.findByName(name);
        if (member == null) {
            treePanel.clearHighlight();
            statusLabel.setText("Member not found: \"" + name + "\"");
            JOptionPane.showMessageDialog(this, "Member not found: " + name);
        } else {
            treePanel.highlight(member);
            statusLabel.setText("Found member: " + member.getName());
            treePanel.repaint();
        }
    }

    private void refreshParentBox() {
        FamilyMember selected = (FamilyMember) parentBox.getSelectedItem();
        parentBox.removeAllItems();
        parentBox.addItem(null); // null = root-level member
        for (FamilyMember member : tree.getMembers()) {
            parentBox.addItem(member);
        }
        if (selected != null) parentBox.setSelectedItem(selected);
    }

    private void resetExample() {
        int answer = JOptionPane.showConfirmDialog(this,
                "Clear added members and restore the sample family?",
                "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        tree.clear();
        FamilyMember grandpa = tree.addMember("Robert", null);
        tree.addMember("Margaret", null);
        FamilyMember father = tree.addMember("James", grandpa);
        tree.addMember("Linda", father);
        FamilyMember mother = tree.addMember("Susan", null);
        FamilyMember child1 = tree.addMember("Michael", father);
        tree.addMember("Emily", child1);
        tree.addMember("Sophia", child1);
        tree.addMember("Daniel", father);
        tree.addMember("Olivia", father);
        refreshParentBox();
        treePanel.clearHighlight();
        treePanel.revalidate();
        treePanel.repaint();
        statusLabel.setText("Sample family restored.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FamilyTreeApp().setVisible(true));
    }
}

class FamilyMember {
    private final String name;
    private FamilyMember parent;
    private final List<FamilyMember> children = new ArrayList<>();
    private int depth;

    public FamilyMember(String name) { this.name = name; }
    public String getName() { return name; }
    public FamilyMember getParent() { return parent; }
    public List<FamilyMember> getChildren() { return children; }
    public int getDepth() { return depth; }
    public void setParent(FamilyMember parent) { this.parent = parent; }
    public void setDepth(int depth) { this.depth = depth; }
    @Override public String toString() { return name; }
}

class FamilyTree {
    private final List<FamilyMember> members = new ArrayList<>();
    private final List<FamilyMember> roots = new ArrayList<>();

    public FamilyMember addMember(String name, FamilyMember parent) {
        FamilyMember member = new FamilyMember(name);
        member.setParent(parent);
        member.setDepth(parent == null ? 0 : parent.getDepth() + 1);
        members.add(member);
        if (parent == null) roots.add(member);
        else parent.getChildren().add(member);
        return member;
    }

    public FamilyMember findByName(String name) {
        for (FamilyMember member : members) {
            if (member.getName().equalsIgnoreCase(name)) return member;
        }
        return null;
    }

    public List<FamilyMember> getMembers() { return members; }
    public List<FamilyMember> getRoots() { return roots; }

    public void clear() {
        members.clear();
        roots.clear();
    }
}

class FamilyTreePanel extends JPanel {
    private final FamilyTree tree;
    private FamilyMember highlighted;

    private static final int NODE_W = 112;
    private static final int NODE_H = 42;
    private static final int GAP_X = 22;
    private static final int GAP_Y = 82;
    private static final int MARGIN = 35;

    public FamilyTreePanel(FamilyTree tree) {
        this.tree = tree;
        setBackground(new Color(250, 252, 255));
    }

    public void highlight(FamilyMember member) { highlighted = member; }
    public void clearHighlight() { highlighted = null; }

    @Override
    public Dimension getPreferredSize() {
        int leaves = Math.max(1, countLeaves(tree.getRoots()));
        int width = Math.max(950, leaves * (NODE_W + GAP_X) + MARGIN * 2);
        int maxDepth = 0;
        for (FamilyMember member : tree.getMembers()) maxDepth = Math.max(maxDepth, member.getDepth());
        int height = Math.max(480, (maxDepth + 1) * GAP_Y + 100);
        return new Dimension(width, height);
    }

    private int countLeaves(List<FamilyMember> nodes) {
        int count = 0;
        for (FamilyMember node : nodes) {
            if (node.getChildren().isEmpty()) count++;
            else count += countLeaves(node.getChildren());
        }
        return count;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tree.getMembers().isEmpty()) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));

        int[] nextX = {MARGIN};
        for (FamilyMember root : tree.getRoots()) {
            layoutNode(root, nextX);
            nextX[0] += GAP_X * 2;
        }

        // วาดเส้นก่อน เพื่อให้เส้นอยู่ด้านหลังโหนด
        for (FamilyMember member : tree.getMembers()) {
            Point p = positions.get(member);
            if (p == null) continue;
            for (FamilyMember child : member.getChildren()) {
                Point q = positions.get(child);
                if (q != null) {
                    g2.setColor(new Color(145, 160, 180));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(p.x + NODE_W / 2, p.y + NODE_H, q.x + NODE_W / 2, q.y);
                }
            }
        }

        for (FamilyMember member : tree.getMembers()) {
            Point p = positions.get(member);
            if (p == null) continue;
            boolean isRoot = member.getParent() == null;
            Color fill = highlighted == member ? new Color(255, 225, 135)
                    : isRoot ? new Color(215, 232, 255) : new Color(220, 245, 230);
            g2.setColor(fill);
            g2.fillRoundRect(p.x, p.y, NODE_W, NODE_H, 18, 18);
            g2.setColor(new Color(75, 95, 120));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(p.x, p.y, NODE_W, NODE_H, 18, 18);
            FontMetrics fm = g2.getFontMetrics();
            String label = member.getName();
            if (fm.stringWidth(label) > NODE_W - 12) {
                while (label.length() > 1 && fm.stringWidth(label + "…") > NODE_W - 12)
                    label = label.substring(0, label.length() - 1);
                label += "…";
            }
            g2.setColor(new Color(35, 45, 60));
            g2.drawString(label, p.x + (NODE_W - fm.stringWidth(label)) / 2, p.y + 26);
        }
        g2.dispose();
    }

    private final java.util.Map<FamilyMember, Point> positions = new java.util.IdentityHashMap<>();

    private void layoutNode(FamilyMember node, int[] nextX) {
        int y = MARGIN + node.getDepth() * GAP_Y;
        if (node.getChildren().isEmpty()) {
            positions.put(node, new Point(nextX[0], y));
            nextX[0] += NODE_W + GAP_X;
        } else {
            int start = nextX[0];
            for (FamilyMember child : node.getChildren()) layoutNode(child, nextX);
            int end = nextX[0] - GAP_X - NODE_W;
            int x = (start + end) / 2;
            positions.put(node, new Point(x, y));
        }
    }
}
