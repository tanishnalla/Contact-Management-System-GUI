import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// ⚠️ Custom Exception
class ContactNotFoundException extends Exception {
    ContactNotFoundException(String msg) {
        super(msg);
    }
}

// 📱 Contact class implementing Comparable
class Contact implements Comparable<Contact> {
    String name, mono, type;

    Contact(String name, String mono, String type) {
        this.name = name;
        this.mono = mono;
        this.type = type;
    }

    public int compareTo(Contact other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nMobile: " + mono + "\nType: " + type + "\n--------------------\n";
    }
}

// 📒 Contact Management class
class ContactManagement {
    ArrayList<Contact> list = new ArrayList<>();

    void addContact(Contact c) {
        list.add(c);
    }

    Contact searchContact(String mobileno) throws ContactNotFoundException {
        for (Contact c : list) {
            if (c.mono.equals(mobileno))
                return c;
        }
        throw new ContactNotFoundException("❌ Contact Not Found");
    }

    ArrayList<Contact> displayContacts() {
        ArrayList<Contact> sorted = new ArrayList<>(list);
        Collections.sort(sorted); // uses Comparable
        return sorted;
    }
}

// 💻 GUI Class
class ContactGUI extends JFrame implements ActionListener, Runnable {
    JLabel lTitle, lName, lMobile, lType;
    JTextField tName, tMobile;
    JRadioButton rbFamily, rbOfficial;
    JButton bAdd, bSearch, bDisplay;
    JTextArea ta;
    Thread th;
    int x;
    ContactManagement manager;

    ContactGUI() {
        setSize(500, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Contact Management System");

        manager = new ContactManagement();

        // Header Label (Moving)
        lTitle = new JLabel("WELCOME TO CONTACT MANAGEMENT SYSTEM");
        lTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lTitle.setForeground(Color.BLUE);
        lTitle.setBounds(60, 20, 400, 30);
        add(lTitle);
        x = getWidth();

        // Name Label & TextField
        lName = new JLabel("Name:");
        lName.setBounds(50, 80, 100, 25);
        add(lName);
        tName = new JTextField(15);
        tName.setBounds(150, 80, 150, 25);
        add(tName);

        // Mobile Label & TextField
        lMobile = new JLabel("Mobile:");
        lMobile.setBounds(50, 120, 100, 25);
        add(lMobile);
        tMobile = new JTextField(15);
        tMobile.setBounds(150, 120, 150, 25);
        add(tMobile);

        // Radio Buttons
        lType = new JLabel("Type:");
        lType.setBounds(50, 160, 100, 25);
        add(lType);

        rbFamily = new JRadioButton("Family");
        rbOfficial = new JRadioButton("Official");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbFamily);
        bg.add(rbOfficial);
        rbFamily.setBounds(150, 160, 100, 25);
        rbOfficial.setBounds(250, 160, 100, 25);
        add(rbFamily);
        add(rbOfficial);

        // Buttons
        bAdd = new JButton("Add Contact");
        bAdd.setBounds(50, 210, 130, 30);
        add(bAdd);
        bSearch = new JButton("Search Contact");
        bSearch.setBounds(190, 210, 130, 30);
        add(bSearch);
        bDisplay = new JButton("Display All");
        bDisplay.setBounds(330, 210, 120, 30);
        add(bDisplay);

        // Text Area
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        sp.setBounds(50, 260, 400, 180);
        add(sp);
        ta.setEditable(false);

        // Button listeners
        bAdd.addActionListener(this);
        bSearch.addActionListener(this);
        bDisplay.addActionListener(this);

        // Thread for moving text
        th = new Thread(this);
        th.start();

        setVisible(true);
    }

    // 🎬 Thread for moving title text
    public void run() {
        while (true) {
            try {
                x = x - 10;
                if (x < -lTitle.getWidth())
                    x = getWidth();
                lTitle.setLocation(x, 20);
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    // 🎯 Button Actions
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == bAdd) {
                String name = tName.getText().trim();
                String mobile = tMobile.getText().trim();
                String type = rbFamily.isSelected() ? "Family"
                             : rbOfficial.isSelected() ? "Official" : "";

                if (name.isEmpty() || mobile.isEmpty() || type.isEmpty()) {
                    ta.setText("⚠️ Please fill all fields and select a contact type!");
                    return;
                }

                manager.addContact(new Contact(name, mobile, type));
                ta.setText("✅ Contact Added Successfully!\n");
                clearFields();

            } else if (e.getSource() == bSearch) {
                String mobile = tMobile.getText().trim();
                if (mobile.isEmpty()) {
                    ta.setText("⚠️ Enter mobile number to search!");
                    return;
                }
                Contact found = manager.searchContact(mobile);
                ta.setText("✅ Contact Found:\n" + found);

            } else if (e.getSource() == bDisplay) {
                ArrayList<Contact> all = manager.displayContacts();
                if (all.isEmpty()) {
                    ta.setText("📭 No contacts to display!");
                    return;
                }
                StringBuilder sb = new StringBuilder("📋 All Contacts (Sorted by Name):\n--------------------\n");
                for (Contact c : all) sb.append(c.toString());
                ta.setText(sb.toString());
            }
        } catch (ContactNotFoundException ex) {
            ta.setText(ex.getMessage());
        } catch (Exception ex) {
            ta.setText("Unexpected Error: " + ex.getMessage());
        }
    }

    // 🧹 Helper to clear input fields
    void clearFields() {
        tName.setText("");
        tMobile.setText("");
        rbFamily.setSelected(false);
        rbOfficial.setSelected(false);
    }

    // 🏁 main
    public static void main(String[] args) {
        new ContactGUI();
    }
}

