import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.Hashtable;
import java.util.Map;

class MainTest {

    @BeforeEach
    void setUp() throws InterruptedException {
        // Initialize components and set up initial data
        Main.main(new String[]{});
        Main.itemDao.deleteAll();
        sleep(2000);
    }

    @Test
    void testAddNewItemToInventory() {
        Main.nameField.setText("Excalibur");
        Main.powerField.setText("100");
        Main.weightField.setText("5.5");
        Main.itemTypeCombo.setSelectedItem("Weapon");

        Main.addItem();

        boolean itemExists = false;
        for (int i = 0; i < Main.tableModel.getRowCount(); i++) {
            if (Main.tableModel.getValueAt(i, 0).equals("Excalibur")) {
                itemExists = true;
                break;
            }
        }
        assertTrue(itemExists, "The item should be added to the inventory table.");
    }

    @Test
    void testRemoveSelectedItemFromInventory() {
        Main.nameField.setText("Shield");
        Main.powerField.setText("50");
        Main.weightField.setText("10.0");
        Main.itemTypeCombo.setSelectedItem("Armor");
        Main.addItem();

        int initialRowCount = Main.tableModel.getRowCount();
        Main.inventoryTable.setRowSelectionInterval(0, 0);
        Main.removeSelectedItem();

        assertEquals(initialRowCount - 1, Main.tableModel.getRowCount(), "The item should be removed from the inventory table.");
    }

    @Test
    void testEquipRandomItem() {
        Main.nameField.setText("Potion of Strength");
        Main.powerField.setText("30");
        Main.weightField.setText("2.0");
        Main.itemTypeCombo.setSelectedItem("Potion");
        Main.addItem();

        // Add a second item to allow randomness
        Main.nameField.setText("Sword of Destiny");
        Main.powerField.setText("50");
        Main.weightField.setText("5.0");
        Main.itemTypeCombo.setSelectedItem("Weapon");
        Main.addItem();

        Hashtable<String, Integer> equipCounts = new Hashtable<>();
        for (int i = 0; i < 50; i++) {
            Main.equipRandomItem();
            String lastEquipped = Main.lastMessage;
            equipCounts.put(lastEquipped, equipCounts.getOrDefault(lastEquipped, 0) + 1);
        }

        assertTrue(equipCounts.size() > 1, "The equipped item should vary between tests, confirming randomness.");
    }


    @Test
    void testCreatePowerfulCharacter() {
        Main.itemDao.save(new Hashtable<>(Map.of("itemName", "Sword", "type", "Weapon", "power", "100", "weight", "5.0")));
        Main.itemDao.save(new Hashtable<>(Map.of("itemName", "Shield", "type", "Armor", "power", "50", "weight", "10.0")));
        Main.itemDao.save(new Hashtable<>(Map.of("itemName", "Potion", "type", "Potion", "power", "25", "weight", "1.0")));

        Main.createPowerfulCharacter();

        String message = Main.lastMessage;
        assertTrue(message.contains("Sword") && message.contains("Shield") && message.contains("Potion"),
                "A character should be created with the most powerful items from each category.");
    }

    @Test
    void testValidationForNonNumericPowerAndWeight() {
        Main.nameField.setText("Mace");
        Main.powerField.setText("xyz");
        Main.weightField.setText("2.0");
        Main.itemTypeCombo.setSelectedItem("Weapon");

        Main.addItem();
        assertEquals(0, Main.tableModel.getRowCount(), "Non-numeric power should prevent item addition.");

        Main.nameField.setText("Alice");
        Main.powerField.setText("50");
        Main.weightField.setText("xyz");
        Main.itemTypeCombo.setSelectedItem("Weapon");
        Main.addItem();
        assertEquals(1, Main.tableModel.getRowCount(), "Non-numeric weight should prevent item addition.");
    }

    @Test
    void testClearAllFunctionality() {
        Main.nameField.setText("Axe");
        Main.powerField.setText("75");
        Main.weightField.setText("6.5");
        Main.itemTypeCombo.setSelectedItem("Weapon");

        Main.clearAll();

        assertTrue(Main.nameField.getText().isEmpty(), "Name field should be cleared.");
        assertTrue(Main.powerField.getText().isEmpty(), "Power field should be cleared.");
        assertTrue(Main.weightField.getText().isEmpty(), "Weight field should be cleared.");
        assertEquals(0, Main.itemTypeCombo.getSelectedIndex(), "Combo box should reset to first option.");
    }

    @Test
    void testEquipRandomItemNoRepeat() {
        Main.nameField.setText("Bow");
        Main.powerField.setText("20");
        Main.weightField.setText("3.0");
        Main.itemTypeCombo.setSelectedItem("Weapon");
        Main.addItem();

        Main.nameField.setText("Magic Wand");
        Main.powerField.setText("40");
        Main.weightField.setText("1.5");
        Main.itemTypeCombo.setSelectedItem("Weapon");
        Main.addItem();

        String lastEquipped = null;
        boolean foundRepeat = false;
        for (int i = 0; i < 20; i++) {
            Main.equipRandomItem();
            String equippedItem = Main.lastMessage;
            if (equippedItem.equals(lastEquipped)) {
                foundRepeat = true;
                break;
            }
            lastEquipped = equippedItem;
        }
        assertFalse(foundRepeat, "Equipped items should not repeat consecutively.");
    }

    @Test
    void testDynamicInventoryUpdate() {
        Main.nameField.setText("Helmet");
        Main.powerField.setText("15");
        Main.weightField.setText("2.5");
        Main.itemTypeCombo.setSelectedItem("Armor");
        Main.addItem();

        assertEquals(1, Main.tableModel.getRowCount(), "Item should be added to the table view.");

        Main.inventoryTable.setRowSelectionInterval(0, 0);
        Main.removeSelectedItem();

        assertEquals(0, Main.tableModel.getRowCount(), "Item should be removed from the table view.");

        Main.clearAll();
        assertEquals(0, Main.tableModel.getRowCount(), "Table view should be cleared after 'Clear All'.");
    }

    @Test
    void testSearchInventory() {
        Main.nameField.setText("Spear");
        Main.powerField.setText("25");
        Main.weightField.setText("3.5");
        Main.itemTypeCombo.setSelectedItem("Weapon");
        Main.addItem();

        Main.searchField.setText("Spear");
        Main.search();
        assertEquals(1, Main.inventoryTable.getRowCount(), "Search should filter inventory to match 'Spear'.");

        Main.searchField.setText("");
        Main.search();
        assertEquals(Main.tableModel.getRowCount(), Main.inventoryTable.getRowCount(), "Empty search should reset the view to show all items.");
    }
}