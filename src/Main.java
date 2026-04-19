import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class Main {
    public static MyTextField nameField, powerField, weightField, searchField;
    public static MyComboBox<String> itemTypeCombo;
    public static DefaultTableModel tableModel;
    public static JTable inventoryTable;
    public static TableRowSorter<DefaultTableModel> sorter;
    public static ItemDbDAO itemDao;

    static String lastMessage;

    public static void showMessage(JComponent parentComponent, String message, String title, int type) {
        lastMessage = message;
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(parentComponent, message, title, type)
        );
    }

    public static void main(String[] args) {
        itemDao = new ItemDbDAO();

        SwingUtilities.invokeLater(() -> {
            MyJFrame frame = new MyJFrame("Game Inventory Management");

            MyPanel mainPanel = new MyPanel(20);
            mainPanel.setLayout(new BorderLayout(20, 0));
            mainPanel.setPreferredSize(new Dimension(450, 685));
            mainPanel.setBackground(Color.black);
            mainPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

            JPanel inputPanel = createInputPanel();
            JPanel buttonPanel = createButtonPanel();

            inventoryTable = createInventoryTable();  // Initialize inventoryTable and tableModel
            loadInventoryFromDatabase(); // Load initial data after table model is ready

            JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
            tableScrollPane.setOpaque(false);
            tableScrollPane.getViewport().setOpaque(false);
            tableScrollPane.setBorder(new EmptyBorder(0, 5, 5, 5));

            mainPanel.add(inputPanel, BorderLayout.NORTH);
            mainPanel.add(buttonPanel, BorderLayout.CENTER);
            mainPanel.add(tableScrollPane, BorderLayout.SOUTH);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }

    public static void loadInventoryFromDatabase() {
        if (tableModel == null) return;  // Ensure tableModel is initialized
        tableModel.setRowCount(0); // Clear existing rows
        ArrayList<Hashtable<String, String>> items = itemDao.load();

        for (Hashtable<String, String> item : items) {
            Object[] row = {
                    item.get("itemName"),
                    item.get("type"),
                    item.get("power"),
                    item.get("weight")
            };
            tableModel.addRow(row);
        }
    }

    public static JPanel createInputPanel() {
        MyPanel panel = new MyPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 8, 4, 3);

        String[] options = {"Weapon", "Potion", "Armor"};
        itemTypeCombo = new MyComboBox<>(options);

        panel.add(new MyLabel("Name"), gbc);
        panel.add(new MyLabel("Power"), gbc);
        panel.add(new MyLabel("Weight"), gbc);
        panel.add(new MyLabel("Type"), gbc);
        panel.add(new MyLabel("Search"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 5.0;

        nameField = new MyTextField();
        powerField = new MyTextField();
        weightField = new MyTextField();
        searchField = new MyTextField();

        ((AbstractDocument) powerField.getDocument()).setDocumentFilter(new IntegerFilter());
        ((AbstractDocument) weightField.getDocument()).setDocumentFilter(new DecimalFilter());

        panel.add(nameField, gbc);
        panel.add(powerField, gbc);
        panel.add(weightField, gbc);
        panel.add(itemTypeCombo, gbc);
        panel.add(searchField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;  // No expansion
        gbc.insets = new Insets(4, 0, 4, 3);

        MyLabel powerInfoLabel = new MyLabel("Enter an integer");
        powerInfoLabel.setForeground(new Color(52, 152, 219));
        powerInfoLabel.setFont(powerInfoLabel.getFont().deriveFont(12f));

        MyLabel weightInfoLabel = new MyLabel("Enter a decimal");
        weightInfoLabel.setForeground(new Color(52, 152, 219));
        weightInfoLabel.setFont(weightInfoLabel.getFont().deriveFont(12f));

        gbc.gridy = 1;
        panel.add(powerInfoLabel, gbc);
        gbc.gridy = 2;
        panel.add(weightInfoLabel, gbc);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                search();
            }

            public void removeUpdate(DocumentEvent e) {
                search();
            }

            public void insertUpdate(DocumentEvent e) {
                search();
            }
        });

        return panel;
    }


    static class IntegerFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string.matches("\\d*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    static class DecimalFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (isValidDecimalInput(fb.getDocument().getText(0, fb.getDocument().getLength()) + string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (isValidDecimalInput(fb.getDocument().getText(0, fb.getDocument().getLength()) + text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        public boolean isValidDecimalInput(String text) {
            return text.matches("\\d*\\.?\\d*");
        }
    }

    public static JPanel createButtonPanel() {
        MyPanel panel = new MyPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(Color.DARK_GRAY);

        MyButton addButton = new MyButton("Add Item");
        addButton.addActionListener(e -> addItem());

        MyButton removeButton = new MyButton("Remove Selected Item");
        removeButton.addActionListener(e -> removeSelectedItem());

        MyButton clearButton = new MyButton("Clear All");
        clearButton.addActionListener(e -> clearAll());

        MyButton equipRandomButton = new MyButton("Equip Random Item");
        equipRandomButton.addActionListener(e -> equipRandomItem());

        MyButton createPowerfulCharButton = new MyButton("Create Powerful Character");
        createPowerfulCharButton.addActionListener(e -> createPowerfulCharacter());

        panel.add(addButton);
        panel.add(removeButton);
        panel.add(clearButton);
        panel.add(equipRandomButton);
        panel.add(createPowerfulCharButton);

        return panel;
    }

    public static JTable createInventoryTable() {
        String[] columnNames = {"Item Name", "Type", "Power", "Weight"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // This makes all cells non-editable
            }
        };
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.darkGray);
        table.setForeground(new Color(0, 250, 250));
        table.setFont(new Font("Monospaced", Font.BOLD, 14));

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    loadRowToForm(row);
                }
            }
        });

        return table;
    }

    public static void loadRowToForm(int row) {
        if (row != -1) {
            nameField.setText((String) tableModel.getValueAt(row, 0));
            itemTypeCombo.setSelectedItem(tableModel.getValueAt(row, 1));
            powerField.setText((String) tableModel.getValueAt(row, 2));
            weightField.setText((String) tableModel.getValueAt(row, 3));
        }
    }

    public static void addItem() {
        String name = nameField.getText();
        String type = (String) itemTypeCombo.getSelectedItem();
        String power = powerField.getText();
        String weight = weightField.getText();

        if (!name.isEmpty() && !power.isEmpty() && !weight.isEmpty()) {
            // Validate power and weight are numeric
            if (!power.matches("\\d+") || !weight.matches("\\d*\\.?\\d+")) {
                showMessage(null,
                        "Power must be an integer and weight must be a decimal number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return; // Stop if validation fails
            }

            Hashtable<String, String> itemData = new Hashtable<>();
            itemData.put("itemName", name);
            itemData.put("type", type);
            itemData.put("power", power);
            itemData.put("weight", weight);

            if (itemDao.save(itemData)) {
                tableModel.addRow(new Object[]{name, type, power, weight});
                clearInputFields();
            } else {
                showMessage(null,
                        "Failed to save item to database",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            showMessage(null,
                    "Please fill all fields",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void removeSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = inventoryTable.convertRowIndexToModel(selectedRow);
            String itemName = (String) tableModel.getValueAt(modelRow, 0);

            if (itemDao.delete(itemName)) {
                tableModel.removeRow(modelRow);
            } else {
                showMessage(null,
                        "Failed to delete item from database",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            showMessage(null,
                    "Please select an item to remove",
                    "Selection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void clearAll() {
        clearInputFields();
        loadInventoryFromDatabase(); // Reload from database
    }

    // Add a new static variable to store the last equipped item index
    private static int lastEquippedIndex = -1;

    public static void equipRandomItem() {
        ArrayList<Hashtable<String, String>> items = itemDao.load();
        if (!items.isEmpty()) {
            Random random = new Random();
            int randomIndex;

            // Ensure the new randomIndex is different from lastEquippedIndex
            do {
                randomIndex = random.nextInt(items.size());
            } while (randomIndex == lastEquippedIndex && items.size() > 1);

            lastEquippedIndex = randomIndex; // Update the last equipped index
            String itemName = items.get(randomIndex).get("itemName");

            showMessage(
                    null,
                    "Equipped: " + itemName,
                    "Random Item Equipped",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            showMessage(
                    null,
                    "No items in inventory",
                    "Equip Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public static void createPowerfulCharacter() {
        Hashtable<String, String> bestWeapon = itemDao.getMostPowerfulItemByType("Weapon");
        Hashtable<String, String> bestArmor = itemDao.getMostPowerfulItemByType("Armor");
        Hashtable<String, String> bestPotion = itemDao.getMostPowerfulItemByType("Potion");

        if (bestWeapon == null || bestArmor == null || bestPotion == null) {
            showMessage(
                    null,
                    "Unable to create a fully equipped character.",
                    "Equipment Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String equipMessage = String.format(
                "Your character is equipped with %s, %s, and %s.",
                bestWeapon.get("itemName"),
                bestArmor.get("itemName"),
                bestPotion.get("itemName")
        );

        showMessage(
                null,
                equipMessage,
                "Character Equipped",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void search() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    public static void clearInputFields() {
        nameField.setText("");
        powerField.setText("");
        weightField.setText("");
        itemTypeCombo.setSelectedIndex(0);
    }
}